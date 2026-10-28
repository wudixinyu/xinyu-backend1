package com.ruoyi.web.controller.data;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.poi.CsvToWorkbookUtil;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.YgWooCommerceStoreDaily;
import com.ruoyi.system.service.IYgWooCommerceStoreDailyService;

/**
 * WooCommerce店铺渠道日报Controller
 *
 * @author ruoyi
 * @date 2026-10-16
 */
@RestController
@RequestMapping("/data/woocommerce")
public class YgWooCommerceStoreDailyController extends BaseController
{
    @Autowired
    private IYgWooCommerceStoreDailyService ygWooCommerceStoreDailyService;

    /**
     * 查询店铺日报列表
     */
    @PreAuthorize("@ss.hasPermi('data:woocommerce:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgWooCommerceStoreDaily ygWooCommerceStoreDaily)
    {
        startPage();
        List<YgWooCommerceStoreDaily> list = ygWooCommerceStoreDailyService.selectYgWooCommerceStoreDailyList(ygWooCommerceStoreDaily);
        return getDataTable(list);
    }

    /**
     * 统计总览（指标卡片）
     */
    @PreAuthorize("@ss.hasPermi('data:woocommerce:list')")
    @GetMapping("/stats/summary")
    public AjaxResult statsSummary(YgWooCommerceStoreDaily ygWooCommerceStoreDaily)
    {
        return success(ygWooCommerceStoreDailyService.selectStoreSummary(ygWooCommerceStoreDaily));
    }

    /**
     * 按日期趋势统计（折线图）
     */
    @PreAuthorize("@ss.hasPermi('data:woocommerce:list')")
    @GetMapping("/stats/trend")
    public AjaxResult statsTrend(YgWooCommerceStoreDaily ygWooCommerceStoreDaily)
    {
        return success(ygWooCommerceStoreDailyService.selectStoreTrend(ygWooCommerceStoreDaily));
    }

    /**
     * 按销售渠道汇总统计（柱状对比/占比饼图）
     */
    @PreAuthorize("@ss.hasPermi('data:woocommerce:list')")
    @GetMapping("/stats/channel")
    public AjaxResult statsChannel(YgWooCommerceStoreDaily ygWooCommerceStoreDaily)
    {
        return success(ygWooCommerceStoreDailyService.selectStoreChannelStats(ygWooCommerceStoreDaily));
    }

    /**
     * 导出店铺日报列表
     */
    @PreAuthorize("@ss.hasPermi('data:woocommerce:export')")
    @Log(title = "WooCommerce店铺数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgWooCommerceStoreDaily ygWooCommerceStoreDaily)
    {
        List<YgWooCommerceStoreDaily> list = ygWooCommerceStoreDailyService.selectYgWooCommerceStoreDailyList(ygWooCommerceStoreDaily);
        ExcelUtil<YgWooCommerceStoreDaily> util = new ExcelUtil<YgWooCommerceStoreDaily>(YgWooCommerceStoreDaily.class);
        util.exportExcel(response, list, "WooCommerce店铺日报数据");
    }

    /**
     * 下载导入模板
     */
    @PreAuthorize("@ss.hasPermi('data:woocommerce:import')")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<YgWooCommerceStoreDaily> util = new ExcelUtil<YgWooCommerceStoreDaily>(YgWooCommerceStoreDaily.class);
        util.importTemplateExcel(response, "店铺日报数据");
    }

    /**
     * 导入店铺日报数据（支持 xlsx/xls 与 WooCommerce 报表 CSV）
     */
    @PreAuthorize("@ss.hasPermi('data:woocommerce:import')")
    @Log(title = "WooCommerce店铺数据", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<YgWooCommerceStoreDaily> util = new ExcelUtil<YgWooCommerceStoreDaily>(YgWooCommerceStoreDaily.class);
        List<YgWooCommerceStoreDaily> storeList;
        String filename = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();
        if (filename.endsWith(".csv"))
        {
            storeList = util.importExcel(CsvToWorkbookUtil.toWorkbookStream(file.getInputStream()));
        }
        else
        {
            storeList = util.importExcel(file.getInputStream());
        }
        String operName = getUsername();
        String message = ygWooCommerceStoreDailyService.importWooCommerceStoreDaily(storeList, updateSupport, operName);
        return success(message);
    }

    /**
     * 获取店铺日报详细信息
     */
    @PreAuthorize("@ss.hasPermi('data:woocommerce:query')")
    @GetMapping(value = "/{dailyId}")
    public AjaxResult getInfo(@PathVariable("dailyId") Long dailyId)
    {
        return success(ygWooCommerceStoreDailyService.selectYgWooCommerceStoreDailyByDailyId(dailyId));
    }

    /**
     * 新增店铺日报
     */
    @PreAuthorize("@ss.hasPermi('data:woocommerce:add')")
    @Log(title = "WooCommerce店铺数据", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody YgWooCommerceStoreDaily ygWooCommerceStoreDaily)
    {
        ygWooCommerceStoreDaily.setCreateBy(getUsername());
        return toAjax(ygWooCommerceStoreDailyService.insertYgWooCommerceStoreDaily(ygWooCommerceStoreDaily));
    }

    /**
     * 修改店铺日报
     */
    @PreAuthorize("@ss.hasPermi('data:woocommerce:edit')")
    @Log(title = "WooCommerce店铺数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody YgWooCommerceStoreDaily ygWooCommerceStoreDaily)
    {
        ygWooCommerceStoreDaily.setUpdateBy(getUsername());
        return toAjax(ygWooCommerceStoreDailyService.updateYgWooCommerceStoreDaily(ygWooCommerceStoreDaily));
    }

    /**
     * 删除店铺日报
     */
    @PreAuthorize("@ss.hasPermi('data:woocommerce:remove')")
    @Log(title = "WooCommerce店铺数据", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dailyIds}")
    public AjaxResult remove(@PathVariable Long[] dailyIds)
    {
        return toAjax(ygWooCommerceStoreDailyService.deleteYgWooCommerceStoreDailyByDailyIds(dailyIds));
    }
}
