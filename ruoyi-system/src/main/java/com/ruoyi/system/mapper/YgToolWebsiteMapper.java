package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.YgToolWebsite;

/**
 * 工具使用网站Mapper接口
 * 
 * @author ruoyi
 * @date 2026-10-16
 */
public interface YgToolWebsiteMapper 
{
    /**
     * 查询工具使用网站
     * 
     * @param toolId 工具使用网站主键
     * @return 工具使用网站
     */
    public YgToolWebsite selectYgToolWebsiteByToolId(Long toolId);

    /**
     * 查询工具使用网站列表
     * 
     * @param ygToolWebsite 工具使用网站
     * @return 工具使用网站集合
     */
    public List<YgToolWebsite> selectYgToolWebsiteList(YgToolWebsite ygToolWebsite);

    /**
     * 新增工具使用网站
     * 
     * @param ygToolWebsite 工具使用网站
     * @return 结果
     */
    public int insertYgToolWebsite(YgToolWebsite ygToolWebsite);

    /**
     * 修改工具使用网站
     * 
     * @param ygToolWebsite 工具使用网站
     * @return 结果
     */
    public int updateYgToolWebsite(YgToolWebsite ygToolWebsite);

    /**
     * 删除工具使用网站
     * 
     * @param toolId 工具使用网站主键
     * @return 结果
     */
    public int deleteYgToolWebsiteByToolId(Long toolId);

    /**
     * 批量删除工具使用网站
     * 
     * @param toolIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteYgToolWebsiteByToolIds(Long[] toolIds);
}
