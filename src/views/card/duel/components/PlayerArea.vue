<template>
  <div class="player-area" :class="{ 'is-active': active, 'is-opponent': opponent }">
    <!-- 信息条 -->
    <div class="player-info">
      <div class="player-name">
        <span class="name-text">{{ player.name }}</span>
        <span v-if="active" class="active-tag">回合中</span>
      </div>
      <div class="lp-box">
        <span class="lp-label">LP</span>
        <span class="lp-value">{{ player.lp }}</span>
        <button class="lp-btn" @click="$emit('lp-change', 500)">+500</button>
        <button class="lp-btn" @click="$emit('lp-change', -500)">-500</button>
      </div>
      <div class="zone-btns">
        <button class="side-btn" @click="$emit('draw')">卡组 {{ player.deck.length }}</button>
        <button class="side-btn" @click="viewZone('grave')">墓地 {{ player.grave.length }}</button>
        <button class="side-btn" @click="viewZone('banned')">除外 {{ player.banned.length }}</button>
      </div>
    </div>

    <div class="field-block">
    <!-- 手牌行 -->
    <div class="hand-row">
      <div class="hand-cards">
        <div
          v-for="(c, i) in player.hand"
          :key="i"
          class="hand-card"
          draggable="true"
          @dragstart="dragStartHand(i, c, $event)"
        >
          <template v-if="showHandFace">
            <el-image v-if="c.imageUrl" :src="c.imageUrl + '!half'" fit="cover">
              <template #error><div class="noimg">无图</div></template>
            </el-image>
            <div v-else class="noimg">无图</div>
          </template>
          <div v-else class="card-back" />
        </div>
        <div v-if="!player.hand.length" class="hand-empty">（无手牌）</div>
      </div>
      <button class="side-btn hand-toggle" @click="$emit('toggle-hidden')">
        {{ player.handFaceUp ? '明牌' : '暗牌' }}
      </button>
    </div>

    <!-- 怪兽区行: 场地区 + 5怪兽区 + 额外卡组 -->
    <div class="zone-row">
      <div class="slot-wrap single">
        <div class="slot-label">场地区</div>
        <div
          class="slot"
          :class="{ 'slot-filled': !!player.slots.field }"
          @drop.prevent="$emit('drop-slot', { zone: 'field' })"
          @dragover.prevent
        >
          <slot-card
            v-if="player.slots.field"
            :slot-data="player.slots.field"
            @click="$emit('slot-click', { zone: 'field' })"
            @return="$emit('slot-return', { zone: 'field' })"
            @grave="$emit('slot-to-grave', { zone: 'field' })"
            @banish="$emit('slot-to-banned', { zone: 'field' })"
            @dragstart="dragStartSlot('field', 0, $event)"
          />
        </div>
      </div>
      <div class="slot-wrap five">
        <div class="slot-label">怪兽区</div>
        <div class="slot-line">
          <div
            v-for="(s, i) in player.slots.monster"
            :key="i"
            class="slot"
            :class="{ 'slot-filled': !!s }"
            @drop.prevent="$emit('drop-slot', { zone: 'monster', index: i })"
            @dragover.prevent
          >
            <slot-card
              v-if="s"
              :slot-data="s"
              @click="$emit('slot-click', { zone: 'monster', index: i })"
              @return="$emit('slot-return', { zone: 'monster', index: i })"
              @grave="$emit('slot-to-grave', { zone: 'monster', index: i })"
              @banish="$emit('slot-to-banned', { zone: 'monster', index: i })"
              @dragstart="dragStartSlot('monster', i, $event)"
            />
          </div>
        </div>
      </div>
      <div class="slot-wrap single">
        <div class="slot-label">额外卡组</div>
        <div class="slot extra-slot" @drop.prevent="$emit('drop-extra')" @dragover.prevent>
          <div class="card-back" />
          <span class="extra-count">{{ player.extra.length }}</span>
        </div>
      </div>
    </div>

    <!-- 魔陷区行: 灵摆左 + 5魔陷区 + 灵摆右 -->
    <div class="zone-row">
      <div class="slot-wrap single">
        <div class="slot-label">灵摆左</div>
        <div
          class="slot"
          :class="{ 'slot-filled': !!player.slots.pendulumL }"
          @drop.prevent="$emit('drop-slot', { zone: 'pendulumL' })"
          @dragover.prevent
        >
          <slot-card
            v-if="player.slots.pendulumL"
            :slot-data="player.slots.pendulumL"
            @click="$emit('slot-click', { zone: 'pendulumL' })"
            @return="$emit('slot-return', { zone: 'pendulumL' })"
            @grave="$emit('slot-to-grave', { zone: 'pendulumL' })"
            @banish="$emit('slot-to-banned', { zone: 'pendulumL' })"
            @dragstart="dragStartSlot('pendulumL', 0, $event)"
          />
        </div>
      </div>
      <div class="slot-wrap five">
        <div class="slot-label">魔法与陷阱区域</div>
        <div class="slot-line">
          <div
            v-for="(s, i) in player.slots.spell"
            :key="i"
            class="slot"
            :class="{ 'slot-filled': !!s }"
            @drop.prevent="$emit('drop-slot', { zone: 'spell', index: i })"
            @dragover.prevent
          >
            <slot-card
              v-if="s"
              :slot-data="s"
              @click="$emit('slot-click', { zone: 'spell', index: i })"
              @return="$emit('slot-return', { zone: 'spell', index: i })"
              @grave="$emit('slot-to-grave', { zone: 'spell', index: i })"
              @banish="$emit('slot-to-banned', { zone: 'spell', index: i })"
              @dragstart="dragStartSlot('spell', i, $event)"
            />
          </div>
        </div>
      </div>
      <div class="slot-wrap single">
        <div class="slot-label">灵摆右</div>
        <div
          class="slot"
          :class="{ 'slot-filled': !!player.slots.pendulumR }"
          @drop.prevent="$emit('drop-slot', { zone: 'pendulumR' })"
          @dragover.prevent
        >
          <slot-card
            v-if="player.slots.pendulumR"
            :slot-data="player.slots.pendulumR"
            @click="$emit('slot-click', { zone: 'pendulumR' })"
            @return="$emit('slot-return', { zone: 'pendulumR' })"
            @grave="$emit('slot-to-grave', { zone: 'pendulumR' })"
            @banish="$emit('slot-to-banned', { zone: 'pendulumR' })"
            @dragstart="dragStartSlot('pendulumR', 0, $event)"
          />
        </div>
      </div>
    </div>
    </div>
  </div>
