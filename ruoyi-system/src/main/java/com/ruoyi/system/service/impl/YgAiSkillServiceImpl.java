package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YgAiSkill;
import com.ruoyi.system.mapper.YgAiSkillMapper;
import com.ruoyi.system.service.IYgAiSkillService;

/**
 * AI技能Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-10-14
 */
@Service
public class YgAiSkillServiceImpl implements IYgAiSkillService 
{
    @Autowired
    private YgAiSkillMapper ygAiSkillMapper;

    /**
     * 查询技能
     * 
     * @param skillId 技能主键
     * @return 技能
     */
    @Override
    public YgAiSkill selectYgAiSkillBySkillId(Long skillId)
    {
        return ygAiSkillMapper.selectYgAiSkillBySkillId(skillId);
    }

    /**
     * 校验技能标识是否唯一
     * 
     * @param ygAiSkill 技能
     * @return 结果
     */
    @Override
    public boolean checkSkillCodeUnique(YgAiSkill ygAiSkill)
    {
        Long skillId = StringUtils.isNull(ygAiSkill.getSkillId()) ? -1L : ygAiSkill.getSkillId();
        YgAiSkill info = ygAiSkillMapper.selectYgAiSkillBySkillCode(ygAiSkill.getSkillCode());
        if (StringUtils.isNotNull(info) && info.getSkillId().longValue() != skillId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 查询技能列表
     * 
     * @param ygAiSkill 技能
     * @return 技能
     */
    @Override
    public List<YgAiSkill> selectYgAiSkillList(YgAiSkill ygAiSkill)
    {
        return ygAiSkillMapper.selectYgAiSkillList(ygAiSkill);
    }

    /**
     * 新增技能
     * 
     * @param ygAiSkill 技能
     * @return 结果
     */
    @Override
    public int insertYgAiSkill(YgAiSkill ygAiSkill)
    {
        ygAiSkill.setCreateTime(DateUtils.getNowDate());
        return ygAiSkillMapper.insertYgAiSkill(ygAiSkill);
    }

    /**
     * 修改技能
     * 
     * @param ygAiSkill 技能
     * @return 结果
     */
    @Override
    public int updateYgAiSkill(YgAiSkill ygAiSkill)
    {
        ygAiSkill.setUpdateTime(DateUtils.getNowDate());
        return ygAiSkillMapper.updateYgAiSkill(ygAiSkill);
    }

    /**
     * 批量删除技能
     * 
     * @param skillIds 需要删除的技能主键
     * @return 结果
     */
    @Override
    public int deleteYgAiSkillBySkillIds(Long[] skillIds)
    {
        return ygAiSkillMapper.deleteYgAiSkillBySkillIds(skillIds);
    }

    /**
     * 删除技能信息
     * 
     * @param skillId 技能主键
     * @return 结果
     */
    @Override
    public int deleteYgAiSkillBySkillId(Long skillId)
    {
        return ygAiSkillMapper.deleteYgAiSkillBySkillId(skillId);
    }
}
