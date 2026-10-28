package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.YgShopWooProduct;

/**
 * WooCommerce商品Mapper接口
 *
 * @author ruoyi
 * @date 2026-10-23
 */
public interface YgShopWooProductMapper
{
    /**
     * 查询商品
     */
    public YgShopWooProduct selectYgShopWooProductByProductId(Long productId);

    /**
     * 查询商品列表
     */
    public List<YgShopWooProduct> selectYgShopWooProductList(YgShopWooProduct ygShopWooProduct);

    /**
     * 按店铺+远端商品ID新增或更新
     */
    public int upsertYgShopWooProduct(YgShopWooProduct ygShopWooProduct);

    /**
     * 批量删除商品
     */
    public int deleteYgShopWooProductByProductIds(Long[] productIds);

    /**
     * 按店铺+远端商品ID删除（Webhook删除事件）
     */
    public int deleteByStoreAndWooProductId(@Param("storeId") Long storeId, @Param("wooProductId") Long wooProductId);
}
