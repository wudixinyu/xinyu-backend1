package com.ruoyi.web.controller.shop;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.YgShopWooPost;
import com.ruoyi.system.service.IYgShopWooPostService;

/**
 * WooCommerce文章Controller
 *
 * @author ruoyi
 * @date 2026-10-23
 */
@RestController
@RequestMapping("/shop/woo/post")
public class YgShopWooPostController extends BaseController
{
    @Autowired
    private IYgShopWooPostService ygShopWooPostService;

    /**
     * 查询文章列表
     */
    @PreAuthorize("@ss.hasPermi('shop:woo:post:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgShopWooPost ygShopWooPost)
    {
        startPage();
        List<YgShopWooPost> list = ygShopWooPostService.selectYgShopWooPostList(ygShopWooPost);
        return getDataTable(list);
    }

    /**
     * 获取文章详细信息（含正文）
     */
    @PreAuthorize("@ss.hasPermi('shop:woo:post:query')")
    @GetMapping(value = "/{postId}")
    public AjaxResult getInfo(@PathVariable("postId") Long postId)
    {
        return success(ygShopWooPostService.selectYgShopWooPostByPostId(postId));
    }

    /**
     * 同步文章（不传店铺则同步全部启用店铺）
     */
    @PreAuthorize("@ss.hasPermi('shop:woo:post:sync')")
    @Log(title = "WooCommerce文章同步", businessType = BusinessType.OTHER)
    @PostMapping("/sync")
    public AjaxResult sync(Long storeId)
    {
        int count = ygShopWooPostService.syncWooPost(storeId);
        return success("同步完成，共更新 " + count + " 篇文章");
    }

    /**
     * 删除文章
     */
    @PreAuthorize("@ss.hasPermi('shop:woo:post:remove')")
    @Log(title = "WooCommerce文章", businessType = BusinessType.DELETE)
    @DeleteMapping("/{postIds}")
    public AjaxResult remove(@PathVariable Long[] postIds)
    {
        return toAjax(ygShopWooPostService.deleteYgShopWooPostByPostIds(postIds));
    }
}
