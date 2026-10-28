<template>
  <div class="app-container canvas-board">
    <!-- 顶部工具条 -->
    <el-card shadow="never" class="toolbar-card">
      <div class="toolbar">
        <div class="toolbar-left">
          <el-button icon="Back" @click="goBack">返回</el-button>
          <el-input v-model="canvasName" placeholder="画布名称" style="width: 200px" maxlength="200" />
        </div>
        <div class="toolbar-center">
          <el-button type="primary" plain icon="Document" @click="addNode('text')" v-hasPermi="['ai:canvas:generate']">文本节点</el-button>
          <el-button type="success" plain icon="Picture" @click="addNode('image')" v-hasPermi="['ai:canvas:generate']">图片节点</el-button>
          <el-button type="warning" plain icon="VideoCamera" @click="addNode('video')" v-hasPermi="['ai:canvas:generate']">视频节点</el-button>
          <el-button icon="Delete" :disabled="!selectedId" @click="removeSelected">删除选中</el-button>
        </div>
        <div class="toolbar-right">
          <el-button-group>
            <el-button icon="ZoomOut" title="缩小" @click="zoomBy(0.8)" />
            <el-button style="width: 64px">{{ Math.round(viewport.zoom * 100) }}%</el-button>
            <el-button icon="ZoomIn" title="放大" @click="zoomBy(1.25)" />
          </el-button-group>
          <el-button icon="Aim" @click="resetViewport">复位</el-button>
          <el-button type="primary" icon="Select" :loading="saving" @click="save" v-hasPermi="['ai:canvas:edit']">保 存</el-button>
        </div>
      </div>
    </el-card>

    <!-- 无限画布区 -->
    <div
      ref="boardRef"
      class="board"
      :class="{ panning: panning }"
      @wheel.prevent="onWheel"
      @mousedown="onBoardMouseDown"
    >
      <div class="world" :style="worldStyle">
        <div
          v-for="node in nodes"
          :key="node.id"
          class="node-card"
          :class="['node-' + node.type, { selected: selectedId === node.id }]"
          :style="{ left: node.x + 'px', top: node.y + 'px', width: node.w + 'px' }"
          @mousedown.stop="selectNode(node)"
        >
          <div class="node-header" @mousedown.stop="startNodeDrag(node, $event)">
            <el-icon class="node-icon">
              <Document v-if="node.type === 'text'" />
              <Picture v-else-if="node.type === 'image'" />
              <VideoCamera v-else />
            </el-icon>
            <span class="node-title">{{ nodeTitle(node) }}</span>
            <el-tag size="small" :type="statusTagType(node.status)">{{ statusLabel(node.status) }}</el-tag>
          </div>
          <div class="node-body" @mousedown.stop>
            <el-input
              v-model="node.prompt"
              type="textarea"
              :rows="2"
              placeholder="输入提示词，例如：一只戴着宇航员头盔的橘猫，写实风格"
              resize="none"
            />
            <div class="node-controls">
              <el-select v-model="node.modelId" placeholder="选择模型" size="small" clearable style="flex: 1">
                <el-option
                  v-for="m in modelsByType(node.type)"
                  :key="m.modelId"
                  :label="m.modelName + (m.provider ? ' · ' + m.provider : '')"
                  :value="m.modelId"
                />
              </el-select>
              <el-button
                size="small"
                type="primary"
                :loading="node.status === 'queued' || node.status === 'running'"
                @click="generate(node)"
                v-hasPermi="['ai:canvas:generate']"
              >{{ node.status === 'success' ? '重新生成' : '生成' }}</el-button>
            </div>
            <!-- 结果区 -->
            <div class="node-result">
              <template v-if="node.status === 'queued' || node.status === 'running'">
                <el-icon class="is-loading" color="var(--el-color-primary)"><Loading /></el-icon>
                <span class="result-tip">{{ node.status === 'queued' ? '任务排队中…' : 'AI 生成中，请稍候…' }}</span>
              </template>
              <template v-else-if="node.status === 'failed'">
                <span class="result-error">{{ node.errorMsg || '生成失败' }}</span>
              </template>
              <template v-else-if="node.status === 'success'">
                <el-input
                  v-if="node.type === 'text'"
                  v-model="node.result"
                  type="textarea"
                  :rows="5"
                  resize="none"
                  placeholder="生成结果"
                />
                <el-image
                  v-else-if="node.type === 'image'"
                  :src="resolveUrl(node.url)"
                  :preview-src-list="[resolveUrl(node.url)]"
                  preview-teleported
                  hide-on-click-modal
                  fit="contain"
                  class="result-media"
                />
                <video
                  v-else
                  :src="resolveUrl(node.url)"
                  controls
                  class="result-media"
                />
              </template>
              <span v-else class="result-tip">输入提示词并选择模型后点击"生成"</span>
            </div>
          </div>
        </div>
        <div v-if="nodes.length === 0 && !pageLoading" class="board-empty">
          点击上方"文本/图片/视频节点"开始创作
        </div>
      </div>
    </div>

    <div class="footer-tip">
      提示：空白处拖拽平移画布，滚轮缩放；拖动节点标题栏移动位置；Delete 键删除选中节点；图片/视频生成结果自动存入素材库
    </div>
  </div>
