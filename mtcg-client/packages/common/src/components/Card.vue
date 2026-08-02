<script setup lang="ts">
import { computed } from 'vue'
import type { CardInstance } from '../types'

type CardTheme = 'character' | 'rush' | 'neutral'
type Highlight = 'none' | 'selected' | 'can-attack' | 'can-move' | 'can-target' | 'can-intercept'

interface Props {
  image?: string
  backType?: 'character' | 'rush'
  isFaceDown?: boolean
  tag?: string
  count?: number | string
  power?: number
  level?: number | string
  attackRange?: number | string
  side?: 'local' | 'opponent'
  theme?: CardTheme
  horizontal?: boolean
  highlight?: Highlight
  instance?: CardInstance
}

const props = withDefaults(defineProps<Props>(), {
  isFaceDown: true,
  backType: 'character',
  side: 'local',
  theme: 'character',
  horizontal: false,
  highlight: 'none',
})

const bgImage = computed(() => {
  if (props.image && !props.isFaceDown) return `url("${props.image}")`
  return props.backType === 'rush'
    ? `url("/card_back_rush.png")`
    : `url("/card_back_character.png")`
})

const bgSize = computed(() =>
  props.isFaceDown ? 'contain' : 'cover'
)

const isLocal = computed(() => props.side === 'local')

const tagPos = computed(() => isLocal.value ? 'top-left' : 'top-right')
const countPos = computed(() => isLocal.value ? 'top-right' : 'top-left')

/* 战力格式化: >=1万用w, >=1千用k, 节省底部状态条空间 */
const powerText = computed(() => {
  const p = typeof props.power === 'number' ? props.power : Number(props.power)
  if (isNaN(p)) return String(props.power)
  if (p >= 10000) {
    const w = p / 10000
    return w % 1 === 0 ? `${w}w` : `${w.toFixed(1)}w`
  }
  if (p >= 1000) {
    const k = p / 1000
    return k % 1 === 0 ? `${k}k` : `${k.toFixed(1)}k`
  }
  return String(p)
})

const highlightColor = computed(() => {
  switch (props.highlight) {
    case 'selected': return 'rgba(255, 255, 0, 0.8)'
    case 'can-attack': return 'rgba(239, 68, 68, 0.8)'
    case 'can-move': return 'rgba(59, 130, 246, 0.8)'
    case 'can-target': return 'rgba(168, 85, 247, 0.8)'
    case 'can-intercept': return 'rgba(251, 146, 60, 0.8)'
    default: return 'transparent'
  }
})
</script>

<template>
  <div
    class="card-face"
    :class="[theme, { horizontal, 'is-face-down': isFaceDown, 'has-highlight': highlight !== 'none' }]"
    :style="{
      backgroundImage: bgImage,
      backgroundSize: bgSize,
      '--hl-color': highlightColor,
    }"
  >
    <!-- 标签 (卡组类用途) -->
    <span v-if="tag && isFaceDown" class="card-tag" :class="tagPos">{{ tag }}</span>
    <!-- 统计数 (卡组剩余数) -->
    <span v-if="count !== undefined && isFaceDown" class="card-count" :class="countPos">{{ count }}</span>

    <!-- 正面战斗属性: 等级(顶部) + 底部状态条(攻距/战力) -->
    <template v-if="!isFaceDown">
      <!-- 等级: 顶部居中 -->
      <span v-if="level !== undefined" class="card-level"><b>Lv</b>{{ level }}</span>
      <!-- 底部状态条: 攻距(左) + 战力(右), 横向通栏容纳大数字 -->
      <div v-if="power !== undefined || attackRange !== undefined" class="card-stat-bar">
        <span v-if="attackRange !== undefined" class="stat-item stat-range"><b>R</b>{{ attackRange }}</span>
        <span v-if="power !== undefined" class="stat-item stat-power"><b>力</b>{{ powerText }}</span>
      </div>
    </template>
  </div>
</template>

<style scoped>
.card-face {
  position: absolute;
  inset: 0;
  background-position: center;
  background-repeat: no-repeat;
  border-radius: inherit;
  pointer-events: none;
  z-index: 0;
}

.card-face.horizontal {
  top: 50%;
  left: 50%;
  width: calc(var(--card-h, 92px) * 0.717);
  height: var(--card-h, 92px);
  transform: translate(-50%, -50%) rotate(90deg);
  background-size: cover;
  border-radius: var(--radius-sm, 4px);
}

