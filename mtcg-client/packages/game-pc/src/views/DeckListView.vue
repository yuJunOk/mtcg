<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { deckApi } from '@mtcg/common'
import type { DeckVO } from '@mtcg/common'
import { useThemeStore } from '@mtcg/common/stores'

useThemeStore()

const router = useRouter()
const loading = ref(false)
const validatingId = ref<number | null>(null)
const decks = ref<DeckVO[]>([])
const dragFrom = ref<number | null>(null)
const orderSnapshot = ref<DeckVO[]>([])

const hasValidDeck = computed(() => decks.value.some((d) => d.isValid === true))

onMounted(() => {
  void loadDecks()
})

async function loadDecks(): Promise<void> {
  decks.value = await deckApi.list(undefined, loading)
}

function goHome(): void {
  router.push('/')
}

function goCreate(): void {
  router.push('/decks/new')
}

function goEdit(id: number): void {
  router.push(`/decks/${id}`)
}

async function handleValidate(deck: DeckVO): Promise<void> {
  validatingId.value = deck.id
  try {
    const result = await deckApi.validate(deck.id)
    if (result.valid) {
      window.alert('校验通过：卡组合法')
    } else {
      window.alert(`校验未通过：\n${result.errors.join('\n')}`)
    }
    await loadDecks()
  } catch {
    // notifier
  } finally {
    validatingId.value = null
  }
}

async function handleDelete(deck: DeckVO): Promise<void> {
  if (!window.confirm(`确定删除卡组「${deck.deckName}」？`)) {
    return
  }
  try {
    await deckApi.delete(deck.id, loading)
    await loadDecks()
  } catch {
    // notifier
  }
}

function onDragStart(index: number, e: DragEvent): void {
  dragFrom.value = index
  orderSnapshot.value = decks.value.slice()
  e.dataTransfer?.setData('text/plain', String(index))
  if (e.dataTransfer) {
    e.dataTransfer.effectAllowed = 'move'
  }
}

function onDragOver(e: DragEvent): void {
  e.preventDefault()
  if (e.dataTransfer) {
    e.dataTransfer.dropEffect = 'move'
  }
}

function onDrop(toIndex: number, e: DragEvent): void {
  e.preventDefault()
  const from = dragFrom.value
  dragFrom.value = null
  if (from === null || from === toIndex) {
    return
  }
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
  const items = decks.value.map((d, i) => ({ id: d.id, sortOrder: i }))
  try {
    await deckApi.reorder({ items })
  } catch {
    decks.value = orderSnapshot.value.slice()
  }
}

function formatTags(tags: string | null): string {
  if (!tags || !tags.trim()) return ''
  return tags
}
</script>

<template>
  <div class="deck-list-page">
    <header class="page-header">
      <button type="button" class="btn-ghost" @click="goHome">← 首页</button>
      <div class="header-main">
        <h1>我的卡组</h1>
        <p class="subtitle">拖拽卡片可调整列表顺序；开打需合法卡组（主 50 + 冲击 9）</p>
      </div>
      <button type="button" class="btn-primary" :disabled="loading" @click="goCreate">
        新建卡组
      </button>
    </header>

    <div v-if="!loading && decks.length === 0" class="empty-state">
      <p>还没有卡组</p>
      <p class="hint">从卡池挑选角色卡与冲击卡，保存并校验通过后即可对战</p>
      <button type="button" class="btn-primary" @click="goCreate">去构筑</button>
    </div>

    <div v-else-if="!hasValidDeck && decks.length > 0" class="banner-warn">
      暂无合法卡组。请编辑卡组凑齐 50+9 并点击「校验」，或新建后保存校验。
      <button type="button" class="btn-link" @click="goCreate">去构筑</button>
    </div>

    <ul class="deck-list" @dragover="onDragOver">
      <li
        v-for="(deck, index) in decks"
        :key="deck.id"
        class="deck-card"
        :class="{ dragging: dragFrom === index }"
        draggable="true"
        @dragstart="onDragStart(index, $event)"
        @drop="onDrop(index, $event)"
        @dragend="onDragEnd"
      >
        <div class="drag-handle" title="拖拽排序">⋮⋮</div>
        <div class="deck-body" @click="goEdit(deck.id)">
          <div class="deck-title-row">
            <h2>{{ deck.deckName }}</h2>
            <span
              class="valid-badge"
              :class="deck.isValid ? 'ok' : 'bad'"
            >
              {{ deck.isValid ? '合法' : '未合法' }}
            </span>
          </div>
          <p class="deck-meta">
            主卡组 {{ deck.mainDeckSize ?? 0 }} / 50 · 冲击 {{ deck.rushDeckSize ?? 0 }} / 9
          </p>
          <p v-if="formatTags(deck.tags)" class="deck-tags">{{ formatTags(deck.tags) }}</p>
        </div>
        <div class="deck-actions">
          <button type="button" class="btn-ghost" @click="goEdit(deck.id)">编辑</button>
          <button
            type="button"
            class="btn-ghost"
            :disabled="validatingId === deck.id"
            @click="handleValidate(deck)"
          >
            {{ validatingId === deck.id ? '校验中…' : '校验' }}
          </button>
          <button type="button" class="btn-danger" @click="handleDelete(deck)">删除</button>
        </div>
      </li>
    </ul>

    <p v-if="loading" class="loading-tip">加载中…</p>
  </div>
