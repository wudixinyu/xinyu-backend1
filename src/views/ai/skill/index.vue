<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="技能名称" prop="skillName">
        <el-input
          v-model="queryParams.skillName"
          placeholder="请输入技能名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="技能标识" prop="skillCode">
        <el-input
          v-model="queryParams.skillCode"
          placeholder="请输入技能标识"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="分类" prop="category">
        <el-input
          v-model="queryParams.category"
          placeholder="请输入技能分类"
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
          v-hasPermi="['ai:skill:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['ai:skill:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['ai:skill:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="skillList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="skillId" width="70" />
      <el-table-column label="技能名称" align="center" prop="skillName" min-width="140" show-overflow-tooltip />
      <el-table-column label="技能标识" align="center" prop="skillCode" min-width="140" show-overflow-tooltip />
      <el-table-column label="分类" align="center" prop="category" width="100" show-overflow-tooltip />
      <el-table-column label="技能说明" align="center" prop="description" min-width="180" show-overflow-tooltip />
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
      <el-table-column label="操作" align="center" width="180" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['ai:skill:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['ai:skill:remove']">删除</el-button>
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

    <!-- 添加或修改技能对话框 -->
    <el-dialog :title="title" v-model="open" width="680px" append-to-body>
      <el-form ref="skillRef" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="技能名称" prop="skillName">
              <el-input v-model="form.skillName" placeholder="请输入技能名称" maxlength="100" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="技能标识" prop="skillCode">
              <el-input v-model="form.skillCode" placeholder="请输入技能标识（唯一）" maxlength="100" show-word-limit />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="分类" prop="category">
              <el-input v-model="form.category" placeholder="请输入技能分类" maxlength="50" />
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
        <el-form-item label="技能说明" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="请输入技能说明" maxlength="500" show-word-limit />
        </el-form-item>
        <el-form-item label="技能内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="8"
            placeholder="请输入技能内容（提示词/脚本）"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
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

<script setup name="Skill">
import { listSkill, getSkill, delSkill, addSkill, updateSkill } from "@/api/ai/skill"

const { proxy } = getCurrentInstance()
const { sys_normal_disable } = proxy.useDict('sys_normal_disable')

const skillList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    skillName: undefined,
    skillCode: undefined,
    category: undefined,
    status: undefined,
  },
  rules: {
    skillName: [{ required: true, message: "技能名称不能为空", trigger: "blur" }],
    skillCode: [
      { required: true, message: "技能标识不能为空", trigger: "blur" },
      { pattern: /^[A-Za-z0-9_-]+$/, message: "技能标识只能包含字母、数字、下划线、中划线", trigger: "blur" }
    ],
  },
})

const { queryParams, form, rules } = toRefs(data)

/** 查询技能列表 */
function getList() {
  loading.value = true
  listSkill(queryParams.value).then(response => {
    skillList.value = response.rows
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
    skillId: undefined,
    skillName: undefined,
    skillCode: undefined,
    category: undefined,
    description: undefined,
    content: undefined,
    status: "0",
    remark: undefined
  }
  proxy.resetForm("skillRef")
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
  ids.value = selection.map(item => item.skillId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加技能"
}

/** 修改按钮操作（拉取详情含 content） */
function handleUpdate(row) {
  reset()
  const skillId = row.skillId || ids.value
  getSkill(skillId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改技能"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["skillRef"].validate(valid => {
    if (valid) {
      if (form.value.skillId != undefined) {
        updateSkill(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addSkill(form.value).then(response => {
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
  const skillIds = row.skillId || ids.value
  proxy.$modal.confirm('是否确认删除技能编号为"' + skillIds + '"的数据项？').then(function () {
    return delSkill(skillIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('ai/skill/export', {
    ...queryParams.value
  }, `ai_skill_${new Date().getTime()}.xlsx`)
}

getList()
</script>
