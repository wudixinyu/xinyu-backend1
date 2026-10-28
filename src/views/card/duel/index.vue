<template>
  <div class="duel-page">
    <!-- 决斗场地 -->
    <div class="field-wrap">
      <div class="field-toolbar">
        <div class="field-title">决斗场地</div>
        <div class="field-actions">
          <el-button size="small" icon="Plus" @click="pickerOpen = true">添加卡片</el-button>
          <el-button size="small" icon="RefreshLeft" @click="handleReset">重置场地</el-button>
        </div>
      </div>

      <div class="duel-field">
        <!-- 上方玩家(玩家2) -->
        <player-area
          :player="state.players[1]"
          :player-index="1"
          :active="state.turnOwner === 1"
          :opponent="true"
          @lp-change="stepLp(1, $event)"
          @draw="handleDraw(1)"
          @toggle-hidden="state.players[1].handFaceUp = !state.players[1].handFaceUp"
          @drop-slot="onDropToSlot(1, $event)"
          @drop-extra="onDropToExtra(1, $event)"
          @slot-click="cycleSlot(1, $event)"
          @slot-return="returnToHand(1, $event)"
          @slot-to-grave="slotToGrave(1, $event)"
          @slot-to-banned="slotToBanned(1, $event)"
        />

        <!-- 中央回合指示 -->
        <div class="field-center">
          <div class="turn-badge">
            <div class="turn-no">Turn {{ state.turnCount }}</div>
            <div class="turn-phase">{{ state.phase }}</div>
          </div>
          <div class="turn-controls">
            <el-select v-model="state.phase" size="small" style="width: 96px">
              <el-option v-for="p in PHASES" :key="p" :label="p" :value="p" />
            </el-select>
            <el-button size="small" @click="state.turnCount = Math.max(1, state.turnCount - 1)">-</el-button>
            <el-button size="small" @click="state.turnCount++">+</el-button>
            <el-button size="small" type="primary" @click="endTurn">结束回合</el-button>
          </div>
        </div>

        <!-- 下方玩家(玩家1) -->
        <player-area
          :player="state.players[0]"
          :player-index="0"
          :active="state.turnOwner === 0"
          :opponent="false"
          @lp-change="stepLp(0, $event)"
          @draw="handleDraw(0)"
          @toggle-hidden="state.players[0].handFaceUp = !state.players[0].handFaceUp"
          @drop-slot="onDropToSlot(0, $event)"
          @drop-extra="onDropToExtra(0, $event)"
          @slot-click="cycleSlot(0, $event)"
          @slot-return="returnToHand(0, $event)"
          @slot-to-grave="slotToGrave(0, $event)"
          @slot-to-banned="slotToBanned(0, $event)"
        />
      </div>
    </div>

    <!-- 添加卡片抽屉 -->
    <el-drawer v-model="pickerOpen" title="添加卡片" size="420px" append-to-body class="picker-drawer">
      <el-tabs v-model="toolTab" class="tool-tabs">
        <el-tab-pane label="搜索卡片" name="search">
          <div class="tool-search-bar">
            <el-input
              v-model="cardKeyword"
              placeholder="卡片密码 / 名称"
              size="small"
              clearable
              @keyup.enter="handleCardQuery"
              @clear="handleCardQuery"
            />
            <el-button type="primary" size="small" icon="Search" @click="handleCardQuery">搜索</el-button>
          </div>
          <div class="tool-target">
            添加到：
            <el-radio-group v-model="addTarget" size="small">
              <el-radio-button :value="0">玩家1</el-radio-button>
              <el-radio-button :value="1">玩家2</el-radio-button>
            </el-radio-group>
          </div>
          <div v-loading="cardLoading" class="tool-card-grid">
            <el-empty v-if="!cardLoading && cardList.length === 0" :image-size="50" description="无匹配卡片" style="grid-column: 1 / -1" />
            <div
              v-for="row in cardList"
              :key="row.cardId"
              class="tool-card"
              draggable="true"
              @dragstart="onDragStartCard(row, $event)"
              @click="addCardToHand(row)"
              title="点击加入手牌 / 拖拽到场地区域"
            >
              <el-image v-if="row.imageUrl" :src="row.imageUrl + '!half'" fit="cover">
                <template #error><div class="noimg">无图</div></template>
              </el-image>
              <div v-else class="noimg">无图</div>
              <div class="tool-card-name" :title="row.cnName">{{ row.cnName || row.cardId }}</div>
            </div>
          </div>
          <pagination
            v-show="cardTotal > 0"
            :total="cardTotal"
            v-model:page="cardQuery.pageNum"
            v-model:limit="cardQuery.pageSize"
            layout="prev, pager, next"
            @pagination="getCardList"
          />
        </el-tab-pane>

        <el-tab-pane label="导入卡组" name="pack">
          <div class="tool-pack-bar">
            <el-select v-model="selectedPackId" size="small" placeholder="选择卡组" style="width: 100%" @change="handlePackChange">
              <el-option v-for="p in packList" :key="p.packId" :label="p.packName" :value="p.packId" />
            </el-select>
          </div>
          <div class="tool-target">
            导入到：
            <el-radio-group v-model="importTarget" size="small">
              <el-radio-button :value="0">玩家1</el-radio-button>
              <el-radio-button :value="1">玩家2</el-radio-button>
            </el-radio-group>
            <el-button type="primary" size="small" :disabled="!packCards.length" :loading="packLoading" @click="handleImportPack">导入卡组</el-button>
          </div>
          <div v-loading="packLoading" class="tool-card-grid">
            <el-empty v-if="!packLoading && packCards.length === 0" :image-size="50" description="请选择卡组" style="grid-column: 1 / -1" />
            <div
              v-for="(row, idx) in packCards"
              :key="idx"
              class="tool-card"
              draggable="true"
              @dragstart="onDragStartCard(row, $event)"
              title="可拖拽到场地区域"
            >
              <el-image v-if="row.imageUrl" :src="row.imageUrl + '!half'" fit="cover">
                <template #error><div class="noimg">无图</div></template>
              </el-image>
              <div v-else class="noimg">无图</div>
              <span class="tool-card-qty">{{ row.quantity }}</span>
              <div class="tool-card-name" :title="row.cnName">{{ row.cnName || row.cardId }}</div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-drawer>

    <!-- 墓地/除外查看 -->
    <el-dialog v-model="zoneViewOpen" :title="zoneViewTitle" width="520px" append-to-body>
      <div class="zone-list">
        <div v-for="(c, i) in zoneViewCards" :key="i" class="zone-list-item">
          <el-image v-if="c.imageUrl" :src="c.imageUrl + '!half'" fit="cover" style="width: 40px; height: 58px">
            <template #error><div class="noimg" style="width:40px;height:58px">无图</div></template>
          </el-image>
          <div v-else class="noimg" style="width:40px;height:58px">无图</div>
          <span>{{ c.cnName || c.cardId }}</span>
        </div>
        <el-empty v-if="!zoneViewCards.length" :image-size="60" description="区域为空" />
      </div>
    </el-dialog>
  </div>
