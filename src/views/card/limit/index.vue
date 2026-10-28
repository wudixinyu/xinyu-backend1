<template>
   <div class="app-container">
      <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
         <el-form-item label="卡片密码" prop="cardId">
            <el-input
               v-model="queryParams.cardId"
               placeholder="请输入卡片密码"
               clearable
               style="width: 200px"
               @keyup.enter="handleQuery"
            />
         </el-form-item>
         <el-form-item label="适用环境" prop="otType">
            <el-select v-model="queryParams.otType" placeholder="请选择适用环境" clearable style="width: 200px">
               <el-option label="OCG" value="ocg" />
               <el-option label="TCG" value="tcg" />
               <el-option label="SC" value="sc" />
            </el-select>
         </el-form-item>
         <el-form-item label="限制类型" prop="limitType">
            <el-select v-model="queryParams.limitType" placeholder="请选择限制类型" clearable style="width: 200px">
               <el-option
                  v-for="dict in yg_limit_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
               />
            </el-select>
         </el-form-item>
         <el-form-item label="生效日期" style="width: 308px">
            <el-date-picker
               v-model="dateRange"
               value-format="YYYY-MM-DD"
               type="daterange"
               range-separator="-"
               start-placeholder="开始日期"
               end-placeholder="结束日期"
            ></el-date-picker>
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
               v-hasPermi="['card:limit:add']"
            >新增</el-button>
         </el-col>
         <el-col :span="1.5">
            <el-button
               type="success"
               plain
               icon="Edit"
               :disabled="single"
               @click="handleUpdate"
               v-hasPermi="['card:limit:edit']"
            >修改</el-button>
         </el-col>
         <el-col :span="1.5">
            <el-button
               type="danger"
               plain
               icon="Delete"
               :disabled="multiple"
               @click="handleDelete"
               v-hasPermi="['card:limit:remove']"
            >删除</el-button>
         </el-col>
         <el-col :span="1.5">
            <el-button
               type="warning"
               plain
               icon="Download"
               @click="handleExport"
               v-hasPermi="['card:limit:export']"
            >导出</el-button>
         </el-col>
         <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <el-table v-loading="loading" :data="limitList" @selection-change="handleSelectionChange">
         <el-table-column type="selection" width="55" align="center" />
         <el-table-column label="主键" align="center" prop="limitId" />
         <el-table-column label="卡片图片" align="center" prop="imageUrl" width="80">
            <template #default="scope">
               <el-image
                  v-if="scope.row.imageUrl"
                  :src="scope.row.imageUrl + '!half'"
                  :preview-src-list="[scope.row.imageUrl]"
                  preview-teleported
                  hide-on-click-modal
                  style="width: 50px; height: 73px"
                  fit="cover"
               >
                  <template #error>
                     <span>无图</span>
                  </template>
               </el-image>
               <span v-else>无图</span>
            </template>
         </el-table-column>
         <el-table-column label="卡片密码" align="center" prop="cardId" />
         <el-table-column label="适用环境" align="center" prop="otType" />
         <el-table-column label="限制类型" align="center" prop="limitType">
            <template #default="scope">
               <dict-tag :options="yg_limit_type" :value="scope.row.limitType" />
            </template>
         </el-table-column>
         <el-table-column label="生效日期" align="center" prop="effectiveDate" width="180">
            <template #default="scope">
               <span>{{ parseTime(scope.row.effectiveDate, '{y}-{m}-{d}') }}</span>
            </template>
         </el-table-column>
         <el-table-column label="是否当前" align="center" prop="isCurrent">
            <template #default="scope">
               <el-tag v-if="scope.row.isCurrent === '1'" type="success">当前</el-tag>
               <el-tag v-else type="info">历史</el-tag>
            </template>
         </el-table-column>
         <el-table-column label="创建时间" align="center" prop="createTime" width="180">
            <template #default="scope">
               <span>{{ parseTime(scope.row.createTime) }}</span>
            </template>
         </el-table-column>
         <el-table-column label="操作" width="180" align="center" class-name="small-padding fixed-width">
            <template #default="scope">
               <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['card:limit:edit']">修改</el-button>
               <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['card:limit:remove']">删除</el-button>
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

      <!-- 添加或修改禁限卡对话框 -->
      <el-dialog :title="title" v-model="open" width="500px" append-to-body>
         <el-form ref="limitRef" :model="form" :rules="rules" label-width="80px">
            <el-form-item label="卡片密码" prop="cardId">
               <el-input v-model="form.cardId" placeholder="请输入卡片密码" />
            </el-form-item>
            <el-form-item label="适用环境" prop="otType">
               <el-select v-model="form.otType" placeholder="请选择适用环境">
                  <el-option label="OCG" value="ocg" />
                  <el-option label="TCG" value="tcg" />
                  <el-option label="SC" value="sc" />
               </el-select>
            </el-form-item>
            <el-form-item label="限制类型" prop="limitType">
               <el-select v-model="form.limitType" placeholder="请选择限制类型">
                  <el-option
                     v-for="dict in yg_limit_type"
                     :key="dict.value"
                     :label="dict.label"
                     :value="dict.value"
                  />
               </el-select>
            </el-form-item>
            <el-form-item label="生效日期" prop="effectiveDate">
               <el-date-picker
                  v-model="form.effectiveDate"
                  type="date"
                  value-format="YYYY-MM-DD"
                  placeholder="请选择生效日期"
                  style="width: 100%"
               ></el-date-picker>
            </el-form-item>
            <el-form-item label="是否当前" prop="isCurrent">
               <el-radio-group v-model="form.isCurrent">
                  <el-radio value="1">当前</el-radio>
                  <el-radio value="0">历史</el-radio>
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

<script setup name="Limit">
import { listLimit, getLimit, delLimit, addLimit, updateLimit } from "@/api/card/limit"

const { proxy } = getCurrentInstance()
const { yg_limit_type } = useDict("yg_limit_type")

const limitList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
const dateRange = ref([])

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    cardId: undefined,
    otType: undefined,
    limitType: undefined
  },
  rules: {
    cardId: [{ required: true, message: "卡片密码不能为空", trigger: "blur" }],
    otType: [{ required: true, message: "适用环境不能为空", trigger: "change" }],
    limitType: [{ required: true, message: "限制类型不能为空", trigger: "change" }],
    effectiveDate: [{ required: true, message: "生效日期不能为空", trigger: "change" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询禁限卡列表 */
function getList() {
  loading.value = true
  listLimit(proxy.addDateRange(queryParams.value, dateRange.value, 'EffectiveDate')).then(response => {
    limitList.value = response.rows
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
    limitId: undefined,
    cardId: undefined,
    otType: undefined,
    limitType: undefined,
    effectiveDate: undefined,
    isCurrent: "1",
    remark: undefined
  }
  proxy.resetForm("limitRef")
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = []
  proxy.resetForm("queryRef")
  handleQuery()
}

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.limitId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加禁限卡"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const limitId = row.limitId || ids.value
  getLimit(limitId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改禁限卡"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["limitRef"].validate(valid => {
    if (valid) {
      if (form.value.limitId != undefined) {
        updateLimit(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addLimit(form.value).then(() => {
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
  const limitIds = row.limitId || ids.value
  proxy.$modal.confirm('是否确认删除禁限卡编号为"' + limitIds + '"的数据项？').then(function() {
    return delLimit(limitIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download("card/limit/export", {
    ...queryParams.value
  }, `limit_${new Date().getTime()}.xlsx`)
}

getList()
</script>
