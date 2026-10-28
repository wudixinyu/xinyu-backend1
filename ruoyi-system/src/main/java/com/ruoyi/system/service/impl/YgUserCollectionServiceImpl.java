package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.YgUserCollectionMapper;
import com.ruoyi.system.domain.YgUserCollection;
import com.ruoyi.system.service.IYgUserCollectionService;

/**
 * 用户收藏 服务层实现
 * 
 * @author ruoyi
 * @date 2026-10-12
 */
@Service
public class YgUserCollectionServiceImpl implements IYgUserCollectionService
{
    @Autowired
    private YgUserCollectionMapper ygUserCollectionMapper;

    /**
     * 查询用户收藏列表
     * 
     * @param ygUserCollection 用户收藏信息
     * @return 用户收藏
     */
    @Override
    public List<YgUserCollection> selectYgUserCollectionList(YgUserCollection ygUserCollection)
    {
        return ygUserCollectionMapper.selectYgUserCollectionList(ygUserCollection);
    }

    /**
     * 通过收藏主键查询用户收藏
     * 
     * @param collectId 收藏主键
     * @return 用户收藏
     */
    @Override
    public YgUserCollection selectYgUserCollectionByCollectId(Long collectId)
    {
        return ygUserCollectionMapper.selectYgUserCollectionByCollectId(collectId);
    }

    /**
     * 查询用户收藏的卡片密码集合
     * 
     * @param userId 用户ID
     * @return 卡片密码集合
     */
    @Override
    public List<Long> selectCardIdsByUserId(Long userId)
    {
        return ygUserCollectionMapper.selectCardIdsByUserId(userId);
    }

    /**
     * 切换用户对某卡片的收藏状态(已收藏则取消,未收藏则新增)
     * 
     * @param userId 用户ID
     * @param cardId 卡片密码
     * @return true 表示切换后为已收藏,false 表示已取消收藏
     */
    @Override
    public boolean toggleCollect(Long userId, Long cardId)
    {
        YgUserCollection query = new YgUserCollection();
        query.setUserId(userId);
        query.setCardId(cardId);
        YgUserCollection exist = ygUserCollectionMapper.selectYgUserCollectionByUserAndCard(query);
        if (exist != null)
        {
            ygUserCollectionMapper.deleteYgUserCollectionByCollectId(exist.getCollectId());
            return false;
        }
        YgUserCollection collect = new YgUserCollection();
        collect.setUserId(userId);
        collect.setCardId(cardId);
        collect.setQuantity(1);
        ygUserCollectionMapper.insertYgUserCollection(collect);
        return true;
    }

    /**
     * 新增用户收藏(已存在则累加数量,上限3)
     * 
     * @param ygUserCollection 用户收藏信息
     * @return 结果
     */
    @Override
    public int insertYgUserCollection(YgUserCollection ygUserCollection)
    {
        YgUserCollection exist = ygUserCollectionMapper.selectYgUserCollectionByUserAndCard(ygUserCollection);
        if (exist != null)
        {
            int quantity = (exist.getQuantity() == null ? 0 : exist.getQuantity()) + (ygUserCollection.getQuantity() == null ? 1 : ygUserCollection.getQuantity());
            exist.setQuantity(Math.min(quantity, 3));
            return ygUserCollectionMapper.updateYgUserCollection(exist);
        }
        if (ygUserCollection.getQuantity() == null)
        {
            ygUserCollection.setQuantity(1);
        }
        ygUserCollection.setQuantity(Math.min(ygUserCollection.getQuantity(), 3));
        return ygUserCollectionMapper.insertYgUserCollection(ygUserCollection);
    }

    /**
     * 通过收藏主键删除用户收藏
     * 
     * @param collectId 收藏主键
     * @return 结果
     */
    @Override
    public int deleteYgUserCollectionByCollectId(Long collectId)
    {
        return ygUserCollectionMapper.deleteYgUserCollectionByCollectId(collectId);
    }

    /**
     * 批量删除用户收藏信息
     * 
     * @param collectIds 需要删除的用户收藏主键集合
     * @return 结果
     */
    @Override
    public int deleteYgUserCollectionByCollectIds(Long[] collectIds)
    {
        return ygUserCollectionMapper.deleteYgUserCollectionByCollectIds(collectIds);
    }
}
