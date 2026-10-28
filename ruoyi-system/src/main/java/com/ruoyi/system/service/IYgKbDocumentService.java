package com.ruoyi.system.service;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.system.domain.YgKbDocument;

/**
 * 知识库文档Service接口
 *
 * @author ruoyi
 * @date 2026-10-14
 */
public interface IYgKbDocumentService
{
    /**
     * 查询文档
     */
    public YgKbDocument selectYgKbDocumentByDocId(Long docId);

    /**
     * 查询文档列表
     */
    public List<YgKbDocument> selectYgKbDocumentList(YgKbDocument ygKbDocument);

    /**
     * 上传文档并同步解析入库
     */
    public YgKbDocument uploadDocument(Long kbId, MultipartFile file, String username);

    /**
     * 批量删除文档（删除记录与文件，提示需重建索引）
     */
    public int deleteYgKbDocumentByDocIds(Long[] docIds);

    /**
     * 重建知识库索引
     */
    public void rebuildIndex(Long kbId);
}
