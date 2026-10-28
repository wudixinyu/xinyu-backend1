package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.YgShopWooOrder;

/**
 * WooCommerce订单Mapper接口
 *
 * @author ruoyi
 * @date 2026-10-23
 */
public interface YgShopWooOrderMapper
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
     * 按店铺+远端订单ID新增或更新
     */
    public int upsertYgShopWooOrder(YgShopWooOrder ygShopWooOrder);

    /**
     * 批量删除订单
     */
    public int deleteYgShopWooOrderByOrderIds(Long[] orderIds);

    /**
     * 按店铺+远端订单ID删除（Webhook删除事件）
     */
    public int deleteByStoreAndWooOrderId(@Param("storeId") Long storeId, @Param("wooOrderId") Long wooOrderId);
}
