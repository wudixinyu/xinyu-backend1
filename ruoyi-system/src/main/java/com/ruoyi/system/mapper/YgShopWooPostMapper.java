package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.YgShopWooPost;

/**
 * WooCommerce文章Mapper接口
 *
 * @author ruoyi
 * @date 2026-10-23
 */
public interface YgShopWooPostMapper
{
    /**
     * 查询文章
     */
    public YgShopWooPost selectYgShopWooPostByPostId(Long postId);

    /**
     * 查询文章列表（不返回正文）
     */
    public List<YgShopWooPost> selectYgShopWooPostList(YgShopWooPost ygShopWooPost);

    /**
     * 按店铺+远端文章ID新增或更新
     */
    public int upsertYgShopWooPost(YgShopWooPost ygShopWooPost);

    /**
     * 批量删除文章
     */
    public int deleteYgShopWooPostByPostIds(Long[] postIds);
}
