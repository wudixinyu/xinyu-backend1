<template>
  <div class="app-container">
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="分析看板" name="dashboard">
        <dashboard-tab v-if="activated.dashboard" />
      </el-tab-pane>
      <el-tab-pane label="明细数据" name="detail">
        <detail-tab v-if="activated.detail" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup name="FacebookAds">
import DashboardTab from "./components/DashboardTab.vue";
import DetailTab from "./components/DetailTab.vue";

const activeTab = ref("dashboard");
// 首次激活后才渲染（图表需要在可见容器上初始化）
const activated = reactive({ dashboard: true, detail: false });

function handleTabChange(name) {
  activated[name] = true;
}
</script>
