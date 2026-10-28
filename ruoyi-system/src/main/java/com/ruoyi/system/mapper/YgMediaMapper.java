package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.YgMedia;

/**
 * AI素材库Mapper接口
 * 
 * @author ruoyi
 * @date 2026-10-14
 */
public interface YgMediaMapper 
{
    /**
     * 查询AI素材库
     * 
     * @param mediaId AI素材库主键
     * @return AI素材库
     */
    public YgMedia selectYgMediaByMediaId(Long mediaId);

    /**
     * 查询AI素材库列表
     * 
     * @param ygMedia AI素材库
     * @return AI素材库集合
     */
    public List<YgMedia> selectYgMediaList(YgMedia ygMedia);

    /**
     * 新增AI素材库
     * 
     * @param ygMedia AI素材库
     * @return 结果
     */
    public int insertYgMedia(YgMedia ygMedia);

    /**
     * 修改AI素材库
     * 
     * @param ygMedia AI素材库
     * @return 结果
     */
    public int updateYgMedia(YgMedia ygMedia);

    /**
     * 删除AI素材库
     * 
     * @param mediaId AI素材库主键
     * @return 结果
     */
    public int deleteYgMediaByMediaId(Long mediaId);

    /**
     * 批量删除AI素材库
     * 
     * @param mediaIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteYgMediaByMediaIds(Long[] mediaIds);
}
