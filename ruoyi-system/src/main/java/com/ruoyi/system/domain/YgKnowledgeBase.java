package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.ColumnType;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 知识库对象 yg_knowledge_base
 *
 * @author ruoyi
 * @date 2026-10-14
 */
public class YgKnowledgeBase extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 知识库主键 */
    @Excel(name = "知识库主键", cellType = ColumnType.NUMERIC)
    private Long kbId;

    /** 知识库名称 */
    @Excel(name = "知识库名称")
    private String kbName;

    /** 描述 */
    @Excel(name = "描述")
    private String description;

    /** 向量化模型（建库时固化） */
    @Excel(name = "向量化模型")
    private String embeddingModel;

    /** 向量维度 */
    @Excel(name = "向量维度", cellType = ColumnType.NUMERIC)
    private Integer embeddingDim;

    /** 文档数 */
    @Excel(name = "文档数", cellType = ColumnType.NUMERIC)
    private Integer docCount;

    /** 分块数 */
    @Excel(name = "分块数", cellType = ColumnType.NUMERIC)
    private Integer chunkCount;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 删除标志 */
    private String delFlag;

    public void setKbId(Long kbId)
    {
        this.kbId = kbId;
    }

    public Long getKbId()
    {
        return kbId;
    }

    public void setKbName(String kbName)
    {
        this.kbName = kbName;
    }

    public String getKbName()
    {
        return kbName;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }

    public void setEmbeddingModel(String embeddingModel)
    {
        this.embeddingModel = embeddingModel;
    }

    public String getEmbeddingModel()
    {
        return embeddingModel;
    }

    public void setEmbeddingDim(Integer embeddingDim)
    {
        this.embeddingDim = embeddingDim;
    }

    public Integer getEmbeddingDim()
    {
        return embeddingDim;
    }

    public void setDocCount(Integer docCount)
    {
        this.docCount = docCount;
    }

    public Integer getDocCount()
    {
        return docCount;
    }

    public void setChunkCount(Integer chunkCount)
    {
        this.chunkCount = chunkCount;
    }

    public Integer getChunkCount()
    {
        return chunkCount;
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
            .append("kbId", getKbId())
            .append("kbName", getKbName())
            .append("description", getDescription())
            .append("embeddingModel", getEmbeddingModel())
            .append("embeddingDim", getEmbeddingDim())
            .append("docCount", getDocCount())
            .append("chunkCount", getChunkCount())
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
