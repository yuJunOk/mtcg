<script setup lang="ts">
import { computed, ref, watch, onUnmounted } from 'vue'
import { resolveCardImageUrl } from '@mtcg/common'
import type { CardInstanceVO } from '@mtcg/common'
import { Card } from '@mtcg/common/components'

const props = withDefaults(
  defineProps<{
    slots: (CardInstanceVO | null)[]
    side: 'local' | 'opponent'
    rushDeckCount: number
    ariaLabel?: string
  }>(),
  {
    ariaLabel: '时间线',
  },
)

/** 时间线上实际已有的卡（按入场顺序，一张张往上叠） */
const stackCards = computed(() => {
  const list: Array<{ card: CardInstanceVO; index: number }> = []
  props.slots.forEach((c, i) => {
    if (c) list.push({ card: c, index: i })
  })
  return list
})

const filled = computed(() => stackCards.value.length)

/** 顶牌：最新一张，完整露出 */
const topCard = computed(() => {
  const list = stackCards.value
  return list.length ? list[list.length - 1] : null
})

/** 下层叠露：紧挨顶牌的是次新，越往冲击侧越旧 */
const underCards = computed(() => {
  const list = stackCards.value
  if (list.length <= 1) return []
  return list.slice(0, -1).reverse()
})

const seenIds = ref<Set<string>>(new Set())
const pulseIndex = ref<number | null>(null)
const flash = ref<{
  card: CardInstanceVO
  index: number
} | null>(null)
const peekOpen = ref(false)

let primed = false
let flashTimer: ReturnType<typeof setTimeout> | null = null
let pulseTimer: ReturnType<typeof setTimeout> | null = null
let peekTimer: ReturnType<typeof setTimeout> | null = null

watch(
  () => props.slots.map((c) => c?.instanceId ?? null),
  (ids) => {
    const present = ids.filter((id): id is string => !!id)

    if (!primed) {
      seenIds.value = new Set(present)
      primed = true
      return
    }

    const nextSeen = new Set(seenIds.value)
    for (let i = 0; i < ids.length; i++) {
      const id = ids[i]
      if (!id || seenIds.value.has(id)) continue
      nextSeen.add(id)
      const card = props.slots[i]
      if (card) queueFlash(card, i)
    }

    for (const id of [...nextSeen]) {
      if (!present.includes(id)) nextSeen.delete(id)
    }
    seenIds.value = nextSeen
  },
  { immediate: true },
)

function queueFlash(card: CardInstanceVO, index: number): void {
  if (flashTimer) clearTimeout(flashTimer)
  if (pulseTimer) clearTimeout(pulseTimer)
  peekOpen.value = false
  flash.value = { card, index }
  flashTimer = setTimeout(() => {
    flash.value = null
    pulseIndex.value = index
    pulseTimer = setTimeout(() => {
      pulseIndex.value = null
    }, 700)
  }, 1400)
}

function faceUrl(card: CardInstanceVO | null | undefined): string {
  const code = card?.cardCode
  if (!code) return ''
  const product = code.split('-')[0]
  return resolveCardImageUrl(`card/faces/${product}/${code}.png`)
}

function onStackClick(): void {
  if (!topCard.value) return
  if (peekTimer) clearTimeout(peekTimer)
  peekOpen.value = !peekOpen.value
  if (peekOpen.value) {
    peekTimer = setTimeout(() => {
      peekOpen.value = false
    }, 2200)
  }
}

function dismissFlash(): void {
  if (flashTimer) clearTimeout(flashTimer)
  const idx = flash.value?.index ?? null
  flash.value = null
  if (idx != null) {
    pulseIndex.value = idx
    if (pulseTimer) clearTimeout(pulseTimer)
    pulseTimer = setTimeout(() => {
      pulseIndex.value = null
    }, 700)
  }
}

onUnmounted(() => {
  if (flashTimer) clearTimeout(flashTimer)
  if (pulseTimer) clearTimeout(pulseTimer)
  if (peekTimer) clearTimeout(peekTimer)
})
</script>

