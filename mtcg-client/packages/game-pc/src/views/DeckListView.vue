<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { NButton, NEmpty, NInput, NSpin } from 'naive-ui'
import {
  deckApi,
  collectColorCodes,
  formatColorLabels,
  deckStatusLabel,
  isDeckReady,
} from '@mtcg/common'
import type { DeckVO } from '@mtcg/common'
import DeckCover from '@/components/DeckCover.vue'
import MtcgDialog from '@/components/MtcgDialog.vue'
import { confirm, toast } from '@/feedback'

const router = useRouter()
const loading = ref(false)
const decks = ref<DeckVO[]>([])
const dragFrom = ref<number | null>(null)
const orderSnapshot = ref<DeckVO[]>([])

const copyOpen = ref(false)
const copyCode = ref('')
const copying = ref(false)

const hasReadyDeck = computed(() => decks.value.some((d) => isDeckReady(d)))

onMounted(() => {
  void loadDecks()
})

function deckPath(deck: DeckVO): string {
  return deck.deckCode || String(deck.id)
}

function deckColors(deck: DeckVO): string[] {
  return collectColorCodes(deck.colors ?? [])
}

function deckColorText(deck: DeckVO): string {
  return formatColorLabels(deck.colors ?? [])
}

/** 左缘色带：双色时用渐变 */
function accentStyle(deck: DeckVO): Record<string, string> {
  const colors = deckColors(deck)
  const map: Record<string, string> = {
    RED: '#e53935',
    YELLOW: '#f9a825',
    BLUE: '#5c6bc0',
    GREEN: '#43a047',
    ORANGE: '#fb8c00',
    PURPLE: '#8e24aa',
  }
  if (colors.length === 0) return {}
  if (colors.length === 1) {
    return { '--tile-accent': map[colors[0]] ?? 'var(--accent)' }
  }
  const a = map[colors[0]] ?? 'var(--accent)'
  const b = map[colors[1]] ?? a
  return {
    '--tile-accent': a,
    '--tile-accent-2': b,
  }
}

async function loadDecks(): Promise<void> {
  decks.value = await deckApi.list(undefined, loading)
}

function openCopyDialog(): void {
  copyCode.value = ''
  copyOpen.value = true
}

async function confirmCopy(): Promise<void> {
  const code = copyCode.value.trim().toUpperCase()
  if (!code) {
    toast.warning('请输入卡组编码')
    return
  }
  try {
    const newCode = await deckApi.copyByCode({ deckCode: code }, copying)
    copyOpen.value = false
    toast.success('已复制到我的卡组')
    await loadDecks()
    await router.push(`/decks/${newCode}/edit`)
  } catch {
    // HTTP 错误由全局 notifier 提示
  }
}

async function handleDelete(deck: DeckVO): Promise<void> {
  const ok = await confirm({
    title: '删除卡组',
    content: `确定删除「${deck.deckName}」？此操作不可恢复。`,
    positiveText: '删除',
    danger: true,
  })
  if (!ok) return
  try {
    await deckApi.delete(deckPath(deck), loading)
    toast.success('卡组已删除')
    await loadDecks()
  } catch {
    // HTTP 错误由全局 notifier 提示
  }
}

function openViewer(deck: DeckVO): void {
  void router.push(`/decks/${deckPath(deck)}`)
}

function openEditor(deck: DeckVO): void {
  void router.push(`/decks/${deckPath(deck)}/edit`)
}

function playDeck(deck: DeckVO): void {
  localStorage.setItem('mtcg_ready_deck_id', String(deck.id))
  void router.push('/match')
}

async function copyOwnCode(deck: DeckVO): Promise<void> {
  const code = deck.deckCode
  if (!code) return
  try {
    await navigator.clipboard.writeText(code)
    toast.success('卡组编码已复制')
  } catch {
    toast.info(code)
  }
}

function onDragStart(index: number, e: DragEvent): void {
  dragFrom.value = index
  orderSnapshot.value = decks.value.slice()
  e.dataTransfer?.setData('text/plain', String(index))
  if (e.dataTransfer) e.dataTransfer.effectAllowed = 'move'
}

function onDragOver(e: DragEvent): void {
  e.preventDefault()
}

function onDrop(toIndex: number, e: DragEvent): void {
  e.preventDefault()
  const from = dragFrom.value
  dragFrom.value = null
  if (from === null || from === toIndex) return
  const next = decks.value.slice()
  const [item] = next.splice(from, 1)
  next.splice(toIndex, 0, item)
  decks.value = next
  void persistOrder()
}

function onDragEnd(): void {
  dragFrom.value = null
}

async function persistOrder(): Promise<void> {
  try {
    await deckApi.reorder({
      items: decks.value.map((d, i) => ({ id: d.id, sortOrder: i })),
    })
  } catch {
    decks.value = orderSnapshot.value.slice()
  }
}
</script>

