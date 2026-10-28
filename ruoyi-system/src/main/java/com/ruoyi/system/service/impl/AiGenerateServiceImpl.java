package com.ruoyi.system.service.impl;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.system.domain.YgAiGenerateTask;
import com.ruoyi.system.domain.YgAiModel;
import com.ruoyi.system.domain.YgMedia;
import com.ruoyi.system.mapper.YgAiModelMapper;
import com.ruoyi.system.service.IAiGenerateService;
import com.ruoyi.system.service.IYgAiGenerateTaskService;
import com.ruoyi.system.service.IYgMediaService;

/**
 * AI生成Service业务层处理
 * 
 * 接口约定（OpenAI 兼容，适配 OpenAI/one-api/new-api/Ollama /v1 等网关）：
 * 文本：POST {api_url}/chat/completions
 * 图片：POST {api_url}/images/generations（b64_json 或 url 两种响应均支持）
 * 视频：POST {api_url}/videos 创建任务 → GET {api_url}/videos/{id} 轮询 → GET {api_url}/videos/{id}/content 下载
 * 
 * @author ruoyi
 * @date 2026-10-14
 */
@Service
public class AiGenerateServiceImpl implements IAiGenerateService
{
    private static final Logger log = LoggerFactory.getLogger(AiGenerateServiceImpl.class);

    /** 任务状态：0排队 1生成中 2成功 3失败 */
    private static final String STATUS_RUNNING = "1";
    private static final String STATUS_SUCCESS = "2";
    private static final String STATUS_FAILED = "3";

    /** 视频任务轮询间隔与最长等待 */
    private static final long VIDEO_POLL_INTERVAL_MS = 5000L;
    private static final long VIDEO_MAX_WAIT_MS = 30 * 60 * 1000L;

    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    @Autowired
    private IYgAiGenerateTaskService taskService;

    @Autowired
    private YgAiModelMapper ygAiModelMapper;

    @Autowired
    private IYgMediaService ygMediaService;

    @Override
    public void runTask(Long taskId)
    {
        YgAiGenerateTask task = taskService.selectYgAiGenerateTaskByTaskId(taskId);
        if (task == null)
        {
            return;
        }
        YgAiModel model = ygAiModelMapper.selectYgAiModelByModelId(task.getModelId());
        if (model == null)
        {
            fail(task, "所选模型不存在或已删除");
            return;
        }
        if ("1".equals(model.getStatus()))
        {
            fail(task, "所选模型[" + model.getModelName() + "]已停用");
            return;
        }
        String base = trimTrailingSlash(model.getApiUrl());
        if (StringUtils.isEmpty(base))
        {
            fail(task, "所选模型[" + model.getModelName() + "]未配置API地址");
            return;
        }
        updateStatus(task, STATUS_RUNNING, null);
        try
        {
            int type = task.getGenerateType();
            if (type == 1)
            {
                String text = callChat(base, model, task.getPrompt());
                task.setResultText(text);
                updateStatus(task, STATUS_SUCCESS, null);
            }
            else if (type == 2)
            {
                byte[] bytes = callImage(base, model, task.getPrompt());
                saveAsMedia(task, bytes, "png", 1, model);
            }
            else if (type == 3)
            {
                byte[] bytes = callVideo(base, model, task.getPrompt());
                saveAsMedia(task, bytes, "mp4", 2, model);
            }
            else
            {
                fail(task, "不支持的生成类型：" + type);
            }
        }
        catch (Exception e)
        {
            log.error("AI生成任务[{}]执行失败", taskId, e);
            fail(task, e.getMessage());
        }
    }

    // ---------------- 文本 ----------------

