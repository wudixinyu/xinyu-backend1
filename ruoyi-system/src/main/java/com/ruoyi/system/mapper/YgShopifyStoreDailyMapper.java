package com.ruoyi.system.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.YgShopifyStoreDaily;

/**
 * Shopify店铺渠道日报Mapper接口
 *
 * @author ruoyi
 * @date 2026-10-16
 */
public interface YgShopifyStoreDailyMapper
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
     * 按唯一键(日期+店铺+渠道)查询，供导入查重
     */
    public YgShopifyStoreDaily selectByUniqueKey(@Param("reportDate") Date reportDate, @Param("storeDomain") String storeDomain, @Param("channel") String channel);

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
     * 按销售渠道汇总统计（销售额降序，前20）
     */
    public List<Map<String, Object>> selectStoreChannelStats(YgShopifyStoreDaily ygShopifyStoreDaily);
}
