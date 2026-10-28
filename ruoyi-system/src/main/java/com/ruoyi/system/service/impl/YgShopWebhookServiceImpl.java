package com.ruoyi.system.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YgShopStore;
import com.ruoyi.system.domain.YgShopWebhook;
import com.ruoyi.system.mapper.YgShopWebhookMapper;
import com.ruoyi.system.service.IYgShopStoreService;
import com.ruoyi.system.service.IYgShopWebhookService;
import com.ruoyi.system.service.IYgShopWooOrderService;
import com.ruoyi.system.service.IYgShopWooProductService;
import com.ruoyi.system.service.shop.YgShopWooClient;

/**
 * 店铺WebhookService业务层处理
 *
 * @author ruoyi
 * @date 2026-10-23
 */
@Service
public class YgShopWebhookServiceImpl implements IYgShopWebhookService
{
    private static final Logger log = LoggerFactory.getLogger(YgShopWebhookServiceImpl.class);

    /** 支持的Webhook主题 */
    private static final String[] SUPPORTED_TOPICS = {
            "order.created", "order.updated", "order.deleted",
            "product.created", "product.updated", "product.deleted" };

    @Autowired
    private YgShopWebhookMapper ygShopWebhookMapper;

    @Autowired
    private IYgShopStoreService ygShopStoreService;

    @Autowired
    private IYgShopWooOrderService ygShopWooOrderService;

    @Autowired
    private IYgShopWooProductService ygShopWooProductService;

    @Autowired
    private YgShopWooClient ygShopWooClient;

    @Override
    public List<YgShopWebhook> selectYgShopWebhookList(YgShopWebhook ygShopWebhook)
    {
        return ygShopWebhookMapper.selectYgShopWebhookList(ygShopWebhook);
    }

    @Override
    public YgShopWebhook selectYgShopWebhookByWebhookId(Long webhookId)
    {
        return ygShopWebhookMapper.selectYgShopWebhookByWebhookId(webhookId);
    }

    @Override
    public int syncYgShopWebhook(Long storeId)
    {
        List<YgShopStore> stores = ygShopStoreService.selectSyncStores(storeId);
        int count = 0;
        for (YgShopStore store : stores)
        {
            count += syncStoreWebhooks(store);
        }
        return count;
    }

    @Override
    public YgShopWebhook createYgShopWebhook(YgShopWebhook ygShopWebhook)
    {
        YgShopStore store = requireStore(ygShopWebhook.getStoreId());
        validateWebhook(ygShopWebhook);
        String secret = StringUtils.trimToEmpty(ygShopWebhook.getSecret());
        if (StringUtils.isEmpty(secret))
        {
            secret = UUID.randomUUID().toString().replace("-", "");
        }
        JSONObject body = new JSONObject();
        body.put("name", ygShopWebhook.getName());
        body.put("topic", ygShopWebhook.getTopic());
        body.put("delivery_url", ygShopWebhook.getDeliveryUrl());
        body.put("secret", secret);
        body.put("status", "1".equals(ygShopWebhook.getStatus()) ? "disabled" : "active");
        JSONObject created = ygShopWooClient.createWebhook(store, body);
        YgShopWebhook local = toWebhook(created, store.getStoreId(), secret);
        local.setCreateBy(ygShopWebhook.getCreateBy());
        local.setRemark(ygShopWebhook.getRemark());
        ygShopWebhookMapper.upsertYgShopWebhook(local);
        return ygShopWebhookMapper.selectByStoreAndWooId(store.getStoreId(), local.getWooWebhookId());
    }

