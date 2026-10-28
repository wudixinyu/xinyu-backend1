package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 店铺Webhook对象 yg_shop_webhook
 *
 * @author ruoyi
 * @date 2026-10-23
 */
public class YgShopWebhook extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long webhookId;

    /** 店铺ID */
    private Long storeId;

    /** 店铺名称（关联查询） */
    private String storeName;

    /** WooCommerce Webhook ID */
    private Long wooWebhookId;

    /** Webhook名称 */
    private String name;

    /** 主题 */
    private String topic;

    /** 回调地址 */
    private String deliveryUrl;

    /** 密钥 */
    private String secret;

    /** 状态（0启用 1停用） */
    private String status;

    /** 最近推送时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastDeliveryTime;

    /** 最近推送结果（0成功 1失败） */
    private String lastDeliveryStatus;

    public void setWebhookId(Long webhookId)
    {
        this.webhookId = webhookId;
    }

    public Long getWebhookId()
    {
        return webhookId;
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

    public void setWooWebhookId(Long wooWebhookId)
    {
        this.wooWebhookId = wooWebhookId;
    }

    public Long getWooWebhookId()
    {
        return wooWebhookId;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setTopic(String topic)
    {
        this.topic = topic;
    }

    public String getTopic()
    {
        return topic;
    }

    public void setDeliveryUrl(String deliveryUrl)
    {
        this.deliveryUrl = deliveryUrl;
    }

    public String getDeliveryUrl()
    {
        return deliveryUrl;
    }

    public void setSecret(String secret)
    {
        this.secret = secret;
    }

    public String getSecret()
    {
        return secret;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    public void setLastDeliveryTime(Date lastDeliveryTime)
    {
        this.lastDeliveryTime = lastDeliveryTime;
    }

    public Date getLastDeliveryTime()
    {
        return lastDeliveryTime;
    }

    public void setLastDeliveryStatus(String lastDeliveryStatus)
    {
        this.lastDeliveryStatus = lastDeliveryStatus;
    }

    public String getLastDeliveryStatus()
    {
        return lastDeliveryStatus;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("webhookId", getWebhookId())
            .append("storeId", getStoreId())
            .append("wooWebhookId", getWooWebhookId())
            .append("name", getName())
            .append("topic", getTopic())
            .append("deliveryUrl", getDeliveryUrl())
            .append("status", getStatus())
            .append("lastDeliveryTime", getLastDeliveryTime())
            .append("lastDeliveryStatus", getLastDeliveryStatus())
            .toString();
    }
}
