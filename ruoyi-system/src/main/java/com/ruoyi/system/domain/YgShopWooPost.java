package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * WooCommerce文章对象 yg_shop_woo_post
 *
 * @author ruoyi
 * @date 2026-10-23
 */
public class YgShopWooPost extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long postId;

    /** 店铺ID */
    private Long storeId;

    /** 店铺名称（关联查询） */
    private String storeName;

    /** WordPress文章ID */
    private Long wooPostId;

    /** 文章标题 */
    private String title;

    /** 文章别名 */
    private String slug;

    /** 文章状态 */
    private String status;

    /** 摘要(纯文本) */
    private String excerpt;

    /** 正文(纯文本) */
    private String content;

    /** 文章链接 */
    private String link;

    /** 发布时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dateCreated;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dateModified;

    public void setPostId(Long postId)
    {
        this.postId = postId;
    }

    public Long getPostId()
    {
        return postId;
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

    public void setWooPostId(Long wooPostId)
    {
        this.wooPostId = wooPostId;
    }

    public Long getWooPostId()
    {
        return wooPostId;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getTitle()
    {
        return title;
    }

    public void setSlug(String slug)
    {
        this.slug = slug;
    }

    public String getSlug()
    {
        return slug;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    public void setExcerpt(String excerpt)
    {
        this.excerpt = excerpt;
    }

    public String getExcerpt()
    {
        return excerpt;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getContent()
    {
        return content;
    }

    public void setLink(String link)
    {
        this.link = link;
    }

    public String getLink()
    {
        return link;
    }

    public void setDateCreated(Date dateCreated)
    {
        this.dateCreated = dateCreated;
    }

    public Date getDateCreated()
    {
        return dateCreated;
    }

    public void setDateModified(Date dateModified)
    {
        this.dateModified = dateModified;
    }

    public Date getDateModified()
    {
        return dateModified;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("postId", getPostId())
            .append("storeId", getStoreId())
            .append("wooPostId", getWooPostId())
            .append("title", getTitle())
            .append("slug", getSlug())
            .append("status", getStatus())
            .append("excerpt", getExcerpt())
            .append("link", getLink())
            .append("dateCreated", getDateCreated())
            .append("dateModified", getDateModified())
            .toString();
    }
}
