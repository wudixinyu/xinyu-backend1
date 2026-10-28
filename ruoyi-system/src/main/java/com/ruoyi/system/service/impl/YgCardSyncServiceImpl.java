package com.ruoyi.system.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YgCardInfo;
import com.ruoyi.system.domain.YgCardSet;
import com.ruoyi.system.domain.YgLimitList;
import com.ruoyi.system.mapper.YgCardInfoMapper;
import com.ruoyi.system.mapper.YgCardSetMapper;
import com.ruoyi.system.mapper.YgLimitListMapper;
import com.ruoyi.system.service.IYgCardSyncService;
import com.ruoyi.system.service.ygo.YgDataUtils;
import com.ruoyi.system.service.ygo.YgocdbClient;

/**
 * ygocdb卡片数据同步 服务层处理
 * 
 * @author ruoyi
 * @date 2026-10-12
 */
@Service
public class YgCardSyncServiceImpl implements IYgCardSyncService
{
    private static final Logger log = LoggerFactory.getLogger(YgCardSyncServiceImpl.class);

    /** cards.zip的md5缓存key */
    private static final String CARDS_MD5_KEY = "yg:cards:md5";

    /** 批量入库分片大小 */
    private static final int BATCH_SIZE = 500;

    @Autowired
    private YgocdbClient ygocdbClient;

    @Autowired
    private YgCardInfoMapper ygCardInfoMapper;

    @Autowired
    private YgCardSetMapper ygCardSetMapper;

    @Autowired
    private YgLimitListMapper ygLimitListMapper;

    @Autowired
    private RedisCache redisCache;

    /**
     * 全量同步卡片数据(cards.zip)
     * 
     * @param force 是否强制同步(忽略md5比对)
     * @return 同步结果
     */
    @Override
    public Map<String, Object> syncAllCards(boolean force)
    {
        Map<String, Object> result = new HashMap<>();
        try
        {
            // 1. 获取远端md5并与Redis缓存比对
            String newMd5 = ygocdbClient.get(YgocdbClient.BASE_URL + "/cards.zip.md5").trim();
            String cachedMd5 = redisCache.getCacheObject(CARDS_MD5_KEY);
            if (!force && newMd5.equals(cachedMd5))
            {
                result.put("skipped", true);
                result.put("message", "数据未变化");
                return result;
            }
            // 2. 下载cards.zip并解出cards.json
            byte[] zipBytes = ygocdbClient.getBytes(YgocdbClient.BASE_URL + "/cards.zip");
            String json = extractCardsJson(zipBytes);
            JSONObject root = JSONObject.parseObject(json);
            // 3. 遍历条目转实体,分批入库
            List<YgCardInfo> buffer = new ArrayList<>(BATCH_SIZE);
            int total = 0;
            int affected = 0;
            for (String cidKey : root.keySet())
            {
                JSONObject card = root.getJSONObject(cidKey);
                if (card == null || card.getLong("id") == null)
                {
                    continue;
                }
                YgCardInfo info = YgDataUtils.parseCard(card);
                info.setSyncSource(1);
                buffer.add(info);
                total++;
                if (buffer.size() >= BATCH_SIZE)
                {
                    affected += ygCardInfoMapper.batchUpsertYgCardInfo(buffer);
                    buffer.clear();
                }
            }
            if (!buffer.isEmpty())
            {
                affected += ygCardInfoMapper.batchUpsertYgCardInfo(buffer);
            }
            // 4. 成功后缓存md5
            redisCache.setCacheObject(CARDS_MD5_KEY, newMd5);
            result.put("skipped", false);
            result.put("total", total);
            result.put("affected", affected);
            result.put("md5", newMd5);
            result.put("message", "同步完成,共处理" + total + "条");
        }
        catch (Exception e)
        {
            log.error("ygocdb全量同步卡片失败", e);
            result.put("skipped", false);
            result.put("message", "同步失败:" + e.getMessage());
        }
        return result;
    }

    /**
     * 按需同步单张卡片(/card/{id})
     * 
     * @param cardId 卡片密码(id)
     * @return 影响行数,接口无此卡返回0
     */
    @Override
    public int syncSingleCard(long cardId)
    {
        try
        {
            String body = ygocdbClient.get(YgocdbClient.BASE_URL + "/card/" + cardId);
            JSONObject root = JSONObject.parseObject(body);
            if (root == null)
            {
                return 0;
            }
            // 接口无此卡时返回错误结构,容错处理
            if (root.containsKey("result") && !root.getBooleanValue("result"))
            {
                return 0;
            }
            JSONObject card = extractSingleCard(root);
            if (card == null)
            {
                return 0;
            }
            YgCardInfo info = YgDataUtils.parseCard(card);
            if (info.getCardId() == null)
            {
                info.setCardId(cardId);
            }
            info.setSyncSource(2);
            List<YgCardInfo> list = new ArrayList<>(1);
            list.add(info);
            return ygCardInfoMapper.batchUpsertYgCardInfo(list);
        }
        catch (Exception e)
        {
            log.error("ygocdb单卡同步失败,cardId:{}", cardId, e);
            return 0;
        }
    }

