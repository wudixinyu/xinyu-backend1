package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YgWooCommerceStoreDaily;
import com.ruoyi.system.mapper.YgWooCommerceStoreDailyMapper;
import com.ruoyi.system.service.IYgWooCommerceStoreDailyService;

/**
 * WooCommerce店铺渠道日报Service业务层处理
 *
 * @author ruoyi
 * @date 2026-10-16
 */
@Service
public class YgWooCommerceStoreDailyServiceImpl implements IYgWooCommerceStoreDailyService
{
    private static final Logger log = LoggerFactory.getLogger(YgWooCommerceStoreDailyServiceImpl.class);

    @Autowired
    private YgWooCommerceStoreDailyMapper ygWooCommerceStoreDailyMapper;

    /**
     * 查询日报记录
     */
    @Override
    public YgWooCommerceStoreDaily selectYgWooCommerceStoreDailyByDailyId(Long dailyId)
    {
        return ygWooCommerceStoreDailyMapper.selectYgWooCommerceStoreDailyByDailyId(dailyId);
    }

    /**
     * 查询日报列表
     */
    @Override
    public List<YgWooCommerceStoreDaily> selectYgWooCommerceStoreDailyList(YgWooCommerceStoreDaily ygWooCommerceStoreDaily)
    {
        return ygWooCommerceStoreDailyMapper.selectYgWooCommerceStoreDailyList(ygWooCommerceStoreDaily);
    }

    /**
     * 新增日报记录
     */
    @Override
    public int insertYgWooCommerceStoreDaily(YgWooCommerceStoreDaily ygWooCommerceStoreDaily)
    {
        return ygWooCommerceStoreDailyMapper.insertYgWooCommerceStoreDaily(ygWooCommerceStoreDaily);
    }

    /**
     * 修改日报记录
     */
    @Override
    public int updateYgWooCommerceStoreDaily(YgWooCommerceStoreDaily ygWooCommerceStoreDaily)
    {
        return ygWooCommerceStoreDailyMapper.updateYgWooCommerceStoreDaily(ygWooCommerceStoreDaily);
    }

    /**
     * 批量删除日报记录
     */
    @Override
    public int deleteYgWooCommerceStoreDailyByDailyIds(Long[] dailyIds)
    {
        return ygWooCommerceStoreDailyMapper.deleteYgWooCommerceStoreDailyByDailyIds(dailyIds);
    }

    /**
     * 统计总览（单行汇总+派生指标）
     */
    @Override
    public Map<String, Object> selectStoreSummary(YgWooCommerceStoreDaily ygWooCommerceStoreDaily)
    {
        return ygWooCommerceStoreDailyMapper.selectStoreSummary(ygWooCommerceStoreDaily);
    }

    /**
     * 按日期趋势统计
     */
    @Override
    public List<Map<String, Object>> selectStoreTrend(YgWooCommerceStoreDaily ygWooCommerceStoreDaily)
    {
        return ygWooCommerceStoreDailyMapper.selectStoreTrend(ygWooCommerceStoreDaily);
    }

    /**
     * 按销售渠道汇总统计
     */
    @Override
    public List<Map<String, Object>> selectStoreChannelStats(YgWooCommerceStoreDaily ygWooCommerceStoreDaily)
    {
        return ygWooCommerceStoreDailyMapper.selectStoreChannelStats(ygWooCommerceStoreDaily);
    }

    /**
     * 导入日报数据（按 日期+店铺+渠道 查重，已存在时按开关决定更新或失败）
     */
    @Override
    public String importWooCommerceStoreDaily(List<YgWooCommerceStoreDaily> storeList, Boolean isUpdateSupport, String operName)
    {
        if (StringUtils.isNull(storeList) || storeList.size() == 0)
        {
            throw new ServiceException("导入店铺数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (YgWooCommerceStoreDaily store : storeList)
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
                YgWooCommerceStoreDaily exist = ygWooCommerceStoreDailyMapper.selectByUniqueKey(store.getReportDate(), store.getStoreDomain(), store.getChannel());
                if (StringUtils.isNull(exist))
                {
                    store.setCreateBy(operName);
                    ygWooCommerceStoreDailyMapper.insertYgWooCommerceStoreDaily(store);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、" + store.getReportDate() + " " + store.getChannel() + " 导入成功");
                }
                else if (isUpdateSupport)
                {
                    store.setDailyId(exist.getDailyId());
                    store.setUpdateBy(operName);
                    ygWooCommerceStoreDailyMapper.updateYgWooCommerceStoreDaily(store);
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
