package com.ruoyi.system.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.ColumnType;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 用户收藏对象 yg_user_collection
 * 
 * @author ruoyi
 * @date 2026-10-12
 */
public class YgUserCollection extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 收藏主键 */
    @Excel(name = "收藏主键", cellType = ColumnType.NUMERIC)
    private Long collectId;

    /** 用户id */
    @Excel(name = "用户id", cellType = ColumnType.NUMERIC)
    private Long userId;

    /** 卡片密码(id) */
    @Excel(name = "卡片密码", cellType = ColumnType.NUMERIC)
    private Long cardId;

    /** 数量(1-3) */
    @Excel(name = "数量", cellType = ColumnType.NUMERIC)
    private Integer quantity;

    /** 卡片中文名(联查展示) */
    private String cnName;

    /** 卡图地址(联查展示) */
    private String imageUrl;

    public String getCnName()
    {
        return cnName;
    }

    public void setCnName(String cnName)
    {
        this.cnName = cnName;
    }

    public String getImageUrl()
    {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }

    public void setCollectId(Long collectId) 
    {
        this.collectId = collectId;
    }

    public Long getCollectId() 
    {
        return collectId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setCardId(Long cardId) 
    {
        this.cardId = cardId;
    }

    public Long getCardId() 
    {
        return cardId;
    }

    public void setQuantity(Integer quantity) 
    {
        this.quantity = quantity;
    }

    public Integer getQuantity() 
    {
        return quantity;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("collectId", getCollectId())
            .append("userId", getUserId())
            .append("cardId", getCardId())
            .append("quantity", getQuantity())
            .append("cnName", getCnName())
            .append("imageUrl", getImageUrl())
            .append("createTime", getCreateTime())
            .append("remark", getRemark())
            .toString();
    }
}
