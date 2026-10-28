<template>
  <div class="app-container">
    <el-alert
      v-if="healthInfo && !healthInfo.available"
      :title="healthInfo.message"
      type="warning"
      show-icon
      :closable="false"
      class="mb8"
    />

    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="名称" prop="kbName">
        <el-input
          v-model="queryParams.kbName"
          placeholder="请输入知识库名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 200px">
          <el-option
            v-for="dict in sys_normal_disable"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
          v-hasPermi="['ai:kb:add']"
        >新增</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="kbList">
      <el-table-column label="主键" align="center" prop="kbId" width="80" />
      <el-table-column label="知识库名称" align="center" prop="kbName" min-width="140" show-overflow-tooltip />
      <el-table-column label="描述" align="center" prop="description" min-width="180" show-overflow-tooltip />
      <el-table-column label="文档数" align="center" prop="docCount" width="80" />
      <el-table-column label="分块数" align="center" prop="chunkCount" width="80" />
      <el-table-column label="向量化模型" align="center" prop="embeddingModel" width="130" show-overflow-tooltip />
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template #default="scope">
          <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="300" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['ai:kb:edit']">修改</el-button>
          <el-button link type="primary" icon="Document" @click="handleDocuments(scope.row)" v-hasPermi="['ai:kb:query']">文档</el-button>
          <el-button link type="primary" icon="ChatDotRound" @click="handleChat(scope.row)" v-hasPermi="['ai:kb:chat']">问答测试</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['ai:kb:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改知识库对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="kbRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="名称" prop="kbName">
          <el-input v-model="form.kbName" placeholder="请输入知识库名称" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" maxlength="500" show-word-limit />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in sys_normal_disable"
              :key="dict.value"
              :value="dict.value"
            >{{ dict.label }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="向量化模型">
          <span>{{ form.kbId ? form.embeddingModel + '（' + form.embeddingDim + '维）' : (healthInfo ? healthInfo.embeddingModel : '') + '（新增时自动固化）' }}</span>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 文档管理抽屉 -->
    <el-drawer v-model="docOpen" :title="'文档管理 - ' + (currentKb.kbName || '')" size="60%">
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-upload
            :show-file-list="false"
            :http-request="customUpload"
            accept=".txt,.md,.markdown,.pdf,.docx"
            :disabled="uploading"
          >
            <el-button type="primary" plain icon="Upload" :loading="uploading" v-hasPermi="['ai:kb:upload']">
              {{ uploading ? '解析入库中...' : '上传文档' }}
            </el-button>
          </el-upload>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="RefreshRight" :loading="rebuilding" @click="handleRebuild" v-hasPermi="['ai:kb:rebuild']">重建索引</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button icon="Refresh" @click="getDocList">刷新</el-button>
        </el-col>
      </el-row>
      <div class="mb8" style="font-size: 12px; color: var(--el-text-color-secondary)">
        支持 txt / md / pdf / docx，单文件不超过10MB；删除文档后需点击"重建索引"以移除对应向量。
      </div>
      <el-table v-loading="docLoading" :data="docList">
        <el-table-column label="文件名" align="center" prop="fileName" min-width="180" show-overflow-tooltip />
        <el-table-column label="类型" align="center" prop="fileType" width="80" />
        <el-table-column label="大小" align="center" width="90">
          <template #default="scope">
            <span>{{ formatFileSize(scope.row.fileSize) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="分块数" align="center" prop="chunkCount" width="80" />
        <el-table-column label="索引状态" align="center" prop="indexStatus" width="100">
          <template #default="scope">
            <el-tooltip v-if="scope.row.indexStatus === '3' && scope.row.errorMsg" :content="scope.row.errorMsg" placement="top">
              <dict-tag :options="yg_kb_index_status" :value="scope.row.indexStatus" />
            </el-tooltip>
            <dict-tag v-else :options="yg_kb_index_status" :value="scope.row.indexStatus" />
          </template>
        </el-table-column>
        <el-table-column label="上传时间" align="center" prop="createTime" width="160">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="80" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="primary" icon="Delete" @click="handleDeleteDoc(scope.row)" v-hasPermi="['ai:kb:upload']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination
        v-show="docTotal > 0"
        :total="docTotal"
        v-model:page="docQuery.pageNum"
        v-model:limit="docQuery.pageSize"
        @pagination="getDocList"
      />
    </el-drawer>

    <!-- 问答测试抽屉 -->
    <el-drawer v-model="chatOpen" :title="'问答测试 - ' + (currentKb.kbName || '')" size="45%">
      <div class="chat-container">
        <div class="chat-messages" ref="chatBoxRef">
          <el-empty v-if="chatMessages.length === 0" description="输入问题，基于该知识库进行RAG问答" :image-size="80" />
          <div v-for="(msg, i) in visibleMessages" :key="i" :class="['chat-msg', msg.role]">
            <div class="msg-bubble">
              <div class="msg-text">{{ msg.content }}</div>
              <el-collapse v-if="msg.sources && msg.sources.length" class="msg-sources">
                <el-collapse-item :title="'引用来源（' + msg.sources.length + '条）'">
                  <div v-for="(s, si) in msg.sources" :key="si" class="source-item">
                    <div class="source-head">
                      <el-tag size="small" type="info">[{{ si + 1 }}] {{ s.fileName || ('文档' + s.documentId) }}</el-tag>
                      <span class="source-score">相似度 {{ (s.score * 100).toFixed(1) }}%</span>
                    </div>
                    <div class="source-snippet">{{ s.snippet }}</div>
                  </div>
                </el-collapse-item>
              </el-collapse>
              <div v-if="msg.costMs" class="msg-cost">耗时 {{ (msg.costMs / 1000).toFixed(1) }}s</div>
            </div>
          </div>
          <div v-if="chatSending && !lastMsgHasContent" class="chat-msg assistant">
            <div class="msg-bubble"><div class="msg-text">思考中...</div></div>
          </div>
        </div>
        <div class="chat-input">
          <el-input
            v-model="chatQuestion"
            type="textarea"
            :rows="2"
            resize="none"
            placeholder="请输入问题，Enter发送 / Shift+Enter换行"
            @keydown.enter.exact.prevent="handleSend"
          />
          <el-button type="primary" icon="Promotion" :loading="chatSending" @click="handleSend">发送</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup name="Kb">
import { listKb, getKb, addKb, updateKb, delKb, kbHealth, chatKbStream, listDocument, uploadDocument, delDocument, rebuildIndex } from "@/api/ai/kb"

const { proxy } = getCurrentInstance()
const { sys_normal_disable, yg_kb_index_status } = proxy.useDict('sys_normal_disable', 'yg_kb_index_status')

const kbList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const total = ref(0)
const title = ref("")
const healthInfo = ref(null)

// 文档管理
const docOpen = ref(false)
const docList = ref([])
const docLoading = ref(false)
const docTotal = ref(0)
const uploading = ref(false)
const rebuilding = ref(false)

// 问答测试
const chatOpen = ref(false)
const chatMessages = ref([])
const chatQuestion = ref("")
const chatSending = ref(false)
const chatBoxRef = ref(null)

const currentKb = ref({})

/** 隐藏尚未产出内容的流式占位气泡 */
const visibleMessages = computed(() => chatMessages.value.filter(m => !(m.role === 'assistant' && !m.content)))

/** 最后一条消息是否已开始产出内容（决定"思考中"是否显示） */
const lastMsgHasContent = computed(() => {
  const last = chatMessages.value[chatMessages.value.length - 1]
  return !!last && last.role === 'assistant' && !!last.content
})

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    kbName: undefined,
    status: undefined,
  },
  docQuery: {
    pageNum: 1,
    pageSize: 10,
    kbId: undefined,
  },
  rules: {
    kbName: [{ required: true, message: "知识库名称不能为空", trigger: "blur" }],
  },
})

const { queryParams, form, rules, docQuery } = toRefs(data)

/** 查询知识库列表 */
function getList() {
  loading.value = true
  listKb(queryParams.value).then(response => {
    kbList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

/** 模型服务探活 */
function checkHealth() {
  kbHealth().then(response => {
    healthInfo.value = response.data
  }).catch(() => {})
}

/** 文件大小格式化 */
function formatFileSize(size) {
  if (!size && size !== 0) return "-"
  if (size < 1024) return size + " B"
  if (size < 1024 * 1024) return (size / 1024).toFixed(1) + " KB"
  return (size / 1024 / 1024).toFixed(2) + " MB"
}

/** 取消按钮 */
function cancel() {
  open.value = false
  reset()
}

/** 表单重置 */
function reset() {
  form.value = {
    kbId: null,
    kbName: null,
    description: null,
    embeddingModel: null,
    embeddingDim: null,
    status: "0",
    remark: null,
  }
  proxy.resetForm("kbRef")
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加知识库"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  getKb(row.kbId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改知识库"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["kbRef"].validate(valid => {
    if (valid) {
      if (form.value.kbId != null) {
        updateKb(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addKb(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

/** 删除按钮操作 */
function handleDelete(row) {
  const kbIds = row.kbId
  proxy.$modal.confirm('是否确认删除知识库"' + row.kbName + '"？其文档与向量索引将一并删除。').then(function () {
    return delKb(kbIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 打开文档管理 */
function handleDocuments(row) {
  currentKb.value = row
  docQuery.value = { pageNum: 1, pageSize: 10, kbId: row.kbId }
  docOpen.value = true
  getDocList()
}

/** 查询文档列表 */
function getDocList() {
  docLoading.value = true
  listDocument(docQuery.value).then(response => {
    docList.value = response.rows
    docTotal.value = response.total
    docLoading.value = false
  })
}

/** 自定义上传（同步解析入库） */
function customUpload(option) {
  uploading.value = true
  uploadDocument(currentKb.value.kbId, option.file).then(response => {
    proxy.$modal.msgSuccess("上传成功，已入库 " + (response.data.chunkCount || 0) + " 个分块")
    getDocList()
    getList()
  }).catch(() => {
    getDocList()
  }).finally(() => {
    uploading.value = false
  })
}

/** 删除文档 */
function handleDeleteDoc(row) {
  proxy.$modal.confirm('是否确认删除文档"' + row.fileName + '"？删除后需重建索引以移除向量。').then(function () {
    return delDocument(row.docId)
  }).then(() => {
    getDocList()
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 重建索引 */
function handleRebuild() {
  proxy.$modal.confirm('重建索引将重新向量化"' + currentKb.value.kbName + '"的全部文档，耗时较长，是否继续？').then(function () {
    rebuilding.value = true
    return rebuildIndex(currentKb.value.kbId)
  }).then(() => {
    proxy.$modal.msgSuccess("重建完成")
    getDocList()
    getList()
  }).catch(() => {}).finally(() => {
    rebuilding.value = false
  })
}

/** 打开问答测试 */
function handleChat(row) {
  currentKb.value = row
  chatMessages.value = []
  chatQuestion.value = ""
  chatOpen.value = true
}

/** 发送问题（SSE 流式输出） */
function handleSend() {
  const question = chatQuestion.value.trim()
  if (!question || chatSending.value) return
  chatMessages.value.push({ role: "user", content: question })
  chatMessages.value.push({ role: "assistant", content: "", sources: [], costMs: 0 })
  const msg = chatMessages.value[chatMessages.value.length - 1]
  chatQuestion.value = ""
  chatSending.value = true
  scrollToBottom()
  chatKbStream({ kbId: currentKb.value.kbId, question: question }, {
    onSources: list => { msg.sources = list },
    onDelta: text => { msg.content += text; scrollToBottom() },
    onDone: costMs => {
      msg.costMs = costMs
      chatSending.value = false
      scrollToBottom()
    },
    onError: message => {
      if (!msg.content) {
        msg.content = message || "问答失败，请检查模型服务是否可用"
      }
      chatSending.value = false
      proxy.$modal.msgError(message || "问答失败")
      scrollToBottom()
    },
  }).catch(err => {
    if (!msg.content) {
      msg.content = (err && err.message) ? err.message : "问答失败，请检查模型服务是否可用"
    }
    chatSending.value = false
    scrollToBottom()
  })
}

/** 滚动到底部 */
function scrollToBottom() {
  nextTick(() => {
    if (chatBoxRef.value) {
      chatBoxRef.value.scrollTop = chatBoxRef.value.scrollHeight
    }
  })
}

getList()
checkHealth()
</script>

<style scoped lang="scss">
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100%;

  .chat-messages {
    flex: 1;
    overflow-y: auto;
    padding-right: 4px;

    .chat-msg {
      display: flex;
      margin-bottom: 14px;

      &.user {
        justify-content: flex-end;

        .msg-bubble {
          background-color: var(--el-color-primary);
          color: #fff;
        }
      }

      &.assistant {
        justify-content: flex-start;

        .msg-bubble {
          background-color: var(--el-fill-color-light);
          color: var(--el-text-color-primary);
        }
      }

      .msg-bubble {
        max-width: 85%;
        padding: 10px 14px;
        border-radius: 8px;

        .msg-text {
          font-size: 14px;
          line-height: 22px;
          white-space: pre-wrap;
          word-break: break-word;
        }

        .msg-sources {
          margin-top: 8px;
          border-top: none;

          .source-item {
            margin-bottom: 8px;

            .source-head {
              display: flex;
              align-items: center;
              gap: 8px;

              .source-score {
                font-size: 12px;
                color: var(--el-text-color-secondary);
              }
            }

            .source-snippet {
              margin-top: 4px;
              font-size: 12px;
              color: var(--el-text-color-regular);
              line-height: 18px;
            }
          }
        }

        .msg-cost {
          margin-top: 6px;
          font-size: 12px;
          color: var(--el-text-color-secondary);
        }
      }
    }
  }

  .chat-input {
    display: flex;
    gap: 8px;
    align-items: flex-end;
    padding-top: 10px;
    border-top: 1px solid var(--el-border-color-lighter);
  }
}
</style>
