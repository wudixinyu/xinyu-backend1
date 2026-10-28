<template>
  <div class="big-screen" ref="screenRef">
    <!-- 顶部标题栏 -->
    <header class="bs-header">
      <div class="bs-h-left">
        <div class="bs-logo">YG</div>
        <div>
          <div class="bs-sys">若依管理系统</div>
          <div class="bs-sys-sub">YAOGO DATA PLATFORM</div>
        </div>
      </div>
      <div class="bs-h-title">
        <span class="bs-title-line left"></span>
        <h1>数 据 驾 驶 舱</h1>
        <span class="bs-title-line right"></span>
      </div>
      <div class="bs-h-right">
        <div class="bs-clock">{{ nowTime }}</div>
        <div class="bs-date">{{ nowDate }}</div>
        <button class="bs-fs-btn" :title="isFullscreen ? '退出全屏' : '全屏显示'" @click="toggleFullscreen">
          <svg v-if="!isFullscreen" viewBox="0 0 1024 1024" width="16" height="16"><path fill="currentColor" d="M332.8 704H224v108.8c0 19.2-12.8 32-32 32s-32-12.8-32-32V672c0-19.2 12.8-32 32-32h140.8c19.2 0 32 12.8 32 32s-12.8 32-32 32zM352 224V112c0-19.2-12.8-32-32-32s-32 12.8-32 32v140.8c0 19.2 12.8 32 32 32h140.8c19.2 0 32-12.8 32-32s-12.8-32-32-32H352zM704 332.8V224h108.8c19.2 0 32-12.8 32-32s-12.8-32-32-32H672c-19.2 0-32 12.8-32 32v140.8c0 19.2 12.8 32 32 32s32-12.8 32-32zM691.2 672H550.4c-19.2 0-32 12.8-32 32s12.8 32 32 32H704v108.8c0 19.2 12.8 32 32 32s32-12.8 32-32V704c0-19.2-12.8-32-32-32z"/></svg>
          <svg v-else viewBox="0 0 1024 1024" width="16" height="16"><path fill="currentColor" d="M396.8 352H288c-19.2 0-32 12.8-32 32s12.8 32 32 32h140.8c19.2 0 32-12.8 32-32V275.2c0-19.2-12.8-32-32-32s-32 12.8-32 32V352zM396.8 640V748.8c0 19.2 12.8 32 32 32s32-12.8 32-32V608c0-19.2-12.8-32-32-32H288c-19.2 0-32 12.8-32 32s12.8 32 32 32h108.8zM627.2 352V243.2c0-19.2-12.8-32-32-32s-32 12.8-32 32v140.8c0 19.2 12.8 32 32 32h140.8c19.2 0 32-12.8 32-32s-12.8-32-32-32H627.2zM627.2 640h108.8c19.2 0 32-12.8 32-32s-12.8-32-32-32H595.2c-19.2 0-32 12.8-32 32v140.8c0 19.2 12.8 32 32 32s32-12.8 32-32V640z"/></svg>
        </button>
      </div>
    </header>

    <!-- 核心指标 KPI -->
    <section class="bs-kpis">
      <div class="bs-kpi" v-for="k in kpiCards" :key="k.label">
        <div class="bs-kpi-label">{{ k.label }}</div>
        <div class="bs-kpi-value" :style="{ color: k.color }">
          {{ k.value }}<span class="bs-kpi-unit" v-if="k.unit">{{ k.unit }}</span>
        </div>
        <div class="bs-kpi-sub">{{ k.sub }}</div>
      </div>
    </section>

    <!-- 主体图表面板 -->
    <section class="bs-main">
      <!-- 服务器状态 -->
      <div class="bs-panel area-server">
        <div class="bs-panel-head"><span class="head-bar"></span><span class="head-title">服务器状态</span></div>
        <div class="bs-panel-body">
          <template v-if="can.server && serverData">
            <div class="bs-gauges">
              <div ref="cpuRef" class="bs-gauge"></div>
              <div ref="memRef" class="bs-gauge"></div>
            </div>
            <ul class="bs-srv-info">
              <li><span>CPU</span><i>{{ serverData.cpu.cpuNum }} 核心 · 使用率 {{ serverData.cpu.used }}%</i></li>
              <li><span>内存</span><i>{{ serverData.mem.used }}G / {{ serverData.mem.total }}G</i></li>
              <li><span>JVM</span><i>{{ serverData.jvm.name }} · 已运行 {{ serverData.jvm.runTime }}</i></li>
              <li><span>主机</span><i>{{ serverData.sys.computerIp }} · {{ serverData.sys.osName }}</i></li>
              <li v-if="cacheData"><span>Redis</span><i>{{ cacheData.info.redis_version }} · 内存 {{ cacheData.info.used_memory_human }} · 连接 {{ cacheData.info.connected_clients }}</i></li>
              <li v-if="cacheData"><span>缓存</span><i>{{ cacheData.dbSize }} 个键 · 已运行 {{ cacheData.info.uptime_in_days }} 天</i></li>
            </ul>
          </template>
          <div v-else class="bs-empty">暂无数据或无访问权限</div>
        </div>
      </div>

      <!-- 广告投放趋势 -->
      <div class="bs-panel area-trend">
        <div class="bs-panel-head">
          <span class="head-bar"></span><span class="head-title">广告投放趋势</span>
          <span class="head-extra">近 30 天</span>
        </div>
        <div class="bs-panel-body">
          <div v-show="can.ads" ref="trendRef" class="bs-chart"></div>
          <div v-if="!can.ads" class="bs-empty">暂无数据或无访问权限</div>
        </div>
      </div>

      <!-- 广告花费占比 -->
      <div class="bs-panel area-share">
        <div class="bs-panel-head">
          <span class="head-bar"></span><span class="head-title">广告活动花费占比</span>
        </div>
        <div class="bs-panel-body">
          <div v-show="can.ads" ref="shareRef" class="bs-chart"></div>
          <div v-if="!can.ads" class="bs-empty">暂无数据或无访问权限</div>
        </div>
      </div>

      <!-- 卡片大类分布 -->
      <div class="bs-panel area-card">
        <div class="bs-panel-head"><span class="head-bar"></span><span class="head-title">卡片大类分布</span></div>
        <div class="bs-panel-body">
          <div v-show="can.card" ref="cardKindRef" class="bs-chart"></div>
          <div v-if="!can.card" class="bs-empty">暂无数据或无访问权限</div>
        </div>
      </div>

      <!-- 广告活动对比 -->
      <div class="bs-panel area-campaign">
        <div class="bs-panel-head">
          <span class="head-bar"></span><span class="head-title">广告活动花费 / 销售额对比</span>
          <span class="head-extra">TOP 10</span>
        </div>
        <div class="bs-panel-body">
          <div v-show="can.ads" ref="campaignRef" class="bs-chart"></div>
          <div v-if="!can.ads" class="bs-empty">暂无数据或无访问权限</div>
        </div>
      </div>

      <!-- 更新日志 -->
      <div class="bs-panel area-logs">
        <div class="bs-panel-head">
          <span class="head-bar"></span><span class="head-title">系统更新日志</span>
        </div>
        <div class="bs-panel-body">
          <ul v-if="logs.length" class="bs-logs">
            <li v-for="(l, i) in logs" :key="i">
              <div class="bs-log-top">
                <span class="bs-log-ver">{{ l.version }}</span>
                <span class="bs-log-date">{{ parseTime(l.createTime, '{y}-{m}-{d}') }}</span>
              </div>
              <div class="bs-log-content">{{ l.content }}</div>
            </li>
          </ul>
          <div v-else class="bs-empty">暂无更新日志</div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup name="Index">
