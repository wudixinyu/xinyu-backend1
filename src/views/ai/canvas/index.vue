<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="画布名称" prop="canvasName">
        <el-input
          v-model="queryParams.canvasName"
          placeholder="请输入画布名称"
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
          :loading="adding"
          @click="handleAdd"
          v-hasPermi="['ai:canvas:add']"
        >新建画布</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['ai:canvas:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['ai:canvas:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="canvasList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="canvasId" width="70" />
      <el-table-column label="缩略图" align="center" width="120">
        <template #default="scope">
          <el-image
            v-if="scope.row.thumbnail"
            :src="resolveUrl(scope.row.thumbnail)"
            :preview-src-list="[resolveUrl(scope.row.thumbnail)]"
            preview-teleported
            hide-on-click-modal
            style="width: 90px; height: 60px"
            fit="cover"
          >
            <template #error><span>无图</span></template>
          </el-image>
          <span v-else>空白</span>
        </template>
      </el-table-column>
      <el-table-column label="画布名称" align="center" prop="canvasName" min-width="160" show-overflow-tooltip />
      <el-table-column label="尺寸" align="center" width="120">
        <template #default="scope">{{ scope.row.canvasWidth }} × {{ scope.row.canvasHeight }}</template>
      </el-table-column>
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
      <el-table-column label="操作" align="center" width="220" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="goEditor(scope.row)" v-hasPermi="['ai:canvas:edit']">编辑</el-button>
          <el-button link type="primary" icon="Sort" @click="handleRename(scope.row)" v-hasPermi="['ai:canvas:edit']">改名</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['ai:canvas:remove']">删除</el-button>
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

    <!-- 改名对话框 -->
    <el-dialog title="画布改名" v-model="renameOpen" width="440px" append-to-body>
      <el-form ref="renameRef" :model="form" :rules="renameRules" label-width="80px">
        <el-form-item label="画布名称" prop="canvasName">
          <el-input v-model="form.canvasName" placeholder="请输入画布名称" maxlength="200" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitRename">确 定</el-button>
          <el-button @click="renameOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Canvas">
import { listCanvas, addCanvas, updateCanvas, delCanvas } from "@/api/ai/canvas"

const { proxy } = getCurrentInstance()
const router = useRouter()
const { sys_normal_disable } = proxy.useDict('sys_normal_disable')

const baseUrl = import.meta.env.VITE_APP_BASE_API

const canvasList = ref([])
const loading = ref(true)
const adding = ref(false)
const showSearch = ref(true)
const ids = ref([])
const multiple = ref(true)
const total = ref(0)

const renameOpen = ref(false)
const form = ref({})
const renameRules = {
  canvasName: [{ required: true, message: "画布名称不能为空", trigger: "blur" }]
}

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    canvasName: undefined,
    status: undefined,
  },
})
const { queryParams } = toRefs(data)

/** 拼接 /profile 相对路径为可访问地址 */
function resolveUrl(url) {
  if (!url) return ""
  return url.startsWith("http") ? url : baseUrl + url
}

/** 查询画布列表 */
function getList() {
  loading.value = true
  listCanvas(queryParams.value).then(response => {
    canvasList.value = response.rows
    total.value = response.total
    loading.value = false
  })
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

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.canvasId)
  multiple.value = !selection.length
}

/** 新建画布并进入编辑器 */
function handleAdd() {
  adding.value = true
  addCanvas({
    canvasName: "未命名画布 " + proxy.parseTime(new Date(), "{y}{m}{d} {h}{i}"),
    content: '{"nodes":[]}',
    canvasWidth: 900,
    canvasHeight: 600,
    status: "0"
  }).then(response => {
    adding.value = false
    const canvasId = response.data && response.data.canvasId
    if (canvasId) {
      router.push("/ai/canvas-edit/index/" + canvasId)
    } else {
      proxy.$modal.msgError("创建画布失败，未获取到主键")
      getList()
    }
  }).catch(() => {
    adding.value = false
  })
}

/** 进入编辑器 */
function goEditor(row) {
  router.push("/ai/canvas-edit/index/" + row.canvasId)
}

/** 改名 */
function handleRename(row) {
  form.value = { canvasId: row.canvasId, canvasName: row.canvasName }
  renameOpen.value = true
}

/** 提交改名 */
function submitRename() {
  proxy.$refs["renameRef"].validate(valid => {
    if (valid) {
      updateCanvas(form.value).then(() => {
        proxy.$modal.msgSuccess("修改成功")
        renameOpen.value = false
        getList()
      })
    }
  })
}

/** 删除按钮操作 */
function handleDelete(row) {
  const canvasIds = row.canvasId ? [row.canvasId] : ids.value
  proxy.$modal.confirm('是否确认删除选中的 ' + canvasIds.length + ' 个画布？').then(function () {
    return delCanvas(canvasIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('ai/canvas/export', {
    ...queryParams.value
  }, `ai_canvas_${new Date().getTime()}.xlsx`)
}

getList()
</script>
