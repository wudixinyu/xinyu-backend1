package com.ruoyi.web.controller.ai;

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
import com.ruoyi.system.domain.YgCanvas;
import com.ruoyi.system.service.IYgCanvasService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 画布Controller
 * 
 * @author ruoyi
 * @date 2026-10-14
 */
@RestController
@RequestMapping("/ai/canvas")
public class YgCanvasController extends BaseController
{
    @Autowired
    private IYgCanvasService ygCanvasService;

    /**
     * 查询画布列表（不含 content 大字段）
     */
    @PreAuthorize("@ss.hasPermi('ai:canvas:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgCanvas ygCanvas)
    {
        startPage();
        List<YgCanvas> list = ygCanvasService.selectYgCanvasList(ygCanvas);
        return getDataTable(list);
    }

    /**
     * 导出画布列表
     */
    @PreAuthorize("@ss.hasPermi('ai:canvas:export')")
    @Log(title = "画布", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgCanvas ygCanvas)
    {
        List<YgCanvas> list = ygCanvasService.selectYgCanvasList(ygCanvas);
        ExcelUtil<YgCanvas> util = new ExcelUtil<YgCanvas>(YgCanvas.class);
        util.exportExcel(response, list, "画布数据");
    }

    /**
     * 获取画布详细信息（含 content，供编辑器加载）
     */
    @PreAuthorize("@ss.hasPermi('ai:canvas:query')")
    @GetMapping(value = "/{canvasId}")
    public AjaxResult getInfo(@PathVariable("canvasId") Long canvasId)
    {
        return success(ygCanvasService.selectYgCanvasByCanvasId(canvasId));
    }

    /**
     * 新增画布（返回回传主键，前端据此跳转编辑器）
     */
    @PreAuthorize("@ss.hasPermi('ai:canvas:add')")
    @Log(title = "画布", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody YgCanvas ygCanvas)
    {
        ygCanvas.setCreateBy(getUsername());
        ygCanvasService.insertYgCanvas(ygCanvas);
        return success(ygCanvas);
    }

    /**
     * 修改画布（编辑器保存 content+缩略图；列表页改名）
     */
    @PreAuthorize("@ss.hasPermi('ai:canvas:edit')")
    @Log(title = "画布", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody YgCanvas ygCanvas)
    {
        ygCanvas.setUpdateBy(getUsername());
        return toAjax(ygCanvasService.updateYgCanvas(ygCanvas));
    }

    /**
     * 删除画布
     */
    @PreAuthorize("@ss.hasPermi('ai:canvas:remove')")
    @Log(title = "画布", businessType = BusinessType.DELETE)
	@DeleteMapping("/{canvasIds}")
    public AjaxResult remove(@PathVariable Long[] canvasIds)
    {
        return toAjax(ygCanvasService.deleteYgCanvasByCanvasIds(canvasIds));
    }
}
