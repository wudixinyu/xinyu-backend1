package com.ruoyi.system.kb.rag;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YgKbChatLog;
import com.ruoyi.system.domain.YgKnowledgeBase;
import com.ruoyi.system.domain.vo.KbChatResult;
import com.ruoyi.system.domain.vo.KbSourceVo;
import com.ruoyi.system.kb.config.KbModelManager;
import com.ruoyi.system.kb.config.KbProperties;
import com.ruoyi.system.kb.store.KbEmbeddingStoreManager;
import com.ruoyi.system.mapper.YgKbChatLogMapper;
import com.ruoyi.system.mapper.YgKnowledgeBaseMapper;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

/**
 * 知识库 RAG 问答服务（检索 → 组装Prompt → 生成 → 记录日志）
 *
 * @author ruoyi
 * @date 2026-10-14
 */
@Service
public class KbQaService
{
    private static final Logger log = LoggerFactory.getLogger(KbQaService.class);

    private static final String SYSTEM_PROMPT =
            "你是严谨的知识库问答助手。仅依据【资料】回答用户问题，回答中用 [n] 标注所引用的资料编号；"
            + "若资料无法支撑答案，请明确说明不知道，不要编造。";

    @Autowired
    private KbProperties props;

    @Autowired
    private KbModelManager modelManager;

    @Autowired
    private KbEmbeddingStoreManager storeManager;

    @Autowired
    private YgKnowledgeBaseMapper kbMapper;

    @Autowired
    private YgKbChatLogMapper chatLogMapper;

    /**
     * 基于指定知识库问答（同步返回）
     */
    public KbChatResult chat(Long kbId, String question, String username)
    {
        if (kbId == null || StringUtils.isBlank(question))
        {
            throw new ServiceException("知识库ID与问题不能为空");
        }
        YgKnowledgeBase kb = kbMapper.selectYgKnowledgeBaseByKbId(kbId);
        if (kb == null)
        {
            throw new ServiceException("知识库不存在");
        }
        if ("1".equals(kb.getStatus()))
        {
            throw new ServiceException("知识库已停用");
        }
        checkDimension(kb);

        long start = System.currentTimeMillis();
        KbChatResult result = new KbChatResult();
        try
        {
            InMemoryEmbeddingStore<TextSegment> store = storeManager.getStore(kbId);
            Response<Embedding> queryEmb = modelManager.getEmbeddingModel().embed(question);
            List<EmbeddingMatch<TextSegment>> matches = store.search(EmbeddingSearchRequest.builder()
                    .queryEmbedding(queryEmb.content())
                    .maxResults(props.getRetrieval().getMaxResults())
                    .minScore(props.getRetrieval().getMinScore())
                    .build()).matches();

            List<KbSourceVo> sources = toSources(matches);
            result.setSources(sources);
            if (sources.isEmpty())
            {
                result.setAnswer("知识库中未找到与该问题相关的内容，无法回答。请尝试更换问法，或先上传相关文档。");
            }
            else
            {
                List<ChatMessage> messages = new ArrayList<>();
                messages.add(SystemMessage.from(SYSTEM_PROMPT));
                messages.add(UserMessage.from(buildUserPrompt(question, matches)));
                ChatResponse response = modelManager.getChatModel().chat(messages);
                result.setAnswer(response.aiMessage().text());
            }
        }
        catch (ServiceException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            log.error("知识库问答失败", e);
            throw new ServiceException(friendlyMessage(e));
        }
        result.setCostMs(System.currentTimeMillis() - start);
        saveChatLog(kbId, question, result, username);
        return result;
    }

    /**
     * 流式问答回调
     */
    public interface KbStreamListener
    {
        /** 检索到的引用来源（生成开始前推送） */
        void onSources(List<KbSourceVo> sources);

        /** 增量文本 token */
        void onToken(String token);

        /** 生成完成，参数为耗时毫秒 */
        void onComplete(long costMs);

        /** 出错 */
        void onError(String message);
    }

