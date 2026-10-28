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
import com.ruoyi.system.domain.YgShopifyStoreDaily;
import com.ruoyi.system.service.IYgShopifyStoreDailyService;

/**
 * Shopify店铺渠道日报Controller
 *
 * @author ruoyi
 * @date 2026-10-16
 */
@RestController
@RequestMapping("/data/shopify")
public class YgShopifyStoreDailyController extends BaseController
{
    @Autowired
    private IYgShopifyStoreDailyService ygShopifyStoreDailyService;

    /**
     * 查询店铺日报列表
     */
    @PreAuthorize("@ss.hasPermi('data:shopify:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgShopifyStoreDaily ygShopifyStoreDaily)
    {
        startPage();
        List<YgShopifyStoreDaily> list = ygShopifyStoreDailyService.selectYgShopifyStoreDailyList(ygShopifyStoreDaily);
        return getDataTable(list);
    }

    /**
     * 统计总览（指标卡片）
     */
    @PreAuthorize("@ss.hasPermi('data:shopify:list')")
    @GetMapping("/stats/summary")
    public AjaxResult statsSummary(YgShopifyStoreDaily ygShopifyStoreDaily)
    {
        return success(ygShopifyStoreDailyService.selectStoreSummary(ygShopifyStoreDaily));
    }

    /**
     * 按日期趋势统计（折线图）
     */
    @PreAuthorize("@ss.hasPermi('data:shopify:list')")
    @GetMapping("/stats/trend")
    public AjaxResult statsTrend(YgShopifyStoreDaily ygShopifyStoreDaily)
    {
        return success(ygShopifyStoreDailyService.selectStoreTrend(ygShopifyStoreDaily));
    }

    /**
     * 按销售渠道汇总统计（柱状对比/占比饼图）
     */
    @PreAuthorize("@ss.hasPermi('data:shopify:list')")
    @GetMapping("/stats/channel")
    public AjaxResult statsChannel(YgShopifyStoreDaily ygShopifyStoreDaily)
    {
        return success(ygShopifyStoreDailyService.selectStoreChannelStats(ygShopifyStoreDaily));
    }

    /**
     * 导出店铺日报列表
     */
    @PreAuthorize("@ss.hasPermi('data:shopify:export')")
    @Log(title = "Shopify店铺数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgShopifyStoreDaily ygShopifyStoreDaily)
    {
        List<YgShopifyStoreDaily> list = ygShopifyStoreDailyService.selectYgShopifyStoreDailyList(ygShopifyStoreDaily);
        ExcelUtil<YgShopifyStoreDaily> util = new ExcelUtil<YgShopifyStoreDaily>(YgShopifyStoreDaily.class);
        util.exportExcel(response, list, "Shopify店铺日报数据");
    }

    /**
     * 下载导入模板
     */
    @PreAuthorize("@ss.hasPermi('data:shopify:import')")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<YgShopifyStoreDaily> util = new ExcelUtil<YgShopifyStoreDaily>(YgShopifyStoreDaily.class);
        util.importTemplateExcel(response, "店铺日报数据");
    }

    /**
     * 导入店铺日报数据（支持 xlsx/xls 与 Shopify 报表 CSV）
     */
    @PreAuthorize("@ss.hasPermi('data:shopify:import')")
    @Log(title = "Shopify店铺数据", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<YgShopifyStoreDaily> util = new ExcelUtil<YgShopifyStoreDaily>(YgShopifyStoreDaily.class);
        List<YgShopifyStoreDaily> storeList;
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
        String message = ygShopifyStoreDailyService.importShopifyStoreDaily(storeList, updateSupport, operName);
        return success(message);
    }

    /**
     * 获取店铺日报详细信息
     */
    @PreAuthorize("@ss.hasPermi('data:shopify:query')")
    @GetMapping(value = "/{dailyId}")
    public AjaxResult getInfo(@PathVariable("dailyId") Long dailyId)
    {
        return success(ygShopifyStoreDailyService.selectYgShopifyStoreDailyByDailyId(dailyId));
    }

    /**
     * 新增店铺日报
     */
    @PreAuthorize("@ss.hasPermi('data:shopify:add')")
    @Log(title = "Shopify店铺数据", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody YgShopifyStoreDaily ygShopifyStoreDaily)
    {
        ygShopifyStoreDaily.setCreateBy(getUsername());
        return toAjax(ygShopifyStoreDailyService.insertYgShopifyStoreDaily(ygShopifyStoreDaily));
    }

    /**
     * 修改店铺日报
     */
    @PreAuthorize("@ss.hasPermi('data:shopify:edit')")
    @Log(title = "Shopify店铺数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody YgShopifyStoreDaily ygShopifyStoreDaily)
    {
        ygShopifyStoreDaily.setUpdateBy(getUsername());
        return toAjax(ygShopifyStoreDailyService.updateYgShopifyStoreDaily(ygShopifyStoreDaily));
    }

    /**
     * 删除店铺日报
     */
    @PreAuthorize("@ss.hasPermi('data:shopify:remove')")
    @Log(title = "Shopify店铺数据", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dailyIds}")
    public AjaxResult remove(@PathVariable Long[] dailyIds)
    {
        return toAjax(ygShopifyStoreDailyService.deleteYgShopifyStoreDailyByDailyIds(dailyIds));
    }
}