</template>

<script setup name="Duel">
import { listInfo } from "@/api/card/info"
import { listPack, listPackItem } from "@/api/card/pack"
import PlayerArea from "./components/PlayerArea.vue"

const { proxy } = getCurrentInstance()

const STORAGE_KEY = 'card-d…te-v1'
const PHASES = ['Draw', 'Stand', 'Main1', 'BP1', 'Main2', 'End']
// 额外卡组类型位掩码(融合/同步/超量/连接),与 pack 页保持一致
const EXTRA_TYPE_MASK = 0x40 | 0x2000 | 0x800000 | 0x4000000

function makeCard(c) {
  return {
    cardId: c.cardId,
    cnName: c.cnName,
    imageUrl: c.imageUrl,
    atk: c.atk,
    def: c.def,
    cardKind: c.cardKind,
    type: c.type
  }
}

function newPlayer(name) {
  return {
    name,
    lp: 8000,
    hand: [],
    deck: [],
    extra: [],
    grave: [],
    banned: [],
    handFaceUp: true,
    slots: {
      monster: [null, null, null, null, null],
      spell: [null, null, null, null, null],
      field: null,
      pendulumL: null,
      pendulumR: null
    }
  }
}

function newState() {
  return {
    turnCount: 1,
    turnOwner: 0,
    phase: 'Draw',
    players: [newPlayer('玩家1'), newPlayer('玩家2')]
  }
}

