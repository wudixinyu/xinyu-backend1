package com.ruoyi.system.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YgShopStore;
import com.ruoyi.system.domain.YgShopWooOrder;
import com.ruoyi.system.mapper.YgShopWooOrderMapper;
import com.ruoyi.system.service.IYgShopStoreService;
import com.ruoyi.system.service.IYgShopWooOrderService;
import com.ruoyi.system.service.shop.YgShopWooClient;

/**
 * WooCommerce订单Service业务层处理
 *
 * @author ruoyi
 * @date 2026-10-23
 */
@Service
public class YgShopWooOrderServiceImpl implements IYgShopWooOrderService
{
    @Autowired
    private YgShopWooOrderMapper ygShopWooOrderMapper;

    @Autowired
    private IYgShopStoreService ygShopStoreService;

    @Autowired
    private YgShopWooClient ygShopWooClient;

    @Override
    public YgShopWooOrder selectYgShopWooOrderByOrderId(Long orderId)
    {
        return ygShopWooOrderMapper.selectYgShopWooOrderByOrderId(orderId);
    }

    @Override
    public List<YgShopWooOrder> selectYgShopWooOrderList(YgShopWooOrder ygShopWooOrder)
    {
        return ygShopWooOrderMapper.selectYgShopWooOrderList(ygShopWooOrder);
    }

    @Override
    public int syncWooOrder(Long storeId)
    {
        List<YgShopStore> stores = ygShopStoreService.selectSyncStores(storeId);
        int count = 0;
        for (YgShopStore store : stores)
        {
            List<JSONObject> orders = ygShopWooClient.fetchOrders(store);
            for (JSONObject json : orders)
            {
                ygShopWooOrderMapper.upsertYgShopWooOrder(toOrder(json, store.getStoreId()));
                count++;
            }
            ygShopStoreService.updateLastSyncTime(store.getStoreId());
        }
        return count;
    }

    @Override
    public int deleteYgShopWooOrderByOrderIds(Long[] orderIds)
    {
        return ygShopWooOrderMapper.deleteYgShopWooOrderByOrderIds(orderIds);
    }

    @Override
    public void handleWebhook(String topic, JSONObject payload, Long storeId)
    {
        Long wooOrderId = payload == null ? null : payload.getLong("id");
        if (wooOrderId == null)
        {
            return;
        }
        if (topic != null && topic.endsWith(".deleted"))
        {
            ygShopWooOrderMapper.deleteByStoreAndWooOrderId(storeId, wooOrderId);
            return;
        }
        ygShopWooOrderMapper.upsertYgShopWooOrder(toOrder(payload, storeId));
    }

    /**
     * WooCommerce订单JSON转本地对象
     */
    private YgShopWooOrder toOrder(JSONObject json, Long storeId)
    {
        Date now = DateUtils.getNowDate();
        YgShopWooOrder order = new YgShopWooOrder();
        order.setStoreId(storeId);
        order.setWooOrderId(json.getLong("id"));
        order.setOrderNumber(StringUtils.substring(StringUtils.trimToEmpty(json.getString("number")), 0, 60));
        order.setStatus(StringUtils.substring(StringUtils.trimToEmpty(json.getString("status")), 0, 30));
        order.setCurrency(StringUtils.substring(StringUtils.trimToEmpty(json.getString("currency")), 0, 8));
        order.setTotal(decimal(json.getString("total")));
        JSONObject billing = json.getJSONObject("billing");
        if (billing != null)
        {
            String name = StringUtils.trimToEmpty(billing.getString("first_name")) + " "
                    + StringUtils.trimToEmpty(billing.getString("last_name"));
            order.setCustomerName(StringUtils.substring(StringUtils.trimToEmpty(name), 0, 190));
            order.setCustomerEmail(StringUtils.substring(StringUtils.trimToEmpty(billing.getString("email")), 0, 190));
        }
        String paymentTitle = json.getString("payment_method_title");
        order.setPaymentMethod(StringUtils.substring(StringUtils.isNotEmpty(paymentTitle) ? paymentTitle
                : StringUtils.trimToEmpty(json.getString("payment_method")), 0, 90));
        JSONArray items = json.getJSONArray("line_items");
        order.setItemCount(items == null ? 0 : items.size());
        order.setItemsSummary(buildItemsSummary(items));
        order.setDateCreated(YgShopWooClient.parseShopDate(json.getString("date_created")));
        order.setDatePaid(YgShopWooClient.parseShopDate(json.getString("date_paid")));
        order.setCreateTime(now);
        order.setUpdateTime(now);
        return order;
    }

    /**
     * 组装商品摘要，如：商品A ×2；商品B ×1
     */
    private String buildItemsSummary(JSONArray items)
    {
        if (items == null || items.isEmpty())
        {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int limit = Math.min(items.size(), 5);
        for (int i = 0; i < limit; i++)
        {
            JSONObject item = items.getJSONObject(i);
            if (i > 0)
            {
                sb.append("；");
            }
            sb.append(StringUtils.trimToEmpty(item.getString("name")))
                    .append(" ×").append(item.getInteger("quantity") == null ? 0 : item.getInteger("quantity"));
        }
        if (items.size() > limit)
        {
            sb.append(" 等").append(items.size()).append("项");
        }
        return StringUtils.substring(sb.toString(), 0, 900);
    }

    /**
     * 金额解析
     */
    private BigDecimal decimal(String value)
    {
        try
        {
            return StringUtils.isEmpty(value) ? BigDecimal.ZERO : new BigDecimal(value);
        }
        catch (Exception e)
        {
            return BigDecimal.ZERO;
        }
    }
}
