<template>
   <div class="app-container">
      <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
         <el-form-item label="用户ID" prop="userId">
            <el-input
               v-model="queryParams.userId"
               placeholder="请输入用户ID"
               clearable
               style="width: 200px"
               @keyup.enter="handleQuery"
            />
         </el-form-item>
         <el-form-item label="卡包名称" prop="packName">
            <el-input
               v-model="queryParams.packName"
               placeholder="请输入卡包名称"
               clearable
               style="width: 200px"
               @keyup.enter="handleQuery"
            />
         </el-form-item>
         <el-form-item label="创建时间" style="width: 308px">
            <el-date-picker
               v-model="dateRange"
               value-format="YYYY-MM-DD HH:mm:ss"
               type="daterange"
               range-separator="-"
               start-placeholder="开始日期"
               end-placeholder="结束日期"
               :default-time="[new Date(2000, 1, 1, 0, 0, 0), new Date(2000, 1, 1, 23, 59, 59)]"
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
               v-hasPermi="['card:pack:add']"
            >新增卡包</el-button>
         </el-col>
         <el-col :span="1.5">
            <el-button
               type="danger"
               plain
               icon="Delete"
               :disabled="multiple"
               @click="handleDelete"
               v-hasPermi="['card:pack:remove']"
            >删除</el-button>
         </el-col>
         <el-col :span="6">
            <el-radio-group v-model="viewMode" class="view-switch">
               <el-radio-button value="table">
                  <el-icon><Grid /></el-icon>
                  <span style="margin-left: 4px">表格</span>
               </el-radio-button>
               <el-radio-button value="list">
                  <el-icon><List /></el-icon>
                  <span style="margin-left: 4px">列表</span>
               </el-radio-button>
            </el-radio-group>
         </el-col>
         <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <el-table v-if="viewMode === 'table'" v-loading="loading" :data="packList" @selection-change="handleSelectionChange">
         <el-table-column type="selection" width="55" align="center" />
         <el-table-column label="主键" align="center" prop="packId" />
         <el-table-column label="用户ID" align="center" prop="userId" />
         <el-table-column label="用户账号" align="center" prop="userName" />
         <el-table-column label="用户昵称" align="center" prop="nickName" />
         <el-table-column label="卡包名称" align="center" prop="packName" :show-overflow-tooltip="true" />
         <el-table-column label="卡片数量" align="center" prop="cardCount" />
         <el-table-column label="创建时间" align="center" prop="createTime" width="180">
            <template #default="scope">
               <span>{{ parseTime(scope.row.createTime) }}</span>
            </template>
         </el-table-column>
         <el-table-column label="备注" align="center" prop="remark" :show-overflow-tooltip="true" />
         <el-table-column label="操作" width="220" align="center" class-name="small-padding fixed-width">
            <template #default="scope">
               <el-button link type="primary" icon="Files" @click="handleOpenItems(scope.row)" v-hasPermi="['card:pack:query']">卡包卡片</el-button>
               <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['card:pack:edit']">修改</el-button>
               <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['card:pack:remove']">删除</el-button>
            </template>
         </el-table-column>
      </el-table>

      <!-- 列表(卡片)视图 -->
      <div v-else v-loading="loading" class="pack-card-grid">
         <el-empty v-if="!loading && packList.length === 0" description="暂无数据" style="grid-column: 1 / -1" />
         <el-card v-for="row in packList" :key="row.packId" class="pack-card" shadow="hover">
            <div class="pack-card-header">
               <span class="pack-card-name" :title="row.packName">{{ row.packName }}</span>
               <el-tag type="info" size="small">#{{ row.packId }}</el-tag>
            </div>
            <div class="pack-card-body">
               <div class="pack-card-field">
                  <span class="label">用户</span>
                  <span>{{ row.userName || '-' }}<template v-if="row.nickName">（{{ row.nickName }}）</template></span>
               </div>
               <div class="pack-card-field">
                  <span class="label">用户ID</span>
                  <span>{{ row.userId }}</span>
               </div>
               <div class="pack-card-field">
                  <span class="label">卡片数量</span>
                  <span class="pack-card-count">{{ row.cardCount }}</span>
               </div>
               <div class="pack-card-field">
                  <span class="label">创建时间</span>
                  <span>{{ parseTime(row.createTime) }}</span>
               </div>
               <div class="pack-card-field">
                  <span class="label">备注</span>
                  <span class="pack-card-remark" :title="row.remark">{{ row.remark || '-' }}</span>
               </div>
            </div>
            <div class="pack-card-footer">
               <el-button link type="primary" icon="Files" @click="handleOpenItems(row)" v-hasPermi="['card:pack:query']">卡包卡片</el-button>
               <el-button link type="primary" icon="Edit" @click="handleUpdate(row)" v-hasPermi="['card:pack:edit']">修改</el-button>
               <el-button link type="danger" icon="Delete" @click="handleDelete(row)" v-hasPermi="['card:pack:remove']">删除</el-button>
            </div>
         </el-card>
      </div>

      <pagination
         v-show="total > 0"
         :total="total"
         v-model:page="queryParams.pageNum"
         v-model:limit="queryParams.pageSize"
         @pagination="getList"
      />

      <!-- 添加或修改卡包对话框 -->
      <el-dialog :title="title" v-model="open" width="500px" append-to-body>
         <el-form ref="packRef" :model="form" :rules="rules" label-width="80px">
            <el-form-item label="用户ID" v-if="form.packId != undefined">
               <el-input :value="form.userName ? form.userName + ' (' + form.userId + ')' : form.userId" disabled />
            </el-form-item>
            <el-form-item label="卡包名称" prop="packName">
               <el-input v-model="form.packName" placeholder="请输入卡包名称" />
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

      <!-- 卡包卡片管理全屏抽屉 -->
      <el-drawer v-model="itemOpen" :title="'卡包[' + currentPack.packName + '] 内卡片'" size="100%" class="deck-drawer">
         <div class="deck-editor" v-loading="itemLoading">
            <!-- 蓝: 卡片详情(上卡片图 + 下文字详情) -->
            <div class="deck-panel deck-preview">
               <div class="deck-panel-title"><span>卡片详情</span></div>
               <div class="preview-body" v-loading="detailLoading">
                  <template v-if="previewCard">
                     <div class="preview-card-img">
                        <el-image :src="previewCard.imageUrl" fit="contain" style="width:100%;height:100%">
                           <template #error>
                              <div class="pack-item-noimg">无图</div>
                           </template>
                        </el-image>
                     </div>
                     <div class="detail-body">
                        <div class="detail-name" :title="previewCard.cnName">{{ previewCard.cnName || '-' }}</div>
                        <div class="detail-line">
                           <span class="detail-tag kind" v-if="detail">{{ kindText(detail.cardKind) }}</span>
                           <span class="detail-types" v-if="detail">{{ detail.typesDesc || '' }}</span>
                        </div>
                        <div class="detail-line" v-if="detail && detail.cardKind === 1 && !isLinkCard(detail)">
                           <span class="detail-label">等级/阶级</span><span>{{ detail.cardLevel }}</span>
                           <span class="detail-label">ATK</span><span>{{ detail.atk === -2 ? '?' : detail.atk }}</span>
                           <span class="detail-label">DEF</span><span>{{ detail.def === -2 ? '?' : detail.def }}</span>
                        </div>
                        <div class="detail-line" v-else-if="detail && isLinkCard(detail)">
                           <span class="detail-label">LINK</span><span>{{ detail.cardLevel }}</span>
                           <span class="detail-label">ATK</span><span>{{ detail.atk === -2 ? '?' : detail.atk }}</span>
                        </div>
                        <div class="detail-text" v-if="detail && detail.pendulumDesc">[灵摆] {{ detail.pendulumDesc }}</div>
                        <div class="detail-text" v-if="detail && detail.effectDesc">{{ detail.effectDesc }}</div>
                        <div class="detail-text detail-empty-text" v-else-if="detail && !detail.pendulumDesc">（无效果文本）</div>
                        <div class="detail-meta">密码 {{ previewCard.cardId }}<template v-if="previewCard.quantity"> · 数量 {{ previewCard.quantity }}</template></div>
                     </div>
                  </template>
                  <div v-else class="preview-empty">悬停卡片查看详情</div>
               </div>
            </div>

            <!-- 中: 绿主卡组 + 橙额外卡组 -->
            <div class="deck-middle">
               <div class="deck-panel deck-main">
                  <div class="deck-panel-title"><span>主卡组</span><span class="deck-count">{{ mainCount }}</span></div>
                  <div class="deck-section-grid">
                     <el-empty v-if="mainCards.length === 0" :image-size="60" description="暂无主卡" style="grid-column: 1 / -1" />
                     <div v-for="row in mainCards" :key="row.itemId" class="mini-card" @mouseenter="previewCard = row">
                        <el-image v-if="row.imageUrl" :src="row.imageUrl + '!half'" fit="cover">
                           <template #error><div class="pack-item-noimg">无图</div></template>
                        </el-image>
                        <div v-else class="pack-item-noimg">无图</div>
                        <span class="pack-item-qty">{{ row.quantity }}</span>
                     </div>
                  </div>
               </div>
               <div class="deck-panel deck-extra">
                  <div class="deck-panel-title"><span>额外卡组</span><span class="deck-count">{{ extraCount }}</span></div>
                  <div class="deck-section-grid">
                     <el-empty v-if="extraCards.length === 0" :image-size="60" description="暂无额外卡片" style="grid-column: 1 / -1" />
                     <div v-for="row in extraCards" :key="row.itemId" class="mini-card" @mouseenter="previewCard = row">
                        <el-image v-if="row.imageUrl" :src="row.imageUrl + '!half'" fit="cover">
                           <template #error><div class="pack-item-noimg">无图</div></template>
                        </el-image>
                        <div v-else class="pack-item-noimg">无图</div>
                        <span class="pack-item-qty">{{ row.quantity }}</span>
                     </div>
                  </div>
               </div>
            </div>

            <!-- 红: 所有卡片 / 添加卡片 -->
            <div class="deck-panel deck-all">
               <div class="deck-panel-title">
                  <span>{{ allTab === 'list' ? '所有卡片' : '添加卡片' }}</span>
                  <span class="deck-count">{{ allTab === 'list' ? itemTotal : cardTotal }}</span>
               </div>
               <div class="deck-all-toolbar">
                  <el-radio-group v-model="allTab" size="small">
                     <el-radio-button value="list">所有卡片</el-radio-button>
                     <el-radio-button value="pick">添加卡片</el-radio-button>
                  </el-radio-group>
                  <template v-if="allTab === 'list'">
                     <el-button
                        type="danger"
                        plain
                        size="small"
                        icon="Delete"
                        :disabled="itemMultiple"
                        @click="handleDeleteItems"
                        v-hasPermi="['card:pack:remove']"
                     >移除选中</el-button>
                  </template>
                  <template v-else>
                     <el-input
                        v-model="cardKeyword"
                        placeholder="卡片密码 / 名称"
                        size="small"
                        clearable
                        style="width: 160px"
                        @keyup.enter="handleCardQuery"
                        @clear="handleCardQuery"
                     />
                     <el-button type="primary" size="small" icon="Search" @click="handleCardQuery">搜索</el-button>
                  </template>
               </div>
               <div v-show="allTab === 'list'" class="deck-all-body">
                  <div class="pack-item-grid">
                     <el-empty v-if="!itemLoading && itemList.length === 0" description="暂无卡片" style="grid-column: 1 / -1" />
                     <div
                        v-for="row in itemList"
                        :key="row.itemId"
                        class="pack-item"
                        :class="{ 'is-checked': row.checked }"
                        @mouseenter="previewCard = row"
                     >
                        <div class="pack-item-img">
                           <el-checkbox v-model="row.checked" class="pack-item-check" />
                           <el-image
                              v-if="row.imageUrl"
                              :src="row.imageUrl + '!half'"
                              :preview-src-list="[row.imageUrl]"
                              preview-teleported
                              hide-on-click-modal
                              fit="cover"
                           >
                              <template #error>
                                 <div class="pack-item-noimg">无图</div>
                              </template>
                           </el-image>
                           <div v-else class="pack-item-noimg">无图</div>
                           <span class="pack-item-qty">{{ row.quantity }}</span>
                           <div class="pack-item-edit">
                              <button class="qty-btn" @click.stop="stepQuantity(row, -1)">-</button>
                              <span class="qty-num">{{ row.quantity }}</span>
                              <button class="qty-btn" @click.stop="stepQuantity(row, 1)">+</button>
                           </div>
                        </div>
                     </div>
                  </div>
                  <pagination
                     v-show="itemTotal > 0"
                     :total="itemTotal"
                     v-model:page="itemQueryParams.pageNum"
                     v-model:limit="itemQueryParams.pageSize"
                     layout="prev, pager, next"
                     @pagination="getItemList"
                  />
               </div>
               <div v-show="allTab === 'pick'" class="deck-all-body">
                  <div v-loading="cardLoading" class="pack-item-grid">
                     <el-empty v-if="!cardLoading && cardList.length === 0" description="无匹配卡片" style="grid-column: 1 / -1" />
                     <div
                        v-for="row in cardList"
                        :key="row.cardId"
                        class="pack-item"
                        @mouseenter="previewCard = row"
                     >
                        <div class="pack-item-img">
                           <el-image
                              v-if="row.imageUrl"
                              :src="row.imageUrl + '!half'"
                              :preview-src-list="[row.imageUrl]"
                              preview-teleported
                              hide-on-click-modal
                              fit="cover"
                           >
                              <template #error>
                                 <div class="pack-item-noimg">无图</div>
                              </template>
                           </el-image>
                           <div v-else class="pack-item-noimg">无图</div>
                           <button class="pick-add-btn" @click.stop="handlePickCard(row)">＋ 添加</button>
                        </div>
                     </div>
                  </div>
                  <pagination
                     v-show="cardTotal > 0"
                     :total="cardTotal"
                     v-model:page="cardQuery.pageNum"
                     v-model:limit="cardQuery.pageSize"
                     layout="prev, pager, next"
                     @pagination="getCardList"
                  />
               </div>
            </div>
         </div>
      </el-drawer>

   </div>
