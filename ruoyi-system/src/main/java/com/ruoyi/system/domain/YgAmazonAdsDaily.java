package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.ColumnType;
import com.ruoyi.common.annotation.Excel.Type;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 亚马逊广告活动日报对象 yg_amazon_ads_daily
 *
 * @author ruoyi
 * @date 2026-10-14
 */
public class YgAmazonAdsDaily extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @Excel(name = "主键", cellType = ColumnType.NUMERIC, type = Type.EXPORT)
    private Long dailyId;

    /** 报告日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "日期", dateFormat = "yyyy-MM-dd", headerMatch = { "Date" })
    private Date reportDate;

    /** 广告档案/店铺标识(可选) */
    @Excel(name = "Profile", headerMatch = { "Profile ID" })
    private String profileId;

    /** 广告活动名称 */
    @Excel(name = "广告活动", headerMatch = { "Campaign", "Campaign Name" })
    private String campaignName;

    /** 曝光量 */
    @Excel(name = "曝光量", cellType = ColumnType.NUMERIC, headerMatch = { "Impressions" })
    private Long impressions;

    /** 点击量 */
    @Excel(name = "点击量", cellType = ColumnType.NUMERIC, headerMatch = { "Clicks" })
    private Long clicks;

    /** 花费 */
    @Excel(name = "花费", cellType = ColumnType.NUMERIC, scale = 2, headerMatch = { "Spend" })
    private BigDecimal spend;

    /** 销售额 */
    @Excel(name = "销售额", cellType = ColumnType.NUMERIC, scale = 2, headerMatch = { "Total Sales", "Sales" })
    private BigDecimal sales;

    /** 订单数 */
    @Excel(name = "订单数", cellType = ColumnType.NUMERIC, headerMatch = { "Orders" })
    private Long orders;

    public void setDailyId(Long dailyId)
    {
        this.dailyId = dailyId;
    }

    public Long getDailyId()
    {
        return dailyId;
    }

    public void setReportDate(Date reportDate)
    {
        this.reportDate = reportDate;
    }

    public Date getReportDate()
    {
        return reportDate;
    }

    public void setProfileId(String profileId)
    {
        this.profileId = profileId;
    }

    public String getProfileId()
    {
        return profileId;
    }

    public void setCampaignName(String campaignName)
    {
        this.campaignName = campaignName;
    }

    public String getCampaignName()
    {
        return campaignName;
    }

    public void setImpressions(Long impressions)
    {
        this.impressions = impressions;
    }

    public Long getImpressions()
    {
        return impressions;
    }

    public void setClicks(Long clicks)
    {
        this.clicks = clicks;
    }

    public Long getClicks()
    {
        return clicks;
    }

    public void setSpend(BigDecimal spend)
    {
        this.spend = spend;
    }

    public BigDecimal getSpend()
    {
        return spend;
    }

    public void setSales(BigDecimal sales)
    {
        this.sales = sales;
    }

    public BigDecimal getSales()
    {
        return sales;
    }

    public void setOrders(Long orders)
    {
        this.orders = orders;
    }

    public Long getOrders()
    {
        return orders;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("dailyId", getDailyId())
            .append("reportDate", getReportDate())
            .append("profileId", getProfileId())
            .append("campaignName", getCampaignName())
            .append("impressions", getImpressions())
            .append("clicks", getClicks())
            .append("spend", getSpend())
            .append("sales", getSales())
            .append("orders", getOrders())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
