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
import com.ruoyi.system.domain.YgCardInfo;
import com.ruoyi.system.service.IYgCardInfoService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 卡片信息Controller
 * 
 * @author ruoyi
 * @date 2026-10-12
 */
@RestController
@RequestMapping("/card/info")
public class YgCardInfoController extends BaseController
{
    @Autowired
    private IYgCardInfoService ygCardInfoService;

    /**
     * 查询卡片信息列表
     */
    @PreAuthorize("@ss.hasPermi('card:info:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgCardInfo ygCardInfo)
    {
        startPage();
        List<YgCardInfo> list = ygCardInfoService.selectYgCardInfoList(ygCardInfo);
        return getDataTable(list);
    }

    /**
     * 导出卡片信息列表
     */
    @PreAuthorize("@ss.hasPermi('card:info:export')")
    @Log(title = "卡片信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgCardInfo ygCardInfo)
    {
        List<YgCardInfo> list = ygCardInfoService.selectYgCardInfoList(ygCardInfo);
        ExcelUtil<YgCardInfo> util = new ExcelUtil<YgCardInfo>(YgCardInfo.class);
        util.exportExcel(response, list, "卡片信息数据");
    }

    /**
     * 获取卡片信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('card:info:query')")
    @GetMapping(value = "/{cardId}")
    public AjaxResult getInfo(@PathVariable("cardId") Long cardId)
    {
        return success(ygCardInfoService.selectYgCardInfoByCardId(cardId));
    }

    /**
     * 新增卡片信息
     */
    @PreAuthorize("@ss.hasPermi('card:info:add')")
    @Log(title = "卡片信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody YgCardInfo ygCardInfo)
    {
        return toAjax(ygCardInfoService.insertYgCardInfo(ygCardInfo));
    }

    /**
     * 修改卡片信息
     */
    @PreAuthorize("@ss.hasPermi('card:info:edit')")
    @Log(title = "卡片信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody YgCardInfo ygCardInfo)
    {
        return toAjax(ygCardInfoService.updateYgCardInfo(ygCardInfo));
    }

    /**
     * 删除卡片信息
     */
    @PreAuthorize("@ss.hasPermi('card:info:remove')")
    @Log(title = "卡片信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{cardIds}")
    public AjaxResult remove(@PathVariable Long[] cardIds)
    {
        return toAjax(ygCardInfoService.deleteYgCardInfoByCardIds(cardIds));
    }
}
