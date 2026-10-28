package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.YgAiModel;

/**
 * AI生成模型档案Service接口
 * 
 * @author ruoyi
 * @date 2026-10-14
 */
public interface IYgAiModelService 
{
    /**
     * 查询AI生成模型档案
     * 
     * @param modelId AI生成模型档案主键
     * @return AI生成模型档案
     */
    public YgAiModel selectYgAiModelByModelId(Long modelId);

    /**
     * 查询AI生成模型档案列表（不含 api_key）
     * 
     * @param ygAiModel AI生成模型档案
     * @return AI生成模型档案集合
     */
    public List<YgAiModel> selectYgAiModelList(YgAiModel ygAiModel);

    /**
     * 新增AI生成模型档案
     * 
     * @param ygAiModel AI生成模型档案
     * @return 结果
     */
    public int insertYgAiModel(YgAiModel ygAiModel);

    /**
     * 修改AI生成模型档案
     * 
     * @param ygAiModel AI生成模型档案
     * @return 结果
     */
    public int updateYgAiModel(YgAiModel ygAiModel);

    /**
     * 批量删除AI生成模型档案
     * 
     * @param modelIds 需要删除的AI生成模型档案主键集合
     * @return 结果
     */
    public int deleteYgAiModelByModelIds(Long[] modelIds);

    /**
     * 删除AI生成模型档案信息
     * 
     * @param modelId AI生成模型档案主键
     * @return 结果
     */
    public int deleteYgAiModelByModelId(Long modelId);
}
