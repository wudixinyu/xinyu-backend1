package com.ruoyi.system.service;

/**
 * AI生成Service接口（调用"模型管理"配置的 OpenAI 兼容接口生成文本/图片/视频）
 * 
 * @author ruoyi
 * @date 2026-10-14
 */
public interface IAiGenerateService 
{
    /**
     * 执行生成任务（异步线程中调用，更新任务状态与结果）
     * 
     * @param taskId 任务主键
     */
    public void runTask(Long taskId);
}
