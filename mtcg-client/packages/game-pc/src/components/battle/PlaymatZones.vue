<script setup lang="ts">
import { Card } from '@mtcg/common/components'

withDefaults(
  defineProps<{
    side: 'local' | 'opponent'
    retreat: number
    voidZone: number
    characterDeck: number
    playerName?: string
    handCount?: number | null
  }>(),
  {
    playerName: '',
    handCount: null,
  },
)
</script>

<template>
  <div class="playmat-zones" :class="side">
    <!--
      180° 镜像（外缘 = 各自手牌侧）：
      对手：身份+手数 → 卡组 → 撤退 → 虚空
      我方：虚空 → 撤退 → 卡组 → 身份+手数
      尺寸全部随侧栏高度/宽度弹性分配，避免溢出
    -->
    <template v-if="side === 'opponent'">
      <div class="id-row" :title="playerName || '对手'">
        <span class="dot opponent" aria-hidden="true" />
        <strong class="id-name">对手</strong>
        <span v-if="handCount != null" class="id-hand">
          手 <b>{{ handCount }}</b>
        </span>
      </div>

      <div class="zone-slot deck" title="对手卡组">
        <div class="slot-face">
          <Card v-if="characterDeck > 0" back-type="character" :is-face-down="true" />
          <span v-else class="slot-empty">空</span>
        </div>
        <div class="slot-meta">
          <em>卡组</em>
          <b>{{ characterDeck }}</b>
        </div>
      </div>

      <div class="zone-slot" title="撤退区">
        <div class="slot-face" :class="{ vacant: retreat <= 0 }">
          <Card v-if="retreat > 0" back-type="character" />
        </div>
        <div class="slot-meta">
          <em>撤退</em>
          <b>{{ retreat }}</b>
        </div>
      </div>

      <div class="zone-slot" title="虚空区">
        <div class="slot-face" :class="{ vacant: voidZone <= 0 }">
          <Card v-if="voidZone > 0" back-type="character" />
        </div>
        <div class="slot-meta">
          <em>虚空</em>
          <b>{{ voidZone }}</b>
        </div>
      </div>
    </template>

    <template v-else>
      <div class="zone-slot" title="虚空区">
        <div class="slot-face" :class="{ vacant: voidZone <= 0 }">
          <Card v-if="voidZone > 0" back-type="character" />
        </div>
        <div class="slot-meta">
          <em>虚空</em>
          <b>{{ voidZone }}</b>
        </div>
      </div>

      <div class="zone-slot" title="撤退区">
        <div class="slot-face" :class="{ vacant: retreat <= 0 }">
          <Card v-if="retreat > 0" back-type="character" />
        </div>
        <div class="slot-meta">
          <em>撤退</em>
          <b>{{ retreat }}</b>
        </div>
      </div>

      <div class="zone-slot deck" title="角色卡组">
        <div class="slot-face">
          <Card v-if="characterDeck > 0" back-type="character" :is-face-down="true" />
          <span v-else class="slot-empty">空</span>
        </div>
        <div class="slot-meta">
          <em>卡组</em>
          <b>{{ characterDeck }}</b>
        </div>
      </div>

      <div class="id-row" title="我方">
        <span class="dot local" aria-hidden="true" />
        <strong class="id-name">我方</strong>
        <span v-if="handCount != null" class="id-hand">
          手 <b>{{ handCount }}</b>
        </span>
      </div>
    </template>
  </div>
</template>

<style scoped>
.playmat-zones {
  display: flex;
  flex-direction: column;
  align-items: stretch;
  gap: 0.4vh;
  width: 100%;
  height: 100%;
  min-height: 0;
  overflow: hidden;
}

.playmat-zones.local {
  justify-content: flex-end;
}

.playmat-zones.opponent {
  justify-content: flex-start;
}

.id-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: center;
  gap: 0.2em 0.35em;
  flex: 0 0 auto;
  padding: 0.3vh 0.15vw;
  border-radius: 0.4em;
  background: var(--bg-surface);
  border: 1px solid var(--border-light);
  line-height: 1;
}

.dot {
  width: 0.55em;
  height: 0.55em;
  border-radius: 50%;
  flex-shrink: 0;
}

.dot.opponent {
  background: var(--accent-blue);
}

.dot.local {
  background: var(--accent);
}

.id-name {
  font-size: clamp(9px, 1.05vh, 11px);
  font-weight: 700;
  color: var(--text-primary);
}

.id-hand {
  font-size: clamp(9px, 1.05vh, 11px);
  font-weight: 700;
  color: var(--text-secondary);
  font-variant-numeric: tabular-nums;
}

.id-hand b {
  font-size: 1.25em;
  font-weight: 800;
  color: var(--accent-blue);
}

.playmat-zones.local .id-hand b {
  color: var(--accent);
}

/* 三槽均分剩余高度，卡面随槽伸缩 */
.zone-slot {
  display: grid;
  grid-template-rows: minmax(0, 1fr) auto;
  align-items: center;
  justify-items: center;
  gap: 0.25vh;
  flex: 1 1 0;
  min-height: 0;
  padding: 0.3vh 0.15vw;
  border-radius: 0.4em;
  background: var(--bg-surface);
  border: 1px solid var(--border-light);
  box-shadow: var(--shadow-sm), var(--edge-highlight, none);
}

.zone-slot.deck {
  border-color: color-mix(in srgb, var(--accent) 22%, var(--border-light));
}

/* 窄栏内卡面：宽度吃满侧栏，高度封顶，避免虚空/撤退/卡组显得过胖 */
.slot-face {
  position: relative;
  place-self: center;
  width: 88%;
  max-width: 100%;
  height: auto;
  max-height: min(100%, 9.5vh);
  aspect-ratio: 0.717;
  border-radius: 0.3em;
  overflow: hidden;
  background: var(--bg-elevated);
  border: 1px solid var(--border);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.22);
}

.slot-face.vacant {
  border-style: dashed;
  border-color: color-mix(in srgb, var(--border) 85%, var(--text-secondary));
  background: color-mix(in srgb, var(--bg-surface-2) 80%, var(--bg-elevated));
  box-shadow: none;
}

.slot-face :deep(.card-face) {
  position: absolute !important;
  inset: 0 !important;
  transform: none !important;
  border-radius: inherit !important;
  background-size: cover !important;
}

.slot-empty {
  display: grid;
  place-items: center;
  height: 100%;
  font-size: clamp(8px, 1vh, 10px);
  font-weight: 700;
  color: var(--text-disabled);
}

.slot-meta {
  display: flex;
  align-items: baseline;
  justify-content: center;
  gap: 0.25em;
  line-height: 1;
  flex: 0 0 auto;
}

.slot-meta em {
  font-style: normal;
  font-size: clamp(7px, 0.9vh, 9px);
  font-weight: 700;
  color: var(--text-secondary);
}

.slot-meta b {
  font-size: clamp(10px, 1.2vh, 12px);
  font-weight: 800;
  font-variant-numeric: tabular-nums;
  color: var(--text-primary);
}
</style>