<template>
  <div class="page">
    <header class="head">
      <div class="title-block">
        <h1>我的卡组</h1>
        <p class="hint">点封面查看 · 拖拽排序 · 导入编码 · 未完成自动为草稿</p>
      </div>
      <div class="head-actions">
        <n-button secondary :disabled="loading" @click="openCopyDialog">导入编码</n-button>
        <n-button type="primary" :disabled="loading" @click="router.push('/decks/new')">
          新建卡组
        </n-button>
      </div>
    </header>

    <p v-if="decks.length > 0 && !hasReadyDeck" class="notice">
      暂无可用卡组，完善构筑后即可出战
    </p>

    <div v-if="loading && decks.length === 0" class="spin-wrap">
      <n-spin size="large" />
    </div>

    <div v-else-if="!loading && decks.length === 0" class="empty">
      <n-empty description="还没有卡组">
        <template #extra>
          <n-button type="primary" @click="router.push('/decks/new')">开始构筑</n-button>
        </template>
      </n-empty>
    </div>

    <div v-else class="grid" @dragover="onDragOver">
      <article
        v-for="(deck, index) in decks"
        :key="deck.id"
        class="tile"
        :class="{
          dragging: dragFrom === index,
          ok: isDeckReady(deck),
          dual: deckColors(deck).length > 1,
        }"
        :style="accentStyle(deck)"
        draggable="true"
        @dragstart="onDragStart(index, $event)"
        @drop="onDrop(index, $event)"
        @dragend="onDragEnd"
      >
        <button
          type="button"
          class="thumb"
          :title="'查看 ' + deck.deckName"
          @click="openViewer(deck)"
        >
          <DeckCover :image-path="deck.coverImagePath" fit="cover" placeholder="🃏" lazy />
          <span class="thumb-eye" aria-hidden="true">👁</span>
        </button>

        <div class="body">
          <div class="top">
            <h2 :title="deck.deckName">{{ deck.deckName }}</h2>
            <span class="badge" :class="{ on: isDeckReady(deck) }">{{ deckStatusLabel(deck) }}</span>
          </div>

          <div class="meta">
            <span v-if="deckColors(deck).length" class="pips" :title="deckColorText(deck)">
              <i
                v-for="c in deckColors(deck)"
                :key="c"
                class="pip"
                :class="c.toLowerCase()"
              />
              <em>{{ deckColorText(deck) }}</em>
            </span>
            <span class="counts">
              主 {{ deck.mainDeckSize ?? 0 }}/50
              <span class="sep">·</span>
              冲击 {{ deck.rushDeckSize ?? 0 }}/9
            </span>
          </div>

          <div class="foot">
            <button
              v-if="deck.deckCode"
              type="button"
              class="code"
              :title="'复制 ' + deck.deckCode"
              @click="copyOwnCode(deck)"
            >
              {{ deck.deckCode }}
            </button>
            <span v-else class="code muted">—</span>

            <div class="ops">
              <n-button
                v-if="isDeckReady(deck)"
                type="primary"
                size="small"
                @click="playDeck(deck)"
              >
                出战
              </n-button>
              <n-button size="small" secondary @click="openEditor(deck)">编辑</n-button>
              <n-button size="small" quaternary type="error" @click="handleDelete(deck)">
                删除
              </n-button>
            </div>
          </div>
        </div>
      </article>
    </div>

    <div v-if="loading && decks.length > 0" class="spin-wrap subtle">
      <n-spin size="small" />
    </div>

    <MtcgDialog
      :open="copyOpen"
      title="导入卡组编码"
      width="420px"
      body-height="auto"
      confirm-text="导入"
      :confirm-loading="copying"
      :confirm-disabled="!copyCode.trim()"
      :close-disabled="copying"
      @update:open="copyOpen = $event"
      @confirm="confirmCopy"
    >
      <div class="copy-form">
        <p class="copy-hint">输入他人开启「可复制」的卡组编码（如 D-A3F8Q2NW）</p>
        <n-input
          v-model:value="copyCode"
          placeholder="D-XXXXXXXX"
          :disabled="copying"
          @keyup.enter="confirmCopy"
        />
      </div>
    </MtcgDialog>
  </div>
</template>

<style scoped>
.page {
  width: min(var(--shell-max), 100%);
  margin: 0 auto;
  padding: 20px var(--space-lg) 48px;
}

.head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-md);
  margin-bottom: 16px;
}

.head-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  flex-shrink: 0;
}

.title-block {
  display: flex;
  flex-wrap: wrap;
  align-items: baseline;
  gap: 8px 12px;
  min-width: 0;
}

.head h1 {
  margin: 0;
  font-size: var(--font-size-lg);
  font-weight: 700;
}

.hint {
  margin: 0;
  font-size: 12px;
  color: var(--text-secondary);
}

.notice {
  margin: 0 0 var(--space-md);
  padding: 12px 14px;
  border-radius: var(--radius-md);
  background: color-mix(in srgb, var(--bg-surface) 88%, transparent);
  border: 1px solid var(--border);
  color: var(--text-secondary);
  font-size: var(--font-size-sm);
}

