package com.ruoyi.system.kb.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.StringUtils;

/**
 * 知识库（本地 RAG）配置属性
 *
 * @author ruoyi
 * @date 2026-10-14
 */
@Component
@ConfigurationProperties(prefix = "yg.kb")
public class KbProperties
{
    /** 模型提供方：ollama | openai（llama.cpp llama-server OpenAI 兼容接口）| custom（自定义API，OpenAI兼容），作为对话/向量化提供方未单独配置时的默认值 */
    private String provider = "ollama";

    /** 对话模型提供方（为空回退 provider） */
    private String chatProvider = "";

    /** 向量化模型提供方（为空回退 provider） */
    private String embeddingProvider = "";

    private Ollama ollama = new Ollama();

    private Openai openai = new Openai();

    /** 对话用自定义API（运行时由 KbModelManager 依据 customChatApiId 从数据库解析填充） */
    private CustomApi customChat = new CustomApi();

    /** 向量化用自定义API（运行时由 KbModelManager 依据 customEmbeddingApiId 从数据库解析填充） */
    private CustomApi customEmbedding = new CustomApi();

    /** 对话用自定义API ID（yg_kb_custom_api.id，provider=custom 时生效） */
    private Long customChatApiId;

    /** 向量化用自定义API ID（yg_kb_custom_api.id，provider=custom 时生效） */
    private Long customEmbeddingApiId;

    /** 分块参数 */
    private Chunk chunk = new Chunk();

    /** 检索参数 */
    private Retrieval retrieval = new Retrieval();

    /** 向量持久化目录（为空默认 {ruoyi.profile}/kb/store） */
    private String storeDir = "";

    /** 文档上传目录（为空默认 {ruoyi.profile}/kb/upload） */
    private String uploadDir = "";

    public Path storePath()
    {
        if (StringUtils.isNotEmpty(storeDir))
        {
            return Paths.get(storeDir);
        }
        return Paths.get(RuoYiConfig.getProfile(), "kb", "store");
    }

    public Path uploadPath()
    {
        if (StringUtils.isNotEmpty(uploadDir))
        {
            return Paths.get(uploadDir);
        }
        return Paths.get(RuoYiConfig.getProfile(), "kb", "upload");
    }

    /** 当前配置的 embedding 模型名 */
    public String currentEmbeddingModel()
    {
        if (isEmbeddingOllama())
        {
            return ollama.getEmbeddingModel();
        }
        return isEmbeddingCustom() ? customEmbedding.getEmbeddingModel() : openai.getEmbeddingModel();
    }

    /** 当前配置的 embedding 向量维度 */
    public int currentEmbeddingDimension()
    {
        if (isEmbeddingOllama())
        {
            return ollama.getEmbeddingDimension();
        }
        return isEmbeddingCustom() ? customEmbedding.getEmbeddingDimension() : openai.getEmbeddingDimension();
    }

    /** 当前 embedding 提供方名称（ollama/openai/custom） */
    public String embeddingProviderName()
    {
        if (isEmbeddingOllama())
        {
            return "ollama";
        }
        return isEmbeddingCustom() ? "custom" : "openai";
    }

    /** 当前 embedding 提供方服务地址 */
    public String embeddingBaseUrl()
    {
        if (isEmbeddingOllama())
        {
            return ollama.getBaseUrl();
        }
        return isEmbeddingCustom() ? customEmbedding.getBaseUrl() : openai.getBaseUrl();
    }

    /** 当前 embedding 提供方 API Key（ollama 无鉴权返回空） */
    public String embeddingApiKey()
    {
        if (isEmbeddingOllama())
        {
            return "";
        }
        return isEmbeddingCustom() ? customEmbedding.getApiKey() : openai.getApiKey();
    }

    /** 当前对话模型名称 */
    public String currentChatModel()
    {
        if (isChatOllama())
        {
            return ollama.getChatModel();
        }
        return isChatCustom() ? customChat.getChatModel() : openai.getChatModel();
    }

    /** 当前对话提供方名称（ollama/openai/custom） */
    public String chatProviderName()
    {
        if (isChatOllama())
        {
            return "ollama";
        }
        return isChatCustom() ? "custom" : "openai";
    }

    /** 当前对话提供方服务地址 */
    public String chatBaseUrl()
    {
        if (isChatOllama())
        {
            return ollama.getBaseUrl();
        }
        return isChatCustom() ? customChat.getBaseUrl() : openai.getBaseUrl();
    }

    /** 当前对话提供方 API Key（ollama 无鉴权返回空） */
    public String chatApiKey()
    {
        if (isChatOllama())
        {
            return "";
        }
        return isChatCustom() ? customChat.getApiKey() : openai.getApiKey();
    }

    public boolean isChatOllama()
    {
        String p = getChatProvider();
        return !"openai".equalsIgnoreCase(p) && !"custom".equalsIgnoreCase(p);
    }

    public boolean isEmbeddingOllama()
    {
        String p = getEmbeddingProvider();
        return !"openai".equalsIgnoreCase(p) && !"custom".equalsIgnoreCase(p);
    }

    public boolean isChatCustom()
    {
        return "custom".equalsIgnoreCase(getChatProvider());
    }

    public boolean isEmbeddingCustom()
    {
        return "custom".equalsIgnoreCase(getEmbeddingProvider());
    }

    public String getProvider()
    {
        return provider;
    }

    public void setProvider(String provider)
    {
        this.provider = provider;
    }

    /** 生效的对话提供方：chat-provider 为空时回退 provider */
    public String getChatProvider()
    {
        return StringUtils.isNotEmpty(chatProvider) ? chatProvider : provider;
    }

