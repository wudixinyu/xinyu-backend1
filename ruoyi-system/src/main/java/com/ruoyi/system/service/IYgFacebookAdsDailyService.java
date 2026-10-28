package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.YgFacebookAdsDaily;

/**
 * Facebook广告活动日报Service接口
 *
 * @author ruoyi
 * @date 2026-10-14
 */
public interface IYgFacebookAdsDailyService
{
    /**
     * 查询日报记录
     */
    public YgFacebookAdsDaily selectYgFacebookAdsDailyByDailyId(Long dailyId);

    /**
     * 查询日报列表
     */
    public List<YgFacebookAdsDaily> selectYgFacebookAdsDailyList(YgFacebookAdsDaily ygFacebookAdsDaily);

    /**
     * 新增日报记录
     */
    public int insertYgFacebookAdsDaily(YgFacebookAdsDaily ygFacebookAdsDaily);

    /**
     * 修改日报记录
     */
    public int updateYgFacebookAdsDaily(YgFacebookAdsDaily ygFacebookAdsDaily);

    /**
     * 批量删除日报记录
     */
    public int deleteYgFacebookAdsDailyByDailyIds(Long[] dailyIds);

    /**
     * 统计总览（单行汇总+派生指标）
     */
    public Map<String, Object> selectAdsSummary(YgFacebookAdsDaily ygFacebookAdsDaily);

    /**
     * 按日期趋势统计
     */
    public List<Map<String, Object>> selectAdsTrend(YgFacebookAdsDaily ygFacebookAdsDaily);

    /**
     * 按广告活动汇总统计
     */
    public List<Map<String, Object>> selectAdsCampaignStats(YgFacebookAdsDaily ygFacebookAdsDaily);

    /**
     * 导入日报数据（按 日期+账户+活动 查重，已存在时按开关决定更新或失败）
     *
     * @param adsList 日报数据列表
     * @param isUpdateSupport 是否更新已存在的数据
     * @param operName 操作用户
     * @return 结果消息
     */
    public String importFacebookAdsDaily(List<YgFacebookAdsDaily> adsList, Boolean isUpdateSupport, String operName);
}
