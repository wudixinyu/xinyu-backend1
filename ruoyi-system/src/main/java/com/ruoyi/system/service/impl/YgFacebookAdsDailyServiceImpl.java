package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YgFacebookAdsDaily;
import com.ruoyi.system.mapper.YgFacebookAdsDailyMapper;
import com.ruoyi.system.service.IYgFacebookAdsDailyService;

/**
 * Facebook广告活动日报Service业务层处理
 *
 * @author ruoyi
 * @date 2026-10-14
 */
@Service
public class YgFacebookAdsDailyServiceImpl implements IYgFacebookAdsDailyService
{
    private static final Logger log = LoggerFactory.getLogger(YgFacebookAdsDailyServiceImpl.class);

    @Autowired
    private YgFacebookAdsDailyMapper ygFacebookAdsDailyMapper;

    /**
     * 查询日报记录
     */
    @Override
    public YgFacebookAdsDaily selectYgFacebookAdsDailyByDailyId(Long dailyId)
    {
        return ygFacebookAdsDailyMapper.selectYgFacebookAdsDailyByDailyId(dailyId);
    }

    /**
     * 查询日报列表
     */
    @Override
    public List<YgFacebookAdsDaily> selectYgFacebookAdsDailyList(YgFacebookAdsDaily ygFacebookAdsDaily)
    {
        return ygFacebookAdsDailyMapper.selectYgFacebookAdsDailyList(ygFacebookAdsDaily);
    }

    /**
     * 新增日报记录
     */
    @Override
    public int insertYgFacebookAdsDaily(YgFacebookAdsDaily ygFacebookAdsDaily)
    {
        return ygFacebookAdsDailyMapper.insertYgFacebookAdsDaily(ygFacebookAdsDaily);
    }

    /**
     * 修改日报记录
     */
    @Override
    public int updateYgFacebookAdsDaily(YgFacebookAdsDaily ygFacebookAdsDaily)
    {
        return ygFacebookAdsDailyMapper.updateYgFacebookAdsDaily(ygFacebookAdsDaily);
    }

    /**
     * 批量删除日报记录
     */
    @Override
    public int deleteYgFacebookAdsDailyByDailyIds(Long[] dailyIds)
    {
        return ygFacebookAdsDailyMapper.deleteYgFacebookAdsDailyByDailyIds(dailyIds);
    }

    /**
     * 统计总览（单行汇总+派生指标）
     */
    @Override
    public Map<String, Object> selectAdsSummary(YgFacebookAdsDaily ygFacebookAdsDaily)
    {
        return ygFacebookAdsDailyMapper.selectAdsSummary(ygFacebookAdsDaily);
    }

    /**
     * 按日期趋势统计
     */
    @Override
    public List<Map<String, Object>> selectAdsTrend(YgFacebookAdsDaily ygFacebookAdsDaily)
    {
        return ygFacebookAdsDailyMapper.selectAdsTrend(ygFacebookAdsDaily);
    }

    /**
     * 按广告活动汇总统计
     */
    @Override
    public List<Map<String, Object>> selectAdsCampaignStats(YgFacebookAdsDaily ygFacebookAdsDaily)
    {
        return ygFacebookAdsDailyMapper.selectAdsCampaignStats(ygFacebookAdsDaily);
    }

    /**
     * 导入日报数据（按 日期+账户+活动 查重，已存在时按开关决定更新或失败）
     */
    @Override
    public String importFacebookAdsDaily(List<YgFacebookAdsDaily> adsList, Boolean isUpdateSupport, String operName)
    {
        if (StringUtils.isNull(adsList) || adsList.size() == 0)
        {
            throw new ServiceException("导入广告数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (YgFacebookAdsDaily ads : adsList)
        {
            try
            {
                if (ads.getReportDate() == null)
                {
                    throw new ServiceException("日期不能为空");
                }
                if (StringUtils.isEmpty(ads.getCampaignName()))
                {
                    throw new ServiceException("广告活动名称不能为空");
                }
                if (ads.getAccountId() == null)
                {
                    ads.setAccountId("");
                }
                YgFacebookAdsDaily exist = ygFacebookAdsDailyMapper.selectByUniqueKey(ads.getReportDate(), ads.getAccountId(), ads.getCampaignName());
                if (StringUtils.isNull(exist))
                {
                    ads.setCreateBy(operName);
                    ygFacebookAdsDailyMapper.insertYgFacebookAdsDaily(ads);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、" + ads.getReportDate() + " " + ads.getCampaignName() + " 导入成功");
                }
                else if (isUpdateSupport)
                {
                    ads.setDailyId(exist.getDailyId());
                    ads.setUpdateBy(operName);
                    ygFacebookAdsDailyMapper.updateYgFacebookAdsDaily(ads);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、" + ads.getReportDate() + " " + ads.getCampaignName() + " 更新成功");
                }
                else
                {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、" + ads.getReportDate() + " " + ads.getCampaignName() + " 已存在");
                }
            }
            catch (Exception e)
            {
                failureNum++;
                String msg = "<br/>" + failureNum + "、" + (ads.getCampaignName() == null ? "未知活动" : ads.getCampaignName()) + " 导入失败：";
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
