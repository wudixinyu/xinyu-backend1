<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="名称" prop="toolName">
        <el-input
          v-model="queryParams.toolName"
          placeholder="请输入工具名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="网站" prop="websiteUrl">
        <el-input
          v-model="queryParams.websiteUrl"
          placeholder="请输入网站地址"
          clearable
          style="width: 240px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="分类" prop="category">
        <el-select v-model="queryParams.category" placeholder="请选择分类" clearable style="width: 160px">
          <el-option
            v-for="dict in yg_tool_category"
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
          v-hasPermi="['tool:website:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['tool:website:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['tool:website:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button-group>
          <el-button :type="viewMode === 'table' ? 'primary' : ''" icon="Grid" title="表格视图" @click="viewMode = 'table'" />
          <el-button :type="viewMode === 'list' ? 'primary' : ''" icon="Menu" title="宫格视图" @click="viewMode = 'list'" />
        </el-button-group>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 表格视图 -->
    <el-table v-if="viewMode === 'table'" v-loading="loading" :data="websiteList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="toolId" width="80" />
      <el-table-column label="名称" align="center" prop="toolName" width="180" show-overflow-tooltip />
      <el-table-column label="网站" align="center" prop="websiteUrl" min-width="280" show-overflow-tooltip>
        <template #default="scope">
          <el-link type="primary" :underline="false" @click="handleOpen(scope.row)">{{ scope.row.websiteUrl }}</el-link>
        </template>
      </el-table-column>
      <el-table-column label="分类" align="center" prop="category" width="100">
        <template #default="scope">
          <dict-tag :options="yg_tool_category" :value="scope.row.category" />
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" min-width="160" show-overflow-tooltip />
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="230" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Position" @click="handleOpen(scope.row)" v-hasPermi="['tool:website:query']">打开网站</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['tool:website:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['tool:website:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 宫格视图 -->
    <div v-if="viewMode === 'list'" v-loading="loading" class="website-grid-view">
      <div class="website-card" v-for="item in websiteList" :key="item.toolId">
        <div class="card-head">
          <span class="item-name" :title="item.toolName">{{ item.toolName }}</span>
          <dict-tag :options="yg_tool_category" :value="item.category" />
        </div>
        <el-link class="item-url" type="primary" :underline="false" :title="item.websiteUrl" @click="handleOpen(item)">{{ item.websiteUrl }}</el-link>
        <div class="item-meta">
          <div class="meta-line">创建时间：{{ parseTime(item.createTime) }}</div>
          <div class="meta-line" v-if="item.remark" :title="item.remark">备注：{{ item.remark }}</div>
        </div>
        <div class="card-ops">
          <el-button type="primary" plain size="small" icon="Position" @click="handleOpen(item)" v-hasPermi="['tool:website:query']">打开网站</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(item)" v-hasPermi="['tool:website:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(item)" v-hasPermi="['tool:website:remove']">删除</el-button>
        </div>
      </div>
      <el-empty v-if="!loading && websiteList.length === 0" class="grid-empty" description="暂无数据" />
    </div>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改工具网站对话框 -->
    <el-dialog :title="title" v-model="open" width="620px" append-to-body>
      <el-form ref="websiteRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="toolName">
          <el-input v-model="form.toolName" placeholder="请输入工具名称" maxlength="100" />
        </el-form-item>
        <el-form-item label="网站" prop="websiteUrl">
          <el-input v-model="form.websiteUrl" placeholder="请输入网站地址，如 https://example.com" maxlength="500" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择分类" clearable style="width: 100%">
            <el-option
              v-for="dict in yg_tool_category"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
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
  </div>
</template>

<script setup name="ToolWebsite">
import { listWebsite, getWebsite, delWebsite, addWebsite, updateWebsite } from "@/api/tool/website"

const { proxy } = getCurrentInstance()
const { yg_tool_category } = useDict("yg_tool_category")

const websiteList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
// 视图模式（table表格 list列表）
const viewMode = ref("table")

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    toolName: undefined,
    websiteUrl: undefined,
    category: undefined,
  },
  rules: {
    toolName: [{ required: true, message: "工具名称不能为空", trigger: "blur" }],
    websiteUrl: [{ required: true, message: "网站地址不能为空", trigger: "blur" }],
  },
})

const { queryParams, form, rules } = toRefs(data)

/** 查询工具网站列表 */
function getList() {
  loading.value = true
  listWebsite(queryParams.value).then(response => {
    websiteList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

/** 在新窗口打开网站 */
function handleOpen(row) {
  let url = row.websiteUrl || ""
  if (url && !/^https?:\/\//i.test(url)) {
    url = "https://" + url
  }
  window.open(url, "_blank")
}

/** 取消按钮 */
function cancel() {
  open.value = false
  reset()
}

/** 表单重置 */
function reset() {
  form.value = {
    toolId: null,
    toolName: null,
    websiteUrl: null,
    category: null,
    remark: null,
  }
  proxy.resetForm("websiteRef")
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
  ids.value = selection.map(item => item.toolId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加工具网站"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const toolId = row.toolId || ids.value
  getWebsite(toolId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改工具网站"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["websiteRef"].validate(valid => {
    if (valid) {
      if (form.value.toolId != null) {
        updateWebsite(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addWebsite(form.value).then(response => {
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
  const toolIds = row.toolId || ids.value
  proxy.$modal.confirm('是否确认删除工具网站编号为"' + toolIds + '"的数据项？').then(function () {
    return delWebsite(toolIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

getList()
</script>

<style scoped lang="scss">
.website-grid-view {
  min-height: 200px;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;

  .grid-empty {
    grid-column: 1 / -1;
  }

  .website-card {
    display: flex;
    flex-direction: column;
    padding: 16px;
    border: 1px solid var(--el-border-color-lighter);
    border-radius: 8px;
    background-color: var(--el-bg-color);
    transition: box-shadow 0.2s, transform 0.2s;

    &:hover {
      box-shadow: var(--el-box-shadow-light);
      transform: translateY(-2px);
    }

    .card-head {
      display: flex;
      align-items: center;
      gap: 8px;

      .item-name {
        font-size: 15px;
        font-weight: 600;
        color: var(--el-text-color-primary);
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }

    .item-url {
      margin-top: 10px;
      font-size: 13px;
      justify-content: flex-start;
      width: 100%;

      :deep(span) {
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }

    .item-meta {
      margin-top: 10px;
      flex: 1;
      font-size: 12px;
      color: var(--el-text-color-secondary);

      .meta-line {
        line-height: 20px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }

    .card-ops {
      margin-top: 12px;
      padding-top: 12px;
      border-top: 1px solid var(--el-border-color-lighter);
      display: flex;
      align-items: center;
      gap: 4px;
    }
  }
}
</style>