    private String callChat(String base, YgAiModel model, String prompt) throws Exception
    {
        JSONObject message = new JSONObject();
        message.put("role", "user");
        message.put("content", prompt);
        JSONObject body = new JSONObject();
        body.put("model", model.getModelName());
        body.put("messages", new JSONArray().fluentAdd(message));
        String resp = postJson(base + "/chat/completions", model.getApiKey(), body.toJSONString(), Duration.ofSeconds(300));
        JSONObject json = JSON.parseObject(resp);
        JSONArray choices = json.getJSONArray("choices");
        if (choices == null || choices.isEmpty())
        {
            throw new RuntimeException("接口未返回 choices：" + StringUtils.substring(resp, 0, 200));
        }
        String text = choices.getJSONObject(0).getJSONObject("message").getString("content");
        if (StringUtils.isEmpty(text))
        {
            throw new RuntimeException("接口返回内容为空");
        }
        return text;
    }

    // ---------------- 图片 ----------------

    private byte[] callImage(String base, YgAiModel model, String prompt) throws Exception
    {
        JSONObject body = new JSONObject();
        body.put("model", model.getModelName());
        body.put("prompt", prompt);
        body.put("n", 1);
        body.put("response_format", "b64_json");
        String resp = postJson(base + "/images/generations", model.getApiKey(), body.toJSONString(), Duration.ofSeconds(600));
        JSONObject json = JSON.parseObject(resp);
        JSONArray data = json.getJSONArray("data");
        if (data == null || data.isEmpty())
        {
            throw new RuntimeException("接口未返回图片数据：" + StringUtils.substring(resp, 0, 200));
        }
        JSONObject first = data.getJSONObject(0);
        String b64 = first.getString("b64_json");
        if (StringUtils.isNotEmpty(b64))
        {
            return Base64.getDecoder().decode(b64);
        }
        String url = first.getString("url");
        if (StringUtils.isNotEmpty(url))
        {
            return getBytes(url, model.getApiKey(), Duration.ofSeconds(300));
        }
        throw new RuntimeException("接口返回中既无 b64_json 也无 url");
    }

    // ---------------- 视频（OpenAI Videos 异步接口） ----------------

    private byte[] callVideo(String base, YgAiModel model, String prompt) throws Exception
    {
        JSONObject body = new JSONObject();
        body.put("model", model.getModelName());
        body.put("prompt", prompt);
        String resp;
        try
        {
            resp = postJson(base + "/videos", model.getApiKey(), body.toJSONString(), Duration.ofSeconds(120));
        }
        catch (Exception e)
        {
            throw new RuntimeException("视频生成需 OpenAI Videos 兼容接口（" + base + "/videos），当前模型调用失败：" + e.getMessage());
        }
        JSONObject created = JSON.parseObject(resp);
        String videoId = created.getString("id");
        if (StringUtils.isEmpty(videoId))
        {
            throw new RuntimeException("创建视频任务未返回id：" + StringUtils.substring(resp, 0, 200));
        }
        long deadline = System.currentTimeMillis() + VIDEO_MAX_WAIT_MS;
        while (System.currentTimeMillis() < deadline)
        {
            Thread.sleep(VIDEO_POLL_INTERVAL_MS);
            String statusResp = getJson(base + "/videos/" + videoId, model.getApiKey(), Duration.ofSeconds(60));
            JSONObject status = JSON.parseObject(statusResp);
            String state = status.getString("status");
            if ("completed".equalsIgnoreCase(state))
            {
                return getBytes(base + "/videos/" + videoId + "/content", model.getApiKey(), Duration.ofSeconds(600));
            }
            if ("failed".equalsIgnoreCase(state) || "cancelled".equalsIgnoreCase(state))
            {
                JSONObject error = status.getJSONObject("error");
                throw new RuntimeException("视频生成失败：" + (error != null ? error.toJSONString() : state));
            }
        }
        throw new RuntimeException("视频生成超时（超过30分钟未完成）");
    }

    // ---------------- 结果落盘+入素材库 ----------------

