package com.ruoyi.web.controller.ai;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.YgKbDocument;
import com.ruoyi.system.service.IYgKbDocumentService;

/**
 * 知识库文档Controller
 *
 * @author ruoyi
 * @date 2026-10-14
 */
@RestController
@RequestMapping("/ai/kb/document")
public class YgKbDocumentController extends BaseController
{
    @Autowired
    private IYgKbDocumentService ygKbDocumentService;

    /**
     * 查询知识库文档列表
     */
    @PreAuthorize("@ss.hasPermi('ai:kb:query')")
    @GetMapping("/list")
    public TableDataInfo list(YgKbDocument ygKbDocument)
    {
        startPage();
        List<YgKbDocument> list = ygKbDocumentService.selectYgKbDocumentList(ygKbDocument);
        return getDataTable(list);
    }

    /**
     * 上传文档（同步解析入库）
     */
    @PreAuthorize("@ss.hasPermi('ai:kb:upload')")
    @Log(title = "知识库文档", businessType = BusinessType.INSERT)
    @PostMapping("/upload")
    public AjaxResult upload(@RequestParam("kbId") Long kbId, @RequestParam("file") MultipartFile file)
    {
        YgKbDocument doc = ygKbDocumentService.uploadDocument(kbId, file, getUsername());
        return success(doc);
    }

    /**
     * 删除文档（删除后需重建索引以移除向量）
     */
    @PreAuthorize("@ss.hasPermi('ai:kb:upload')")
    @Log(title = "知识库文档", businessType = BusinessType.DELETE)
    @DeleteMapping("/{docIds}")
    public AjaxResult remove(@PathVariable Long[] docIds)
    {
        return toAjax(ygKbDocumentService.deleteYgKbDocumentByDocIds(docIds));
    }

    /**
     * 重建知识库索引
     */
    @PreAuthorize("@ss.hasPermi('ai:kb:rebuild')")
    @Log(title = "知识库文档", businessType = BusinessType.UPDATE)
    @PostMapping("/rebuild/{kbId}")
    public AjaxResult rebuild(@PathVariable("kbId") Long kbId)
    {
        ygKbDocumentService.rebuildIndex(kbId);
        return success();
    }
}
