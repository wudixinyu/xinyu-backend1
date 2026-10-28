package com.ruoyi.web.controller.shop;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.YgShopWooOrder;
import com.ruoyi.system.service.IYgShopWooOrderService;

/**
 * WooCommerce订单Controller
 *
 * @author ruoyi
 * @date 2026-10-23
 */
@RestController
@RequestMapping("/shop/woo/order")
public class YgShopWooOrderController extends BaseController
{
    @Autowired
    private IYgShopWooOrderService ygShopWooOrderService;

    /**
     * 查询订单列表
     */
    @PreAuthorize("@ss.hasPermi('shop:woo:order:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgShopWooOrder ygShopWooOrder)
    {
        startPage();
        List<YgShopWooOrder> list = ygShopWooOrderService.selectYgShopWooOrderList(ygShopWooOrder);
        return getDataTable(list);
    }

    /**
     * 获取订单详细信息
     */
    @PreAuthorize("@ss.hasPermi('shop:woo:order:query')")
    @GetMapping(value = "/{orderId}")
    public AjaxResult getInfo(@PathVariable("orderId") Long orderId)
    {
        return success(ygShopWooOrderService.selectYgShopWooOrderByOrderId(orderId));
    }

    /**
     * 同步订单（不传店铺则同步全部启用店铺）
     */
    @PreAuthorize("@ss.hasPermi('shop:woo:order:sync')")
    @Log(title = "WooCommerce订单同步", businessType = BusinessType.OTHER)
    @PostMapping("/sync")
    public AjaxResult sync(Long storeId)
    {
        int count = ygShopWooOrderService.syncWooOrder(storeId);
        return success("同步完成，共更新 " + count + " 条订单");
    }

    /**
     * 删除订单
     */
    @PreAuthorize("@ss.hasPermi('shop:woo:order:remove')")
    @Log(title = "WooCommerce订单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{orderIds}")
    public AjaxResult remove(@PathVariable Long[] orderIds)
    {
        return toAjax(ygShopWooOrderService.deleteYgShopWooOrderByOrderIds(orderIds));
    }
}