</template>

<style scoped>
.deck-list-page {
  min-height: 100vh;
  padding: 24px 28px 48px;
  background:
    radial-gradient(ellipse 70% 40% at 10% 0%, rgba(0, 212, 170, 0.08), transparent 55%),
    var(--bg-base);
  color: var(--text-primary);
}

.page-header {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 24px;
}

.header-main {
  flex: 1;
}

.header-main h1 {
  margin: 0;
  font-size: var(--font-size-xl);
}

.subtitle {
  margin: 6px 0 0;
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

.empty-state,
.banner-warn {
  padding: 20px 24px;
  border-radius: 12px;
  border: 1px solid var(--border);
  background: var(--bg-surface);
  margin-bottom: 20px;
}

.empty-state {
  text-align: center;
}

.empty-state .hint {
  color: var(--text-secondary);
  font-size: var(--font-size-sm);
}

.banner-warn {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  color: var(--text-secondary);
  font-size: var(--font-size-sm);
}

.deck-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.deck-card {
  display: flex;
  align-items: stretch;
  gap: 12px;
  padding: 14px 16px;
  border-radius: 12px;
  background: var(--bg-surface);
  border: 1px solid var(--border);
  box-shadow: var(--shadow-sm);
  transition: border-color 0.15s ease, opacity 0.15s ease;
}

.deck-card.dragging {
  opacity: 0.55;
}

.drag-handle {
  display: flex;
  align-items: center;
  padding: 0 4px;
  color: var(--text-disabled);
  cursor: grab;
  user-select: none;
  letter-spacing: -2px;
}

.deck-body {
  flex: 1;
  min-width: 0;
  cursor: pointer;
}

.deck-title-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.deck-title-row h2 {
  margin: 0;
  font-size: var(--font-size-md);
  font-weight: 600;
}

.valid-badge {
  font-size: var(--font-size-xs);
  padding: 2px 8px;
  border-radius: 999px;
  border: 1px solid var(--border);
}

.valid-badge.ok {
  color: var(--accent);
  border-color: color-mix(in srgb, var(--accent) 50%, var(--border));
}

.valid-badge.bad {
  color: var(--text-secondary);
}

.deck-meta,
.deck-tags {
  margin: 6px 0 0;
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

.deck-actions {
  display: flex;
  flex-direction: column;
  gap: 6px;
  justify-content: center;
}

.btn-primary,
.btn-ghost,
.btn-danger,
.btn-link {
  border-radius: 8px;
  cursor: pointer;
  font-size: var(--font-size-sm);
}

.btn-primary {
  height: 40px;
  padding: 0 18px;
  border: none;
  background: var(--accent);
  color: #0b1220;
  font-weight: 600;
}

.btn-ghost {
  height: 32px;
  padding: 0 12px;
  border: 1px solid var(--border);
  background: transparent;
  color: var(--text-secondary);
}

.btn-ghost:hover:not(:disabled) {
  color: var(--accent);
  border-color: var(--accent);
}

.btn-danger {
  height: 32px;
  padding: 0 12px;
  border: 1px solid color-mix(in srgb, var(--accent-red) 40%, var(--border));
  background: transparent;
  color: var(--accent-red);
}

.btn-link {
  border: none;
  background: transparent;
  color: var(--text-link);
  padding: 0;
}

.btn-primary:disabled,
.btn-ghost:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.loading-tip {
  text-align: center;
  color: var(--text-secondary);
}
</style>
