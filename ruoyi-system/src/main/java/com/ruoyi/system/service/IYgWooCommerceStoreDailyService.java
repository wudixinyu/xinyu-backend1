package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.YgWooCommerceStoreDaily;

/**
 * WooCommerce店铺渠道日报Service接口
 *
 * @author ruoyi
 * @date 2026-10-16
 */
public interface IYgWooCommerceStoreDailyService
{
    /**
     * 查询日报记录
     */
    public YgWooCommerceStoreDaily selectYgWooCommerceStoreDailyByDailyId(Long dailyId);

    /**
     * 查询日报列表
     */
    public List<YgWooCommerceStoreDaily> selectYgWooCommerceStoreDailyList(YgWooCommerceStoreDaily ygWooCommerceStoreDaily);

    /**
     * 新增日报记录
     */
    public int insertYgWooCommerceStoreDaily(YgWooCommerceStoreDaily ygWooCommerceStoreDaily);

    /**
     * 修改日报记录
     */
    public int updateYgWooCommerceStoreDaily(YgWooCommerceStoreDaily ygWooCommerceStoreDaily);

    /**
     * 批量删除日报记录
     */
    public int deleteYgWooCommerceStoreDailyByDailyIds(Long[] dailyIds);

    /**
     * 统计总览（单行汇总+派生指标）
     */
    public Map<String, Object> selectStoreSummary(YgWooCommerceStoreDaily ygWooCommerceStoreDaily);

    /**
     * 按日期趋势统计
     */
    public List<Map<String, Object>> selectStoreTrend(YgWooCommerceStoreDaily ygWooCommerceStoreDaily);

    /**
     * 按销售渠道汇总统计
     */
    public List<Map<String, Object>> selectStoreChannelStats(YgWooCommerceStoreDaily ygWooCommerceStoreDaily);

    /**
     * 导入日报数据（按 日期+店铺+渠道 查重，已存在时按开关决定更新或失败）
     *
     * @param storeList 日报数据列表
     * @param isUpdateSupport 是否更新已存在的数据
     * @param operName 操作用户
     * @return 结果消息
     */
    public String importWooCommerceStoreDaily(List<YgWooCommerceStoreDaily> storeList, Boolean isUpdateSupport, String operName);
}
