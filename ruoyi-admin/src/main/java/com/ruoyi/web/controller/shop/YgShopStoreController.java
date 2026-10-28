package com.ruoyi.web.controller.shop;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.YgShopStore;
import com.ruoyi.system.service.IYgShopStoreService;

/**
 * 店铺设置Controller
 *
 * @author ruoyi
 * @date 2026-10-23
 */
@RestController
@RequestMapping("/shop/store")
public class YgShopStoreController extends BaseController
{
    @Autowired
    private IYgShopStoreService ygShopStoreService;

    /**
     * 查询店铺列表
     */
    @PreAuthorize("@ss.hasPermi('shop:store:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgShopStore ygShopStore)
    {
        startPage();
        List<YgShopStore> list = ygShopStoreService.selectYgShopStoreList(ygShopStore);
        return getDataTable(list);
    }

    /**
     * 启用店铺下拉选项（Woo订单/商品/文章页共用）
     */
    @GetMapping("/options")
    public AjaxResult options()
    {
        return success(ygShopStoreService.selectEnabledStoreOptions());
    }

    /**
     * 获取店铺详细信息
     */
    @PreAuthorize("@ss.hasPermi('shop:store:query')")
    @GetMapping(value = "/{storeId}")
    public AjaxResult getInfo(@PathVariable("storeId") Long storeId)
    {
        return success(ygShopStoreService.selectYgShopStoreByStoreId(storeId));
    }

    /**
     * 新增店铺
     */
    @PreAuthorize("@ss.hasPermi('shop:store:add')")
    @Log(title = "店铺设置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody YgShopStore ygShopStore)
    {
        ygShopStore.setCreateBy(getUsername());
        return toAjax(ygShopStoreService.insertYgShopStore(ygShopStore));
    }

    /**
     * 修改店铺
     */
    @PreAuthorize("@ss.hasPermi('shop:store:edit')")
    @Log(title = "店铺设置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody YgShopStore ygShopStore)
    {
        ygShopStore.setUpdateBy(getUsername());
        return toAjax(ygShopStoreService.updateYgShopStore(ygShopStore));
    }

    /**
     * 删除店铺
     */
    @PreAuthorize("@ss.hasPermi('shop:store:remove')")
    @Log(title = "店铺设置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{storeIds}")
    public AjaxResult remove(@PathVariable Long[] storeIds)
    {
        return toAjax(ygShopStoreService.deleteYgShopStoreByStoreIds(storeIds));
    }

    /**
     * 测试店铺连接
     */
    @PreAuthorize("@ss.hasPermi('shop:store:test')")
    @Log(title = "店铺连接测试", businessType = BusinessType.OTHER)
    @PostMapping("/test/{storeId}")
    public AjaxResult test(@PathVariable("storeId") Long storeId)
    {
        ygShopStoreService.testConnection(storeId);
        return success("连接成功");
    }
}
