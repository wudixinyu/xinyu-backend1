package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.mapper.YgUserCardPackItemMapper;
import com.ruoyi.system.mapper.YgUserCardPackMapper;
import com.ruoyi.system.domain.YgUserCardPack;
import com.ruoyi.system.service.IYgUserCardPackService;

/**
 * 用户卡包 服务层实现
 * 
 * @author ruoyi
 * @date 2026-10-12
 */
@Service
public class YgUserCardPackServiceImpl implements IYgUserCardPackService
{
    @Autowired
    private YgUserCardPackMapper ygUserCardPackMapper;

    @Autowired
    private YgUserCardPackItemMapper ygUserCardPackItemMapper;

    /**
     * 查询用户卡包列表
     * 
     * @param ygUserCardPack 用户卡包信息
     * @return 用户卡包
     */
    @Override
    public List<YgUserCardPack> selectYgUserCardPackList(YgUserCardPack ygUserCardPack)
    {
        return ygUserCardPackMapper.selectYgUserCardPackList(ygUserCardPack);
    }

    /**
     * 通过卡包主键查询用户卡包
     * 
     * @param packId 卡包主键
     * @return 用户卡包
     */
    @Override
    public YgUserCardPack selectYgUserCardPackByPackId(Long packId)
    {
        return ygUserCardPackMapper.selectYgUserCardPackByPackId(packId);
    }

    /**
     * 新增用户卡包
     * 
     * @param ygUserCardPack 用户卡包信息
     * @return 结果
     */
    @Override
    public int insertYgUserCardPack(YgUserCardPack ygUserCardPack)
    {
        ygUserCardPack.setCreateTime(DateUtils.getNowDate());
        return ygUserCardPackMapper.insertYgUserCardPack(ygUserCardPack);
    }

    /**
     * 修改用户卡包
     * 
     * @param ygUserCardPack 用户卡包信息
     * @return 结果
     */
    @Override
    public int updateYgUserCardPack(YgUserCardPack ygUserCardPack)
    {
        return ygUserCardPackMapper.updateYgUserCardPack(ygUserCardPack);
    }

    /**
     * 批量删除用户卡包(级联删除卡包明细)
     * 
     * @param packIds 需要删除的用户卡包主键集合
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteYgUserCardPackByPackIds(Long[] packIds)
    {
        for (Long packId : packIds)
        {
            ygUserCardPackItemMapper.deleteYgUserCardPackItemByPackId(packId);
        }
        return ygUserCardPackMapper.deleteYgUserCardPackByPackIds(packIds);
    }
}
