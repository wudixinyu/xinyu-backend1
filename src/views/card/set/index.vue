<template>
   <div class="app-container">
      <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
         <el-form-item label="系列名称" prop="cnName">
            <el-input
               v-model="queryParams.cnName"
               placeholder="请输入系列名称"
               clearable
               style="width: 200px"
               @keyup.enter="handleQuery"
            />
         </el-form-item>
         <el-form-item label="状态" prop="status">
            <el-select v-model="queryParams.status" placeholder="系列状态" clearable style="width: 200px">
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
               v-hasPermi="['card:set:add']"
            >新增</el-button>
         </el-col>
         <el-col :span="1.5">
            <el-button
               type="success"
               plain
               icon="Edit"
               :disabled="single"
               @click="handleUpdate"
               v-hasPermi="['card:set:edit']"
            >修改</el-button>
         </el-col>
         <el-col :span="1.5">
            <el-button
               type="danger"
               plain
               icon="Delete"
               :disabled="multiple"
               @click="handleDelete"
               v-hasPermi="['card:set:remove']"
            >删除</el-button>
         </el-col>
         <el-col :span="1.5">
            <el-button
               type="warning"
               plain
               icon="Download"
               @click="handleExport"
               v-hasPermi="['card:set:export']"
            >导出</el-button>
         </el-col>
         <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
      </el-row>

      <el-table v-loading="loading" :data="setList" @selection-change="handleSelectionChange">
         <el-table-column type="selection" width="55" align="center" />
         <el-table-column label="系列code" align="center" prop="setCode" v-if="columns.setCode.visible" />
         <el-table-column label="系列code数值" align="center" prop="setValue" v-if="columns.setValue.visible" />
         <el-table-column label="中文名" align="center" prop="cnName" v-if="columns.cnName.visible" />
         <el-table-column label="日文名" align="center" prop="jpName" v-if="columns.jpName.visible" />
         <el-table-column label="英文名" align="center" prop="enName" v-if="columns.enName.visible" :show-overflow-tooltip="true" />
         <el-table-column label="状态" align="center" prop="status" v-if="columns.status.visible">
            <template #default="scope">
               <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
            </template>
         </el-table-column>
         <el-table-column label="创建时间" align="center" prop="createTime" v-if="columns.createTime.visible" width="180">
            <template #default="scope">
               <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
            </template>
         </el-table-column>
         <el-table-column label="操作" width="180" align="center" class-name="small-padding fixed-width">
            <template #default="scope">
               <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['card:set:edit']">修改</el-button>
               <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['card:set:remove']">删除</el-button>
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

      <!-- 添加或修改卡组系列对话框 -->
      <el-dialog :title="title" v-model="open" width="500px" append-to-body>
         <el-form ref="setRef" :model="form" :rules="rules" label-width="80px">
            <el-form-item label="系列code" prop="setCode">
               <el-input v-model="form.setCode" placeholder="请输入系列code(如0x57)" />
            </el-form-item>
            <el-form-item label="中文名" prop="cnName">
               <el-input v-model="form.cnName" placeholder="请输入系列中文名" />
            </el-form-item>
            <el-form-item label="日文名" prop="jpName">
               <el-input v-model="form.jpName" placeholder="请输入系列日文名" />
            </el-form-item>
            <el-form-item label="英文名" prop="enName">
               <el-input v-model="form.enName" placeholder="请输入系列英文名" />
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

<script setup name="Set">
import { listSet, addSet, delSet, getSet, updateSet } from "@/api/card/set"

const { proxy } = getCurrentInstance()
const { sys_normal_disable } = useDict("sys_normal_disable")

const setList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
// 列显隐信息
const columns = ref({
  setCode: { label: '系列code', visible: true },
  setValue: { label: '系列code数值', visible: false },
  cnName: { label: '中文名', visible: true },
  jpName: { label: '日文名', visible: true },
  enName: { label: '英文名', visible: true },
  status: { label: '状态', visible: true },
  createTime: { label: '创建时间', visible: true }
})

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    cnName: undefined,
    status: undefined
  },
  rules: {
    setCode: [{ required: true, message: "系列code不能为空", trigger: "blur" }],
    cnName: [{ required: true, message: "系列中文名不能为空", trigger: "blur" }],
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询卡组系列列表 */
function getList() {
  loading.value = true
  listSet(queryParams.value).then(response => {
    setList.value = response.rows
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
    setId: undefined,
    setCode: undefined,
    setValue: undefined,
    cnName: undefined,
    jpName: undefined,
    enName: undefined,
    status: "0",
    remark: undefined
  }
  proxy.resetForm("setRef")
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
  ids.value = selection.map(item => item.setId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加卡组系列"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const setId = row.setId || ids.value
  getSet(setId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改卡组系列"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["setRef"].validate(valid => {
    if (valid) {
      if (form.value.setId != undefined) {
        updateSet(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addSet(form.value).then(() => {
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
  const setIds = row.setId || ids.value
  proxy.$modal.confirm('是否确认删除卡组系列编号为"' + setIds + '"的数据项？').then(function() {
    return delSet(setIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download("card/set/export", {
    ...queryParams.value
  }, `set_${new Date().getTime()}.xlsx`)
}

getList()
</script>
