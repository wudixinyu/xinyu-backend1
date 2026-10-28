package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.YgKnowledgeBase;

/**
 * 知识库Service接口
 *
 * @author ruoyi
 * @date 2026-10-14
 */
public interface IYgKnowledgeBaseService
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
     * 新增知识库（固化当前embedding模型与维度）
     */
    public int insertYgKnowledgeBase(YgKnowledgeBase ygKnowledgeBase);

    /**
     * 修改知识库
     */
    public int updateYgKnowledgeBase(YgKnowledgeBase ygKnowledgeBase);

    /**
     * 批量删除知识库（级联删除文档记录、向量文件与上传目录）
     */
    public int deleteYgKnowledgeBaseByKbIds(Long[] kbIds);
}
