<template>
  <div class="app-container">
    <el-alert
      title="修改向量化模型或向量维度后，已有知识库需到【知识库-文档管理】中点击【重建索引】才能继续问答。"
      type="info"
      show-icon
      :closable="false"
      class="mb8"
    />

    <el-form ref="modelRef" :model="form" :rules="rules" label-position="top" v-loading="loading" class="kb-model-form">
      <el-card shadow="never" class="mb8">
        <template #header>
          <div class="card-header">
            <span>基础设置</span>
            <el-tag v-if="healthResult" :type="healthResult.available ? 'success' : 'danger'" size="small">
              {{ healthResult.available ? '服务正常' : '服务不可用' }}
            </el-tag>
          </div>
        </template>
        <el-row :gutter="24">
          <el-col :xs="24" :sm="12">
            <el-form-item label="对话模型提供方" prop="chatProvider">
              <el-radio-group v-model="form.chatProvider">
                <el-radio value="ollama">Ollama</el-radio>
                <el-radio value="openai">llama.cpp（OpenAI兼容）</el-radio>
                <el-radio value="custom">自定义API（OpenAI兼容）</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item v-if="form.chatProvider === 'custom'" label="对话使用的自定义API" prop="customChatApiId">
              <el-select v-model="form.customChatApiId" filterable placeholder="请选择自定义API" style="width: 100%">
                <el-option v-for="api in customApis" :key="api.id" :label="api.name" :value="api.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="向量化模型提供方" prop="embeddingProvider">
              <el-radio-group v-model="form.embeddingProvider">
                <el-radio value="ollama">Ollama</el-radio>
                <el-radio value="openai">llama.cpp（OpenAI兼容）</el-radio>
                <el-radio value="custom">自定义API（OpenAI兼容）</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item v-if="form.embeddingProvider === 'custom'" label="向量化使用的自定义API" prop="customEmbeddingApiId">
              <el-select v-model="form.customEmbeddingApiId" filterable placeholder="请选择自定义API" style="width: 100%">
                <el-option v-for="api in customApis" :key="api.id" :label="api.name" :value="api.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <div class="update-info">
          <template v-if="form.updateTime">{{ form.updateBy || '-' }} 于 {{ form.updateTime }} 保存</template>
          <template v-else>当前为 application.yml 默认配置（尚未在页面保存过）</template>
        </div>
      </el-card>

      <el-row :gutter="16" class="provider-row mb8">
        <el-col :xs="24" :lg="8">
          <el-card shadow="never" class="provider-card">
            <template #header>
              <div class="card-header">
                <span class="card-title">
                  Ollama 配置
                  <el-tag v-if="form.chatProvider === 'ollama'" size="small">对话使用中</el-tag>
                  <el-tag v-if="form.embeddingProvider === 'ollama'" size="small" type="success">向量化使用中</el-tag>
                </span>
                <el-button link type="primary" icon="Refresh" :loading="ollamaModelsLoading" @click="handleFetchOllamaModels">获取模型列表</el-button>
              </div>
            </template>
            <el-form-item label="服务地址" prop="ollamaBaseUrl">
              <el-input v-model="form.ollamaBaseUrl" placeholder="http://localhost:11434" />
            </el-form-item>
            <el-form-item label="对话模型" prop="ollamaChatModel">
              <el-select v-model="form.ollamaChatModel" filterable allow-create default-first-option placeholder="如 qwen2.5:7b" style="width: 100%">
                <el-option v-for="m in ollamaModels" :key="m" :label="m" :value="m" />
              </el-select>
            </el-form-item>
            <el-form-item label="向量化模型" prop="ollamaEmbeddingModel">
              <el-select v-model="form.ollamaEmbeddingModel" filterable allow-create default-first-option placeholder="如 bge-m3 / nomic-embed-text" style="width: 100%">
                <el-option v-for="m in ollamaEmbeddingOptions" :key="m" :label="m" :value="m" />
              </el-select>
            </el-form-item>
            <el-row :gutter="12">
              <el-col :span="8">
                <el-form-item label="温度" prop="ollamaChatTemperature">
                  <el-input-number v-model="form.ollamaChatTemperature" :min="0" :max="2" :step="0.1" :precision="2" controls-position="right" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="向量维度" prop="ollamaEmbeddingDim">
                  <el-input-number v-model="form.ollamaEmbeddingDim" :min="1" :max="8192" controls-position="right" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="超时(秒)" prop="ollamaTimeoutSeconds">
                  <el-input-number v-model="form.ollamaTimeoutSeconds" :min="10" :max="3600" controls-position="right" style="width: 100%" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-card>
        </el-col>
        <el-col :xs="24" :lg="8">
          <el-card shadow="never" class="provider-card">
            <template #header>
              <div class="card-header">
                <span class="card-title">
                  llama.cpp（OpenAI 兼容）配置
                  <el-tag v-if="form.chatProvider === 'openai'" size="small">对话使用中</el-tag>
                  <el-tag v-if="form.embeddingProvider === 'openai'" size="small" type="success">向量化使用中</el-tag>
                </span>
                <el-button link type="primary" icon="Refresh" :loading="openaiModelsLoading" @click="handleFetchOpenaiModels">获取模型列表</el-button>
              </div>
            </template>
            <el-form-item label="服务地址" prop="openaiBaseUrl">
              <el-input v-model="form.openaiBaseUrl" placeholder="http://localhost:8080/v1" />
            </el-form-item>
            <el-form-item label="API Key" prop="openaiApiKey">
              <el-input v-model="form.openaiApiKey" placeholder="llama-server 无鉴权时填 none" />
            </el-form-item>
            <el-form-item label="对话模型" prop="openaiChatModel">
              <el-select v-model="form.openaiChatModel" filterable allow-create default-first-option placeholder="如 qwen2.5-7b-instruct-q8_0" style="width: 100%">
                <el-option v-for="m in openaiChatOptions" :key="m" :label="m" :value="m" />
              </el-select>
            </el-form-item>
            <el-form-item label="向量化模型" prop="openaiEmbeddingModel">
              <el-select v-model="form.openaiEmbeddingModel" filterable allow-create default-first-option placeholder="如 bge-m3（llama-server 需 --embedding 启动）" style="width: 100%">
                <el-option v-for="m in openaiEmbeddingOptions" :key="m" :label="m" :value="m" />
              </el-select>
            </el-form-item>
            <el-row :gutter="12">
              <el-col :span="8">
                <el-form-item label="温度" prop="openaiChatTemperature">
                  <el-input-number v-model="form.openaiChatTemperature" :min="0" :max="2" :step="0.1" :precision="2" controls-position="right" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="向量维度" prop="openaiEmbeddingDim">
                  <el-input-number v-model="form.openaiEmbeddingDim" :min="1" :max="8192" controls-position="right" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="超时(秒)" prop="openaiTimeoutSeconds">
                  <el-input-number v-model="form.openaiTimeoutSeconds" :min="10" :max="3600" controls-position="right" style="width: 100%" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-card>
        </el-col>
        <el-col :xs="24" :lg="8">
          <el-card shadow="never" class="provider-card">
            <template #header>
              <div class="card-header">
                <span class="card-title">
                  自定义API（OpenAI 兼容）配置
                  <el-tag v-if="form.chatProvider === 'custom'" size="small">对话使用中</el-tag>
                  <el-tag v-if="form.embeddingProvider === 'custom'" size="small" type="success">向量化使用中</el-tag>
                </span>
                <el-button link type="primary" icon="Plus" @click="handleAddCustom">新增自定义API</el-button>
              </div>
            </template>
            <div v-if="customApis.length === 0" class="empty-tip">暂无自定义API，点击右上角「新增自定义API」添加</div>
            <div v-for="api in customApis" :key="api.id" class="custom-item">
              <div class="custom-item-head">
                <span class="custom-item-name">
                  {{ api.name }}
                  <el-tag v-if="form.chatProvider === 'custom' && form.customChatApiId === api.id" size="small">对话使用中</el-tag>
                  <el-tag v-if="form.embeddingProvider === 'custom' && form.customEmbeddingApiId === api.id" size="small" type="success">向量化使用中</el-tag>
                </span>
                <span class="custom-item-actions">
                  <el-button link type="primary" icon="Refresh" :loading="!!customModelsLoading[api.id]" @click="handleFetchCustomModels(api)">获取模型列表</el-button>
                  <el-button link type="primary" icon="Edit" @click="handleEditCustom(api)">编辑</el-button>
                  <el-button link type="danger" icon="Delete" @click="handleDeleteCustom(api)">删除</el-button>
                </span>
              </div>
              <div class="custom-item-body">
                <div class="custom-item-line">地址：{{ api.baseUrl || '-' }}</div>
                <div class="custom-item-line">对话模型：{{ api.chatModel || '-' }}</div>
                <div class="custom-item-line">向量化模型：{{ api.embeddingModel || '-' }}（维度 {{ api.embeddingDim || '-' }}）</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-card shadow="never" class="mb8">
        <template #header>
          <span>分块与检索</span>
        </template>
        <el-row :gutter="24">
          <el-col :xs="12" :sm="6">
            <el-form-item label="分块策略" prop="chunkStrategy">
              <el-select v-model="form.chunkStrategy" style="width: 100%">
                <el-option label="固定长度递归" value="recursive" />
                <el-option label="按段落" value="paragraph" />
                <el-option label="按句子" value="sentence" />
                <el-option label="自定义分隔符" value="separator" />
                <el-option label="按Markdown标题" value="markdown" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col v-if="form.chunkStrategy === 'separator'" :xs="12" :sm="6">
            <el-form-item label="分隔符" prop="chunkSeparator">
              <el-input v-model="form.chunkSeparator" placeholder="如 ---、===，换行填 \n" />
            </el-form-item>
          </el-col>
          <el-col :xs="12" :sm="6">
            <el-form-item label="分块最大长度" prop="chunkMaxSize">
              <el-input-number v-model="form.chunkMaxSize" :min="50" :max="4000" :step="50" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :xs="12" :sm="6">
            <el-form-item label="分块重叠长度" prop="chunkOverlap">
              <el-input-number v-model="form.chunkOverlap" :min="0" :max="4000" :step="10" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :xs="12" :sm="6">
            <el-form-item label="检索片段数" prop="retrievalMaxResults">
              <el-input-number v-model="form.retrievalMaxResults" :min="1" :max="20" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :xs="12" :sm="6">
            <el-form-item label="最低相似度" prop="retrievalMinScore">
              <el-input-number v-model="form.retrievalMinScore" :min="0" :max="1" :step="0.05" :precision="3" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <div class="form-tip">
          {{ chunkStrategyTip }} 分块单位为字符（中文约1字≈1token）；最低相似度建议 0.3 左右；修改分块策略后需到【知识库-文档管理】重建索引生效。
        </div>
      </el-card>

      <div class="form-actions">
        <el-button type="primary" icon="Check" :loading="saving" @click="handleSave" v-hasPermi="['ai:kb:model:edit']">保存并应用</el-button>
        <el-button icon="Connection" :loading="healthLoading" @click="handleHealth">测试连接</el-button>
        <el-button icon="RefreshLeft" @click="loadConfig">重置</el-button>
      </div>
    </el-form>

    <!-- 新增/编辑自定义API -->
    <el-dialog v-model="customDialogVisible" :title="customDialogTitle" width="640px" append-to-body>
      <el-form ref="customFormRef" :model="customForm" :rules="customRules" label-position="top">
        <el-form-item label="名称" prop="name">
          <el-input v-model="customForm.name" maxlength="100" placeholder="如 SiliconFlow、ModelScope" />
        </el-form-item>
        <el-form-item label="服务地址" prop="baseUrl">
          <el-input v-model="customForm.baseUrl" placeholder="如 https://api.siliconflow.cn/v1" />
        </el-form-item>
        <el-form-item label="API Key" prop="apiKey">
          <el-input v-model="customForm.apiKey" type="password" show-password placeholder="云平台密钥，如 sk-..." />
        </el-form-item>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="对话模型" prop="chatModel">
              <el-select v-model="customForm.chatModel" filterable allow-create default-first-option placeholder="如 Qwen/Qwen2.5-7B-Instruct" style="width: 100%">
                <el-option v-for="m in dialogChatOptions" :key="m" :label="m" :value="m" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="温度" prop="chatTemperature">
              <el-input-number v-model="customForm.chatTemperature" :min="0" :max="2" :step="0.1" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="向量化模型" prop="embeddingModel">
              <el-select v-model="customForm.embeddingModel" filterable allow-create default-first-option placeholder="如 BAAI/bge-m3" style="width: 100%">
                <el-option v-for="m in dialogEmbeddingOptions" :key="m" :label="m" :value="m" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="向量维度" prop="embeddingDim">
              <el-input-number v-model="customForm.embeddingDim" :min="1" :max="8192" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="超时(秒)" prop="timeoutSeconds">
              <el-input-number v-model="customForm.timeoutSeconds" :min="10" :max="3600" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <div class="dialog-tip">
          <el-button link type="primary" icon="Refresh" :loading="dialogModelsLoading" @click="handleFetchDialogModels">获取模型列表</el-button>
          <span class="form-tip">填写服务地址与 API Key 后可拉取模型列表用于选择</span>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="customDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="customSubmitting" @click="handleSubmitCustom">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="KbModel">
