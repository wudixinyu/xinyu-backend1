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
 * WooCommerce店铺渠道日报对象 yg_woocommerce_store_daily
 *
 * @author ruoyi
 * @date 2026-10-16
 */
public class YgWooCommerceStoreDaily extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @Excel(name = "主键", cellType = ColumnType.NUMERIC, type = Type.EXPORT)
    private Long dailyId;

    /** 报告日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "日期", dateFormat = "yyyy-MM-dd", headerMatch = { "Date" })
    private Date reportDate;

    /** 店铺域名(标识,可选) */
    @Excel(name = "店铺域名", headerMatch = { "Store Domain", "Site URL", "Domain" })
    private String storeDomain;

    /** 销售渠道 */
    @Excel(name = "销售渠道", headerMatch = { "Channel", "Sales Channel" })
    private String channel;

    /** 会话数(访客数) */
    @Excel(name = "会话数", cellType = ColumnType.NUMERIC, headerMatch = { "Sessions", "Visits", "Unique Visitors" })
    private Long sessions;

    /** 订单数 */
    @Excel(name = "订单数", cellType = ColumnType.NUMERIC, headerMatch = { "Orders", "Total Orders" })
    private Long orders;

    /** 售出件数 */
    @Excel(name = "售出件数", cellType = ColumnType.NUMERIC, headerMatch = { "Units Sold", "Items Sold", "Units" })
    private Long unitsSold;

    /** 销售额 */
    @Excel(name = "销售额", cellType = ColumnType.NUMERIC, scale = 2, headerMatch = { "Gross Sales", "Total Sales", "Net Sales", "Sales" })
    private BigDecimal revenue;

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

    public void setStoreDomain(String storeDomain)
    {
        this.storeDomain = storeDomain;
    }

    public String getStoreDomain()
    {
        return storeDomain;
    }

    public void setChannel(String channel)
    {
        this.channel = channel;
    }

    public String getChannel()
    {
        return channel;
    }

    public void setSessions(Long sessions)
    {
        this.sessions = sessions;
    }

    public Long getSessions()
    {
        return sessions;
    }

    public void setOrders(Long orders)
    {
        this.orders = orders;
    }

    public Long getOrders()
    {
        return orders;
    }

    public void setUnitsSold(Long unitsSold)
    {
        this.unitsSold = unitsSold;
    }

    public Long getUnitsSold()
    {
        return unitsSold;
    }

    public void setRevenue(BigDecimal revenue)
    {
        this.revenue = revenue;
    }

    public BigDecimal getRevenue()
    {
        return revenue;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("dailyId", getDailyId())
            .append("reportDate", getReportDate())
            .append("storeDomain", getStoreDomain())
            .append("channel", getChannel())
            .append("sessions", getSessions())
            .append("orders", getOrders())
            .append("unitsSold", getUnitsSold())
            .append("revenue", getRevenue())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
