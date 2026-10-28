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
import com.ruoyi.system.domain.YgAiPrompt;
import com.ruoyi.system.service.IYgAiPromptService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * AI提示词Controller
 * 
 * @author ruoyi
 * @date 2026-10-14
 */
@RestController
@RequestMapping("/ai/prompt")
public class YgAiPromptController extends BaseController
{
    @Autowired
    private IYgAiPromptService ygAiPromptService;

    /**
     * 查询AI提示词列表
     */
    @PreAuthorize("@ss.hasPermi('ai:prompt:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgAiPrompt ygAiPrompt)
    {
        startPage();
        List<YgAiPrompt> list = ygAiPromptService.selectYgAiPromptList(ygAiPrompt);
        return getDataTable(list);
    }

    /**
     * 导出AI提示词列表
     */
    @PreAuthorize("@ss.hasPermi('ai:prompt:export')")
    @Log(title = "AI提示词", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgAiPrompt ygAiPrompt)
    {
        List<YgAiPrompt> list = ygAiPromptService.selectYgAiPromptList(ygAiPrompt);
        ExcelUtil<YgAiPrompt> util = new ExcelUtil<YgAiPrompt>(YgAiPrompt.class);
        util.exportExcel(response, list, "AI提示词数据");
    }

    /**
     * 获取AI提示词详细信息
     */
    @PreAuthorize("@ss.hasPermi('ai:prompt:query')")
    @GetMapping(value = "/{promptId}")
    public AjaxResult getInfo(@PathVariable("promptId") Long promptId)
    {
        return success(ygAiPromptService.selectYgAiPromptByPromptId(promptId));
    }

    /**
     * 新增AI提示词
     */
    @PreAuthorize("@ss.hasPermi('ai:prompt:add')")
    @Log(title = "AI提示词", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody YgAiPrompt ygAiPrompt)
    {
        ygAiPrompt.setCreateBy(getUsername());
        return toAjax(ygAiPromptService.insertYgAiPrompt(ygAiPrompt));
    }

    /**
     * 修改AI提示词
     */
    @PreAuthorize("@ss.hasPermi('ai:prompt:edit')")
    @Log(title = "AI提示词", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody YgAiPrompt ygAiPrompt)
    {
        ygAiPrompt.setUpdateBy(getUsername());
        return toAjax(ygAiPromptService.updateYgAiPrompt(ygAiPrompt));
    }

    /**
     * 删除AI提示词
     */
    @PreAuthorize("@ss.hasPermi('ai:prompt:remove')")
    @Log(title = "AI提示词", businessType = BusinessType.DELETE)
	@DeleteMapping("/{promptIds}")
    public AjaxResult remove(@PathVariable Long[] promptIds)
    {
        return toAjax(ygAiPromptService.deleteYgAiPromptByPromptIds(promptIds));
    }
}