    private void saveAsMedia(YgAiGenerateTask task, byte[] bytes, String ext, int mediaType, YgAiModel model) throws Exception
    {
        String datePath = DateUtils.datePath();
        String fileName = "gen_" + IdUtils.fastSimpleUUID() + "." + ext;
        Path dir = Paths.get(RuoYiConfig.getUploadPath(), datePath);
        Files.createDirectories(dir);
        Path file = dir.resolve(fileName);
        Files.write(file, bytes);
        // /profile/upload/yyyy/MM/dd/xxx.png（与 FileUploadUtils 返回格式一致）
        String absolute = file.toAbsolutePath().toString().replace("\\", "/");
        String profile = RuoYiConfig.getProfile().replace("\\", "/");
        String resourceUrl = Constants.RESOURCE_PREFIX + absolute.substring(profile.length());

        YgMedia media = new YgMedia();
        String title = StringUtils.substring(task.getPrompt(), 0, 50);
        media.setMediaName(StringUtils.isEmpty(title) ? "AI生成." + ext : title + "." + ext);
        media.setOriginalName(media.getMediaName());
        media.setMediaType(mediaType);
        media.setFileUrl(resourceUrl);
        media.setFileSize((long) bytes.length);
        media.setExt(ext);
        media.setCategory("AI生成");
        media.setRemark("模型：" + model.getModelName());
        media.setCreateBy(task.getCreateBy());
        ygMediaService.insertYgMedia(media);

        task.setResultUrl(resourceUrl);
        task.setMediaId(media.getMediaId());
        updateStatus(task, STATUS_SUCCESS, null);
    }

    // ---------------- HTTP 工具 ----------------

    private String postJson(String url, String apiKey, String body, Duration timeout) throws Exception
    {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(timeout)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8));
        applyAuth(builder, apiKey);
        HttpResponse<String> response = HTTP.send(builder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        checkStatus(response.statusCode(), response.body(), url);
        return response.body();
    }

    private String getJson(String url, String apiKey, Duration timeout) throws Exception
    {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(timeout)
                .GET();
        applyAuth(builder, apiKey);
        HttpResponse<String> response = HTTP.send(builder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        checkStatus(response.statusCode(), response.body(), url);
        return response.body();
    }

    private byte[] getBytes(String url, String apiKey, Duration timeout) throws Exception
    {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(timeout)
                .GET();
        applyAuth(builder, apiKey);
        HttpResponse<byte[]> response = HTTP.send(builder.build(), HttpResponse.BodyHandlers.ofByteArray());
        if (response.statusCode() >= 400)
        {
            throw new RuntimeException("HTTP " + response.statusCode() + "：" + url);
        }
        return response.body();
    }

    private void applyAuth(HttpRequest.Builder builder, String apiKey)
    {
        if (StringUtils.isNotEmpty(apiKey) && !"none".equalsIgnoreCase(apiKey))
        {
            builder.header("Authorization", "Bearer " + apiKey);
        }
    }

    private void checkStatus(int status, String body, String url)
    {
        if (status >= 400)
        {
            throw new RuntimeException("HTTP " + status + "（" + url + "）：" + StringUtils.substring(body, 0, 300));
        }
    }

    // ---------------- 状态更新 ----------------

    private void updateStatus(YgAiGenerateTask task, String status, String errorMsg)
    {
        YgAiGenerateTask update = new YgAiGenerateTask();
        update.setTaskId(task.getTaskId());
        update.setStatus(status);
        update.setResultText(task.getResultText());
        update.setResultUrl(task.getResultUrl());
        update.setMediaId(task.getMediaId());
        update.setErrorMsg(errorMsg != null ? StringUtils.substring(errorMsg, 0, 1000) : task.getErrorMsg());
        taskService.updateYgAiGenerateTask(update);
        task.setStatus(status);
    }

    private void fail(YgAiGenerateTask task, String errorMsg)
    {
        YgAiGenerateTask update = new YgAiGenerateTask();
        update.setTaskId(task.getTaskId());
        update.setStatus(STATUS_FAILED);
        update.setErrorMsg(StringUtils.substring(errorMsg, 0, 1000));
        taskService.updateYgAiGenerateTask(update);
    }

    private String trimTrailingSlash(String url)
    {
        if (url == null)
        {
            return "";
        }
        String trimmed = url.trim();
        while (trimmed.endsWith("/"))
        {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }
}
