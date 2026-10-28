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
      <el-form-item label="文章标题" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入文章标题"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="文章状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部状态" clearable style="width: 150px">
          <el-option
            v-for="item in statusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="发布时间">
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
          v-hasPermi="['shop:woo:post:sync']"
        >同步文章</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['shop:woo:post:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="postList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="文章标题" align="center" prop="title" min-width="260" show-overflow-tooltip />
      <el-table-column label="店铺" align="center" prop="storeName" width="130" show-overflow-tooltip>
        <template #default="scope">{{ scope.row.storeName || "已删除店铺" }}</template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="90">
        <template #default="scope">
          <el-tag :type="statusTagType(scope.row.status)">{{ statusLabel(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="摘要" align="center" prop="excerpt" min-width="220" show-overflow-tooltip />
      <el-table-column label="发布时间" align="center" prop="dateCreated" width="160">
        <template #default="scope">{{ scope.row.dateCreated ? parseTime(scope.row.dateCreated) : "-" }}</template>
      </el-table-column>
      <el-table-column label="更新时间" align="center" prop="dateModified" width="160">
        <template #default="scope">{{ scope.row.dateModified ? parseTime(scope.row.dateModified) : "-" }}</template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="160" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handlePreview(scope.row)" v-hasPermi="['shop:woo:post:query']">预览</el-button>
          <el-button v-if="scope.row.link" link type="primary" icon="Link" @click="openLink(scope.row.link)">访问</el-button>
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

    <!-- 文章预览对话框 -->
    <el-dialog title="文章预览" v-model="open" width="860px" append-to-body>
      <div class="post-header">
        <h2 class="post-title">{{ detail.title }}</h2>
        <div class="post-meta">
          <el-tag size="small" :type="statusTagType(detail.status)">{{ statusLabel(detail.status) }}</el-tag>
          <span>{{ detail.storeName }}</span>
          <span>{{ detail.dateCreated ? parseTime(detail.dateCreated) : "" }}</span>
          <el-link v-if="detail.link" :href="detail.link" target="_blank" type="primary">原文链接</el-link>
        </div>
      </div>
      <el-alert
        v-if="!detail.content"
        title="未同步到正文内容，可点击原文链接查看"
        type="info"
        :closable="false"
        show-icon
      />
      <div v-else class="post-content">{{ detail.content }}</div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="open = false">关 闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="ShopWooPost">
import { listWooPost, getWooPost, syncWooPost, delWooPost, getShopStoreOptions } from "@/api/shop";

const { proxy } = getCurrentInstance();

const postList = ref([]);
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
  { value: "publish", label: "已发布", tag: "success" },
  { value: "draft", label: "草稿", tag: "info" },
  { value: "pending", label: "待审核", tag: "warning" },
  { value: "private", label: "私有", tag: "info" }
];

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    storeId: undefined,
    title: undefined,
    status: undefined
  }
});

const { queryParams } = toRefs(data);

/** 文章状态文案 */
function statusLabel(value) {
  const item = statusOptions.find(o => o.value === value);
  return item ? item.label : (value || "-");
}

/** 文章状态标签颜色 */
function statusTagType(value) {
  const item = statusOptions.find(o => o.value === value);
  return item ? item.tag : "info";
}

/** 查询文章列表 */
function getList() {
  loading.value = true;
  listWooPost(proxy.addDateRange(queryParams.value, dateRange.value)).then(response => {
    postList.value = response.rows;
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
  ids.value = selection.map(item => item.postId);
  multiple.value = !selection.length;
}

/** 同步文章 */
function handleSync() {
  const storeId = queryParams.value.storeId;
  const tip = storeId ? "是否同步当前店铺的文章？" : "未选择店铺，将同步全部启用店铺的文章，是否继续？";
  proxy.$modal.confirm(tip).then(() => {
    syncing.value = true;
    return syncWooPost(storeId);
  }).then(response => {
    proxy.$modal.msgSuccess(response.msg || "同步完成");
    getList();
    getStoreOptions();
  }).catch(() => {}).finally(() => {
    syncing.value = false;
  });
}

/** 预览按钮操作 */
function handlePreview(row) {
  getWooPost(row.postId).then(response => {
    detail.value = response.data || {};
    open.value = true;
  });
}

/** 打开原文 */
function openLink(link) {
  window.open(link, "_blank");
}

/** 删除按钮操作 */
function handleDelete() {
  const postIds = ids.value;
  proxy.$modal.confirm('是否确认删除选中的文章数据？').then(function () {
    return delWooPost(postIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

getStoreOptions();
getList();
</script>

<style scoped>
.post-header {
  margin-bottom: 12px;
}

.post-title {
  margin: 0 0 8px;
  font-size: 20px;
  line-height: 1.4;
}

.post-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 13px;
  color: var(--el-text-color-secondary);
}

.post-content {
  max-height: 56vh;
  overflow-y: auto;
  padding: 16px;
  border-radius: 6px;
  background: var(--el-fill-color-light);
  white-space: pre-wrap;
  line-height: 1.8;
  font-size: 14px;
}
</style>
