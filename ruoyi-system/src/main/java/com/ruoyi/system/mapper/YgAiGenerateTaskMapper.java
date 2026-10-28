package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.YgAiGenerateTask;

/**
 * AI生成任务Mapper接口
 * 
 * @author ruoyi
 * @date 2026-10-14
 */
public interface YgAiGenerateTaskMapper 
{
    /**
     * 查询AI生成任务
     * 
     * @param taskId AI生成任务主键
     * @return AI生成任务
     */
    public YgAiGenerateTask selectYgAiGenerateTaskByTaskId(Long taskId);

    /**
     * 批量查询AI生成任务
     * 
     * @param taskIds 任务主键集合
     * @return AI生成任务集合
     */
    public List<YgAiGenerateTask> selectYgAiGenerateTaskByTaskIds(@Param("taskIds") List<Long> taskIds);

    /**
     * 新增AI生成任务
     * 
     * @param ygAiGenerateTask AI生成任务
     * @return 结果
     */
    public int insertYgAiGenerateTask(YgAiGenerateTask ygAiGenerateTask);

    /**
     * 修改AI生成任务
     * 
     * @param ygAiGenerateTask AI生成任务
     * @return 结果
     */
    public int updateYgAiGenerateTask(YgAiGenerateTask ygAiGenerateTask);
}
