<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="素材名称" prop="mediaName">
        <el-input
          v-model="queryParams.mediaName"
          placeholder="请输入素材名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="类型" prop="mediaType">
        <el-select v-model="queryParams.mediaType" placeholder="请选择类型" clearable style="width: 200px">
          <el-option
            v-for="dict in yg_media_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="分类" prop="category">
        <el-input
          v-model="queryParams.category"
          placeholder="请输入分类/标签"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
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
          icon="Upload"
          @click="openUpload"
          v-hasPermi="['ai:media:upload']"
        >上传素材</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['ai:media:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button-group>
          <el-button :type="viewMode === 'grid' ? 'primary' : ''" icon="Grid" title="网格视图" @click="viewMode = 'grid'" />
          <el-button :type="viewMode === 'table' ? 'primary' : ''" icon="List" title="表格视图" @click="viewMode = 'table'" />
        </el-button-group>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 网格视图 -->
    <div v-if="viewMode === 'grid'" v-loading="loading" class="media-grid">
      <el-card v-for="item in mediaList" :key="item.mediaId" class="media-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <el-checkbox
              :model-value="ids.includes(item.mediaId)"
              @change="checked => toggleCheck(checked, item)"
            />
            <span class="card-name" :title="item.mediaName">{{ item.mediaName }}</span>
            <dict-tag :options="yg_media_type" :value="item.mediaType" />
          </div>
        </template>
        <div class="media-preview" @click="handlePreview(item)">
          <el-image
            v-if="item.mediaType === 1"
            :src="resolveUrl(item.fileUrl)"
            :preview-src-list="[resolveUrl(item.fileUrl)]"
            preview-teleported
            hide-on-click-modal
            fit="cover"
            class="preview-img"
          />
          <video v-else :src="resolveUrl(item.fileUrl)" preload="metadata" class="preview-img" />
          <div class="preview-mask"><el-icon size="24"><ZoomIn /></el-icon></div>
        </div>
        <div class="card-meta">
          <span>{{ formatSize(item.fileSize) }}</span>
          <span>{{ item.ext }}</span>
        </div>
        <div class="card-ops">
          <el-button link type="primary" icon="View" @click="handlePreview(item)">预览</el-button>
          <el-button link type="primary" icon="Edit" @click="handleRename(item)" v-hasPermi="['ai:media:edit']">重命名</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(item)" v-hasPermi="['ai:media:remove']">删除</el-button>
        </div>
      </el-card>
      <el-empty v-if="!loading && mediaList.length === 0" description="暂无素材" style="grid-column: 1 / -1" />
    </div>

    <!-- 表格视图 -->
    <el-table v-if="viewMode === 'table'" v-loading="loading" :data="mediaList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="mediaId" width="70" />
      <el-table-column label="预览" align="center" width="90">
        <template #default="scope">
          <el-image
            v-if="scope.row.mediaType === 1"
            :src="resolveUrl(scope.row.fileUrl)"
            :preview-src-list="[resolveUrl(scope.row.fileUrl)]"
            preview-teleported
            hide-on-click-modal
            style="width: 48px; height: 48px"
            fit="cover"
          />
          <el-icon v-else size="32" color="var(--el-color-warning)" @click="handlePreview(scope.row)" style="cursor: pointer"><VideoCamera /></el-icon>
        </template>
      </el-table-column>
      <el-table-column label="素材名称" align="center" prop="mediaName" min-width="180" show-overflow-tooltip />
      <el-table-column label="类型" align="center" prop="mediaType" width="80">
        <template #default="scope">
          <dict-tag :options="yg_media_type" :value="scope.row.mediaType" />
        </template>
      </el-table-column>
      <el-table-column label="大小" align="center" width="100">
        <template #default="scope">{{ formatSize(scope.row.fileSize) }}</template>
      </el-table-column>
      <el-table-column label="扩展名" align="center" prop="ext" width="80" />
      <el-table-column label="分类" align="center" prop="category" width="120" show-overflow-tooltip />
      <el-table-column label="上传时间" align="center" prop="createTime" width="160">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handlePreview(scope.row)">预览</el-button>
          <el-button link type="primary" icon="Edit" @click="handleRename(scope.row)" v-hasPermi="['ai:media:edit']">重命名</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['ai:media:remove']">删除</el-button>
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

    <!-- 上传素材对话框 -->
    <el-dialog title="上传素材" v-model="uploadOpen" width="560px" append-to-body>
      <el-form label-width="68px">
        <el-form-item label="分类">
          <el-input v-model="uploadCategory" placeholder="选填，如：海报/头像/宣传片" maxlength="100" />
        </el-form-item>
        <el-form-item label="文件">
          <el-upload
            ref="uploadRef"
            drag
            multiple
            :action="uploadUrl"
            :headers="uploadHeaders"
            :data="{ category: uploadCategory }"
            :before-upload="beforeUpload"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :show-file-list="true"
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
            <template #tip>
              <div class="el-upload__tip">
                支持 bmp/gif/jpg/jpeg/png 图片与 mp4/avi/rmvb 视频，单个文件不超过 {{ MAX_SIZE_MB }}MB
              </div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="uploadOpen = false">关 闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 预览对话框（视频） -->
    <el-dialog :title="previewTitle" v-model="previewOpen" width="720px" append-to-body destroy-on-close>
      <video :src="previewUrl" controls autoplay style="width: 100%; max-height: 65vh; background: #000" />
    </el-dialog>

    <!-- 重命名对话框 -->
    <el-dialog title="编辑素材" v-model="renameOpen" width="480px" append-to-body>
      <el-form ref="renameRef" :model="form" :rules="renameRules" label-width="80px">
        <el-form-item label="素材名称" prop="mediaName">
          <el-input v-model="form.mediaName" placeholder="请输入素材名称" maxlength="255" show-word-limit />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-input v-model="form.category" placeholder="请输入分类/标签" maxlength="100" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
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

<script setup name="Media">
import { listMedia, updateMedia, delMedia } from "@/api/ai/media"
import { getToken } from "@/utils/auth"
import { UploadFilled } from "@element-plus/icons-vue"

const { proxy } = getCurrentInstance()
const { yg_media_type } = proxy.useDict('yg_media_type')

const baseUrl = import.meta.env.VITE_APP_BASE_API
const MAX_SIZE_MB = 10
const ALLOWED_EXT = ["bmp", "gif", "jpg", "jpeg", "png", "mp4", "avi", "rmvb"]

const mediaList = ref([])
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const multiple = ref(true)
const total = ref(0)
const viewMode = ref("grid")

const uploadOpen = ref(false)
const uploadCategory = ref("")
const uploadUrl = baseUrl + "/ai/media/upload"
const uploadHeaders = computed(() => ({ Authorization: "Bearer " + getToken() }))

const previewOpen = ref(false)
const previewUrl = ref("")
const previewTitle = ref("")

const renameOpen = ref(false)
const form = ref({})
const renameRules = {
  mediaName: [{ required: true, message: "素材名称不能为空", trigger: "blur" }]
}

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 12,
    mediaName: undefined,
    mediaType: undefined,
    category: undefined,
  },
})
const { queryParams } = toRefs(data)

