package com.ruoyi.web.controller.card;

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
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.YgCardSet;
import com.ruoyi.system.service.IYgCardSetService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 卡组系列Controller
 * 
 * @author ruoyi
 * @date 2026-10-12
 */
@RestController
@RequestMapping("/card/set")
public class YgCardSetController extends BaseController
{
    @Autowired
    private IYgCardSetService ygCardSetService;

    /**
     * 查询卡组系列列表
     */
    @PreAuthorize("@ss.hasPermi('card:set:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgCardSet ygCardSet)
    {
        startPage();
        List<YgCardSet> list = ygCardSetService.selectYgCardSetList(ygCardSet);
        return getDataTable(list);
    }

    /**
     * 导出卡组系列列表
     */
    @PreAuthorize("@ss.hasPermi('card:set:export')")
    @Log(title = "卡组系列", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgCardSet ygCardSet)
    {
        List<YgCardSet> list = ygCardSetService.selectYgCardSetList(ygCardSet);
        ExcelUtil<YgCardSet> util = new ExcelUtil<YgCardSet>(YgCardSet.class);
        util.exportExcel(response, list, "卡组系列数据");
    }

    /**
     * 获取卡组系列详细信息
     */
    @PreAuthorize("@ss.hasPermi('card:set:query')")
    @GetMapping(value = "/{setId}")
    public AjaxResult getInfo(@PathVariable("setId") Long setId)
    {
        return success(ygCardSetService.selectYgCardSetBySetId(setId));
    }

    /**
     * 新增卡组系列
     */
    @PreAuthorize("@ss.hasPermi('card:set:add')")
    @Log(title = "卡组系列", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody YgCardSet ygCardSet)
    {
        return toAjax(ygCardSetService.insertYgCardSet(ygCardSet));
    }

    /**
     * 修改卡组系列
     */
    @PreAuthorize("@ss.hasPermi('card:set:edit')")
    @Log(title = "卡组系列", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody YgCardSet ygCardSet)
    {
        return toAjax(ygCardSetService.updateYgCardSet(ygCardSet));
    }

    /**
     * 删除卡组系列
     */
    @PreAuthorize("@ss.hasPermi('card:set:remove')")
    @Log(title = "卡组系列", businessType = BusinessType.DELETE)
	@DeleteMapping("/{setIds}")
    public AjaxResult remove(@PathVariable Long[] setIds)
    {
        return toAjax(ygCardSetService.deleteYgCardSetBySetIds(setIds));
    }
}
