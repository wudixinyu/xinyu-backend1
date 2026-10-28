package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.ColumnType;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 画布对象 yg_canvas
 * 
 * @author ruoyi
 * @date 2026-10-14
 */
public class YgCanvas extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 画布主键 */
    @Excel(name = "画布主键", cellType = ColumnType.NUMERIC)
    private Long canvasId;

    /** 画布名称 */
    @Excel(name = "画布名称")
    private String canvasName;

    /** 画布JSON（fabric.js toJSON，列表查询不返回） */
    private String content;

    /** 缩略图地址(/profile/...) */
    @Excel(name = "缩略图")
    private String thumbnail;

    /** 画布宽度(px) */
    @Excel(name = "画布宽度", cellType = ColumnType.NUMERIC)
    private Integer canvasWidth;

    /** 画布高度(px) */
    @Excel(name = "画布高度", cellType = ColumnType.NUMERIC)
    private Integer canvasHeight;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 删除标志 */
    private String delFlag;

    public void setCanvasId(Long canvasId) 
    {
        this.canvasId = canvasId;
    }

    public Long getCanvasId() 
    {
        return canvasId;
    }

    public void setCanvasName(String canvasName) 
    {
        this.canvasName = canvasName;
    }

    public String getCanvasName() 
    {
        return canvasName;
    }

    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }

    public void setThumbnail(String thumbnail) 
    {
        this.thumbnail = thumbnail;
    }

    public String getThumbnail() 
    {
        return thumbnail;
    }

    public void setCanvasWidth(Integer canvasWidth) 
    {
        this.canvasWidth = canvasWidth;
    }

    public Integer getCanvasWidth() 
    {
        return canvasWidth;
    }

    public void setCanvasHeight(Integer canvasHeight) 
    {
        this.canvasHeight = canvasHeight;
    }

    public Integer getCanvasHeight() 
    {
        return canvasHeight;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
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
            .append("canvasId", getCanvasId())
            .append("canvasName", getCanvasName())
            .append("thumbnail", getThumbnail())
            .append("canvasWidth", getCanvasWidth())
            .append("canvasHeight", getCanvasHeight())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
