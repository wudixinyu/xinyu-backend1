package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.YgKbModelConfig;

/**
 * 知识库模型配置Mapper接口
 *
 * @author ruoyi
 * @date 2026-10-14
 */
public interface YgKbModelConfigMapper
{
    /**
     * 查询模型配置（取最新一行）
     */
    public YgKbModelConfig selectKbModelConfig();

    /**
     * 新增模型配置
     */
    public int insertKbModelConfig(YgKbModelConfig ygKbModelConfig);

    /**
     * 修改模型配置
     */
    public int updateKbModelConfig(YgKbModelConfig ygKbModelConfig);
}
