package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.YgUpdateLog;

/**
 * 更新日志Service接口
 * 
 * @author ruoyi
 * @date 2026-10-14
 */
public interface IYgUpdateLogService 
{
    /**
     * 查询更新日志
     * 
     * @param logId 更新日志主键
     * @return 更新日志
     */
    public YgUpdateLog selectYgUpdateLogByLogId(Long logId);

    /**
     * 查询更新日志列表
     * 
     * @param ygUpdateLog 更新日志
     * @return 更新日志集合
     */
    public List<YgUpdateLog> selectYgUpdateLogList(YgUpdateLog ygUpdateLog);

    /**
     * 新增更新日志
     * 
     * @param ygUpdateLog 更新日志
     * @return 结果
     */
    public int insertYgUpdateLog(YgUpdateLog ygUpdateLog);

    /**
     * 修改更新日志
     * 
     * @param ygUpdateLog 更新日志
     * @return 结果
     */
    public int updateYgUpdateLog(YgUpdateLog ygUpdateLog);

    /**
     * 批量删除更新日志
     * 
     * @param logIds 需要删除的更新日志主键集合
     * @return 结果
     */
    public int deleteYgUpdateLogByLogIds(Long[] logIds);

    /**
     * 删除更新日志信息
     * 
     * @param logId 更新日志主键
     * @return 结果
     */
    public int deleteYgUpdateLogByLogId(Long logId);
}
