package com.ruoyi.system.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.ColumnType;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 用户卡包明细对象 yg_user_card_pack_item
 * 
 * @author ruoyi
 * @date 2026-10-12
 */
public class YgUserCardPackItem extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 明细主键 */
    @Excel(name = "明细主键", cellType = ColumnType.NUMERIC)
    private Long itemId;

    /** 卡包主键 */
    @Excel(name = "卡包主键", cellType = ColumnType.NUMERIC)
    private Long packId;

    /** 卡片密码(id) */
    @Excel(name = "卡片密码", cellType = ColumnType.NUMERIC)
    private Long cardId;

    /** 数量 */
    @Excel(name = "数量", cellType = ColumnType.NUMERIC)
    private Integer quantity;

    /** 卡片中文名(联查展示) */
    private String cnName;

    /** 卡图地址(联查展示) */
    private String imageUrl;

    /** 卡片类型位掩码(联查展示,用于区分主卡/额外卡片) */
    private Integer type;

    public Long getItemId()
    {
        return itemId;
    }

    public void setItemId(Long itemId)
    {
        this.itemId = itemId;
    }

    public Long getPackId()
    {
        return packId;
    }

    public void setPackId(Long packId)
    {
        this.packId = packId;
    }

    public Long getCardId()
    {
        return cardId;
    }

    public void setCardId(Long cardId)
    {
        this.cardId = cardId;
    }

    public Integer getQuantity()
    {
        return quantity;
    }

    public void setQuantity(Integer quantity)
    {
        this.quantity = quantity;
    }

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

    public Integer getType()
    {
        return type;
    }

    public void setType(Integer type)
    {
        this.type = type;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("itemId", getItemId())
            .append("packId", getPackId())
            .append("cardId", getCardId())
            .append("quantity", getQuantity())
            .append("cnName", getCnName())
            .append("imageUrl", getImageUrl())
            .append("createTime", getCreateTime())
            .append("remark", getRemark())
            .toString();
    }
}
