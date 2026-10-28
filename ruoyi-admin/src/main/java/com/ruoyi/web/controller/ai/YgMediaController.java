package com.ruoyi.web.controller.ai;

import java.util.Arrays;
import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.file.InvalidExtensionException;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.MimeTypeUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.YgMedia;
import com.ruoyi.system.service.IYgMediaService;

/**
 * AI素材库Controller
 * 
 * @author ruoyi
 * @date 2026-10-14
 */
@RestController
@RequestMapping("/ai/media")
public class YgMediaController extends BaseController
{
    @Autowired
    private IYgMediaService ygMediaService;

    /** 允许上传的图片+视频扩展名 */
    private static final String[] ALLOWED_EXTENSION = concatExtension(MimeTypeUtils.IMAGE_EXTENSION, MimeTypeUtils.VIDEO_EXTENSION);

    private static String[] concatExtension(String[] imageExt, String[] videoExt)
    {
        String[] merged = Arrays.copyOf(imageExt, imageExt.length + videoExt.length);
        System.arraycopy(videoExt, 0, merged, imageExt.length, videoExt.length);
        return merged;
    }

    /**
     * 查询AI素材库列表
     */
    @PreAuthorize("@ss.hasPermi('ai:media:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgMedia ygMedia)
    {
        startPage();
        List<YgMedia> list = ygMediaService.selectYgMediaList(ygMedia);
        return getDataTable(list);
    }

    /**
     * 导出AI素材库列表
     */
    @PreAuthorize("@ss.hasPermi('ai:media:list')")
    @Log(title = "AI素材库", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, YgMedia ygMedia)
    {
        List<YgMedia> list = ygMediaService.selectYgMediaList(ygMedia);
        ExcelUtil<YgMedia> util = new ExcelUtil<YgMedia>(YgMedia.class);
        util.exportExcel(response, list, "AI素材库数据");
    }

    /**
     * 获取AI素材库详细信息
     */
    @PreAuthorize("@ss.hasPermi('ai:media:query')")
    @GetMapping(value = "/{mediaId}")
    public AjaxResult getInfo(@PathVariable("mediaId") Long mediaId)
    {
        return success(ygMediaService.selectYgMediaByMediaId(mediaId));
    }

    /**
     * 上传素材（存盘+落库一次完成）
     */
    @PreAuthorize("@ss.hasPermi('ai:media:upload')")
    @Log(title = "AI素材库", businessType = BusinessType.INSERT)
    @PostMapping("/upload")
    public AjaxResult upload(@RequestParam("file") MultipartFile file,
                             @RequestParam(value = "category", required = false) String category)
    {
        try
        {
            String fileUrl = FileUploadUtils.upload(RuoYiConfig.getUploadPath(), file, ALLOWED_EXTENSION);
            String ext = FileUploadUtils.getExtension(file).toLowerCase();
            YgMedia ygMedia = new YgMedia();
            ygMedia.setMediaName(file.getOriginalFilename());
            ygMedia.setOriginalName(file.getOriginalFilename());
            ygMedia.setMediaType(isVideoExtension(ext) ? 2 : 1);
            ygMedia.setFileUrl(fileUrl);
            ygMedia.setFileSize(file.getSize());
            ygMedia.setExt(ext);
            ygMedia.setCategory(category == null ? "" : category);
            ygMedia.setCreateBy(getUsername());
            ygMediaService.insertYgMedia(ygMedia);
            AjaxResult ajax = AjaxResult.success();
            ajax.put("media", ygMedia);
            return ajax;
        }
        catch (InvalidExtensionException e)
        {
            return AjaxResult.error("仅支持上传图片或视频文件（bmp/gif/jpg/jpeg/png/mp4/avi/rmvb）");
        }
        catch (Exception e)
        {
            return AjaxResult.error(e.getMessage());
        }
    }

    private static boolean isVideoExtension(String ext)
    {
        return Arrays.asList(MimeTypeUtils.VIDEO_EXTENSION).contains(ext);
    }

    /**
     * 修改AI素材库（重命名/分类）
     */
    @PreAuthorize("@ss.hasPermi('ai:media:edit')")
    @Log(title = "AI素材库", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody YgMedia ygMedia)
    {
        ygMedia.setUpdateBy(getUsername());
        return toAjax(ygMediaService.updateYgMedia(ygMedia));
    }

    /**
     * 删除AI素材库（仅删除记录，不删除磁盘文件，避免历史画布引用失效）
     */
    @PreAuthorize("@ss.hasPermi('ai:media:remove')")
    @Log(title = "AI素材库", businessType = BusinessType.DELETE)
	@DeleteMapping("/{mediaIds}")
    public AjaxResult remove(@PathVariable Long[] mediaIds)
    {
        return toAjax(ygMediaService.deleteYgMediaByMediaIds(mediaIds));
    }
}
