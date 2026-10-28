package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.YgUserCardPack;

/**
 * 用户卡包 服务层
 * 
 * @author ruoyi
 * @date 2026-10-12
 */
public interface IYgUserCardPackService
{
    /**
     * 查询用户卡包列表
     * 
     * @param ygUserCardPack 用户卡包信息
     * @return 用户卡包集合
     */
    public List<YgUserCardPack> selectYgUserCardPackList(YgUserCardPack ygUserCardPack);

    /**
     * 通过卡包主键查询用户卡包
     * 
     * @param packId 卡包主键
     * @return 用户卡包信息
     */
    public YgUserCardPack selectYgUserCardPackByPackId(Long packId);

    /**
     * 新增用户卡包
     * 
     * @param ygUserCardPack 用户卡包信息
     * @return 结果
     */
    public int insertYgUserCardPack(YgUserCardPack ygUserCardPack);

    /**
     * 修改用户卡包
     * 
     * @param ygUserCardPack 用户卡包信息
     * @return 结果
     */
    public int updateYgUserCardPack(YgUserCardPack ygUserCardPack);

    /**
     * 批量删除用户卡包(级联删除卡包明细)
     * 
     * @param packIds 需要删除的用户卡包主键集合
     * @return 结果
     */
    public int deleteYgUserCardPackByPackIds(Long[] packIds);
}
