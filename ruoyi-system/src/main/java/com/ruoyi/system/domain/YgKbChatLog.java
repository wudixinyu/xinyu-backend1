package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 知识库问答日志对象 yg_kb_chat_log
 *
 * @author ruoyi
 * @date 2026-10-14
 */
public class YgKbChatLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 日志主键 */
    private Long logId;

    /** 知识库ID */
    private Long kbId;

    /** 问题 */
    private String question;

    /** 回答 */
    private String answer;

    /** 引用来源（JSON） */
    private String sourceJson;

    /** 耗时（毫秒） */
    private Long costMs;

    public Long getLogId()
    {
        return logId;
    }

    public void setLogId(Long logId)
    {
        this.logId = logId;
    }

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

    public String getAnswer()
    {
        return answer;
    }

    public void setAnswer(String answer)
    {
        this.answer = answer;
    }

    public String getSourceJson()
    {
        return sourceJson;
    }

    public void setSourceJson(String sourceJson)
    {
        this.sourceJson = sourceJson;
    }

    public Long getCostMs()
    {
        return costMs;
    }

    public void setCostMs(Long costMs)
    {
        this.costMs = costMs;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("logId", getLogId())
            .append("kbId", getKbId())
            .append("question", getQuestion())
            .append("answer", getAnswer())
            .append("sourceJson", getSourceJson())
            .append("costMs", getCostMs())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .toString();
    }
}
