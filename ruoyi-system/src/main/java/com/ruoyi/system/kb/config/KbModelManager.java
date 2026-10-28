package com.ruoyi.system.kb.config;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YgKbCustomApi;
import com.ruoyi.system.domain.YgKbModelConfig;
import com.ruoyi.system.mapper.YgKbCustomApiMapper;
import com.ruoyi.system.mapper.YgKbModelConfigMapper;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;

/**
 * 知识库模型管理器
 *
 * <p>模型配置以 application.yml 为默认值，数据库 yg_kb_model_config 单行记录为运行时配置。
 * 后台"模型管理"页面保存后重建 ChatModel/EmbeddingModel，无需重启应用。</p>
 *
 * @author ruoyi
 * @date 2026-10-14
 */
@Component
public class KbModelManager
{
    private static final Logger log = LoggerFactory.getLogger(KbModelManager.class);

    @Autowired
    private KbProperties props;

    @Autowired
    private YgKbModelConfigMapper configMapper;

    @Autowired
    private YgKbCustomApiMapper customApiMapper;

    private volatile ChatModel chatModel;

    private volatile StreamingChatModel streamingChatModel;

    private volatile EmbeddingModel embeddingModel;

    @PostConstruct
    public void init()
    {
        try
        {
            YgKbModelConfig db = configMapper.selectKbModelConfig();
            if (db != null)
            {
                applyToProperties(db);
                log.info("知识库模型配置已从数据库加载：chatProvider={}, embeddingProvider={}, chatModel={}, embeddingModel={}",
                        props.getChatProvider(), props.getEmbeddingProvider(), props.currentChatModel(), props.currentEmbeddingModel());
            }
        }
        catch (Exception e)
        {
            log.error("加载知识库模型配置失败，使用yml默认值：{}", e.getMessage());
        }
        rebuild();
    }

    /**
     * 当前生效的对话模型
     */
    public ChatModel getChatModel()
    {
        return chatModel;
    }

    /**
     * 当前生效的流式对话模型
     */
    public StreamingChatModel getStreamingChatModel()
    {
        return streamingChatModel;
    }

    /**
     * 当前生效的向量化模型
     */
    public EmbeddingModel getEmbeddingModel()
    {
        return embeddingModel;
    }

    /**
     * 当前生效配置（用于页面回显）
     */
    public YgKbModelConfig currentConfig()
    {
        YgKbModelConfig cfg = new YgKbModelConfig();
        cfg.setProvider(props.getProvider());
        cfg.setChatProvider(props.getChatProvider());
        cfg.setEmbeddingProvider(props.getEmbeddingProvider());
        cfg.setOllamaBaseUrl(props.getOllama().getBaseUrl());
        cfg.setOllamaChatModel(props.getOllama().getChatModel());
        cfg.setOllamaChatTemperature(BigDecimal.valueOf(props.getOllama().getChatTemperature()));
        cfg.setOllamaEmbeddingModel(props.getOllama().getEmbeddingModel());
        cfg.setOllamaEmbeddingDim(props.getOllama().getEmbeddingDimension());
        cfg.setOllamaTimeoutSeconds(props.getOllama().getTimeoutSeconds());
        cfg.setOpenaiBaseUrl(props.getOpenai().getBaseUrl());
        cfg.setOpenaiApiKey(props.getOpenai().getApiKey());
        cfg.setOpenaiChatModel(props.getOpenai().getChatModel());
        cfg.setOpenaiChatTemperature(BigDecimal.valueOf(props.getOpenai().getChatTemperature()));
        cfg.setOpenaiEmbeddingModel(props.getOpenai().getEmbeddingModel());
        cfg.setOpenaiEmbeddingDim(props.getOpenai().getEmbeddingDimension());
        cfg.setOpenaiTimeoutSeconds(props.getOpenai().getTimeoutSeconds());
        cfg.setCustomChatApiId(props.getCustomChatApiId());
        cfg.setCustomEmbeddingApiId(props.getCustomEmbeddingApiId());
        cfg.setChunkStrategy(props.getChunk().getStrategy());
        cfg.setChunkSeparator(props.getChunk().getSeparator());
        cfg.setChunkMaxSize(props.getChunk().getMaxSize());
        cfg.setChunkOverlap(props.getChunk().getOverlap());
        cfg.setRetrievalMaxResults(props.getRetrieval().getMaxResults());
        cfg.setRetrievalMinScore(BigDecimal.valueOf(props.getRetrieval().getMinScore()));
        YgKbModelConfig db = configMapper.selectKbModelConfig();
        if (db != null)
        {
            cfg.setId(db.getId());
            cfg.setUpdateBy(db.getUpdateBy());
            cfg.setUpdateTime(db.getUpdateTime());
        }
        return cfg;
    }

