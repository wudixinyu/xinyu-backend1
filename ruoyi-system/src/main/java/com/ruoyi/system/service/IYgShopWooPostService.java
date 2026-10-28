package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.YgShopWooPost;

/**
 * WooCommerce文章Service接口
 *
 * @author ruoyi
 * @date 2026-10-23
 */
public interface IYgShopWooPostService
{
    /**
     * 查询文章（含正文）
     */
    public YgShopWooPost selectYgShopWooPostByPostId(Long postId);

    /**
     * 查询文章列表（不含正文）
     */
    public List<YgShopWooPost> selectYgShopWooPostList(YgShopWooPost ygShopWooPost);

    /**
     * 同步文章（storeId为空时同步全部启用店铺）
     *
     * @return 同步条数
     */
    public int syncWooPost(Long storeId);

    /**
     * 批量删除文章
     */
    public int deleteYgShopWooPostByPostIds(Long[] postIds);
}
