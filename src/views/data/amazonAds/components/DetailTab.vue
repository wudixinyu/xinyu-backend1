<template>
  <div>
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="日期范围">
        <el-date-picker
          v-model="dateRange"
          value-format="YYYY-MM-DD"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        />
      </el-form-item>
      <el-form-item label="广告活动" prop="campaignName">
        <el-input v-model="queryParams.campaignName" placeholder="请输入广告活动名称" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="Profile" prop="profileId">
        <el-input v-model="queryParams.profileId" placeholder="请输入广告档案/店铺标识" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['data:amazonAds:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['data:amazonAds:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['data:amazonAds:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain icon="Upload" @click="handleImport" v-hasPermi="['data:amazonAds:import']">导入</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['data:amazonAds:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="adsList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="日期" align="center" prop="reportDate" width="110" />
      <el-table-column label="Profile" align="center" prop="profileId" width="110" :show-overflow-tooltip="true" />
      <el-table-column label="广告活动" align="center" prop="campaignName" min-width="180" :show-overflow-tooltip="true" />
      <el-table-column label="曝光量" align="center" prop="impressions" width="100" />
      <el-table-column label="点击量" align="center" prop="clicks" width="90" />
      <el-table-column label="花费" align="center" prop="spend" width="100" />
      <el-table-column label="销售额" align="center" prop="sales" width="100" />
      <el-table-column label="订单数" align="center" prop="orders" width="90" />
      <el-table-column label="ACOS" align="center" width="90">
        <template #default="scope">{{ rate(scope.row.spend, scope.row.sales) }}</template>
      </el-table-column>
      <el-table-column label="CTR" align="center" width="90">
        <template #default="scope">{{ rate(scope.row.clicks, scope.row.impressions) }}</template>
      </el-table-column>
      <el-table-column label="CVR" align="center" width="90">
        <template #default="scope">{{ rate(scope.row.orders, scope.row.clicks) }}</template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="150" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['data:amazonAds:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['data:amazonAds:remove']">删除</el-button>
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

    <!-- 添加或修改广告日报对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="adsRef" :model="form" :rules="rules" label-width="90px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="日期" prop="reportDate">
              <el-date-picker v-model="form.reportDate" value-format="YYYY-MM-DD" type="date" placeholder="请选择日期" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Profile" prop="profileId">
              <el-input v-model="form.profileId" placeholder="广告档案/店铺标识(可选)" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="广告活动" prop="campaignName">
              <el-input v-model="form.campaignName" placeholder="请输入广告活动名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="曝光量" prop="impressions">
              <el-input-number v-model="form.impressions" :min="0" :precision="0" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="点击量" prop="clicks">
              <el-input-number v-model="form.clicks" :min="0" :precision="0" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="花费" prop="spend">
              <el-input-number v-model="form.spend" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="销售额" prop="sales">
              <el-input-number v-model="form.sales" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="订单数" prop="orders">
              <el-input-number v-model="form.orders" :min="0" :precision="0" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
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

    <!-- 数据导入对话框 -->
    <excel-import-dialog
      ref="importRef"
      title="亚马逊广告数据导入"
      action="/data/amazonAds/importData"
      template-action="/data/amazonAds/importTemplate"
      template-file-name="amazon_ads_template"
      accept=".xlsx, .xls, .csv"
      :extensions="['xlsx', 'xls', 'csv']"
      @success="getList"
    />
  </div>
</template>

<script setup name="DetailTab">
import { listAmazonAds, getAmazonAds, delAmazonAds, addAmazonAds, updateAmazonAds } from "@/api/data/amazonAds";
import ExcelImportDialog from "@/components/ExcelImportDialog";

const { proxy } = getCurrentInstance();

const adsList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const dateRange = ref([]);
const importRef = ref(null);

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    campaignName: undefined,
    profileId: undefined
  },
  rules: {
    reportDate: [{ required: true, message: "日期不能为空", trigger: "change" }],
    campaignName: [{ required: true, message: "广告活动名称不能为空", trigger: "blur" }]
  }
});

const { queryParams, form, rules } = toRefs(data);

// 派生指标（分母为0显示 -）
function rate(numerator, denominator) {
  const n = Number(numerator || 0);
  const d = Number(denominator || 0);
  return d > 0 ? (n / d * 100).toFixed(2) + "%" : "-";
}

/** 查询广告日报列表 */
function getList() {
  loading.value = true;
  listAmazonAds(proxy.addDateRange(queryParams.value, dateRange.value)).then(response => {
    adsList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

/** 取消按钮 */
function cancel() {
  open.value = false;
  reset();
}

/** 表单重置 */
function reset() {
  form.value = {
    dailyId: undefined,
    reportDate: undefined,
    profileId: undefined,
    campaignName: undefined,
    impressions: 0,
    clicks: 0,
    spend: 0,
    sales: 0,
    orders: 0,
    remark: undefined
  };
  proxy.resetForm("adsRef");
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
  ids.value = selection.map(item => item.dailyId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加广告日报";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const dailyId = row.dailyId || ids.value;
  getAmazonAds(dailyId).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "修改广告日报";
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["adsRef"].validate(valid => {
    if (valid) {
      if (form.value.dailyId != undefined) {
        updateAmazonAds(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addAmazonAds(form.value).then(response => {
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
  const dailyIds = row.dailyId || ids.value;
  proxy.$modal.confirm('是否确认删除选中的广告日报数据？').then(function () {
    return delAmazonAds(dailyIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

/** 导入按钮操作 */
function handleImport() {
  importRef.value.open();
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download("data/amazonAds/export", {
    ...proxy.addDateRange(queryParams.value, dateRange.value)
  }, `amazon_ads_${new Date().getTime()}.xlsx`);
}

getList();
</script>
