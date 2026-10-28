package com.ruoyi.system.service;

import java.util.List;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.system.domain.YgShopWooOrder;

/**
 * WooCommerce订单Service接口
 *
 * @author ruoyi
 * @date 2026-10-23
 */
public interface IYgShopWooOrderService
{
    /**
     * 查询订单
     */
    public YgShopWooOrder selectYgShopWooOrderByOrderId(Long orderId);

    /**
     * 查询订单列表
     */
    public List<YgShopWooOrder> selectYgShopWooOrderList(YgShopWooOrder ygShopWooOrder);

    /**
     * 同步订单（storeId为空时同步全部启用店铺）
     *
     * @return 同步条数
     */
    public int syncWooOrder(Long storeId);

    /**
     * 批量删除订单
     */
    public int deleteYgShopWooOrderByOrderIds(Long[] orderIds);

    /**
     * 处理Webhook推送（创建/更新/删除）
     */
    public void handleWebhook(String topic, JSONObject payload, Long storeId);
}
