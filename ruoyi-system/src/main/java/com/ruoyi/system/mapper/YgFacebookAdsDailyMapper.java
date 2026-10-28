package com.ruoyi.system.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.YgFacebookAdsDaily;

/**
 * Facebook广告活动日报Mapper接口
 *
 * @author ruoyi
 * @date 2026-10-14
 */
public interface YgFacebookAdsDailyMapper
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
     * 按唯一键(日期+账户+活动)查询，供导入查重
     */
    public YgFacebookAdsDaily selectByUniqueKey(@Param("reportDate") Date reportDate, @Param("accountId") String accountId, @Param("campaignName") String campaignName);

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
     * 按广告活动汇总统计（花费降序，前20）
     */
    public List<Map<String, Object>> selectAdsCampaignStats(YgFacebookAdsDaily ygFacebookAdsDaily);
}
