package com.ruoyi.quartz.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.system.service.IYgShopWooOrderService;
import com.ruoyi.system.service.IYgShopWooPostService;
import com.ruoyi.system.service.IYgShopWooProductService;

/**
 * 店铺数据同步定时任务
 *
 * @author ruoyi
 * @date 2026-10-23
 */
@Component("ygShopSyncTask")
public class YgShopSyncTask
{
    private static final Logger log = LoggerFactory.getLogger(YgShopSyncTask.class);

    @Autowired
    private IYgShopWooOrderService ygShopWooOrderService;

    @Autowired
    private IYgShopWooProductService ygShopWooProductService;

    @Autowired
    private IYgShopWooPostService ygShopWooPostService;

    /**
     * 同步全部启用店铺的订单、商品、文章（单类失败不影响其他类型，全部完成后汇总异常）
     */
    public void syncAll()
    {
        StringBuilder errors = new StringBuilder();
        errors.append(syncOrders());
        errors.append(syncProducts());
        errors.append(syncPosts());
        if (errors.length() > 0)
        {
            throw new RuntimeException(errors.toString());
        }
    }

    /**
     * 同步订单
     */
    private String syncOrders()
    {
        try
        {
            int count = ygShopWooOrderService.syncWooOrder(null);
            log.info("店铺定时同步-订单完成，共更新 {} 条", count);
            return "";
        }
        catch (Exception e)
        {
            log.error("店铺定时同步-订单失败：{}", e.getMessage(), e);
            return "订单同步失败：" + e.getMessage() + "；";
        }
    }

    /**
     * 同步商品
     */
    private String syncProducts()
    {
        try
        {
            int count = ygShopWooProductService.syncWooProduct(null);
            log.info("店铺定时同步-商品完成，共更新 {} 条", count);
            return "";
        }
        catch (Exception e)
        {
            log.error("店铺定时同步-商品失败：{}", e.getMessage(), e);
            return "商品同步失败：" + e.getMessage() + "；";
        }
    }

    /**
     * 同步文章
     */
    private String syncPosts()
    {
        try
        {
            int count = ygShopWooPostService.syncWooPost(null);
            log.info("店铺定时同步-文章完成，共更新 {} 篇", count);
            return "";
        }
        catch (Exception e)
        {
            log.error("店铺定时同步-文章失败：{}", e.getMessage(), e);
            return "文章同步失败：" + e.getMessage() + "；";
        }
    }
}