import * as echarts from 'echarts'
import { checkPermi } from '@/utils/permission'
import { getAdsSummary, getAdsTrend, getAdsCampaign } from '@/api/data/amazonAds'
import { getServer } from '@/api/monitor/server'
import { getCache } from '@/api/monitor/cache'
import { list as listOnline } from '@/api/monitor/online'
import { listInfo } from '@/api/card/info'
import { listModel } from '@/api/ai/model'
import { listPrompt } from '@/api/ai/prompt'
import { listSkill } from '@/api/ai/skill'
import { listKb } from '@/api/ai/kb'
import { listLog } from '@/api/update/log'
import useSettingsStore from '@/store/modules/settings'

const { yg_card_kind } = useDict('yg_card_kind')
const { parseTime } = getCurrentInstance().appContext.config.globalProperties
const settingsStore = useSettingsStore()
// 是否暗黑模式: 直接监听 html.dark 类, 任何切换路径都能跟随
const isDark = ref(document.documentElement.classList.contains('dark'))
let themeObserver = null

// 各模块查看权限
const can = {
  ads: checkPermi(['data:amazonAds:list']),
  server: checkPermi(['monitor:server:list']),
  cache: checkPermi(['monitor:cache:list']),
  online: checkPermi(['monitor:online:list']),
  card: checkPermi(['card:info:list']),
  ai: checkPermi(['ai:model:list']),
  log: checkPermi(['update:log:list'])
}

/* ---------------- 时钟 ---------------- */
const now = ref(new Date())
let clockTimer = null
const weekMap = ['日', '一', '二', '三', '四', '五', '六']
const nowTime = computed(() => {
  const d = now.value
  const p = n => String(n).padStart(2, '0')
  return `${p(d.getHours())}:${p(d.getMinutes())}:${p(d.getSeconds())}`
})
const nowDate = computed(() => {
  const d = now.value
  return `${d.getFullYear()}年${String(d.getMonth() + 1).padStart(2, '0')}月${String(d.getDate()).padStart(2, '0')}日 星期${weekMap[d.getDay()]}`
})

