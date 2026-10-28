package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.YgCardInfo;

/**
 * 卡片信息Service接口
 * 
 * @author ruoyi
 * @date 2026-10-12
 */
public interface IYgCardInfoService 
{
    /**
     * 查询卡片信息
     * 
     * @param cardId 卡片信息主键
     * @return 卡片信息
     */
    public YgCardInfo selectYgCardInfoByCardId(Long cardId);

    /**
     * 查询卡片信息列表
     * 
     * @param ygCardInfo 卡片信息
     * @return 卡片信息集合
     */
    public List<YgCardInfo> selectYgCardInfoList(YgCardInfo ygCardInfo);

    /**
     * 新增卡片信息
     * 
     * @param ygCardInfo 卡片信息
     * @return 结果
     */
    public int insertYgCardInfo(YgCardInfo ygCardInfo);

    /**
     * 修改卡片信息
     * 
     * @param ygCardInfo 卡片信息
     * @return 结果
     */
    public int updateYgCardInfo(YgCardInfo ygCardInfo);

    /**
     * 批量删除卡片信息
     * 
     * @param cardIds 需要删除的卡片信息主键集合
     * @return 结果
     */
    public int deleteYgCardInfoByCardIds(Long[] cardIds);

    /**
     * 删除卡片信息信息
     * 
     * @param cardId 卡片信息主键
     * @return 结果
     */
    public int deleteYgCardInfoByCardId(Long cardId);
}
