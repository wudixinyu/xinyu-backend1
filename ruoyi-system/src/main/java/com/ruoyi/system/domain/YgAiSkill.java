package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.ColumnType;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * AI技能对象 yg_ai_skill
 * 
 * @author ruoyi
 * @date 2026-10-14
 */
public class YgAiSkill extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 技能主键 */
    @Excel(name = "技能主键", cellType = ColumnType.NUMERIC)
    private Long skillId;

    /** 技能名称 */
    @Excel(name = "技能名称")
    private String skillName;

    /** 技能标识（唯一） */
    @Excel(name = "技能标识")
    private String skillCode;

    /** 技能分类 */
    @Excel(name = "技能分类")
    private String category;

    /** 技能说明 */
    @Excel(name = "技能说明")
    private String description;

    /** 技能内容（提示词/脚本，列表查询不返回） */
    private String content;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 删除标志 */
    private String delFlag;

    public void setSkillId(Long skillId) 
    {
        this.skillId = skillId;
    }

    public Long getSkillId() 
    {
        return skillId;
    }

    public void setSkillName(String skillName) 
    {
        this.skillName = skillName;
    }

    public String getSkillName() 
    {
        return skillName;
    }

    public void setSkillCode(String skillCode) 
    {
        this.skillCode = skillCode;
    }

    public String getSkillCode() 
    {
        return skillCode;
    }

    public void setCategory(String category) 
    {
        this.category = category;
    }

    public String getCategory() 
    {
        return category;
    }

    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }

    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
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
            .append("skillId", getSkillId())
            .append("skillName", getSkillName())
            .append("skillCode", getSkillCode())
            .append("category", getCategory())
            .append("description", getDescription())
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