</template>

<script setup name="PlayerArea">
import SlotCard from "./SlotCard.vue"

const props = defineProps({
  player: { type: Object, required: true },
  playerIndex: { type: Number, required: true },
  active: { type: Boolean, default: false },
  opponent: { type: Boolean, default: false }
})
defineEmits([
  'lp-change', 'draw', 'toggle-hidden',
  'drop-slot', 'drop-extra',
  'slot-click', 'slot-return', 'slot-to-grave', 'slot-to-banned'
])

const dragStartHandFn = inject('duelDragStartHand')
const dragStartSlotFn = inject('duelDragStartSlot')
const viewZoneFn = inject('duelViewZone')

function dragStartHand(index, card, e) {
  dragStartHandFn(props.playerIndex, index, card, e)
}
function dragStartSlot(zone, index, e) {
  dragStartSlotFn(props.playerIndex, zone, index, e)
}
function viewZone(zone) {
  viewZoneFn(props.playerIndex, zone)
}

const showHandFace = computed(() => !props.opponent || props.player.handFaceUp)
</script>

<style scoped>
.player-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 6px 8px;
  border-radius: 6px;
  min-height: 0;
}
.player-area.is-active {
  background: rgba(255, 210, 90, .06);
  box-shadow: inset 0 0 0 1px rgba(255, 210, 90, .35);
}

