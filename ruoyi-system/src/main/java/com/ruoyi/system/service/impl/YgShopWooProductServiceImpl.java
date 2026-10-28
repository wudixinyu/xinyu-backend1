package com.ruoyi.system.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YgShopStore;
import com.ruoyi.system.domain.YgShopWooProduct;
import com.ruoyi.system.mapper.YgShopWooProductMapper;
import com.ruoyi.system.service.IYgShopStoreService;
import com.ruoyi.system.service.IYgShopWooProductService;
import com.ruoyi.system.service.shop.YgShopWooClient;

/**
 * WooCommerce商品Service业务层处理
 *
 * @author ruoyi
 * @date 2026-10-23
 */
@Service
public class YgShopWooProductServiceImpl implements IYgShopWooProductService
{
    /** 导入结果最多展示的明细条数 */
    private static final int IMPORT_DETAIL_LIMIT = 20;

    @Autowired
    private YgShopWooProductMapper ygShopWooProductMapper;

    @Autowired
    private IYgShopStoreService ygShopStoreService;

    @Autowired
    private YgShopWooClient ygShopWooClient;

    @Override
    public YgShopWooProduct selectYgShopWooProductByProductId(Long productId)
    {
        return ygShopWooProductMapper.selectYgShopWooProductByProductId(productId);
    }

    @Override
    public List<YgShopWooProduct> selectYgShopWooProductList(YgShopWooProduct ygShopWooProduct)
    {
        return ygShopWooProductMapper.selectYgShopWooProductList(ygShopWooProduct);
    }

    @Override
    public int syncWooProduct(Long storeId)
    {
        List<YgShopStore> stores = ygShopStoreService.selectSyncStores(storeId);
        int count = 0;
        for (YgShopStore store : stores)
        {
            List<JSONObject> products = ygShopWooClient.fetchProducts(store);
            for (JSONObject json : products)
            {
                ygShopWooProductMapper.upsertYgShopWooProduct(toProduct(json, store.getStoreId()));
                count++;
            }
            ygShopStoreService.updateLastSyncTime(store.getStoreId());
        }
        return count;
    }

    @Override
    public YgShopWooProduct createWooProduct(YgShopWooProduct ygShopWooProduct)
    {
        YgShopStore store = requireStore(ygShopWooProduct.getStoreId());
        if (StringUtils.isEmpty(ygShopWooProduct.getName()))
        {
            throw new ServiceException("商品名称不能为空");
        }
        JSONObject body = buildProductBody(store, ygShopWooProduct, true, null, null);
        if (ygShopWooProduct.getDescription() != null)
        {
            body.put("description", ygShopWooProduct.getDescription());
        }
        JSONObject created = ygShopWooClient.createProduct(store, body);
        return saveRemoteProduct(created, store.getStoreId());
    }

    @Override
    public YgShopWooProduct updateWooProduct(YgShopWooProduct ygShopWooProduct)
    {
        if (ygShopWooProduct.getProductId() == null)
        {
            throw new ServiceException("商品ID不能为空");
        }
        YgShopWooProduct local = ygShopWooProductMapper.selectYgShopWooProductByProductId(ygShopWooProduct.getProductId());
        if (local == null)
        {
            throw new ServiceException("商品不存在");
        }
        if (local.getWooProductId() == null)
        {
            throw new ServiceException("该商品缺少店铺商品ID，无法更新");
        }
        if (StringUtils.isEmpty(ygShopWooProduct.getName()))
        {
            throw new ServiceException("商品名称不能为空");
        }
        YgShopStore store = requireStore(local.getStoreId());
        JSONObject body = buildProductBody(store, ygShopWooProduct, false, local.getImageUrl(), null);
        if (ygShopWooProduct.getDescription() != null)
        {
            body.put("description", ygShopWooProduct.getDescription());
        }
        JSONObject updated = ygShopWooClient.updateProduct(store, local.getWooProductId(), body);
        return saveRemoteProduct(updated, store.getStoreId());
    }