.empty {
  padding: 48px 24px;
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  background: var(--bg-surface);
}

.spin-wrap {
  display: flex;
  justify-content: center;
  padding: 48px 0;
}

.spin-wrap.subtle {
  padding: 24px 0 0;
}

/* 两列横卡：避免单列瘦长竖条 */
.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 14px;
  align-content: start;
}

.tile {
  --tile-accent: var(--accent);
  --tile-accent-2: var(--tile-accent);
  position: relative;
  display: grid;
  grid-template-columns: 96px minmax(0, 1fr);
  gap: 0 14px;
  min-height: 132px;
  padding: 12px 14px 12px 12px;
  border: 1px solid var(--border);
  border-radius: 14px;
  background:
    linear-gradient(
      105deg,
      color-mix(in srgb, var(--tile-accent) 10%, transparent) 0%,
      transparent 42%
    ),
    var(--bg-surface);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
  transition:
    border-color var(--transition-fast),
    transform var(--transition-fast),
    box-shadow var(--transition-fast);
}

.tile::before {
  content: '';
  position: absolute;
  left: 0;
  top: 10px;
  bottom: 10px;
  width: 3px;
  border-radius: 0 3px 3px 0;
  background: linear-gradient(180deg, var(--tile-accent), var(--tile-accent-2));
  opacity: 0.9;
}

.tile:hover {
  transform: translateY(-2px);
  border-color: color-mix(in srgb, var(--tile-accent) 45%, var(--border));
  box-shadow: var(--shadow-md);
}

.tile.ok {
  border-color: color-mix(in srgb, var(--accent) 26%, var(--border));
}

.tile.dragging {
  opacity: 0.4;
}

.thumb {
  position: relative;
  z-index: 1;
  width: 96px;
  height: 134px;
  padding: 0;
  border: 1px solid var(--border);
  border-radius: 10px;
  overflow: hidden;
  background: var(--bg-surface-2);
  cursor: pointer;
  appearance: none;
  flex: none;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: border-color var(--transition-fast), box-shadow var(--transition-fast);
}

.thumb:hover,
.thumb:focus-visible {
  border-color: var(--accent);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
}

.thumb :deep(.deck-cover),
.thumb :deep(img) {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center 18%;
}

.thumb-eye {
  position: absolute;
  right: 6px;
  bottom: 6px;
  width: 24px;
  height: 24px;
  display: grid;
  place-items: center;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.92);
  font-size: 12px;
  opacity: 0;
  transform: scale(0.9);
  transition: opacity 0.15s ease, transform 0.15s ease;
  pointer-events: none;
}

.thumb:hover .thumb-eye,
.thumb:focus-visible .thumb-eye {
  opacity: 1;
  transform: scale(1);
}

.body {
  position: relative;
  z-index: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 8px;
  padding: 2px 0;
}

.top {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  min-width: 0;
}

.top h2 {
  margin: 0;
  flex: 1;
  min-width: 0;
  font-size: 16px;
  font-weight: 700;
  line-height: 1.35;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.badge {
  flex: none;
  padding: 2px 8px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 650;
  letter-spacing: 0.02em;
  background: color-mix(in srgb, var(--text-secondary) 16%, transparent);
  color: var(--text-secondary);
}

.badge.on {
  background: color-mix(in srgb, var(--accent) 18%, transparent);
  color: var(--accent);
}

.meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px 12px;
  font-size: 12px;
  color: var(--text-secondary);
}

.pips {
  display: inline-flex;
  align-items: center;
  gap: 5px;
}

.pips em {
  font-style: normal;
  font-weight: 600;
  color: var(--text-primary);
}

.pip {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  box-shadow: inset 0 0 0 1px rgba(0, 0, 0, 0.12);
}

.pip.red { background: #e53935; }
.pip.yellow { background: #f9a825; }
.pip.blue { background: #5c6bc0; }
.pip.green { background: #43a047; }
.pip.orange { background: #fb8c00; }
.pip.purple { background: #8e24aa; }

.sep {
  margin: 0 4px;
  opacity: 0.45;
}

.foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  min-width: 0;
}

.code {
  margin: 0;
  padding: 0;
  border: none;
  background: none;
  color: var(--accent);
  font-size: 11px;
  font-family: Consolas, ui-monospace, monospace;
  letter-spacing: 0.02em;
  cursor: pointer;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  min-width: 0;
}

.code:hover {
  text-decoration: underline;
}

.code.muted {
  color: var(--text-secondary);
  cursor: default;
}

.ops {
  display: flex;
  flex-wrap: nowrap;
  gap: 6px;
  flex-shrink: 0;
}

.copy-form {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 4px 0 8px;
}

.copy-hint {
  margin: 0;
  font-size: 13px;
  color: var(--text-secondary);
  line-height: 1.5;
}

@media (max-width: 720px) {
  .grid {
    grid-template-columns: 1fr;
  }
}
</style>
