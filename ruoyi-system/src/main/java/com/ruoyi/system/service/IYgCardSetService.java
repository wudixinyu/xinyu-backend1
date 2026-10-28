package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.YgCardSet;

/**
 * 卡组系列Service接口
 * 
 * @author ruoyi
 * @date 2026-10-12
 */
public interface IYgCardSetService 
{
    /**
     * 查询卡组系列
     * 
     * @param setId 卡组系列主键
     * @return 卡组系列
     */
    public YgCardSet selectYgCardSetBySetId(Long setId);

    /**
     * 查询卡组系列列表
     * 
     * @param ygCardSet 卡组系列
     * @return 卡组系列集合
     */
    public List<YgCardSet> selectYgCardSetList(YgCardSet ygCardSet);

    /**
     * 新增卡组系列
     * 
     * @param ygCardSet 卡组系列
     * @return 结果
     */
    public int insertYgCardSet(YgCardSet ygCardSet);

    /**
     * 修改卡组系列
     * 
     * @param ygCardSet 卡组系列
     * @return 结果
     */
    public int updateYgCardSet(YgCardSet ygCardSet);

    /**
     * 批量删除卡组系列
     * 
     * @param setIds 需要删除的卡组系列主键集合
     * @return 结果
     */
    public int deleteYgCardSetBySetIds(Long[] setIds);

    /**
     * 删除卡组系列信息
     * 
     * @param setId 卡组系列主键
     * @return 结果
     */
    public int deleteYgCardSetBySetId(Long setId);
}
