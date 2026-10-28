package com.ruoyi.system.domain.vo;

/**
 * 知识库问答引用来源
 *
 * @author ruoyi
 * @date 2026-10-14
 */
public class KbSourceVo
{
    /** 文档ID */
    private Long documentId;

    /** 文件名 */
    private String fileName;

    /** 相似度分数 */
    private Double score;

    /** 片段内容（截断展示） */
    private String snippet;

    public Long getDocumentId()
    {
        return documentId;
    }

    public void setDocumentId(Long documentId)
    {
        this.documentId = documentId;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public Double getScore()
    {
        return score;
    }

    public void setScore(Double score)
    {
        this.score = score;
    }

    public String getSnippet()
    {
        return snippet;
    }

    public void setSnippet(String snippet)
    {
        this.snippet = snippet;
    }
}
