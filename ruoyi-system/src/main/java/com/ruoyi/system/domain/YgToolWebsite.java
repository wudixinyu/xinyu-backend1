package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 工具使用网站对象 yg_tool_website
 * 
 * @author ruoyi
 * @date 2026-10-16
 */
public class YgToolWebsite extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long toolId;

    /** 工具名称 */
    private String toolName;

    /** 网站地址 */
    private String websiteUrl;

    /** 工具分类（字典 yg_tool_category） */
    private String category;

    public void setToolId(Long toolId) 
    {
        this.toolId = toolId;
    }

    public Long getToolId() 
    {
        return toolId;
    }

    public void setToolName(String toolName) 
    {
        this.toolName = toolName;
    }

    public String getToolName() 
    {
        return toolName;
    }

    public void setWebsiteUrl(String websiteUrl)
    {
        this.websiteUrl = websiteUrl;
    }

    public String getWebsiteUrl()
    {
        return websiteUrl;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public String getCategory()
    {
        return category;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("toolId", getToolId())
            .append("toolName", getToolName())
            .append("websiteUrl", getWebsiteUrl())
            .append("category", getCategory())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
