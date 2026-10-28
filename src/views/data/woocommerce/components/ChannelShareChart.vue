<template>
  <div ref="chartRef" style="height: 360px; width: 100%"></div>
</template>

<script setup name="ChannelShareChart">
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
  const revenueData = rows.map(r => ({ name: r.channel, value: Number(r.revenue || 0) })).filter(d => d.value > 0);
  const ordersData = rows.map(r => ({ name: r.channel, value: Number(r.orders || 0) })).filter(d => d.value > 0);
  return {
    tooltip: { trigger: "item", formatter: "{a} <br/>{b}: {c} ({d}%)" },
    title: [
      { text: "销售额占比", left: "20%", top: 8, textAlign: "center", textStyle: { fontSize: 13 } },
      { text: "订单数占比", left: "75%", top: 8, textAlign: "center", textStyle: { fontSize: 13 } }
    ],
    legend: { type: "scroll", bottom: 0, left: "center" },
    series: [
      {
        name: "销售额",
        type: "pie",
        radius: ["35%", "60%"],
        center: ["25%", "52%"],
        data: revenueData,
        label: { show: false }
      },
      {
        name: "订单数",
        type: "pie",
        radius: ["35%", "60%"],
        center: ["78%", "52%"],
        data: ordersData,
        label: { show: false }
      }
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
