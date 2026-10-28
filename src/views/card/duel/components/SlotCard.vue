<template>
  <div class="slot-card" :class="'pos-' + slotData.position" draggable="true" @click="onClick" @dragstart.stop="$emit('dragstart', $event)">
    <template v-if="slotData.position !== 'facedown'">
      <el-image v-if="slotData.card.imageUrl" :src="slotData.card.imageUrl + '!half'" fit="cover">
        <template #error><div class="noimg">无图</div></template>
      </el-image>
      <div v-else class="noimg">无图</div>
      <div class="slot-card-stats" v-if="slotData.card.cardKind === 1">
        {{ atkText }}/{{ defText }}
      </div>
    </template>
    <div v-else class="card-back" />
    <div class="slot-card-menu" @click.stop>
      <button class="menu-btn" title="收回手牌" @click="$emit('return')">↩</button>
      <button class="menu-btn" title="送入墓地" @click="$emit('grave')">墓</button>
      <button class="menu-btn" title="除外" @click="$emit('banish')">除</button>
    </div>
  </div>
</template>

<script setup name="SlotCard">
const props = defineProps({
  slotData: { type: Object, required: true }
})
const emit = defineEmits(['click', 'return', 'grave', 'banish', 'dragstart'])

const atkText = computed(() => (props.slotData.card.atk === -2 || props.slotData.card.atk == null ? '?' : props.slotData.card.atk))
const defText = computed(() => (props.slotData.card.def === -2 || props.slotData.card.def == null ? '?' : props.slotData.card.def))

function onClick() {
  emit('click')
}
</script>

<style scoped>
.slot-card {
  width: 100%;
  height: 100%;
  position: relative;
  cursor: pointer;
  border-radius: 3px;
  overflow: visible;
}
.slot-card .el-image {
  width: 100%;
  height: 100%;
  border-radius: 3px;
  display: block;
  border: 1px solid rgba(255, 220, 150, .5);
}
/* 守备表示: 横置 */
.slot-card.pos-defense {
  transform: rotate(90deg) scale(.82);
}
.slot-card.pos-defense .slot-card-menu {
  transform: rotate(-90deg);
}

.slot-card-stats {
  position: absolute;
  top: -16px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0, 0, 0, .75);
  color: #ffd166;
  font-size: 11px;
  font-weight: 700;
  border-radius: 8px;
  padding: 0 6px;
  white-space: nowrap;
  pointer-events: none;
}
.card-back {
  width: 100%;
  height: 100%;
  border-radius: 3px;
  background:
    radial-gradient(circle at 50% 45%, rgba(255, 170, 60, .25) 0%, transparent 55%),
    linear-gradient(160deg, #6b3122 0%, #3a1b12 50%, #5a2a1c 100%);
  border: 2px solid #caa15c;
}
.noimg {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
  font-size: 11px;
  background: #f0f2f5;
  border-radius: 3px;
}
.slot-card-menu {
  position: absolute;
  bottom: -10px;
  right: -6px;
  display: none;
  gap: 2px;
  z-index: 6;
}
.slot:hover .slot-card-menu,
.slot-card:hover .slot-card-menu {
  display: flex;
}
.menu-btn {
  width: 18px;
  height: 18px;
  border-radius: 50%;
  border: none;
  background: rgba(20, 28, 40, .9);
  color: #cfd8e3;
  font-size: 10px;
  line-height: 1;
  cursor: pointer;
}
.menu-btn:hover { background: var(--el-color-primary); color: #fff; }
</style>
