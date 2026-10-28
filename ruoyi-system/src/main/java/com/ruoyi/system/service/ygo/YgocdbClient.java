package com.ruoyi.system.service.ygo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import org.springframework.stereotype.Component;
import com.ruoyi.common.exception.ServiceException;

/**
 * ygocdb卡片数据库API客户端
 * 
 * @author ruoyi
 * @date 2026-10-12
 */
@Component
public class YgocdbClient
{
    /** ygocdb API v0 基址 */
    public static final String BASE_URL = "https://ygocdb.com/api/v0";

    /** 连接超时时间(秒) */
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(30);

    /** 请求超时时间(秒) */
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(120);

    /** JDK17自带HTTP客户端 */
    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(CONNECT_TIMEOUT).build();

    /**
     * GET请求,返回UTF-8文本内容
     * 
     * @param url 完整请求地址
     * @return 响应文本
     */
    public String get(String url)
    {
        return new String(getBytes(url), StandardCharsets.UTF_8);
    }

    /**
     * GET请求,返回原始字节内容(用于zip等二进制下载)
     * 
     * @param url 完整请求地址
     * @return 响应字节
     */
    public byte[] getBytes(String url)
    {
        try
        {
            HttpRequest request = HttpRequest.newBuilder(URI.create(url)).timeout(REQUEST_TIMEOUT)
                    .header("User-Agent", "Mozilla/5.0 (compatible; ruoyi-ygocdb-sync)").GET().build();
            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() != 200)
            {
                throw new ServiceException("ygocdb接口请求失败,HTTP状态码:" + response.statusCode() + ",url:" + url);
            }
            return response.body();
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            throw new ServiceException("ygocdb接口请求被中断:" + e.getMessage());
        }
        catch (ServiceException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new ServiceException("ygocdb接口请求异常:" + e.getMessage());
        }
    }
}
