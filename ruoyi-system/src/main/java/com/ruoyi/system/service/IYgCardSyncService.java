package com.ruoyi.system.service;

import java.util.Map;

/**
 * ygocdb卡片数据同步Service接口
 * 
 * @author ruoyi
 * @date 2026-10-12
 */
public interface IYgCardSyncService
{
    /**
     * 全量同步卡片数据(cards.zip)
     * 
     * @param force 是否强制同步(忽略md5比对)
     * @return 同步结果(条数/md5/消息)
     */
    public Map<String, Object> syncAllCards(boolean force);

    /**
     * 按需同步单张卡片(/card/{id})
     * 
     * @param cardId 卡片密码(id)
     * @return 影响行数,接口无此卡返回0
     */
    public int syncSingleCard(long cardId);

    /**
     * 收集卡片系列(setcode拆分)
     * 
     * @return 系列条数
     */
    public int syncCardSets();

    /**
     * 同步禁限卡表(/limits.json)
     * 
     * @return 各语种条数与跳过数
     */
    public Map<String, Object> syncLimitLists();
}
