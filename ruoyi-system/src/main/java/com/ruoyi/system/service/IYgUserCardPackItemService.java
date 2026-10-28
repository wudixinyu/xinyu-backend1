package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.YgUserCardPackItem;

/**
 * 用户卡包明细 服务层
 * 
 * @author ruoyi
 * @date 2026-10-12
 */
public interface IYgUserCardPackItemService
{
    /**
     * 查询卡包明细列表
     * 
     * @param ygUserCardPackItem 卡包明细信息
     * @return 卡包明细集合
     */
    public List<YgUserCardPackItem> selectYgUserCardPackItemList(YgUserCardPackItem ygUserCardPackItem);

    /**
     * 通过明细主键查询卡包明细
     * 
     * @param itemId 明细主键
     * @return 卡包明细信息
     */
    public YgUserCardPackItem selectYgUserCardPackItemByItemId(Long itemId);

    /**
     * 向卡包添加卡片(已存在则累加数量)
     * 
     * @param ygUserCardPackItem 卡包明细信息
     * @return 结果
     */
    public int addCardToPack(YgUserCardPackItem ygUserCardPackItem);

    /**
     * 修改卡包明细
     * 
     * @param ygUserCardPackItem 卡包明细信息
     * @return 结果
     */
    public int updateYgUserCardPackItem(YgUserCardPackItem ygUserCardPackItem);

    /**
     * 批量删除卡包明细
     * 
     * @param itemIds 需要删除的卡包明细主键集合
     * @return 结果
     */
    public int deleteYgUserCardPackItemByItemIds(Long[] itemIds);
}
