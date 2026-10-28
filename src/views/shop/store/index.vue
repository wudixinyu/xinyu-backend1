<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="店铺名称" prop="storeName">
        <el-input
          v-model="queryParams.storeName"
          placeholder="请输入店铺名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 160px">
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
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['shop:store:add']">新增店铺</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['shop:store:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['shop:store:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="storeList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="店铺名称" align="center" prop="storeName" min-width="140" show-overflow-tooltip />
      <el-table-column label="店铺地址" align="center" prop="storeUrl" min-width="240" show-overflow-tooltip>
        <template #default="scope">
          <el-link :href="scope.row.storeUrl" target="_blank" type="primary">{{ scope.row.storeUrl }}</el-link>
        </template>
      </el-table-column>
      <el-table-column label="Consumer Key" align="center" prop="consumerKey" width="180" show-overflow-tooltip>
        <template #default="scope">{{ maskKey(scope.row.consumerKey) }}</template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template #default="scope">
          <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="排序" align="center" prop="sort" width="70" />
      <el-table-column label="最近同步" align="center" prop="lastSyncTime" width="160">
        <template #default="scope">
          <span>{{ scope.row.lastSyncTime ? parseTime(scope.row.lastSyncTime) : "-" }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="240" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button
            link
            type="primary"
            icon="Connection"
            :loading="testingId === scope.row.storeId"
            @click="handleTest(scope.row)"
            v-hasPermi="['shop:store:test']"
          >测试连接</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['shop:store:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['shop:store:remove']">删除</el-button>
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

    <!-- 添加或修改店铺对话框 -->
    <el-dialog :title="title" v-model="open" width="680px" append-to-body>
      <el-form ref="storeRef" :model="form" :rules="rules" label-width="150px">
        <el-form-item label="店铺名称" prop="storeName">
          <el-input v-model="form.storeName" placeholder="请输入店铺名称" maxlength="100" />
        </el-form-item>
        <el-form-item label="店铺地址" prop="storeUrl">
          <el-input v-model="form.storeUrl" placeholder="如 https://shop.example.com（无需带 /wp-json）" maxlength="255" />
        </el-form-item>
        <el-form-item label="Consumer Key" prop="consumerKey">
          <el-input v-model="form.consumerKey" placeholder="WooCommerce REST API 消费者密钥" maxlength="255" />
        </el-form-item>
        <el-form-item label="Consumer Secret" prop="consumerSecret">
          <el-input v-model="form.consumerSecret" placeholder="WooCommerce REST API 消费者密钥" maxlength="255" show-password />
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="显示排序" prop="sort">
              <el-input-number v-model="form.sort" :min="0" :max="9999" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio v-for="dict in sys_normal_disable" :key="dict.value" :value="dict.value">{{ dict.label }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" maxlength="500" />
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

<script setup name="ShopStore">
import { listShopStore, getShopStore, addShopStore, updateShopStore, delShopStore, testShopStore } from "@/api/shop";

const { proxy } = getCurrentInstance();
const { sys_normal_disable } = proxy.useDict("sys_normal_disable");

const storeList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const testingId = ref(null);

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    storeName: undefined,
    status: undefined
  },
  rules: {
    storeName: [{ required: true, message: "店铺名称不能为空", trigger: "blur" }],
    storeUrl: [
      { required: true, message: "店铺地址不能为空", trigger: "blur" },
      { pattern: /^(https?:\/\/)?[\w.-]+(:\d+)?(\/.*)?$/, message: "请输入正确的店铺地址", trigger: "blur" }
    ]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询店铺列表 */
function getList() {
  loading.value = true;
  listShopStore(queryParams.value).then(response => {
    storeList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

/** 密钥脱敏展示 */
function maskKey(value) {
  if (!value) {
    return "未配置";
  }
  if (value.length <= 8) {
    return "****";
  }
  return value.substring(0, 4) + "****" + value.substring(value.length - 4);
}

/** 取消按钮 */
function cancel() {
  open.value = false;
  reset();
}

/** 表单重置 */
function reset() {
  form.value = {
    storeId: undefined,
    storeName: undefined,
    storeUrl: undefined,
    consumerKey: undefined,
    consumerSecret: undefined,
    status: "0",
    sort: 0,
    remark: undefined
  };
  proxy.resetForm("storeRef");
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
  ids.value = selection.map(item => item.storeId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "新增店铺";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const storeId = row.storeId || ids.value[0];
  getShopStore(storeId).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "修改店铺";
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["storeRef"].validate(valid => {
    if (valid) {
      if (form.value.storeId != undefined) {
        updateShopStore(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addShopStore(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  const storeIds = row.storeId || ids.value;
  proxy.$modal.confirm('是否确认删除选中的店铺？删除后已同步的订单/商品/文章不再更新，可在对应页面手动清理。').then(function () {
    return delShopStore(storeIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

/** 测试连接 */
function handleTest(row) {
  testingId.value = row.storeId;
  testShopStore(row.storeId).then(response => {
    proxy.$modal.msgSuccess(response.msg || "连接成功");
  }).finally(() => {
    testingId.value = null;
  });
}

getList();
</script>
