package com.ruoyi.web.controller.ai;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.alibaba.fastjson2.JSON;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.YgKnowledgeBase;
import com.ruoyi.system.domain.dto.KbChatRequest;
import com.ruoyi.system.domain.vo.KbChatResult;
import com.ruoyi.system.domain.vo.KbSourceVo;
import com.ruoyi.system.kb.config.KbModelManager;
import com.ruoyi.system.kb.rag.KbQaService;
import com.ruoyi.system.service.IYgKnowledgeBaseService;

/**
 * 知识库Controller
 *
 * @author ruoyi
 * @date 2026-10-14
 */
@RestController
@RequestMapping("/ai/kb")
public class YgKnowledgeBaseController extends BaseController
{
    @Autowired
    private IYgKnowledgeBaseService ygKnowledgeBaseService;

    @Autowired
    private KbQaService kbQaService;

    @Autowired
    private KbModelManager kbModelManager;

    /**
     * 查询知识库列表
     */
    @PreAuthorize("@ss.hasPermi('ai:kb:list')")
    @GetMapping("/list")
    public TableDataInfo list(YgKnowledgeBase ygKnowledgeBase)
    {
        startPage();
        List<YgKnowledgeBase> list = ygKnowledgeBaseService.selectYgKnowledgeBaseList(ygKnowledgeBase);
        return getDataTable(list);
    }

    /**
     * 模型服务探活
     */
    @PreAuthorize("@ss.hasPermi('ai:kb:list')")
    @GetMapping("/health")
    public AjaxResult health()
    {
        Map<String, Object> info = kbModelManager.health();
        return success(info);
    }

    /**
     * 获取知识库详细信息
     */
    @PreAuthorize("@ss.hasPermi('ai:kb:query')")
    @GetMapping(value = "/{kbId}")
    public AjaxResult getInfo(@PathVariable("kbId") Long kbId)
    {
        return success(ygKnowledgeBaseService.selectYgKnowledgeBaseByKbId(kbId));
    }

    /**
     * 新增知识库
     */
    @PreAuthorize("@ss.hasPermi('ai:kb:add')")
    @Log(title = "知识库", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody YgKnowledgeBase ygKnowledgeBase)
    {
        ygKnowledgeBase.setCreateBy(getUsername());
        return toAjax(ygKnowledgeBaseService.insertYgKnowledgeBase(ygKnowledgeBase));
    }

    /**
     * 修改知识库
     */
    @PreAuthorize("@ss.hasPermi('ai:kb:edit')")
    @Log(title = "知识库", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody YgKnowledgeBase ygKnowledgeBase)
    {
        ygKnowledgeBase.setUpdateBy(getUsername());
        return toAjax(ygKnowledgeBaseService.updateYgKnowledgeBase(ygKnowledgeBase));
    }

    /**
     * 删除知识库（级联删除文档与向量索引）
     */
    @PreAuthorize("@ss.hasPermi('ai:kb:remove')")
    @Log(title = "知识库", businessType = BusinessType.DELETE)
    @DeleteMapping("/{kbIds}")
    public AjaxResult remove(@PathVariable Long[] kbIds)
    {
        return toAjax(ygKnowledgeBaseService.deleteYgKnowledgeBaseByKbIds(kbIds));
    }

    /**
     * 知识库RAG问答（管理端测试与移动端共用）
     */
    @PreAuthorize("@ss.hasPermi('ai:kb:chat')")
    @Log(title = "知识库问答", businessType = BusinessType.OTHER)
    @PostMapping("/chat")
    public AjaxResult chat(@RequestBody KbChatRequest request)
    {
        KbChatResult result = kbQaService.chat(request.getKbId(), request.getQuestion(), getUsername());
        return success(result);
    }

    /**
     * 知识库RAG流式问答（SSE：sources/delta/done/error 事件，data 为 JSON）
     */
    @PreAuthorize("@ss.hasPermi('ai:kb:chat')")
    @PostMapping(value = "/chat/stream", produces = "text/event-stream;charset=UTF-8")
    public SseEmitter chatStream(@RequestBody KbChatRequest request)
    {
        SseEmitter emitter = new SseEmitter(300000L);
        String username = getUsername();
        kbQaService.chatStream(request.getKbId(), request.getQuestion(), username, new KbQaService.KbStreamListener()
        {
            @Override
            public void onSources(List<KbSourceVo> sources)
            {
                sendEvent(emitter, "sources", sources);
            }

            @Override
            public void onToken(String token)
            {
                sendEvent(emitter, "delta", token);
            }

            @Override
            public void onComplete(long costMs)
            {
                sendEvent(emitter, "done", costMs);
                emitter.complete();
            }

            @Override
            public void onError(String message)
            {
                sendEvent(emitter, "error", message);
                emitter.complete();
            }
        });
        return emitter;
    }

    private void sendEvent(SseEmitter emitter, String type, Object data)
    {
        try
        {
            Map<String, Object> payload = new HashMap<>();
            payload.put("type", type);
            payload.put("data", data);
            emitter.send(JSON.toJSONString(payload));
        }
        catch (Exception e)
        {
            emitter.completeWithError(e);
        }
    }
}
