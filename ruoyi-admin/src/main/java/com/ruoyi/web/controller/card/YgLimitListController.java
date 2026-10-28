package com.ruoyi.web.controller.card;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.ruoyi.system.domain.YgLimitList;
import com.ruoyi.system.service.IYgLimitListService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 禁限卡管理
 * 
 * @author ruoyi
 * @date 2026-10-12
 */
@RestController
@RequestMapping("/card/limit")
public class YgLimitListController extends BaseController
{
    @Autowired
    private IYgLimitListService ygLimitListService;

    /**
     * 查询禁限卡列表
     */
    @PreAuthorize("@ss.hasPermi('card:limit:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgLimitList ygLimitList)
    {
        startPage();
        List<YgLimitList> list = ygLimitListService.selectYgLimitListList(ygLimitList);
        return getDataTable(list);
    }

    /**
     * 导出禁限卡列表
     */
    @PreAuthorize("@ss.hasPermi('card:limit:export')")
    @Log(title = "禁限卡", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgLimitList ygLimitList)
    {
        List<YgLimitList> list = ygLimitListService.selectYgLimitListList(ygLimitList);
        ExcelUtil<YgLimitList> util = new ExcelUtil<YgLimitList>(YgLimitList.class);
        util.exportExcel(response, list, "禁限卡数据");
    }

    /**
     * 获取禁限卡详细信息
     */
    @PreAuthorize("@ss.hasPermi('card:limit:query')")
    @GetMapping(value = "/{limitId}")
    public AjaxResult getInfo(@PathVariable("limitId") Long limitId)
    {
        return success(ygLimitListService.selectYgLimitListByLimitId(limitId));
    }

    /**
     * 新增禁限卡
     */
    @PreAuthorize("@ss.hasPermi('card:limit:add')")
    @Log(title = "禁限卡", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody YgLimitList ygLimitList)
    {
        return toAjax(ygLimitListService.insertYgLimitList(ygLimitList));
    }

    /**
     * 修改禁限卡
     */
    @PreAuthorize("@ss.hasPermi('card:limit:edit')")
    @Log(title = "禁限卡", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody YgLimitList ygLimitList)
    {
        return toAjax(ygLimitListService.updateYgLimitList(ygLimitList));
    }

    /**
     * 删除禁限卡
     */
    @PreAuthorize("@ss.hasPermi('card:limit:remove')")
    @Log(title = "禁限卡", businessType = BusinessType.DELETE)
    @DeleteMapping("/{limitIds}")
    public AjaxResult remove(@PathVariable Long[] limitIds)
    {
        return toAjax(ygLimitListService.deleteYgLimitListByLimitIds(limitIds));
    }
}
