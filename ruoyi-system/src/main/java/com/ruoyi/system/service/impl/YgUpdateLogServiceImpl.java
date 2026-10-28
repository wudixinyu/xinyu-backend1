package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.YgUpdateLog;
import com.ruoyi.system.mapper.YgUpdateLogMapper;
import com.ruoyi.system.service.IYgUpdateLogService;

/**
 * 更新日志Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-10-14
 */
@Service
public class YgUpdateLogServiceImpl implements IYgUpdateLogService 
{
    @Autowired
    private YgUpdateLogMapper ygUpdateLogMapper;

    /**
     * 查询更新日志
     * 
     * @param logId 更新日志主键
     * @return 更新日志
     */
    @Override
    public YgUpdateLog selectYgUpdateLogByLogId(Long logId)
    {
        return ygUpdateLogMapper.selectYgUpdateLogByLogId(logId);
    }

    /**
     * 查询更新日志列表
     * 
     * @param ygUpdateLog 更新日志
     * @return 更新日志
     */
    @Override
    public List<YgUpdateLog> selectYgUpdateLogList(YgUpdateLog ygUpdateLog)
    {
        return ygUpdateLogMapper.selectYgUpdateLogList(ygUpdateLog);
    }

    /**
     * 新增更新日志
     * 
     * @param ygUpdateLog 更新日志
     * @return 结果
     */
    @Override
    public int insertYgUpdateLog(YgUpdateLog ygUpdateLog)
    {
        ygUpdateLog.setCreateTime(DateUtils.getNowDate());
        return ygUpdateLogMapper.insertYgUpdateLog(ygUpdateLog);
    }

    /**
     * 修改更新日志
     * 
     * @param ygUpdateLog 更新日志
     * @return 结果
     */
    @Override
    public int updateYgUpdateLog(YgUpdateLog ygUpdateLog)
    {
        ygUpdateLog.setUpdateTime(DateUtils.getNowDate());
        return ygUpdateLogMapper.updateYgUpdateLog(ygUpdateLog);
    }

    /**
     * 批量删除更新日志
     * 
     * @param logIds 需要删除的更新日志主键
     * @return 结果
     */
    @Override
    public int deleteYgUpdateLogByLogIds(Long[] logIds)
    {
        return ygUpdateLogMapper.deleteYgUpdateLogByLogIds(logIds);
    }

    /**
     * 删除更新日志信息
     * 
     * @param logId 更新日志主键
     * @return 结果
     */
    @Override
    public int deleteYgUpdateLogByLogId(Long logId)
    {
        return ygUpdateLogMapper.deleteYgUpdateLogByLogId(logId);
    }
}
