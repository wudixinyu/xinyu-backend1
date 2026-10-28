package com.ruoyi.system.mapper;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.YgShopWebhook;

/**
 * 店铺WebhookMapper接口
 *
 * @author ruoyi
 * @date 2026-10-23
 */
public interface YgShopWebhookMapper
{
    /**
     * 查询Webhook
     */
    public YgShopWebhook selectYgShopWebhookByWebhookId(Long webhookId);

    /**
     * 查询Webhook列表
     */
    public List<YgShopWebhook> selectYgShopWebhookList(YgShopWebhook ygShopWebhook);

    /**
     * 按店铺+远端Webhook ID查询
     */
    public YgShopWebhook selectByStoreAndWooId(@Param("storeId") Long storeId, @Param("wooWebhookId") Long wooWebhookId);

    /**
     * 新增或更新Webhook
     */
    public int upsertYgShopWebhook(YgShopWebhook ygShopWebhook);

    /**
     * 更新启用状态
     */
    public int updateStatus(@Param("webhookId") Long webhookId, @Param("status") String status);

    /**
     * 更新最近推送结果
     */
    public int updateDeliveryResult(@Param("webhookId") Long webhookId, @Param("status") String status,
            @Param("deliveryTime") Date deliveryTime);

    /**
     * 批量删除Webhook
     */
    public int deleteYgShopWebhookByWebhookIds(Long[] webhookIds);
}
