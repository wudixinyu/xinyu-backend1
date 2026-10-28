package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.YgLimitList;

/**
 * 禁限卡 数据层
 * 
 * @author ruoyi
 * @date 2026-10-12
 */
public interface YgLimitListMapper
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
     * 批量删除禁限卡
     * 
     * @param limitIds 需要删除的禁限卡主键集合
     * @return 结果
     */
    public int deleteYgLimitListByLimitIds(Long[] limitIds);

    /**
     * 将指定环境的当前禁限卡记录标记为历史
     * 
     * @param otType 适用环境(ocg/tcg/sc)
     * @return 结果
     */
    public int markLimitHistory(String otType);

    /**
     * 批量新增或更新禁限卡(唯一键冲突时更新限制类型与当前标记)
     * 
     * @param ygLimitLists 禁限卡集合
     * @return 结果
     */
    public int batchUpsertYgLimitList(List<YgLimitList> ygLimitLists);
}
