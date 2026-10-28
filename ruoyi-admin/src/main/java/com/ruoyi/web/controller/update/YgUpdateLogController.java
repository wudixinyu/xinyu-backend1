package com.ruoyi.web.controller.update;

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
import com.ruoyi.system.domain.YgUpdateLog;
import com.ruoyi.system.service.IYgUpdateLogService;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 更新日志Controller
 * 
 * @author ruoyi
 * @date 2026-10-14
 */
@RestController
@RequestMapping("/update/log")
public class YgUpdateLogController extends BaseController
{
    @Autowired
    private IYgUpdateLogService ygUpdateLogService;

    /**
     * 查询更新日志列表
     */
    @PreAuthorize("@ss.hasPermi('update:log:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgUpdateLog ygUpdateLog)
    {
        startPage();
        List<YgUpdateLog> list = ygUpdateLogService.selectYgUpdateLogList(ygUpdateLog);
        return getDataTable(list);
    }

    /**
     * 获取更新日志详细信息
     */
    @PreAuthorize("@ss.hasPermi('update:log:query')")
    @GetMapping(value = "/{logId}")
    public AjaxResult getInfo(@PathVariable("logId") Long logId)
    {
        return success(ygUpdateLogService.selectYgUpdateLogByLogId(logId));
    }

    /**
     * 新增更新日志
     */
    @PreAuthorize("@ss.hasPermi('update:log:add')")
    @Log(title = "更新日志", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody YgUpdateLog ygUpdateLog)
    {
        ygUpdateLog.setCreateBy(getUsername());
        return toAjax(ygUpdateLogService.insertYgUpdateLog(ygUpdateLog));
    }

    /**
     * 修改更新日志
     */
    @PreAuthorize("@ss.hasPermi('update:log:edit')")
    @Log(title = "更新日志", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody YgUpdateLog ygUpdateLog)
    {
        ygUpdateLog.setUpdateBy(getUsername());
        return toAjax(ygUpdateLogService.updateYgUpdateLog(ygUpdateLog));
    }

    /**
     * 删除更新日志
     */
    @PreAuthorize("@ss.hasPermi('update:log:remove')")
    @Log(title = "更新日志", businessType = BusinessType.DELETE)
	@DeleteMapping("/{logIds}")
    public AjaxResult remove(@PathVariable Long[] logIds)
    {
        return toAjax(ygUpdateLogService.deleteYgUpdateLogByLogIds(logIds));
    }
}
