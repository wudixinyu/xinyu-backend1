package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.YgUserCollection;

/**
 * 用户收藏 服务层
 * 
 * @author ruoyi
 * @date 2026-10-12
 */
public interface IYgUserCollectionService
{
    /**
     * 查询用户收藏列表
     * 
     * @param ygUserCollection 用户收藏信息
     * @return 用户收藏集合
     */
    public List<YgUserCollection> selectYgUserCollectionList(YgUserCollection ygUserCollection);

    /**
     * 通过收藏主键查询用户收藏
     * 
     * @param collectId 收藏主键
     * @return 用户收藏信息
     */
    public YgUserCollection selectYgUserCollectionByCollectId(Long collectId);

    /**
     * 查询用户收藏的卡片密码集合
     * 
     * @param userId 用户ID
     * @return 卡片密码集合
     */
    public List<Long> selectCardIdsByUserId(Long userId);

    /**
     * 切换用户对某卡片的收藏状态
     * 
     * @param userId 用户ID
     * @param cardId 卡片密码
     * @return true 表示切换后为已收藏,false 表示已取消收藏
     */
    public boolean toggleCollect(Long userId, Long cardId);

    /**
     * 新增用户收藏(已存在则累加数量,上限3)
     * 
     * @param ygUserCollection 用户收藏信息
     * @return 结果
     */
    public int insertYgUserCollection(YgUserCollection ygUserCollection);

    /**
     * 通过收藏主键删除用户收藏
     * 
     * @param collectId 收藏主键
     * @return 结果
     */
    public int deleteYgUserCollectionByCollectId(Long collectId);

    /**
     * 批量删除用户收藏信息
     * 
     * @param collectIds 需要删除的用户收藏主键集合
     * @return 结果
     */
    public int deleteYgUserCollectionByCollectIds(Long[] collectIds);
}