/** 拼接 /profile 相对路径为可访问地址 */
function resolveUrl(url) {
  if (!url) return ""
  return url.startsWith("http") ? url : baseUrl + url
}

/** 文件大小格式化 */
function formatSize(size) {
  if (size == null) return "-"
  if (size < 1024) return size + " B"
  if (size < 1024 * 1024) return (size / 1024).toFixed(1) + " KB"
  return (size / 1024 / 1024).toFixed(2) + " MB"
}

/** 查询素材列表 */
function getList() {
  loading.value = true
  listMedia(queryParams.value).then(response => {
    mediaList.value = response.rows
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

/** 表格多选 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.mediaId)
  multiple.value = !selection.length
}

/** 网格视图勾选 */
function toggleCheck(checked, item) {
  if (checked) {
    if (!ids.value.includes(item.mediaId)) ids.value.push(item.mediaId)
  } else {
    ids.value = ids.value.filter(id => id !== item.mediaId)
  }
  multiple.value = ids.value.length === 0
}

/** 打开上传对话框 */
function openUpload() {
  uploadCategory.value = ""
  uploadOpen.value = true
}

/** 上传前校验 */
function beforeUpload(file) {
  const ext = file.name.split(".").pop().toLowerCase()
  if (!ALLOWED_EXT.includes(ext)) {
    proxy.$modal.msgError(`不支持的文件类型：${ext}，仅支持 ${ALLOWED_EXT.join("/")}`)
    return false
  }
  if (file.size > MAX_SIZE_MB * 1024 * 1024) {
    proxy.$modal.msgError(`文件大小不能超过 ${MAX_SIZE_MB}MB`)
    return false
  }
  return true
}

/** 上传成功回调 */
let uploadCount = 0
function handleUploadSuccess(response, file) {
  if (response.code === 200) {
    uploadCount++
    proxy.$modal.msgSuccess(`${file.name} 上传成功`)
    getList()
  } else {
    proxy.$modal.msgError(response.msg || "上传失败")
  }
}

/** 上传失败回调 */
function handleUploadError() {
  proxy.$modal.msgError("上传失败，请检查网络或文件大小")
}

/** 预览 */
function handlePreview(item) {
  if (item.mediaType === 2) {
    previewUrl.value = resolveUrl(item.fileUrl)
    previewTitle.value = item.mediaName
    previewOpen.value = true
  }
  // 图片预览由 el-image 的 preview-src-list 处理
}

/** 重命名 */
function handleRename(item) {
  form.value = { mediaId: item.mediaId, mediaName: item.mediaName, category: item.category, remark: item.remark }
  renameOpen.value = true
}

/** 提交重命名 */
function submitRename() {
  proxy.$refs["renameRef"].validate(valid => {
    if (valid) {
      updateMedia(form.value).then(() => {
        proxy.$modal.msgSuccess("修改成功")
        renameOpen.value = false
        getList()
      })
    }
  })
}

/** 删除按钮操作 */
function handleDelete(row) {
  const mediaIds = row.mediaId ? [row.mediaId] : ids.value
  proxy.$modal.confirm('是否确认删除选中的 ' + mediaIds.length + ' 个素材？（仅删除记录，不删除服务器文件）').then(function () {
    return delMedia(mediaIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

getList()
</script>

<style scoped lang="scss">
.media-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 16px;
  min-height: 200px;

  .media-card {
    .card-header {
      display: flex;
      align-items: center;
      gap: 8px;

      .card-name {
        flex: 1;
        min-width: 0;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        font-size: 13px;
      }
    }

    .media-preview {
      position: relative;
      height: 140px;
      cursor: pointer;
      border-radius: 4px;
      overflow: hidden;

      .preview-img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        background: var(--el-fill-color-light);
      }

      .preview-mask {
        position: absolute;
        inset: 0;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #fff;
        background: rgba(0, 0, 0, 0.35);
        opacity: 0;
        transition: opacity 0.2s;
        pointer-events: none;
      }

      &:hover .preview-mask {
        opacity: 1;
      }
    }

    .card-meta {
      display: flex;
      justify-content: space-between;
      margin-top: 8px;
      font-size: 12px;
      color: var(--el-text-color-secondary);
    }

    .card-ops {
      margin-top: 4px;
      display: flex;
      justify-content: space-between;
    }
  }
}
</style>