    @Override
    public YgShopWooProduct selectWooProductDetail(Long productId)
    {
        YgShopWooProduct local = ygShopWooProductMapper.selectYgShopWooProductByProductId(productId);
        if (local == null)
        {
            throw new ServiceException("商品不存在");
        }
        if (local.getWooProductId() == null)
        {
            throw new ServiceException("该商品缺少店铺商品ID，无法获取详情");
        }
        YgShopStore store = requireStore(local.getStoreId());
        JSONObject remote = ygShopWooClient.fetchProduct(store, local.getWooProductId());
        local.setPrice(decimal(remote.getString("price")));
        local.setRegularPrice(decimal(remote.getString("regular_price")));
        local.setSalePrice(decimal(remote.getString("sale_price")));
        local.setStockQuantity(remote.getInteger("stock_quantity"));
        local.setStockStatus(StringUtils.trimToEmpty(remote.getString("stock_status")));
        local.setCategories(buildCategories(remote.getJSONArray("categories")));
        local.setDescription(StringUtils.trimToEmpty(remote.getString("description")));
        return local;
    }

    @Override
    public String importWooProduct(List<YgShopWooProduct> list, boolean updateSupport)
    {
        int created = 0;
        int updated = 0;
        int skipped = 0;
        int failed = 0;
        List<String> details = new ArrayList<>();
        Map<Long, Map<String, Long>> categoryCache = new HashMap<>();
        for (YgShopWooProduct row : list)
        {
            try
            {
                YgShopStore store = resolveImportStore(row);
                if (StringUtils.isEmpty(row.getName()))
                {
                    throw new ServiceException("商品名称不能为空");
                }
                if (StringUtils.isEmpty(row.getSku()))
                {
                    JSONObject body = buildProductBody(store, row, true, null, categoryCache);
                    saveRemoteProduct(ygShopWooClient.createProduct(store, body), store.getStoreId());
                    created++;
                    continue;
                }
                JSONObject existing = ygShopWooClient.findProductBySku(store, row.getSku());
                if (existing == null)
                {
                    JSONObject body = buildProductBody(store, row, true, null, categoryCache);
                    saveRemoteProduct(ygShopWooClient.createProduct(store, body), store.getStoreId());
                    created++;
                }
                else if (updateSupport)
                {
                    String existingImage = null;
                    JSONArray existingImages = existing.getJSONArray("images");
                    if (existingImages != null && !existingImages.isEmpty())
                    {
                        existingImage = existingImages.getJSONObject(0).getString("src");
                    }
                    JSONObject body = buildProductBody(store, row, false, existingImage, categoryCache);
                    saveRemoteProduct(ygShopWooClient.updateProduct(store, existing.getLong("id"), body), store.getStoreId());
                    updated++;
                }
                else
                {
                    skipped++;
                    details.add("SKU " + row.getSku() + " 已存在，未更新");
                }
            }
            catch (Exception e)
            {
                failed++;
                details.add(describeRow(row) + "：" + e.getMessage());
            }
        }
        return buildImportResult(created, updated, skipped, failed, details);
    }

    @Override
    public int deleteYgShopWooProductByProductIds(Long[] productIds)
    {
        return ygShopWooProductMapper.deleteYgShopWooProductByProductIds(productIds);
    }

    @Override
    public void handleWebhook(String topic, JSONObject payload, Long storeId)
    {
        Long wooProductId = payload == null ? null : payload.getLong("id");
        if (wooProductId == null)
        {
            return;
        }
        if (topic != null && topic.endsWith(".deleted"))
        {
            ygShopWooProductMapper.deleteByStoreAndWooProductId(storeId, wooProductId);
            return;
        }
        ygShopWooProductMapper.upsertYgShopWooProduct(toProduct(payload, storeId));
    }

    /**
     * 保存店铺返回的商品并同步本地
     */
    private YgShopWooProduct saveRemoteProduct(JSONObject json, Long storeId)
    {
        YgShopWooProduct product = toProduct(json, storeId);
        ygShopWooProductMapper.upsertYgShopWooProduct(product);
        return product;
    }

