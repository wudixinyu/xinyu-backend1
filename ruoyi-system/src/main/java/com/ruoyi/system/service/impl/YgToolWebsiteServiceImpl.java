package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.YgToolWebsite;
import com.ruoyi.system.mapper.YgToolWebsiteMapper;
import com.ruoyi.system.service.IYgToolWebsiteService;

/**
 * 工具使用网站Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-10-16
 */
@Service
public class YgToolWebsiteServiceImpl implements IYgToolWebsiteService 
{
    @Autowired
    private YgToolWebsiteMapper ygToolWebsiteMapper;

    /**
     * 查询工具使用网站
     * 
     * @param toolId 工具使用网站主键
     * @return 工具使用网站
     */
    @Override
    public YgToolWebsite selectYgToolWebsiteByToolId(Long toolId)
    {
        return ygToolWebsiteMapper.selectYgToolWebsiteByToolId(toolId);
    }

    /**
     * 查询工具使用网站列表
     * 
     * @param ygToolWebsite 工具使用网站
     * @return 工具使用网站
     */
    @Override
    public List<YgToolWebsite> selectYgToolWebsiteList(YgToolWebsite ygToolWebsite)
    {
        return ygToolWebsiteMapper.selectYgToolWebsiteList(ygToolWebsite);
    }

    /**
     * 新增工具使用网站
     * 
     * @param ygToolWebsite 工具使用网站
     * @return 结果
     */
    @Override
    public int insertYgToolWebsite(YgToolWebsite ygToolWebsite)
    {
        ygToolWebsite.setCreateTime(DateUtils.getNowDate());
        return ygToolWebsiteMapper.insertYgToolWebsite(ygToolWebsite);
    }

    /**
     * 修改工具使用网站
     * 
     * @param ygToolWebsite 工具使用网站
     * @return 结果
     */
    @Override
    public int updateYgToolWebsite(YgToolWebsite ygToolWebsite)
    {
        ygToolWebsite.setUpdateTime(DateUtils.getNowDate());
        return ygToolWebsiteMapper.updateYgToolWebsite(ygToolWebsite);
    }

    /**
     * 批量删除工具使用网站
     * 
     * @param toolIds 需要删除的工具使用网站主键
     * @return 结果
     */
    @Override
    public int deleteYgToolWebsiteByToolIds(Long[] toolIds)
    {
        return ygToolWebsiteMapper.deleteYgToolWebsiteByToolIds(toolIds);
    }

    /**
     * 删除工具使用网站信息
     * 
     * @param toolId 工具使用网站主键
     * @return 结果
     */
    @Override
    public int deleteYgToolWebsiteByToolId(Long toolId)
    {
        return ygToolWebsiteMapper.deleteYgToolWebsiteByToolId(toolId);
    }
}
