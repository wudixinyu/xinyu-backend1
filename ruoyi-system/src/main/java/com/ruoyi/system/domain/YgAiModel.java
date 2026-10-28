package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.ColumnType;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * AI生成模型档案对象 yg_ai_model
 * 
 * @author ruoyi
 * @date 2026-10-14
 */
public class YgAiModel extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 模型主键 */
    @Excel(name = "模型主键", cellType = ColumnType.NUMERIC)
    private Long modelId;

    /** 模型名称 */
    @Excel(name = "模型名称")
    private String modelName;

    /** 模型类型(1:文本 2:图片 3:视频 4:音乐 5:旁白 6:多模态 7:向量化) */
    @Excel(name = "模型类型", cellType = ColumnType.NUMERIC)
    private Integer modelType;

    /** 提供方 */
    @Excel(name = "提供方")
    private String provider;

    /** API地址 */
    @Excel(name = "API地址")
    private String apiUrl;

    /** API Key（列表查询不返回） */
    private String apiKey;

    /** 显示排序 */
    @Excel(name = "显示排序", cellType = ColumnType.NUMERIC)
    private Integer sort;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 删除标志 */
    private String delFlag;

    public void setModelId(Long modelId) 
    {
        this.modelId = modelId;
    }

    public Long getModelId() 
    {
        return modelId;
    }

    public void setModelName(String modelName) 
    {
        this.modelName = modelName;
    }

    public String getModelName() 
    {
        return modelName;
    }

    public void setModelType(Integer modelType) 
    {
        this.modelType = modelType;
    }

    public Integer getModelType() 
    {
        return modelType;
    }

    public void setProvider(String provider) 
    {
        this.provider = provider;
    }

    public String getProvider() 
    {
        return provider;
    }

    public void setApiUrl(String apiUrl) 
    {
        this.apiUrl = apiUrl;
    }

    public String getApiUrl() 
    {
        return apiUrl;
    }

    public void setApiKey(String apiKey) 
    {
        this.apiKey = apiKey;
    }

    public String getApiKey() 
    {
        return apiKey;
    }

    public void setSort(Integer sort) 
    {
        this.sort = sort;
    }

    public Integer getSort() 
    {
        return sort;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("modelId", getModelId())
            .append("modelName", getModelName())
            .append("modelType", getModelType())
            .append("provider", getProvider())
            .append("apiUrl", getApiUrl())
            .append("sort", getSort())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