    /**
     * 构造WooCommerce商品请求体
     *
     * @param create 是否新增（新增才传商品类型）
     * @param localImageUrl 本地主图地址（编辑时未修改则不下发图片，避免覆盖图库）
     * @param categoryCache 导入时的分类缓存，可为null
     */
    private JSONObject buildProductBody(YgShopStore store, YgShopWooProduct product, boolean create,
            String localImageUrl, Map<Long, Map<String, Long>> categoryCache)
    {
        JSONObject body = new JSONObject();
        body.put("name", StringUtils.trimToEmpty(product.getName()));
        if (create)
        {
            body.put("type", StringUtils.isNotEmpty(product.getType()) ? product.getType() : "simple");
            body.put("status", StringUtils.isNotEmpty(product.getStatus()) ? product.getStatus() : "publish");
        }
        else if (StringUtils.isNotEmpty(product.getStatus()))
        {
            body.put("status", product.getStatus());
        }
        if (product.getSku() != null)
        {
            body.put("sku", StringUtils.trimToEmpty(product.getSku()));
        }
        if (product.getRegularPrice() != null)
        {
            body.put("regular_price", plain(product.getRegularPrice()));
        }
        if (product.getSalePrice() != null)
        {
            body.put("sale_price", product.getSalePrice().compareTo(BigDecimal.ZERO) > 0
                    ? plain(product.getSalePrice()) : "");
        }
        if (product.getStockQuantity() != null)
        {
            body.put("manage_stock", true);
            body.put("stock_quantity", product.getStockQuantity());
        }
        else if (create)
        {
            body.put("manage_stock", false);
        }
        if (StringUtils.isNotEmpty(product.getStockStatus()))
        {
            body.put("stock_status", product.getStockStatus());
        }
        if (StringUtils.isNotEmpty(product.getDescription()))
        {
            body.put("description", product.getDescription());
        }
        String imageUrl = StringUtils.trimToEmpty(product.getImageUrl());
        if (StringUtils.isNotEmpty(imageUrl) && (create || !imageUrl.equals(StringUtils.trimToEmpty(localImageUrl))))
        {
            JSONArray images = new JSONArray();
            JSONObject image = new JSONObject();
            image.put("src", imageUrl);
            images.add(image);
            body.put("images", images);
        }
        body.put("categories", resolveCategoryIds(store, product.getCategories(), categoryCache));
        return body;
    }

    /**
     * 分类名称解析为WoCommerce分类ID，不存在的自动创建
     */
    private JSONArray resolveCategoryIds(YgShopStore store, String categoryNames,
            Map<Long, Map<String, Long>> categoryCache)
    {
        JSONArray result = new JSONArray();
        if (StringUtils.isEmpty(categoryNames))
        {
            return result;
        }
        Map<String, Long> categories;
        if (categoryCache != null)
        {
            categories = categoryCache.computeIfAbsent(store.getStoreId(), key -> loadCategories(store));
        }
        else
        {
            categories = loadCategories(store);
        }
        String[] names = categoryNames.split("[,，/|]");
        for (String item : names)
        {
            String name = StringUtils.trimToEmpty(item);
            if (StringUtils.isEmpty(name))
            {
                continue;
            }
            Long categoryId = categories.get(name.toLowerCase());
            if (categoryId == null)
            {
                JSONObject created = ygShopWooClient.createProductCategory(store, name);
                categoryId = created == null ? null : created.getLong("id");
                if (categoryId == null)
                {
                    throw new ServiceException("商品分类创建失败：" + name);
                }
                categories.put(name.toLowerCase(), categoryId);
            }
            JSONObject category = new JSONObject();
            category.put("id", categoryId);
            result.add(category);
        }
        return result;
    }

