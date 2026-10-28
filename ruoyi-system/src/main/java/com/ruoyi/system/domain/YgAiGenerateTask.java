package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * AI生成任务对象 yg_ai_generate_task
 * 
 * @author ruoyi
 * @date 2026-10-14
 */
public class YgAiGenerateTask extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 任务主键 */
    private Long taskId;

    /** 来源画布ID(0表示非画布发起) */
    private Long canvasId;

    /** 来源画布节点ID */
    private String nodeId;

    /** 使用模型ID(yg_ai_model) */
    private Long modelId;

    /** 生成类型(1:文本 2:图片 3:视频) */
    private Integer generateType;

    /** 提示词 */
    private String prompt;

    /** 状态（0排队 1生成中 2成功 3失败） */
    private String status;

    /** 文本生成结果 */
    private String resultText;

    /** 媒体结果地址(/profile/...) */
    private String resultUrl;

    /** 生成的素材ID(yg_media) */
    private Long mediaId;

    /** 失败原因 */
    private String errorMsg;

    public void setTaskId(Long taskId) 
    {
        this.taskId = taskId;
    }

    public Long getTaskId() 
    {
        return taskId;
    }

    public void setCanvasId(Long canvasId) 
    {
        this.canvasId = canvasId;
    }

    public Long getCanvasId() 
    {
        return canvasId;
    }

    public void setNodeId(String nodeId) 
    {
        this.nodeId = nodeId;
    }

    public String getNodeId() 
    {
        return nodeId;
    }

    public void setModelId(Long modelId) 
    {
        this.modelId = modelId;
    }

    public Long getModelId() 
    {
        return modelId;
    }

    public void setGenerateType(Integer generateType) 
    {
        this.generateType = generateType;
    }

    public Integer getGenerateType() 
    {
        return generateType;
    }

    public void setPrompt(String prompt) 
    {
        this.prompt = prompt;
    }

    public String getPrompt() 
    {
        return prompt;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setResultText(String resultText) 
    {
        this.resultText = resultText;
    }

    public String getResultText() 
    {
        return resultText;
    }

    public void setResultUrl(String resultUrl) 
    {
        this.resultUrl = resultUrl;
    }

    public String getResultUrl() 
    {
        return resultUrl;
    }

    public void setMediaId(Long mediaId) 
    {
        this.mediaId = mediaId;
    }

    public Long getMediaId() 
    {
        return mediaId;
    }

    public void setErrorMsg(String errorMsg) 
    {
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() 
    {
        return errorMsg;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("taskId", getTaskId())
            .append("canvasId", getCanvasId())
            .append("nodeId", getNodeId())
            .append("modelId", getModelId())
            .append("generateType", getGenerateType())
            .append("prompt", getPrompt())
            .append("status", getStatus())
            .append("resultUrl", getResultUrl())
            .append("mediaId", getMediaId())
            .append("errorMsg", getErrorMsg())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
