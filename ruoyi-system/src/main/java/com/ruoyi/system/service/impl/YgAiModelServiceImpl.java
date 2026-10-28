package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.YgAiModel;
import com.ruoyi.system.mapper.YgAiModelMapper;
import com.ruoyi.system.service.IYgAiModelService;

/**
 * AI生成模型档案Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-10-14
 */
@Service
public class YgAiModelServiceImpl implements IYgAiModelService 
{
    @Autowired
    private YgAiModelMapper ygAiModelMapper;

    /**
     * 查询AI生成模型档案
     * 
     * @param modelId AI生成模型档案主键
     * @return AI生成模型档案
     */
    @Override
    public YgAiModel selectYgAiModelByModelId(Long modelId)
    {
        return ygAiModelMapper.selectYgAiModelByModelId(modelId);
    }

    /**
     * 查询AI生成模型档案列表
     * 
     * @param ygAiModel AI生成模型档案
     * @return AI生成模型档案
     */
    @Override
    public List<YgAiModel> selectYgAiModelList(YgAiModel ygAiModel)
    {
        return ygAiModelMapper.selectYgAiModelList(ygAiModel);
    }

    /**
     * 新增AI生成模型档案
     * 
     * @param ygAiModel AI生成模型档案
     * @return 结果
     */
    @Override
    public int insertYgAiModel(YgAiModel ygAiModel)
    {
        ygAiModel.setCreateTime(DateUtils.getNowDate());
        return ygAiModelMapper.insertYgAiModel(ygAiModel);
    }

    /**
     * 修改AI生成模型档案
     * 
     * @param ygAiModel AI生成模型档案
     * @return 结果
     */
    @Override
    public int updateYgAiModel(YgAiModel ygAiModel)
    {
        ygAiModel.setUpdateTime(DateUtils.getNowDate());
        return ygAiModelMapper.updateYgAiModel(ygAiModel);
    }

    /**
     * 批量删除AI生成模型档案
     * 
     * @param modelIds 需要删除的AI生成模型档案主键
     * @return 结果
     */
    @Override
    public int deleteYgAiModelByModelIds(Long[] modelIds)
    {
        return ygAiModelMapper.deleteYgAiModelByModelIds(modelIds);
    }

    /**
     * 删除AI生成模型档案信息
     * 
     * @param modelId AI生成模型档案主键
     * @return 结果
     */
    @Override
    public int deleteYgAiModelByModelId(Long modelId)
    {
        return ygAiModelMapper.deleteYgAiModelByModelId(modelId);
    }
}
