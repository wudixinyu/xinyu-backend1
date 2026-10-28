package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 卡片信息对象 yg_card_info
 * 
 * @author ruoyi
 * @date 2026-10-12
 */
public class YgCardInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 卡片密码(id) */
    private Long cardId;

    /** 官方数据库标识(cid) */
    @Excel(name = "官方数据库标识(cid)")
    private Long cid;

    /** 中文名称(YGOPro译名) */
    @Excel(name = "中文名称(YGOPro译名)")
    private String cnName;

    /** 官方简体中文名称 */
    @Excel(name = "官方简体中文名称")
    private String scName;

    /** Master Duel中文名称 */
    @Excel(name = "Master Duel中文名称")
    private String mdName;

    /** 官方日文名称 */
    @Excel(name = "官方日文名称")
    private String jpName;

    /** 日文假名注音 */
    @Excel(name = "日文假名注音")
    private String jpRuby;

    /** 官方英文名称 */
    @Excel(name = "官方英文名称")
    private String enName;

    /** 可用环境位掩码(1:OCG 2:TCG 4:动画 8: illegal 16:视频 32:非法 64:仅OCG独有 128:仅TCG独有 等) */
    @Excel(name = "可用环境位掩码")
    private Integer ot;

    /** 卡组系列code位掩码(64bit,关联yg_card_set) */
    @Excel(name = "卡组系列code位掩码")
    private Long setcode;

    /** 卡片类型位掩码(1:怪兽 2:魔法 4:陷阱 16:通常 32:效果 ...) */
    @Excel(name = "卡片类型位掩码")
    private Integer type;

    /** 卡片大类(1:怪兽 2:魔法 3:陷阱,由type解析) */
    @Excel(name = "卡片大类")
    private Integer cardKind;

    /** 属性位掩码(1:地 2:水 4:炎 8:风 16:光 32:暗 64:神) */
    @Excel(name = "属性位掩码")
    private Integer attribute;

    /** 种族位掩码(龙/战士/魔法使者等) */
    @Excel(name = "种族位掩码")
    private Integer race;

    /** 攻击力(-2:?) */
    @Excel(name = "攻击力")
    private Integer atk;

    /** 防御力(连接怪兽该字段存link_marker位掩码) */
    @Excel(name = "防御力")
    private Integer def;

    /** 等级原始值(灵摆卡高16位含刻度) */
    @Excel(name = "等级原始值")
    private Integer levelRaw;

    /** 等级/阶级(Rank<<8解析) */
    @Excel(name = "等级/阶级")
    private Integer cardLevel;

    /** 灵摆左刻度(非灵摆为NULL) */
    @Excel(name = "灵摆左刻度")
    private Integer lscale;

    /** 灵摆右刻度(非灵摆为NULL) */
    @Excel(name = "灵摆右刻度")
    private Integer rscale;

    /** 连接标记位掩码(非连接怪兽为NULL) */
    @Excel(name = "连接标记位掩码")
    private Integer linkMarker;

    /** 类型描述文本(text.types) */
    @Excel(name = "类型描述文本")
    private String typesDesc;

    /** 效果文本(text.desc) */
    @Excel(name = "效果文本")
    private String effectDesc;

    /** 灵摆效果文本(text.pdesc) */
    @Excel(name = "灵摆效果文本")
    private String pendulumDesc;

    /** 卡图地址(CDN相对路径,前端拼接!half等参数) */
    @Excel(name = "卡图地址")
    private String imageUrl;

    /** 同步来源(1:全量导入 2:按需补录) */
    @Excel(name = "同步来源")
    private Integer syncSource;

    /** 最后同步时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "最后同步时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date syncTime;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    /** 用户名称（查询参数,模糊匹配中文名称） */
    private String userName;

    public void setCardId(Long cardId)
    {
        this.cardId = cardId;
    }

    public Long getCardId() 
    {
        return cardId;
    }

    public void setCid(Long cid) 
    {
        this.cid = cid;
    }

    public Long getCid() 
    {
        return cid;
    }

    public void setCnName(String cnName) 
    {
        this.cnName = cnName;
    }

    public String getCnName() 
    {
        return cnName;
    }

    public void setScName(String scName) 
    {
        this.scName = scName;
    }

    public String getScName() 
    {
        return scName;
    }

    public void setMdName(String mdName) 
    {
        this.mdName = mdName;
    }

    public String getMdName() 
    {
        return mdName;
    }

    public void setJpName(String jpName) 
    {
        this.jpName = jpName;
    }

    public String getJpName() 
    {
        return jpName;
    }

    public void setJpRuby(String jpRuby) 
    {
        this.jpRuby = jpRuby;
    }

    public String getJpRuby() 
    {
        return jpRuby;
    }

    public void setEnName(String enName) 
    {
        this.enName = enName;
    }

    public String getEnName() 
    {
        return enName;
    }

    public void setOt(Integer ot) 
    {
        this.ot = ot;
    }

    public Integer getOt() 
    {
        return ot;
    }

    public void setSetcode(Long setcode) 
    {
        this.setcode = setcode;
    }

    public Long getSetcode() 
    {
        return setcode;
    }

    public void setType(Integer type) 
    {
        this.type = type;
    }

    public Integer getType() 
    {
        return type;
    }

    public void setCardKind(Integer cardKind) 
    {
        this.cardKind = cardKind;
    }

    public Integer getCardKind() 
    {
        return cardKind;
    }

    public void setAttribute(Integer attribute) 
    {
        this.attribute = attribute;
    }

    public Integer getAttribute() 
    {
        return attribute;
    }

    public void setRace(Integer race) 
    {
        this.race = race;
    }

    public Integer getRace() 
    {
        return race;
    }

    public void setAtk(Integer atk) 
    {
        this.atk = atk;
    }

    public Integer getAtk() 
    {
        return atk;
    }

    public void setDef(Integer def) 
    {
        this.def = def;
    }

    public Integer getDef() 
    {
        return def;
    }

    public void setLevelRaw(Integer levelRaw) 
    {
        this.levelRaw = levelRaw;
    }

    public Integer getLevelRaw() 
    {
        return levelRaw;
    }

    public void setCardLevel(Integer cardLevel) 
    {
        this.cardLevel = cardLevel;
    }

    public Integer getCardLevel() 
    {
        return cardLevel;
    }

    public void setLscale(Integer lscale) 
    {
        this.lscale = lscale;
    }

    public Integer getLscale() 
    {
        return lscale;
    }

    public void setRscale(Integer rscale) 
    {
        this.rscale = rscale;
    }

    public Integer getRscale() 
    {
        return rscale;
    }

    public void setLinkMarker(Integer linkMarker) 
    {
        this.linkMarker = linkMarker;
    }

    public Integer getLinkMarker() 
    {
        return linkMarker;
    }

    public void setTypesDesc(String typesDesc) 
    {
        this.typesDesc = typesDesc;
    }

    public String getTypesDesc() 
    {
        return typesDesc;
    }

    public void setEffectDesc(String effectDesc) 
    {
        this.effectDesc = effectDesc;
    }

    public String getEffectDesc() 
    {
        return effectDesc;
    }

    public void setPendulumDesc(String pendulumDesc) 
    {
        this.pendulumDesc = pendulumDesc;
    }

    public String getPendulumDesc() 
    {
        return pendulumDesc;
    }

    public void setImageUrl(String imageUrl) 
    {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() 
    {
        return imageUrl;
    }

    public void setSyncSource(Integer syncSource) 
    {
        this.syncSource = syncSource;
    }

    public Integer getSyncSource() 
    {
        return syncSource;
    }

    public void setSyncTime(Date syncTime) 
    {
        this.syncTime = syncTime;
    }

    public Date getSyncTime() 
    {
        return syncTime;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getUserName()
    {
        return userName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("cardId", getCardId())
            .append("cid", getCid())
            .append("cnName", getCnName())
            .append("scName", getScName())
            .append("mdName", getMdName())
            .append("jpName", getJpName())
            .append("jpRuby", getJpRuby())
            .append("enName", getEnName())
            .append("ot", getOt())
            .append("setcode", getSetcode())
            .append("type", getType())
            .append("cardKind", getCardKind())
            .append("attribute", getAttribute())
            .append("race", getRace())
            .append("atk", getAtk())
            .append("def", getDef())
            .append("levelRaw", getLevelRaw())
            .append("cardLevel", getCardLevel())
            .append("lscale", getLscale())
            .append("rscale", getRscale())
            .append("linkMarker", getLinkMarker())
            .append("typesDesc", getTypesDesc())
            .append("effectDesc", getEffectDesc())
            .append("pendulumDesc", getPendulumDesc())
            .append("imageUrl", getImageUrl())
            .append("syncSource", getSyncSource())
            .append("syncTime", getSyncTime())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
