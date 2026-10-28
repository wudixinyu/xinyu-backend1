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
      <el-form-item label="商品名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入商品名称"
          clearable
          style="width: 180px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="SKU" prop="sku">
        <el-input
          v-model="queryParams.sku"
          placeholder="请输入SKU"
          clearable
          style="width: 160px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商品状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部状态" clearable style="width: 150px">
          <el-option
            v-for="item in statusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="库存状态" prop="stockStatus">
        <el-select v-model="queryParams.stockStatus" placeholder="全部库存" clearable style="width: 150px">
          <el-option label="有货" value="instock" />
          <el-option label="缺货" value="outofstock" />
          <el-option label="可订购" value="onbackorder" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['shop:woo:product:add']">新增商品</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Refresh"
          :loading="syncing"
          @click="handleSync"
          v-hasPermi="['shop:woo:product:sync']"
        >同步商品</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Upload" @click="handleImport" v-hasPermi="['shop:woo:product:import']">批量上传</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain icon="Download" @click="handleDownloadTemplate" v-hasPermi="['shop:woo:product:import']">下载模板</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['shop:woo:product:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-tabs v-model="activeTab">
      <!-- 卡片图墙浏览 -->
      <el-tab-pane label="商品浏览" name="browse">
        <div v-loading="loading" class="card-grid">
          <el-empty v-if="!productList.length" description="暂无商品" />
          <el-row v-else :gutter="16">
            <el-col v-for="item in productList" :key="item.productId" :xs="12" :sm="8" :md="6" :lg="6" :xl="4">
              <el-card class="product-card" shadow="hover" :body-style="{ padding: '0' }" @click="handleDetail(item)">
                <div class="product-cover">
                  <el-image v-if="item.imageUrl" :src="item.imageUrl" fit="cover" class="cover-img" lazy />
                  <div v-else class="cover-empty">暂无图片</div>
                  <el-tag class="status-tag" size="small" :type="statusTagType(item.status)">{{ statusLabel(item.status) }}</el-tag>
                </div>
                <div class="product-info">
                  <div class="product-name" :title="item.name">{{ item.name }}</div>
                  <div class="product-sku">{{ item.sku || "无SKU" }}</div>
                  <div class="product-price-row">
                    <span class="price">{{ formatPrice(item.price) }}</span>
                    <span v-if="item.salePrice && Number(item.salePrice) > 0" class="origin-price">{{ formatPrice(item.regularPrice) }}</span>
                  </div>
                  <div class="product-meta">
                    <span>{{ stockLabel(item.stockStatus) }}</span>
                    <span>库存 {{ item.stockQuantity == null ? "-" : item.stockQuantity }}</span>
                  </div>
                  <div class="product-store">{{ item.storeName || "已删除店铺" }}</div>
                </div>
                <div class="product-actions">
                  <el-button link type="primary" icon="Edit" @click.stop="handleEdit(item)" v-hasPermi="['shop:woo:product:edit']">编辑</el-button>
                  <el-button v-if="item.permalink" link type="primary" icon="Link" @click.stop="openLink(item.permalink)">访问</el-button>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>
      </el-tab-pane>

      <!-- 表格列表 -->
      <el-tab-pane label="商品列表" name="list">
        <el-table v-loading="loading" :data="productList" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column label="图片" align="center" width="80">
            <template #default="scope">
              <el-image
                v-if="scope.row.imageUrl"
                :src="scope.row.imageUrl"
                :preview-src-list="[scope.row.imageUrl]"
                preview-teleported
                fit="cover"
                style="width: 44px; height: 44px; border-radius: 4px"
              />
              <span v-else class="sub-text">无图</span>
            </template>
          </el-table-column>
          <el-table-column label="商品名称" align="center" prop="name" min-width="220" show-overflow-tooltip>
            <template #default="scope">
              <el-link v-if="scope.row.permalink" :href="scope.row.permalink" target="_blank" type="primary">{{ scope.row.name }}</el-link>
              <span v-else>{{ scope.row.name }}</span>
            </template>
          </el-table-column>
          <el-table-column label="SKU" align="center" prop="sku" width="140" show-overflow-tooltip />
          <el-table-column label="价格" align="center" width="100">
            <template #default="scope">{{ formatPrice(scope.row.price) }}</template>
          </el-table-column>
          <el-table-column label="促销价" align="center" width="100">
            <template #default="scope">{{ formatSalePrice(scope.row.salePrice) }}</template>
          </el-table-column>
          <el-table-column label="库存" align="center" width="120">
            <template #default="scope">
              <span>{{ scope.row.stockQuantity == null ? "-" : scope.row.stockQuantity }}</span>
              <el-tag size="small" :type="stockTagType(scope.row.stockStatus)" style="margin-left: 4px">{{ stockLabel(scope.row.stockStatus) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="状态" align="center" prop="status" width="90">
            <template #default="scope">
              <el-tag :type="statusTagType(scope.row.status)">{{ statusLabel(scope.row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="分类" align="center" prop="categories" min-width="140" show-overflow-tooltip />
          <el-table-column label="店铺" align="center" prop="storeName" width="130" show-overflow-tooltip>
            <template #default="scope">{{ scope.row.storeName || "已删除店铺" }}</template>
          </el-table-column>
          <el-table-column label="更新时间" align="center" prop="dateModified" width="160">
            <template #default="scope">{{ scope.row.dateModified ? parseTime(scope.row.dateModified) : "-" }}</template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="140" class-name="small-padding fixed-width">
            <template #default="scope">
              <el-button link type="primary" icon="View" @click="handleDetail(scope.row)">详情</el-button>
              <el-button link type="primary" icon="Edit" @click="handleEdit(scope.row)" v-hasPermi="['shop:woo:product:edit']">编辑</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 商品详情抽屉 -->
    <el-drawer v-model="detailOpen" title="商品详情" size="480px">
      <div v-loading="detailLoading" class="detail-body">
        <el-image v-if="detail.imageUrl" :src="detail.imageUrl" fit="cover" class="detail-cover" :preview-src-list="[detail.imageUrl]" preview-teleported />
        <h3 class="detail-name">{{ detail.name }}</h3>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="店铺">{{ detail.storeName || "已删除店铺" }}</el-descriptions-item>
          <el-descriptions-item label="SKU">{{ detail.sku || "-" }}</el-descriptions-item>
          <el-descriptions-item label="商品类型">{{ detail.type || "-" }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="statusTagType(detail.status)">{{ statusLabel(detail.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="现价">{{ formatPrice(detail.price) }}</el-descriptions-item>
          <el-descriptions-item label="原价">{{ formatPrice(detail.regularPrice) }}</el-descriptions-item>
          <el-descriptions-item label="促销价">{{ formatSalePrice(detail.salePrice) }}</el-descriptions-item>
          <el-descriptions-item label="库存">
            {{ detail.stockQuantity == null ? "-" : detail.stockQuantity }}（{{ stockLabel(detail.stockStatus) }}）
          </el-descriptions-item>
          <el-descriptions-item label="分类">{{ detail.categories || "-" }}</el-descriptions-item>
          <el-descriptions-item label="店铺更新时间">{{ detail.dateModified ? parseTime(detail.dateModified) : "-" }}</el-descriptions-item>
        </el-descriptions>
        <div class="detail-section-title">商品描述</div>
        <div class="detail-description">{{ detail.description || "暂无描述" }}</div>
        <div class="detail-actions">
          <el-button type="primary" icon="Edit" @click="handleEdit(detail)" v-hasPermi="['shop:woo:product:edit']">编辑商品</el-button>
          <el-button v-if="detail.permalink" icon="Link" @click="openLink(detail.permalink)">访问商品</el-button>
        </div>
      </div>
    </el-drawer>

    <!-- 新增/编辑商品对话框 -->
    <el-dialog :title="formTitle" v-model="formOpen" width="760px" append-to-body>
      <el-form ref="productRef" :model="form" :rules="rules" label-width="100px" v-loading="formLoading">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="所属店铺" prop="storeId">
              <el-select v-model="form.storeId" placeholder="请选择店铺" :disabled="!!form.productId" style="width: 100%">
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
            <el-form-item label="商品类型" prop="type">
              <el-select v-model="form.type" :disabled="!!form.productId" style="width: 100%">
                <el-option label="简单商品" value="simple" />
                <el-option label="可变商品" value="variable" />
                <el-option label="分组商品" value="grouped" />
                <el-option label="外部商品" value="external" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="商品名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入商品名称" maxlength="200" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="SKU" prop="sku">
              <el-input v-model="form.sku" placeholder="店铺内唯一，可留空" maxlength="190" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商品状态" prop="status">
              <el-select v-model="form.status" style="width: 100%">
                <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="原价" prop="regularPrice">
              <el-input-number v-model="form.regularPrice" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="促销价" prop="salePrice">
              <el-input-number v-model="form.salePrice" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="库存数量" prop="stockQuantity">
              <el-input-number v-model="form.stockQuantity" :min="0" :precision="0" controls-position="right" style="width: 100%" placeholder="留空不管理库存" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="库存状态" prop="stockStatus">
              <el-select v-model="form.stockStatus" style="width: 100%">
                <el-option label="有货" value="instock" />
                <el-option label="缺货" value="outofstock" />
                <el-option label="可订购" value="onbackorder" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商品分类" prop="categories">
              <el-input v-model="form.categories" placeholder="多个分类用逗号分隔，不存在的会自动创建" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="主图地址" prop="imageUrl">
              <el-input v-model="form.imageUrl" placeholder="https://... 图片地址，修改后以该图作为主图" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="商品描述" prop="description">
              <el-input v-model="form.description" type="textarea" :rows="4" placeholder="支持HTML，可留空" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" :loading="saving" @click="submitForm">确 定</el-button>
          <el-button @click="formOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 批量上传对话框 -->
    <excel-import-dialog
      ref="importRef"
      title="WooCommerce商品批量上传"
      action="/shop/woo/product/importData"
      template-action="/shop/woo/product/importTemplate"
      template-file-name="woocommerce_product_template"
      update-support-label="SKU已存在时更新商品信息"
      accept=".xlsx, .xls"
      :extensions="['xlsx', 'xls']"
      @success="getList"
    />
  </div>
</template>

<script setup name="ShopWooProduct">
import {
  listWooProduct,
  syncWooProduct,
  delWooProduct,
  getShopStoreOptions,
  getWooProductDetail,
  addWooProduct,
  updateWooProduct
} from "@/api/shop";
import ExcelImportDialog from "@/components/ExcelImportDialog";

const { proxy } = getCurrentInstance();

const productList = ref([]);
const storeOptions = ref([]);
const loading = ref(true);
const syncing = ref(false);
const showSearch = ref(true);
const ids = ref([]);
const multiple = ref(true);
const total = ref(0);
const activeTab = ref("browse");
const detailOpen = ref(false);
const detailLoading = ref(false);
const detail = ref({});
const formOpen = ref(false);
const formLoading = ref(false);
const saving = ref(false);
const formTitle = ref("");
const importRef = ref(null);

const statusOptions = [
  { value: "publish", label: "已发布", tag: "success" },
  { value: "draft", label: "草稿", tag: "info" },
  { value: "pending", label: "待审核", tag: "warning" },
  { value: "private", label: "私有", tag: "info" }
];

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 12,
    storeId: undefined,
    name: undefined,
    sku: undefined,
    status: undefined,
    stockStatus: undefined
  },
  rules: {
    storeId: [{ required: true, message: "请选择店铺", trigger: "change" }],
    name: [{ required: true, message: "商品名称不能为空", trigger: "blur" }]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 商品状态文案 */
function statusLabel(value) {
  const item = statusOptions.find(o => o.value === value);
  return item ? item.label : (value || "-");
}

/** 商品状态标签颜色 */
function statusTagType(value) {
  const item = statusOptions.find(o => o.value === value);
  return item ? item.tag : "info";
}

/** 库存状态文案 */
function stockLabel(value) {
  if (value === "instock") return "有货";
  if (value === "outofstock") return "缺货";
  if (value === "onbackorder") return "可订购";
  return value || "-";
}

/** 库存状态标签颜色 */
function stockTagType(value) {
  if (value === "instock") return "success";
  if (value === "outofstock") return "danger";
  return "info";
}

/** 价格展示 */
function formatPrice(value) {
  if (value == null) {
    return "-";
  }
  return Number(value).toFixed(2);
}

/** 促销价展示（0 视为未设置） */
function formatSalePrice(value) {
  if (value == null || Number(value) === 0) {
    return "-";
  }
  return Number(value).toFixed(2);
}

/** 查询商品列表 */
function getList() {
  loading.value = true;
  listWooProduct(queryParams.value).then(response => {
    productList.value = response.rows;
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
  proxy.resetForm("queryRef");
  handleQuery();
}

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.productId);
  multiple.value = !selection.length;
}

/** 同步商品 */
function handleSync() {
  const storeId = queryParams.value.storeId;
  const tip = storeId ? "是否同步当前店铺的商品？" : "未选择店铺，将同步全部启用店铺的商品，是否继续？";
  proxy.$modal.confirm(tip).then(() => {
    syncing.value = true;
    return syncWooProduct(storeId);
  }).then(response => {
    proxy.$modal.msgSuccess(response.msg || "同步完成");
    getList();
    getStoreOptions();
  }).catch(() => {}).finally(() => {
    syncing.value = false;
  });
}

/** 打开商品详情 */
function handleDetail(row) {
  detail.value = { ...row };
  detailOpen.value = true;
  detailLoading.value = true;
  getWooProductDetail(row.productId).then(response => {
    detail.value = response.data || row;
  }).catch(() => {}).finally(() => {
    detailLoading.value = false;
  });
}

/** 重置商品表单 */
function resetForm() {
  form.value = {
    productId: undefined,
    storeId: queryParams.value.storeId || (storeOptions.value[0] && storeOptions.value[0].storeId),
    name: undefined,
    sku: undefined,
    type: "simple",
    status: "publish",
    regularPrice: undefined,
    salePrice: undefined,
    stockQuantity: undefined,
    stockStatus: "instock",
    categories: undefined,
    imageUrl: undefined,
    description: undefined
  };
  proxy.resetForm("productRef");
}

/** 新增商品 */
function handleAdd() {
  resetForm();
  formTitle.value = "新增商品";
  formOpen.value = true;
}

/** 编辑商品 */
function handleEdit(row) {
  if (!row || !row.productId) {
    return;
  }
  detailOpen.value = false;
  resetForm();
  formTitle.value = "修改商品";
  formOpen.value = true;
  formLoading.value = true;
  getWooProductDetail(row.productId).then(response => {
    const item = response.data || {};
    form.value = {
      productId: item.productId,
      storeId: item.storeId,
      name: item.name,
      sku: item.sku,
      type: item.type || "simple",
      status: item.status || "publish",
      regularPrice: item.regularPrice == null ? 0 : Number(item.regularPrice),
      salePrice: item.salePrice == null ? 0 : Number(item.salePrice),
      stockQuantity: item.stockQuantity == null ? undefined : item.stockQuantity,
      stockStatus: item.stockStatus || "instock",
      categories: (item.categories || "").split(" / ").filter(Boolean).join(", "),
      imageUrl: item.imageUrl,
      description: item.description || ""
    };
  }).catch(() => {
    formOpen.value = false;
  }).finally(() => {
    formLoading.value = false;
  });
}

/** 提交商品 */
function submitForm() {
  proxy.$refs["productRef"].validate(valid => {
    if (!valid) {
      return;
    }
    saving.value = true;
    const payload = { ...form.value };
    const action = payload.productId ? updateWooProduct(payload) : addWooProduct(payload);
    action.then(() => {
      proxy.$modal.msgSuccess(payload.productId ? "商品已更新到店铺" : "商品已新增到店铺");
      formOpen.value = false;
      getList();
    }).catch(() => {}).finally(() => {
      saving.value = false;
    });
  });
}

/** 批量上传 */
function handleImport() {
  importRef.value.open();
}

/** 下载导入模板 */
function handleDownloadTemplate() {
  proxy.download("shop/woo/product/importTemplate", {}, `商品导入模板_${new Date().getTime()}.xlsx`);
}

/** 打开商品链接 */
function openLink(link) {
  window.open(link, "_blank");
}

/** 删除按钮操作（仅删除本地缓存） */
function handleDelete() {
  const productIds = ids.value;
  proxy.$modal.confirm('是否确认删除选中的本地商品记录？该操作不会删除Woo店铺中的商品，下次同步会重新拉取。').then(function () {
    return delWooProduct(productIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

getStoreOptions();
getList();
</script>

<style scoped>
.card-grid {
  min-height: 200px;
}

.product-card {
  margin-bottom: 16px;
  cursor: pointer;
  overflow: hidden;
}

.product-cover {
  position: relative;
  height: 150px;
  background: var(--el-fill-color-light);
}

.cover-img {
  display: block;
  width: 100%;
  height: 150px;
}

.cover-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 150px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.status-tag {
  position: absolute;
  top: 8px;
  right: 8px;
}

.product-info {
  padding: 10px 12px 0;
}

.product-name {
  overflow: hidden;
  font-size: 14px;
  font-weight: 600;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.product-sku {
  margin-top: 4px;
  overflow: hidden;
  font-size: 12px;
  color: var(--el-text-color-secondary);
  white-space: nowrap;
  text-overflow: ellipsis;
}

.product-price-row {
  display: flex;
  align-items: baseline;
  gap: 8px;
  margin-top: 8px;
}

.price {
  font-size: 16px;
  font-weight: 600;
  color: var(--el-color-danger);
}

.origin-price {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  text-decoration: line-through;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  margin-top: 6px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.product-store {
  margin-top: 4px;
  overflow: hidden;
  font-size: 12px;
  color: var(--el-text-color-secondary);
  white-space: nowrap;
  text-overflow: ellipsis;
}

.product-actions {
  display: flex;
  justify-content: flex-end;
  padding: 6px 12px 10px;
}

.detail-body {
  padding: 0 4px;
}

.detail-cover {
  display: block;
  width: 100%;
  max-height: 260px;
  border-radius: 6px;
  background: var(--el-fill-color-light);
}

.detail-name {
  margin: 14px 0 12px;
  font-size: 18px;
  line-height: 1.4;
}

.detail-section-title {
  margin: 18px 0 8px;
  font-weight: 600;
}

.detail-description {
  max-height: 220px;
  padding: 12px;
  overflow-y: auto;
  font-size: 13px;
  line-height: 1.8;
  color: var(--el-text-color-regular);
  white-space: pre-wrap;
  background: var(--el-fill-color-light);
  border-radius: 6px;
}

.detail-actions {
  margin-top: 18px;
  text-align: right;
}

.sub-text {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}
</style>