    public void setChatProvider(String chatProvider)
    {
        this.chatProvider = chatProvider;
    }

    /** 生效的向量化提供方：embedding-provider 为空时回退 provider */
    public String getEmbeddingProvider()
    {
        return StringUtils.isNotEmpty(embeddingProvider) ? embeddingProvider : provider;
    }

    public void setEmbeddingProvider(String embeddingProvider)
    {
        this.embeddingProvider = embeddingProvider;
    }

    public Ollama getOllama()
    {
        return ollama;
    }

    public void setOllama(Ollama ollama)
    {
        this.ollama = ollama;
    }

    public Openai getOpenai()
    {
        return openai;
    }

    public void setOpenai(Openai openai)
    {
        this.openai = openai;
    }

    public CustomApi getCustomChat()
    {
        return customChat;
    }

    public void setCustomChat(CustomApi customChat)
    {
        this.customChat = customChat;
    }

    public CustomApi getCustomEmbedding()
    {
        return customEmbedding;
    }

    public void setCustomEmbedding(CustomApi customEmbedding)
    {
        this.customEmbedding = customEmbedding;
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

    public Chunk getChunk()
    {
        return chunk;
    }

    public void setChunk(Chunk chunk)
    {
        this.chunk = chunk;
    }

    public Retrieval getRetrieval()
    {
        return retrieval;
    }

    public void setRetrieval(Retrieval retrieval)
    {
        this.retrieval = retrieval;
    }

    public String getStoreDir()
    {
        return storeDir;
    }

    public void setStoreDir(String storeDir)
    {
        this.storeDir = storeDir;
    }

    public String getUploadDir()
    {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir)
    {
        this.uploadDir = uploadDir;
    }

    public static class Ollama
    {
        private String baseUrl = "http://localhost:11434";

        private String chatModel = "qwen2.5:7b";

        private Double chatTemperature = 0.3;

        private String embeddingModel = "bge-m3";

        private Integer embeddingDimension = 1024;

        private Integer timeoutSeconds = 300;

        public String getBaseUrl()
        {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl)
        {
            this.baseUrl = baseUrl;
        }

        public String getChatModel()
        {
            return chatModel;
        }

        public void setChatModel(String chatModel)
        {
            this.chatModel = chatModel;
        }

        public Double getChatTemperature()
        {
            return chatTemperature;
        }

        public void setChatTemperature(Double chatTemperature)
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

        public Integer getEmbeddingDimension()
        {
            return embeddingDimension;
        }

        public void setEmbeddingDimension(Integer embeddingDimension)
        {
            this.embeddingDimension = embeddingDimension;
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

    public static class Openai
    {
        private String baseUrl = "http://localhost:8080/v1";

        private String apiKey = "none";

        private String chatModel = "qwen2.5-7b-instruct-q8_0";

        private Double chatTemperature = 0.3;

        private String embeddingModel = "bge-m3";

        private Integer embeddingDimension = 1024;

        private Integer timeoutSeconds = 300;

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

        public Double getChatTemperature()
        {
            return chatTemperature;
        }

        public void setChatTemperature(Double chatTemperature)
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

        public Integer getEmbeddingDimension()
        {
            return embeddingDimension;
        }

        public void setEmbeddingDimension(Integer embeddingDimension)
        {
            this.embeddingDimension = embeddingDimension;
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

    /**
     * 自定义API（OpenAI兼容，可多条维护；运行时由数据库记录填充，非 yml 固定值）
     */
    public static class CustomApi
    {
        private String baseUrl = "";

        private String apiKey = "";

        private String chatModel = "";

        private Double chatTemperature = 0.3;

        private String embeddingModel = "";

        private Integer embeddingDimension = 1024;

        private Integer timeoutSeconds = 60;

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

        public Double getChatTemperature()
        {
            return chatTemperature;
        }

        public void setChatTemperature(Double chatTemperature)
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

        public Integer getEmbeddingDimension()
        {
            return embeddingDimension;
        }

        public void setEmbeddingDimension(Integer embeddingDimension)
        {
            this.embeddingDimension = embeddingDimension;
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

    public static class Chunk
    {
        /** 分块策略：recursive(固定长度递归) | paragraph(按段落) | sentence(按句子) | separator(自定义分隔符) | markdown(按Markdown标题) */
        private String strategy = "recursive";

        /** 自定义分隔符（strategy=separator 时生效，支持 \n 表示换行） */
        private String separator = "";

        /** 分块最大长度（字符） */
        private Integer maxSize = 500;

        /** 分块重叠长度（字符） */
        private Integer overlap = 100;

        public String getStrategy()
        {
            return strategy;
        }

        public void setStrategy(String strategy)
        {
            this.strategy = strategy;
        }

        public String getSeparator()
        {
            return separator;
        }

        public void setSeparator(String separator)
        {
            this.separator = separator;
        }

        public Integer getMaxSize()
        {
            return maxSize;
        }

        public void setMaxSize(Integer maxSize)
        {
            this.maxSize = maxSize;
        }

        public Integer getOverlap()
        {
            return overlap;
        }

        public void setOverlap(Integer overlap)
        {
            this.overlap = overlap;
        }
    }

    public static class Retrieval
    {
        /** 检索返回的最大片段数 */
        private Integer maxResults = 4;

        /** 最低相似度分数 */
        private Double minScore = 0.3;

        public Integer getMaxResults()
        {
            return maxResults;
        }

        public void setMaxResults(Integer maxResults)
        {
            this.maxResults = maxResults;
        }

        public Double getMinScore()
        {
            return minScore;
        }

        public void setMinScore(Double minScore)
        {
            this.minScore = minScore;
        }
    }
}
