package com.ruoyi.system.domain;

import java.math.BigDecimal;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 知识库自定义API配置对象 yg_kb_custom_api（OpenAI兼容，可维护多条，后台模型管理页面维护）
 *
 * @author ruoyi
 * @date 2026-10-16
 */
public class YgKbCustomApi extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 名称（下拉展示用） */
    private String name;

    /** 服务地址(OpenAI兼容) */
    private String baseUrl;

    /** API Key */
    private String apiKey;

    /** 对话模型 */
    private String chatModel;

    /** 温度 */
    private BigDecimal chatTemperature;

    /** 向量化模型 */
    private String embeddingModel;

    /** 向量维度 */
    private Integer embeddingDim;

    /** 超时秒数 */
    private Integer timeoutSeconds;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getBaseUrl()
    {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl)
    {
        this.baseUrl = baseUrl;
    }

    public String getApiKey()
    {
        return apiKey;
    }

    public void setApiKey(String apiKey)
    {
        this.apiKey = apiKey;
    }

    public String getChatModel()
    {
        return chatModel;
    }

    public void setChatModel(String chatModel)
    {
        this.chatModel = chatModel;
    }

    public BigDecimal getChatTemperature()
    {
        return chatTemperature;
    }

    public void setChatTemperature(BigDecimal chatTemperature)
    {
        this.chatTemperature = chatTemperature;
    }

    public String getEmbeddingModel()
    {
        return embeddingModel;
    }

    public void setEmbeddingModel(String embeddingModel)
    {
        this.embeddingModel = embeddingModel;
    }

    public Integer getEmbeddingDim()
    {
        return embeddingDim;
    }

    public void setEmbeddingDim(Integer embeddingDim)
    {
        this.embeddingDim = embeddingDim;
    }

    public Integer getTimeoutSeconds()
    {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(Integer timeoutSeconds)
    {
        this.timeoutSeconds = timeoutSeconds;
    }
}
