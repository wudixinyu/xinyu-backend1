package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.ColumnType;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * WooCommerce商品对象 yg_shop_woo_product
 *
 * @author ruoyi
 * @date 2026-10-23
 */
public class YgShopWooProduct extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long productId;

    /** 店铺ID */
    @Excel(name = "店铺ID", cellType = ColumnType.NUMERIC)
    private Long storeId;

    /** 店铺名称（关联查询/导入时按名称匹配） */
    @Excel(name = "店铺名称")
    private String storeName;

    /** WooCommerce商品ID */
    private Long wooProductId;

    /** 商品名称 */
    @Excel(name = "商品名称")
    private String name;

    /** SKU */
    @Excel(name = "SKU")
    private String sku;

    /** 商品类型 */
    @Excel(name = "商品类型")
    private String type;

    /** 商品状态 */
    @Excel(name = "商品状态")
    private String status;

    /** 现价 */
    private BigDecimal price;

    /** 原价 */
    @Excel(name = "原价", cellType = ColumnType.NUMERIC, scale = 2)
    private BigDecimal regularPrice;

    /** 促销价 */
    @Excel(name = "促销价", cellType = ColumnType.NUMERIC, scale = 2)
    private BigDecimal salePrice;

    /** 库存数量 */
    @Excel(name = "库存数量", cellType = ColumnType.NUMERIC)
    private Integer stockQuantity;

    /** 库存状态 */
    @Excel(name = "库存状态")
    private String stockStatus;

    /** 商品分类 */
    @Excel(name = "商品分类")
    private String categories;

    /** 主图地址 */
    @Excel(name = "主图地址")
    private String imageUrl;

    /** 商品链接 */
    private String permalink;

    /** 商品描述（仅用于新增/编辑/导入，不落库） */
    @Excel(name = "商品描述")
    private String description;

    /** 创建时间(店铺) */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dateCreated;

    /** 更新时间(店铺) */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dateModified;

    public void setProductId(Long productId)
    {
        this.productId = productId;
    }

    public Long getProductId()
    {
        return productId;
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

    public void setWooProductId(Long wooProductId)
    {
        this.wooProductId = wooProductId;
    }

    public Long getWooProductId()
    {
        return wooProductId;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setSku(String sku)
    {
        this.sku = sku;
    }

    public String getSku()
    {
        return sku;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getType()
    {
        return type;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    public void setPrice(BigDecimal price)
    {
        this.price = price;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public void setRegularPrice(BigDecimal regularPrice)
    {
        this.regularPrice = regularPrice;
    }

    public BigDecimal getRegularPrice()
    {
        return regularPrice;
    }

    public void setSalePrice(BigDecimal salePrice)
    {
        this.salePrice = salePrice;
    }

    public BigDecimal getSalePrice()
    {
        return salePrice;
    }

    public void setStockQuantity(Integer stockQuantity)
    {
        this.stockQuantity = stockQuantity;
    }

    public Integer getStockQuantity()
    {
        return stockQuantity;
    }

    public void setStockStatus(String stockStatus)
    {
        this.stockStatus = stockStatus;
    }

    public String getStockStatus()
    {
        return stockStatus;
    }

    public void setCategories(String categories)
    {
        this.categories = categories;
    }

    public String getCategories()
    {
        return categories;
    }

    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl()
    {
        return imageUrl;
    }

    public void setPermalink(String permalink)
    {
        this.permalink = permalink;
    }

    public String getPermalink()
    {
        return permalink;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
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
            .append("productId", getProductId())
            .append("storeId", getStoreId())
            .append("wooProductId", getWooProductId())
            .append("name", getName())
            .append("sku", getSku())
            .append("type", getType())
            .append("status", getStatus())
            .append("price", getPrice())
            .append("regularPrice", getRegularPrice())
            .append("salePrice", getSalePrice())
            .append("stockQuantity", getStockQuantity())
            .append("stockStatus", getStockStatus())
            .append("categories", getCategories())
            .append("imageUrl", getImageUrl())
            .append("permalink", getPermalink())
            .append("description", getDescription())
            .append("dateCreated", getDateCreated())
            .append("dateModified", getDateModified())
            .toString();
    }
}