    /**
     * 收集卡片系列(拆分全表setcode的4个16bit段)
     * 
     * @return 系列条数
     */
    @Override
    public int syncCardSets()
    {
        List<Long> setcodes = ygCardInfoMapper.selectDistinctSetcodes();
        // 拆分16bit段并去重
        Set<Long> segments = new LinkedHashSet<>();
        for (Long setcode : setcodes)
        {
            if (setcode == null || setcode == 0L)
            {
                continue;
            }
            for (int shift = 0; shift < 64; shift += 16)
            {
                long seg = (setcode >>> shift) & 0xFFFFL;
                if (seg != 0L)
                {
                    segments.add(seg);
                }
            }
        }
        if (segments.isEmpty())
        {
            return 0;
        }
        // 系列名称cards.zip不提供,留空人工维护
        List<YgCardSet> list = new ArrayList<>(segments.size());
        for (Long seg : segments)
        {
            YgCardSet set = new YgCardSet();
            set.setSetCode(Long.toHexString(seg));
            set.setSetValue(seg);
            set.setCnName("");
            set.setJpName("");
            set.setEnName("");
            list.add(set);
        }
        ygCardSetMapper.batchUpsertYgCardSet(list);
        return list.size();
    }

    /**
     * 同步禁限卡表(/limits.json)
     * 
     * @return 各语种条数与跳过数
     */
    @Override
    public Map<String, Object> syncLimitLists()
    {
        Map<String, Object> result = new HashMap<>();
        try
        {
            String body = ygocdbClient.get(YgocdbClient.BASE_URL + "/limits.json");
            JSONObject root = JSONObject.parseObject(body);
            if (root == null)
            {
                result.put("message", "禁限卡表数据为空");
                return result;
            }
            // cid -> card_id 映射(limits.json的键是cid)
            Map<Long, Long> cidMap = new HashMap<>();
            for (Map<String, Object> row : ygCardInfoMapper.selectCidCardIdMap())
            {
                Object cid = row.get("cid");
                Object cardId = row.get("card_id");
                if (cid instanceof Number && cardId instanceof Number)
                {
                    cidMap.put(((Number) cid).longValue(), ((Number) cardId).longValue());
                }
            }
            // 语种 -> ot_type 映射
            String[][] langs = { { "ja", "ocg" }, { "en", "tcg" }, { "cn", "sc" } };
            // 分类 -> 限制类型 映射
            String[][] categories = { { "forbidden", "3" }, { "limited", "1" }, { "semi_limited", "2" } };
            int skipped = 0;
            for (String[] lang : langs)
            {
                JSONObject langObj = root.getJSONObject(lang[0]);
                if (langObj == null)
                {
                    continue;
                }
                String otType = lang[1];
                Date effectiveDate = DateUtils.parseDate(langObj.getString("date"));
                // 旧记录标记为历史
                ygLimitListMapper.markLimitHistory(otType);
                List<YgLimitList> list = new ArrayList<>();
                for (String[] category : categories)
                {
                    JSONObject limitObj = langObj.getJSONObject(category[0]);
                    if (limitObj == null)
                    {
                        continue;
                    }
                    int limitType = Integer.parseInt(category[1]);
                    for (String cidKey : limitObj.keySet())
                    {
                        Long cardId = cidMap.get(Long.parseLong(cidKey));
                        if (cardId == null)
                        {
                            // 卡片库无对应cid,跳过计数
                            skipped++;
                            continue;
                        }
                        YgLimitList limit = new YgLimitList();
                        limit.setCardId(cardId);
                        limit.setOtType(otType);
                        limit.setLimitType(limitType);
                        limit.setEffectiveDate(effectiveDate);
                        limit.setIsCurrent("1");
                        limit.setRemark(StringUtils.nvl(limitObj.getString(cidKey), ""));
                        list.add(limit);
                    }
                }
                if (!list.isEmpty())
                {
                    ygLimitListMapper.batchUpsertYgLimitList(list);
                }
                result.put(otType, list.size());
            }
            result.put("skipped", skipped);
            result.put("message", "禁限卡表同步完成");
        }
        catch (Exception e)
        {
            log.error("ygocdb禁限卡表同步失败", e);
            result.put("message", "同步失败:" + e.getMessage());
        }
        return result;
    }

    /**
     * 从zip字节流中解出cards.json文本
     */
    private String extractCardsJson(byte[] zipBytes) throws Exception
    {
        try (InputStream in = new ByteArrayInputStream(zipBytes); ZipInputStream zis = new ZipInputStream(in))
        {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null)
            {
                if (!entry.isDirectory() && entry.getName().endsWith("cards.json"))
                {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] buf = new byte[8192];
                    int len;
                    while ((len = zis.read(buf)) > 0)
                    {
                        out.write(buf, 0, len);
                    }
                    return out.toString("UTF-8");
                }
            }
        }
        throw new IllegalStateException("cards.zip中未找到cards.json");
    }

    /**
     * 从单卡接口响应中提取卡片对象,兼容顶层对象与data数组两种结构
     */
    private JSONObject extractSingleCard(JSONObject root)
    {
        Object dataNode = root.get("data");
        if (dataNode instanceof JSONArray)
        {
            JSONArray arr = (JSONArray) dataNode;
            if (arr.isEmpty())
            {
                return null;
            }
            JSONObject first = arr.getJSONObject(0);
            JSONObject card = first.getJSONObject("card");
            if (card == null)
            {
                card = first;
            }
            if (card.getLong("cid") == null && first.getLong("cid") != null)
            {
                card.put("cid", first.getLong("cid"));
            }
            return card;
        }
        if (dataNode instanceof JSONObject && root.containsKey("text"))
        {
            return root;
        }
        return root.containsKey("id") || root.containsKey("text") ? root : null;
    }
}