import {
  getKbModelConfig, updateKbModelConfig, listRemoteModels, kbModelHealth,
  listKbCustomApis, addKbCustomApi, updateKbCustomApi, delKbCustomApi
} from "@/api/ai/kb"

const { proxy } = getCurrentInstance()

const loading = ref(false)
const saving = ref(false)
const ollamaModelsLoading = ref(false)
const openaiModelsLoading = ref(false)
const healthLoading = ref(false)
const healthResult = ref(null)
const ollamaModels = ref([])
const openaiModels = ref([])
const customApis = ref([])
const customModelsLoading = reactive({})

const customDialogVisible = ref(false)
const customDialogTitle = ref("")
const customSubmitting = ref(false)
const customFormRef = ref(null)
const dialogModels = ref([])
const dialogModelsLoading = ref(false)

const data = reactive({
  form: {},
  rules: {
    chatProvider: [{ required: true, message: "请选择对话模型提供方", trigger: "change" }],
    embeddingProvider: [{ required: true, message: "请选择向量化模型提供方", trigger: "change" }],
    customChatApiId: [{ validator: validateCustomChat, trigger: "change" }],
    customEmbeddingApiId: [{ validator: validateCustomEmbedding, trigger: "change" }],
    ollamaBaseUrl: [{ required: true, message: "Ollama服务地址不能为空", trigger: "blur" }],
    ollamaChatModel: [{ required: true, message: "对话模型不能为空", trigger: "change" }],
    ollamaEmbeddingModel: [{ required: true, message: "向量化模型不能为空", trigger: "change" }],
    openaiBaseUrl: [{ required: true, message: "OpenAI兼容地址不能为空", trigger: "blur" }],
    openaiChatModel: [{ required: true, message: "OpenAI兼容对话模型不能为空", trigger: "change" }],
    openaiEmbeddingModel: [{ required: true, message: "OpenAI兼容向量化模型不能为空", trigger: "change" }],
  },
})

