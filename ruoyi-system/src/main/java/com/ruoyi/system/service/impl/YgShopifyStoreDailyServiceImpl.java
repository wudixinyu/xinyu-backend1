package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YgShopifyStoreDaily;
import com.ruoyi.system.mapper.YgShopifyStoreDailyMapper;
import com.ruoyi.system.service.IYgShopifyStoreDailyService;

/**
 * Shopify店铺渠道日报Service业务层处理
 *
 * @author ruoyi
 * @date 2026-10-16
 */
@Service
public class YgShopifyStoreDailyServiceImpl implements IYgShopifyStoreDailyService
{
    private static final Logger log = LoggerFactory.getLogger(YgShopifyStoreDailyServiceImpl.class);

    @Autowired
    private YgShopifyStoreDailyMapper ygShopifyStoreDailyMapper;

    /**
     * 查询日报记录
     */
    @Override
    public YgShopifyStoreDaily selectYgShopifyStoreDailyByDailyId(Long dailyId)
    {
        return ygShopifyStoreDailyMapper.selectYgShopifyStoreDailyByDailyId(dailyId);
    }

    /**
     * 查询日报列表
     */
    @Override
    public List<YgShopifyStoreDaily> selectYgShopifyStoreDailyList(YgShopifyStoreDaily ygShopifyStoreDaily)
    {
        return ygShopifyStoreDailyMapper.selectYgShopifyStoreDailyList(ygShopifyStoreDaily);
    }

    /**
     * 新增日报记录
     */
    @Override
    public int insertYgShopifyStoreDaily(YgShopifyStoreDaily ygShopifyStoreDaily)
    {
        return ygShopifyStoreDailyMapper.insertYgShopifyStoreDaily(ygShopifyStoreDaily);
    }

    /**
     * 修改日报记录
     */
    @Override
    public int updateYgShopifyStoreDaily(YgShopifyStoreDaily ygShopifyStoreDaily)
    {
        return ygShopifyStoreDailyMapper.updateYgShopifyStoreDaily(ygShopifyStoreDaily);
    }

    /**
     * 批量删除日报记录
     */
    @Override
    public int deleteYgShopifyStoreDailyByDailyIds(Long[] dailyIds)
    {
        return ygShopifyStoreDailyMapper.deleteYgShopifyStoreDailyByDailyIds(dailyIds);
    }

    /**
     * 统计总览（单行汇总+派生指标）
     */
    @Override
    public Map<String, Object> selectStoreSummary(YgShopifyStoreDaily ygShopifyStoreDaily)
    {
        return ygShopifyStoreDailyMapper.selectStoreSummary(ygShopifyStoreDaily);
    }

    /**
     * 按日期趋势统计
     */
    @Override
    public List<Map<String, Object>> selectStoreTrend(YgShopifyStoreDaily ygShopifyStoreDaily)
    {
        return ygShopifyStoreDailyMapper.selectStoreTrend(ygShopifyStoreDaily);
    }

    /**
     * 按销售渠道汇总统计
     */
    @Override
    public List<Map<String, Object>> selectStoreChannelStats(YgShopifyStoreDaily ygShopifyStoreDaily)
    {
        return ygShopifyStoreDailyMapper.selectStoreChannelStats(ygShopifyStoreDaily);
    }

    /**
     * 导入日报数据（按 日期+店铺+渠道 查重，已存在时按开关决定更新或失败）
     */
    @Override
    public String importShopifyStoreDaily(List<YgShopifyStoreDaily> storeList, Boolean isUpdateSupport, String operName)
    {
        if (StringUtils.isNull(storeList) || storeList.size() == 0)
        {
            throw new ServiceException("导入店铺数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (YgShopifyStoreDaily store : storeList)
        {
            try
            {
                if (store.getReportDate() == null)
                {
                    throw new ServiceException("日期不能为空");
                }
                if (StringUtils.isEmpty(store.getChannel()))
                {
                    throw new ServiceException("销售渠道不能为空");
                }
                if (store.getStoreDomain() == null)
                {
                    store.setStoreDomain("");
                }
                YgShopifyStoreDaily exist = ygShopifyStoreDailyMapper.selectByUniqueKey(store.getReportDate(), store.getStoreDomain(), store.getChannel());
                if (StringUtils.isNull(exist))
                {
                    store.setCreateBy(operName);
                    ygShopifyStoreDailyMapper.insertYgShopifyStoreDaily(store);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、" + store.getReportDate() + " " + store.getChannel() + " 导入成功");
                }
                else if (isUpdateSupport)
                {
                    store.setDailyId(exist.getDailyId());
                    store.setUpdateBy(operName);
                    ygShopifyStoreDailyMapper.updateYgShopifyStoreDaily(store);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、" + store.getReportDate() + " " + store.getChannel() + " 更新成功");
                }
                else
                {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、" + store.getReportDate() + " " + store.getChannel() + " 已存在");
                }
            }
            catch (Exception e)
            {
                failureNum++;
                String msg = "<br/>" + failureNum + "、" + (store.getChannel() == null ? "未知渠道" : store.getChannel()) + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
                log.error(msg, e);
            }
        }
        if (failureNum > 0)
        {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据未成功，错误如下：");
            throw new ServiceException(failureMsg.toString());
        }
        else
        {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        return successMsg.toString();
    }
}