    @Override
    public YgShopWebhook updateYgShopWebhook(YgShopWebhook ygShopWebhook)
    {
        if (ygShopWebhook.getWebhookId() == null)
        {
            throw new ServiceException("Webhook ID不能为空");
        }
        YgShopWebhook local = ygShopWebhookMapper.selectYgShopWebhookByWebhookId(ygShopWebhook.getWebhookId());
        if (local == null)
        {
            throw new ServiceException("Webhook不存在");
        }
        YgShopStore store = requireStore(local.getStoreId());
        validateWebhook(ygShopWebhook);
        JSONObject body = new JSONObject();
        body.put("name", ygShopWebhook.getName());
        body.put("topic", ygShopWebhook.getTopic());
        body.put("delivery_url", ygShopWebhook.getDeliveryUrl());
        body.put("status", "0".equals(ygShopWebhook.getStatus()) ? "active" : "disabled");
        String secret = StringUtils.trimToEmpty(ygShopWebhook.getSecret());
        if (StringUtils.isNotEmpty(secret))
        {
            body.put("secret", secret);
        }
        else
        {
            secret = local.getSecret();
        }
        JSONObject updated = ygShopWooClient.updateWebhook(store, local.getWooWebhookId(), body);
        YgShopWebhook result = toWebhook(updated, store.getStoreId(), secret);
        result.setUpdateBy(ygShopWebhook.getUpdateBy());
        result.setRemark(ygShopWebhook.getRemark());
        ygShopWebhookMapper.upsertYgShopWebhook(result);
        return ygShopWebhookMapper.selectYgShopWebhookByWebhookId(local.getWebhookId());
    }

    @Override
    public int changeStatus(Long webhookId, String status)
    {
        YgShopWebhook local = ygShopWebhookMapper.selectYgShopWebhookByWebhookId(webhookId);
        if (local == null)
        {
            throw new ServiceException("Webhook不存在");
        }
        YgShopStore store = requireStore(local.getStoreId());
        JSONObject body = new JSONObject();
        body.put("status", "0".equals(status) ? "active" : "disabled");
        ygShopWooClient.updateWebhook(store, local.getWooWebhookId(), body);
        return ygShopWebhookMapper.updateStatus(webhookId, status);
    }

    @Override
    public int deleteYgShopWebhookByWebhookIds(Long[] webhookIds)
    {
        List<Long> deleted = new ArrayList<>();
        for (Long webhookId : webhookIds)
        {
            YgShopWebhook local = ygShopWebhookMapper.selectYgShopWebhookByWebhookId(webhookId);
            if (local == null)
            {
                continue;
            }
            YgShopStore store = ygShopStoreService.selectYgShopStoreByStoreId(local.getStoreId());
            if (store != null && "0".equals(store.getStatus()) && local.getWooWebhookId() != null)
            {
                try
                {
                    ygShopWooClient.deleteWebhook(store, local.getWooWebhookId());
                }
                catch (ServiceException e)
                {
                    if (!StringUtils.contains(e.getMessage(), "404"))
                    {
                        throw e;
                    }
                }
            }
            deleted.add(webhookId);
        }
        if (deleted.isEmpty())
        {
            return 0;
        }
        return ygShopWebhookMapper.deleteYgShopWebhookByWebhookIds(deleted.toArray(new Long[0]));
    }

    @Override
    public void receiveWebhook(Long storeId, String wooWebhookId, String topic, String signature, String payload)
    {
        if (storeId == null)
        {
            throw new ServiceException("店铺ID不能为空");
        }
        Long remoteWebhookId = parseLong(wooWebhookId);
        if (remoteWebhookId == null)
        {
            throw new ServiceException("缺少X-WC-Webhook-ID请求头");
        }
        YgShopWebhook webhook = ygShopWebhookMapper.selectByStoreAndWooId(storeId, remoteWebhookId);
        if (webhook == null)
        {
            YgShopStore store = ygShopStoreService.selectYgShopStoreByStoreId(storeId);
            if (store != null)
            {
                syncStoreWebhooks(store);
                webhook = ygShopWebhookMapper.selectByStoreAndWooId(storeId, remoteWebhookId);
            }
        }
        if (webhook == null)
        {
            throw new ServiceException("Webhook未登记，请先在Webhooks页面从店铺同步");
        }
        String secret = StringUtils.trimToEmpty(webhook.getSecret());
        if (StringUtils.isEmpty(secret))
        {
            ygShopWebhookMapper.updateDeliveryResult(webhook.getWebhookId(), "1", new Date());
            throw new ServiceException("Webhook未配置密钥，无法校验签名");
        }
        if (!verifySignature(payload, secret, signature))
        {
            ygShopWebhookMapper.updateDeliveryResult(webhook.getWebhookId(), "1", new Date());
            throw new ServiceException("Webhook签名校验失败");
        }
        try
        {
            String event = StringUtils.trimToEmpty(topic);
            JSONObject json = JSON.parseObject(payload);
            if (event.startsWith("order."))
            {
                ygShopWooOrderService.handleWebhook(event, json, storeId);
            }
            else if (event.startsWith("product."))
            {
                ygShopWooProductService.handleWebhook(event, json, storeId);
            }
            ygShopWebhookMapper.updateDeliveryResult(webhook.getWebhookId(), "0", new Date());
        }
        catch (Exception e)
        {
            ygShopWebhookMapper.updateDeliveryResult(webhook.getWebhookId(), "1", new Date());
            log.error("Webhook处理失败, storeId={}, topic={}", storeId, topic, e);
            throw e instanceof ServiceException ? (ServiceException) e
                    : new ServiceException("Webhook处理失败：" + e.getMessage());
        }
    }