    /**
     * 保存配置并立即生效（upsert 数据库 + 重建模型）
     */
    public synchronized void saveAndApply(YgKbModelConfig cfg, String username)
    {
        validate(cfg);
        cfg.setUpdateBy(username);
        cfg.setUpdateTime(DateUtils.getNowDate());
        YgKbModelConfig exist = configMapper.selectKbModelConfig();
        if (exist == null)
        {
            configMapper.insertKbModelConfig(cfg);
        }
        else
        {
            cfg.setId(exist.getId());
            configMapper.updateKbModelConfig(cfg);
        }
        applyToProperties(cfg);
        rebuild();
        log.info("知识库模型配置已更新并生效：chatProvider={}, embeddingProvider={}, chatModel={}, embeddingModel={}, 操作人={}",
                cfg.getChatProvider(), cfg.getEmbeddingProvider(), props.currentChatModel(), props.currentEmbeddingModel(), username);
    }

    /**
     * 模型服务探活
     */
    public Map<String, Object> health()
    {
        Map<String, Object> info = new HashMap<>();
        info.put("chatProvider", props.getChatProvider());
        info.put("embeddingProvider", props.getEmbeddingProvider());
        info.put("chatModel", props.currentChatModel());
        info.put("embeddingModel", props.currentEmbeddingModel());
        String chatError = probeService(props.chatProviderName(), props.chatBaseUrl(), props.chatApiKey());
        String embedError;
        if (trimSlash(props.chatBaseUrl()).equals(trimSlash(props.embeddingBaseUrl()))
                && props.chatProviderName().equals(props.embeddingProviderName()))
        {
            embedError = chatError;
        }
        else
        {
            embedError = probeService(props.embeddingProviderName(), props.embeddingBaseUrl(), props.embeddingApiKey());
        }
        info.put("chatAvailable", chatError == null);
        info.put("embeddingAvailable", embedError == null);
        boolean available = chatError == null && embedError == null;
        StringBuilder message = new StringBuilder();
        if (chatError != null)
        {
            message.append("对话服务异常：").append(chatError).append("；");
        }
        if (embedError != null)
        {
            message.append("向量化服务异常：").append(embedError).append("；");
        }
        info.put("available", available);
        info.put("message", available ? "模型服务正常" : message.substring(0, message.length() - 1));
        return info;
    }