<template>
  <div
    class="rush-timeline"
    :class="[side, { 'near-win': filled >= 7, empty: filled === 0 }]"
    :title="`${ariaLabel}（9 张冲击卡胜利）`"
  >
    <!-- 冲击组贴外缘；标识也在外缘侧（对手在上、我方在下）→ 180° 镜像 -->
    <div v-if="side === 'opponent'" class="rush-pile" title="冲击卡组">
      <div class="pile-meta">
        <em>冲击</em>
        <b>{{ rushDeckCount }}</b>
      </div>
      <div class="pile-face">
        <Card v-if="rushDeckCount > 0" back-type="rush" :is-face-down="true" />
        <span v-else class="pile-empty">空</span>
      </div>
    </div>

    <!--
      叠放规则：
      - 开局不预留 9 空槽
      - 有几张叠几张，一张张往上长
      - 顶牌（最新）完整露全；更早的只露细边
    -->
    <button
      type="button"
      class="tl-rail"
      :aria-label="`${ariaLabel} ${filled}/9`"
      @click="onStackClick"
    >
      <!-- 边缘标识：0/9 远离冲击（贴 VS 侧）；空态竖标居中 -->
      <div class="edge-marks" aria-hidden="true">
        <strong class="rail-count" :class="{ near: filled >= 7 }">{{ filled }}/9</strong>
        <span v-if="!topCard" class="edge-label">时间线</span>
      </div>

      <div class="tl-grow">
        <div
          v-if="topCard"
          class="tl-top"
          :class="{ pulse: pulseIndex === topCard.index }"
        >
          <img
            v-if="faceUrl(topCard.card)"
            :src="faceUrl(topCard.card)"
            :alt="topCard.card.cardName || '冲击卡'"
            class="top-art"
          />
          <div v-else class="top-ph">冲击</div>
        </div>

        <div v-if="underCards.length" class="tl-unders" aria-hidden="true">
          <div
            v-for="row in underCards"
            :key="row.card.instanceId"
            class="tl-under"
            :class="{ pulse: pulseIndex === row.index }"
          >
            <img
              v-if="faceUrl(row.card)"
              :src="faceUrl(row.card)"
              alt=""
              class="under-art"
            />
          </div>
        </div>
      </div>
    </button>

    <div v-if="side === 'local'" class="rush-pile" title="冲击卡组">
      <div class="pile-face">
        <Card v-if="rushDeckCount > 0" back-type="rush" :is-face-down="true" />
        <span v-else class="pile-empty">空</span>
      </div>
      <div class="pile-meta">
        <em>冲击</em>
        <b>{{ rushDeckCount }}</b>
      </div>
    </div>

    <Transition name="tl-peek">
      <div v-if="peekOpen && topCard" class="tl-pop" :class="side">
        <div class="pop-face">
          <img
            v-if="faceUrl(topCard.card)"
            :src="faceUrl(topCard.card)"
            :alt="topCard.card.cardName || '冲击卡'"
          />
        </div>
        <p class="pop-meta">
          <b>{{ topCard.card.cardName || '冲击卡' }}</b>
          <span>{{ filled }}/9</span>
        </p>
      </div>
    </Transition>

    <Teleport to="body">
      <Transition name="tl-flash">
        <div
          v-if="flash"
          class="tl-flash"
          role="dialog"
          aria-label="冲击卡翻开"
          @click="dismissFlash"
        >
          <div class="flash-veil" />
          <div class="flash-stage" @click.stop>
            <p class="flash-kicker">
              {{ side === 'local' ? '我方冲击' : '对手冲击' }}
              · {{ flash.index + 1 }}/9
            </p>
            <div class="flash-card">
              <div class="flash-flip">
                <div class="face back" aria-hidden="true">
                  <div class="back-host">
                    <Card back-type="rush" :is-face-down="true" />
                  </div>
                </div>
                <div class="face front">
                  <img
                    v-if="faceUrl(flash.card)"
                    :src="faceUrl(flash.card)"
                    :alt="flash.card.cardName || '冲击卡'"
                  />
                  <div v-else class="front-ph">冲击</div>
                </div>
              </div>
            </div>
            <p class="flash-name">{{ flash.card.cardName || '冲击卡' }}</p>
          </div>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<style scoped>
.rush-timeline {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: stretch;
  height: 100%;
  min-height: 0;
  padding: 3px 2px;
  gap: 3px;
  border-radius: 8px;
  background: color-mix(in srgb, var(--accent-gold) 8%, var(--bg-surface));
  border: 1px solid color-mix(in srgb, var(--accent-gold) 30%, var(--border-light));
  box-shadow: var(--edge-highlight, none);
  overflow: hidden;
}

.rush-timeline.empty {
  background: color-mix(in srgb, var(--accent-gold) 5%, transparent);
  border-style: dashed;
}

.rush-timeline.near-win {
  border-color: color-mix(in srgb, var(--accent-gold) 40%, var(--accent-red));
}

.rush-pile {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1px;
  flex: 0 0 auto;
}

