package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.YgCardSetMapper;
import com.ruoyi.system.domain.YgCardSet;
import com.ruoyi.system.service.IYgCardSetService;

/**
 * 卡组系列Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-10-12
 */
@Service
public class YgCardSetServiceImpl implements IYgCardSetService 
{
    @Autowired
    private YgCardSetMapper ygCardSetMapper;

    /**
     * 查询卡组系列
     * 
     * @param setId 卡组系列主键
     * @return 卡组系列
     */
    @Override
    public YgCardSet selectYgCardSetBySetId(Long setId)
    {
        return ygCardSetMapper.selectYgCardSetBySetId(setId);
    }

    /**
     * 查询卡组系列列表
     * 
     * @param ygCardSet 卡组系列
     * @return 卡组系列
     */
    @Override
    public List<YgCardSet> selectYgCardSetList(YgCardSet ygCardSet)
    {
        return ygCardSetMapper.selectYgCardSetList(ygCardSet);
    }

    /**
     * 新增卡组系列
     * 
     * @param ygCardSet 卡组系列
     * @return 结果
     */
    @Override
    public int insertYgCardSet(YgCardSet ygCardSet)
    {
        ygCardSet.setCreateTime(DateUtils.getNowDate());
        return ygCardSetMapper.insertYgCardSet(ygCardSet);
    }

    /**
     * 修改卡组系列
     * 
     * @param ygCardSet 卡组系列
     * @return 结果
     */
    @Override
    public int updateYgCardSet(YgCardSet ygCardSet)
    {
        ygCardSet.setUpdateTime(DateUtils.getNowDate());
        return ygCardSetMapper.updateYgCardSet(ygCardSet);
    }

    /**
     * 批量删除卡组系列
     * 
     * @param setIds 需要删除的卡组系列主键
     * @return 结果
     */
    @Override
    public int deleteYgCardSetBySetIds(Long[] setIds)
    {
        return ygCardSetMapper.deleteYgCardSetBySetIds(setIds);
    }

    /**
     * 删除卡组系列信息
     * 
     * @param setId 卡组系列主键
     * @return 结果
     */
    @Override
    public int deleteYgCardSetBySetId(Long setId)
    {
        return ygCardSetMapper.deleteYgCardSetBySetId(setId);
    }
}
