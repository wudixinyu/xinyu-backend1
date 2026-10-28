package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.YgMedia;
import com.ruoyi.system.mapper.YgMediaMapper;
import com.ruoyi.system.service.IYgMediaService;

/**
 * AI素材库Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-10-14
 */
@Service
public class YgMediaServiceImpl implements IYgMediaService 
{
    @Autowired
    private YgMediaMapper ygMediaMapper;

    /**
     * 查询AI素材库
     * 
     * @param mediaId AI素材库主键
     * @return AI素材库
     */
    @Override
    public YgMedia selectYgMediaByMediaId(Long mediaId)
    {
        return ygMediaMapper.selectYgMediaByMediaId(mediaId);
    }

    /**
     * 查询AI素材库列表
     * 
     * @param ygMedia AI素材库
     * @return AI素材库
     */
    @Override
    public List<YgMedia> selectYgMediaList(YgMedia ygMedia)
    {
        return ygMediaMapper.selectYgMediaList(ygMedia);
    }

    /**
     * 新增AI素材库
     * 
     * @param ygMedia AI素材库
     * @return 结果
     */
    @Override
    public int insertYgMedia(YgMedia ygMedia)
    {
        ygMedia.setCreateTime(DateUtils.getNowDate());
        return ygMediaMapper.insertYgMedia(ygMedia);
    }

    /**
     * 修改AI素材库
     * 
     * @param ygMedia AI素材库
     * @return 结果
     */
    @Override
    public int updateYgMedia(YgMedia ygMedia)
    {
        ygMedia.setUpdateTime(DateUtils.getNowDate());
        return ygMediaMapper.updateYgMedia(ygMedia);
    }

    /**
     * 批量删除AI素材库
     * 
     * @param mediaIds 需要删除的AI素材库主键
     * @return 结果
     */
    @Override
    public int deleteYgMediaByMediaIds(Long[] mediaIds)
    {
        return ygMediaMapper.deleteYgMediaByMediaIds(mediaIds);
    }

    /**
     * 删除AI素材库信息
     * 
     * @param mediaId AI素材库主键
     * @return 结果
     */
    @Override
    public int deleteYgMediaByMediaId(Long mediaId)
    {
        return ygMediaMapper.deleteYgMediaByMediaId(mediaId);
    }
}
