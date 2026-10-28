package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.YgShopWebhook;

/**
 * 店铺WebhookService接口
 *
 * @author ruoyi
 * @date 2026-10-23
 */
public interface IYgShopWebhookService
{
    /**
     * 查询Webhook列表
     */
    public List<YgShopWebhook> selectYgShopWebhookList(YgShopWebhook ygShopWebhook);

    /**
     * 查询Webhook
     */
    public YgShopWebhook selectYgShopWebhookByWebhookId(Long webhookId);

    /**
     * 从店铺同步Webhook（storeId为空时同步全部启用店铺）
     *
     * @return 同步条数
     */
    public int syncYgShopWebhook(Long storeId);

    /**
     * 新增Webhook（同时创建到WooCommerce）
     */
    public YgShopWebhook createYgShopWebhook(YgShopWebhook ygShopWebhook);

    /**
     * 修改Webhook（同时更新到WooCommerce）
     */
    public YgShopWebhook updateYgShopWebhook(YgShopWebhook ygShopWebhook);

    /**
     * 修改启用状态（同时更新到WooCommerce）
     */
    public int changeStatus(Long webhookId, String status);

    /**
     * 批量删除Webhook（同时删除WooCommerce中的Webhook）
     */
    public int deleteYgShopWebhookByWebhookIds(Long[] webhookIds);

    /**
     * 接收Webhook推送：校验签名并按主题更新本地订单/商品
     */
    public void receiveWebhook(Long storeId, String wooWebhookId, String topic, String signature, String payload);
}