    /**
     * 从店铺拉取并登记Webhook
     */
    private int syncStoreWebhooks(YgShopStore store)
    {
        List<JSONObject> webhooks = ygShopWooClient.fetchWebhooks(store);
        for (JSONObject json : webhooks)
        {
            ygShopWebhookMapper.upsertYgShopWebhook(toWebhook(json, store.getStoreId(), null));
        }
        return webhooks.size();
    }

    /**
     * WooCommerce Webhook JSON转本地对象
     */
    private YgShopWebhook toWebhook(JSONObject json, Long storeId, String fallbackSecret)
    {
        Date now = DateUtils.getNowDate();
        YgShopWebhook webhook = new YgShopWebhook();
        webhook.setStoreId(storeId);
        webhook.setWooWebhookId(json.getLong("id"));
        webhook.setName(StringUtils.substring(StringUtils.trimToEmpty(json.getString("name")), 0, 190));
        webhook.setTopic(StringUtils.substring(StringUtils.trimToEmpty(json.getString("topic")), 0, 60));
        webhook.setDeliveryUrl(StringUtils.substring(StringUtils.trimToEmpty(json.getString("delivery_url")), 0, 490));
        String secret = json.getString("secret");
        webhook.setSecret(StringUtils.isNotEmpty(secret) ? secret : fallbackSecret);
        webhook.setStatus("active".equalsIgnoreCase(json.getString("status")) ? "0" : "1");
        webhook.setCreateTime(now);
        webhook.setUpdateTime(now);
        return webhook;
    }

    /**
     * 基础校验
     */
    private void validateWebhook(YgShopWebhook webhook)
    {
        if (StringUtils.isEmpty(webhook.getName()))
        {
            throw new ServiceException("Webhook名称不能为空");
        }
        if (StringUtils.isEmpty(webhook.getTopic()))
        {
            throw new ServiceException("Webhook主题不能为空");
        }
        boolean supported = false;
        for (String topic : SUPPORTED_TOPICS)
        {
            if (topic.equals(webhook.getTopic()))
            {
                supported = true;
                break;
            }
        }
        if (!supported)
        {
            throw new ServiceException("不支持的Webhook主题：" + webhook.getTopic());
        }
        String deliveryUrl = StringUtils.trimToEmpty(webhook.getDeliveryUrl());
        if (StringUtils.isEmpty(deliveryUrl))
        {
            throw new ServiceException("回调地址不能为空");
        }
        if (!deliveryUrl.startsWith("http://") && !deliveryUrl.startsWith("https://"))
        {
            throw new ServiceException("回调地址必须以 http:// 或 https:// 开头");
        }
        webhook.setName(webhook.getName().trim());
        webhook.setDeliveryUrl(deliveryUrl);
    }

    /**
     * 获取启用店铺
     */
    private YgShopStore requireStore(Long storeId)
    {
        if (storeId == null)
        {
            throw new ServiceException("请选择店铺");
        }
        return ygShopStoreService.selectSyncStores(storeId).get(0);
    }

    /**
     * HMAC-SHA256签名校验
     */
    private boolean verifySignature(String payload, String secret, String signature)
    {
        if (StringUtils.isEmpty(signature))
        {
            return false;
        }
        try
        {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String computed = Base64.getEncoder().encodeToString(hash);
            return MessageDigest.isEqual(computed.getBytes(StandardCharsets.UTF_8),
                    signature.getBytes(StandardCharsets.UTF_8));
        }
        catch (Exception e)
        {
            log.error("Webhook签名计算失败", e);
            return false;
        }
    }

    /**
     * 安全转换Long
     */
    private Long parseLong(String value)
    {
        try
        {
            return StringUtils.isEmpty(value) ? null : Long.valueOf(value.trim());
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
