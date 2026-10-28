package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.YgUserCollection;

/**
 * 用户收藏 数据层
 * 
 * @author ruoyi
 * @date 2026-10-12
 */
public interface YgUserCollectionMapper
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
     * 通过用户和卡片查询收藏
     * 
     * @param ygUserCollection 用户收藏信息(userId/cardId)
     * @return 用户收藏信息
     */
    public YgUserCollection selectYgUserCollectionByUserAndCard(YgUserCollection ygUserCollection);

    /**
     * 查询用户收藏的卡片密码集合
     * 
     * @param userId 用户ID
     * @return 卡片密码集合
     */
    public List<Long> selectCardIdsByUserId(Long userId);

    /**
     * 新增用户收藏
     * 
     * @param ygUserCollection 用户收藏信息
     * @return 结果
     */
    public int insertYgUserCollection(YgUserCollection ygUserCollection);

    /**
     * 修改用户收藏
     * 
     * @param ygUserCollection 用户收藏信息
     * @return 结果
     */
    public int updateYgUserCollection(YgUserCollection ygUserCollection);

    /**
     * 通过收藏主键删除用户收藏
     * 
     * @param collectId 收藏主键
     * @return 结果
     */
    public int deleteYgUserCollectionByCollectId(Long collectId);

    /**
     * 批量删除用户收藏
     * 
     * @param collectIds 需要删除的用户收藏主键集合
     * @return 结果
     */
    public int deleteYgUserCollectionByCollectIds(Long[] collectIds);
}
