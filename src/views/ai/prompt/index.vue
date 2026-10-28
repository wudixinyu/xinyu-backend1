<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="提示词标题" prop="promptName">
        <el-input
          v-model="queryParams.promptName"
          placeholder="请输入提示词标题"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="生成类型" prop="promptType">
        <el-select v-model="queryParams.promptType" placeholder="请选择生成类型" clearable style="width: 200px">
          <el-option
            v-for="dict in yg_prompt_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
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
          v-hasPermi="['ai:prompt:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['ai:prompt:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['ai:prompt:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['ai:prompt:export']"
        >导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button-group>
          <el-button :type="viewMode === 'table' ? 'primary' : ''" icon="Grid" title="表格视图" @click="viewMode = 'table'" />
          <el-button :type="viewMode === 'list' ? 'primary' : ''" icon="List" title="列表视图" @click="viewMode = 'list'" />
        </el-button-group>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-if="viewMode === 'table'" v-loading="loading" :data="promptList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="promptId" width="80" />
      <el-table-column label="提示词标题" align="center" prop="promptName" min-width="160" show-overflow-tooltip />
      <el-table-column label="生成类型" align="center" prop="promptType" width="100">
        <template #default="scope">
          <dict-tag :options="yg_prompt_type" :value="scope.row.promptType" />
        </template>
      </el-table-column>
      <el-table-column label="适用模型" align="center" prop="modelName" width="140" show-overflow-tooltip />
      <el-table-column label="提示词内容" align="center" prop="promptContent" min-width="240" show-overflow-tooltip />
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
      <el-table-column label="操作" align="center" width="160" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['ai:prompt:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['ai:prompt:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 列表视图 -->
    <div v-if="viewMode === 'list'" v-loading="loading" class="prompt-list-view">
      <div class="prompt-item" v-for="item in promptList" :key="item.promptId">
        <div class="item-body">
          <div class="item-title-row">
            <span class="item-name">{{ item.promptName }}</span>
            <dict-tag :options="yg_prompt_type" :value="item.promptType" />
            <dict-tag :options="sys_normal_disable" :value="item.status" />
          </div>
          <el-tooltip :content="item.promptContent" placement="top">
            <div class="item-content">{{ item.promptContent }}</div>
          </el-tooltip>
          <div class="item-meta">
            <span v-if="item.modelName">适用模型：{{ item.modelName }}</span>
            <span>创建时间：{{ parseTime(item.createTime) }}</span>
          </div>
        </div>
        <div class="item-ops">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(item)" v-hasPermi="['ai:prompt:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(item)" v-hasPermi="['ai:prompt:remove']">删除</el-button>
        </div>
      </div>
      <el-empty v-if="!loading && promptList.length === 0" description="暂无数据" />
    </div>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改AI提示词对话框 -->
    <el-dialog :title="title" v-model="open" width="680px" append-to-body>
      <el-form ref="promptRef" :model="form" :rules="rules" label-width="90px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="提示词标题" prop="promptName">
              <el-input v-model="form.promptName" placeholder="请输入提示词标题" maxlength="200" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="生成类型" prop="promptType">
              <el-select v-model="form.promptType" placeholder="请选择生成类型" style="width: 100%">
                <el-option
                  v-for="dict in yg_prompt_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="parseInt(dict.value)"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="适用模型" prop="modelName">
              <el-input v-model="form.modelName" placeholder="如 ChatGPT / Midjourney / Suno" maxlength="100" />
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
        <el-form-item label="提示词内容" prop="promptContent">
          <el-input v-model="form.promptContent" type="textarea" :rows="6" placeholder="请输入提示词内容" />
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

<script setup name="Prompt">
import { listPrompt, getPrompt, delPrompt, addPrompt, updatePrompt } from "@/api/ai/prompt"

const { proxy } = getCurrentInstance()
const { yg_prompt_type, sys_normal_disable } = proxy.useDict('yg_prompt_type', 'sys_normal_disable')

const promptList = ref([])
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
    promptName: undefined,
    promptType: undefined,
    status: undefined,
  },
  rules: {
    promptName: [{ required: true, message: "提示词标题不能为空", trigger: "blur" }],
    promptType: [{ required: true, message: "生成类型不能为空", trigger: "change" }],
    promptContent: [{ required: true, message: "提示词内容不能为空", trigger: "blur" }],
  },
})

const { queryParams, form, rules } = toRefs(data)

/** 查询AI提示词列表 */
function getList() {
  loading.value = true
  listPrompt(queryParams.value).then(response => {
    promptList.value = response.rows
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
    promptId: null,
    promptName: null,
    promptType: null,
    promptContent: null,
    modelName: null,
    status: "0",
    remark: null,
  }
  proxy.resetForm("promptRef")
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
  ids.value = selection.map(item => item.promptId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加AI提示词"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const promptId = row.promptId || ids.value
  getPrompt(promptId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改AI提示词"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["promptRef"].validate(valid => {
    if (valid) {
      if (form.value.promptId != null) {
        updatePrompt(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addPrompt(form.value).then(response => {
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
  const promptIds = row.promptId || ids.value
  proxy.$modal.confirm('是否确认删除AI提示词编号为"' + promptIds + '"的数据项？').then(function () {
    return delPrompt(promptIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('ai/prompt/export', {
    ...queryParams.value
  }, `ai_prompt_${new Date().getTime()}.xlsx`)
}

getList()
</script>

<style scoped lang="scss">
.prompt-list-view {
  min-height: 200px;

  .prompt-item {
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
