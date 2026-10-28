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
import com.ruoyi.system.domain.YgAiSkill;
import com.ruoyi.system.service.IYgAiSkillService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * AI技能Controller
 * 
 * @author ruoyi
 * @date 2026-10-14
 */
@RestController
@RequestMapping("/ai/skill")
public class YgAiSkillController extends BaseController
{
    @Autowired
    private IYgAiSkillService ygAiSkillService;

    /**
     * 查询技能列表（不含 content 大字段）
     */
    @PreAuthorize("@ss.hasPermi('ai:skill:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgAiSkill ygAiSkill)
    {
        startPage();
        List<YgAiSkill> list = ygAiSkillService.selectYgAiSkillList(ygAiSkill);
        return getDataTable(list);
    }

    /**
     * 导出技能列表
     */
    @PreAuthorize("@ss.hasPermi('ai:skill:export')")
    @Log(title = "技能管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgAiSkill ygAiSkill)
    {
        List<YgAiSkill> list = ygAiSkillService.selectYgAiSkillList(ygAiSkill);
        ExcelUtil<YgAiSkill> util = new ExcelUtil<YgAiSkill>(YgAiSkill.class);
        util.exportExcel(response, list, "技能数据");
    }

    /**
     * 获取技能详细信息（含 content）
     */
    @PreAuthorize("@ss.hasPermi('ai:skill:query')")
    @GetMapping(value = "/{skillId}")
    public AjaxResult getInfo(@PathVariable("skillId") Long skillId)
    {
        return success(ygAiSkillService.selectYgAiSkillBySkillId(skillId));
    }

    /**
     * 新增技能
     */
    @PreAuthorize("@ss.hasPermi('ai:skill:add')")
    @Log(title = "技能管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody YgAiSkill ygAiSkill)
    {
        if (!ygAiSkillService.checkSkillCodeUnique(ygAiSkill))
        {
            return error("新增技能'" + ygAiSkill.getSkillName() + "'失败，技能标识已存在");
        }
        ygAiSkill.setCreateBy(getUsername());
        return toAjax(ygAiSkillService.insertYgAiSkill(ygAiSkill));
    }

    /**
     * 修改技能
     */
    @PreAuthorize("@ss.hasPermi('ai:skill:edit')")
    @Log(title = "技能管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody YgAiSkill ygAiSkill)
    {
        if (!ygAiSkillService.checkSkillCodeUnique(ygAiSkill))
        {
            return error("修改技能'" + ygAiSkill.getSkillName() + "'失败，技能标识已存在");
        }
        ygAiSkill.setUpdateBy(getUsername());
        return toAjax(ygAiSkillService.updateYgAiSkill(ygAiSkill));
    }

    /**
     * 删除技能
     */
    @PreAuthorize("@ss.hasPermi('ai:skill:remove')")
    @Log(title = "技能管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{skillIds}")
    public AjaxResult remove(@PathVariable Long[] skillIds)
    {
        return toAjax(ygAiSkillService.deleteYgAiSkillBySkillIds(skillIds));
    }
}
