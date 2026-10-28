package com.ruoyi.system.kb.rag;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YgKbDocument;
import com.ruoyi.system.domain.YgKnowledgeBase;
import com.ruoyi.system.kb.config.KbProperties;
import com.ruoyi.system.kb.parser.KbTextExtractor;
import com.ruoyi.system.kb.store.KbEmbeddingStoreManager;
import com.ruoyi.system.mapper.YgKbDocumentMapper;
import com.ruoyi.system.mapper.YgKnowledgeBaseMapper;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.document.splitter.DocumentByRegexSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

/**
 * 知识库文档入库服务（解析 → 分块 → 向量化 → 存储）
 *
 * @author ruoyi
 * @date 2026-10-14
 */
@Service
public class KbIngestService
{
    private static final Logger log = LoggerFactory.getLogger(KbIngestService.class);

    /** 每批向量化的分块数（防 Ollama 单请求过大超时） */
    private static final int EMBED_BATCH = 16;

    /** 按句子切分的正则：中英文句末标点与换行之后切分 */
    private static final String SENTENCE_REGEX = "(?<=[。！？；.!?;\\n])\\s*";

    /** 按 Markdown 标题切分的正则：标题行之前切分 */
    private static final String MARKDOWN_HEADING_REGEX = "(?m)\\n+(?=#{1,6}\\s)";

    @Autowired
    private KbProperties props;

    @Autowired
    private KbTextExtractor extractor;

    @Autowired
    private com.ruoyi.system.kb.config.KbModelManager modelManager;

    @Autowired
    private KbEmbeddingStoreManager storeManager;

    @Autowired
    private YgKbDocumentMapper documentMapper;

    @Autowired
    private YgKnowledgeBaseMapper kbMapper;

    /**
     * 单文档入库（同步）。先解析+向量化全部成功，再写入 store，失败不污染索引。
     *
     * @param doc 文档记录
     * @param persist 是否立即持久化向量文件（批量重建时可关闭，最后统一 persist）
     */
    public int ingest(YgKbDocument doc, boolean persist)
    {
        documentMapper.updateIndexStatus(doc.getDocId(), "1", null);
        try
        {
            YgKnowledgeBase kb = kbMapper.selectYgKnowledgeBaseByKbId(doc.getKbId());
            if (kb == null)
            {
                throw new ServiceException("知识库不存在");
            }
            checkDimension(kb);

            File file = resolveFile(doc.getFilePath());
            String text = extractor.extract(file, doc.getFileType());
            if (StringUtils.isBlank(text))
            {
                throw new ServiceException("文档解析结果为空");
            }
            List<TextSegment> segments = splitToSegments(text, doc);
            if (segments.isEmpty())
            {
                throw new ServiceException("文档分块结果为空");
            }
            List<Embedding> embeddings = embedInBatches(segments);

            InMemoryEmbeddingStore<TextSegment> store = storeManager.getStore(doc.getKbId());
            store.addAll(embeddings, segments);
            if (persist)
            {
                storeManager.persist(doc.getKbId());
            }

            YgKbDocument update = new YgKbDocument();
            update.setDocId(doc.getDocId());
            update.setChunkCount(segments.size());
            update.setIndexStatus("2");
            update.setErrorMsg("");
            documentMapper.updateYgKbDocument(update);
            kbMapper.updateStats(doc.getKbId());
            return segments.size();
        }
        catch (Exception e)
        {
            log.error("知识库文档[{}]入库失败", doc.getDocId(), e);
            String msg = StringUtils.substring(StringUtils.defaultString(e.getMessage(), e.getClass().getSimpleName()), 0, 900);
            documentMapper.updateIndexStatus(doc.getDocId(), "3", friendlyMessage(e, msg));
            throw new ServiceException(friendlyMessage(e, msg));
        }
    }

    /**
     * 重建整库索引：删除旧向量文件，逐文档重新入库
     */
    public void rebuild(Long kbId)
    {
        YgKnowledgeBase kb = kbMapper.selectYgKnowledgeBaseByKbId(kbId);
        if (kb == null)
        {
            throw new ServiceException("知识库不存在");
        }
        List<YgKbDocument> docs = documentMapper.selectByKbIdAll(kbId);
        storeManager.delete(kbId);
        for (YgKbDocument doc : docs)
        {
            try
            {
                ingest(doc, false);
            }
            catch (Exception e)
            {
                log.error("重建索引时文档[{}]失败：{}", doc.getDocId(), e.getMessage());
            }
        }
        storeManager.persist(kbId);
        kbMapper.updateStats(kbId);
    }

