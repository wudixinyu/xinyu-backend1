package com.ruoyi.system.service.ygo;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YgCardInfo;

/**
 * ygocdb卡片数据位掩码解析工具
 * 
 * @author ruoyi
 * @date 2026-10-12
 */
public class YgDataUtils
{
    /** 卡图CDN前缀 */
    public static final String IMAGE_BASE_URL = "https://cdn.233.momobako.com/ygoimg/sc/";

    /** 怪兽类型位 */
    private static final int TYPE_MONSTER = 0x1;

    /** 魔法类型位 */
    private static final int TYPE_SPELL = 0x2;

    /** 陷阱类型位 */
    private static final int TYPE_TRAP = 0x4;

    /** 灵摆类型位 */
    private static final int TYPE_PENDULUM = 0x10000;

    /** 连接类型位 */
    private static final int TYPE_LINK = 0x40000000;

    /**
     * 解析ygocdb卡片JSON对象为卡片信息实体
     * 兼容cards.zip条目(名称在顶层)与单卡接口(名称在text.name)两种结构,
     * sync_source由调用方设置
     * 
     * @param card 卡片JSON对象(含id/cid/data/text等)
     * @return 卡片信息实体
     */
    public static YgCardInfo parseCard(JSONObject card)
    {
        JSONObject data = card.getJSONObject("data");
        JSONObject text = card.getJSONObject("text");
        if (data == null)
        {
            data = new JSONObject();
        }
        if (text == null)
        {
            text = new JSONObject();
        }
        YgCardInfo info = new YgCardInfo();
        info.setCardId(card.getLong("id"));
        info.setCid(card.getLong("cid"));
        // 名称字段:cards.zip条目在顶层,单卡接口在text内,缺失填空串
        info.setCnName(defaultStr(firstNonEmpty(card.getString("cn_name"), text.getString("name"))));
        info.setScName(defaultStr(firstNonEmpty(card.getString("sc_name"), text.getString("sc_name"))));
        info.setMdName(defaultStr(firstNonEmpty(card.getString("md_name"), text.getString("md_name"))));
        info.setJpName(defaultStr(firstNonEmpty(card.getString("jp_name"), text.getString("jp_name"))));
        info.setJpRuby(defaultStr(card.getString("jp_ruby")));
        info.setEnName(defaultStr(firstNonEmpty(card.getString("en_name"), text.getString("en_name"))));
        // 文本描述
        info.setTypesDesc(defaultStr(text.getString("types")));
        info.setEffectDesc(defaultStr(text.getString("desc")));
        info.setPendulumDesc(defaultStr(text.getString("pdesc")));
        // 位掩码原值存储
        int ot = data.getIntValue("ot", 0);
        info.setOt(ot);
        long setcode = data.getLongValue("setcode", 0L);
        info.setSetcode(setcode);
        int type = data.getIntValue("type", 0);
        info.setType(type);
        info.setAttribute(data.getIntValue("attribute", 0));
        info.setRace(data.getIntValue("race", 0));
        info.setAtk(data.getIntValue("atk", -2));
        int def = data.getIntValue("def", -2);
        info.setDef(def);
        int levelRaw = data.getIntValue("level", 0);
        info.setLevelRaw(levelRaw);
        // 卡片大类:由type低位解析
        int cardKind = 0;
        if ((type & TYPE_MONSTER) != 0)
        {
            cardKind = 1;
        }
        else if ((type & TYPE_SPELL) != 0)
        {
            cardKind = 2;
        }
        else if ((type & TYPE_TRAP) != 0)
        {
            cardKind = 3;
        }
        info.setCardKind(cardKind);
        // 等级/阶级:低8位
        info.setCardLevel(levelRaw & 0xFF);
        // 灵摆:bits16-23为左刻度,bits24-31为右刻度
        if ((type & TYPE_PENDULUM) != 0)
        {
            info.setLscale((levelRaw >> 16) & 0xFF);
            info.setRscale((levelRaw >> 24) & 0xFF);
        }
        // 连接:def字段存link_marker位掩码
        if ((type & TYPE_LINK) != 0)
        {
            info.setLinkMarker(def);
        }
        // 卡图地址
        info.setImageUrl(IMAGE_BASE_URL + info.getCardId() + ".webp");
        info.setSyncTime(DateUtils.getNowDate());
        return info;
    }

    /**
     * 取第一个非空字符串
     */
    private static String firstNonEmpty(String a, String b)
    {
        return StringUtils.isNotEmpty(a) ? a : b;
    }

    /**
     * null转空串
     */
    private static String defaultStr(String value)
    {
        return value == null ? "" : value;
    }
}
