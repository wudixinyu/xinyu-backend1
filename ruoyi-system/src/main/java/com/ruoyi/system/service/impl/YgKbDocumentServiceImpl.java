package com.ruoyi.system.service.impl;

import java.io.File;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.system.domain.YgKbDocument;
import com.ruoyi.system.domain.YgKnowledgeBase;
import com.ruoyi.system.kb.config.KbProperties;
import com.ruoyi.system.kb.rag.KbIngestService;
import com.ruoyi.system.mapper.YgKbDocumentMapper;
import com.ruoyi.system.mapper.YgKnowledgeBaseMapper;
import com.ruoyi.system.service.IYgKbDocumentService;

/**
 * 知识库文档Service业务层处理
 *
 * @author ruoyi
 * @date 2026-10-14
 */
@Service
public class YgKbDocumentServiceImpl implements IYgKbDocumentService
{
    private static final Logger log = LoggerFactory.getLogger(YgKbDocumentServiceImpl.class);

    /** 允许上传的文档扩展名 */
    private static final String[] ALLOWED_EXT = { "txt", "md", "markdown", "pdf", "docx" };

    @Autowired
    private YgKbDocumentMapper ygKbDocumentMapper;

    @Autowired
    private YgKnowledgeBaseMapper ygKnowledgeBaseMapper;

    @Autowired
    private KbProperties props;

    @Autowired
    private KbIngestService ingestService;

    @Override
    public YgKbDocument selectYgKbDocumentByDocId(Long docId)
    {
        return ygKbDocumentMapper.selectYgKbDocumentByDocId(docId);
    }

    @Override
    public List<YgKbDocument> selectYgKbDocumentList(YgKbDocument ygKbDocument)
    {
        return ygKbDocumentMapper.selectYgKbDocumentList(ygKbDocument);
    }

    @Override
    public YgKbDocument uploadDocument(Long kbId, MultipartFile file, String username)
    {
        YgKnowledgeBase kb = ygKnowledgeBaseMapper.selectYgKnowledgeBaseByKbId(kbId);
        if (kb == null)
        {
            throw new ServiceException("知识库不存在");
        }
        if (file == null || file.isEmpty())
        {
            throw new ServiceException("上传文件不能为空");
        }
        String relativePath;
        try
        {
            String baseDir = props.uploadPath().resolve(String.valueOf(kbId)).toString();
            relativePath = FileUploadUtils.upload(baseDir, file, ALLOWED_EXT, true);
        }
        catch (Exception e)
        {
            throw new ServiceException("文件上传失败：" + e.getMessage());
        }
        YgKbDocument doc = new YgKbDocument();
        doc.setKbId(kbId);
        doc.setFileName(file.getOriginalFilename());
        doc.setFilePath(relativePath);
        doc.setFileType(StringUtils.lowerCase(StringUtils.substringAfterLast(relativePath, ".")));
        doc.setFileSize(file.getSize());
        doc.setChunkCount(0);
        doc.setIndexStatus("0");
        doc.setDelFlag("0");
        doc.setCreateBy(username);
        doc.setCreateTime(DateUtils.getNowDate());
        ygKbDocumentMapper.insertYgKbDocument(doc);
        // 同步解析入库（失败时 ingest 内部已置状态为3并抛出异常提示）
        ingestService.ingest(doc, true);
        return ygKbDocumentMapper.selectYgKbDocumentByDocId(doc.getDocId());
    }

    @Override
    public int deleteYgKbDocumentByDocIds(Long[] docIds)
    {
        for (Long docId : docIds)
        {
            YgKbDocument doc = ygKbDocumentMapper.selectYgKbDocumentByDocId(docId);
            if (doc != null)
            {
                deletePhysicalFile(doc.getFilePath());
            }
        }
        return ygKbDocumentMapper.deleteYgKbDocumentByDocIds(docIds);
    }

    @Override
    public void rebuildIndex(Long kbId)
    {
        ingestService.rebuild(kbId);
    }

    private void deletePhysicalFile(String relativePath)
    {
        if (StringUtils.isEmpty(relativePath))
        {
            return;
        }
        try
        {
            File file = resolveFile(relativePath);
            if (file.exists())
            {
                file.delete();
            }
        }
        catch (Exception e)
        {
            log.warn("删除文档物理文件失败：{}", e.getMessage());
        }
    }

    private File resolveFile(String relativePath)
    {
        if (relativePath.startsWith(Constants.RESOURCE_PREFIX))
        {
            return new File(com.ruoyi.common.config.RuoYiConfig.getProfile()
                    + StringUtils.substringAfter(relativePath, Constants.RESOURCE_PREFIX));
        }
        return new File(relativePath);
    }
}
