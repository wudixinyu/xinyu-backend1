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
import com.ruoyi.system.domain.YgFacebookAdsDaily;
import com.ruoyi.system.service.IYgFacebookAdsDailyService;

/**
 * Facebook广告活动日报Controller
 *
 * @author ruoyi
 * @date 2026-10-14
 */
@RestController
@RequestMapping("/data/facebookAds")
public class YgFacebookAdsDailyController extends BaseController
{
    @Autowired
    private IYgFacebookAdsDailyService ygFacebookAdsDailyService;

    /**
     * 查询广告日报列表
     */
    @PreAuthorize("@ss.hasPermi('data:facebookAds:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgFacebookAdsDaily ygFacebookAdsDaily)
    {
        startPage();
        List<YgFacebookAdsDaily> list = ygFacebookAdsDailyService.selectYgFacebookAdsDailyList(ygFacebookAdsDaily);
        return getDataTable(list);
    }

    /**
     * 统计总览（指标卡片）
     */
    @PreAuthorize("@ss.hasPermi('data:facebookAds:list')")
    @GetMapping("/stats/summary")
    public AjaxResult statsSummary(YgFacebookAdsDaily ygFacebookAdsDaily)
    {
        return success(ygFacebookAdsDailyService.selectAdsSummary(ygFacebookAdsDaily));
    }

    /**
     * 按日期趋势统计（折线图）
     */
    @PreAuthorize("@ss.hasPermi('data:facebookAds:list')")
    @GetMapping("/stats/trend")
    public AjaxResult statsTrend(YgFacebookAdsDaily ygFacebookAdsDaily)
    {
        return success(ygFacebookAdsDailyService.selectAdsTrend(ygFacebookAdsDaily));
    }

    /**
     * 按广告活动汇总统计（柱状对比/占比饼图）
     */
    @PreAuthorize("@ss.hasPermi('data:facebookAds:list')")
    @GetMapping("/stats/campaign")
    public AjaxResult statsCampaign(YgFacebookAdsDaily ygFacebookAdsDaily)
    {
        return success(ygFacebookAdsDailyService.selectAdsCampaignStats(ygFacebookAdsDaily));
    }

    /**
     * 导出广告日报列表
     */
    @PreAuthorize("@ss.hasPermi('data:facebookAds:export')")
    @Log(title = "Facebook广告数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgFacebookAdsDaily ygFacebookAdsDaily)
    {
        List<YgFacebookAdsDaily> list = ygFacebookAdsDailyService.selectYgFacebookAdsDailyList(ygFacebookAdsDaily);
        ExcelUtil<YgFacebookAdsDaily> util = new ExcelUtil<YgFacebookAdsDaily>(YgFacebookAdsDaily.class);
        util.exportExcel(response, list, "Facebook广告日报数据");
    }

    /**
     * 下载导入模板
     */
    @PreAuthorize("@ss.hasPermi('data:facebookAds:import')")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<YgFacebookAdsDaily> util = new ExcelUtil<YgFacebookAdsDaily>(YgFacebookAdsDaily.class);
        util.importTemplateExcel(response, "广告日报数据");
    }

    /**
     * 导入广告日报数据（支持 xlsx/xls 与 Facebook 报表 CSV）
     */
    @PreAuthorize("@ss.hasPermi('data:facebookAds:import')")
    @Log(title = "Facebook广告数据", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<YgFacebookAdsDaily> util = new ExcelUtil<YgFacebookAdsDaily>(YgFacebookAdsDaily.class);
        List<YgFacebookAdsDaily> adsList;
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
        String message = ygFacebookAdsDailyService.importFacebookAdsDaily(adsList, updateSupport, operName);
        return success(message);
    }

    /**
     * 获取广告日报详细信息
     */
    @PreAuthorize("@ss.hasPermi('data:facebookAds:query')")
    @GetMapping(value = "/{dailyId}")
    public AjaxResult getInfo(@PathVariable("dailyId") Long dailyId)
    {
        return success(ygFacebookAdsDailyService.selectYgFacebookAdsDailyByDailyId(dailyId));
    }

    /**
     * 新增广告日报
     */
    @PreAuthorize("@ss.hasPermi('data:facebookAds:add')")
    @Log(title = "Facebook广告数据", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody YgFacebookAdsDaily ygFacebookAdsDaily)
    {
        ygFacebookAdsDaily.setCreateBy(getUsername());
        return toAjax(ygFacebookAdsDailyService.insertYgFacebookAdsDaily(ygFacebookAdsDaily));
    }

    /**
     * 修改广告日报
     */
    @PreAuthorize("@ss.hasPermi('data:facebookAds:edit')")
    @Log(title = "Facebook广告数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody YgFacebookAdsDaily ygFacebookAdsDaily)
    {
        ygFacebookAdsDaily.setUpdateBy(getUsername());
        return toAjax(ygFacebookAdsDailyService.updateYgFacebookAdsDaily(ygFacebookAdsDaily));
    }

    /**
     * 删除广告日报
     */
    @PreAuthorize("@ss.hasPermi('data:facebookAds:remove')")
    @Log(title = "Facebook广告数据", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dailyIds}")
    public AjaxResult remove(@PathVariable Long[] dailyIds)
    {
        return toAjax(ygFacebookAdsDailyService.deleteYgFacebookAdsDailyByDailyIds(dailyIds));
    }
}
