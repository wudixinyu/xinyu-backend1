package com.ruoyi.system.service;

import java.util.List;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.system.domain.YgShopWooProduct;

/**
 * WooCommerce商品Service接口
 *
 * @author ruoyi
 * @date 2026-10-23
 */
public interface IYgShopWooProductService
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
     * 同步商品（storeId为空时同步全部启用店铺）
     *
     * @return 同步条数
     */
    public int syncWooProduct(Long storeId);

    /**
     * 新增商品（写回WooCommerce并同步本地）
     */
    public YgShopWooProduct createWooProduct(YgShopWooProduct ygShopWooProduct);

    /**
     * 修改商品（写回WooCommerce并同步本地）
     */
    public YgShopWooProduct updateWooProduct(YgShopWooProduct ygShopWooProduct);

    /**
     * 查询商品详情（含店铺端描述等信息）
     */
    public YgShopWooProduct selectWooProductDetail(Long productId);

    /**
     * 批量导入商品（按SKU新增或更新到WooCommerce）
     *
     * @param list 商品列表
     * @param updateSupport 已存在SKU是否更新
     * @return 导入结果说明
     */
    public String importWooProduct(List<YgShopWooProduct> list, boolean updateSupport);

    /**
     * 批量删除商品
     */
    public int deleteYgShopWooProductByProductIds(Long[] productIds);

    /**
     * 处理Webhook推送（创建/更新/删除）
     */
    public void handleWebhook(String topic, JSONObject payload, Long storeId);
}
