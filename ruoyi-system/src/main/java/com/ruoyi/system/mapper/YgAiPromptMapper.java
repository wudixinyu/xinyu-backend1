package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.YgAiPrompt;

/**
 * AI提示词Mapper接口
 * 
 * @author ruoyi
 * @date 2026-10-14
 */
public interface YgAiPromptMapper 
{
    /**
     * 查询AI提示词
     * 
     * @param promptId AI提示词主键
     * @return AI提示词
     */
    public YgAiPrompt selectYgAiPromptByPromptId(Long promptId);

    /**
     * 查询AI提示词列表
     * 
     * @param ygAiPrompt AI提示词
     * @return AI提示词集合
     */
    public List<YgAiPrompt> selectYgAiPromptList(YgAiPrompt ygAiPrompt);

    /**
     * 新增AI提示词
     * 
     * @param ygAiPrompt AI提示词
     * @return 结果
     */
    public int insertYgAiPrompt(YgAiPrompt ygAiPrompt);

    /**
     * 修改AI提示词
     * 
     * @param ygAiPrompt AI提示词
     * @return 结果
     */
    public int updateYgAiPrompt(YgAiPrompt ygAiPrompt);

    /**
     * 删除AI提示词
     * 
     * @param promptId AI提示词主键
     * @return 结果
     */
    public int deleteYgAiPromptByPromptId(Long promptId);

    /**
     * 批量删除AI提示词
     * 
     * @param promptIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteYgAiPromptByPromptIds(Long[] promptIds);
}
