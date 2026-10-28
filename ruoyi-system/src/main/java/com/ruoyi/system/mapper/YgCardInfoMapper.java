package com.ruoyi.system.mapper;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.YgCardInfo;

/**
 * 卡片信息Mapper接口
 * 
 * @author ruoyi
 * @date 2026-10-12
 */
public interface YgCardInfoMapper 
{
    /**
     * 查询卡片信息
     * 
     * @param cardId 卡片信息主键
     * @return 卡片信息
     */
    public YgCardInfo selectYgCardInfoByCardId(Long cardId);

    /**
     * 查询卡片信息列表
     * 
     * @param ygCardInfo 卡片信息
     * @return 卡片信息集合
     */
    public List<YgCardInfo> selectYgCardInfoList(YgCardInfo ygCardInfo);

    /**
     * 新增卡片信息
     * 
     * @param ygCardInfo 卡片信息
     * @return 结果
     */
    public int insertYgCardInfo(YgCardInfo ygCardInfo);

    /**
     * 修改卡片信息
     * 
     * @param ygCardInfo 卡片信息
     * @return 结果
     */
    public int updateYgCardInfo(YgCardInfo ygCardInfo);

    /**
     * 删除卡片信息
     * 
     * @param cardId 卡片信息主键
     * @return 结果
     */
    public int deleteYgCardInfoByCardId(Long cardId);

    /**
     * 批量删除卡片信息
     * 
     * @param cardIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteYgCardInfoByCardIds(Long[] cardIds);

    /**
     * 批量新增或更新卡片信息(主键card_id冲突时全列更新)
     * 
     * @param ygCardInfos 卡片信息集合
     * @return 结果
     */
    public int batchUpsertYgCardInfo(List<YgCardInfo> ygCardInfos);

    /**
     * 查询全表去重后的卡组系列code位掩码
     * 
     * @return setcode集合
     */
    public List<Long> selectDistinctSetcodes();

    /**
     * 查询cid与card_id映射(禁限卡表按cid匹配用)
     * 
     * @return cid/card_id集合
     */
    public List<Map<String, Object>> selectCidCardIdMap();
}
