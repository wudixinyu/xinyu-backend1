package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 知识库模型配置对象 yg_kb_model_config（单行表，后台模型管理页面维护）
 *
 * @author ruoyi
 * @date 2026-10-14
 */
public class YgKbModelConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 模型提供方(ollama/openai/custom)，兼容旧数据，作为两个提供方未单独设置时的默认值 */
    private String provider;

    /** 对话模型提供方(ollama/openai/custom) */
    private String chatProvider;

    /** 向量化模型提供方(ollama/openai/custom) */
    private String embeddingProvider;

    /** Ollama服务地址 */
    private String ollamaBaseUrl;

    /** Ollama对话模型 */
    private String ollamaChatModel;

    /** Ollama温度 */
    private BigDecimal ollamaChatTemperature;

    /** Ollama向量化模型 */
    private String ollamaEmbeddingModel;

    /** Ollama向量维度 */
    private Integer ollamaEmbeddingDim;

    /** Ollama超时秒数 */
    private Integer ollamaTimeoutSeconds;

    /** llama-server/OpenAI兼容地址 */
    private String openaiBaseUrl;

    /** API Key */
    private String openaiApiKey;

    /** OpenAI兼容对话模型 */
    private String openaiChatModel;

    /** OpenAI兼容温度 */
    private BigDecimal openaiChatTemperature;

    /** OpenAI兼容向量化模型 */
    private String openaiEmbeddingModel;

    /** OpenAI兼容向量维度 */
    private Integer openaiEmbeddingDim;

    /** OpenAI兼容超时秒数 */
    private Integer openaiTimeoutSeconds;

    /** 对话用自定义API ID(yg_kb_custom_api.id) */
    private Long customChatApiId;

    /** 向量化用自定义API ID(yg_kb_custom_api.id) */
    private Long customEmbeddingApiId;

    /** 分块策略(recursive/paragraph/sentence/separator/markdown) */
    private String chunkStrategy;

    /** 自定义分隔符(strategy=separator时生效) */
    private String chunkSeparator;

    /** 分块最大长度(字符) */
    private Integer chunkMaxSize;

    /** 分块重叠长度(字符) */
    private Integer chunkOverlap;

    /** 检索返回片段数 */
    private Integer retrievalMaxResults;

    /** 检索最低相似度 */
    private BigDecimal retrievalMinScore;

    /** 更新者 */
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getProvider()
    {
        return provider;
    }

    public void setProvider(String provider)
    {
        this.provider = provider;
    }

    public String getChatProvider()
    {
        return chatProvider;
    }

    public void setChatProvider(String chatProvider)
    {
        this.chatProvider = chatProvider;
    }

    public String getEmbeddingProvider()
    {
        return embeddingProvider;
    }

    public void setEmbeddingProvider(String embeddingProvider)
    {
        this.embeddingProvider = embeddingProvider;
    }

    public String getOllamaBaseUrl()
    {
        return ollamaBaseUrl;
    }

    public void setOllamaBaseUrl(String ollamaBaseUrl)
    {
        this.ollamaBaseUrl = ollamaBaseUrl;
    }

    public String getOllamaChatModel()
    {
        return ollamaChatModel;
    }

    public void setOllamaChatModel(String ollamaChatModel)
    {
        this.ollamaChatModel = ollamaChatModel;
    }

    public BigDecimal getOllamaChatTemperature()
    {
        return ollamaChatTemperature;
    }

    public void setOllamaChatTemperature(BigDecimal ollamaChatTemperature)
    {
        this.ollamaChatTemperature = ollamaChatTemperature;
    }

    public String getOllamaEmbeddingModel()
    {
        return ollamaEmbeddingModel;
    }

    public void setOllamaEmbeddingModel(String ollamaEmbeddingModel)
    {
        this.ollamaEmbeddingModel = ollamaEmbeddingModel;
    }

    public Integer getOllamaEmbeddingDim()
    {
        return ollamaEmbeddingDim;
    }

    public void setOllamaEmbeddingDim(Integer ollamaEmbeddingDim)
    {
        this.ollamaEmbeddingDim = ollamaEmbeddingDim;
    }

    public Integer getOllamaTimeoutSeconds()
    {
        return ollamaTimeoutSeconds;
    }

    public void setOllamaTimeoutSeconds(Integer ollamaTimeoutSeconds)
    {
        this.ollamaTimeoutSeconds = ollamaTimeoutSeconds;
    }

    public String getOpenaiBaseUrl()
    {
        return openaiBaseUrl;
    }

    public void setOpenaiBaseUrl(String openaiBaseUrl)
    {
        this.openaiBaseUrl = openaiBaseUrl;
    }

    public String getOpenaiApiKey()
    {
        return openaiApiKey;
    }

    public void setOpenaiApiKey(String openaiApiKey)
    {
        this.openaiApiKey = openaiApiKey;
    }

    public String getOpenaiChatModel()
    {
        return openaiChatModel;
    }

    public void setOpenaiChatModel(String openaiChatModel)
    {
        this.openaiChatModel = openaiChatModel;
    }

    public BigDecimal getOpenaiChatTemperature()
    {
        return openaiChatTemperature;
    }

    public void setOpenaiChatTemperature(BigDecimal openaiChatTemperature)
    {
        this.openaiChatTemperature = openaiChatTemperature;
    }

    public String getOpenaiEmbeddingModel()
    {
        return openaiEmbeddingModel;
    }

    public void setOpenaiEmbeddingModel(String openaiEmbeddingModel)
    {
        this.openaiEmbeddingModel = openaiEmbeddingModel;
    }

    public Integer getOpenaiEmbeddingDim()
    {
        return openaiEmbeddingDim;
    }

    public void setOpenaiEmbeddingDim(Integer openaiEmbeddingDim)
    {
        this.openaiEmbeddingDim = openaiEmbeddingDim;
    }

    public Integer getOpenaiTimeoutSeconds()
    {
        return openaiTimeoutSeconds;
    }

    public void setOpenaiTimeoutSeconds(Integer openaiTimeoutSeconds)
    {
        this.openaiTimeoutSeconds = openaiTimeoutSeconds;
    }

    public Long getCustomChatApiId()
    {
        return customChatApiId;
    }

    public void setCustomChatApiId(Long customChatApiId)
    {
        this.customChatApiId = customChatApiId;
    }

    public Long getCustomEmbeddingApiId()
    {
        return customEmbeddingApiId;
    }

    public void setCustomEmbeddingApiId(Long customEmbeddingApiId)
    {
        this.customEmbeddingApiId = customEmbeddingApiId;
    }

    public String getChunkStrategy()
    {
        return chunkStrategy;
    }

    public void setChunkStrategy(String chunkStrategy)
    {
        this.chunkStrategy = chunkStrategy;
    }

    public String getChunkSeparator()
    {
        return chunkSeparator;
    }

    public void setChunkSeparator(String chunkSeparator)
    {
        this.chunkSeparator = chunkSeparator;
    }

    public Integer getChunkMaxSize()
    {
        return chunkMaxSize;
    }

    public void setChunkMaxSize(Integer chunkMaxSize)
    {
        this.chunkMaxSize = chunkMaxSize;
    }

    public Integer getChunkOverlap()
    {
        return chunkOverlap;
    }

    public void setChunkOverlap(Integer chunkOverlap)
    {
        this.chunkOverlap = chunkOverlap;
    }

    public Integer getRetrievalMaxResults()
    {
        return retrievalMaxResults;
    }

    public void setRetrievalMaxResults(Integer retrievalMaxResults)
    {
        this.retrievalMaxResults = retrievalMaxResults;
    }

    public BigDecimal getRetrievalMinScore()
    {
        return retrievalMinScore;
    }

    public void setRetrievalMinScore(BigDecimal retrievalMinScore)
    {
        this.retrievalMinScore = retrievalMinScore;
    }

    @Override
    public String getUpdateBy()
    {
        return updateBy;
    }

    @Override
    public void setUpdateBy(String updateBy)
    {
        this.updateBy = updateBy;
    }

    @Override
    public Date getUpdateTime()
    {
        return updateTime;
    }

    @Override
    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }
}