const { form, rules } = toRefs(data)

/** 自定义API 表单 */
function emptyCustomForm() {
  return {
    id: null,
    name: "",
    baseUrl: "",
    apiKey: "",
    chatModel: "",
    chatTemperature: 0.3,
    embeddingModel: "",
    embeddingDim: 1024,
    timeoutSeconds: 60,
  }
}
const customForm = reactive(emptyCustomForm())
const customRules = {
  name: [{ required: true, message: "名称不能为空", trigger: "blur" }],
  baseUrl: [{ required: true, message: "服务地址不能为空", trigger: "blur" }],
  chatTemperature: [{ required: true, message: "温度不能为空", trigger: "change" }],
  embeddingDim: [{ required: true, message: "向量维度不能为空", trigger: "change" }],
  timeoutSeconds: [{ required: true, message: "超时秒数不能为空", trigger: "change" }],
}

const EMB_PATTERN = /embed|bge|nomic|minilm|m3e|jina/i

/** Ollama 向量化模型下拉（按常见 embedding 名称特征过滤，无匹配则显示全部） */
const ollamaEmbeddingOptions = computed(() => {
  const emb = ollamaModels.value.filter(m => EMB_PATTERN.test(m))
  return emb.length > 0 ? emb : ollamaModels.value
})

/** llama.cpp 对话模型下拉（排除 embedding 类模型） */
const openaiChatOptions = computed(() => {
  const chat = openaiModels.value.filter(m => !EMB_PATTERN.test(m))
  return chat.length > 0 ? chat : openaiModels.value
})

