package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.YgCardInfoMapper;
import com.ruoyi.system.domain.YgCardInfo;
import com.ruoyi.system.service.IYgCardInfoService;

/**
 * 卡片信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-10-12
 */
@Service
public class YgCardInfoServiceImpl implements IYgCardInfoService 
{
    @Autowired
    private YgCardInfoMapper ygCardInfoMapper;

    /**
     * 查询卡片信息
     * 
     * @param cardId 卡片信息主键
     * @return 卡片信息
     */
    @Override
    public YgCardInfo selectYgCardInfoByCardId(Long cardId)
    {
        return ygCardInfoMapper.selectYgCardInfoByCardId(cardId);
    }

    /**
     * 查询卡片信息列表
     * 
     * @param ygCardInfo 卡片信息
     * @return 卡片信息
     */
    @Override
    public List<YgCardInfo> selectYgCardInfoList(YgCardInfo ygCardInfo)
    {
        return ygCardInfoMapper.selectYgCardInfoList(ygCardInfo);
    }

    /**
     * 新增卡片信息
     * 
     * @param ygCardInfo 卡片信息
     * @return 结果
     */
    @Override
    public int insertYgCardInfo(YgCardInfo ygCardInfo)
    {
        ygCardInfo.setCreateTime(DateUtils.getNowDate());
        return ygCardInfoMapper.insertYgCardInfo(ygCardInfo);
    }

    /**
     * 修改卡片信息
     * 
     * @param ygCardInfo 卡片信息
     * @return 结果
     */
    @Override
    public int updateYgCardInfo(YgCardInfo ygCardInfo)
    {
        ygCardInfo.setUpdateTime(DateUtils.getNowDate());
        return ygCardInfoMapper.updateYgCardInfo(ygCardInfo);
    }

    /**
     * 批量删除卡片信息
     * 
     * @param cardIds 需要删除的卡片信息主键
     * @return 结果
     */
    @Override
    public int deleteYgCardInfoByCardIds(Long[] cardIds)
    {
        return ygCardInfoMapper.deleteYgCardInfoByCardIds(cardIds);
    }

    /**
     * 删除卡片信息信息
     * 
     * @param cardId 卡片信息主键
     * @return 结果
     */
    @Override
    public int deleteYgCardInfoByCardId(Long cardId)
    {
        return ygCardInfoMapper.deleteYgCardInfoByCardId(cardId);
    }
}