    /** 探活单个服务，返回 null 表示正常，否则返回错误描述 */
    private String probeService(String provider, String baseUrl, String apiKey)
    {
        try
        {
            boolean ollama = "ollama".equalsIgnoreCase(provider);
            String url = trimSlash(baseUrl) + (ollama ? "/api/version" : "/models");
            HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(5)).GET();
            if (!ollama && StringUtils.isNotEmpty(apiKey) && !"none".equalsIgnoreCase(apiKey))
            {
                builder.header("Authorization", "Bearer " + apiKey);
            }
            HttpResponse<String> response = httpClient().send(builder.build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200)
            {
                return null;
            }
            return "服务响应异常，HTTP " + response.statusCode() + "（" + baseUrl + "）";
        }
        catch (Exception e)
        {
            return "服务不可用：请确认已启动（" + baseUrl + "）";
        }
    }

    /**
     * 拉取指定提供方的可用模型列表（Ollama /api/tags 或 OpenAI兼容 /models）
     *
     * @param provider    ollama | openai | custom，为空时回退当前对话提供方
     * @param customApiId 自定义API ID（provider=custom 且已保存时使用，从库中取地址与密钥）
     * @param baseUrl     自定义API 服务地址（provider=custom 且未保存时使用）
     * @param apiKey      自定义API 密钥（provider=custom 且未保存时使用）
     */
    public List<String> listRemoteModels(String provider, Long customApiId, String baseUrl, String apiKey)
    {
        String p = StringUtils.isNotEmpty(provider) ? provider : props.getChatProvider();
        boolean ollama = "ollama".equalsIgnoreCase(p);
        boolean custom = "custom".equalsIgnoreCase(p);
        if (custom)
        {
            if (customApiId != null)
            {
                YgKbCustomApi row = customApiMapper.selectYgKbCustomApiById(customApiId);
                if (row == null)
                {
                    throw new ServiceException("自定义API不存在，请保存后再获取模型列表");
                }
                baseUrl = row.getBaseUrl();
                apiKey = row.getApiKey();
            }
            else if (StringUtils.isEmpty(baseUrl))
            {
                throw new ServiceException("请先填写自定义API服务地址");
            }
        }
        else
        {
            baseUrl = ollama ? props.getOllama().getBaseUrl() : props.getOpenai().getBaseUrl();
            apiKey = props.getOpenai().getApiKey();
        }
        try
        {
            String url = trimSlash(baseUrl) + (ollama ? "/api/tags" : "/models");
            HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(10)).GET();
            if (!ollama && StringUtils.isNotEmpty(apiKey) && !"none".equalsIgnoreCase(apiKey))
            {
                builder.header("Authorization", "Bearer " + apiKey);
            }
            HttpResponse<String> response = httpClient().send(builder.build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200)
            {
                throw new ServiceException("模型服务响应异常，HTTP " + response.statusCode());
            }
            List<String> names = new ArrayList<>();
            JSONObject json = JSON.parseObject(response.body());
            if (ollama)
            {
                JSONArray models = json.getJSONArray("models");
                if (models != null)
                {
                    for (int i = 0; i < models.size(); i++)
                    {
                        names.add(models.getJSONObject(i).getString("name"));
                    }
                }
            }
            else
            {
                JSONArray data = json.getJSONArray("data");
                if (data != null)
                {
                    for (int i = 0; i < data.size(); i++)
                    {
                        names.add(data.getJSONObject(i).getString("id"));
                    }
                }
            }
            return names;
        }
        catch (ServiceException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            String name = ollama ? "Ollama" : custom ? "自定义API" : "llama.cpp";
            throw new ServiceException("获取模型列表失败：请确认 " + name + " 服务可用（" + baseUrl + "）");
        }
    }

    private void rebuild()
    {
        resolveCustomApis();
        this.chatModel = buildChatModel();
        this.streamingChatModel = buildStreamingChatModel();
        this.embeddingModel = buildEmbeddingModel();
    }

    /**
     * 依据当前选中的自定义API ID，从数据库解析并填充 props.customChat / props.customEmbedding。
     * 未选中或记录不存在时清空对应持有者，避免使用上一次的旧值。
     */
    private void resolveCustomApis()
    {
        fillCustom(props.isChatCustom(), props.getCustomChatApiId(), props.getCustomChat(), "对话");
        fillCustom(props.isEmbeddingCustom(), props.getCustomEmbeddingApiId(), props.getCustomEmbedding(), "向量化");
    }

    private void fillCustom(boolean selected, Long id, KbProperties.CustomApi target, String usage)
    {
        target.setBaseUrl("");
        target.setApiKey("");
        target.setChatModel("");
        target.setEmbeddingModel("");
        target.setEmbeddingDimension(1024);
        target.setChatTemperature(0.3);
        target.setTimeoutSeconds(60);
        if (!selected)
        {
            return;
        }
        if (id == null)
        {
            log.warn("{}提供方为自定义API，但未配置自定义API ID，已回退默认配置", usage);
            return;
        }
        YgKbCustomApi row = customApiMapper.selectYgKbCustomApiById(id);
        if (row == null)
        {
            log.warn("{}提供方选择的自定义API[{}]不存在，已回退默认配置", usage, id);
            return;
        }
        target.setBaseUrl(row.getBaseUrl());
        target.setApiKey(row.getApiKey());
        target.setChatModel(row.getChatModel());
        if (row.getChatTemperature() != null)
        {
            target.setChatTemperature(row.getChatTemperature().doubleValue());
        }
        target.setEmbeddingModel(row.getEmbeddingModel());
        if (row.getEmbeddingDim() != null)
        {
            target.setEmbeddingDimension(row.getEmbeddingDim());
        }
        if (row.getTimeoutSeconds() != null)
        {
            target.setTimeoutSeconds(row.getTimeoutSeconds());
        }
    }

    private StreamingChatModel buildStreamingChatModel()
    {
        if (props.isChatOllama())
        {
            KbProperties.Ollama o = props.getOllama();
            return OllamaStreamingChatModel.builder()
                    .baseUrl(o.getBaseUrl())
                    .modelName(o.getChatModel())
                    .temperature(o.getChatTemperature())
                    .timeout(Duration.ofSeconds(o.getTimeoutSeconds()))
                    .build();
        }
        if (props.isChatCustom())
        {
            KbProperties.CustomApi o = props.getCustomChat();
            return OpenAiStreamingChatModel.builder()
                    .baseUrl(o.getBaseUrl())
                    .apiKey(StringUtils.isNotEmpty(o.getApiKey()) ? o.getApiKey() : "none")
                    .modelName(o.getChatModel())
                    .temperature(o.getChatTemperature())
                    .timeout(Duration.ofSeconds(o.getTimeoutSeconds()))
                    .build();
        }
        KbProperties.Openai o = props.getOpenai();
        return OpenAiStreamingChatModel.builder()
                .baseUrl(o.getBaseUrl())
                .apiKey(StringUtils.isNotEmpty(o.getApiKey()) ? o.getApiKey() : "none")
                .modelName(o.getChatModel())
                .temperature(o.getChatTemperature())
                .timeout(Duration.ofSeconds(o.getTimeoutSeconds()))
                .build();
    }

    private ChatModel buildChatModel()
    {
        if (props.isChatOllama())
        {
            KbProperties.Ollama o = props.getOllama();
            return OllamaChatModel.builder()
                    .baseUrl(o.getBaseUrl())
                    .modelName(o.getChatModel())
                    .temperature(o.getChatTemperature())
                    .timeout(Duration.ofSeconds(o.getTimeoutSeconds()))
                    .maxRetries(1)
                    .build();
        }
        if (props.isChatCustom())
        {
            KbProperties.CustomApi o = props.getCustomChat();
            return OpenAiChatModel.builder()
                    .baseUrl(o.getBaseUrl())
                    .apiKey(StringUtils.isNotEmpty(o.getApiKey()) ? o.getApiKey() : "none")
                    .modelName(o.getChatModel())
                    .temperature(o.getChatTemperature())
                    .timeout(Duration.ofSeconds(o.getTimeoutSeconds()))
                    .build();
        }
        KbProperties.Openai o = props.getOpenai();
        return OpenAiChatModel.builder()
                .baseUrl(o.getBaseUrl())
                .apiKey(StringUtils.isNotEmpty(o.getApiKey()) ? o.getApiKey() : "none")
                .modelName(o.getChatModel())
                .temperature(o.getChatTemperature())
                .timeout(Duration.ofSeconds(o.getTimeoutSeconds()))
                .build();
    }

    private EmbeddingModel buildEmbeddingModel()
    {
        if (props.isEmbeddingOllama())
        {
            KbProperties.Ollama o = props.getOllama();
            return OllamaEmbeddingModel.builder()
                    .baseUrl(o.getBaseUrl())
                    .modelName(o.getEmbeddingModel())
                    .timeout(Duration.ofSeconds(o.getTimeoutSeconds()))
                    .build();
        }
        if (props.isEmbeddingCustom())
        {
            KbProperties.CustomApi o = props.getCustomEmbedding();
            return OpenAiEmbeddingModel.builder()
                    .baseUrl(o.getBaseUrl())
                    .apiKey(StringUtils.isNotEmpty(o.getApiKey()) ? o.getApiKey() : "none")
                    .modelName(o.getEmbeddingModel())
                    .timeout(Duration.ofSeconds(o.getTimeoutSeconds()))
                    .build();
        }
        KbProperties.Openai o = props.getOpenai();
        return OpenAiEmbeddingModel.builder()
                .baseUrl(o.getBaseUrl())
                .apiKey(StringUtils.isNotEmpty(o.getApiKey()) ? o.getApiKey() : "none")
                .modelName(o.getEmbeddingModel())
                .timeout(Duration.ofSeconds(o.getTimeoutSeconds()))
                .build();
    }

    private void applyToProperties(YgKbModelConfig cfg)
    {
        if (StringUtils.isNotEmpty(cfg.getProvider()))
        {
            props.setProvider(cfg.getProvider());
        }
        if (StringUtils.isNotEmpty(cfg.getChatProvider()))
        {
            props.setChatProvider(cfg.getChatProvider());
        }
        if (StringUtils.isNotEmpty(cfg.getEmbeddingProvider()))
        {
            props.setEmbeddingProvider(cfg.getEmbeddingProvider());
        }
        KbProperties.Ollama ollama = props.getOllama();
        if (StringUtils.isNotEmpty(cfg.getOllamaBaseUrl()))
        {
            ollama.setBaseUrl(cfg.getOllamaBaseUrl());
        }
        if (StringUtils.isNotEmpty(cfg.getOllamaChatModel()))
        {
            ollama.setChatModel(cfg.getOllamaChatModel());
        }
        if (cfg.getOllamaChatTemperature() != null)
        {
            ollama.setChatTemperature(cfg.getOllamaChatTemperature().doubleValue());
        }
        if (StringUtils.isNotEmpty(cfg.getOllamaEmbeddingModel()))
        {
            ollama.setEmbeddingModel(cfg.getOllamaEmbeddingModel());
        }
        if (cfg.getOllamaEmbeddingDim() != null)
        {
            ollama.setEmbeddingDimension(cfg.getOllamaEmbeddingDim());
        }
        if (cfg.getOllamaTimeoutSeconds() != null)
        {
            ollama.setTimeoutSeconds(cfg.getOllamaTimeoutSeconds());
        }
        KbProperties.Openai openai = props.getOpenai();
        if (StringUtils.isNotEmpty(cfg.getOpenaiBaseUrl()))
        {
            openai.setBaseUrl(cfg.getOpenaiBaseUrl());
        }
        if (cfg.getOpenaiApiKey() != null)
        {
            openai.setApiKey(cfg.getOpenaiApiKey());
        }
        if (StringUtils.isNotEmpty(cfg.getOpenaiChatModel()))
        {
            openai.setChatModel(cfg.getOpenaiChatModel());
        }
        if (cfg.getOpenaiChatTemperature() != null)
        {
            openai.setChatTemperature(cfg.getOpenaiChatTemperature().doubleValue());
        }
        if (StringUtils.isNotEmpty(cfg.getOpenaiEmbeddingModel()))
        {
            openai.setEmbeddingModel(cfg.getOpenaiEmbeddingModel());
        }
        if (cfg.getOpenaiEmbeddingDim() != null)
        {
            openai.setEmbeddingDimension(cfg.getOpenaiEmbeddingDim());
        }
        if (cfg.getOpenaiTimeoutSeconds() != null)
        {
            openai.setTimeoutSeconds(cfg.getOpenaiTimeoutSeconds());
        }
        if (cfg.getCustomChatApiId() != null)
        {
            props.setCustomChatApiId(cfg.getCustomChatApiId());
        }
        if (cfg.getCustomEmbeddingApiId() != null)
        {
            props.setCustomEmbeddingApiId(cfg.getCustomEmbeddingApiId());
        }
        if (StringUtils.isNotEmpty(cfg.getChunkStrategy()))
        {
            props.getChunk().setStrategy(cfg.getChunkStrategy());
        }
        if (cfg.getChunkSeparator() != null)
        {
            props.getChunk().setSeparator(cfg.getChunkSeparator());
        }
        if (cfg.getChunkMaxSize() != null)
        {
            props.getChunk().setMaxSize(cfg.getChunkMaxSize());
        }
        if (cfg.getChunkOverlap() != null)
        {
            props.getChunk().setOverlap(cfg.getChunkOverlap());
        }
        if (cfg.getRetrievalMaxResults() != null)
        {
            props.getRetrieval().setMaxResults(cfg.getRetrievalMaxResults());
        }
        if (cfg.getRetrievalMinScore() != null)
        {
            props.getRetrieval().setMinScore(cfg.getRetrievalMinScore().doubleValue());
        }
    }

    private void validate(YgKbModelConfig cfg)
    {
        String chatProvider = cfg.getChatProvider();
        String embeddingProvider = cfg.getEmbeddingProvider();
        if (!"ollama".equalsIgnoreCase(chatProvider) && !"openai".equalsIgnoreCase(chatProvider)
                && !"custom".equalsIgnoreCase(chatProvider))
        {
            throw new ServiceException("对话模型提供方仅支持 ollama/openai/custom");
        }
        if (!"ollama".equalsIgnoreCase(embeddingProvider) && !"openai".equalsIgnoreCase(embeddingProvider)
                && !"custom".equalsIgnoreCase(embeddingProvider))
        {
            throw new ServiceException("向量化模型提供方仅支持 ollama/openai/custom");
        }
        if ("ollama".equalsIgnoreCase(chatProvider) || "ollama".equalsIgnoreCase(embeddingProvider))
        {
            if (StringUtils.isEmpty(cfg.getOllamaBaseUrl()))
            {
                throw new ServiceException("Ollama服务地址不能为空");
            }
            if ("ollama".equalsIgnoreCase(chatProvider) && StringUtils.isEmpty(cfg.getOllamaChatModel()))
            {
                throw new ServiceException("Ollama对话模型不能为空");
            }
            if ("ollama".equalsIgnoreCase(embeddingProvider) && StringUtils.isEmpty(cfg.getOllamaEmbeddingModel()))
            {
                throw new ServiceException("Ollama向量化模型不能为空");
            }
            if ("ollama".equalsIgnoreCase(chatProvider))
            {
                checkRange(cfg.getOllamaChatTemperature(), 0, 2, "Ollama温度");
            }
            if ("ollama".equalsIgnoreCase(embeddingProvider)
                    && (cfg.getOllamaEmbeddingDim() == null || cfg.getOllamaEmbeddingDim() <= 0))
            {
                throw new ServiceException("Ollama向量维度需为正整数");
            }
            if (cfg.getOllamaTimeoutSeconds() == null || cfg.getOllamaTimeoutSeconds() < 10)
            {
                throw new ServiceException("Ollama超时秒数需不小于10");
            }
        }
        if ("openai".equalsIgnoreCase(chatProvider) || "openai".equalsIgnoreCase(embeddingProvider))
        {
            if (StringUtils.isEmpty(cfg.getOpenaiBaseUrl()))
            {
                throw new ServiceException("llama.cpp（OpenAI兼容）服务地址不能为空");
            }
            if ("openai".equalsIgnoreCase(chatProvider) && StringUtils.isEmpty(cfg.getOpenaiChatModel()))
            {
                throw new ServiceException("llama.cpp（OpenAI兼容）对话模型不能为空");
            }
            if ("openai".equalsIgnoreCase(embeddingProvider) && StringUtils.isEmpty(cfg.getOpenaiEmbeddingModel()))
            {
                throw new ServiceException("llama.cpp（OpenAI兼容）向量化模型不能为空");
            }
            if ("openai".equalsIgnoreCase(chatProvider))
            {
                checkRange(cfg.getOpenaiChatTemperature(), 0, 2, "llama.cpp温度");
            }
            if ("openai".equalsIgnoreCase(embeddingProvider)
                    && (cfg.getOpenaiEmbeddingDim() == null || cfg.getOpenaiEmbeddingDim() <= 0))
            {
                throw new ServiceException("llama.cpp（OpenAI兼容）向量维度需为正整数");
            }
            if (cfg.getOpenaiTimeoutSeconds() == null || cfg.getOpenaiTimeoutSeconds() < 10)
            {
                throw new ServiceException("llama.cpp（OpenAI兼容）超时秒数需不小于10");
            }
        }
        if ("custom".equalsIgnoreCase(chatProvider))
        {
            checkCustomApi(cfg.getCustomChatApiId(), true, "对话");
        }
        if ("custom".equalsIgnoreCase(embeddingProvider))
        {
            checkCustomApi(cfg.getCustomEmbeddingApiId(), false, "向量化");
        }
        checkRange(cfg.getRetrievalMinScore(), 0, 1, "最低相似度");
        if (cfg.getChunkStrategy() == null
                || !java.util.Arrays.asList("recursive", "paragraph", "sentence", "separator", "markdown").contains(cfg.getChunkStrategy()))
        {
            throw new ServiceException("分块策略仅支持 recursive/paragraph/sentence/separator/markdown");
        }
        if ("separator".equals(cfg.getChunkStrategy()) && StringUtils.isEmpty(cfg.getChunkSeparator()))
        {
            throw new ServiceException("选择自定义分隔符分块时必须填写分隔符");
        }
        if (cfg.getChunkSeparator() != null && cfg.getChunkSeparator().length() > 100)
        {
            throw new ServiceException("分块分隔符长度不能超过100字符");
        }
        if (cfg.getChunkMaxSize() == null || cfg.getChunkMaxSize() < 50 || cfg.getChunkMaxSize() > 4000)
        {
            throw new ServiceException("分块最大长度需在 50~4000 之间");
        }
        if (cfg.getChunkOverlap() == null || cfg.getChunkOverlap() < 0 || cfg.getChunkOverlap() >= cfg.getChunkMaxSize())
        {
            throw new ServiceException("分块重叠需不小于0且小于分块最大长度");
        }
        if (cfg.getRetrievalMaxResults() == null || cfg.getRetrievalMaxResults() < 1 || cfg.getRetrievalMaxResults() > 20)
        {
            throw new ServiceException("检索片段数需在 1~20 之间");
        }
    }

    /** 校验自定义API引用可用（所选记录存在且字段合法） */
    private void checkCustomApi(Long id, boolean chat, String usage)
    {
        if (id == null)
        {
            throw new ServiceException(usage + "提供方为自定义API时，必须选择一条自定义API");
        }
        YgKbCustomApi row = customApiMapper.selectYgKbCustomApiById(id);
        if (row == null)
        {
            throw new ServiceException(usage + "提供方选择的自定义API不存在，请重新选择");
        }
        String label = "自定义API[" + row.getName() + "]";
        if (StringUtils.isEmpty(row.getBaseUrl()))
        {
            throw new ServiceException(label + "服务地址不能为空");
        }
        if (chat)
        {
            if (StringUtils.isEmpty(row.getChatModel()))
            {
                throw new ServiceException(label + "对话模型不能为空");
            }
            checkRange(row.getChatTemperature(), 0, 2, label + "温度");
        }
        else
        {
            if (StringUtils.isEmpty(row.getEmbeddingModel()))
            {
                throw new ServiceException(label + "向量化模型不能为空");
            }
            if (row.getEmbeddingDim() == null || row.getEmbeddingDim() <= 0)
            {
                throw new ServiceException(label + "向量维度需为正整数");
            }
        }
        if (row.getTimeoutSeconds() == null || row.getTimeoutSeconds() < 10)
        {
            throw new ServiceException(label + "超时秒数需不小于10");
        }
    }

    private void checkRange(BigDecimal value, double min, double max, String name)
    {
        if (value == null || value.doubleValue() < min || value.doubleValue() > max)
        {
            throw new ServiceException(name + "需在 " + min + "~" + max + " 之间");
        }
    }

    private HttpClient httpClient()
    {
        return HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    }

    private String trimSlash(String url)
    {
        return url != null && url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
