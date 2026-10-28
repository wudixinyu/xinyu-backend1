package com.ruoyi.web.controller.card;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.YgUserCollection;
import com.ruoyi.system.service.IYgUserCollectionService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 用户收藏管理
 * 
 * @author ruoyi
 * @date 2026-10-12
 */
@RestController
@RequestMapping("/card/collection")
public class YgUserCollectionController extends BaseController
{
    @Autowired
    private IYgUserCollectionService ygUserCollectionService;

    /**
     * 查询用户收藏列表
     */
    @PreAuthorize("@ss.hasPermi('card:collection:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgUserCollection ygUserCollection)
    {
        startPage();
        List<YgUserCollection> list = ygUserCollectionService.selectYgUserCollectionList(ygUserCollection);
        return getDataTable(list);
    }

    /**
     * 导出用户收藏列表
     */
    @PreAuthorize("@ss.hasPermi('card:collection:export')")
    @Log(title = "用户收藏", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgUserCollection ygUserCollection)
    {
        List<YgUserCollection> list = ygUserCollectionService.selectYgUserCollectionList(ygUserCollection);
        ExcelUtil<YgUserCollection> util = new ExcelUtil<YgUserCollection>(YgUserCollection.class);
        util.exportExcel(response, list, "用户收藏数据");
    }

    /**
     * 获取用户收藏详细信息
     */
    @PreAuthorize("@ss.hasPermi('card:collection:query')")
    @GetMapping(value = "/{collectId}")
    public AjaxResult getInfo(@PathVariable("collectId") Long collectId)
    {
        return success(ygUserCollectionService.selectYgUserCollectionByCollectId(collectId));
    }

    /**
     * 查询当前登录用户的收藏列表(移动端使用,免管理权限)
     */
    @GetMapping("/mine")
    public TableDataInfo mine(YgUserCollection ygUserCollection)
    {
        ygUserCollection.setUserId(getUserId());
        startPage();
        List<YgUserCollection> list = ygUserCollectionService.selectYgUserCollectionList(ygUserCollection);
        return getDataTable(list);
    }

    /**
     * 查询当前登录用户收藏的卡片密码集合(移动端使用,免管理权限)
     */
    @GetMapping("/mine/ids")
    public AjaxResult mineIds()
    {
        return success(ygUserCollectionService.selectCardIdsByUserId(getUserId()));
    }

    /**
     * 切换当前登录用户对某卡片的收藏状态(移动端使用,免管理权限)
     */
    @Log(title = "用户收藏", businessType = BusinessType.UPDATE)
    @PostMapping("/mine/toggle")
    public AjaxResult toggleMine(@RequestBody YgUserCollection ygUserCollection)
    {
        if (ygUserCollection.getCardId() == null)
        {
            return error("卡片密码不能为空");
        }
        boolean collected = ygUserCollectionService.toggleCollect(getUserId(), ygUserCollection.getCardId());
        return success(collected);
    }

    /**
     * 新增用户收藏(移动端使用,收藏人取当前登录用户)
     */
    @Log(title = "用户收藏", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody YgUserCollection ygUserCollection)
    {
        ygUserCollection.setUserId(getUserId());
        return toAjax(ygUserCollectionService.insertYgUserCollection(ygUserCollection));
    }

    /**
     * 删除当前登录用户的收藏(移动端使用,校验归属)
     */
    @Log(title = "用户收藏", businessType = BusinessType.DELETE)
    @DeleteMapping("/mine/{collectId}")
    public AjaxResult removeMine(@PathVariable Long collectId)
    {
        YgUserCollection collect = ygUserCollectionService.selectYgUserCollectionByCollectId(collectId);
        if (collect == null || !getUserId().equals(collect.getUserId()))
        {
            return error("收藏记录不存在或无权操作");
        }
        return toAjax(ygUserCollectionService.deleteYgUserCollectionByCollectId(collectId));
    }

    /**
     * 删除用户收藏
     */
    @PreAuthorize("@ss.hasPermi('card:collection:remove')")
    @Log(title = "用户收藏", businessType = BusinessType.DELETE)
    @DeleteMapping("/{collectIds}")
    public AjaxResult remove(@PathVariable Long[] collectIds)
    {
        return toAjax(ygUserCollectionService.deleteYgUserCollectionByCollectIds(collectIds));
    }
}
