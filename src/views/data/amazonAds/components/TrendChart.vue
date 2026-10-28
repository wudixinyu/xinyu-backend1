<template>
  <div ref="chartRef" style="height: 360px; width: 100%"></div>
</template>

<script setup name="TrendChart">
import * as echarts from "echarts";

const props = defineProps({
  data: {
    type: Array,
    default: () => []
  }
});

const chartRef = ref(null);
let chartInstance = null;

function buildOption() {
  const rows = props.data || [];
  const dates = rows.map(r => r.statDate);
  const spend = rows.map(r => Number(r.spend || 0));
  const sales = rows.map(r => Number(r.sales || 0));
  const acos = rows.map(r => (r.acos == null ? null : Number(r.acos)));
  return {
    tooltip: { trigger: "axis" },
    legend: { data: ["花费", "销售额", "ACOS"] },
    grid: { left: 50, right: 50, top: 40, bottom: 30 },
    xAxis: { type: "category", data: dates, boundaryGap: false },
    yAxis: [
      { type: "value", name: "金额" },
      { type: "value", name: "ACOS(%)", axisLabel: { formatter: "{value}%" }, splitLine: { show: false } }
    ],
    series: [
      { name: "花费", type: "line", smooth: true, areaStyle: { opacity: 0.15 }, data: spend },
      { name: "销售额", type: "line", smooth: true, areaStyle: { opacity: 0.15 }, data: sales },
      { name: "ACOS", type: "line", smooth: true, yAxisIndex: 1, data: acos, itemStyle: { color: "#e6a23c" }, lineStyle: { color: "#e6a23c" } }
    ]
  };
}

function render() {
  if (!chartRef.value) return;
  if (!chartInstance) {
    chartInstance = echarts.init(chartRef.value);
  }
  chartInstance.setOption(buildOption(), true);
}

function handleResize() {
  chartInstance && chartInstance.resize();
}

watch(() => props.data, () => {
  nextTick(render);
}, { deep: true });

onMounted(() => {
  nextTick(render);
  window.addEventListener("resize", handleResize);
});

onBeforeUnmount(() => {
  window.removeEventListener("resize", handleResize);
  if (chartInstance) {
    chartInstance.dispose();
    chartInstance = null;
  }
});
</script>
