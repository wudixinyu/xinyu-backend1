package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YgAmazonAdsDaily;
import com.ruoyi.system.mapper.YgAmazonAdsDailyMapper;
import com.ruoyi.system.service.IYgAmazonAdsDailyService;

/**
 * 亚马逊广告活动日报Service业务层处理
 *
 * @author ruoyi
 * @date 2026-10-14
 */
@Service
public class YgAmazonAdsDailyServiceImpl implements IYgAmazonAdsDailyService
{
    private static final Logger log = LoggerFactory.getLogger(YgAmazonAdsDailyServiceImpl.class);

    @Autowired
    private YgAmazonAdsDailyMapper ygAmazonAdsDailyMapper;

    /**
     * 查询日报记录
     */
    @Override
    public YgAmazonAdsDaily selectYgAmazonAdsDailyByDailyId(Long dailyId)
    {
        return ygAmazonAdsDailyMapper.selectYgAmazonAdsDailyByDailyId(dailyId);
    }

    /**
     * 查询日报列表
     */
    @Override
    public List<YgAmazonAdsDaily> selectYgAmazonAdsDailyList(YgAmazonAdsDaily ygAmazonAdsDaily)
    {
        return ygAmazonAdsDailyMapper.selectYgAmazonAdsDailyList(ygAmazonAdsDaily);
    }

    /**
     * 新增日报记录
     */
    @Override
    public int insertYgAmazonAdsDaily(YgAmazonAdsDaily ygAmazonAdsDaily)
    {
        return ygAmazonAdsDailyMapper.insertYgAmazonAdsDaily(ygAmazonAdsDaily);
    }

    /**
     * 修改日报记录
     */
    @Override
    public int updateYgAmazonAdsDaily(YgAmazonAdsDaily ygAmazonAdsDaily)
    {
        return ygAmazonAdsDailyMapper.updateYgAmazonAdsDaily(ygAmazonAdsDaily);
    }

    /**
     * 批量删除日报记录
     */
    @Override
    public int deleteYgAmazonAdsDailyByDailyIds(Long[] dailyIds)
    {
        return ygAmazonAdsDailyMapper.deleteYgAmazonAdsDailyByDailyIds(dailyIds);
    }

    /**
     * 统计总览（单行汇总+派生指标）
     */
    @Override
    public Map<String, Object> selectAdsSummary(YgAmazonAdsDaily ygAmazonAdsDaily)
    {
        return ygAmazonAdsDailyMapper.selectAdsSummary(ygAmazonAdsDaily);
    }

    /**
     * 按日期趋势统计
     */
    @Override
    public List<Map<String, Object>> selectAdsTrend(YgAmazonAdsDaily ygAmazonAdsDaily)
    {
        return ygAmazonAdsDailyMapper.selectAdsTrend(ygAmazonAdsDaily);
    }

    /**
     * 按广告活动汇总统计
     */
    @Override
    public List<Map<String, Object>> selectAdsCampaignStats(YgAmazonAdsDaily ygAmazonAdsDaily)
    {
        return ygAmazonAdsDailyMapper.selectAdsCampaignStats(ygAmazonAdsDaily);
    }

    /**
     * 导入日报数据（按 日期+档案+活动 查重，已存在时按开关决定更新或失败）
     */
    @Override
    public String importAmazonAdsDaily(List<YgAmazonAdsDaily> adsList, Boolean isUpdateSupport, String operName)
    {
        if (StringUtils.isNull(adsList) || adsList.size() == 0)
        {
            throw new ServiceException("导入广告数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (YgAmazonAdsDaily ads : adsList)
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
                if (ads.getProfileId() == null)
                {
                    ads.setProfileId("");
                }
                YgAmazonAdsDaily exist = ygAmazonAdsDailyMapper.selectByUniqueKey(ads.getReportDate(), ads.getProfileId(), ads.getCampaignName());
                if (StringUtils.isNull(exist))
                {
                    ads.setCreateBy(operName);
                    ygAmazonAdsDailyMapper.insertYgAmazonAdsDaily(ads);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、" + ads.getReportDate() + " " + ads.getCampaignName() + " 导入成功");
                }
                else if (isUpdateSupport)
                {
                    ads.setDailyId(exist.getDailyId());
                    ads.setUpdateBy(operName);
                    ygAmazonAdsDailyMapper.updateYgAmazonAdsDaily(ads);
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
