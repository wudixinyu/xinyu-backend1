package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.ColumnType;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * AI提示词对象 yg_ai_prompt
 * 
 * @author ruoyi
 * @date 2026-10-14
 */
public class YgAiPrompt extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 提示词主键 */
    @Excel(name = "提示词主键", cellType = ColumnType.NUMERIC)
    private Long promptId;

    /** 提示词标题 */
    @Excel(name = "提示词标题")
    private String promptName;

    /** 生成类型(1:文本 2:图片 3:视频 4:音乐 5:旁白 6:多模态) */
    @Excel(name = "生成类型", cellType = ColumnType.NUMERIC)
    private Integer promptType;

    /** 提示词内容 */
    @Excel(name = "提示词内容")
    private String promptContent;

    /** 适用模型 */
    @Excel(name = "适用模型")
    private String modelName;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 删除标志 */
    private String delFlag;

    public void setPromptId(Long promptId) 
    {
        this.promptId = promptId;
    }

    public Long getPromptId() 
    {
        return promptId;
    }

    public void setPromptName(String promptName) 
    {
        this.promptName = promptName;
    }

    public String getPromptName() 
    {
        return promptName;
    }

    public void setPromptType(Integer promptType) 
    {
        this.promptType = promptType;
    }

    public Integer getPromptType() 
    {
        return promptType;
    }

    public void setPromptContent(String promptContent) 
    {
        this.promptContent = promptContent;
    }

    public String getPromptContent() 
    {
        return promptContent;
    }

    public void setModelName(String modelName) 
    {
        this.modelName = modelName;
    }

    public String getModelName() 
    {
        return modelName;
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
            .append("promptId", getPromptId())
            .append("promptName", getPromptName())
            .append("promptType", getPromptType())
            .append("promptContent", getPromptContent())
            .append("modelName", getModelName())
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
