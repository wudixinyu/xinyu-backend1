package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.YgKnowledgeBase;

/**
 * 知识库Mapper接口
 *
 * @author ruoyi
 * @date 2026-10-14
 */
public interface YgKnowledgeBaseMapper
{
    /**
     * 查询知识库
     */
    public YgKnowledgeBase selectYgKnowledgeBaseByKbId(Long kbId);

    /**
     * 查询知识库列表
     */
    public List<YgKnowledgeBase> selectYgKnowledgeBaseList(YgKnowledgeBase ygKnowledgeBase);

    /**
     * 新增知识库
     */
    public int insertYgKnowledgeBase(YgKnowledgeBase ygKnowledgeBase);

    /**
     * 修改知识库
     */
    public int updateYgKnowledgeBase(YgKnowledgeBase ygKnowledgeBase);

    /**
     * 逻辑删除知识库
     */
    public int deleteYgKnowledgeBaseByKbIds(Long[] kbIds);

    /**
     * 刷新统计（文档数/分块数由子查询重算）
     */
    public int updateStats(@Param("kbId") Long kbId);
}
