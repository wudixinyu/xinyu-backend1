package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 卡组系列对象 yg_card_set
 * 
 * @author ruoyi
 * @date 2026-10-12
 */
public class YgCardSet extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 系列主键 */
    private Long setId;

    /** 系列code(十六进制字符串,如0x57) */
    @Excel(name = "系列code")
    private String setCode;

    /** 系列code数值(用于与卡片setcode按位匹配) */
    @Excel(name = "系列code数值")
    private Long setValue;

    /** 系列中文名 */
    @Excel(name = "系列中文名")
    private String cnName;

    /** 系列日文名 */
    @Excel(name = "系列日文名")
    private String jpName;

    /** 系列英文名 */
    @Excel(name = "系列英文名")
    private String enName;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    public void setSetId(Long setId) 
    {
        this.setId = setId;
    }

    public Long getSetId() 
    {
        return setId;
    }

    public void setSetCode(String setCode) 
    {
        this.setCode = setCode;
    }

    public String getSetCode() 
    {
        return setCode;
    }

    public void setSetValue(Long setValue) 
    {
        this.setValue = setValue;
    }

    public Long getSetValue() 
    {
        return setValue;
    }

    public void setCnName(String cnName) 
    {
        this.cnName = cnName;
    }

    public String getCnName() 
    {
        return cnName;
    }

    public void setJpName(String jpName) 
    {
        this.jpName = jpName;
    }

    public String getJpName() 
    {
        return jpName;
    }

    public void setEnName(String enName) 
    {
        this.enName = enName;
    }

    public String getEnName() 
    {
        return enName;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("setId", getSetId())
            .append("setCode", getSetCode())
            .append("setValue", getSetValue())
            .append("cnName", getCnName())
            .append("jpName", getJpName())
            .append("enName", getEnName())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