</template>

<script setup name="Pack">
import { listPack, getPack, addPack, updatePack, delPack, listPackItem, addPackItem, updatePackItem, delPackItem } from "@/api/card/pack"
import { listInfo, getInfo } from "@/api/card/info"

const { proxy } = getCurrentInstance()

const packList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const multiple = ref(true)
const total = ref(0)
const title = ref("")
const dateRange = ref([])
// 视图模式: table-表格 / list-列表
const viewMode = ref("table")

watch(viewMode, (val) => {
  if (val === "list") {
    ids.value = []
    multiple.value = true
  }
})

// 卡包明细
const itemOpen = ref(false)
const itemLoading = ref(false)
const itemList = ref([])
const itemMultiple = ref(true)
const itemTotal = ref(0)
const currentPack = ref({})
const itemQueryParams = ref({
  pageNum: 1,
  pageSize: 10,
  packId: undefined
})

// 添加卡片(右侧面板"添加卡片"页签)
const allTab = ref("list")
const cardKeyword = ref("")
const cardLoading = ref(false)
const cardList = ref([])
const cardTotal = ref(0)
const cardQuery = ref({
  pageNum: 1,
  pageSize: 12,
  cardId: undefined,
  cnName: undefined
})

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    userId: undefined,
    packName: undefined
  },
  rules: {
    packName: [{ required: true, message: "卡包名称不能为空", trigger: "blur" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询用户卡包列表 */
function getList() {
  loading.value = true
  listPack(proxy.addDateRange(queryParams.value, dateRange.value, 'CreateTime')).then(response => {
    packList.value = response.rows
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
    packId: undefined,
    userId: undefined,
    packName: undefined,
    remark: undefined
  }
  proxy.resetForm("packRef")
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
  ids.value = selection.map(item => item.packId)
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "新增卡包"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const packId = row.packId || ids.value
  getPack(packId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改卡包"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["packRef"].validate(valid => {
    if (valid) {
      if (form.value.packId != undefined) {
        updatePack(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addPack(form.value).then(() => {
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
  const packIds = row.packId || ids.value
  proxy.$modal.confirm('是否确认删除卡包编号为"' + packIds + '"的数据项？删除后卡包内卡片将一并移除。').then(function() {
    return delPack(packIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 打开卡包卡片管理 */
function handleOpenItems(row) {
  currentPack.value = row
  previewCard.value = null
  allTab.value = "list"
  itemQueryParams.value = { pageNum: 1, pageSize: 30, packId: row.packId }
  itemOpen.value = true
  getItemList()
}

/** 查询卡包内卡片列表(同一张卡的多条明细合并叠加数量) */
function getItemList() {
  itemLoading.value = true
  listPackItem(itemQueryParams.value).then(response => {
    const merged = []
    const indexByCard = {}
    ;(response.rows || []).forEach(item => {
      const key = String(item.cardId)
      if (indexByCard[key] !== undefined) {
        const exist = merged[indexByCard[key]]
        exist.quantity = (exist.quantity || 0) + (item.quantity || 0)
        exist.itemIds = exist.itemIds.concat([item.itemId])
      } else {
        indexByCard[key] = merged.length
        merged.push({ ...item, checked: false, itemIds: [item.itemId] })
      }
    })
    itemList.value = merged
    itemTotal.value = response.total
    itemLoading.value = false
  })
}

/** 额外卡组类型位掩码: 融合0x40 | 同调0x2000 | 超量0x800000 | 连接0x4000000 */
const EXTRA_TYPE_MASK = 0x40 | 0x2000 | 0x800000 | 0x4000000

/** 是否额外卡片 */
function isExtraCard(row) {
  return !!row.type && (row.type & EXTRA_TYPE_MASK) !== 0
}

/** 主卡组(当前页) */
const mainCards = computed(() => itemList.value.filter(row => !isExtraCard(row)))
/** 额外卡组(当前页) */
const extraCards = computed(() => itemList.value.filter(row => isExtraCard(row)))
/** 主卡组张数 */
const mainCount = computed(() => mainCards.value.reduce((sum, row) => sum + (row.quantity || 0), 0))
/** 额外卡组张数 */
const extraCount = computed(() => extraCards.value.reduce((sum, row) => sum + (row.quantity || 0), 0))

/** 大图预览卡片 */
const previewCard = ref(null)

/** 卡片详情 */
const detail = ref(null)
const detailLoading = ref(false)
let detailReqId = 0

watch(previewCard, (card) => {
  if (!card) {
    detail.value = null
    return
  }
  const reqId = ++detailReqId
  detailLoading.value = true
  getInfo(card.cardId).then(response => {
    if (reqId !== detailReqId) return
    detail.value = response.data
    detailLoading.value = false
  }).catch(() => {
    if (reqId === detailReqId) {
      detail.value = null
      detailLoading.value = false
    }
  })
})

/** 卡片大类文本 */
function kindText(kind) {
  return { 1: "怪兽卡", 2: "魔法卡", 3: "陷阱卡" }[kind] || ""
}

/** 是否连接怪兽 */
function isLinkCard(d) {
  return !!(d && d.type && (d.type & 0x4000000))
}

/** 卡包明细选中数据(含合并行的全部明细ID) */
const selectedItemIds = computed(() => itemList.value.filter(item => item.checked).flatMap(item => item.itemIds || [item.itemId]))
watch(selectedItemIds, (val) => {
  itemMultiple.value = val.length === 0
})

/** 调整卡包内卡片数量 */
function stepQuantity(row, delta) {
  const next = Math.min(99, Math.max(1, (row.quantity || 1) + delta))
  if (next === row.quantity) return
  row.quantity = next
  const extraIds = (row.itemIds || []).slice(1)
  const tasks = [updatePackItem({ itemId: row.itemId, quantity: next })]
  // 存在同卡重复明细时,数量归并到首条,其余删除
  if (extraIds.length > 0) {
    tasks.push(delPackItem(extraIds))
  }
  Promise.all(tasks).then(() => {
    getList()
  })
}

/** 移除卡包内选中卡片 */
function handleDeleteItems() {
  const ids = selectedItemIds.value
  proxy.$modal.confirm('是否确认移除选中的卡片？').then(function() {
    return delPackItem(ids)
  }).then(() => {
    getItemList()
    getList()
    proxy.$modal.msgSuccess("移除成功")
  }).catch(() => {})
}

/** 切换到"添加卡片"页签时加载卡片库 */
watch(allTab, (val) => {
  if (val === 'pick' && cardList.value.length === 0) {
    handleCardQuery()
  }
})

/** 查询卡片列表 */
function getCardList() {
  cardLoading.value = true
  const params = { ...cardQuery.value }
  // 关键词为纯数字按卡片密码检索,否则按卡片名称检索
  const kw = String(cardKeyword.value || '').trim()
  params.cardId = undefined
  params.cnName = undefined
  if (kw) {
    if (/^\d+$/.test(kw)) {
      params.cardId = kw
    } else {
      params.cnName = kw
    }
  }
  listInfo(params).then(response => {
    cardList.value = response.rows
    cardTotal.value = response.total
    cardLoading.value = false
  })
}

/** 卡片搜索 */
function handleCardQuery() {
  cardQuery.value.pageNum = 1
  getCardList()
}

/** 添加卡片到卡包 */
function handlePickCard(row) {
  addPackItem({ packId: currentPack.value.packId, cardId: row.cardId, quantity: 1 }).then(() => {
    proxy.$modal.msgSuccess("添加成功")
    getItemList()
    getList()
  })
}

getList()
</script>

<style scoped>
.view-switch {
   margin-left: 10px;
   vertical-align: middle;
}

.pack-card-grid {
   display: grid;
   grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
   gap: 16px;
   min-height: 120px;
}

.pack-card {
   display: flex;
   flex-direction: column;
}

.pack-card :deep(.el-card__body) {
   display: flex;
   flex-direction: column;
   flex: 1;
   padding: 14px 16px;
}

.pack-card-header {
   display: flex;
   align-items: center;
   justify-content: space-between;
   gap: 8px;
   padding-bottom: 10px;
   border-bottom: 1px solid var(--el-border-color-lighter);
}

.pack-card-name {
   font-size: 16px;
   font-weight: 600;
   overflow: hidden;
   text-overflow: ellipsis;
   white-space: nowrap;
}

.pack-card-body {
   flex: 1;
   padding: 10px 0;
   font-size: 13px;
}

.pack-card-field {
   display: flex;
   line-height: 26px;
}

.pack-card-field .label {
   flex-shrink: 0;
   width: 70px;
   color: var(--el-text-color-secondary);
}

.pack-card-count {
   font-weight: 600;
   color: var(--el-color-primary);
}

.pack-card-remark {
   overflow: hidden;
   text-overflow: ellipsis;
   white-space: nowrap;
}

.pack-card-footer {
   display: flex;
   justify-content: flex-end;
   gap: 4px;
   padding-top: 8px;
   border-top: 1px solid var(--el-border-color-lighter);
}

/* 卡包内卡片网格 */
.pack-item-grid {
   display: grid;
   grid-template-columns: repeat(6, 1fr);
   gap: 6px;
   min-height: 200px;
}

.pack-item {
   position: relative;
   border-radius: 4px;
   overflow: hidden;
   background-color: var(--el-fill-color-light);
}

.pack-item.is-checked {
   outline: 2px solid var(--el-color-primary);
   outline-offset: -2px;
}

.pack-item-img {
   position: relative;
   aspect-ratio: 5 / 7;
}

.pack-item-img :deep(.el-image) {
   width: 100%;
   height: 100%;
   display: block;
}

.pack-item-check {
   position: absolute;
   top: 0;
   left: 0;
   z-index: 2;
   height: auto;
   padding: 2px 0 0 4px;
   opacity: 0;
   transition: opacity 0.15s;
}

.pack-item-check :deep(.el-checkbox__inner) {
   background-color: rgba(255, 255, 255, 0.85);
}

.pack-item:hover .pack-item-check,
.pack-item.is-checked .pack-item-check {
   opacity: 1;
}

.pack-item-qty {
   position: absolute;
   left: 3px;
   bottom: 2px;
   z-index: 1;
   min-width: 14px;
   padding: 0 3px;
   font-size: 12px;
   font-weight: 700;
   line-height: 16px;
   text-align: center;
   color: #fff;
   text-shadow: 0 0 2px #000;
   pointer-events: none;
}

.pack-item-edit {
   position: absolute;
   right: 0;
   bottom: 0;
   z-index: 2;
   display: flex;
   align-items: center;
   gap: 2px;
   padding: 2px 3px;
   background-color: rgba(0, 0, 0, 0.65);
   border-radius: 4px 0 0 0;
   opacity: 0;
   transition: opacity 0.15s;
}

.pack-item:hover .pack-item-edit {
   opacity: 1;
}

.pack-item:hover .pack-item-qty {
   opacity: 0;
}

.qty-btn {
   width: 18px;
   height: 18px;
   line-height: 16px;
   padding: 0;
   font-size: 13px;
   color: #fff;
   background-color: rgba(255, 255, 255, 0.18);
   border: none;
   border-radius: 3px;
   cursor: pointer;
}

.qty-btn:hover {
   background-color: var(--el-color-primary);
}

.qty-num {
   min-width: 16px;
   font-size: 12px;
   font-weight: 700;
   color: #fff;
   text-align: center;
}

.pack-item-noimg {
   width: 100%;
   height: 100%;
   display: flex;
   align-items: center;
   justify-content: center;
   color: var(--el-text-color-secondary);
   font-size: 13px;
}

/* 全屏卡组编辑器 */
.deck-editor {
   display: flex;
   gap: 12px;
   height: 100%;
}

.deck-panel {
   display: flex;
   flex-direction: column;
   border: 2px solid var(--el-border-color);
   border-radius: 8px;
   overflow: hidden;
   background-color: var(--el-bg-color);
}

.deck-panel-title {
   display: flex;
   align-items: center;
   justify-content: space-between;
   gap: 8px;
   padding: 6px 10px;
   font-size: 14px;
   font-weight: 600;
   color: #fff;
   flex-shrink: 0;
}

.deck-count {
   font-size: 13px;
   font-weight: 700;
}

/* 蓝: 卡片详情 */
.deck-preview {
   border-color: var(--el-color-primary);
   flex: 0 0 320px;
}
.deck-preview .deck-panel-title {
   background-color: var(--el-color-primary);
}
.preview-body {
   flex: 1;
   display: flex;
   flex-direction: column;
   padding: 10px;
   overflow: auto;
}
.preview-card-img {
   flex: 0 0 auto;
   width: 260px;
   max-width: 100%;
   aspect-ratio: 5 / 7;
   margin: 0 auto;
   border-radius: 6px;
   overflow: hidden;
   background-color: var(--el-fill-color-light);
}
.detail-body {
   margin-top: 10px;
   flex: 1;
}
.detail-name {
   font-size: 16px;
   font-weight: 700;
   margin-bottom: 6px;
}
.detail-line {
   display: flex;
   flex-wrap: wrap;
   align-items: center;
   gap: 6px;
   font-size: 13px;
   margin-bottom: 6px;
}
.detail-tag {
   padding: 1px 8px;
   border-radius: 4px;
   font-size: 12px;
   color: #fff;
}
.detail-tag.kind {
   background-color: var(--el-color-primary);
}
.detail-types {
   color: var(--el-text-color-regular);
}
.detail-label {
   color: var(--el-text-color-secondary);
   margin-right: 2px;
}
.detail-label + span {
   margin-right: 10px;
}
.detail-text {
   font-size: 12px;
   line-height: 1.6;
   color: var(--el-text-color-regular);
   white-space: pre-wrap;
   border: 1px solid var(--el-border-color-lighter);
   border-radius: 4px;
   padding: 6px 8px;
   margin-bottom: 6px;
   background-color: var(--el-fill-color-lighter);
}
.detail-empty-text {
   color: var(--el-text-color-secondary);
}
.detail-meta {
   font-size: 12px;
   color: var(--el-text-color-secondary);
   margin-top: 4px;
}
.preview-empty {
   flex: 1;
   display: flex;
   align-items: center;
   justify-content: center;
   color: var(--el-text-color-secondary);
   font-size: 13px;
}

/* 中: 主卡组/额外卡组 */
.deck-middle {
   flex: 1;
   display: flex;
   flex-direction: column;
   gap: 12px;
   min-width: 0;
}
.deck-main {
   border-color: var(--el-color-success);
   flex: 1;
}
.deck-main .deck-panel-title {
   background-color: var(--el-color-success);
}
.deck-extra {
   border-color: var(--el-color-warning);
   flex: 0 0 30%;
}
.deck-extra .deck-panel-title {
   background-color: var(--el-color-warning);
}
.deck-section-grid {
   flex: 1;
   overflow: auto;
   display: grid;
   grid-template-columns: repeat(auto-fill, minmax(88px, 1fr));
   gap: 6px;
   padding: 8px;
   align-content: start;
}
.mini-card {
   position: relative;
   aspect-ratio: 5 / 7;
   border-radius: 4px;
   overflow: hidden;
   background-color: var(--el-fill-color-light);
   cursor: pointer;
}
.mini-card :deep(.el-image) {
   width: 100%;
   height: 100%;
   display: block;
}

/* 红: 所有卡片 */
.deck-all {
   border-color: var(--el-color-danger);
   flex: 0 0 420px;
}
.deck-all .deck-panel-title {
   background-color: var(--el-color-danger);
}
.deck-all-toolbar {
   display: flex;
   flex-wrap: wrap;
   align-items: center;
   gap: 8px;
   padding: 8px 10px 0;
   flex-shrink: 0;
}

.pick-add-btn {
   position: absolute;
   right: 0;
   bottom: 0;
   z-index: 2;
   padding: 2px 6px;
   font-size: 12px;
   color: #fff;
   background-color: rgba(0, 0, 0, 0.65);
   border: none;
   border-radius: 4px 0 0 0;
   cursor: pointer;
   opacity: 0;
   transition: opacity 0.15s;
}

.pack-item:hover .pick-add-btn {
   opacity: 1;
}

.pick-add-btn:hover {
   background-color: var(--el-color-primary);
}
.deck-all-body {
   flex: 1;
   display: flex;
   flex-direction: column;
   overflow: hidden;
}
.deck-all-body .pack-item-grid {
   flex: 1;
   overflow: auto;
   padding: 8px 10px;
   align-content: start;
   grid-template-columns: repeat(auto-fill, minmax(96px, 1fr));
}
</style>

<style>
/* 全屏抽屉: 内容区占满高度 */
.deck-drawer .el-drawer__body {
   height: 0;
   flex: 1;
   overflow: hidden;
   padding: 12px;
}
</style>
