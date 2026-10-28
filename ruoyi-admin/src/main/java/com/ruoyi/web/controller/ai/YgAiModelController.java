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
import com.ruoyi.system.domain.YgAiModel;
import com.ruoyi.system.service.IYgAiModelService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * AI生成模型档案Controller
 * 
 * @author ruoyi
 * @date 2026-10-14
 */
@RestController
@RequestMapping("/ai/model")
public class YgAiModelController extends BaseController
{
    @Autowired
    private IYgAiModelService ygAiModelService;

    /**
     * 查询AI生成模型档案列表（不含 api_key）
     */
    @PreAuthorize("@ss.hasPermi('ai:model:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgAiModel ygAiModel)
    {
        startPage();
        List<YgAiModel> list = ygAiModelService.selectYgAiModelList(ygAiModel);
        return getDataTable(list);
    }

    /**
     * 导出AI生成模型档案列表
     */
    @PreAuthorize("@ss.hasPermi('ai:model:export')")
    @Log(title = "AI模型管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgAiModel ygAiModel)
    {
        List<YgAiModel> list = ygAiModelService.selectYgAiModelList(ygAiModel);
        ExcelUtil<YgAiModel> util = new ExcelUtil<YgAiModel>(YgAiModel.class);
        util.exportExcel(response, list, "AI模型数据");
    }

    /**
     * 获取AI生成模型档案详细信息（含 api_key，供编辑回填）
     */
    @PreAuthorize("@ss.hasPermi('ai:model:query')")
    @GetMapping(value = "/{modelId}")
    public AjaxResult getInfo(@PathVariable("modelId") Long modelId)
    {
        return success(ygAiModelService.selectYgAiModelByModelId(modelId));
    }

    /**
     * 新增AI生成模型档案
     */
    @PreAuthorize("@ss.hasPermi('ai:model:add')")
    @Log(title = "AI模型管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody YgAiModel ygAiModel)
    {
        ygAiModel.setCreateBy(getUsername());
        return toAjax(ygAiModelService.insertYgAiModel(ygAiModel));
    }

    /**
     * 修改AI生成模型档案
     */
    @PreAuthorize("@ss.hasPermi('ai:model:edit')")
    @Log(title = "AI模型管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody YgAiModel ygAiModel)
    {
        ygAiModel.setUpdateBy(getUsername());
        return toAjax(ygAiModelService.updateYgAiModel(ygAiModel));
    }

    /**
     * 删除AI生成模型档案
     */
    @PreAuthorize("@ss.hasPermi('ai:model:remove')")
    @Log(title = "AI模型管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{modelIds}")
    public AjaxResult remove(@PathVariable Long[] modelIds)
    {
        return toAjax(ygAiModelService.deleteYgAiModelByModelIds(modelIds));
    }
}
