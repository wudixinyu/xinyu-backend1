package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.YgUserCardPackItem;

/**
 * 用户卡包明细 数据层
 * 
 * @author ruoyi
 * @date 2026-10-12
 */
public interface YgUserCardPackItemMapper
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
     * 通过卡包和卡片查询明细
     * 
     * @param ygUserCardPackItem 卡包明细信息(packId/cardId)
     * @return 卡包明细信息
     */
    public YgUserCardPackItem selectByPackAndCard(YgUserCardPackItem ygUserCardPackItem);

    /**
     * 新增卡包明细
     * 
     * @param ygUserCardPackItem 卡包明细信息
     * @return 结果
     */
    public int insertYgUserCardPackItem(YgUserCardPackItem ygUserCardPackItem);

    /**
     * 修改卡包明细
     * 
     * @param ygUserCardPackItem 卡包明细信息
     * @return 结果
     */
    public int updateYgUserCardPackItem(YgUserCardPackItem ygUserCardPackItem);

    /**
     * 通过明细主键删除卡包明细
     * 
     * @param itemId 明细主键
     * @return 结果
     */
    public int deleteYgUserCardPackItemByItemId(Long itemId);

    /**
     * 批量删除卡包明细
     * 
     * @param itemIds 需要删除的卡包明细主键集合
     * @return 结果
     */
    public int deleteYgUserCardPackItemByItemIds(Long[] itemIds);

    /**
     * 通过卡包主键删除卡包明细
     * 
     * @param packId 卡包主键
     * @return 结果
     */
    public int deleteYgUserCardPackItemByPackId(Long packId);
}