</template>

<script setup name="CanvasEditor">
import { getCanvas, updateCanvas, generateText, generateImage, generateVideo, listGenerateTasks } from "@/api/ai/canvas"
import { listModel } from "@/api/ai/model"
import { Document, Picture, VideoCamera, Loading } from "@element-plus/icons-vue"

const { proxy } = getCurrentInstance()
const router = useRouter()
const route = useRoute()

const baseUrl = import.meta.env.VITE_APP_BASE_API
const canvasId = route.params.canvasId

const boardRef = ref(null)
const canvasName = ref("")
const pageLoading = ref(true)
const saving = ref(false)

const nodes = ref([])
const viewport = reactive({ x: 0, y: 0, zoom: 1 })
const selectedId = ref(null)
const models = ref([])

let nodeSeq = 0
let pollTimer = null

const worldStyle = computed(() => ({
  transform: `translate(${viewport.x}px, ${viewport.y}px) scale(${viewport.zoom})`
}))

function resolveUrl(url) {
  if (!url) return ""
  return url.startsWith("http") ? url : baseUrl + url
}

function nodeTitle(node) {
  return { text: "文本", image: "图片", video: "视频" }[node.type] || node.type
}

function statusLabel(status) {
  return { idle: "待生成", queued: "排队", running: "生成中", success: "成功", failed: "失败" }[status] || status
}

function statusTagType(status) {
  return { idle: "info", queued: "info", running: "primary", success: "success", failed: "danger" }[status] || "info"
}

/** 按节点类型筛选可用模型（1文本 2图片 3视频） */
function modelsByType(type) {
  const t = { text: 1, image: 2, video: 3 }[type]
  return models.value.filter(m => m.modelType === t)
}

// ---------------- 节点操作 ----------------

function addNode(type) {
  const board = boardRef.value
  const bw = board ? board.clientWidth : 800
  const bh = board ? board.clientHeight : 500
  // 添加到当前视口中心（世界坐标）
  const cx = (bw / 2 - viewport.x) / viewport.zoom
  const cy = (bh / 2 - viewport.y) / viewport.zoom
  const offset = (nodeSeq % 5) * 28
  nodes.value.push({
    id: "n" + Date.now() + "_" + (++nodeSeq),
    type,
    x: Math.round(cx - 160 + offset),
    y: Math.round(cy - 120 + offset),
    w: type === "video" ? 380 : 320,
    prompt: "",
    modelId: null,
    status: "idle",
    result: "",
    url: "",
    taskId: null,
    errorMsg: ""
  })
  selectedId.value = nodes.value[nodes.value.length - 1].id
}

function selectNode(node) {
  selectedId.value = node.id
}

function removeSelected() {
  if (!selectedId.value) return
  nodes.value = nodes.value.filter(n => n.id !== selectedId.value)
  selectedId.value = null
}

/** 发起生成 */
async function generate(node) {
  if (!node.modelId) {
    proxy.$modal.msgWarning("请先选择模型")
    return
  }
  if (!node.prompt || !node.prompt.trim()) {
    proxy.$modal.msgWarning("请输入提示词")
    return
  }
  const api = { text: generateText, image: generateImage, video: generateVideo }[node.type]
  try {
    const res = await api({ modelId: node.modelId, prompt: node.prompt, canvasId: Number(canvasId), nodeId: node.id })
    node.taskId = res.taskId
    node.status = "queued"
    node.errorMsg = ""
    startPolling()
  } catch (e) {
    node.status = "failed"
    node.errorMsg = e.message || "任务创建失败"
  }
}

/** 轮询任务状态 */
function startPolling() {
  if (pollTimer) return
  pollTimer = setInterval(pollTasks, 2500)
}

