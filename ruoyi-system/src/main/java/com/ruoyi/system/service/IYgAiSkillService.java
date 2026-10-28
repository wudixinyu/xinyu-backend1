package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.YgAiSkill;

/**
 * AI技能Service接口
 * 
 * @author ruoyi
 * @date 2026-10-14
 */
public interface IYgAiSkillService 
{
    /**
     * 查询技能
     * 
     * @param skillId 技能主键
     * @return 技能
     */
    public YgAiSkill selectYgAiSkillBySkillId(Long skillId);

    /**
     * 校验技能标识是否唯一
     * 
     * @param ygAiSkill 技能
     * @return 结果
     */
    public boolean checkSkillCodeUnique(YgAiSkill ygAiSkill);

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
     * 批量删除技能
     * 
     * @param skillIds 需要删除的技能主键集合
     * @return 结果
     */
    public int deleteYgAiSkillBySkillIds(Long[] skillIds);

    /**
     * 删除技能信息
     * 
     * @param skillId 技能主键
     * @return 结果
     */
    public int deleteYgAiSkillBySkillId(Long skillId);
}
