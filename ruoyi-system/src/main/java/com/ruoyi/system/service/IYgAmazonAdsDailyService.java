package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.YgAmazonAdsDaily;

/**
 * 亚马逊广告活动日报Service接口
 *
 * @author ruoyi
 * @date 2026-10-14
 */
public interface IYgAmazonAdsDailyService
{
    /**
     * 查询日报记录
     */
    public YgAmazonAdsDaily selectYgAmazonAdsDailyByDailyId(Long dailyId);

    /**
     * 查询日报列表
     */
    public List<YgAmazonAdsDaily> selectYgAmazonAdsDailyList(YgAmazonAdsDaily ygAmazonAdsDaily);

    /**
     * 新增日报记录
     */
    public int insertYgAmazonAdsDaily(YgAmazonAdsDaily ygAmazonAdsDaily);

    /**
     * 修改日报记录
     */
    public int updateYgAmazonAdsDaily(YgAmazonAdsDaily ygAmazonAdsDaily);

    /**
     * 批量删除日报记录
     */
    public int deleteYgAmazonAdsDailyByDailyIds(Long[] dailyIds);

    /**
     * 统计总览（单行汇总+派生指标）
     */
    public Map<String, Object> selectAdsSummary(YgAmazonAdsDaily ygAmazonAdsDaily);

    /**
     * 按日期趋势统计
     */
    public List<Map<String, Object>> selectAdsTrend(YgAmazonAdsDaily ygAmazonAdsDaily);

    /**
     * 按广告活动汇总统计
     */
    public List<Map<String, Object>> selectAdsCampaignStats(YgAmazonAdsDaily ygAmazonAdsDaily);

    /**
     * 导入日报数据（按 日期+档案+活动 查重，已存在时按开关决定更新或失败）
     *
     * @param adsList 日报数据列表
     * @param isUpdateSupport 是否更新已存在的数据
     * @param operName 操作用户
     * @return 结果消息
     */
    public String importAmazonAdsDaily(List<YgAmazonAdsDaily> adsList, Boolean isUpdateSupport, String operName);
}
