<template>
   <div class="app-container">
      <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
         <el-form-item label="卡片名称" prop="cnName">
            <el-input
               v-model="queryParams.cnName"
               placeholder="请输入卡片名称"
               clearable
               style="width: 200px"
               @keyup.enter="handleQuery"
            />
         </el-form-item>
         <el-form-item label="卡片大类" prop="cardKind">
            <el-select v-model="queryParams.cardKind" placeholder="卡片大类" clearable style="width: 200px">
               <el-option
                  v-for="dict in yg_card_kind"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
               />
            </el-select>
         </el-form-item>
         <el-form-item label="状态" prop="status">
            <el-select v-model="queryParams.status" placeholder="卡片状态" clearable style="width: 200px">
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
               v-hasPermi="['card:info:add']"
            >新增</el-button>
         </el-col>
         <el-col :span="1.5">
            <el-button
               type="success"
               plain
               icon="Edit"
               :disabled="single"
               @click="handleUpdate"
               v-hasPermi="['card:info:edit']"
            >修改</el-button>
         </el-col>
         <el-col :span="1.5">
            <el-button
               type="danger"
               plain
               icon="Delete"
               :disabled="multiple"
               @click="handleDelete"
               v-hasPermi="['card:info:remove']"
            >删除</el-button>
         </el-col>
         <el-col :span="1.5">
            <el-button
               type="warning"
               plain
               icon="Download"
               @click="handleExport"
               v-hasPermi="['card:info:export']"
            >导出</el-button>
         </el-col>
         <el-col :span="1.5">
            <el-button
               type="info"
               plain
               icon="Refresh"
               :loading="syncLoading"
               @click="handleSyncAll(false)"
               v-hasPermi="['card:info:sync']"
            >全量同步</el-button>
         </el-col>
         <el-col :span="1.5">
            <el-button
               type="info"
               plain
               icon="Upload"
               :loading="syncLoading"
               @click="handleSyncAll(true)"
               v-hasPermi="['card:info:sync']"
            >强制同步</el-button>
         </el-col>
         <el-col :span="1.5">
            <el-button
               type="info"
               plain
               icon="Plus"
               @click="handleSyncSingle"
               v-hasPermi="['card:info:sync']"
            >补录单卡</el-button>
         </el-col>
         <el-col :span="1.5">
            <el-button
               type="info"
               plain
               icon="Collection"
               :loading="syncLoading"
               @click="handleSyncSets"
               v-hasPermi="['card:set:sync']"
            >同步系列</el-button>
         </el-col>
         <el-col :span="1.5">
            <el-button
               type="info"
               plain
               icon="CircleClose"
               :loading="syncLoading"
               @click="handleSyncLimits"
               v-hasPermi="['card:limit:sync']"
            >同步禁限表</el-button>
         </el-col>
         <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <el-table v-loading="loading" :data="infoList" @selection-change="handleSelectionChange">
         <el-table-column type="selection" width="55" align="center" />
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
         <el-table-column label="中文名" align="center" prop="cnName" />
         <el-table-column label="类型描述" align="center" prop="typesDesc" :show-overflow-tooltip="true" />
         <el-table-column label="大类" align="center" prop="cardKind">
            <template #default="scope">
               <dict-tag :options="yg_card_kind" :value="scope.row.cardKind" />
            </template>
         </el-table-column>
         <el-table-column label="攻/防" align="center" width="100">
            <template #default="scope">
               <span>{{ scope.row.atk === -2 ? "?" : scope.row.atk }}/{{ scope.row.def === -2 ? "?" : scope.row.def }}</span>
            </template>
         </el-table-column>
         <el-table-column label="等级" align="center" prop="cardLevel" />
         <el-table-column label="状态" align="center" prop="status">
            <template #default="scope">
               <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
            </template>
         </el-table-column>
         <el-table-column label="同步时间" align="center" prop="syncTime" width="180">
            <template #default="scope">
               <span>{{ parseTime(scope.row.syncTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
            </template>
         </el-table-column>
         <el-table-column label="操作" width="240" align="center" class-name="small-padding fixed-width">
            <template #default="scope">
               <el-button
                  link
                  :type="collectedIds.includes(scope.row.cardId) ? 'warning' : 'primary'"
                  :icon="collectedIds.includes(scope.row.cardId) ? 'StarFilled' : 'Star'"
                  @click="handleToggleCollect(scope.row)"
               >{{ collectedIds.includes(scope.row.cardId) ? "已收藏" : "收藏" }}</el-button>
               <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['card:info:edit']">修改</el-button>
               <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['card:info:remove']">删除</el-button>
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

      <!-- 添加或修改卡片信息对话框 -->
      <el-dialog :title="title" v-model="open" width="750px" append-to-body>
         <el-form ref="infoRef" :model="form" :rules="rules" label-width="100px">
            <el-row>
               <el-col :span="12">
                  <el-form-item label="卡片密码" prop="cardId">
                     <el-input v-model="form.cardId" placeholder="请输入卡片密码" :disabled="isEdit" />
                  </el-form-item>
               </el-col>
               <el-col :span="12">
                  <el-form-item label="中文名" prop="cnName">
                     <el-input v-model="form.cnName" placeholder="请输入中文名称" />
                  </el-form-item>
               </el-col>
            </el-row>
            <el-row>
               <el-col :span="12">
                  <el-form-item label="简中名称" prop="scName">
                     <el-input v-model="form.scName" placeholder="请输入官方简体中文名称" />
                  </el-form-item>
               </el-col>
               <el-col :span="12">
                  <el-form-item label="MD名称" prop="mdName">
                     <el-input v-model="form.mdName" placeholder="请输入Master Duel中文名称" />
                  </el-form-item>
               </el-col>
            </el-row>
            <el-row>
               <el-col :span="12">
                  <el-form-item label="日文名称" prop="jpName">
                     <el-input v-model="form.jpName" placeholder="请输入官方日文名称" />
                  </el-form-item>
               </el-col>
               <el-col :span="12">
                  <el-form-item label="英文名称" prop="enName">
                     <el-input v-model="form.enName" placeholder="请输入官方英文名称" />
                  </el-form-item>
               </el-col>
            </el-row>
            <el-row>
               <el-col :span="24">
                  <el-form-item label="类型描述" prop="typesDesc">
                     <el-input v-model="form.typesDesc" placeholder="请输入类型描述文本" />
                  </el-form-item>
               </el-col>
            </el-row>
            <el-row>
               <el-col :span="12">
                  <el-form-item label="攻击力" prop="atk">
                     <el-input-number v-model="form.atk" controls-position="right" placeholder="请输入攻击力" />
                  </el-form-item>
               </el-col>
               <el-col :span="12">
                  <el-form-item label="防御力" prop="def">
                     <el-input-number v-model="form.def" controls-position="right" placeholder="请输入防御力" />
                  </el-form-item>
               </el-col>
            </el-row>
            <el-row>
               <el-col :span="12">
                  <el-form-item label="等级" prop="cardLevel">
                     <el-input-number v-model="form.cardLevel" controls-position="right" :min="0" placeholder="请输入等级/阶级" />
                  </el-form-item>
               </el-col>
               <el-col :span="12">
                  <el-form-item label="卡片大类" prop="cardKind">
                     <el-select v-model="form.cardKind" placeholder="请选择卡片大类" style="width: 100%">
                        <el-option
                           v-for="dict in yg_card_kind"
                           :key="dict.value"
                           :label="dict.label"
                           :value="Number(dict.value)"
                        />
                     </el-select>
                  </el-form-item>
               </el-col>
            </el-row>
            <el-row>
               <el-col :span="24">
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
            <el-row>
               <el-col :span="24">
                  <el-form-item label="效果文本" prop="effectDesc">
                     <el-input v-model="form.effectDesc" type="textarea" :rows="3" placeholder="请输入效果文本" />
                  </el-form-item>
               </el-col>
            </el-row>
            <el-row>
               <el-col :span="24">
                  <el-form-item label="灵摆文本" prop="pendulumDesc">
                     <el-input v-model="form.pendulumDesc" type="textarea" :rows="3" placeholder="请输入灵摆效果文本" />
                  </el-form-item>
               </el-col>
            </el-row>
            <el-row>
               <el-col :span="24">
                  <el-form-item label="卡图地址" prop="imageUrl">
                     <el-input v-model="form.imageUrl" placeholder="请输入卡图地址(CDN相对路径)" />
                  </el-form-item>
               </el-col>
            </el-row>
            <el-row>
               <el-col :span="24">
                  <el-form-item label="备注" prop="remark">
                     <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
                  </el-form-item>
               </el-col>
            </el-row>
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

<script setup name="Info">
import { listInfo, addInfo, delInfo, getInfo, updateInfo } from "@/api/card/info"
import { syncAllCards, syncSingleCard, syncCardSets, syncLimitLists } from "@/api/card/sync"
import { toggleCollection, listMyCollectIds } from "@/api/card/collection"

const { proxy } = getCurrentInstance()
const { sys_normal_disable, yg_card_kind } = useDict("sys_normal_disable", "yg_card_kind")

const infoList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
const isEdit = ref(false)
const syncLoading = ref(false)
// 当前用户已收藏的卡片密码集合
const collectedIds = ref([])

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    cnName: undefined,
    cardKind: undefined,
    status: undefined
  },
  rules: {
    cardId: [{ required: true, message: "卡片密码不能为空", trigger: "blur" }],
    cnName: [{ required: true, message: "中文名称不能为空", trigger: "blur" }],
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询当前用户收藏的卡片密码集合 */
function getCollectedIds() {
  listMyCollectIds().then(response => {
    collectedIds.value = response.data || []
  })
}

/** 收藏/取消收藏切换 */
function handleToggleCollect(row) {
  toggleCollection(row.cardId).then(response => {
    proxy.$modal.msgSuccess(response.data ? "收藏成功" : "已取消收藏")
    getCollectedIds()
  })
}

/** 查询卡片信息列表 */
function getList() {
  loading.value = true
  listInfo(queryParams.value).then(response => {
    infoList.value = response.rows
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
    cardId: undefined,
    cnName: undefined,
    scName: undefined,
    mdName: undefined,
    jpName: undefined,
    enName: undefined,
    typesDesc: undefined,
    atk: undefined,
    def: undefined,
    cardLevel: undefined,
    cardKind: undefined,
    status: "0",
    effectDesc: undefined,
    pendulumDesc: undefined,
    imageUrl: undefined,
    remark: undefined
  }
  proxy.resetForm("infoRef")
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
  ids.value = selection.map(item => item.cardId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  isEdit.value = false
  open.value = true
  title.value = "添加卡片信息"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const cardId = row.cardId || ids.value
  getInfo(cardId).then(response => {
    form.value = response.data
    isEdit.value = true
    open.value = true
    title.value = "修改卡片信息"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["infoRef"].validate(valid => {
    if (valid) {
      if (isEdit.value) {
        updateInfo(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addInfo(form.value).then(() => {
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
  const cardIds = row.cardId || ids.value
  proxy.$modal.confirm('是否确认删除卡片密码为"' + cardIds + '"的数据项？').then(function() {
    return delInfo(cardIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download("card/info/export", {
    ...queryParams.value
  }, `info_${new Date().getTime()}.xlsx`)
}

/** 全量同步按钮操作(md5比对,数据未变化时后端自动跳过) */
function handleSyncAll(force) {
  const tip = force ? "强制全量同步将重新下载全部卡片数据，是否继续？" : "全量同步将检查ygocdb数据更新并同步到本地，是否继续？"
  proxy.$modal.confirm(tip).then(() => {
    syncLoading.value = true
    proxy.$modal.loading("正在同步卡片数据，请耐心等待...")
    return syncAllCards(force)
  }).then(response => {
    const data = response.data || {}
    proxy.$modal.closeLoading()
    syncLoading.value = false
    if (data.skipped) {
      proxy.$modal.msg("数据未变化，已跳过同步")
    } else {
      proxy.$modal.alertSuccess("同步完成：共处理 " + (data.total || 0) + " 条")
      getList()
    }
  }).catch(() => {
    proxy.$modal.closeLoading()
    syncLoading.value = false
  })
}

/** 补录单卡按钮操作 */
function handleSyncSingle() {
  proxy.$modal.prompt("请输入要补录的卡片密码(9位数字id)").then(({ value }) => {
    const cardId = (value || "").trim()
    if (!/^\d{1,20}$/.test(cardId)) {
      proxy.$modal.msgError("卡片密码格式不正确")
      return Promise.reject()
    }
    return syncSingleCard(cardId)
  }).then(response => {
    if (response && response.data > 0) {
      proxy.$modal.msgSuccess("补录成功")
      getList()
    } else if (response) {
      proxy.$modal.msgWarning("ygocdb未找到该卡片，未补录")
    }
  }).catch(() => {})
}

/** 同步系列按钮操作 */
function handleSyncSets() {
  proxy.$modal.confirm("将从卡片数据中收集卡组系列code(名称需人工维护)，是否继续？").then(() => {
    syncLoading.value = true
    return syncCardSets()
  }).then(response => {
    syncLoading.value = false
    proxy.$modal.alertSuccess("系列收集完成：共 " + (response.data || 0) + " 个系列code")
  }).catch(() => {
    syncLoading.value = false
  })
}

/** 同步禁限表按钮操作 */
function handleSyncLimits() {
  proxy.$modal.confirm("将同步ygocdb当前禁限卡表(OCG/TCG/简中)，是否继续？").then(() => {
    syncLoading.value = true
    return syncLimitLists()
  }).then(response => {
    syncLoading.value = false
    const data = response.data || {}
    proxy.$modal.alertSuccess("禁限表同步完成：OCG " + (data.ocg || 0) + " 条，TCG " + (data.tcg || 0) + " 条，简中 " + (data.sc || 0) + " 条，跳过 " + (data.skipped || 0) + " 条")
  }).catch(() => {
    syncLoading.value = false
  })
}

getList()
getCollectedIds()
</script>
