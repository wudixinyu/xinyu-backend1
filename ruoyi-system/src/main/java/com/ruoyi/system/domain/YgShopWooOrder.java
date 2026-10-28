package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * WooCommerce订单对象 yg_shop_woo_order
 *
 * @author ruoyi
 * @date 2026-10-23
 */
public class YgShopWooOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long orderId;

    /** 店铺ID */
    private Long storeId;

    /** 店铺名称（关联查询） */
    private String storeName;

    /** WooCommerce订单ID */
    private Long wooOrderId;

    /** 订单号 */
    private String orderNumber;

    /** 订单状态 */
    private String status;

    /** 币种 */
    private String currency;

    /** 订单总额 */
    private BigDecimal total;

    /** 客户姓名 */
    private String customerName;

    /** 客户邮箱 */
    private String customerEmail;

    /** 支付方式 */
    private String paymentMethod;

    /** 商品件数 */
    private Integer itemCount;

    /** 商品摘要 */
    private String itemsSummary;

    /** 下单时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dateCreated;

    /** 支付时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date datePaid;

    public void setOrderId(Long orderId)
    {
        this.orderId = orderId;
    }

    public Long getOrderId()
    {
        return orderId;
    }

    public void setStoreId(Long storeId)
    {
        this.storeId = storeId;
    }

    public Long getStoreId()
    {
        return storeId;
    }

    public void setStoreName(String storeName)
    {
        this.storeName = storeName;
    }

    public String getStoreName()
    {
        return storeName;
    }

    public void setWooOrderId(Long wooOrderId)
    {
        this.wooOrderId = wooOrderId;
    }

    public Long getWooOrderId()
    {
        return wooOrderId;
    }

    public void setOrderNumber(String orderNumber)
    {
        this.orderNumber = orderNumber;
    }

    public String getOrderNumber()
    {
        return orderNumber;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setTotal(BigDecimal total)
    {
        this.total = total;
    }

    public BigDecimal getTotal()
    {
        return total;
    }

    public void setCustomerName(String customerName)
    {
        this.customerName = customerName;
    }

    public String getCustomerName()
    {
        return customerName;
    }

    public void setCustomerEmail(String customerEmail)
    {
        this.customerEmail = customerEmail;
    }

    public String getCustomerEmail()
    {
        return customerEmail;
    }

    public void setPaymentMethod(String paymentMethod)
    {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentMethod()
    {
        return paymentMethod;
    }

    public void setItemCount(Integer itemCount)
    {
        this.itemCount = itemCount;
    }

    public Integer getItemCount()
    {
        return itemCount;
    }

    public void setItemsSummary(String itemsSummary)
    {
        this.itemsSummary = itemsSummary;
    }

    public String getItemsSummary()
    {
        return itemsSummary;
    }

    public void setDateCreated(Date dateCreated)
    {
        this.dateCreated = dateCreated;
    }

    public Date getDateCreated()
    {
        return dateCreated;
    }

    public void setDatePaid(Date datePaid)
    {
        this.datePaid = datePaid;
    }

    public Date getDatePaid()
    {
        return datePaid;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("orderId", getOrderId())
            .append("storeId", getStoreId())
            .append("wooOrderId", getWooOrderId())
            .append("orderNumber", getOrderNumber())
            .append("status", getStatus())
            .append("currency", getCurrency())
            .append("total", getTotal())
            .append("customerName", getCustomerName())
            .append("customerEmail", getCustomerEmail())
            .append("paymentMethod", getPaymentMethod())
            .append("itemCount", getItemCount())
            .append("itemsSummary", getItemsSummary())
            .append("dateCreated", getDateCreated())
            .append("datePaid", getDatePaid())
            .toString();
    }
}
