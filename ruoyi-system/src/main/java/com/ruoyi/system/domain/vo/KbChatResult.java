package com.ruoyi.system.domain.vo;

import java.util.List;

/**
 * 知识库问答结果
 *
 * @author ruoyi
 * @date 2026-10-14
 */
public class KbChatResult
{
    /** 回答内容 */
    private String answer;

    /** 引用来源 */
    private List<KbSourceVo> sources;

    /** 耗时（毫秒） */
    private long costMs;

    public String getAnswer()
    {
        return answer;
    }

    public void setAnswer(String answer)
    {
        this.answer = answer;
    }

    public List<KbSourceVo> getSources()
    {
        return sources;
    }

    public void setSources(List<KbSourceVo> sources)
    {
        this.sources = sources;
    }

    public long getCostMs()
    {
        return costMs;
    }

    public void setCostMs(long costMs)
    {
        this.costMs = costMs;
    }
}
