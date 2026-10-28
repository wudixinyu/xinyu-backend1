package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.YgLimitListMapper;
import com.ruoyi.system.domain.YgLimitList;
import com.ruoyi.system.service.IYgLimitListService;

/**
 * 禁限卡 服务层实现
 * 
 * @author ruoyi
 * @date 2026-10-12
 */
@Service
public class YgLimitListServiceImpl implements IYgLimitListService
{
    @Autowired
    private YgLimitListMapper ygLimitListMapper;

    /**
     * 查询禁限卡列表
     * 
     * @param ygLimitList 禁限卡信息
     * @return 禁限卡
     */
    @Override
    public List<YgLimitList> selectYgLimitListList(YgLimitList ygLimitList)
    {
        return ygLimitListMapper.selectYgLimitListList(ygLimitList);
    }

    /**
     * 通过禁限卡主键查询禁限卡
     * 
     * @param limitId 禁限卡主键
     * @return 禁限卡
     */
    @Override
    public YgLimitList selectYgLimitListByLimitId(Long limitId)
    {
        return ygLimitListMapper.selectYgLimitListByLimitId(limitId);
    }

    /**
     * 新增禁限卡
     * 
     * @param ygLimitList 禁限卡信息
     * @return 结果
     */
    @Override
    public int insertYgLimitList(YgLimitList ygLimitList)
    {
        return ygLimitListMapper.insertYgLimitList(ygLimitList);
    }

    /**
     * 修改禁限卡
     * 
     * @param ygLimitList 禁限卡信息
     * @return 结果
     */
    @Override
    public int updateYgLimitList(YgLimitList ygLimitList)
    {
        return ygLimitListMapper.updateYgLimitList(ygLimitList);
    }

    /**
     * 通过禁限卡主键删除禁限卡
     * 
     * @param limitId 禁限卡主键
     * @return 结果
     */
    @Override
    public int deleteYgLimitListByLimitId(Long limitId)
    {
        return ygLimitListMapper.deleteYgLimitListByLimitId(limitId);
    }

    /**
     * 批量删除禁限卡信息
     * 
     * @param limitIds 需要删除的禁限卡主键集合
     * @return 结果
     */
    @Override
    public int deleteYgLimitListByLimitIds(Long[] limitIds)
    {
        return ygLimitListMapper.deleteYgLimitListByLimitIds(limitIds);
    }
}
