<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="版本号" prop="version">
        <el-input
          v-model="queryParams.version"
          placeholder="请输入版本号"
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
          v-hasPermi="['update:log:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['update:log:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['update:log:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button-group>
          <el-button :type="viewMode === 'table' ? 'primary' : ''" icon="Grid" title="表格视图" @click="viewMode = 'table'" />
          <el-button :type="viewMode === 'list' ? 'primary' : ''" icon="List" title="列表视图" @click="viewMode = 'list'" />
        </el-button-group>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-if="viewMode === 'table'" v-loading="loading" :data="logList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="logId" width="80" />
      <el-table-column label="版本" align="center" prop="version" width="140" />
      <el-table-column label="更新内容" align="center" prop="content" min-width="320" show-overflow-tooltip>
        <template #default="scope">
          <span style="white-space: pre-line">{{ scope.row.content }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template #default="scope">
          <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="时间" align="center" prop="createTime" width="160">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="160" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['update:log:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['update:log:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 列表视图 -->
    <div v-if="viewMode === 'list'" v-loading="loading" class="log-list-view">
      <div class="log-item" v-for="item in logList" :key="item.logId">
        <div class="item-body">
          <div class="item-title-row">
            <span class="item-name">{{ item.version }}</span>
            <dict-tag :options="sys_normal_disable" :value="item.status" />
          </div>
          <el-tooltip :content="item.content" placement="top">
            <div class="item-content">{{ item.content }}</div>
          </el-tooltip>
          <div class="item-meta">
            <span>时间：{{ parseTime(item.createTime) }}</span>
            <span v-if="item.remark">备注：{{ item.remark }}</span>
          </div>
        </div>
        <div class="item-ops">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(item)" v-hasPermi="['update:log:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(item)" v-hasPermi="['update:log:remove']">删除</el-button>
        </div>
      </div>
      <el-empty v-if="!loading && logList.length === 0" description="暂无数据" />
    </div>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改更新日志对话框 -->
    <el-dialog :title="title" v-model="open" width="620px" append-to-body>
      <el-form ref="logRef" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="版本号" prop="version">
              <el-input v-model="form.version" placeholder="如 V1.2.0" maxlength="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio
                  v-for="dict in sys_normal_disable"
                  :key="dict.value"
                  :value="dict.value"
                >{{ dict.label }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="更新内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="6" placeholder="请输入更新内容，每行一条" />
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

<script setup name="UpdateLog">
import { listLog, getLog, delLog, addLog, updateLog } from "@/api/update/log"

const { proxy } = getCurrentInstance()
const { sys_normal_disable } = proxy.useDict('sys_normal_disable')

const logList = ref([])
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
    version: undefined,
    status: undefined,
  },
  rules: {
    version: [{ required: true, message: "版本号不能为空", trigger: "blur" }],
    content: [{ required: true, message: "更新内容不能为空", trigger: "blur" }],
  },
})

const { queryParams, form, rules } = toRefs(data)

/** 查询更新日志列表 */
function getList() {
  loading.value = true
  listLog(queryParams.value).then(response => {
    logList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

/** 取消按钮 */
function cancel() {
  open.value = false
  reset()
}

/** 表单重置 */
function reset() {
  form.value = {
    logId: null,
    version: null,
    content: null,
    status: "0",
    remark: null,
  }
  proxy.resetForm("logRef")
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
  ids.value = selection.map(item => item.logId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加更新日志"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const logId = row.logId || ids.value
  getLog(logId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改更新日志"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["logRef"].validate(valid => {
    if (valid) {
      if (form.value.logId != null) {
        updateLog(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addLog(form.value).then(response => {
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
  const logIds = row.logId || ids.value
  proxy.$modal.confirm('是否确认删除更新日志编号为"' + logIds + '"的数据项？').then(function () {
    return delLog(logIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

getList()
</script>

<style scoped lang="scss">
.log-list-view {
  min-height: 200px;

  .log-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16px 20px;
    margin-bottom: 12px;
    border: 1px solid var(--el-border-color-lighter);
    border-radius: 6px;
    background-color: var(--el-bg-color);
    transition: box-shadow 0.2s;

    &:hover {
      box-shadow: var(--el-box-shadow-light);
    }

    .item-body {
      flex: 1;
      min-width: 0;
      margin-right: 16px;

      .item-title-row {
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

      .item-content {
        margin-top: 8px;
        font-size: 13px;
        line-height: 20px;
        color: var(--el-text-color-regular);
        display: -webkit-box;
        -webkit-box-orient: vertical;
        -webkit-line-clamp: 2;
        overflow: hidden;
        word-break: break-all;
        white-space: pre-line;
      }

      .item-meta {
        margin-top: 8px;
        display: flex;
        gap: 24px;
        font-size: 12px;
        color: var(--el-text-color-secondary);
      }
    }

    .item-ops {
      flex-shrink: 0;
    }
  }
}
</style>
