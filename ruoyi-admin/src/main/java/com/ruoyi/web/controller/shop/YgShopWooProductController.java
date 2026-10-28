package com.ruoyi.web.controller.shop;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.YgShopWooProduct;
import com.ruoyi.system.service.IYgShopWooProductService;

/**
 * WooCommerce商品Controller
 *
 * @author ruoyi
 * @date 2026-10-23
 */
@RestController
@RequestMapping("/shop/woo/product")
public class YgShopWooProductController extends BaseController
{
    @Autowired
    private IYgShopWooProductService ygShopWooProductService;

    /**
     * 查询商品列表
     */
    @PreAuthorize("@ss.hasPermi('shop:woo:product:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgShopWooProduct ygShopWooProduct)
    {
        startPage();
        List<YgShopWooProduct> list = ygShopWooProductService.selectYgShopWooProductList(ygShopWooProduct);
        return getDataTable(list);
    }

    /**
     * 获取商品详细信息
     */
    @PreAuthorize("@ss.hasPermi('shop:woo:product:query')")
    @GetMapping(value = "/{productId}")
    public AjaxResult getInfo(@PathVariable("productId") Long productId)
    {
        return success(ygShopWooProductService.selectYgShopWooProductByProductId(productId));
    }

    /**
     * 获取商品详情（含店铺端描述，编辑时使用）
     */
    @PreAuthorize("@ss.hasPermi('shop:woo:product:query')")
    @GetMapping(value = "/detail/{productId}")
    public AjaxResult detail(@PathVariable("productId") Long productId)
    {
        return success(ygShopWooProductService.selectWooProductDetail(productId));
    }

    /**
     * 新增商品（写回WooCommerce）
     */
    @PreAuthorize("@ss.hasPermi('shop:woo:product:add')")
    @Log(title = "WooCommerce商品", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody YgShopWooProduct ygShopWooProduct)
    {
        return success(ygShopWooProductService.createWooProduct(ygShopWooProduct));
    }

    /**
     * 修改商品（写回WooCommerce）
     */
    @PreAuthorize("@ss.hasPermi('shop:woo:product:edit')")
    @Log(title = "WooCommerce商品", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody YgShopWooProduct ygShopWooProduct)
    {
        return success(ygShopWooProductService.updateWooProduct(ygShopWooProduct));
    }

    /**
     * 下载商品导入模板
     */
    @PreAuthorize("@ss.hasPermi('shop:woo:product:import')")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<YgShopWooProduct> util = new ExcelUtil<YgShopWooProduct>(YgShopWooProduct.class);
        util.importTemplateExcel(response, "商品导入数据");
    }

    /**
     * 批量导入商品（按SKU新增或更新到WooCommerce）
     */
    @PreAuthorize("@ss.hasPermi('shop:woo:product:import')")
    @Log(title = "WooCommerce商品导入", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<YgShopWooProduct> util = new ExcelUtil<YgShopWooProduct>(YgShopWooProduct.class);
        List<YgShopWooProduct> list = util.importExcel(file.getInputStream());
        String message = ygShopWooProductService.importWooProduct(list, updateSupport);
        return success(message);
    }

    /**
     * 同步商品（不传店铺则同步全部启用店铺）
     */
    @PreAuthorize("@ss.hasPermi('shop:woo:product:sync')")
    @Log(title = "WooCommerce商品同步", businessType = BusinessType.OTHER)
    @PostMapping("/sync")
    public AjaxResult sync(Long storeId)
    {
        int count = ygShopWooProductService.syncWooProduct(storeId);
        return success("同步完成，共更新 " + count + " 条商品");
    }

    /**
     * 删除商品
     */
    @PreAuthorize("@ss.hasPermi('shop:woo:product:remove')")
    @Log(title = "WooCommerce商品", businessType = BusinessType.DELETE)
    @DeleteMapping("/{productIds}")
    public AjaxResult remove(@PathVariable Long[] productIds)
    {
        return toAjax(ygShopWooProductService.deleteYgShopWooProductByProductIds(productIds));
    }
}
