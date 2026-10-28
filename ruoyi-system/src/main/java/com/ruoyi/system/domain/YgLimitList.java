package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.ColumnType;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 禁限卡表对象 yg_limit_list
 * 
 * @author ruoyi
 * @date 2026-10-12
 */
public class YgLimitList extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @Excel(name = "主键", cellType = ColumnType.NUMERIC)
    private Long limitId;

    /** 卡片密码(id) */
    @Excel(name = "卡片密码", cellType = ColumnType.NUMERIC)
    private Long cardId;

    /** 适用环境(ocg/tcg/sc) */
    @Excel(name = "适用环境")
    private String otType;

    /** 限制类型(0:无限制 1:限制 2:准限制 3:禁止) */
    @Excel(name = "限制类型", cellType = ColumnType.NUMERIC)
    private Integer limitType;

    /** 生效日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "生效日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date effectiveDate;

    /** 是否当前卡表（1是 0历史） */
    @Excel(name = "是否当前卡表")
    private String isCurrent;

    /** 卡片图片地址(关联查询 yg_card_info，仅用于列表展示) */
    private String imageUrl;

    public void setLimitId(Long limitId) 
    {
        this.limitId = limitId;
    }

    public Long getLimitId() 
    {
        return limitId;
    }

    public void setCardId(Long cardId) 
    {
        this.cardId = cardId;
    }

    public Long getCardId() 
    {
        return cardId;
    }

    public void setOtType(String otType) 
    {
        this.otType = otType;
    }

    public String getOtType() 
    {
        return otType;
    }

    public void setLimitType(Integer limitType) 
    {
        this.limitType = limitType;
    }

    public Integer getLimitType() 
    {
        return limitType;
    }

    public void setEffectiveDate(Date effectiveDate) 
    {
        this.effectiveDate = effectiveDate;
    }

    public Date getEffectiveDate() 
    {
        return effectiveDate;
    }

    public void setIsCurrent(String isCurrent) 
    {
        this.isCurrent = isCurrent;
    }

    public String getIsCurrent() 
    {
        return isCurrent;
    }

    public void setImageUrl(String imageUrl) 
    {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() 
    {
        return imageUrl;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("limitId", getLimitId())
            .append("cardId", getCardId())
            .append("otType", getOtType())
            .append("limitType", getLimitType())
            .append("effectiveDate", getEffectiveDate())
            .append("isCurrent", getIsCurrent())
            .append("imageUrl", getImageUrl())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