/* ---------------- 数据状态 ---------------- */
const ads = reactive({ summary: {}, trend: [], campaign: [] })
const serverData = ref(null)
const cacheData = ref(null)
const onlineTotal = ref(0)
const cardTotal = ref(0)
const cardKindData = ref([])
const aiStat = reactive({ model: 0, prompt: 0, skill: 0, kb: 0 })
const logs = ref([])

const num = v => Number(v || 0)

/* ---------------- KPI 卡片 ---------------- */
const kpiCards = computed(() => {
  const s = ads.summary
  const k = ct.value.kpi
  const roi = num(s.spend) > 0 ? (num(s.sales) / num(s.spend)).toFixed(2) : '-'
  const latest = logs.value[0]
  return [
    { label: '广告总花费', value: can.ads ? num(s.spend).toFixed(2) : '-', unit: '', color: k[0], sub: '近30天累计' },
    { label: '广告总销售额', value: can.ads ? num(s.sales).toFixed(2) : '-', unit: '', color: k[1], sub: roi === '-' ? '近30天累计' : `ROI ${roi}` },
    { label: 'ACOS', value: can.ads ? (s.acos == null ? '-' : num(s.acos)) : '-', unit: can.ads && s.acos != null ? '%' : '', color: k[2], sub: s.ctr != null ? `CTR ${num(s.ctr)}%` : '近30天' },
    { label: '广告订单', value: can.ads ? num(s.orders).toLocaleString() : '-', unit: '', color: k[3], sub: s.clicks != null ? `点击 ${num(s.clicks).toLocaleString()}` : '近30天' },
    { label: '卡片总数', value: can.card ? num(cardTotal.value).toLocaleString() : '-', unit: '', color: k[4], sub: cardKindData.value.length ? cardKindData.value.map(c => `${c.name}${c.value}`).join(' · ') : '卡牌库规模' },
    { label: 'AI 模型', value: can.ai ? num(aiStat.model) : '-', unit: '', color: k[5], sub: `提示词 ${aiStat.prompt} · 技能 ${aiStat.skill} · 知识库 ${aiStat.kb}` },
    { label: '在线用户', value: can.online ? num(onlineTotal.value) : '-', unit: '', color: k[6], sub: '当前活跃会话' },
    { label: '系统版本', value: latest ? latest.version : 'v3.9.2', unit: '', color: k[7], sub: latest ? `更新于 ${parseTime(latest.createTime, '{y}-{m}-{d}')}` : '若依后台框架' }
  ]
})

/* ---------------- 图表 ---------------- */
const screenRef = ref(null)
const trendRef = ref(null)
const campaignRef = ref(null)
const shareRef = ref(null)
const cardKindRef = ref(null)
const cpuRef = ref(null)
const memRef = ref(null)

const charts = {}
function chartOf(key, el) {
  if (!el) return null
  if (!charts[key]) charts[key] = echarts.init(el)
  return charts[key]
}

// 图表配色(亮/暗两套, 跟随主题切换)
const PALETTE = {
  dark: {
    axis: '#a3a6ad',
    axisLine: '#434343',
    split: 'rgba(255, 255, 255, 0.06)',
    tooltipBg: '#1d1e1f',
    tooltipBorder: '#434343',
    tooltipText: '#d0d0d0',
    pieBorder: '#1d1e1f',
    pieLine: '#434343',
    empty: '#8d9095',
    label: '#d0d0d0',
    spend: '#00d4ff',
    sales: '#22c55e',
    acos: '#f59e0b',
    spendArea: ['rgba(0,212,255,0.28)', 'rgba(0,212,255,0)'],
    salesArea: ['rgba(34,197,94,0.22)', 'rgba(34,197,94,0)'],
    spendBar: ['#0077b6', '#00d4ff'],
    salesBar: ['#14532d', '#22c55e'],
    gaugeTrack: 'rgba(255, 255, 255, 0.08)',
    kpi: ['#00d4ff', '#22c55e', '#f59e0b', '#a78bfa', '#38bdf8', '#f472b6', '#34d399', '#e2e8f0']
  },
  light: {
    axis: '#64748b',
    axisLine: '#cbd5e1',
    split: '#eef2f7',
    tooltipBg: '#ffffff',
    tooltipBorder: '#e2e8f0',
    tooltipText: '#334155',
    pieBorder: '#ffffff',
    pieLine: '#cbd5e1',
    empty: '#94a3b8',
    label: '#475569',
    spend: '#0e7490',
    sales: '#16a34a',
    acos: '#d97706',
    spendArea: ['rgba(14,116,144,0.18)', 'rgba(14,116,144,0)'],
    salesArea: ['rgba(22,163,74,0.16)', 'rgba(22,163,74,0)'],
    spendBar: ['#0e7490', '#22d3ee'],
    salesBar: ['#15803d', '#4ade80'],
    gaugeTrack: 'rgba(100, 116, 139, 0.18)',
    kpi: ['#0e7490', '#16a34a', '#d97706', '#7c3aed', '#0284c7', '#db2777', '#059669', '#334155']
  }
}
const ct = computed(() => (isDark.value ? PALETTE.dark : PALETTE.light))

