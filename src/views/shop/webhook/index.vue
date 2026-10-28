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
      <el-form-item label="名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入Webhook名称"
          clearable
          style="width: 180px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="主题" prop="topic">
        <el-select v-model="queryParams.topic" placeholder="全部主题" clearable style="width: 160px">
          <el-option v-for="item in topicOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部状态" clearable style="width: 130px">
          <el-option label="启用" value="0" />
          <el-option label="停用" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['shop:webhook:add']">新增Webhook</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Refresh"
          :loading="syncing"
          @click="handleSync"
          v-hasPermi="['shop:webhook:sync']"
        >从店铺同步</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['shop:webhook:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="webhookList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="店铺" align="center" prop="storeName" width="130" show-overflow-tooltip>
        <template #default="scope">{{ scope.row.storeName || "已删除店铺" }}</template>
      </el-table-column>
      <el-table-column label="名称" align="center" prop="name" min-width="160" show-overflow-tooltip />
      <el-table-column label="主题" align="center" prop="topic" width="130">
        <template #default="scope">
          <el-tag :type="topicTagType(scope.row.topic)">{{ topicLabel(scope.row.topic) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="回调地址" align="center" prop="deliveryUrl" min-width="260" show-overflow-tooltip />
      <el-table-column label="状态" align="center" prop="status" width="90">
        <template #default="scope">
          <el-switch
            :model-value="scope.row.status === '0'"
            @change="handleStatusChange(scope.row)"
            v-hasPermi="['shop:webhook:edit']"
          />
        </template>
      </el-table-column>
      <el-table-column label="最近推送" align="center" width="170">
        <template #default="scope">
          <template v-if="scope.row.lastDeliveryTime">
            <div class="sub-text">{{ parseTime(scope.row.lastDeliveryTime) }}</div>
            <el-tag size="small" :type="scope.row.lastDeliveryStatus === '0' ? 'success' : 'danger'">
              {{ scope.row.lastDeliveryStatus === '0' ? '成功' : '失败' }}
            </el-tag>
          </template>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template #default="scope">{{ parseTime(scope.row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="150" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['shop:webhook:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['shop:webhook:remove']">删除</el-button>
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

    <!-- 新增/修改Webhook对话框 -->
    <el-dialog :title="title" v-model="open" width="700px" append-to-body>
      <el-alert
        type="info"
        :closable="false"
        show-icon
        title="回调地址必须公网可访问，且指向本系统；建议格式：https://你的域名/prod-api/shop/webhook/receive/{店铺ID}"
        style="margin-bottom: 16px"
      />
      <el-form ref="webhookRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="所属店铺" prop="storeId">
              <el-select
                v-model="form.storeId"
                placeholder="请选择店铺"
                :disabled="!!form.webhookId"
                style="width: 100%"
                @change="handleStoreChange"
              >
                <el-option
                  v-for="store in storeOptions"
                  :key="store.storeId"
                  :label="store.storeName"
                  :value="store.storeId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="主题" prop="topic">
              <el-select v-model="form.topic" style="width: 100%">
                <el-option v-for="item in topicOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="Webhook名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入名称" maxlength="190" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="回调地址" prop="deliveryUrl">
              <el-input v-model="form.deliveryUrl" placeholder="https://你的域名/prod-api/shop/webhook/receive/店铺ID" maxlength="490" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="密钥" prop="secret">
              <el-input v-model="form.secret" placeholder="用于校验WooCommerce推送签名" maxlength="100" show-password>
                <template #append>
                  <el-button @click="form.secret = generateSecret()">生成</el-button>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio value="0">启用</el-radio>
                <el-radio value="1">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" maxlength="500" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" :loading="saving" @click="submitForm">确 定</el-button>
          <el-button @click="open = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="ShopWebhook">
import {
  listShopWebhook,
  getShopWebhook,
  addShopWebhook,
  updateShopWebhook,
  changeShopWebhookStatus,
  delShopWebhook,
  syncShopWebhook,
  getShopStoreOptions
} from "@/api/shop";

const { proxy } = getCurrentInstance();

const webhookList = ref([]);
const storeOptions = ref([]);
const loading = ref(true);
const syncing = ref(false);
const saving = ref(false);
const showSearch = ref(true);
const ids = ref([]);
const multiple = ref(true);
const total = ref(0);
const open = ref(false);
const title = ref("");

const topicOptions = [
  { value: "order.created", label: "订单创建" },
  { value: "order.updated", label: "订单更新" },
  { value: "order.deleted", label: "订单删除" },
  { value: "product.created", label: "商品创建" },
  { value: "product.updated", label: "商品更新" },
  { value: "product.deleted", label: "商品删除" }
];

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    storeId: undefined,
    name: undefined,
    topic: undefined,
    status: undefined
  },
  rules: {
    storeId: [{ required: true, message: "请选择店铺", trigger: "change" }],
    name: [{ required: true, message: "Webhook名称不能为空", trigger: "blur" }],
    topic: [{ required: true, message: "请选择主题", trigger: "change" }],
    deliveryUrl: [
      { required: true, message: "回调地址不能为空", trigger: "blur" },
      { pattern: /^https?:\/\/.+/, message: "回调地址必须以 http:// 或 https:// 开头", trigger: "blur" }
    ]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 主题文案 */
function topicLabel(value) {
  const item = topicOptions.find(o => o.value === value);
  return item ? item.label : (value || "-");
}

/** 主题标签颜色 */
function topicTagType(value) {
  return value && value.startsWith("order.") ? "warning" : "success";
}

/** 查询列表 */
function getList() {
  loading.value = true;
  listShopWebhook(queryParams.value).then(response => {
    webhookList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

/** 店铺下拉 */
function getStoreOptions() {
  getShopStoreOptions().then(response => {
    storeOptions.value = response.data || [];
  });
}

/** 建议的回调地址 */
function suggestDeliveryUrl(storeId) {
  if (!storeId) {
    return "";
  }
  const base = import.meta.env.VITE_APP_BASE_API || "";
  return `${window.location.origin}${base}/shop/webhook/receive/${storeId}`;
}

/** 生成密钥 */
function generateSecret() {
  const bytes = new Uint8Array(16);
  if (window.crypto && window.crypto.getRandomValues) {
    window.crypto.getRandomValues(bytes);
  } else {
    for (let i = 0; i < bytes.length; i++) {
      bytes[i] = Math.floor(Math.random() * 256);
    }
  }
  return Array.from(bytes).map(b => b.toString(16).padStart(2, "0")).join("");
}

/** 切换店铺时更新建议地址 */
function handleStoreChange(storeId) {
  if (!form.value.webhookId && (!form.value.deliveryUrl || form.value.deliveryUrl.includes("/shop/webhook/receive/"))) {
    form.value.deliveryUrl = suggestDeliveryUrl(storeId);
  }
}

/** 搜索 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置 */
function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

/** 多选 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.webhookId);
  multiple.value = !selection.length;
}

/** 表单重置 */
function reset() {
  const firstStoreId = queryParams.value.storeId || (storeOptions.value[0] && storeOptions.value[0].storeId);
  form.value = {
    webhookId: undefined,
    storeId: firstStoreId,
    name: undefined,
    topic: "order.created",
    deliveryUrl: suggestDeliveryUrl(firstStoreId),
    secret: generateSecret(),
    status: "0",
    remark: undefined
  };
  proxy.resetForm("webhookRef");
}

/** 新增 */
function handleAdd() {
  reset();
  title.value = "新增Webhook";
  open.value = true;
}

/** 修改 */
function handleUpdate(row) {
  reset();
  const webhookId = row.webhookId || ids.value[0];
  getShopWebhook(webhookId).then(response => {
    form.value = response.data;
    title.value = "修改Webhook";
    open.value = true;
  });
}

/** 提交 */
function submitForm() {
  proxy.$refs["webhookRef"].validate(valid => {
    if (!valid) {
      return;
    }
    saving.value = true;
    const payload = { ...form.value };
    const action = payload.webhookId ? updateShopWebhook(payload) : addShopWebhook(payload);
    action.then(() => {
      proxy.$modal.msgSuccess(payload.webhookId ? "修改成功" : "新增成功");
      open.value = false;
      getList();
    }).catch(() => {}).finally(() => {
      saving.value = false;
    });
  });
}

/** 启停 */
function handleStatusChange(row) {
  const newStatus = row.status === "0" ? "1" : "0";
  changeShopWebhookStatus(row.webhookId, newStatus).then(() => {
    row.status = newStatus;
    proxy.$modal.msgSuccess(newStatus === "0" ? "已启用" : "已停用");
  }).catch(() => {
    getList();
  });
}

/** 从店铺同步 */
function handleSync() {
  const storeId = queryParams.value.storeId;
  const tip = storeId ? "是否从当前店铺同步Webhook？" : "未选择店铺，将从全部启用店铺同步Webhook，是否继续？";
  proxy.$modal.confirm(tip).then(() => {
    syncing.value = true;
    return syncShopWebhook(storeId);
  }).then(response => {
    proxy.$modal.msgSuccess(response.msg || "同步完成");
    getList();
  }).catch(() => {}).finally(() => {
    syncing.value = false;
  });
}

/** 删除 */
function handleDelete(row) {
  const webhookIds = row.webhookId || ids.value;
  proxy.$modal.confirm('删除会同时移除店铺中的Webhook，是否继续？').then(function () {
    return delShopWebhook(webhookIds);
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
