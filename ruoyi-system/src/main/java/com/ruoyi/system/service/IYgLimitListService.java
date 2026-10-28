package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.YgLimitList;

/**
 * 禁限卡 服务层
 * 
 * @author ruoyi
 * @date 2026-10-12
 */
public interface IYgLimitListService
{
    /**
     * 查询禁限卡列表
     * 
     * @param ygLimitList 禁限卡信息
     * @return 禁限卡集合
     */
    public List<YgLimitList> selectYgLimitListList(YgLimitList ygLimitList);

    /**
     * 通过禁限卡主键查询禁限卡
     * 
     * @param limitId 禁限卡主键
     * @return 禁限卡信息
     */
    public YgLimitList selectYgLimitListByLimitId(Long limitId);

    /**
     * 新增禁限卡
     * 
     * @param ygLimitList 禁限卡信息
     * @return 结果
     */
    public int insertYgLimitList(YgLimitList ygLimitList);

    /**
     * 修改禁限卡
     * 
     * @param ygLimitList 禁限卡信息
     * @return 结果
     */
    public int updateYgLimitList(YgLimitList ygLimitList);

    /**
     * 通过禁限卡主键删除禁限卡
     * 
     * @param limitId 禁限卡主键
     * @return 结果
     */
    public int deleteYgLimitListByLimitId(Long limitId);

    /**
     * 批量删除禁限卡信息
     * 
     * @param limitIds 需要删除的禁限卡主键集合
     * @return 结果
     */
    public int deleteYgLimitListByLimitIds(Long[] limitIds);
}
