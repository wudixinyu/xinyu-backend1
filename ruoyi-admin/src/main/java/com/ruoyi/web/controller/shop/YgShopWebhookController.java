package com.ruoyi.web.controller.shop;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.YgShopWebhook;
import com.ruoyi.system.service.IYgShopWebhookService;

/**
 * 店铺WebhookController
 *
 * @author ruoyi
 * @date 2026-10-23
 */
@RestController
@RequestMapping("/shop/webhook")
public class YgShopWebhookController extends BaseController
{
    @Autowired
    private IYgShopWebhookService ygShopWebhookService;

    /**
     * 查询Webhook列表
     */
    @PreAuthorize("@ss.hasPermi('shop:webhook:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgShopWebhook ygShopWebhook)
    {
        startPage();
        List<YgShopWebhook> list = ygShopWebhookService.selectYgShopWebhookList(ygShopWebhook);
        return getDataTable(list);
    }

    /**
     * 获取Webhook详细信息
     */
    @PreAuthorize("@ss.hasPermi('shop:webhook:query')")
    @GetMapping("/{webhookId}")
    public AjaxResult getInfo(@PathVariable("webhookId") Long webhookId)
    {
        return success(ygShopWebhookService.selectYgShopWebhookByWebhookId(webhookId));
    }

    /**
     * 新增Webhook（同时创建到WooCommerce）
     */
    @PreAuthorize("@ss.hasPermi('shop:webhook:add')")
    @Log(title = "店铺Webhook", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody YgShopWebhook ygShopWebhook)
    {
        ygShopWebhook.setCreateBy(getUsername());
        return success(ygShopWebhookService.createYgShopWebhook(ygShopWebhook));
    }

    /**
     * 修改Webhook（同时更新到WooCommerce）
     */
    @PreAuthorize("@ss.hasPermi('shop:webhook:edit')")
    @Log(title = "店铺Webhook", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody YgShopWebhook ygShopWebhook)
    {
        ygShopWebhook.setUpdateBy(getUsername());
        return success(ygShopWebhookService.updateYgShopWebhook(ygShopWebhook));
    }

    /**
     * 启停Webhook
     */
    @PreAuthorize("@ss.hasPermi('shop:webhook:edit')")
    @Log(title = "店铺Webhook状态", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody YgShopWebhook ygShopWebhook)
    {
        return toAjax(ygShopWebhookService.changeStatus(ygShopWebhook.getWebhookId(), ygShopWebhook.getStatus()));
    }

    /**
     * 删除Webhook（同时删除WooCommerce中的Webhook）
     */
    @PreAuthorize("@ss.hasPermi('shop:webhook:remove')")
    @Log(title = "店铺Webhook", businessType = BusinessType.DELETE)
    @DeleteMapping("/{webhookIds}")
    public AjaxResult remove(@PathVariable Long[] webhookIds)
    {
        return toAjax(ygShopWebhookService.deleteYgShopWebhookByWebhookIds(webhookIds));
    }

    /**
     * 从店铺同步Webhook
     */
    @PreAuthorize("@ss.hasPermi('shop:webhook:sync')")
    @Log(title = "店铺Webhook同步", businessType = BusinessType.OTHER)
    @PostMapping("/sync")
    public AjaxResult sync(Long storeId)
    {
        int count = ygShopWebhookService.syncYgShopWebhook(storeId);
        return success("同步完成，共更新 " + count + " 个Webhook");
    }

    /**
     * 接收WooCommerce Webhook推送（匿名访问，使用HMAC-SHA256校验签名）
     */
    @Anonymous
    @PostMapping("/receive/{storeId}")
    public ResponseEntity<AjaxResult> receive(@PathVariable("storeId") Long storeId,
            @RequestBody(required = false) String payload,
            @RequestHeader(value = "X-WC-Webhook-ID", required = false) String webhookId,
            @RequestHeader(value = "X-WC-Webhook-Topic", required = false) String topic,
            @RequestHeader(value = "X-WC-Webhook-Signature", required = false) String signature)
    {
        try
        {
            ygShopWebhookService.receiveWebhook(storeId, webhookId, topic, signature, payload == null ? "" : payload);
            return ResponseEntity.ok(AjaxResult.success("OK"));
        }
        catch (ServiceException e)
        {
            boolean authFailed = e.getMessage() != null && e.getMessage().contains("签名");
            HttpStatus status = authFailed ? HttpStatus.UNAUTHORIZED : HttpStatus.INTERNAL_SERVER_ERROR;
            return ResponseEntity.status(status).body(AjaxResult.error(e.getMessage()));
        }
        catch (Exception e)
        {
            logger.error("Webhook接收失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AjaxResult.error("Webhook处理失败：" + e.getMessage()));
        }
    }
}
