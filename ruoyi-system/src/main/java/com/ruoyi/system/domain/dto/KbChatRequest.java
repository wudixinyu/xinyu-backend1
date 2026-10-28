package com.ruoyi.system.domain.dto;

/**
 * 知识库问答请求
 *
 * @author ruoyi
 * @date 2026-10-14
 */
public class KbChatRequest
{
    /** 知识库ID */
    private Long kbId;

    /** 问题 */
    private String question;

    public Long getKbId()
    {
        return kbId;
    }

    public void setKbId(Long kbId)
    {
        this.kbId = kbId;
    }

    public String getQuestion()
    {
        return question;
    }

    public void setQuestion(String question)
    {
        this.question = question;
    }
}