/* 信息条 */
.player-info {
  display: flex;
  align-items: center;
  gap: 16px;
}
.player-name {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #e8edf4;
  font-size: 14px;
  font-weight: 600;
}
.active-tag {
  font-size: 11px;
  font-weight: 400;
  color: #ffd166;
  border: 1px solid #ffd166;
  border-radius: 8px;
  padding: 0 6px;
}
.lp-box {
  display: flex;
  align-items: center;
  gap: 6px;
}
.lp-label { color: #9fb0c5; font-size: 12px; }
.lp-value {
  color: #fff;
  font-size: 22px;
  font-weight: 800;
  font-style: italic;
  min-width: 64px;
}
.lp-btn {
  background: rgba(255, 255, 255, .12);
  border: 1px solid rgba(255, 255, 255, .25);
  color: #dfe6ef;
  border-radius: 4px;
  font-size: 11px;
  padding: 1px 6px;
  cursor: pointer;
}
.lp-btn:hover { background: rgba(255, 255, 255, .25); }
.zone-btns { display: flex; gap: 6px; margin-left: auto; }
.side-btn {
  background: rgba(255, 255, 255, .1);
  border: 1px solid rgba(255, 255, 255, .22);
  color: #cfd8e3;
  border-radius: 12px;
  font-size: 12px;
  padding: 2px 10px;
  cursor: pointer;
  white-space: nowrap;
}
.side-btn:hover { background: rgba(255, 255, 255, .22); }

/* 手牌 */
.hand-row {
  display: flex;
  align-items: flex-end;
  justify-content: center;
  gap: 8px;
}
.hand-cards {
  display: flex;
  gap: 4px;
  flex-wrap: wrap;
  min-height: 64px;
  align-items: flex-end;
}
.hand-card {
  width: 46px;
  cursor: grab;
  border-radius: 2px;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, .3);
}
.hand-card .el-image { width: 100%; aspect-ratio: 5 / 7; display: block; }
.hand-empty { color: #6d7c8f; font-size: 12px; align-self: center; }
.hand-toggle { align-self: center; }

/* 区域块: 紧凑居中 */
.field-block {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 8px;
  min-height: 0;
}

/* 区域行 */
.zone-row {
  display: flex;
  align-items: flex-end;
  justify-content: center;
  gap: 14px;
}
.slot-wrap { display: flex; flex-direction: column; gap: 2px; }
.slot-label { color: #7f8fa4; font-size: 11px; text-align: center; }
.slot-line {
  display: grid;
  grid-template-columns: repeat(5, 62px);
  gap: 8px;
}
.slot {
  width: 62px;
  height: 88px;
  border: 1.5px dashed rgba(160, 190, 220, .4);
  border-radius: 4px;
  background: rgba(255, 255, 255, .04);
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}
.slot.slot-filled { border-style: solid; border-color: rgba(160, 190, 220, .15); background: transparent; }
.extra-slot { border-style: solid; }
.extra-count {
  position: absolute;
  bottom: 2px;
  right: 4px;
  color: #fff;
  font-size: 12px;
  font-weight: 700;
  text-shadow: 0 0 3px #000;
}

/* 卡背 */
.card-back {
  width: 100%;
  height: 100%;
  border-radius: 3px;
  background:
    radial-gradient(circle at 50% 45%, rgba(255, 170, 60, .25) 0%, transparent 55%),
    linear-gradient(160deg, #6b3122 0%, #3a1b12 50%, #5a2a1c 100%);
  border: 2px solid #caa15c;
}
.hand-card .card-back { aspect-ratio: 5 / 7; }

.noimg {
  width: 100%;
  aspect-ratio: 5 / 7;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
  font-size: 11px;
  background: #f0f2f5;
}
</style>
