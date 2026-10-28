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
  const revenue = rows.map(r => Number(r.revenue || 0));
  const orders = rows.map(r => Number(r.orders || 0));
  const cvr = rows.map(r => (r.cvr == null ? null : Number(r.cvr)));
  return {
    tooltip: { trigger: "axis" },
    legend: { data: ["销售额", "订单数", "转化率"] },
    grid: { left: 50, right: 50, top: 40, bottom: 30 },
    xAxis: { type: "category", data: dates, boundaryGap: false },
    yAxis: [
      { type: "value", name: "金额/数量" },
      { type: "value", name: "转化率(%)", axisLabel: { formatter: "{value}%" }, splitLine: { show: false } }
    ],
    series: [
      { name: "销售额", type: "line", smooth: true, areaStyle: { opacity: 0.15 }, data: revenue },
      { name: "订单数", type: "line", smooth: true, areaStyle: { opacity: 0.15 }, data: orders },
      { name: "转化率", type: "line", smooth: true, yAxisIndex: 1, data: cvr, itemStyle: { color: "#e6a23c" }, lineStyle: { color: "#e6a23c" } }
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
