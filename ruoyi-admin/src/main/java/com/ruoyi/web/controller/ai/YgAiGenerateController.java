package com.ruoyi.web.controller.ai;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YgAiGenerateTask;
import com.ruoyi.system.service.IAiGenerateService;
import com.ruoyi.system.service.IYgAiGenerateTaskService;

/**
 * AI生成Controller（画布节点：文本/图片/视频生成，异步任务+轮询）
 * 
 * @author ruoyi
 * @date 2026-10-14
 */
@RestController
@RequestMapping("/ai/generate")
public class YgAiGenerateController extends BaseController
{
    @Autowired
    private IYgAiGenerateTaskService ygAiGenerateTaskService;

    @Autowired
    private IAiGenerateService aiGenerateService;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * 发起文本生成任务
     */
    @PreAuthorize("@ss.hasPermi('ai:canvas:generate')")
    @Log(title = "AI生成", businessType = BusinessType.INSERT)
    @PostMapping("/text")
    public AjaxResult generateText(@RequestBody YgAiGenerateTask task)
    {
        return createTask(task, 1);
    }

    /**
     * 发起图片生成任务
     */
    @PreAuthorize("@ss.hasPermi('ai:canvas:generate')")
    @Log(title = "AI生成", businessType = BusinessType.INSERT)
    @PostMapping("/image")
    public AjaxResult generateImage(@RequestBody YgAiGenerateTask task)
    {
        return createTask(task, 2);
    }

    /**
     * 发起视频生成任务
     */
    @PreAuthorize("@ss.hasPermi('ai:canvas:generate')")
    @Log(title = "AI生成", businessType = BusinessType.INSERT)
    @PostMapping("/video")
    public AjaxResult generateVideo(@RequestBody YgAiGenerateTask task)
    {
        return createTask(task, 3);
    }

    /**
     * 查询单个任务状态（前端轮询）
     */
    @PreAuthorize("@ss.hasPermi('ai:canvas:generate')")
    @GetMapping("/task/{taskId}")
    public AjaxResult getTask(@PathVariable("taskId") Long taskId)
    {
        return success(ygAiGenerateTaskService.selectYgAiGenerateTaskByTaskId(taskId));
    }

    /**
     * 批量查询任务状态（前端轮询）
     */
    @PreAuthorize("@ss.hasPermi('ai:canvas:generate')")
    @GetMapping("/task/list/{taskIds}")
    public AjaxResult listTask(@PathVariable("taskIds") List<Long> taskIds)
    {
        if (StringUtils.isEmpty(taskIds))
        {
            return success(java.util.Collections.emptyList());
        }
        return success(ygAiGenerateTaskService.selectYgAiGenerateTaskByTaskIds(taskIds));
    }

    private AjaxResult createTask(YgAiGenerateTask task, int generateType)
    {
        if (task.getModelId() == null)
        {
            return AjaxResult.error("请先选择模型");
        }
        if (StringUtils.isEmpty(task.getPrompt()) || StringUtils.isBlank(task.getPrompt()))
        {
            return AjaxResult.error("提示词不能为空");
        }
        task.setGenerateType(generateType);
        task.setStatus("0");
        task.setCreateBy(getUsername());
        if (task.getCanvasId() == null)
        {
            task.setCanvasId(0L);
        }
        if (task.getNodeId() == null)
        {
            task.setNodeId("");
        }
        ygAiGenerateTaskService.insertYgAiGenerateTask(task);
        final Long taskId = task.getTaskId();
        threadPoolTaskExecutor.execute(() -> aiGenerateService.runTask(taskId));
        AjaxResult ajax = AjaxResult.success();
        ajax.put("taskId", taskId);
        return ajax;
    }
}