/** llama.cpp 向量化模型下拉（仅 embedding 类模型，无匹配则显示全部） */
const openaiEmbeddingOptions = computed(() => {
  const emb = openaiModels.value.filter(m => EMB_PATTERN.test(m))
  return emb.length > 0 ? emb : openaiModels.value
})

/** 新增/编辑弹窗：对话模型下拉（排除 embedding 类模型） */
const dialogChatOptions = computed(() => {
  const chat = dialogModels.value.filter(m => !EMB_PATTERN.test(m))
  return chat.length > 0 ? chat : dialogModels.value
})

/** 新增/编辑弹窗：向量化模型下拉（仅 embedding 类模型，无匹配则显示全部） */
const dialogEmbeddingOptions = computed(() => {
  const emb = dialogModels.value.filter(m => EMB_PATTERN.test(m))
  return emb.length > 0 ? emb : dialogModels.value
})

/** 分块策略说明 */
const CHUNK_STRATEGY_TIPS = {
  recursive: "固定长度递归：按最大长度硬切分并保留重叠，适合无结构的长文本。",
  paragraph: "按段落：以空行为界聚合段落，超长段落再按长度递归切分，适合文章、说明书。",
  sentence: "按句子：以中英文句末标点为界聚合句子，语义完整性更好，适合问答类语料。",
  separator: "自定义分隔符：按填写的分隔符切分后聚合，适合固定格式（如条目、FAQ）文档。",
  markdown: "按Markdown标题：在 #/## 等标题行前切分，每个小节自成一块，适合 md 文档。",
}
const chunkStrategyTip = computed(() => CHUNK_STRATEGY_TIPS[form.value.chunkStrategy] || CHUNK_STRATEGY_TIPS.recursive)