function stopPollingIfIdle() {
  const pending = nodes.value.some(n => (n.status === "queued" || n.status === "running") && n.taskId)
  if (!pending && pollTimer) {
    clearInterval(pollTimer)
    pollTimer = null
  }
}

async function pollTasks() {
  const pending = nodes.value.filter(n => (n.status === "queued" || n.status === "running") && n.taskId)
  if (pending.length === 0) {
    stopPollingIfIdle()
    return
  }
  try {
    const res = await listGenerateTasks(pending.map(n => n.taskId))
    const map = {}
    ;(res.data || []).forEach(t => { map[t.taskId] = t })
    pending.forEach(node => {
      const t = map[node.taskId]
      if (!t) return
      if (t.status === "2") {
        node.status = "success"
        if (node.type === "text") {
          node.result = t.resultText || ""
        } else {
          node.url = t.resultUrl || ""
        }
        stopPollingIfIdle()
      } else if (t.status === "3") {
        node.status = "failed"
        node.errorMsg = t.errorMsg || "生成失败"
        stopPollingIfIdle()
      } else {
        node.status = t.status === "1" ? "running" : "queued"
      }
    })
  } catch (e) {
    // 轮询失败静默重试
  }
}

// ---------------- 画布平移/缩放 ----------------

const panning = ref(false)
let panStart = null
let dragNode = null
let dragStart = null

function onWheel(e) {
  const board = boardRef.value
  const rect = board.getBoundingClientRect()
  const mx = e.clientX - rect.left
  const my = e.clientY - rect.top
  const factor = Math.exp(-e.deltaY * 0.0015)
  const newZoom = Math.min(2.5, Math.max(0.3, viewport.zoom * factor))
  const wx = (mx - viewport.x) / viewport.zoom
  const wy = (my - viewport.y) / viewport.zoom
  viewport.x = mx - wx * newZoom
  viewport.y = my - wy * newZoom
  viewport.zoom = newZoom
}

function zoomBy(factor) {
  const board = boardRef.value
  const bw = board.clientWidth / 2
  const bh = board.clientHeight / 2
  const newZoom = Math.min(2.5, Math.max(0.3, viewport.zoom * factor))
  const wx = (bw - viewport.x) / viewport.zoom
  const wy = (bh - viewport.y) / viewport.zoom
  viewport.x = bw - wx * newZoom
  viewport.y = bh - wy * newZoom
  viewport.zoom = newZoom
}

function resetViewport() {
  viewport.x = 0
  viewport.y = 0
  viewport.zoom = 1
}

function onBoardMouseDown(e) {
  if (e.button !== 0) return
  selectedId.value = null
  panning.value = true
  panStart = { x: e.clientX, y: e.clientY, vx: viewport.x, vy: viewport.y }
  window.addEventListener("mousemove", onPanMove)
  window.addEventListener("mouseup", onPanEnd)
}

function onPanMove(e) {
  if (!panStart) return
  viewport.x = panStart.vx + (e.clientX - panStart.x)
  viewport.y = panStart.vy + (e.clientY - panStart.y)
}

function onPanEnd() {
  panning.value = false
  panStart = null
  window.removeEventListener("mousemove", onPanMove)
  window.removeEventListener("mouseup", onPanEnd)
}

function startNodeDrag(node, e) {
  if (e.button !== 0) return
  selectNode(node)
  dragNode = node
  dragStart = { x: e.clientX, y: e.clientY, nx: node.x, ny: node.y }
  window.addEventListener("mousemove", onNodeDragMove)
  window.addEventListener("mouseup", onNodeDragEnd)
}

function onNodeDragMove(e) {
  if (!dragNode) return
  dragNode.x = Math.round(dragStart.nx + (e.clientX - dragStart.x) / viewport.zoom)
  dragNode.y = Math.round(dragStart.ny + (e.clientY - dragStart.y) / viewport.zoom)
}

function onNodeDragEnd() {
  dragNode = null
  dragStart = null
  window.removeEventListener("mousemove", onNodeDragMove)
  window.removeEventListener("mouseup", onNodeDragEnd)
}

/** Delete 键删除选中节点 */
function onKeydown(e) {
  const tag = (e.target && e.target.tagName) || ""
  if (["INPUT", "TEXTAREA"].includes(tag) || e.target.isContentEditable) return
  if (e.key === "Delete" && selectedId.value) {
    removeSelected()
  }
}

// ---------------- 保存/加载 ----------------

