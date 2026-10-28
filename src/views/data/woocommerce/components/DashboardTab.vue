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
      <el-form-item label="店铺域名">
        <el-input v-model="queryParams.storeDomain" placeholder="店铺域名(可选)" clearable style="width: 200px" @keyup.enter="handleQuery" />
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
      <template #header><span>销售额 / 订单数 / 转化率 趋势</span></template>
      <trend-chart :data="trendData" />
    </el-card>

    <el-row :gutter="16">
      <el-col :xs="24" :lg="14">
        <el-card shadow="never" class="chart-card">
          <template #header><span>销售渠道对比（按销售额降序，前20）</span></template>
          <channel-compare-chart :data="channelData" />
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="10">
        <el-card shadow="never" class="chart-card">
          <template #header><span>销售额 / 订单数占比</span></template>
          <channel-share-chart :data="channelData" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="DashboardTab">
import { getStoreSummary, getStoreTrend, getStoreChannel } from "@/api/data/woocommerce";
import TrendChart from "./TrendChart.vue";
import ChannelCompareChart from "./ChannelCompareChart.vue";
import ChannelShareChart from "./ChannelShareChart.vue";

const dateRange = ref([]);
const queryParams = reactive({ storeDomain: "" });

const summary = ref({});
const trendData = ref([]);
const channelData = ref([]);

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
  if (queryParams.storeDomain) {
    query.storeDomain = queryParams.storeDomain;
  }
  if (dateRange.value && dateRange.value.length === 2) {
    query["params[beginTime]"] = dateRange.value[0];
    query["params[endTime]"] = dateRange.value[1];
  }
  return query;
}

function loadStats() {
  const query = buildQuery();
  getStoreSummary(query).then(res => {
    summary.value = res.data || {};
  });
  getStoreTrend(query).then(res => {
    trendData.value = res.data || [];
  });
  getStoreChannel(query).then(res => {
    channelData.value = res.data || [];
  });
}

function handleQuery() {
  loadStats();
}

function resetQuery() {
  queryParams.storeDomain = "";
  dateRange.value = defaultDateRange();
  loadStats();
}

const num = v => Number(v || 0);

const metricCards = computed(() => [
  { label: "总销售额", value: num(summary.value.revenue).toFixed(2) },
  { label: "总订单数", value: num(summary.value.orders).toLocaleString() },
  { label: "转化率", value: summary.value.cvr == null ? "-" : num(summary.value.cvr) + "%", extra: summary.value.aov != null ? "客单价 " + num(summary.value.aov).toFixed(2) : "" },
  { label: "访客会话数", value: num(summary.value.sessions).toLocaleString() },
  { label: "售出件数", value: num(summary.value.units_sold).toLocaleString() },
  { label: "客单价", value: summary.value.aov == null ? "-" : num(summary.value.aov).toFixed(2) }
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
