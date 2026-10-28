package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.YgAiModel;

/**
 * AI生成模型档案Mapper接口
 * 
 * @author ruoyi
 * @date 2026-10-14
 */
public interface YgAiModelMapper 
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
     * 删除AI生成模型档案
     * 
     * @param modelId AI生成模型档案主键
     * @return 结果
     */
    public int deleteYgAiModelByModelId(Long modelId);

    /**
     * 批量删除AI生成模型档案
     * 
     * @param modelIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteYgAiModelByModelIds(Long[] modelIds);
}
