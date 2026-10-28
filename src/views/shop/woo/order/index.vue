<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="店铺" prop="storeId">
        <el-select v-model="queryParams.storeId" placeholder="全部店铺" clearable style="width: 180px">
          <el-option
            v-for="store in storeOptions"
            :key="store.storeId"
            :label="store.storeName"
            :value="store.storeId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="订单状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部状态" clearable style="width: 160px">
          <el-option
            v-for="item in statusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="订单号" prop="orderNumber">
        <el-input
          v-model="queryParams.orderNumber"
          placeholder="请输入订单号"
          clearable
          style="width: 180px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="下单时间">
        <el-date-picker
          v-model="dateRange"
          value-format="YYYY-MM-DD"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          style="width: 240px"
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
          icon="Refresh"
          :loading="syncing"
          @click="handleSync"
          v-hasPermi="['shop:woo:order:sync']"
        >同步订单</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['shop:woo:order:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="orderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="店铺" align="center" prop="storeName" width="130" show-overflow-tooltip>
        <template #default="scope">{{ scope.row.storeName || "已删除店铺" }}</template>
      </el-table-column>
      <el-table-column label="订单号" align="center" prop="orderNumber" width="140" show-overflow-tooltip />
      <el-table-column label="状态" align="center" prop="status" width="110">
        <template #default="scope">
          <el-tag :type="statusTagType(scope.row.status)">{{ statusLabel(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="客户" align="center" min-width="180" show-overflow-tooltip>
        <template #default="scope">
          <div>{{ scope.row.customerName || "-" }}</div>
          <div class="sub-text">{{ scope.row.customerEmail }}</div>
        </template>
      </el-table-column>
      <el-table-column label="商品件数" align="center" prop="itemCount" width="90" />
      <el-table-column label="订单总额" align="center" width="130">
        <template #default="scope">{{ formatAmount(scope.row) }}</template>
      </el-table-column>
      <el-table-column label="支付方式" align="center" prop="paymentMethod" min-width="120" show-overflow-tooltip />
      <el-table-column label="下单时间" align="center" prop="dateCreated" width="160">
        <template #default="scope">{{ parseTime(scope.row.dateCreated) }}</template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="100" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleDetail(scope.row)" v-hasPermi="['shop:woo:order:query']">详情</el-button>
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

    <!-- 订单详情对话框 -->
    <el-dialog title="订单详情" v-model="open" width="760px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="店铺">{{ detail.storeName || "-" }}</el-descriptions-item>
        <el-descriptions-item label="Woo订单ID">{{ detail.wooOrderId }}</el-descriptions-item>
        <el-descriptions-item label="订单号">{{ detail.orderNumber }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTagType(detail.status)">{{ statusLabel(detail.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="客户姓名">{{ detail.customerName || "-" }}</el-descriptions-item>
        <el-descriptions-item label="客户邮箱">{{ detail.customerEmail || "-" }}</el-descriptions-item>
        <el-descriptions-item label="订单总额">{{ formatAmount(detail) }}</el-descriptions-item>
        <el-descriptions-item label="支付方式">{{ detail.paymentMethod || "-" }}</el-descriptions-item>
        <el-descriptions-item label="商品件数">{{ detail.itemCount }}</el-descriptions-item>
        <el-descriptions-item label="下单时间">{{ parseTime(detail.dateCreated) || "-" }}</el-descriptions-item>
        <el-descriptions-item label="支付时间">{{ detail.datePaid ? parseTime(detail.datePaid) : "-" }}</el-descriptions-item>
        <el-descriptions-item label="同步时间">{{ parseTime(detail.updateTime) || "-" }}</el-descriptions-item>
        <el-descriptions-item label="商品摘要" :span="2">{{ detail.itemsSummary || "-" }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="open = false">关 闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="ShopWooOrder">
import { listWooOrder, getWooOrder, syncWooOrder, delWooOrder, getShopStoreOptions } from "@/api/shop";

const { proxy } = getCurrentInstance();

const orderList = ref([]);
const storeOptions = ref([]);
const open = ref(false);
const loading = ref(true);
const syncing = ref(false);
const showSearch = ref(true);
const ids = ref([]);
const multiple = ref(true);
const total = ref(0);
const dateRange = ref([]);
const detail = ref({});

const statusOptions = [
  { value: "pending", label: "待付款", tag: "warning" },
  { value: "processing", label: "处理中", tag: "primary" },
  { value: "on-hold", label: "暂停", tag: "info" },
  { value: "completed", label: "已完成", tag: "success" },
  { value: "cancelled", label: "已取消", tag: "info" },
  { value: "refunded", label: "已退款", tag: "danger" },
  { value: "failed", label: "已失败", tag: "danger" }
];

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    storeId: undefined,
    status: undefined,
    orderNumber: undefined
  }
});

const { queryParams } = toRefs(data);

/** 状态文案 */
function statusLabel(value) {
  const item = statusOptions.find(o => o.value === value);
  return item ? item.label : (value || "-");
}

/** 状态标签颜色 */
function statusTagType(value) {
  const item = statusOptions.find(o => o.value === value);
  return item ? item.tag : "info";
}

/** 金额展示 */
function formatAmount(row) {
  if (!row || row.total == null) {
    return "-";
  }
  return (row.currency || "") + " " + Number(row.total).toFixed(2);
}

/** 查询订单列表 */
function getList() {
  loading.value = true;
  listWooOrder(proxy.addDateRange(queryParams.value, dateRange.value)).then(response => {
    orderList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

/** 店铺下拉选项 */
function getStoreOptions() {
  getShopStoreOptions().then(response => {
    storeOptions.value = response.data || [];
  });
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = [];
  proxy.resetForm("queryRef");
  handleQuery();
}

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.orderId);
  multiple.value = !selection.length;
}

/** 同步订单 */
function handleSync() {
  const storeId = queryParams.value.storeId;
  const tip = storeId ? "是否同步当前店铺的订单？" : "未选择店铺，将同步全部启用店铺的订单，是否继续？";
  proxy.$modal.confirm(tip).then(() => {
    syncing.value = true;
    return syncWooOrder(storeId);
  }).then(response => {
    proxy.$modal.msgSuccess(response.msg || "同步完成");
    getList();
    getStoreOptions();
  }).catch(() => {}).finally(() => {
    syncing.value = false;
  });
}

/** 详情按钮操作 */
function handleDetail(row) {
  getWooOrder(row.orderId).then(response => {
    detail.value = response.data || {};
    open.value = true;
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  const orderIds = row.orderId || ids.value;
  proxy.$modal.confirm('是否确认删除选中的订单数据？').then(function () {
    return delWooOrder(orderIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

getStoreOptions();
getList();
</script>

<style scoped>
.sub-text {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}
</style>
