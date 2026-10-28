<template>
  <div>
    <!-- 筛选条件 -->
    <el-form :model="queryParams" :inline="true">
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
      <el-form-item label="Profile">
        <el-input v-model="queryParams.profileId" placeholder="广告档案/店铺标识(可选)" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 核心指标卡片 -->
    <el-row :gutter="16" class="metric-row">
      <el-col :xs="12" :sm="8" :md="4" v-for="item in metricCards" :key="item.label">
        <el-card shadow="hover" class="metric-card">
          <div class="metric-label">{{ item.label }}</div>
          <div class="metric-value">{{ item.value }}</div>
          <div class="metric-extra" v-if="item.extra">{{ item.extra }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 趋势图 -->
    <el-card shadow="never" class="chart-card">
      <template #header><span>花费 / 销售额 / ACOS 趋势</span></template>
      <trend-chart :data="trendData" />
    </el-card>

    <el-row :gutter="16">
      <el-col :xs="24" :lg="14">
        <el-card shadow="never" class="chart-card">
          <template #header><span>广告活动对比（按花费降序，前20）</span></template>
          <campaign-compare-chart :data="campaignData" />
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="10">
        <el-card shadow="never" class="chart-card">
          <template #header><span>花费 / 销售额占比</span></template>
          <campaign-share-chart :data="campaignData" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="DashboardTab">
import { getAdsSummary, getAdsTrend, getAdsCampaign } from "@/api/data/amazonAds";
import TrendChart from "./TrendChart.vue";
import CampaignCompareChart from "./CampaignCompareChart.vue";
import CampaignShareChart from "./CampaignShareChart.vue";

const dateRange = ref([]);
const queryParams = reactive({ profileId: "" });

const summary = ref({});
const trendData = ref([]);
const campaignData = ref([]);

// 默认近30天
function defaultDateRange() {
  const end = new Date();
  const start = new Date();
  start.setDate(start.getDate() - 29);
  const fmt = d => {
    const y = d.getFullYear();
    const m = String(d.getMonth() + 1).padStart(2, "0");
    const day = String(d.getDate()).padStart(2, "0");
    return `${y}-${m}-${day}`;
  };
  return [fmt(start), fmt(end)];
}

function buildQuery() {
  const query = {};
  if (queryParams.profileId) {
    query.profileId = queryParams.profileId;
  }
  if (dateRange.value && dateRange.value.length === 2) {
    query["params[beginTime]"] = dateRange.value[0];
    query["params[endTime]"] = dateRange.value[1];
  }
  return query;
}

function loadStats() {
  const query = buildQuery();
  getAdsSummary(query).then(res => {
    summary.value = res.data || {};
  });
  getAdsTrend(query).then(res => {
    trendData.value = res.data || [];
  });
  getAdsCampaign(query).then(res => {
    campaignData.value = res.data || [];
  });
}

function handleQuery() {
  loadStats();
}

function resetQuery() {
  queryParams.profileId = "";
  dateRange.value = defaultDateRange();
  loadStats();
}

const num = v => Number(v || 0);

const metricCards = computed(() => [
  { label: "总花费", value: num(summary.value.spend).toFixed(2) },
  { label: "总销售额", value: num(summary.value.sales).toFixed(2) },
  { label: "ACOS", value: summary.value.acos == null ? "-" : num(summary.value.acos) + "%", extra: summary.value.ctr != null ? "CTR " + num(summary.value.ctr) + "%" : "" },
  { label: "点击量", value: num(summary.value.clicks).toLocaleString(), extra: summary.value.cvr != null ? "CVR " + num(summary.value.cvr) + "%" : "" },
  { label: "曝光量", value: num(summary.value.impressions).toLocaleString() },
  { label: "订单数", value: num(summary.value.orders).toLocaleString() }
]);

onMounted(() => {
  dateRange.value = defaultDateRange();
  loadStats();
});
</script>

<style scoped>
.metric-row {
  margin-bottom: 16px;
}
.metric-card {
  margin-bottom: 12px;
}
.metric-label {
  font-size: 13px;
  color: var(--el-text-color-secondary);
}
.metric-value {
  font-size: 22px;
  font-weight: 600;
  margin-top: 6px;
}
.metric-extra {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  margin-top: 4px;
}
.chart-card {
  margin-bottom: 16px;
}
</style>
