package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.YgKbCustomApi;

/**
 * 知识库自定义API配置Mapper接口
 *
 * @author ruoyi
 * @date 2026-10-16
 */
public interface YgKbCustomApiMapper
{
    /**
     * 查询自定义API列表
     */
    public List<YgKbCustomApi> selectYgKbCustomApiList(YgKbCustomApi ygKbCustomApi);

    /**
     * 根据ID查询自定义API
     */
    public YgKbCustomApi selectYgKbCustomApiById(Long id);

    /**
     * 新增自定义API
     */
    public int insertYgKbCustomApi(YgKbCustomApi ygKbCustomApi);

    /**
     * 修改自定义API
     */
    public int updateYgKbCustomApi(YgKbCustomApi ygKbCustomApi);

    /**
     * 批量删除自定义API
     */
    public int deleteYgKbCustomApiByIds(Long[] ids);

    /**
     * 统计该自定义API被模型配置引用的次数（对话/向量化）
     */
    public int countUsage(@Param("id") Long id);
}
