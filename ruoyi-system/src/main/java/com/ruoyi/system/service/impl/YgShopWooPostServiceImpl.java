package com.ruoyi.system.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YgShopStore;
import com.ruoyi.system.domain.YgShopWooPost;
import com.ruoyi.system.mapper.YgShopWooPostMapper;
import com.ruoyi.system.service.IYgShopStoreService;
import com.ruoyi.system.service.IYgShopWooPostService;
import com.ruoyi.system.service.shop.YgShopWooClient;

/**
 * WooCommerce文章Service业务层处理
 *
 * @author ruoyi
 * @date 2026-10-23
 */
@Service
public class YgShopWooPostServiceImpl implements IYgShopWooPostService
{
    @Autowired
    private YgShopWooPostMapper ygShopWooPostMapper;

    @Autowired
    private IYgShopStoreService ygShopStoreService;

    @Autowired
    private YgShopWooClient ygShopWooClient;

    @Override
    public YgShopWooPost selectYgShopWooPostByPostId(Long postId)
    {
        return ygShopWooPostMapper.selectYgShopWooPostByPostId(postId);
    }

    @Override
    public List<YgShopWooPost> selectYgShopWooPostList(YgShopWooPost ygShopWooPost)
    {
        return ygShopWooPostMapper.selectYgShopWooPostList(ygShopWooPost);
    }

    @Override
    public int syncWooPost(Long storeId)
    {
        List<YgShopStore> stores = ygShopStoreService.selectSyncStores(storeId);
        int count = 0;
        for (YgShopStore store : stores)
        {
            List<JSONObject> posts = ygShopWooClient.fetchPosts(store);
            for (JSONObject json : posts)
            {
                ygShopWooPostMapper.upsertYgShopWooPost(toPost(json, store.getStoreId()));
                count++;
            }
            ygShopStoreService.updateLastSyncTime(store.getStoreId());
        }
        return count;
    }

    @Override
    public int deleteYgShopWooPostByPostIds(Long[] postIds)
    {
        return ygShopWooPostMapper.deleteYgShopWooPostByPostIds(postIds);
    }

    /**
     * WordPress文章JSON转本地对象
     */
    private YgShopWooPost toPost(JSONObject json, Long storeId)
    {
        Date now = DateUtils.getNowDate();
        YgShopWooPost post = new YgShopWooPost();
        post.setStoreId(storeId);
        post.setWooPostId(json.getLong("id"));
        post.setTitle(StringUtils.substring(plainText(json.getJSONObject("title"), "rendered"), 0, 490));
        post.setSlug(StringUtils.substring(StringUtils.trimToEmpty(json.getString("slug")), 0, 250));
        post.setStatus(StringUtils.substring(StringUtils.trimToEmpty(json.getString("status")), 0, 30));
        post.setExcerpt(StringUtils.substring(plainText(json.getJSONObject("excerpt"), "rendered"), 0, 990));
        post.setContent(StringUtils.substring(plainText(json.getJSONObject("content"), "rendered"), 0, 100000));
        post.setLink(StringUtils.substring(StringUtils.trimToEmpty(json.getString("link")), 0, 490));
        post.setDateCreated(YgShopWooClient.parseShopDate(json.getString("date")));
        post.setDateModified(YgShopWooClient.parseShopDate(json.getString("modified")));
        post.setCreateTime(now);
        post.setUpdateTime(now);
        return post;
    }

    /**
     * 读取 {field: {rendered: "..."}} 结构中的文本
     */
    private String plainText(JSONObject object, String field)
    {
        if (object == null)
        {
            return "";
        }
        return YgShopWooClient.plainText(object.getString(field));
    }
}