/* 冲击组适中：略大于极小档，仍给叠牌留高 */
.pile-face {
  position: relative;
  width: auto;
  height: clamp(28px, 5.2vh, 38px);
  aspect-ratio: 0.717;
  border-radius: 4px;
  overflow: hidden;
  background: var(--bg-elevated);
  border: 1px solid color-mix(in srgb, var(--accent-gold) 50%, var(--border));
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.pile-face :deep(.card-face) {
  position: absolute !important;
  inset: 0 !important;
  transform: none !important;
  border-radius: 4px !important;
  background-size: cover !important;
}

.pile-empty {
  display: grid;
  place-items: center;
  height: 100%;
  font-size: 9px;
  color: var(--text-disabled);
}

.pile-meta {
  display: flex;
  align-items: baseline;
  gap: 2px;
  line-height: 1;
}

.pile-meta em {
  font-style: normal;
  font-size: 8px;
  font-weight: 700;
  color: var(--text-secondary);
}

.pile-meta b {
  font-size: 10px;
  font-weight: 800;
  font-variant-numeric: tabular-nums;
  color: var(--text-primary);
}

.tl-rail {
  position: relative;
  flex: 1 1 0;
  min-height: 0;
  display: flex;
  flex-direction: column;
  appearance: none;
  margin: 0;
  padding: 0;
  border: none;
  background: transparent;
  cursor: pointer;
  width: 100%;
}

.edge-marks {
  position: absolute;
  z-index: 12;
  inset: 0;
  pointer-events: none;
}

.rail-count {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  min-width: 26px;
  padding: 1px 5px;
  border-radius: 999px;
  font-size: 9px;
  font-weight: 800;
  font-variant-numeric: tabular-nums;
  color: var(--accent-contrast, #0b1220);
  background: var(--accent-gold);
  border: 1px solid color-mix(in srgb, var(--accent-gold) 35%, #fff);
  line-height: 1.2;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.16);
}

/* 我方冲击在下 → 计数在上（朝 VS）；对手冲击在上 → 计数在下（朝 VS） */
.rush-timeline.local .rail-count {
  top: 0;
}

.rush-timeline.opponent .rail-count {
  bottom: 0;
}

.rail-count.near {
  background: var(--accent-red);
  border-color: color-mix(in srgb, var(--accent-red) 35%, #fff);
  color: #fff;
}

.edge-label {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.18em;
  writing-mode: vertical-rl;
  color: color-mix(in srgb, var(--accent-gold) 55%, var(--text-disabled));
}

.tl-grow {
  flex: 1 1 0;
  min-height: 0;
  display: flex;
  flex-direction: column;
  width: 100%;
}

.rush-timeline.local .tl-grow {
  justify-content: flex-end;
  padding-top: 16px;
}

.rush-timeline.opponent .tl-grow {
  justify-content: flex-start;
  padding-bottom: 16px;
}

.tl-top {
  position: relative;
  z-index: 5;
  flex: 0 0 auto;
  width: 100%;
  aspect-ratio: 1488 / 2080;
  max-height: min(100px, 52%);
  border-radius: 6px;
  overflow: hidden;
  border: 1.5px solid color-mix(in srgb, var(--accent-gold) 65%, var(--border));
  background: var(--bg-elevated);
  box-shadow: 0 2px 8px color-mix(in srgb, var(--accent-gold) 20%, transparent);
}

.tl-top.pulse {
  animation: layer-pulse 0.7s ease;
}

.top-art {
  width: 100%;
  height: 100%;
  object-fit: contain;
  display: block;
  background: var(--bg-base);
}

.top-ph {
  display: grid;
  place-items: center;
  height: 100%;
  font-size: 10px;
  font-weight: 700;
  color: var(--accent-gold);
}

.tl-unders {
  display: flex;
  flex-direction: column;
  flex: 0 1 auto;
  min-height: 0;
  max-height: 42%;
  margin-top: -2px;
}

.tl-under {
  position: relative;
  flex: 1 1 0;
  min-height: 7px;
  max-height: 12px;
  margin-top: -2px;
  border-radius: 0 0 4px 4px;
  border: 1px solid color-mix(in srgb, var(--accent-gold) 48%, var(--border));
  border-top: none;
  background: color-mix(in srgb, var(--accent-gold) 16%, var(--bg-surface-2));
  overflow: hidden;
}

.tl-under:first-child {
  margin-top: 0;
}

.tl-under.pulse {
  animation: layer-pulse 0.7s ease;
}

.under-art {
  width: 100%;
  height: 220%;
  object-fit: cover;
  object-position: center top;
  display: block;
  pointer-events: none;
  opacity: 0.9;
}

.tl-pop {
  position: absolute;
  z-index: 30;
  width: 108px;
  top: 40%;
  transform: translateY(-50%);
  display: flex;
  flex-direction: column;
  gap: 5px;
  padding: 7px;
  border-radius: 10px;
  background: var(--bg-elevated);
  border: 1px solid color-mix(in srgb, var(--accent-gold) 45%, var(--border));
  box-shadow: var(--shadow-md);
  pointer-events: none;
}

.tl-pop.opponent {
  right: calc(100% + 8px);
}

.tl-pop.local {
  left: calc(100% + 8px);
}

.pop-face {
  width: 86px;
  height: calc(86px * 2080 / 1488);
  margin: 0 auto;
  border-radius: 6px;
  overflow: hidden;
  background: var(--bg-base);
  border: 1px solid var(--border);
}

.pop-face img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  display: block;
}

.pop-meta {
  margin: 0;
  display: flex;
  justify-content: space-between;
  gap: 4px;
  font-size: 10px;
  color: var(--text-secondary);
}

.pop-meta b {
  color: var(--text-primary);
  font-weight: 700;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.tl-peek-enter-active,
.tl-peek-leave-active {
  transition: opacity 0.18s ease;
}

.tl-peek-enter-from,
.tl-peek-leave-to {
  opacity: 0;
}

@keyframes layer-pulse {
  0%,
  100% {
    filter: brightness(1);
  }
  40% {
    filter: brightness(1.22);
  }
}

/*
  Teleport 到 body 的翻牌层：必须用 :global，否则 scoped 选不中
  （合并原第二段 <style>，避免双 style 块）
*/
:global(.tl-flash) {
  position: fixed;
  inset: 0;
  z-index: 80;
  display: grid;
  place-items: center;
  cursor: pointer;
}

:global(.tl-flash .flash-veil) {
  position: absolute;
  inset: 0;
  background: rgba(8, 10, 16, 0.55);
  backdrop-filter: blur(2px);
}

:global(.tl-flash .flash-stage) {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  cursor: default;
}

:global(.tl-flash .flash-kicker) {
  margin: 0;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.12em;
  color: var(--accent-gold, #d4af37);
}

:global(.tl-flash .flash-name) {
  margin: 0;
  font-size: 14px;
  font-weight: 700;
  color: var(--text-primary, #fff);
}

:global(.tl-flash .flash-card) {
  width: min(200px, 42vw);
  height: calc(min(200px, 42vw) * 2080 / 1488);
  perspective: 900px;
  filter: drop-shadow(0 18px 40px rgba(0, 0, 0, 0.55));
}

:global(.tl-flash .flash-flip) {
  position: relative;
  width: 100%;
  height: 100%;
  transform-style: preserve-3d;
  animation: tl-rush-flip 0.85s cubic-bezier(0.2, 0.85, 0.2, 1) both;
}

:global(.tl-flash .face) {
  position: absolute;
  inset: 0;
  backface-visibility: hidden;
  -webkit-backface-visibility: hidden;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid color-mix(in srgb, var(--accent-gold, #d4af37) 50%, transparent);
}

:global(.tl-flash .face.back) {
  transform: rotateY(0deg);
  background: #1a1e28;
}

:global(.tl-flash .face.front) {
  transform: rotateY(180deg);
  background: #12141c;
}

:global(.tl-flash .back-host) {
  position: relative;
  width: 100%;
  height: 100%;
}

:global(.tl-flash .back-host .card-face) {
  position: absolute !important;
  inset: 0 !important;
  transform: none !important;
  border-radius: 12px !important;
  background-size: cover !important;
}

:global(.tl-flash .face.front img) {
  width: 100%;
  height: 100%;
  object-fit: contain;
  display: block;
}

:global(.tl-flash .front-ph) {
  display: grid;
  place-items: center;
  height: 100%;
  color: var(--accent-gold, #d4af37);
  font-weight: 700;
}

@keyframes tl-rush-flip {
  0% {
    transform: rotateY(0deg) scale(0.86) translateY(12px);
  }
  60% {
    transform: rotateY(180deg) scale(1.04) translateY(0);
  }
  100% {
    transform: rotateY(180deg) scale(1) translateY(0);
  }
}

:global(.tl-flash-enter-active),
:global(.tl-flash-leave-active) {
  transition: opacity 0.22s ease;
}

:global(.tl-flash-enter-from),
:global(.tl-flash-leave-to) {
  opacity: 0;
}

@media (prefers-reduced-motion: reduce) {
  :global(.tl-flash .flash-flip) {
    animation: none;
    transform: rotateY(180deg);
  }
}
</style>
