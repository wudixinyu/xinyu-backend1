package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.YgShopifyStoreDaily;

/**
 * Shopify店铺渠道日报Service接口
 *
 * @author ruoyi
 * @date 2026-10-16
 */
public interface IYgShopifyStoreDailyService
{
    /**
     * 查询日报记录
     */
    public YgShopifyStoreDaily selectYgShopifyStoreDailyByDailyId(Long dailyId);

    /**
     * 查询日报列表
     */
    public List<YgShopifyStoreDaily> selectYgShopifyStoreDailyList(YgShopifyStoreDaily ygShopifyStoreDaily);

    /**
     * 新增日报记录
     */
    public int insertYgShopifyStoreDaily(YgShopifyStoreDaily ygShopifyStoreDaily);

    /**
     * 修改日报记录
     */
    public int updateYgShopifyStoreDaily(YgShopifyStoreDaily ygShopifyStoreDaily);

    /**
     * 批量删除日报记录
     */
    public int deleteYgShopifyStoreDailyByDailyIds(Long[] dailyIds);

    /**
     * 统计总览（单行汇总+派生指标）
     */
    public Map<String, Object> selectStoreSummary(YgShopifyStoreDaily ygShopifyStoreDaily);

    /**
     * 按日期趋势统计
     */
    public List<Map<String, Object>> selectStoreTrend(YgShopifyStoreDaily ygShopifyStoreDaily);

    /**
     * 按销售渠道汇总统计
     */
    public List<Map<String, Object>> selectStoreChannelStats(YgShopifyStoreDaily ygShopifyStoreDaily);

    /**
     * 导入日报数据（按 日期+店铺+渠道 查重，已存在时按开关决定更新或失败）
     *
     * @param storeList 日报数据列表
     * @param isUpdateSupport 是否更新已存在的数据
     * @param operName 操作用户
     * @return 结果消息
     */
    public String importShopifyStoreDaily(List<YgShopifyStoreDaily> storeList, Boolean isUpdateSupport, String operName);
}
