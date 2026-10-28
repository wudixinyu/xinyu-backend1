package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.ColumnType;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * AI素材库对象 yg_media
 * 
 * @author ruoyi
 * @date 2026-10-14
 */
public class YgMedia extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 素材主键 */
    @Excel(name = "素材主键", cellType = ColumnType.NUMERIC)
    private Long mediaId;

    /** 素材名称（可重命名） */
    @Excel(name = "素材名称")
    private String mediaName;

    /** 上传时原始文件名 */
    @Excel(name = "原始文件名")
    private String originalName;

    /** 素材类型(1:图片 2:视频) */
    @Excel(name = "素材类型", cellType = ColumnType.NUMERIC)
    private Integer mediaType;

    /** 文件地址(/profile/...) */
    @Excel(name = "文件地址")
    private String fileUrl;

    /** 文件大小(字节) */
    @Excel(name = "文件大小", cellType = ColumnType.NUMERIC)
    private Long fileSize;

    /** 扩展名 */
    @Excel(name = "扩展名")
    private String ext;

    /** 分类/标签 */
    @Excel(name = "分类")
    private String category;

    /** 删除标志 */
    private String delFlag;

    public void setMediaId(Long mediaId) 
    {
        this.mediaId = mediaId;
    }

    public Long getMediaId() 
    {
        return mediaId;
    }

    public void setMediaName(String mediaName) 
    {
        this.mediaName = mediaName;
    }

    public String getMediaName() 
    {
        return mediaName;
    }

    public void setOriginalName(String originalName) 
    {
        this.originalName = originalName;
    }

    public String getOriginalName() 
    {
        return originalName;
    }

    public void setMediaType(Integer mediaType) 
    {
        this.mediaType = mediaType;
    }

    public Integer getMediaType() 
    {
        return mediaType;
    }

    public void setFileUrl(String fileUrl) 
    {
        this.fileUrl = fileUrl;
    }

    public String getFileUrl() 
    {
        return fileUrl;
    }

    public void setFileSize(Long fileSize) 
    {
        this.fileSize = fileSize;
    }

    public Long getFileSize() 
    {
        return fileSize;
    }

    public void setExt(String ext) 
    {
        this.ext = ext;
    }

    public String getExt() 
    {
        return ext;
    }

    public void setCategory(String category) 
    {
        this.category = category;
    }

    public String getCategory() 
    {
        return category;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("mediaId", getMediaId())
            .append("mediaName", getMediaName())
            .append("originalName", getOriginalName())
            .append("mediaType", getMediaType())
            .append("fileUrl", getFileUrl())
            .append("fileSize", getFileSize())
            .append("ext", getExt())
            .append("category", getCategory())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
