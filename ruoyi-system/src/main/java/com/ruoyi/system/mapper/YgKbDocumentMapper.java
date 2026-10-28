package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.YgKbDocument;

/**
 * 知识库文档Mapper接口
 *
 * @author ruoyi
 * @date 2026-10-14
 */
public interface YgKbDocumentMapper
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
     * 查询某知识库全部有效文档（重建索引用）
     */
    public List<YgKbDocument> selectByKbIdAll(Long kbId);

    /**
     * 新增文档
     */
    public int insertYgKbDocument(YgKbDocument ygKbDocument);

    /**
     * 修改文档
     */
    public int updateYgKbDocument(YgKbDocument ygKbDocument);

    /**
     * 更新索引状态
     */
    public int updateIndexStatus(@Param("docId") Long docId, @Param("indexStatus") String indexStatus,
            @Param("errorMsg") String errorMsg);

    /**
     * 逻辑删除文档
     */
    public int deleteYgKbDocumentByDocIds(Long[] docIds);

    /**
     * 按知识库逻辑删除全部文档
     */
    public int deleteByKbIds(Long[] kbIds);
}
