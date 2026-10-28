package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.YgAiGenerateTask;
import com.ruoyi.system.mapper.YgAiGenerateTaskMapper;
import com.ruoyi.system.service.IYgAiGenerateTaskService;

/**
 * AI生成任务Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-10-14
 */
@Service
public class YgAiGenerateTaskServiceImpl implements IYgAiGenerateTaskService 
{
    @Autowired
    private YgAiGenerateTaskMapper ygAiGenerateTaskMapper;

    @Override
    public YgAiGenerateTask selectYgAiGenerateTaskByTaskId(Long taskId)
    {
        return ygAiGenerateTaskMapper.selectYgAiGenerateTaskByTaskId(taskId);
    }

    @Override
    public List<YgAiGenerateTask> selectYgAiGenerateTaskByTaskIds(List<Long> taskIds)
    {
        return ygAiGenerateTaskMapper.selectYgAiGenerateTaskByTaskIds(taskIds);
    }

    @Override
    public int insertYgAiGenerateTask(YgAiGenerateTask ygAiGenerateTask)
    {
        ygAiGenerateTask.setCreateTime(DateUtils.getNowDate());
        return ygAiGenerateTaskMapper.insertYgAiGenerateTask(ygAiGenerateTask);
    }

    @Override
    public int updateYgAiGenerateTask(YgAiGenerateTask ygAiGenerateTask)
    {
        ygAiGenerateTask.setUpdateTime(DateUtils.getNowDate());
        return ygAiGenerateTaskMapper.updateYgAiGenerateTask(ygAiGenerateTask);
    }
}