// 场地状态(可持久化到 localStorage)
const state = ref(loadState())

function loadState() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    if (raw) {
      const s = JSON.parse(raw)
      if (s && s.players && s.players.length === 2) return s
    }
  } catch (e) { /* 忽略损坏数据 */ }
  return newState()
}

let saveTimer = null
watch(state, () => {
  clearTimeout(saveTimer)
  saveTimer = setTimeout(() => {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(state.value))
  }, 300)
}, { deep: true })

/* ---------------- 拖拽数据 ---------------- */
// 拖拽来源: { type: 'card', card } | { type: 'hand', player, index } | { type: 'slot', player, zone, index }
let dragPayload = null

function onDragStartCard(card, e) {
  dragPayload = { type: 'card', card: makeCard(card) }
  e.dataTransfer.effectAllowed = 'copy'
}
function onDragStartHand(player, index, card, e) {
  dragPayload = { type: 'hand', player, index, card }
  e.dataTransfer.effectAllowed = 'move'
}
function onDragStartSlot(player, zone, index, e) {
  dragPayload = { type: 'slot', player, zone, index }
  e.dataTransfer.effectAllowed = 'move'
}
function onDropToSlot(player, zoneIndex) {
  if (!dragPayload) return
  const p = state.value.players[player]
  const { zone, index } = zoneIndex
  const slotArr = zone === 'field' || zone === 'pendulumL' || zone === 'pendulumR' ? null : p.slots[zone]
  const target = slotArr ? slotArr[index] : p.slots[zone]
  if (target) {
    proxy.$modal.msgWarning('该位置已有卡片')
    return
  }
  if (dragPayload.type === 'card') {
    const placed = { card: dragPayload.card, position: dragPayload.card.cardKind === 1 ? 'attack' : 'facedown' }
    if (slotArr) slotArr[index] = placed
    else p.slots[zone] = placed
  } else if (dragPayload.type === 'hand') {
    const from = state.value.players[dragPayload.player].hand
    const [card] = from.splice(dragPayload.index, 1)
    const placed = { card, position: card.cardKind === 1 ? 'attack' : 'facedown' }
    if (slotArr) slotArr[index] = placed
    else p.slots[zone] = placed
  } else if (dragPayload.type === 'slot') {
    const sp = state.value.players[dragPayload.player]
    const sZone = dragPayload.zone
    const fromArr = sZone === 'field' || sZone === 'pendulumL' || sZone === 'pendulumR' ? null : sp.slots[sZone]
    const moved = fromArr ? fromArr[dragPayload.index] : sp.slots[sZone]
    if (!moved) return
    if (dragPayload.player === player && fromArr && fromArr[index] === moved) return
    if (fromArr) fromArr[dragPayload.index] = null
    else sp.slots[sZone] = null
    if (slotArr) slotArr[index] = moved
    else p.slots[zone] = moved
  }
  dragPayload = null
}
function onDropToExtra(player) {
  if (!dragPayload) return
  const p = state.value.players[player]
  if (dragPayload.type === 'card') {
    p.extra.push(dragPayload.card)
  } else if (dragPayload.type === 'hand') {
    const from = state.value.players[dragPayload.player].hand
    const [card] = from.splice(dragPayload.index, 1)
    p.extra.push(card)
  }
  dragPayload = null
}

/* ---------------- 卡片操作 ---------------- */
function addCardToHand(card) {
  state.value.players[addTarget.value].hand.push(makeCard(card))
}

function handleDraw(player) {
  const p = state.value.players[player]
  if (!p.deck.length) {
    proxy.$modal.msgWarning('卡组已空')
    return
  }
  p.hand.push(p.deck.shift())
}

