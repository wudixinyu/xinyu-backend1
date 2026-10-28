package com.ruoyi.web.controller.card;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.service.IYgCardSyncService;

/**
 * ygocdb数据同步
 * 
 * @author ruoyi
 * @date 2026-10-12
 */
@RestController
@RequestMapping("/card/sync")
public class YgSyncController extends BaseController
{
    @Autowired
    private IYgCardSyncService ygCardSyncService;

    /**
     * 全量同步卡片数据
     */
    @PreAuthorize("@ss.hasPermi('card:info:sync')")
    @Log(title = "ygocdb数据同步", businessType = BusinessType.OTHER)
    @PostMapping("/cards")
    public AjaxResult syncCards(@RequestParam(defaultValue = "false") boolean force)
    {
        return success(ygCardSyncService.syncAllCards(force));
    }

    /**
     * 按需补录单张卡片
     */
    @PreAuthorize("@ss.hasPermi('card:info:sync')")
    @Log(title = "ygocdb数据同步", businessType = BusinessType.OTHER)
    @PostMapping("/card/{cardId}")
    public AjaxResult syncCard(@PathVariable("cardId") long cardId)
    {
        return success(ygCardSyncService.syncSingleCard(cardId));
    }

    /**
     * 收集卡组系列
     */
    @PreAuthorize("@ss.hasPermi('card:set:sync')")
    @Log(title = "ygocdb数据同步", businessType = BusinessType.OTHER)
    @PostMapping("/sets")
    public AjaxResult syncSets()
    {
        return success(ygCardSyncService.syncCardSets());
    }

    /**
     * 同步禁限卡表
     */
    @PreAuthorize("@ss.hasPermi('card:limit:sync')")
    @Log(title = "ygocdb数据同步", businessType = BusinessType.OTHER)
    @PostMapping("/limits")
    public AjaxResult syncLimits()
    {
        return success(ygCardSyncService.syncLimitLists());
    }
}