const PIE_COLORS = ['#00d4ff', '#3b82f6', '#22c55e', '#f59e0b', '#a78bfa', '#f472b6', '#38bdf8', '#64748b']

function axisLabel(t) {
  return { color: t.axis, fontSize: 11 }
}
function tooltipBase(t) {
  return {
    trigger: 'axis',
    backgroundColor: t.tooltipBg,
    borderColor: t.tooltipBorder,
    textStyle: { color: t.tooltipText, fontSize: 12 }
  }
}

function emptyTitle(show, t) {
  return {
    show,
    text: '暂无数据',
    left: 'center',
    top: 'center',
    textStyle: { color: t.empty, fontSize: 14, fontWeight: 'normal' }
  }
}

function renderTrend() {
  const c = chartOf('trend', trendRef.value)
  if (!c) return
  const t = ct.value
  const AXIS_LABEL = axisLabel(t)
  const SPLIT_LINE = { lineStyle: { color: t.split } }
  const rows = ads.trend || []
  c.setOption({
    title: emptyTitle(rows.length === 0, t),
    tooltip: tooltipBase(t),
    legend: { data: ['花费', '销售额', 'ACOS'], textStyle: { color: t.axis }, top: 4 },
    grid: { left: 10, right: 14, top: 36, bottom: 4, containLabel: true },
    xAxis: {
      type: 'category',
      data: rows.map(r => r.statDate),
      boundaryGap: false,
      axisLabel: AXIS_LABEL,
      axisLine: { lineStyle: { color: t.axisLine } }
    },
    yAxis: [
      { type: 'value', name: '金额', nameTextStyle: AXIS_LABEL, axisLabel: AXIS_LABEL, splitLine: SPLIT_LINE },
      { type: 'value', name: 'ACOS(%)', nameTextStyle: AXIS_LABEL, axisLabel: { ...AXIS_LABEL, formatter: '{value}%' }, splitLine: { show: false } }
    ],
    series: [
      { name: '花费', type: 'line', smooth: true, symbol: 'none', data: rows.map(r => num(r.spend)), lineStyle: { color: t.spend }, itemStyle: { color: t.spend }, areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: t.spendArea[0] }, { offset: 1, color: t.spendArea[1] }]) } },
      { name: '销售额', type: 'line', smooth: true, symbol: 'none', data: rows.map(r => num(r.sales)), lineStyle: { color: t.sales }, itemStyle: { color: t.sales }, areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: t.salesArea[0] }, { offset: 1, color: t.salesArea[1] }]) } },
      { name: 'ACOS', type: 'line', smooth: true, symbol: 'none', yAxisIndex: 1, data: rows.map(r => (r.acos == null ? null : num(r.acos))), lineStyle: { color: t.acos }, itemStyle: { color: t.acos } }
    ]
  }, true)
}

function renderCampaign() {
  const c = chartOf('campaign', campaignRef.value)
  if (!c) return
  const t = ct.value
  const AXIS_LABEL = axisLabel(t)
  const SPLIT_LINE = { lineStyle: { color: t.split } }
  const rows = (ads.campaign || []).slice(0, 10)
  const names = rows.map(r => r.campaignName).reverse()
  c.setOption({
    title: emptyTitle(rows.length === 0, t),
    tooltip: { ...tooltipBase(t), axisPointer: { type: 'shadow' } },
    legend: { data: ['花费', '销售额'], textStyle: { color: t.axis }, top: 4 },
    grid: { left: 10, right: 20, top: 36, bottom: 4, containLabel: true },
    xAxis: { type: 'value', axisLabel: AXIS_LABEL, splitLine: SPLIT_LINE },
    yAxis: {
      type: 'category',
      data: names,
      axisLabel: { ...AXIS_LABEL, width: 130, overflow: 'truncate' },
      axisLine: { lineStyle: { color: t.axisLine } }
    },
    series: [
      { name: '花费', type: 'bar', barWidth: 8, data: rows.map(r => num(r.spend)).reverse(), itemStyle: { borderRadius: 4, color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [{ offset: 0, color: t.spendBar[0] }, { offset: 1, color: t.spendBar[1] }]) } },
      { name: '销售额', type: 'bar', barWidth: 8, data: rows.map(r => num(r.sales)).reverse(), itemStyle: { borderRadius: 4, color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [{ offset: 0, color: t.salesBar[0] }, { offset: 1, color: t.salesBar[1] }]) } }
    ]
  }, true)
}