.card-face.has-highlight {
  box-shadow: inset 0 0 0 2px var(--hl-color), 0 0 12px var(--hl-color);
}

/* ============================================
   通用角落定位
   ============================================ */
.top-left     { top: 3px;    left: 3px; }
.top-right    { top: 3px;    right: 3px; }
.bottom-left  { bottom: 3px; left: 3px; }
.bottom-right { bottom: 3px; right: 3px; }

/* ============================================
   卡背标签 / 统计
   ============================================ */
.card-tag {
  position: absolute;
  font-size: 9px;
  font-weight: 700;
  letter-spacing: 1px;
  color: #f1f5f9;
  white-space: nowrap;
  background: linear-gradient(135deg, rgba(0, 0, 0, 0.85), rgba(15, 23, 42, 0.8));
  padding: 1px 8px;
  border-radius: 3px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.9);
  z-index: 2;
  pointer-events: none;
  backdrop-filter: blur(3px);
}

.card-face.character .card-tag { border: 1px solid rgba(74, 222, 128, 0.35); }
.card-face.rush .card-tag { border: 1px solid rgba(251, 146, 60, 0.4); }
.card-face.neutral .card-tag { border: 1px solid rgba(148, 163, 184, 0.35); }

.card-count {
  position: absolute;
  font-size: 12px;
  font-weight: 800;
  color: #fff;
  min-width: 22px;
  text-align: center;
  background: linear-gradient(135deg, rgba(0, 0, 0, 0.88), rgba(15, 23, 42, 0.82));
  padding: 1px 7px;
  border-radius: 11px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.9);
  z-index: 2;
  pointer-events: none;
  backdrop-filter: blur(3px);
}

.card-face.character .card-count { border: 1px solid rgba(74, 222, 128, 0.3); }
.card-face.rush .card-count { border: 1px solid rgba(251, 146, 60, 0.35); }
.card-face.neutral .card-count { border: 1px solid rgba(148, 163, 184, 0.3); }

/* ============================================
   正面战斗属性: 等级(顶部) + 底部状态条(攻距/战力)
   - 三项均有独立背景色块, 完全遮挡卡面文字
   ============================================ */

/* 等级: 顶部居中, 靛蓝药丸 (完全不透明) */
.card-level {
  position: absolute;
  top: 2px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 9px;
  font-weight: 800;
  letter-spacing: 0.3px;
  color: #fff;
  background: linear-gradient(180deg, #5C6BC0, #474FA5);
  padding: 1px 6px;
  border-radius: 8px;
  border: 1px solid rgba(167, 139, 250, 0.6);
  text-shadow: 0 1px 1px rgba(0, 0, 0, 0.8);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.5);
  z-index: 4;
  pointer-events: none;
  white-space: nowrap;
  line-height: 1;
  display: inline-flex;
  align-items: center;
  gap: 2px;
}
.card-level b {
  font-size: 7px;
  font-weight: 700;
  opacity: 0.85;
}

/* 底部状态条: 通栏深色底, 完全遮挡卡面底部 */
.card-stat-bar {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 15px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 4px;
  background: linear-gradient(180deg, rgba(10, 12, 20, 0.92), rgba(5, 7, 12, 0.98));
  border-top: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 0 0 4px 4px;
  z-index: 3;
  pointer-events: none;
  overflow: hidden;
}

/* 状态条子项: 各自带色块背景, 标签+数值 */
.stat-item {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  line-height: 1;
  font-variant-numeric: tabular-nums;
  white-space: nowrap;
  padding: 1px 4px;
  border-radius: 3px;
}
.stat-item b {
  font-size: 7px;
  font-weight: 700;
}

/* 攻距: 左侧, 青色色块 */
.stat-range {
  color: #67e8f9;
  font-size: 10px;
  font-weight: 800;
  background: rgba(34, 211, 238, 0.18);
  border: 1px solid rgba(34, 211, 238, 0.35);
  text-shadow: 0 1px 1px rgba(0, 0, 0, 0.9);
}
.stat-range b {
  opacity: 0.7;
}

/* 战力: 右侧, 红色色块, 适配大数字 */
.stat-power {
  color: #fff;
  font-size: 11px;
  font-weight: 900;
  background: rgba(220, 38, 38, 0.3);
  border: 1px solid rgba(248, 113, 113, 0.4);
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.9);
}
.stat-power b {
  color: #fca5a5;
  opacity: 0.85;
}
</style>
