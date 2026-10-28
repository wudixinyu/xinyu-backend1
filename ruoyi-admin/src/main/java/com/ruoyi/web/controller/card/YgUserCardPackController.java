package com.ruoyi.web.controller.card;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.YgUserCardPack;
import com.ruoyi.system.domain.YgUserCardPackItem;
import com.ruoyi.system.service.IYgUserCardPackService;
import com.ruoyi.system.service.IYgUserCardPackItemService;

/**
 * 用户卡包管理
 * 
 * @author ruoyi
 * @date 2026-10-12
 */
@RestController
@RequestMapping("/card/pack")
public class YgUserCardPackController extends BaseController
{
    @Autowired
    private IYgUserCardPackService ygUserCardPackService;

    @Autowired
    private IYgUserCardPackItemService ygUserCardPackItemService;

    /**
     * 查询用户卡包列表
     */
    @PreAuthorize("@ss.hasPermi('card:pack:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgUserCardPack ygUserCardPack)
    {
        startPage();
        List<YgUserCardPack> list = ygUserCardPackService.selectYgUserCardPackList(ygUserCardPack);
        return getDataTable(list);
    }

    /**
     * 获取用户卡包详细信息
     */
    @PreAuthorize("@ss.hasPermi('card:pack:query')")
    @GetMapping(value = "/{packId}")
    public AjaxResult getInfo(@PathVariable("packId") Long packId)
    {
        return success(ygUserCardPackService.selectYgUserCardPackByPackId(packId));
    }

    /**
     * 新增用户卡包
     */
    @PreAuthorize("@ss.hasPermi('card:pack:add')")
    @Log(title = "用户卡包", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody YgUserCardPack ygUserCardPack)
    {
        // 用户ID由后台自动获取当前登录用户，无需前端填写
        ygUserCardPack.setUserId(getUserId());
        return toAjax(ygUserCardPackService.insertYgUserCardPack(ygUserCardPack));
    }

    /**
     * 修改用户卡包
     */
    @PreAuthorize("@ss.hasPermi('card:pack:edit')")
    @Log(title = "用户卡包", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody YgUserCardPack ygUserCardPack)
    {
        return toAjax(ygUserCardPackService.updateYgUserCardPack(ygUserCardPack));
    }

    /**
     * 删除用户卡包(级联删除卡包内卡片)
     */
    @PreAuthorize("@ss.hasPermi('card:pack:remove')")
    @Log(title = "用户卡包", businessType = BusinessType.DELETE)
    @DeleteMapping("/{packIds}")
    public AjaxResult remove(@PathVariable Long[] packIds)
    {
        return toAjax(ygUserCardPackService.deleteYgUserCardPackByPackIds(packIds));
    }

    /**
     * 查询卡包内卡片明细列表
     */
    @PreAuthorize("@ss.hasPermi('card:pack:query')")
    @GetMapping("/item/list")
    public TableDataInfo itemList(YgUserCardPackItem ygUserCardPackItem)
    {
        startPage();
        List<YgUserCardPackItem> list = ygUserCardPackItemService.selectYgUserCardPackItemList(ygUserCardPackItem);
        return getDataTable(list);
    }

    /**
     * 向卡包添加卡片
     */
    @PreAuthorize("@ss.hasPermi('card:pack:add')")
    @Log(title = "用户卡包", businessType = BusinessType.INSERT)
    @PostMapping("/item")
    public AjaxResult addItem(@RequestBody YgUserCardPackItem ygUserCardPackItem)
    {
        if (ygUserCardPackItem.getPackId() == null || ygUserCardPackItem.getCardId() == null)
        {
            return error("卡包与卡片不能为空");
        }
        return toAjax(ygUserCardPackItemService.addCardToPack(ygUserCardPackItem));
    }

    /**
     * 修改卡包内卡片数量
     */
    @PreAuthorize("@ss.hasPermi('card:pack:edit')")
    @Log(title = "用户卡包", businessType = BusinessType.UPDATE)
    @PutMapping("/item")
    public AjaxResult editItem(@RequestBody YgUserCardPackItem ygUserCardPackItem)
    {
        return toAjax(ygUserCardPackItemService.updateYgUserCardPackItem(ygUserCardPackItem));
    }

    /**
     * 删除卡包内卡片
     */
    @PreAuthorize("@ss.hasPermi('card:pack:remove')")
    @Log(title = "用户卡包", businessType = BusinessType.DELETE)
    @DeleteMapping("/item/{itemIds}")
    public AjaxResult removeItem(@PathVariable Long[] itemIds)
    {
        return toAjax(ygUserCardPackItemService.deleteYgUserCardPackItemByItemIds(itemIds));
    }

    /**
     * 查询当前登录用户的卡包列表(移动端使用,免管理权限)
     */
    @GetMapping("/mine")
    public TableDataInfo mine(YgUserCardPack ygUserCardPack)
    {
        ygUserCardPack.setUserId(getUserId());
        startPage();
        List<YgUserCardPack> list = ygUserCardPackService.selectYgUserCardPackList(ygUserCardPack);
        return getDataTable(list);
    }

    /**
     * 获取当前登录用户的卡包详情(移动端使用,校验归属)
     */
    @GetMapping("/mine/{packId}")
    public AjaxResult mineInfo(@PathVariable("packId") Long packId)
    {
        YgUserCardPack pack = ygUserCardPackService.selectYgUserCardPackByPackId(packId);
        if (pack == null || !getUserId().equals(pack.getUserId()))
        {
            return error("卡包不存在或无权访问");
        }
        return success(pack);
    }

    /**
     * 新增当前登录用户的卡包(移动端使用,卡包归属人取当前登录用户)
     */
    @Log(title = "用户卡包", businessType = BusinessType.INSERT)
    @PostMapping("/mine")
    public AjaxResult addMine(@RequestBody YgUserCardPack ygUserCardPack)
    {
        ygUserCardPack.setPackId(null);
        ygUserCardPack.setUserId(getUserId());
        return toAjax(ygUserCardPackService.insertYgUserCardPack(ygUserCardPack));
    }

    /**
     * 修改当前登录用户的卡包(移动端使用,校验归属)
     */
    @Log(title = "用户卡包", businessType = BusinessType.UPDATE)
    @PutMapping("/mine")
    public AjaxResult editMine(@RequestBody YgUserCardPack ygUserCardPack)
    {
        YgUserCardPack pack = ygUserCardPackService.selectYgUserCardPackByPackId(ygUserCardPack.getPackId());
        if (pack == null || !getUserId().equals(pack.getUserId()))
        {
            return error("卡包不存在或无权操作");
        }
        // 不允许变更归属人
        ygUserCardPack.setUserId(pack.getUserId());
        return toAjax(ygUserCardPackService.updateYgUserCardPack(ygUserCardPack));
    }

    /**
     * 删除当前登录用户的卡包(移动端使用,校验归属)
     */
    @Log(title = "用户卡包", businessType = BusinessType.DELETE)
    @DeleteMapping("/mine/{packIds}")
    public AjaxResult removeMine(@PathVariable Long[] packIds)
    {
        for (Long packId : packIds)
        {
            YgUserCardPack pack = ygUserCardPackService.selectYgUserCardPackByPackId(packId);
            if (pack == null || !getUserId().equals(pack.getUserId()))
            {
                return error("卡包不存在或无权操作");
            }
        }
        return toAjax(ygUserCardPackService.deleteYgUserCardPackByPackIds(packIds));
    }

    /**
     * 查询当前登录用户卡包内的卡片明细(移动端使用,校验归属)
     */
    @GetMapping("/mine/item/list")
    public TableDataInfo mineItemList(YgUserCardPackItem ygUserCardPackItem)
    {
        if (!checkMinePack(ygUserCardPackItem.getPackId()))
        {
            return getDataTable(new java.util.ArrayList<YgUserCardPackItem>());
        }
        startPage();
        List<YgUserCardPackItem> list = ygUserCardPackItemService.selectYgUserCardPackItemList(ygUserCardPackItem);
        return getDataTable(list);
    }

    /**
     * 向当前登录用户的卡包添加卡片(移动端使用,校验归属)
     */
    @Log(title = "用户卡包", businessType = BusinessType.INSERT)
    @PostMapping("/mine/item")
    public AjaxResult addMineItem(@RequestBody YgUserCardPackItem ygUserCardPackItem)
    {
        if (ygUserCardPackItem.getCardId() == null)
        {
            return error("卡片不能为空");
        }
        if (!checkMinePack(ygUserCardPackItem.getPackId()))
        {
            return error("卡包不存在或无权操作");
        }
        return toAjax(ygUserCardPackItemService.addCardToPack(ygUserCardPackItem));
    }

    /**
     * 修改当前登录用户卡包内卡片数量(移动端使用,校验归属)
     */
    @Log(title = "用户卡包", businessType = BusinessType.UPDATE)
    @PutMapping("/mine/item")
    public AjaxResult editMineItem(@RequestBody YgUserCardPackItem ygUserCardPackItem)
    {
        if (!checkMineItem(ygUserCardPackItem.getItemId()))
        {
            return error("卡片不存在或无权操作");
        }
        return toAjax(ygUserCardPackItemService.updateYgUserCardPackItem(ygUserCardPackItem));
    }

    /**
     * 从当前登录用户的卡包移除卡片(移动端使用,校验归属)
     */
    @Log(title = "用户卡包", businessType = BusinessType.DELETE)
    @DeleteMapping("/mine/item/{itemIds}")
    public AjaxResult removeMineItem(@PathVariable Long[] itemIds)
    {
        for (Long itemId : itemIds)
        {
            if (!checkMineItem(itemId))
            {
                return error("卡片不存在或无权操作");
            }
        }
        return toAjax(ygUserCardPackItemService.deleteYgUserCardPackItemByItemIds(itemIds));
    }

    /**
     * 校验卡包是否属于当前登录用户
     */
    private boolean checkMinePack(Long packId)
    {
        if (packId == null)
        {
            return false;
        }
        YgUserCardPack pack = ygUserCardPackService.selectYgUserCardPackByPackId(packId);
        return pack != null && getUserId().equals(pack.getUserId());
    }

    /**
     * 校验卡包明细是否属于当前登录用户
     */
    private boolean checkMineItem(Long itemId)
    {
        if (itemId == null)
        {
            return false;
        }
        YgUserCardPackItem item = ygUserCardPackItemService.selectYgUserCardPackItemByItemId(itemId);
        return item != null && checkMinePack(item.getPackId());
    }
}