    /**
     * 加载店铺商品分类（名称转小写 -> ID）
     */
    private Map<String, Long> loadCategories(YgShopStore store)
    {
        Map<String, Long> categories = new HashMap<>();
        for (JSONObject category : ygShopWooClient.fetchProductCategories(store))
        {
            String name = StringUtils.trimToEmpty(category.getString("name"));
            Long id = category.getLong("id");
            if (StringUtils.isNotEmpty(name) && id != null)
            {
                categories.putIfAbsent(name.toLowerCase(), id);
            }
        }
        return categories;
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
     * 导入时按店铺ID或店铺名称解析店铺
     */
    private YgShopStore resolveImportStore(YgShopWooProduct row)
    {
        if (row.getStoreId() != null)
        {
            return requireStore(row.getStoreId());
        }
        String storeName = StringUtils.trimToEmpty(row.getStoreName());
        if (StringUtils.isEmpty(storeName))
        {
            throw new ServiceException("店铺ID和店铺名称不能同时为空");
        }
        List<YgShopStore> matched = new ArrayList<>();
        for (YgShopStore store : ygShopStoreService.selectSyncStores(null))
        {
            if (storeName.equalsIgnoreCase(StringUtils.trimToEmpty(store.getStoreName())))
            {
                matched.add(store);
            }
        }
        if (matched.isEmpty())
        {
            throw new ServiceException("店铺不存在或未启用：" + storeName);
        }
        if (matched.size() > 1)
        {
            throw new ServiceException("店铺名称重复，请改用店铺ID：" + storeName);
        }
        return matched.get(0);
    }

    /**
     * 导入结果说明
     */
    private String buildImportResult(int created, int updated, int skipped, int failed, List<String> details)
    {
        StringBuilder msg = new StringBuilder();
        msg.append("导入完成：新增 ").append(created).append(" 条，更新 ").append(updated)
                .append(" 条，跳过 ").append(skipped).append(" 条，失败 ").append(failed).append(" 条。");
        if (!details.isEmpty())
        {
            msg.append("<br/>明细：");
            int limit = Math.min(details.size(), IMPORT_DETAIL_LIMIT);
            for (int i = 0; i < limit; i++)
            {
                msg.append("<br/>").append(i + 1).append(". ")
                        .append(StringUtils.substring(details.get(i), 0, 200));
            }
            if (details.size() > limit)
            {
                msg.append("<br/>... 其余 ").append(details.size() - limit).append(" 条省略");
            }
        }
        return msg.toString();
    }

    /**
     * 导入行描述
     */
    private String describeRow(YgShopWooProduct row)
    {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotEmpty(row.getName()))
        {
            sb.append(row.getName());
        }
        if (StringUtils.isNotEmpty(row.getSku()))
        {
            if (sb.length() > 0)
            {
                sb.append(" ");
            }
            sb.append("(SKU:").append(row.getSku()).append(")");
        }
        return sb.length() == 0 ? "未知商品" : sb.toString();
    }

    /**
     * WooCommerce商品JSON转本地对象
     */
    private YgShopWooProduct toProduct(JSONObject json, Long storeId)
    {
        Date now = DateUtils.getNowDate();
        YgShopWooProduct product = new YgShopWooProduct();
        product.setStoreId(storeId);
        product.setWooProductId(json.getLong("id"));
        product.setName(StringUtils.substring(YgShopWooClient.plainText(json.getString("name")), 0, 490));
        product.setSku(StringUtils.substring(StringUtils.trimToEmpty(json.getString("sku")), 0, 190));
        product.setType(StringUtils.substring(StringUtils.trimToEmpty(json.getString("type")), 0, 30));
        product.setStatus(StringUtils.substring(StringUtils.trimToEmpty(json.getString("status")), 0, 30));
        product.setPrice(decimal(json.getString("price")));
        product.setRegularPrice(decimal(json.getString("regular_price")));
        product.setSalePrice(decimal(json.getString("sale_price")));
        product.setStockQuantity(json.getInteger("stock_quantity"));
        product.setStockStatus(StringUtils.substring(StringUtils.trimToEmpty(json.getString("stock_status")), 0, 30));
        product.setCategories(buildCategories(json.getJSONArray("categories")));
        JSONArray images = json.getJSONArray("images");
        if (images != null && !images.isEmpty())
        {
            JSONObject image = images.getJSONObject(0);
            product.setImageUrl(StringUtils.substring(StringUtils.trimToEmpty(image.getString("src")), 0, 490));
        }
        product.setPermalink(StringUtils.substring(StringUtils.trimToEmpty(json.getString("permalink")), 0, 490));
        product.setDateCreated(YgShopWooClient.parseShopDate(json.getString("date_created")));
        product.setDateModified(YgShopWooClient.parseShopDate(json.getString("date_modified")));
        product.setCreateTime(now);
        product.setUpdateTime(now);
        return product;
    }

    /**
     * 商品分类名称拼接
     */
    private String buildCategories(JSONArray categories)
    {
        if (categories == null || categories.isEmpty())
        {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < categories.size(); i++)
        {
            JSONObject category = categories.getJSONObject(i);
            if (i > 0)
            {
                sb.append(" / ");
            }
            sb.append(StringUtils.trimToEmpty(category.getString("name")));
        }
        return StringUtils.substring(sb.toString(), 0, 490);
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

    /**
     * 金额转字符串（保留两位小数）
     */
    private String plain(BigDecimal value)
    {
        return value == null ? "" : value.stripTrailingZeros().toPlainString();
    }
}