/** 对话提供方为自定义API时必须选择一条 */
function validateCustomChat(rule, value, callback) {
  if (form.value.chatProvider === "custom" && !value) {
    callback(new Error("请选择对话使用的自定义API"))
  } else {
    callback()
  }
}

/** 向量化提供方为自定义API时必须选择一条 */
function validateCustomEmbedding(rule, value, callback) {
  if (form.value.embeddingProvider === "custom" && !value) {
    callback(new Error("请选择向量化使用的自定义API"))
  } else {
    callback()
  }
}

/** 加载当前生效配置 */
function loadConfig() {
  loading.value = true
  getKbModelConfig().then(response => {
    form.value = response.data || {}
    healthResult.value = null
  }).finally(() => {
    loading.value = false
  })
  loadCustomApis()
}

/** 加载自定义API列表 */
function loadCustomApis() {
  listKbCustomApis().then(response => {
    customApis.value = response.data || []
  })
}

/** 保存并应用 */
function handleSave() {
  proxy.$refs["modelRef"].validate(valid => {
    if (!valid) return
    saving.value = true
    updateKbModelConfig(form.value).then(() => {
      proxy.$modal.msgSuccess("保存成功，已运行时生效")
      loadConfig()
    }).finally(() => {
      saving.value = false
    })
  })
}

/** 拉取 Ollama 模型列表 */
function handleFetchOllamaModels() {
  ollamaModelsLoading.value = true
  listRemoteModels({ provider: "ollama" }).then(response => {
    ollamaModels.value = response.data || []
    proxy.$modal.msgSuccess("Ollama 获取到 " + ollamaModels.value.length + " 个模型")
  }).finally(() => {
    ollamaModelsLoading.value = false
  })
}

/** 拉取 llama.cpp（OpenAI兼容）模型列表 */
function handleFetchOpenaiModels() {
  openaiModelsLoading.value = true
  listRemoteModels({ provider: "openai" }).then(response => {
    openaiModels.value = response.data || []
    proxy.$modal.msgSuccess("llama.cpp 获取到 " + openaiModels.value.length + " 个模型")
  }).finally(() => {
    openaiModelsLoading.value = false
  })
}

/** 拉取某条自定义API 的模型数量（列表卡片） */
function handleFetchCustomModels(api) {
  customModelsLoading[api.id] = true
  listRemoteModels({ provider: "custom", customApiId: api.id }).then(response => {
    proxy.$modal.msgSuccess("「" + api.name + "」获取到 " + (response.data || []).length + " 个模型")
  }).finally(() => {
    customModelsLoading[api.id] = false
  })
}

