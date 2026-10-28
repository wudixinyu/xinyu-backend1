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
import com.ruoyi.system.domain.YgAmazonAdsDaily;
import com.ruoyi.system.service.IYgAmazonAdsDailyService;

/**
 * 亚马逊广告活动日报Controller
 *
 * @author ruoyi
 * @date 2026-10-14
 */
@RestController
@RequestMapping("/data/amazonAds")
public class YgAmazonAdsDailyController extends BaseController
{
    @Autowired
    private IYgAmazonAdsDailyService ygAmazonAdsDailyService;

    /**
     * 查询广告日报列表
     */
    @PreAuthorize("@ss.hasPermi('data:amazonAds:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgAmazonAdsDaily ygAmazonAdsDaily)
    {
        startPage();
        List<YgAmazonAdsDaily> list = ygAmazonAdsDailyService.selectYgAmazonAdsDailyList(ygAmazonAdsDaily);
        return getDataTable(list);
    }

    /**
     * 统计总览（指标卡片）
     */
    @PreAuthorize("@ss.hasPermi('data:amazonAds:list')")
    @GetMapping("/stats/summary")
    public AjaxResult statsSummary(YgAmazonAdsDaily ygAmazonAdsDaily)
    {
        return success(ygAmazonAdsDailyService.selectAdsSummary(ygAmazonAdsDaily));
    }

    /**
     * 按日期趋势统计（折线图）
     */
    @PreAuthorize("@ss.hasPermi('data:amazonAds:list')")
    @GetMapping("/stats/trend")
    public AjaxResult statsTrend(YgAmazonAdsDaily ygAmazonAdsDaily)
    {
        return success(ygAmazonAdsDailyService.selectAdsTrend(ygAmazonAdsDaily));
    }

    /**
     * 按广告活动汇总统计（柱状对比/占比饼图）
     */
    @PreAuthorize("@ss.hasPermi('data:amazonAds:list')")
    @GetMapping("/stats/campaign")
    public AjaxResult statsCampaign(YgAmazonAdsDaily ygAmazonAdsDaily)
    {
        return success(ygAmazonAdsDailyService.selectAdsCampaignStats(ygAmazonAdsDaily));
    }

    /**
     * 导出广告日报列表
     */
    @PreAuthorize("@ss.hasPermi('data:amazonAds:export')")
    @Log(title = "亚马逊广告数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgAmazonAdsDaily ygAmazonAdsDaily)
    {
        List<YgAmazonAdsDaily> list = ygAmazonAdsDailyService.selectYgAmazonAdsDailyList(ygAmazonAdsDaily);
        ExcelUtil<YgAmazonAdsDaily> util = new ExcelUtil<YgAmazonAdsDaily>(YgAmazonAdsDaily.class);
        util.exportExcel(response, list, "亚马逊广告日报数据");
    }

    /**
     * 下载导入模板
     */
    @PreAuthorize("@ss.hasPermi('data:amazonAds:import')")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<YgAmazonAdsDaily> util = new ExcelUtil<YgAmazonAdsDaily>(YgAmazonAdsDaily.class);
        util.importTemplateExcel(response, "广告日报数据");
    }

    /**
     * 导入广告日报数据（支持 xlsx/xls 与亚马逊报表 CSV）
     */
    @PreAuthorize("@ss.hasPermi('data:amazonAds:import')")
    @Log(title = "亚马逊广告数据", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<YgAmazonAdsDaily> util = new ExcelUtil<YgAmazonAdsDaily>(YgAmazonAdsDaily.class);
        List<YgAmazonAdsDaily> adsList;
        String filename = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();
        if (filename.endsWith(".csv"))
        {
            adsList = util.importExcel(CsvToWorkbookUtil.toWorkbookStream(file.getInputStream()));
        }
        else
        {
            adsList = util.importExcel(file.getInputStream());
        }
        String operName = getUsername();
        String message = ygAmazonAdsDailyService.importAmazonAdsDaily(adsList, updateSupport, operName);
        return success(message);
    }

    /**
     * 获取广告日报详细信息
     */
    @PreAuthorize("@ss.hasPermi('data:amazonAds:query')")
    @GetMapping(value = "/{dailyId}")
    public AjaxResult getInfo(@PathVariable("dailyId") Long dailyId)
    {
        return success(ygAmazonAdsDailyService.selectYgAmazonAdsDailyByDailyId(dailyId));
    }

    /**
     * 新增广告日报
     */
    @PreAuthorize("@ss.hasPermi('data:amazonAds:add')")
    @Log(title = "亚马逊广告数据", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody YgAmazonAdsDaily ygAmazonAdsDaily)
    {
        ygAmazonAdsDaily.setCreateBy(getUsername());
        return toAjax(ygAmazonAdsDailyService.insertYgAmazonAdsDaily(ygAmazonAdsDaily));
    }

    /**
     * 修改广告日报
     */
    @PreAuthorize("@ss.hasPermi('data:amazonAds:edit')")
    @Log(title = "亚马逊广告数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody YgAmazonAdsDaily ygAmazonAdsDaily)
    {
        ygAmazonAdsDaily.setUpdateBy(getUsername());
        return toAjax(ygAmazonAdsDailyService.updateYgAmazonAdsDaily(ygAmazonAdsDaily));
    }

    /**
     * 删除广告日报
     */
    @PreAuthorize("@ss.hasPermi('data:amazonAds:remove')")
    @Log(title = "亚马逊广告数据", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dailyIds}")
    public AjaxResult remove(@PathVariable Long[] dailyIds)
    {
        return toAjax(ygAmazonAdsDailyService.deleteYgAmazonAdsDailyByDailyIds(dailyIds));
    }
}
