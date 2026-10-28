package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.mapper.YgUserCardPackItemMapper;
import com.ruoyi.system.domain.YgUserCardPackItem;
import com.ruoyi.system.service.IYgUserCardPackItemService;

/**
 * 用户卡包明细 服务层实现
 * 
 * @author ruoyi
 * @date 2026-10-12
 */
@Service
public class YgUserCardPackItemServiceImpl implements IYgUserCardPackItemService
{
    @Autowired
    private YgUserCardPackItemMapper ygUserCardPackItemMapper;

    /**
     * 查询卡包明细列表
     * 
     * @param ygUserCardPackItem 卡包明细信息
     * @return 卡包明细
     */
    @Override
    public List<YgUserCardPackItem> selectYgUserCardPackItemList(YgUserCardPackItem ygUserCardPackItem)
    {
        return ygUserCardPackItemMapper.selectYgUserCardPackItemList(ygUserCardPackItem);
    }

    /**
     * 通过明细主键查询卡包明细
     * 
     * @param itemId 明细主键
     * @return 卡包明细
     */
    @Override
    public YgUserCardPackItem selectYgUserCardPackItemByItemId(Long itemId)
    {
        return ygUserCardPackItemMapper.selectYgUserCardPackItemByItemId(itemId);
    }

    /**
     * 向卡包添加卡片(同一张卡已存在则累加数量)
     * 
     * @param ygUserCardPackItem 卡包明细信息
     * @return 结果
     */
    @Override
    public int addCardToPack(YgUserCardPackItem ygUserCardPackItem)
    {
        if (ygUserCardPackItem.getQuantity() == null)
        {
            ygUserCardPackItem.setQuantity(1);
        }
        YgUserCardPackItem exist = ygUserCardPackItemMapper.selectByPackAndCard(ygUserCardPackItem);
        if (exist != null)
        {
            int quantity = (exist.getQuantity() == null ? 0 : exist.getQuantity()) + ygUserCardPackItem.getQuantity();
            exist.setQuantity(quantity);
            return ygUserCardPackItemMapper.updateYgUserCardPackItem(exist);
        }
        ygUserCardPackItem.setCreateTime(DateUtils.getNowDate());
        return ygUserCardPackItemMapper.insertYgUserCardPackItem(ygUserCardPackItem);
    }

    /**
     * 修改卡包明细
     * 
     * @param ygUserCardPackItem 卡包明细信息
     * @return 结果
     */
    @Override
    public int updateYgUserCardPackItem(YgUserCardPackItem ygUserCardPackItem)
    {
        return ygUserCardPackItemMapper.updateYgUserCardPackItem(ygUserCardPackItem);
    }

    /**
     * 批量删除卡包明细
     * 
     * @param itemIds 需要删除的卡包明细主键集合
     * @return 结果
     */
    @Override
    public int deleteYgUserCardPackItemByItemIds(Long[] itemIds)
    {
        return ygUserCardPackItemMapper.deleteYgUserCardPackItemByItemIds(itemIds);
    }
}
