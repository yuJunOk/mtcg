<script setup lang="ts">
import { nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'

const props = defineProps<{
  /** 内容变化时（列表长度）重新计算可否滚动 */
  itemCount?: number
}>()

const viewport = ref<HTMLElement | null>(null)
const canPrev = ref(false)
const canNext = ref(false)
const index = ref(0)

let resizeObs: ResizeObserver | null = null

function gapPx(el: HTMLElement): number {
  const raw = getComputedStyle(el).columnGap || getComputedStyle(el).gap || '12px'
  const n = Number.parseFloat(raw)
  return Number.isFinite(n) ? n : 12
}

function stepPx(el: HTMLElement): number {
  const card = el.querySelector('.strip-card') as HTMLElement | null
  if (!card) return 168
  return card.offsetWidth + gapPx(el)
}

function maxIndexOf(el: HTMLElement): number {
  const step = stepPx(el)
  if (step <= 0) return 0
  const overflow = el.scrollWidth - el.clientWidth
  if (overflow <= 1) return 0
  return Math.max(0, Math.ceil(overflow / step - 1e-6))
}

function syncFromScroll(): void {
  const el = viewport.value
  if (!el) {
    canPrev.value = false
    canNext.value = false
    return
  }
  const step = stepPx(el)
  const maxIdx = maxIndexOf(el)
  const fromScroll = step > 0 ? Math.round(el.scrollLeft / step) : 0
  index.value = Math.min(Math.max(0, fromScroll), maxIdx)
  canPrev.value = index.value > 0
  canNext.value = index.value < maxIdx
}

function scrollByCard(dir: -1 | 1): void {
  const el = viewport.value
  if (!el) return
  const step = stepPx(el)
  const maxIdx = maxIndexOf(el)
  const next = Math.min(Math.max(0, index.value + dir), maxIdx)
  index.value = next
  canPrev.value = next > 0
  canNext.value = next < maxIdx
  el.scrollTo({ left: next * step, behavior: 'smooth' })
}

async function refresh(): Promise<void> {
  await nextTick()
  syncFromScroll()
}

watch(
  () => props.itemCount,
  () => {
    void refresh()
  },
)

onMounted(() => {
  const el = viewport.value
  if (el && typeof ResizeObserver !== 'undefined') {
    resizeObs = new ResizeObserver(() => {
      syncFromScroll()
    })
    resizeObs.observe(el)
  }
  void refresh()
})

onBeforeUnmount(() => {
  resizeObs?.disconnect()
  resizeObs = null
})

defineExpose({ refresh })
</script>

<template>
  <div class="rail">
    <button
      type="button"
      class="rail-btn prev"
      :disabled="!canPrev"
      aria-label="向左一张"
      @click="scrollByCard(-1)"
    >
      <svg viewBox="0 0 16 16" width="16" height="16" aria-hidden="true">
        <path
          d="M10.5 3.5 5.5 8l5 4.5"
          fill="none"
          stroke="currentColor"
          stroke-width="1.8"
          stroke-linecap="round"
          stroke-linejoin="round"
        />
      </svg>
    </button>
    <div ref="viewport" class="rail-view" @scroll.passive="syncFromScroll">
      <slot />
    </div>
    <button
      type="button"
      class="rail-btn next"
      :disabled="!canNext"
      aria-label="向右一张"
      @click="scrollByCard(1)"
    >
      <svg viewBox="0 0 16 16" width="16" height="16" aria-hidden="true">
        <path
          d="M5.5 3.5 10.5 8l-5 4.5"
          fill="none"
          stroke="currentColor"
          stroke-width="1.8"
          stroke-linecap="round"
          stroke-linejoin="round"
        />
      </svg>
    </button>
  </div>
</template>

<style scoped>
.rail {
  position: relative;
}

.rail-view {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  overflow-y: hidden;
  padding: 6px 2px 8px;
  margin: -6px -2px -2px;
  scroll-snap-type: x mandatory;
  scroll-behavior: smooth;
  scrollbar-width: none;
}

.rail-view::-webkit-scrollbar {
  display: none;
}

.rail-view :deep(.strip-card) {
  scroll-snap-align: start;
}

.rail-btn {
  position: absolute;
  top: 50%;
  z-index: 2;
  display: grid;
  place-items: center;
  padding: 0;
  width: 40px;
  height: 40px;
  border: 1px solid var(--border);
  border-radius: 999px;
  background: color-mix(in srgb, var(--bg-surface) 92%, transparent);
  color: var(--text-primary);
  cursor: pointer;
  box-shadow: var(--shadow-sm);
  transform: translateY(-50%);
  transition:
    opacity var(--transition-fast),
    border-color var(--transition-fast),
    color var(--transition-fast),
    background var(--transition-fast);
}

.rail-btn svg {
  display: block;
}

.rail-btn.prev {
  left: 4px;
}

.rail-btn.next {
  right: 4px;
}

.rail-btn:hover:not(:disabled) {
  border-color: color-mix(in srgb, var(--accent) 45%, var(--border));
  color: var(--accent);
  background: var(--bg-surface);
}

.rail-btn:disabled {
  opacity: 0;
  pointer-events: none;
}
</style>