    /**
     * 基于指定知识库流式问答（SSE）：检索 → 推送来源 → 逐 token 推送生成内容
     */
    public void chatStream(Long kbId, String question, String username, KbStreamListener listener)
    {
        if (kbId == null || StringUtils.isBlank(question))
        {
            listener.onError("知识库ID与问题不能为空");
            return;
        }
        YgKnowledgeBase kb = kbMapper.selectYgKnowledgeBaseByKbId(kbId);
        if (kb == null)
        {
            listener.onError("知识库不存在");
            return;
        }
        if ("1".equals(kb.getStatus()))
        {
            listener.onError("知识库已停用");
            return;
        }
        List<ChatMessage> messages;
        List<KbSourceVo> sources;
        long start = System.currentTimeMillis();
        try
        {
            checkDimension(kb);
            InMemoryEmbeddingStore<TextSegment> store = storeManager.getStore(kbId);
            Response<Embedding> queryEmb = modelManager.getEmbeddingModel().embed(question);
            List<EmbeddingMatch<TextSegment>> matches = store.search(EmbeddingSearchRequest.builder()
                    .queryEmbedding(queryEmb.content())
                    .maxResults(props.getRetrieval().getMaxResults())
                    .minScore(props.getRetrieval().getMinScore())
                    .build()).matches();
            sources = toSources(matches);
            listener.onSources(sources);
            if (sources.isEmpty())
            {
                String answer = "知识库中未找到与该问题相关的内容，无法回答。请尝试更换问法，或先上传相关文档。";
                listener.onToken(answer);
                KbChatResult result = new KbChatResult();
                result.setAnswer(answer);
                result.setSources(sources);
                result.setCostMs(System.currentTimeMillis() - start);
                saveChatLog(kbId, question, result, username);
                listener.onComplete(result.getCostMs());
                return;
            }
            messages = new ArrayList<>();
            messages.add(SystemMessage.from(SYSTEM_PROMPT));
            messages.add(UserMessage.from(buildUserPrompt(question, matches)));
        }
        catch (Exception e)
        {
            log.error("知识库流式问答检索阶段失败", e);
            listener.onError(friendlyMessage(e));
            return;
        }

        modelManager.getStreamingChatModel().chat(messages, new StreamingChatResponseHandler()
        {
            @Override
            public void onPartialResponse(String partialResponse)
            {
                if (StringUtils.isNotEmpty(partialResponse))
                {
                    listener.onToken(partialResponse);
                }
            }

            @Override
            public void onCompleteResponse(ChatResponse response)
            {
                try
                {
                    KbChatResult result = new KbChatResult();
                    result.setAnswer(response.aiMessage().text());
                    result.setSources(sources);
                    result.setCostMs(System.currentTimeMillis() - start);
                    saveChatLog(kbId, question, result, username);
                    listener.onComplete(result.getCostMs());
                }
                catch (Exception e)
                {
                    log.error("知识库流式问答完成阶段失败", e);
                    listener.onError("问答失败：" + e.getMessage());
                }
            }

            @Override
            public void onError(Throwable error)
            {
                log.error("知识库流式问答生成阶段失败", error);
                listener.onError(friendlyMessage(error instanceof Exception ? (Exception) error : new RuntimeException(error)));
            }
        });
    }

    private List<KbSourceVo> toSources(List<EmbeddingMatch<TextSegment>> matches)
    {
        List<KbSourceVo> sources = new ArrayList<>();
        for (EmbeddingMatch<TextSegment> match : matches)
        {
            TextSegment segment = match.embedded();
            if (segment == null)
            {
                continue;
            }
            KbSourceVo vo = new KbSourceVo();
            vo.setScore(round4(match.score()));
            String text = segment.text();
            vo.setSnippet(text.length() > 200 ? text.substring(0, 200) + "..." : text);
            try
            {
                String docId = segment.metadata().getString("document_id");
                if (StringUtils.isNotEmpty(docId))
                {
                    vo.setDocumentId(Long.valueOf(docId));
                }
                vo.setFileName(segment.metadata().getString("file_name"));
            }
            catch (Exception e)
            {
                log.warn("解析引用来源metadata失败：{}", e.getMessage());
            }
            sources.add(vo);
        }
        return sources;
    }

    private String buildUserPrompt(String question, List<EmbeddingMatch<TextSegment>> matches)
    {
        StringBuilder sb = new StringBuilder("【资料】\n");
        int index = 1;
        for (EmbeddingMatch<TextSegment> match : matches)
        {
            TextSegment segment = match.embedded();
            if (segment == null)
            {
                continue;
            }
            String fileName = "";
            try
            {
                fileName = StringUtils.defaultString(segment.metadata().getString("file_name"), "");
            }
            catch (Exception e)
            {
                // ignore
            }
            sb.append('[').append(index++).append(']');
            if (StringUtils.isNotEmpty(fileName))
            {
                sb.append('《').append(fileName).append('》');
            }
            sb.append(':').append(segment.text()).append('\n');
        }
        sb.append("\n【问题】\n").append(question);
        return sb.toString();
    }

    private void saveChatLog(Long kbId, String question, KbChatResult result, String username)
    {
        try
        {
            YgKbChatLog chatLog = new YgKbChatLog();
            chatLog.setKbId(kbId);
            chatLog.setQuestion(StringUtils.substring(question, 0, 2000));
            chatLog.setAnswer(result.getAnswer());
            chatLog.setSourceJson(JSON.toJSONString(result.getSources()));
            chatLog.setCostMs(result.getCostMs());
            chatLog.setCreateBy(username);
            chatLog.setCreateTime(new Date());
            chatLogMapper.insertYgKbChatLog(chatLog);
        }
        catch (Exception e)
        {
            log.error("保存问答日志失败：{}", e.getMessage());
        }
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

    private String friendlyMessage(Exception e)
    {
        String text = e.getClass().getName() + ": " + StringUtils.defaultString(e.getMessage(), "");
        if (text.contains("ConnectException") || text.contains("UnknownHostException") || text.contains("timed out")
                || hasConnectCause(e))
        {
            return "本地模型服务不可用：请确认 " + props.chatProviderName() + " 服务已启动（" + props.chatBaseUrl()
                    + "）且已拉取模型 " + props.currentChatModel();
        }
        return "问答失败：" + StringUtils.substring(text, 0, 500);
    }

    private boolean hasConnectCause(Throwable e)
    {
        Throwable cause = e.getCause();
        while (cause != null)
        {
            if (cause instanceof ConnectException || cause instanceof java.net.SocketException
                    || cause instanceof java.net.UnknownHostException)
            {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }

    private Double round4(double score)
    {
        return Math.round(score * 10000d) / 10000d;
    }
}
