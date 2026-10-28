package com.ruoyi.system.service.shop;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YgShopStore;

/**
 * WooCommerce / WordPress REST API客户端
 *
 * @author ruoyi
 * @date 2026-10-23
 */
@Component
public class YgShopWooClient
{
    private static final Logger log = LoggerFactory.getLogger(YgShopWooClient.class);

    /** 连接超时时间(秒) */
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(10);

    /** 请求超时时间(秒) */
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(60);

    /** 单页数量 */
    private static final int PER_PAGE = 100;

    /** 最多同步页数，避免异常店铺无限翻页 */
    private static final int MAX_PAGES = 20;

    /** JDK17自带HTTP客户端 */
    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(CONNECT_TIMEOUT)
            .followRedirects(HttpClient.Redirect.NORMAL).build();

    /**
     * 拉取订单
     */
    public List<JSONObject> fetchOrders(YgShopStore store)
    {
        return fetchAll(store, "/wp-json/wc/v3/orders?per_page=" + PER_PAGE + "&page=", true);
    }

    /**
     * 拉取商品
     */
    public List<JSONObject> fetchProducts(YgShopStore store)
    {
        return fetchAll(store, "/wp-json/wc/v3/products?per_page=" + PER_PAGE + "&page=", true);
    }

    /**
     * 拉取文章（WordPress公开文章接口，无需密钥）
     */
    public List<JSONObject> fetchPosts(YgShopStore store)
    {
        return fetchAll(store, "/wp-json/wp/v2/posts?per_page=" + PER_PAGE + "&page=", false);
    }

    /**
     * 测试店铺连接（调用商品接口校验密钥）
     */
    public void testConnection(YgShopStore store)
    {
        fetchPage(store, "/wp-json/wc/v3/products?per_page=1&page=1", true);
    }

    /**
     * 分页拉取全部数据
     */
    private List<JSONObject> fetchAll(YgShopStore store, String pathPrefix, boolean auth)
    {
        List<JSONObject> result = new ArrayList<>();
        for (int page = 1; page <= MAX_PAGES; page++)
        {
            JSONArray array = fetchPage(store, pathPrefix + page, auth);
            if (array == null || array.isEmpty())
            {
                break;
            }
            for (int i = 0; i < array.size(); i++)
            {
                result.add(array.getJSONObject(i));
            }
            if (array.size() < PER_PAGE)
            {
                break;
            }
        }
        return result;
    }

    /**
     * 请求单页数据
     */
    private JSONArray fetchPage(YgShopStore store, String path, boolean auth)
    {
        return JSON.parseArray(send(store, path, "GET", null, auth));
    }

    /**
     * 查询商品详情
     */
    public JSONObject fetchProduct(YgShopStore store, Long wooProductId)
    {
        return JSON.parseObject(send(store, "/wp-json/wc/v3/products/" + wooProductId, "GET", null, true));
    }

    /**
     * 按SKU查询商品（不存在返回null）
     */
    public JSONObject findProductBySku(YgShopStore store, String sku)
    {
        String path = "/wp-json/wc/v3/products?per_page=1&sku=" + URLEncoder.encode(sku, StandardCharsets.UTF_8);
        JSONArray array = JSON.parseArray(send(store, path, "GET", null, true));
        return array == null || array.isEmpty() ? null : array.getJSONObject(0);
    }

    /**
     * 新增商品
     */
    public JSONObject createProduct(YgShopStore store, JSONObject body)
    {
        return JSON.parseObject(send(store, "/wp-json/wc/v3/products", "POST", body.toJSONString(), true));
    }

    /**
     * 修改商品
     */
    public JSONObject updateProduct(YgShopStore store, Long wooProductId, JSONObject body)
    {
        return JSON.parseObject(send(store, "/wp-json/wc/v3/products/" + wooProductId, "PUT", body.toJSONString(), true));
    }

    /**
     * 拉取商品分类
     */
    public List<JSONObject> fetchProductCategories(YgShopStore store)
    {
        return fetchAll(store, "/wp-json/wc/v3/products/categories?per_page=" + PER_PAGE + "&page=", true);
    }

    /**
     * 新增商品分类
     */
    public JSONObject createProductCategory(YgShopStore store, String name)
    {
        JSONObject body = new JSONObject();
        body.put("name", name);
        return JSON.parseObject(send(store, "/wp-json/wc/v3/products/categories", "POST", body.toJSONString(), true));
    }

    /**
     * 拉取Webhook列表
     */
    public List<JSONObject> fetchWebhooks(YgShopStore store)
    {
        return fetchAll(store, "/wp-json/wc/v3/webhooks?per_page=" + PER_PAGE + "&page=", true);
    }

    /**
     * 新增Webhook
     */
    public JSONObject createWebhook(YgShopStore store, JSONObject body)
    {
        return JSON.parseObject(send(store, "/wp-json/wc/v3/webhooks", "POST", body.toJSONString(), true));
    }