    /**
     * 删除文档后清理（向量无法按条件删除，需提示重建索引）
     */
    public void persistStore(Long kbId)
    {
        storeManager.persist(kbId);
    }

    private List<TextSegment> splitToSegments(String text, YgKbDocument doc)
    {
        Metadata metadata = new Metadata();
        metadata.put("document_id", String.valueOf(doc.getDocId()));
        metadata.put("file_name", StringUtils.defaultString(doc.getFileName(), ""));
        Document document = Document.from(text, metadata);
        return buildSplitter().split(document);
    }

    /**
     * 按配置的分块策略构建分块器；超长片段统一回退到固定长度递归切分
     */
    private DocumentSplitter buildSplitter()
    {
        int maxSize = props.getChunk().getMaxSize();
        int overlap = props.getChunk().getOverlap();
        DocumentSplitter fallback = DocumentSplitters.recursive(maxSize, overlap);
        String strategy = StringUtils.defaultIfEmpty(props.getChunk().getStrategy(), "recursive");
        switch (strategy)
        {
            case "paragraph":
                return new DocumentByParagraphSplitter(maxSize, overlap, fallback);
            case "sentence":
                return new DocumentByRegexSplitter(SENTENCE_REGEX, "en", maxSize, overlap, fallback);
            case "markdown":
                return new DocumentByRegexSplitter(MARKDOWN_HEADING_REGEX, "en", maxSize, overlap, fallback);
            case "separator":
                String separator = StringUtils.defaultString(props.getChunk().getSeparator()).replace("\\n", "\n");
                if (StringUtils.isEmpty(separator))
                {
                    return fallback;
                }
                return new DocumentByRegexSplitter(Pattern.quote(separator), "en", maxSize, overlap, fallback);
            case "recursive":
            default:
                return fallback;
        }
    }

    private List<Embedding> embedInBatches(List<TextSegment> segments)
    {
        List<Embedding> all = new ArrayList<>(segments.size());
        for (int i = 0; i < segments.size(); i += EMBED_BATCH)
        {
            List<TextSegment> sub = segments.subList(i, Math.min(i + EMBED_BATCH, segments.size()));
            all.addAll(modelManager.getEmbeddingModel().embedAll(sub).content());
        }
        return all;
    }

    private void checkDimension(YgKnowledgeBase kb)
    {
        int currentDim = props.currentEmbeddingDimension();
        String currentModel = props.currentEmbeddingModel();
        if (kb.getEmbeddingDim() != null && kb.getEmbeddingDim() > 0 && kb.getEmbeddingDim() != currentDim)
        {
            throw new ServiceException(StringUtils.format(
                    "知识库[{0}]的向量维度({1})与当前embedding模型维度({2})不匹配，请点击重建索引",
                    kb.getKbName(), kb.getEmbeddingDim(), currentDim));
        }
        if (StringUtils.isNotEmpty(kb.getEmbeddingModel()) && StringUtils.isNotEmpty(currentModel)
                && !kb.getEmbeddingModel().equals(currentModel))
        {
            throw new ServiceException(StringUtils.format(
                    "知识库[{0}]的向量化模型({1})与当前embedding模型({2})不一致，请点击重建索引",
                    kb.getKbName(), kb.getEmbeddingModel(), currentModel));
        }
    }

    private File resolveFile(String path)
    {
        if (path != null && path.startsWith(Constants.RESOURCE_PREFIX))
        {
            return new File(RuoYiConfig.getProfile() + StringUtils.substringAfter(path, Constants.RESOURCE_PREFIX));
        }
        return new File(StringUtils.defaultString(path));
    }

    private String friendlyMessage(Exception e, String msg)
    {
        String text = e.getClass().getName() + ": " + msg;
        if (text.contains("ConnectException") || text.contains("UnknownHostException") || text.contains("timed out"))
        {
            return "本地模型服务不可用：请确认 " + props.embeddingProviderName() + " 服务已启动（"
                    + props.embeddingBaseUrl()
                    + "）且已拉取embedding模型 " + props.currentEmbeddingModel();
        }
        return msg;
    }
}