function stepLp(player, delta) {
  const p = state.value.players[player]
  p.lp = Math.max(0, p.lp + delta)
}

function cycleSlot(player, zoneIndex) {
  const { zone, index } = zoneIndex
  const p = state.value.players[player]
  const slotArr = zone === 'field' || zone === 'pendulumL' || zone === 'pendulumR' ? null : p.slots[zone]
  const slot = slotArr ? slotArr[index] : p.slots[zone]
  if (!slot) return
  const order = ['attack', 'defense', 'facedown']
  slot.position = order[(order.indexOf(slot.position) + 1) % order.length]
}

function returnToHand(player, zoneIndex) {
  const { zone, index } = zoneIndex
  const p = state.value.players[player]
  const slotArr = zone === 'field' || zone === 'pendulumL' || zone === 'pendulumR' ? null : p.slots[zone]
  const slot = slotArr ? slotArr[index] : p.slots[zone]
  if (!slot) return
  p.hand.push(slot.card)
  if (slotArr) slotArr[index] = null
  else p.slots[zone] = null
}

function slotToGrave(player, zoneIndex) {
  const { zone, index } = zoneIndex
  const p = state.value.players[player]
  const slotArr = zone === 'field' || zone === 'pendulumL' || zone === 'pendulumR' ? null : p.slots[zone]
  const slot = slotArr ? slotArr[index] : p.slots[zone]
  if (!slot) return
  p.grave.push(slot.card)
  if (slotArr) slotArr[index] = null
  else p.slots[zone] = null
}

function slotToBanned(player, zoneIndex) {
  const { zone, index } = zoneIndex
  const p = state.value.players[player]
  const slotArr = zone === 'field' || zone === 'pendulumL' || zone === 'pendulumR' ? null : p.slots[zone]
  const slot = slotArr ? slotArr[index] : p.slots[zone]
  if (!slot) return
  p.banned.push(slot.card)
  if (slotArr) slotArr[index] = null
  else p.slots[zone] = null
}

function endTurn() {
  state.value.turnOwner = state.value.turnOwner === 0 ? 1 : 0
  if (state.value.turnOwner === 0) state.value.turnCount++
  state.value.phase = 'Draw'
}

function handleReset() {
  proxy.$modal.confirm('确定要清空当前决斗场地吗？').then(() => {
    state.value = newState()
    localStorage.removeItem(STORAGE_KEY)
  }).catch(() => {})
}

/* ---------------- 墓地/除外查看 ---------------- */
const zoneViewOpen = ref(false)
const zoneViewTitle = ref('')
const zoneViewCards = ref([])
function viewZone(player, zone) {
  const p = state.value.players[player]
  zoneViewTitle.value = p.name + ' · ' + (zone === 'grave' ? '墓地' : '除外区')
  zoneViewCards.value = p[zone]
  zoneViewOpen.value = true
}

/* ---------------- 搜索卡片 ---------------- */
const pickerOpen = ref(false)
const toolTab = ref('search')
const addTarget = ref(0)
const cardKeyword = ref('')
const cardLoading = ref(false)
const cardList = ref([])
const cardTotal = ref(0)
const cardQuery = ref({ pageNum: 1, pageSize: 12 })

function getCardList() {
  cardLoading.value = true
  const params = { ...cardQuery.value }
  const kw = String(cardKeyword.value || '').trim()
  if (kw) {
    if (/^\d+$/.test(kw)) params.cardId = kw
    else params.cnName = kw
  }
  listInfo(params).then(response => {
    cardList.value = response.rows || []
    cardTotal.value = response.total || 0
  }).finally(() => {
    cardLoading.value = false
  })
}
function handleCardQuery() {
  cardQuery.value.pageNum = 1
  getCardList()
}

/* ---------------- 导入卡组 ---------------- */
const importTarget = ref(0)
const packList = ref([])
const selectedPackId = ref(undefined)
const packLoading = ref(false)
const packCards = ref([])