function renderShare() {
  const c = chartOf('share', shareRef.value)
  if (!c) return
  const t = ct.value
  const rows = ads.campaign || []
  const top = rows.slice(0, 6)
  const otherSpend = rows.slice(6).reduce((sum, r) => sum + num(r.spend), 0)
  const data = top.map(r => ({ name: r.campaignName, value: num(r.spend) }))
  if (otherSpend > 0) data.push({ name: '其他', value: Number(otherSpend.toFixed(2)) })
  c.setOption({
    title: emptyTitle(data.length === 0, t),
    color: PIE_COLORS,
    tooltip: { trigger: 'item', ...tooltipBase(t), formatter: p => `${p.name}<br/>花费 ${num(p.value).toFixed(2)} (${p.percent}%)` },
    legend: { type: 'scroll', bottom: 0, textStyle: { color: t.axis, fontSize: 11 }, itemWidth: 10, itemHeight: 10 },
    series: [{
      type: 'pie',
      radius: ['42%', '66%'],
      center: ['50%', '44%'],
      data,
      label: { color: t.axis, fontSize: 11, formatter: '{d}%' },
      labelLine: { lineStyle: { color: t.pieLine } },
      itemStyle: { borderColor: t.pieBorder, borderWidth: 2 }
    }]
  }, true)
}

function renderCardKind() {
  const c = chartOf('cardKind', cardKindRef.value)
  if (!c) return
  const t = ct.value
  const data = cardKindData.value
  c.setOption({
    title: emptyTitle(data.length === 0, t),
    color: ['#3b82f6', '#22c55e', '#ef4444', '#f59e0b'],
    tooltip: { trigger: 'item', ...tooltipBase(t) },
    legend: { bottom: 0, textStyle: { color: t.axis }, itemWidth: 10, itemHeight: 10 },
    series: [{
      type: 'pie',
      radius: ['45%', '68%'],
      center: ['50%', '44%'],
      data,
      label: { color: t.label, fontSize: 11, formatter: '{b}\n{c}' },
      labelLine: { lineStyle: { color: t.pieLine } },
      itemStyle: { borderColor: t.pieBorder, borderWidth: 2 }
    }]
  }, true)
}

function gaugeOption(name, value, color, t) {
  return {
    series: [{
      type: 'gauge',
      startAngle: 210,
      endAngle: -30,
      min: 0,
      max: 100,
      radius: '92%',
      center: ['50%', '58%'],
      progress: { show: true, width: 7, roundCap: true, itemStyle: { color } },
      axisLine: { lineStyle: { width: 7, color: [[1, t.gaugeTrack]] } },
      axisTick: { show: false },
      splitLine: { show: false },
      axisLabel: { show: false },
      pointer: { show: false },
      title: { offsetCenter: [0, '32%'], fontSize: 12, color: t.axis },
      detail: { offsetCenter: [0, '-8%'], fontSize: 18, fontWeight: 600, color, formatter: '{value}%' },
      data: [{ value, name }]
    }]
  }
}

function renderServer() {
  const s = serverData.value
  if (!s) return
  const t = ct.value
  const cpu = chartOf('cpu', cpuRef.value)
  cpu && cpu.setOption(gaugeOption('CPU', num(s.cpu.used), t.spend, t), true)
  const mem = chartOf('mem', memRef.value)
  mem && mem.setOption(gaugeOption('内存', num(s.mem.usage), t.sales, t), true)
}

