package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.YgAiSkill;

/**
 * AI技能Mapper接口
 * 
 * @author ruoyi
 * @date 2026-10-14
 */
public interface YgAiSkillMapper 
{
    /**
     * 查询技能
     * 
     * @param skillId 技能主键
     * @return 技能
     */
    public YgAiSkill selectYgAiSkillBySkillId(Long skillId);

    /**
     * 按技能标识查询（用于唯一性校验）
     * 
     * @param skillCode 技能标识
     * @return 技能
     */
    public YgAiSkill selectYgAiSkillBySkillCode(String skillCode);

    /**
     * 查询技能列表（不含 content 大字段）
     * 
     * @param ygAiSkill 技能
     * @return 技能集合
     */
    public List<YgAiSkill> selectYgAiSkillList(YgAiSkill ygAiSkill);

    /**
     * 新增技能
     * 
     * @param ygAiSkill 技能
     * @return 结果
     */
    public int insertYgAiSkill(YgAiSkill ygAiSkill);

    /**
     * 修改技能
     * 
     * @param ygAiSkill 技能
     * @return 结果
     */
    public int updateYgAiSkill(YgAiSkill ygAiSkill);

    /**
     * 删除技能
     * 
     * @param skillId 技能主键
     * @return 结果
     */
    public int deleteYgAiSkillBySkillId(Long skillId);

    /**
     * 批量删除技能
     * 
     * @param skillIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteYgAiSkillBySkillIds(Long[] skillIds);
}