/** 新增自定义API */
function handleAddCustom() {
  Object.assign(customForm, emptyCustomForm())
  dialogModels.value = []
  customDialogTitle.value = "新增自定义API"
  customDialogVisible.value = true
  nextTick(() => customFormRef.value && customFormRef.value.clearValidate())
}

/** 编辑自定义API */
function handleEditCustom(api) {
  Object.assign(customForm, emptyCustomForm(), {
    id: api.id,
    name: api.name,
    baseUrl: api.baseUrl,
    apiKey: api.apiKey,
    chatModel: api.chatModel,
    chatTemperature: api.chatTemperature == null ? 0.3 : Number(api.chatTemperature),
    embeddingModel: api.embeddingModel,
    embeddingDim: api.embeddingDim == null ? 1024 : api.embeddingDim,
    timeoutSeconds: api.timeoutSeconds == null ? 60 : api.timeoutSeconds,
  })
  dialogModels.value = []
  customDialogTitle.value = "编辑自定义API"
  customDialogVisible.value = true
  nextTick(() => customFormRef.value && customFormRef.value.clearValidate())
}

/** 删除自定义API */
function handleDeleteCustom(api) {
  proxy.$modal.confirm("确认删除自定义API「" + api.name + "」？").then(() => {
    return delKbCustomApi(api.id)
  }).then(() => {
    proxy.$modal.msgSuccess("删除成功")
    loadCustomApis()
  }).catch(() => {})
}

/** 弹窗内拉取模型列表（未保存条目直接用当前填写的地址与密钥） */
function handleFetchDialogModels() {
  if (!customForm.baseUrl) {
    proxy.$modal.msgWarning("请先填写服务地址")
    return
  }
  dialogModelsLoading.value = true
  listRemoteModels({ provider: "custom", baseUrl: customForm.baseUrl, apiKey: customForm.apiKey }).then(response => {
    dialogModels.value = response.data || []
    proxy.$modal.msgSuccess("获取到 " + dialogModels.value.length + " 个模型")
  }).finally(() => {
    dialogModelsLoading.value = false
  })
}

/** 提交自定义API（新增/编辑） */
function handleSubmitCustom() {
  customFormRef.value.validate(valid => {
    if (!valid) return
    customSubmitting.value = true
    const edit = customForm.id != null
    const request = edit ? updateKbCustomApi(customForm) : addKbCustomApi(customForm)
    request.then(() => {
      proxy.$modal.msgSuccess(edit ? "修改成功" : "新增成功")
      customDialogVisible.value = false
      loadCustomApis()
      // 若修改的正是当前使用中的自定义API，重新应用配置以刷新运行时模型
      if (edit && (form.value.customChatApiId === customForm.id || form.value.customEmbeddingApiId === customForm.id)) {
        updateKbModelConfig(form.value).then(() => loadConfig())
      }
    }).finally(() => {
      customSubmitting.value = false
    })
  })
}

/** 测试连接 */
function handleHealth() {
  healthLoading.value = true
  kbModelHealth().then(response => {
    healthResult.value = response.data
    if (healthResult.value.available) {
      proxy.$modal.msgSuccess(healthResult.value.message)
    } else {
      proxy.$modal.msgError(healthResult.value.message)
    }
  }).finally(() => {
    healthLoading.value = false
  })
}

loadConfig()
</script>

<style scoped lang="scss">
.kb-model-form {
  max-width: 1400px;
}
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}
.provider-row {
  :deep(.el-col) {
    display: flex;
    flex-direction: column;
    margin-bottom: 0;
  }
  @media (max-width: 1199px) {
    :deep(.el-col) {
      margin-bottom: 16px;
    }
  }
}
.provider-card {
  flex: 1;
}
.update-info {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}
.form-tip {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}
.form-actions {
  display: flex;
  gap: 8px;
}
.empty-tip {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}
.custom-item {
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 4px;
  padding: 8px 12px;
  margin-bottom: 12px;
}
.custom-item:last-child {
  margin-bottom: 0;
}
.custom-item-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 8px;
}
.custom-item-name {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
}
.custom-item-actions {
  display: flex;
  align-items: center;
  gap: 4px;
}
.custom-item-body {
  margin-top: 6px;
}
.custom-item-line {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  line-height: 1.8;
  word-break: break-all;
}
.dialog-tip {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
