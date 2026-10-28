package com.ruoyi.system.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.YgAmazonAdsDaily;

/**
 * 亚马逊广告活动日报Mapper接口
 *
 * @author ruoyi
 * @date 2026-10-14
 */
public interface YgAmazonAdsDailyMapper
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
     * 按唯一键(日期+档案+活动)查询，供导入查重
     */
    public YgAmazonAdsDaily selectByUniqueKey(@Param("reportDate") Date reportDate, @Param("profileId") String profileId, @Param("campaignName") String campaignName);

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
     * 按广告活动汇总统计（花费降序，前20）
     */
    public List<Map<String, Object>> selectAdsCampaignStats(YgAmazonAdsDaily ygAmazonAdsDaily);
}