/* ---------------- 数据加载 ---------------- */
function defaultDateRange() {
  const end = new Date()
  const start = new Date()
  start.setDate(start.getDate() - 29)
  const fmt = d => `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
  return [fmt(start), fmt(end)]
}

function loadAds() {
  if (!can.ads) return
  const [begin, end] = defaultDateRange()
  const query = { 'params[beginTime]': begin, 'params[endTime]': end }
  getAdsSummary(query).then(res => { ads.summary = res.data || {} })
  getAdsTrend(query).then(res => { ads.trend = res.data || []; nextTick(renderTrend) })
  getAdsCampaign(query).then(res => { ads.campaign = res.data || []; nextTick(() => { renderCampaign(); renderShare() }) })
}

function loadServer() {
  if (!can.server) return
  getServer().then(res => { serverData.value = res.data; nextTick(renderServer) })
}

function loadCache() {
  if (!can.cache) return
  getCache().then(res => { cacheData.value = res.data })
}

function loadOnline() {
  if (!can.online) return
  listOnline({ pageNum: 1, pageSize: 1 }).then(res => { onlineTotal.value = res.total || 0 })
}

function loadCards() {
  if (!can.card) return
  listInfo({ pageNum: 1, pageSize: 1 }).then(res => { cardTotal.value = res.total || 0 })
  loadCardKinds()
}

// 字典为异步加载，watch 到 yg_card_kind 就绪后再统计分布
function loadCardKinds() {
  if (!can.card) return
  const kinds = yg_card_kind.value || []
  if (!kinds.length) return
  Promise.all(
    kinds.map(d => listInfo({ pageNum: 1, pageSize: 1, cardKind: d.value }).then(res => ({ name: d.label, value: res.total || 0 })))
  ).then(list => {
    cardKindData.value = list
    nextTick(renderCardKind)
  })
}

watch(() => yg_card_kind.value.length, len => { if (len > 0 && !cardKindData.value.length) loadCardKinds() })

// 主题切换时按新配色重绘全部图表
watch(isDark, () => {
  renderTrend()
  renderCampaign()
  renderShare()
  renderCardKind()
  renderServer()
})

function loadAi() {
  if (!can.ai) return
  listModel({ pageNum: 1, pageSize: 1 }).then(res => { aiStat.model = res.total || 0 })
  checkPermi(['ai:prompt:list']) && listPrompt({ pageNum: 1, pageSize: 1 }).then(res => { aiStat.prompt = res.total || 0 })
  checkPermi(['ai:skill:list']) && listSkill({ pageNum: 1, pageSize: 1 }).then(res => { aiStat.skill = res.total || 0 })
  checkPermi(['ai:kb:list']) && listKb({ pageNum: 1, pageSize: 1 }).then(res => { aiStat.kb = res.total || 0 })
}

function loadLogs() {
  if (!can.log) return
  listLog({ pageNum: 1, pageSize: 6 }).then(res => { logs.value = res.rows || [] })
}

function loadAll() {
  loadAds()
  loadServer()
  loadCache()
  loadOnline()
  loadCards()
  loadAi()
  loadLogs()
}

/* ---------------- 全屏 ---------------- */
const isFullscreen = ref(false)
function toggleFullscreen() {
  if (document.fullscreenElement || document.webkitFullscreenElement) {
    const exit = document.exitFullscreen || document.webkitExitFullscreen
    exit && Promise.resolve(exit.call(document)).catch(() => {})
    return
  }
  const el = screenRef.value || document.documentElement
  const req = el.requestFullscreen || el.webkitRequestFullscreen || el.msRequestFullscreen
  if (!req) return
  Promise.resolve(req.call(el)).catch(() => {})
}
function onFullscreenChange() {
  isFullscreen.value = !!(document.fullscreenElement || document.webkitFullscreenElement)
  nextTick(resizeAll)
}

/* ---------------- 自适应 ---------------- */
function resizeAll() {
  Object.values(charts).forEach(c => c && c.resize())
}
let resizeObserver = null

let refreshTimer = null
onMounted(() => {
  clockTimer = setInterval(() => { now.value = new Date() }, 1000)
  refreshTimer = setInterval(loadAll, 60000)
  document.addEventListener('fullscreenchange', onFullscreenChange)
  document.addEventListener('webkitfullscreenchange', onFullscreenChange)
  window.addEventListener('resize', resizeAll)
  resizeObserver = new ResizeObserver(resizeAll)
  ;[trendRef, campaignRef, shareRef, cardKindRef, cpuRef, memRef].forEach(el => {
    el.value && resizeObserver.observe(el.value)
  })
  themeObserver = new MutationObserver(() => {
    isDark.value = document.documentElement.classList.contains('dark')
  })
  themeObserver.observe(document.documentElement, { attributes: true, attributeFilter: ['class'] })
  loadAll()
})

onBeforeUnmount(() => {
  clearInterval(clockTimer)
  clearInterval(refreshTimer)
  document.removeEventListener('fullscreenchange', onFullscreenChange)
  document.removeEventListener('webkitfullscreenchange', onFullscreenChange)
  window.removeEventListener('resize', resizeAll)
  resizeObserver && resizeObserver.disconnect()
  themeObserver && themeObserver.disconnect()
  Object.values(charts).forEach(c => c && c.dispose())
})
</script>

<style scoped lang="scss">
/* 主题变量: 亮色为默认, html.dark 时覆盖为深色大屏风格 */
.big-screen {
  --bs-bg:
    radial-gradient(ellipse at 50% -10%, rgba(59, 130, 246, 0.10) 0%, transparent 55%),
    radial-gradient(ellipse at 15% 110%, rgba(14, 116, 144, 0.06) 0%, transparent 50%),
    radial-gradient(ellipse at 85% 110%, rgba(59, 130, 246, 0.06) 0%, transparent 50%),
    #f3f6fb;
  --bs-panel-bg: #ffffff;
  --bs-panel-border: #e2e8f0;
  --bs-text: #1f2937;
  --bs-sub: #475569;
  --bs-muted: #94a3b8;
  --bs-accent: #0e7490;
  --bs-accent-soft: #0891b2;
  --bs-accent-line: rgba(14, 116, 144, 0.35);
  --bs-head-border: #eef2f7;
  --bs-dashed: #f1f5f9;
  --bs-log-bg: #f8fafc;
  --bs-btn-border: #cbd5e1;
  --bs-btn-bg: #f1f5f9;
  --bs-btn-color: #0e7490;
  --bs-title-grad: linear-gradient(180deg, #0f2b52 20%, #2563eb 90%);
  --bs-title-glow: none;
  --bs-logo-glow: 0 2px 10px rgba(14, 116, 144, 0.35);
  --bs-kpi-top: linear-gradient(90deg, transparent, rgba(14, 116, 144, 0.35), transparent);
  --bs-glow-line: linear-gradient(90deg, transparent, #0e7490, transparent);
  --bs-scrollbar: #cbd5e1;
}
html.dark .big-screen {
  /* 跟随系统暗色主题(Element Plus 变量) */
  --bs-bg: var(--el-bg-color, #141414);
  --bs-panel-bg: var(--el-bg-color-overlay, #1d1e1f);
  --bs-panel-border: var(--el-border-color, #434343);
  --bs-text: var(--el-text-color-regular, #d0d0d0);
  --bs-sub: var(--el-text-color-secondary, #a3a6ad);
  --bs-muted: var(--el-text-color-placeholder, #8d9095);
  --bs-accent: #00d4ff;
  --bs-accent-soft: #58b6ff;
  --bs-accent-line: rgba(0, 212, 255, 0.55);
  --bs-head-border: #303030;
  --bs-dashed: #2b2b2b;
  --bs-log-bg: rgba(255, 255, 255, 0.04);
  --bs-btn-border: #434343;
  --bs-btn-bg: #1d1e1f;
  --bs-btn-color: #d0d0d0;
  --bs-title-grad: linear-gradient(180deg, #eaf6ff 20%, #58b6ff 90%);
  --bs-title-glow: 0 0 26px rgba(0, 160, 255, 0.35);
  --bs-logo-glow: 0 0 16px rgba(0, 212, 255, 0.45);
  --bs-kpi-top: linear-gradient(90deg, transparent, rgba(0, 212, 255, 0.55), transparent);
  --bs-glow-line: linear-gradient(90deg, transparent, #00d4ff, transparent);
  --bs-scrollbar: #434343;
}

.big-screen {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 84px);
  min-height: 760px;
  padding: 12px 16px 16px;
  box-sizing: border-box;
  color: var(--bs-text);
  background: var(--bs-bg);
  overflow: auto;
}

/* ---------- 头部 ---------- */
.bs-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-shrink: 0;
  padding: 4px 4px 12px;
  border-bottom: 1px solid var(--bs-panel-border);
  margin-bottom: 12px;
  position: relative;

  &::after {
    content: '';
    position: absolute;
    left: 50%;
    bottom: -1px;
    transform: translateX(-50%);
    width: 32%;
    height: 2px;
    background: var(--bs-glow-line);
  }
}

.bs-h-left {
  display: flex;
  align-items: center;
  gap: 10px;
}
.bs-logo {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 15px;
  color: #ffffff;
  background: linear-gradient(135deg, var(--bs-accent), #3b82f6);
  box-shadow: var(--bs-logo-glow);
}
.bs-sys {
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 1px;
}
.bs-sys-sub {
  font-size: 10px;
  color: var(--bs-sub);
  letter-spacing: 2px;
}

.bs-h-title {
  display: flex;
  align-items: center;
  gap: 18px;

  h1 {
    margin: 0;
    font-size: 26px;
    font-weight: 700;
    letter-spacing: 4px;
    background: var(--bs-title-grad);
    -webkit-background-clip: text;
    background-clip: text;
    color: transparent;
    text-shadow: var(--bs-title-glow);
    white-space: nowrap;
  }
}
.bs-title-line {
  width: 120px;
  height: 12px;
  background:
    linear-gradient(90deg, transparent, var(--bs-accent-line)) no-repeat left center / 100% 1px,
    radial-gradient(circle, var(--bs-accent) 1.5px, transparent 2px) no-repeat right center / 6px 6px;

  &.right {
    transform: scaleX(-1);
    background:
      linear-gradient(90deg, transparent, var(--bs-accent-line)) no-repeat right center / 100% 1px,
      radial-gradient(circle, var(--bs-accent) 1.5px, transparent 2px) no-repeat left center / 6px 6px;
  }
}

.bs-h-right {
  display: flex;
  align-items: center;
  gap: 14px;
}
.bs-clock {
  font-size: 22px;
  font-weight: 600;
  font-family: Consolas, Monaco, monospace;
  color: var(--bs-accent);
  text-shadow: 0 0 12px var(--bs-accent-line);
}
.bs-date {
  font-size: 12px;
  color: var(--bs-sub);
}
.bs-fs-btn {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  border: 1px solid var(--bs-btn-border);
  background: var(--bs-btn-bg);
  color: var(--bs-btn-color);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;

  &:hover {
    color: var(--bs-accent);
    border-color: var(--bs-accent);
    box-shadow: 0 0 10px var(--bs-accent-line);
  }
}

/* ---------- KPI ---------- */
.bs-kpis {
  display: grid;
  grid-template-columns: repeat(8, minmax(0, 1fr));
  gap: 12px;
  flex-shrink: 0;
  margin-bottom: 12px;
}
.bs-kpi {
  background: var(--bs-panel-bg);
  border: 1px solid var(--bs-panel-border);
  border-radius: 8px;
  padding: 12px 14px;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 2px;
    background: var(--bs-kpi-top);
  }
}
.bs-kpi-label {
  font-size: 12px;
  color: var(--bs-sub);
}
.bs-kpi-value {
  font-size: 24px;
  font-weight: 700;
  line-height: 1.3;
  font-family: Consolas, Monaco, sans-serif;
  white-space: nowrap;
}
.bs-kpi-unit {
  font-size: 13px;
  margin-left: 2px;
}
.bs-kpi-sub {
  font-size: 11px;
  color: var(--bs-muted);
  margin-top: 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* ---------- 主体 ---------- */
.bs-main {
  flex: 1;
  min-height: 0;
  display: grid;
  gap: 12px;
  grid-template-columns: 320px minmax(0, 1fr) 340px;
  grid-template-rows: minmax(240px, 1fr) minmax(240px, 1fr);
  grid-template-areas:
    'server trend share'
    'card campaign logs';
}
.area-server { grid-area: server; }
.area-trend { grid-area: trend; }
.area-share { grid-area: share; }
.area-card { grid-area: card; }
.area-campaign { grid-area: campaign; }
.area-logs { grid-area: logs; }

.bs-panel {
  background: var(--bs-panel-bg);
  border: 1px solid var(--bs-panel-border);
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow: hidden;
}
.bs-panel-head {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 14px;
  border-bottom: 1px solid var(--bs-head-border);

  .head-bar {
    width: 3px;
    height: 14px;
    border-radius: 2px;
    background: linear-gradient(180deg, var(--bs-accent), #3b82f6);
    box-shadow: 0 0 8px var(--bs-accent-line);
  }
  .head-title {
    font-size: 14px;
    font-weight: 600;
    letter-spacing: 1px;
  }
  .head-extra {
    margin-left: auto;
    font-size: 11px;
    color: var(--bs-muted);
  }
}
.bs-panel-body {
  flex: 1;
  min-height: 0;
  position: relative;
  padding: 8px;
}
.bs-chart {
  width: 100%;
  height: 100%;
  min-height: 180px;
}
.bs-empty {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--bs-muted);
  font-size: 13px;
}

/* 服务器面板 */
.bs-gauges {
  display: flex;
  height: 46%;
  min-height: 120px;
}
.bs-gauge {
  flex: 1;
  height: 100%;
}
.bs-srv-info {
  list-style: none;
  margin: 4px 0 0;
  padding: 0;
  font-size: 12px;

  li {
    display: flex;
    justify-content: space-between;
    gap: 8px;
    padding: 5px 6px;
    border-bottom: 1px dashed var(--bs-dashed);

    &:last-child { border-bottom: none; }
    span {
      color: var(--bs-sub);
      flex-shrink: 0;
    }
    i {
      font-style: normal;
      color: var(--bs-text);
      text-align: right;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }
}

/* 更新日志 */
.bs-logs {
  list-style: none;
  margin: 0;
  padding: 0 6px 0 2px;
  height: 100%;
  overflow-y: auto;

  &::-webkit-scrollbar { width: 4px; }
  &::-webkit-scrollbar-thumb { background: var(--bs-scrollbar); border-radius: 2px; }

  li {
    padding: 8px 8px 8px 14px;
    border-left: 2px solid var(--bs-accent-line);
    margin-bottom: 6px;
    background: var(--bs-log-bg);
    border-radius: 0 6px 6px 0;

    &:last-child { margin-bottom: 0; }
  }
}
.bs-log-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 3px;
}
.bs-log-ver {
  font-size: 13px;
  font-weight: 600;
  color: var(--bs-accent);
}
.bs-log-date {
  font-size: 11px;
  color: var(--bs-muted);
}
.bs-log-content {
  font-size: 12px;
  color: var(--bs-sub);
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* ---------- 响应式 ---------- */
@media (max-width: 1500px) {
  .bs-kpis { grid-template-columns: repeat(4, 1fr); }
}
@media (max-width: 1300px) {
  .big-screen { height: auto; overflow: visible; }
  .bs-main {
    grid-template-columns: 1fr 1fr;
    grid-template-rows: none;
    grid-template-areas:
      'trend trend'
      'campaign share'
      'server card'
      'logs logs';
  }
}
@media (max-width: 860px) {
  .bs-kpis { grid-template-columns: repeat(2, 1fr); }
  .bs-main {
    grid-template-columns: 1fr;
    grid-template-areas: 'trend' 'campaign' 'share' 'server' 'card' 'logs';
  }
  .bs-h-title h1 { font-size: 18px; letter-spacing: 2px; }
  .bs-title-line { display: none; }
}

/* 全屏模式 */
.big-screen:fullscreen {
  height: 100vh;
  overflow: auto;
  padding: 18px 26px 22px;
}
</style>