function save() {
  saving.value = true
  updateCanvas({
    canvasId: canvasId,
    canvasName: canvasName.value || "未命名画布",
    content: JSON.stringify({ nodes: nodes.value, viewport: { x: viewport.x, y: viewport.y, zoom: viewport.zoom } })
  }).then(() => {
    proxy.$modal.msgSuccess("保存成功")
  }).finally(() => {
    saving.value = false
  })
}

function goBack() {
  router.back()
}

onMounted(async () => {
  try {
    const res = await getCanvas(canvasId)
    const data = res.data
    if (!data) {
      proxy.$modal.msgError("画布不存在")
      router.back()
      return
    }
    canvasName.value = data.canvasName
    try {
      const parsed = JSON.parse(data.content || "{}")
      if (Array.isArray(parsed.nodes)) {
        nodes.value = parsed.nodes
        nodes.value.forEach(n => {
          if (n.status === "queued" || n.status === "running") n.status = "idle"
        })
      }
      if (parsed.viewport) {
        viewport.x = parsed.viewport.x || 0
        viewport.y = parsed.viewport.y || 0
        viewport.zoom = parsed.viewport.zoom || 1
      }
    } catch (e) {
      nodes.value = []
    }
    listModel({ status: "0", pageNum: 1, pageSize: 100 }).then(r => {
      models.value = r.rows || []
    })
  } finally {
    pageLoading.value = false
  }
  document.addEventListener("keydown", onKeydown)
})

onBeforeUnmount(() => {
  document.removeEventListener("keydown", onKeydown)
  if (pollTimer) clearInterval(pollTimer)
  window.removeEventListener("mousemove", onPanMove)
  window.removeEventListener("mouseup", onPanEnd)
  window.removeEventListener("mousemove", onNodeDragMove)
  window.removeEventListener("mouseup", onNodeDragEnd)
})
</script>

<style scoped lang="scss">
.canvas-board {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 84px);

  .toolbar-card {
    flex-shrink: 0;
    margin-bottom: 10px;

    :deep(.el-card__body) {
      padding: 10px 16px;
    }
  }

  .toolbar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    flex-wrap: wrap;
    gap: 8px;

    .toolbar-left, .toolbar-center, .toolbar-right {
      display: flex;
      align-items: center;
      gap: 8px;
    }
  }

  .board {
    flex: 1;
    position: relative;
    overflow: hidden;
    border-radius: 6px;
    background-color: var(--el-fill-color-light);
    background-image: radial-gradient(var(--el-border-darker) 1px, transparent 1px);
    background-size: 24px 24px;
    cursor: grab;

    &.panning {
      cursor: grabbing;
    }
  }

  .world {
    position: absolute;
    left: 0;
    top: 0;
    transform-origin: 0 0;
    width: 0;
    height: 0;
  }

  .board-empty {
    position: absolute;
    left: 50%;
    top: 40%;
    transform: translateX(-50%);
    color: var(--el-text-color-secondary);
    font-size: 14px;
    white-space: nowrap;
  }

  .node-card {
    position: absolute;
    background: var(--el-bg-color);
    border: 1px solid var(--el-border-color-lighter);
    border-radius: 8px;
    box-shadow: var(--el-box-shadow-light);
    overflow: hidden;

    &.selected {
      border-color: var(--el-color-primary);
      box-shadow: 0 0 0 2px var(--el-color-primary-light-7);
    }

    .node-header {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 8px 12px;
      background: var(--el-fill-color-blank);
      border-bottom: 1px solid var(--el-border-color-lighter);
      cursor: move;
      user-select: none;

      .node-icon {
        color: var(--el-color-primary);
      }

      .node-title {
        flex: 1;
        font-size: 13px;
        font-weight: 600;
      }
    }

    .node-body {
      padding: 10px 12px;
      cursor: default;

      .node-controls {
        display: flex;
        gap: 8px;
        margin-top: 8px;
      }

      .node-result {
        margin-top: 10px;
        min-height: 32px;
        display: flex;
        align-items: flex-start;
        gap: 6px;

        .result-tip {
          font-size: 12px;
          color: var(--el-text-color-secondary);
          line-height: 32px;
        }

        .result-error {
          font-size: 12px;
          color: var(--el-color-danger);
          word-break: break-all;
        }

        .result-media {
          width: 100%;
          max-height: 260px;
          border-radius: 4px;
          background: var(--el-fill-color-light);
        }
      }
    }
  }

  .footer-tip {
    flex-shrink: 0;
    padding-top: 8px;
    font-size: 12px;
    color: var(--el-text-color-secondary);
  }
}
</style>
