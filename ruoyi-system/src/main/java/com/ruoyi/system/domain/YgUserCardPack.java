package com.ruoyi.system.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.ColumnType;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 用户卡包对象 yg_user_card_pack
 * 
 * @author ruoyi
 * @date 2026-10-12
 */
public class YgUserCardPack extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 卡包主键 */
    @Excel(name = "卡包主键", cellType = ColumnType.NUMERIC)
    private Long packId;

    /** 用户id */
    @Excel(name = "用户id", cellType = ColumnType.NUMERIC)
    private Long userId;

    /** 卡包名称 */
    @Excel(name = "卡包名称")
    private String packName;

    /** 卡片数量(联查统计) */
    @Excel(name = "卡片数量", cellType = ColumnType.NUMERIC)
    private Integer cardCount;

    /** 用户账号(联查展示) */
    private String userName;

    /** 用户昵称(联查展示) */
    private String nickName;

    /** 卡包封面(联查展示,取包内第一张卡的卡图) */
    private String coverUrl;

    public Long getPackId()
    {
        return packId;
    }

    public void setPackId(Long packId)
    {
        this.packId = packId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public String getPackName()
    {
        return packName;
    }

    public void setPackName(String packName)
    {
        this.packName = packName;
    }

    public Integer getCardCount()
    {
        return cardCount;
    }

    public void setCardCount(Integer cardCount)
    {
        this.cardCount = cardCount;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getNickName()
    {
        return nickName;
    }

    public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }

    public String getCoverUrl()
    {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl)
    {
        this.coverUrl = coverUrl;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("packId", getPackId())
            .append("userId", getUserId())
            .append("packName", getPackName())
            .append("cardCount", getCardCount())
            .append("userName", getUserName())
            .append("nickName", getNickName())
            .append("createTime", getCreateTime())
            .append("remark", getRemark())
            .toString();
    }
}
