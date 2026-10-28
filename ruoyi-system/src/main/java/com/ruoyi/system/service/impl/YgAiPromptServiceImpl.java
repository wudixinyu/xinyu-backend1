package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.YgAiPrompt;
import com.ruoyi.system.mapper.YgAiPromptMapper;
import com.ruoyi.system.service.IYgAiPromptService;

/**
 * AI提示词Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-10-14
 */
@Service
public class YgAiPromptServiceImpl implements IYgAiPromptService 
{
    @Autowired
    private YgAiPromptMapper ygAiPromptMapper;

    /**
     * 查询AI提示词
     * 
     * @param promptId AI提示词主键
     * @return AI提示词
     */
    @Override
    public YgAiPrompt selectYgAiPromptByPromptId(Long promptId)
    {
        return ygAiPromptMapper.selectYgAiPromptByPromptId(promptId);
    }

    /**
     * 查询AI提示词列表
     * 
     * @param ygAiPrompt AI提示词
     * @return AI提示词
     */
    @Override
    public List<YgAiPrompt> selectYgAiPromptList(YgAiPrompt ygAiPrompt)
    {
        return ygAiPromptMapper.selectYgAiPromptList(ygAiPrompt);
    }

    /**
     * 新增AI提示词
     * 
     * @param ygAiPrompt AI提示词
     * @return 结果
     */
    @Override
    public int insertYgAiPrompt(YgAiPrompt ygAiPrompt)
    {
        ygAiPrompt.setCreateTime(DateUtils.getNowDate());
        return ygAiPromptMapper.insertYgAiPrompt(ygAiPrompt);
    }

    /**
     * 修改AI提示词
     * 
     * @param ygAiPrompt AI提示词
     * @return 结果
     */
    @Override
    public int updateYgAiPrompt(YgAiPrompt ygAiPrompt)
    {
        ygAiPrompt.setUpdateTime(DateUtils.getNowDate());
        return ygAiPromptMapper.updateYgAiPrompt(ygAiPrompt);
    }

    /**
     * 批量删除AI提示词
     * 
     * @param promptIds 需要删除的AI提示词主键
     * @return 结果
     */
    @Override
    public int deleteYgAiPromptByPromptIds(Long[] promptIds)
    {
        return ygAiPromptMapper.deleteYgAiPromptByPromptIds(promptIds);
    }

    /**
     * 删除AI提示词信息
     * 
     * @param promptId AI提示词主键
     * @return 结果
     */
    @Override
    public int deleteYgAiPromptByPromptId(Long promptId)
    {
        return ygAiPromptMapper.deleteYgAiPromptByPromptId(promptId);
    }
}
