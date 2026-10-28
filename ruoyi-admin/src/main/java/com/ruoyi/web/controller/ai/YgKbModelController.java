package com.ruoyi.web.controller.ai;

import java.util.List;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.YgKbCustomApi;
import com.ruoyi.system.domain.YgKbModelConfig;
import com.ruoyi.system.kb.config.KbModelManager;
import com.ruoyi.system.mapper.YgKbCustomApiMapper;

/**
 * 知识库模型管理Controller（运行时配置，保存后立即生效）
 *
 * @author ruoyi
 * @date 2026-10-14
 */
@RestController
@RequestMapping("/ai/kb/model")
public class YgKbModelController extends BaseController
{
    @Autowired
    private KbModelManager kbModelManager;

    @Autowired
    private YgKbCustomApiMapper customApiMapper;

    /**
     * 获取当前生效的模型配置
     */
    @PreAuthorize("@ss.hasPermi('ai:kb:model')")
    @GetMapping("/config")
    public AjaxResult config()
    {
        return success(kbModelManager.currentConfig());
    }

    /**
     * 保存模型配置并立即生效
     */
    @PreAuthorize("@ss.hasPermi('ai:kb:model:edit')")
    @Log(title = "知识库模型配置", businessType = BusinessType.UPDATE)
    @PutMapping("/config")
    public AjaxResult saveConfig(@RequestBody YgKbModelConfig config)
    {
        kbModelManager.saveAndApply(config, getUsername());
        return success();
    }

    /**
     * 拉取服务端可用模型列表（Ollama /api/tags 或 OpenAI兼容 /models）
     *
     * @param provider    ollama | openai | custom
     * @param customApiId 自定义API ID（已保存条目）
     * @param baseUrl     自定义API 服务地址（未保存条目的临时探测）
     * @param apiKey      自定义API 密钥（未保存条目的临时探测）
     */
    @PreAuthorize("@ss.hasPermi('ai:kb:model')")
    @GetMapping("/models")
    public AjaxResult models(@RequestParam(required = false) String provider,
                             @RequestParam(required = false) Long customApiId,
                             @RequestParam(required = false) String baseUrl,
                             @RequestParam(required = false) String apiKey)
    {
        List<String> models = kbModelManager.listRemoteModels(provider, customApiId, baseUrl, apiKey);
        return success(models);
    }

    /**
     * 模型服务探活
     */
    @PreAuthorize("@ss.hasPermi('ai:kb:model')")
    @GetMapping("/health")
    public AjaxResult health()
    {
        Map<String, Object> info = kbModelManager.health();
        return success(info);
    }

    /**
     * 查询自定义API列表
     */
    @PreAuthorize("@ss.hasPermi('ai:kb:model')")
    @GetMapping("/custom-apis")
    public AjaxResult listCustomApis(YgKbCustomApi query)
    {
        return success(customApiMapper.selectYgKbCustomApiList(query));
    }

    /**
     * 新增自定义API
     */
    @PreAuthorize("@ss.hasPermi('ai:kb:model:edit')")
    @Log(title = "自定义API配置", businessType = BusinessType.INSERT)
    @PostMapping("/custom-apis")
    public AjaxResult addCustomApi(@RequestBody YgKbCustomApi customApi)
    {
        validateCustomApi(customApi);
        customApi.setCreateBy(getUsername());
        return toAjax(customApiMapper.insertYgKbCustomApi(customApi));
    }

    /**
     * 修改自定义API
     */
    @PreAuthorize("@ss.hasPermi('ai:kb:model:edit')")
    @Log(title = "自定义API配置", businessType = BusinessType.UPDATE)
    @PutMapping("/custom-apis")
    public AjaxResult updateCustomApi(@RequestBody YgKbCustomApi customApi)
    {
        if (customApi.getId() == null)
        {
            throw new ServiceException("自定义API ID不能为空");
        }
        validateCustomApi(customApi);
        customApi.setUpdateBy(getUsername());
        return toAjax(customApiMapper.updateYgKbCustomApi(customApi));
    }

    /**
     * 删除自定义API（正在被对话/向量化使用时禁止删除）
     */
    @PreAuthorize("@ss.hasPermi('ai:kb:model:edit')")
    @Log(title = "自定义API配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/custom-apis/{ids}")
    public AjaxResult removeCustomApi(@PathVariable Long[] ids)
    {
        for (Long id : ids)
        {
            if (customApiMapper.countUsage(id) > 0)
            {
                throw new ServiceException("该自定义API正在被对话或向量化使用，请先在基础设置中切换后再删除");
            }
        }
        return toAjax(customApiMapper.deleteYgKbCustomApiByIds(ids));
    }

    private void validateCustomApi(YgKbCustomApi customApi)
    {
        if (StringUtils.isEmpty(customApi.getName()))
        {
            throw new ServiceException("自定义API名称不能为空");
        }
        if (StringUtils.isEmpty(customApi.getBaseUrl()))
        {
            throw new ServiceException("自定义API服务地址不能为空");
        }
    }
}
