package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.ColumnType;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 知识库文档对象 yg_kb_document
 *
 * @author ruoyi
 * @date 2026-10-14
 */
public class YgKbDocument extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 文档主键 */
    @Excel(name = "文档主键", cellType = ColumnType.NUMERIC)
    private Long docId;

    /** 所属知识库ID */
    @Excel(name = "知识库ID", cellType = ColumnType.NUMERIC)
    private Long kbId;

    /** 原始文件名 */
    @Excel(name = "文件名")
    private String fileName;

    /** 存储路径 */
    private String filePath;

    /** 文件类型（txt/md/pdf/docx） */
    @Excel(name = "文件类型")
    private String fileType;

    /** 文件大小（字节） */
    @Excel(name = "文件大小", cellType = ColumnType.NUMERIC)
    private Long fileSize;

    /** 分块数 */
    @Excel(name = "分块数", cellType = ColumnType.NUMERIC)
    private Integer chunkCount;

    /** 索引状态（0待索引 1索引中 2成功 3失败） */
    @Excel(name = "索引状态", readConverterExp = "0=待索引,1=索引中,2=成功,3=失败")
    private String indexStatus;

    /** 失败原因 */
    @Excel(name = "失败原因")
    private String errorMsg;

    /** 删除标志 */
    private String delFlag;

    public void setDocId(Long docId)
    {
        this.docId = docId;
    }

    public Long getDocId()
    {
        return docId;
    }

    public void setKbId(Long kbId)
    {
        this.kbId = kbId;
    }

    public Long getKbId()
    {
        return kbId;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFilePath(String filePath)
    {
        this.filePath = filePath;
    }

    public String getFilePath()
    {
        return filePath;
    }

    public void setFileType(String fileType)
    {
        this.fileType = fileType;
    }

    public String getFileType()
    {
        return fileType;
    }

    public void setFileSize(Long fileSize)
    {
        this.fileSize = fileSize;
    }

    public Long getFileSize()
    {
        return fileSize;
    }

    public void setChunkCount(Integer chunkCount)
    {
        this.chunkCount = chunkCount;
    }

    public Integer getChunkCount()
    {
        return chunkCount;
    }

    public void setIndexStatus(String indexStatus)
    {
        this.indexStatus = indexStatus;
    }

    public String getIndexStatus()
    {
        return indexStatus;
    }

    public void setErrorMsg(String errorMsg)
    {
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg()
    {
        return errorMsg;
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
            .append("docId", getDocId())
            .append("kbId", getKbId())
            .append("fileName", getFileName())
            .append("filePath", getFilePath())
            .append("fileType", getFileType())
            .append("fileSize", getFileSize())
            .append("chunkCount", getChunkCount())
            .append("indexStatus", getIndexStatus())
            .append("errorMsg", getErrorMsg())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