    /**
     * 修改Webhook
     */
    public JSONObject updateWebhook(YgShopStore store, Long wooWebhookId, JSONObject body)
    {
        return JSON.parseObject(send(store, "/wp-json/wc/v3/webhooks/" + wooWebhookId, "PUT", body.toJSONString(), true));
    }

    /**
     * 删除Webhook
     */
    public JSONObject deleteWebhook(YgShopStore store, Long wooWebhookId)
    {
        return JSON.parseObject(send(store, "/wp-json/wc/v3/webhooks/" + wooWebhookId + "?force=true", "DELETE", null, true));
    }

    /**
     * 发送JSON请求
     */
    private String send(YgShopStore store, String path, String method, String body, boolean auth)
    {
        String url = buildBaseUrl(store) + path;
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url)).timeout(REQUEST_TIMEOUT)
                .header("Accept", "application/json")
                .header("User-Agent", "Mozilla/5.0 (compatible; ruoyi-shop-sync)");
        if (auth)
        {
            builder.header("Authorization", basicAuth(store));
        }
        if ("POST".equals(method))
        {
            builder.header("Content-Type", "application/json;charset=UTF-8")
                    .POST(HttpRequest.BodyPublishers.ofString(body == null ? "{}" : body, StandardCharsets.UTF_8));
        }
        else if ("PUT".equals(method))
        {
            builder.header("Content-Type", "application/json;charset=UTF-8")
                    .PUT(HttpRequest.BodyPublishers.ofString(body == null ? "{}" : body, StandardCharsets.UTF_8));
        }
        else if ("DELETE".equals(method))
        {
            builder.DELETE();
        }
        else
        {
            builder.GET();
        }
        try
        {
            HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() != 200 && response.statusCode() != 201 && response.statusCode() != 204)
            {
                throw new ServiceException("店铺【" + store.getStoreName() + "】接口请求失败(HTTP "
                        + response.statusCode() + ")：" + readErrorMessage(response.body()));
            }
            return response.body();
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            throw new ServiceException("店铺【" + store.getStoreName() + "】接口请求被中断");
        }
        catch (ServiceException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            log.error("WooCommerce接口请求异常, url={}", url, e);
            throw new ServiceException("店铺【" + store.getStoreName() + "】接口请求异常：" + e.getMessage());
        }
    }

    /**
     * 构造Basic认证头
     */
    private String basicAuth(YgShopStore store)
    {
        String key = StringUtils.trimToEmpty(store.getConsumerKey());
        String secret = StringUtils.trimToEmpty(store.getConsumerSecret());
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(secret))
        {
            throw new ServiceException("店铺【" + store.getStoreName() + "】未配置 WooCommerce Consumer Key/Secret");
        }
        String token = Base64.getEncoder().encodeToString((key + ":" + secret).getBytes(StandardCharsets.UTF_8));
        return "Basic " + token;
    }

    /**
     * 规范化店铺地址
     */
    public static String buildBaseUrl(YgShopStore store)
    {
        String url = StringUtils.trimToEmpty(store.getStoreUrl());
        if (StringUtils.isEmpty(url))
        {
            throw new ServiceException("店铺【" + store.getStoreName() + "】未配置店铺地址");
        }
        while (url.endsWith("/"))
        {
            url = url.substring(0, url.length() - 1);
        }
        if (!url.startsWith("http://") && !url.startsWith("https://"))
        {
            url = "https://" + url;
        }
        return url;
    }

    /**
     * 解析接口错误信息
     */
    private String readErrorMessage(String body)
    {
        if (StringUtils.isEmpty(body))
        {
            return "无响应内容";
        }
        try
        {
            JSONObject json = JSON.parseObject(body);
            String message = json == null ? null : json.getString("message");
            if (StringUtils.isNotEmpty(message))
            {
                return StringUtils.substring(message, 0, 200);
            }
        }
        catch (Exception ignored)
        {
        }
        return StringUtils.substring(body, 0, 200);
    }

    /**
     * 解析店铺时间字符串（ISO8601，无时区或带时区）
     */
    public static Date parseShopDate(String value)
    {
        if (StringUtils.isEmpty(value))
        {
            return null;
        }
        try
        {
            return Date.from(java.time.OffsetDateTime.parse(value).toInstant());
        }
        catch (Exception ignored)
        {
        }
        try
        {
            return Date.from(LocalDateTime.parse(value).atZone(ZoneId.systemDefault()).toInstant());
        }
        catch (Exception ignored)
        {
        }
        return null;
    }

    /**
     * HTML转纯文本（保留段落换行）
     */
    public static String plainText(String html)
    {
        if (StringUtils.isEmpty(html))
        {
            return "";
        }
        String text = html.replaceAll("(?i)</(p|div|h[1-6]|li|tr|blockquote)>", "\n")
                .replaceAll("(?i)<br\\s*/?>", "\n");
        text = text.replaceAll("<[^>]+>", " ");
        text = HtmlUtils.htmlUnescape(text);
        text = text.replaceAll("[\\t\\x0B\\f\\r]+", " ");
        text = text.replaceAll(" *\\n *", "\n");
        text = text.replaceAll("\\n{3,}", "\n\n");
        return text.trim();
    }
}