function getPackList() {
  listPack({ pageNum: 1, pageSize: 100 }).then(res => {
    packList.value = res.rows || []
  })
}
function handlePackChange(packId) {
  if (!packId) { packCards.value = []; return }
  packLoading.value = true
  listPackItem({ pageNum: 1, pageSize: 400, packId }).then(res => {
    packCards.value = (res.rows || []).map(r => ({ ...makeCard(r), quantity: r.quantity || 1 }))
  }).finally(() => {
    packLoading.value = false
  })
}
function handleImportPack() {
  const main = []
  const extra = []
  packCards.value.forEach(row => {
    const n = row.quantity || 1
    for (let i = 0; i < n; i++) {
      const c = makeCard(row)
      if (row.type && (row.type & EXTRA_TYPE_MASK) !== 0) extra.push(c)
      else main.push(c)
    }
  })
  const p = state.value.players[importTarget.value]
  p.deck = main
  p.extra = extra
  proxy.$modal.msgSuccess(`已导入到 ${p.name}：主卡组 ${main.length} 张，额外卡组 ${extra.length} 张`)
}

/* ---------------- 提供给 PlayerArea 的事件桥接 ---------------- */
provide('duelDragStartHand', onDragStartHand)
provide('duelDragStartSlot', onDragStartSlot)
provide('duelViewZone', viewZone)

getCardList()
getPackList()
</script>

<style scoped>
.duel-page {
  display: flex;
  gap: 12px;
  height: calc(100vh - 84px);
  min-width: 900px;
}

/* 添加卡片抽屉内工具区 */
.tool-tabs {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
:deep(.tool-tabs .el-tabs__content) {
  flex: 1;
  overflow: hidden;
}
:deep(.tool-tabs .el-tab-pane) {
  height: 100%;
  overflow-y: auto;
}
.tool-search-bar {
  display: flex;
  gap: 6px;
  margin-bottom: 8px;
}
.tool-target {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  font-size: 13px;
  color: #606266;
  flex-wrap: wrap;
}
.tool-pack-bar { margin-bottom: 8px; }
.tool-card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(84px, 1fr));
  gap: 8px;
}
.tool-card {
  position: relative;
  cursor: grab;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  overflow: hidden;
  background: #fafafa;
}
.tool-card:hover { border-color: var(--el-color-primary); }
.tool-card .el-image { width: 100%; aspect-ratio: 5 / 7; display: block; }
.tool-card-name {
  font-size: 11px;
  line-height: 1.3;
  padding: 2px 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  color: #303133;
}
.tool-card-qty {
  position: absolute;
  top: 2px;
  right: 2px;
  background: rgba(0, 0, 0, .6);
  color: #fff;
  font-size: 11px;
  border-radius: 3px;
  padding: 0 4px;
}
.noimg {
  width: 100%;
  aspect-ratio: 5 / 7;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
  font-size: 12px;
  background: #f0f2f5;
}

/* 右侧场地 */
.field-wrap {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}
.field-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.field-title { font-size: 15px; font-weight: 600; }
.duel-field {
  flex: 1;
  background: radial-gradient(ellipse at center, #3a4a5f 0%, #232e3d 55%, #161d28 100%);
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  padding: 10px 16px;
  overflow: auto;
}
.field-center {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 14px;
  padding: 4px 8px;
}
.turn-badge {
  width: 74px;
  height: 74px;
  border-radius: 50%;
  background: radial-gradient(circle at 35% 30%, #ffe9a8, #d8a53a 60%, #8a5c12);
  border: 3px solid #6d4a10;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #3a2604;
  box-shadow: 0 0 14px rgba(255, 210, 90, .45);
  flex-shrink: 0;
  z-index: 5;
}
.turn-no { font-size: 13px; font-weight: 700; }
.turn-phase { font-size: 15px; font-weight: 800; }
.turn-controls {
  display: flex;
  align-items: center;
  gap: 6px;
  background: rgba(255, 255, 255, .08);
  padding: 6px 10px;
  border-radius: 20px;
}

/* 墓地列表 */
.zone-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  max-height: 380px;
  overflow-y: auto;
}
.zone-list-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13px;
}
</style>
