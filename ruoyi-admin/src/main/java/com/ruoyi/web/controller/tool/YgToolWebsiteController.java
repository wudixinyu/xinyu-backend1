package com.ruoyi.web.controller.tool;

import java.util.List;
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
import com.ruoyi.system.domain.YgToolWebsite;
import com.ruoyi.system.service.IYgToolWebsiteService;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 工具使用网站Controller
 * 
 * @author ruoyi
 * @date 2026-10-16
 */
@RestController
@RequestMapping("/tool/website")
public class YgToolWebsiteController extends BaseController
{
    @Autowired
    private IYgToolWebsiteService ygToolWebsiteService;

    /**
     * 查询工具使用网站列表
     */
    @PreAuthorize("@ss.hasPermi('tool:website:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgToolWebsite ygToolWebsite)
    {
        startPage();
        List<YgToolWebsite> list = ygToolWebsiteService.selectYgToolWebsiteList(ygToolWebsite);
        return getDataTable(list);
    }

    /**
     * 获取工具使用网站详细信息
     */
    @PreAuthorize("@ss.hasPermi('tool:website:query')")
    @GetMapping(value = "/{toolId}")
    public AjaxResult getInfo(@PathVariable("toolId") Long toolId)
    {
        return success(ygToolWebsiteService.selectYgToolWebsiteByToolId(toolId));
    }

    /**
     * 新增工具使用网站
     */
    @PreAuthorize("@ss.hasPermi('tool:website:add')")
    @Log(title = "工具使用", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody YgToolWebsite ygToolWebsite)
    {
        ygToolWebsite.setCreateBy(getUsername());
        return toAjax(ygToolWebsiteService.insertYgToolWebsite(ygToolWebsite));
    }

    /**
     * 修改工具使用网站
     */
    @PreAuthorize("@ss.hasPermi('tool:website:edit')")
    @Log(title = "工具使用", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody YgToolWebsite ygToolWebsite)
    {
        ygToolWebsite.setUpdateBy(getUsername());
        return toAjax(ygToolWebsiteService.updateYgToolWebsite(ygToolWebsite));
    }

    /**
     * 删除工具使用网站
     */
    @PreAuthorize("@ss.hasPermi('tool:website:remove')")
    @Log(title = "工具使用", businessType = BusinessType.DELETE)
	@DeleteMapping("/{toolIds}")
    public AjaxResult remove(@PathVariable Long[] toolIds)
    {
        return toAjax(ygToolWebsiteService.deleteYgToolWebsiteByToolIds(toolIds));
    }
}
