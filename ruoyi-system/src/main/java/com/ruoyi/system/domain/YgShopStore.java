package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 店铺设置对象 yg_shop_store
 *
 * @author ruoyi
 * @date 2026-10-23
 */
public class YgShopStore extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 店铺主键 */
    private Long storeId;

    /** 店铺名称 */
    private String storeName;

    /** 店铺地址 */
    private String storeUrl;

    /** WooCommerce Consumer Key */
    private String consumerKey;

    /** WooCommerce Consumer Secret */
    private String consumerSecret;

    /** 状态（0正常 1停用） */
    private String status;

    /** 显示排序 */
    private Integer sort;

    /** 最近同步时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastSyncTime;

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

    public void setStoreUrl(String storeUrl)
    {
        this.storeUrl = storeUrl;
    }

    public String getStoreUrl()
    {
        return storeUrl;
    }

    public void setConsumerKey(String consumerKey)
    {
        this.consumerKey = consumerKey;
    }

    public String getConsumerKey()
    {
        return consumerKey;
    }

    public void setConsumerSecret(String consumerSecret)
    {
        this.consumerSecret = consumerSecret;
    }

    public String getConsumerSecret()
    {
        return consumerSecret;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    public void setSort(Integer sort)
    {
        this.sort = sort;
    }

    public Integer getSort()
    {
        return sort;
    }

    public void setLastSyncTime(Date lastSyncTime)
    {
        this.lastSyncTime = lastSyncTime;
    }

    public Date getLastSyncTime()
    {
        return lastSyncTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("storeId", getStoreId())
            .append("storeName", getStoreName())
            .append("storeUrl", getStoreUrl())
            .append("consumerKey", getConsumerKey())
            .append("status", getStatus())
            .append("sort", getSort())
            .append("lastSyncTime", getLastSyncTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
