<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { deckApi } from '@mtcg/common'
import type { DeckVO } from '@mtcg/common'
import DeckCover from '@/components/DeckCover.vue'

const router = useRouter()
const loading = ref(false)
const validatingId = ref<number | null>(null)
const decks = ref<DeckVO[]>([])
const dragFrom = ref<number | null>(null)
const orderSnapshot = ref<DeckVO[]>([])

const hasValidDeck = computed(() => decks.value.some((d) => d.isValid === true))
const noticeText = ref('')

onMounted(() => {
  void loadDecks()
})

async function loadDecks(): Promise<void> {
  decks.value = await deckApi.list(undefined, loading)
}

async function handleValidate(deck: DeckVO): Promise<void> {
  validatingId.value = deck.id
  noticeText.value = ''
  try {
    const result = await deckApi.validate(deck.id)
    noticeText.value = result.valid ? `「${deck.deckName}」校验通过` : result.errors.join('；')
    await loadDecks()
  } catch {
    // notifier
  } finally {
    validatingId.value = null
  }
}

async function handleDelete(deck: DeckVO): Promise<void> {
  if (!window.confirm(`删除「${deck.deckName}」？`)) return
  try {
    await deckApi.delete(deck.id, loading)
    await loadDecks()
  } catch {
    // notifier
  }
}

function playDeck(deck: DeckVO): void {
  localStorage.setItem('mtcg_ready_deck_id', String(deck.id))
  void router.push('/match')
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
        <p class="hint">主 50 · 冲击 9 · 拖拽排序</p>
      </div>
      <button type="button" class="primary" :disabled="loading" @click="router.push('/decks/new')">
        新建卡组
      </button>
    </header>

    <p v-if="noticeText" class="notice">{{ noticeText }}</p>
    <p v-else-if="decks.length > 0 && !hasValidDeck" class="notice">
      暂无合法卡组，完成构筑并校验后方可开打
    </p>

    <div v-if="!loading && decks.length === 0" class="empty">
      <p>还没有卡组</p>
      <button type="button" class="primary" @click="router.push('/decks/new')">开始构筑</button>
    </div>

    <div v-else class="grid" @dragover="onDragOver">
      <article
        v-for="(deck, index) in decks"
        :key="deck.id"
        class="tile"
        :class="{ dragging: dragFrom === index, ok: deck.isValid }"
        draggable="true"
        @dragstart="onDragStart(index, $event)"
        @drop="onDrop(index, $event)"
        @dragend="onDragEnd"
        @click="router.push(`/decks/${deck.id}`)"
      >
        <div class="cover">
          <DeckCover :image-path="deck.coverImagePath" placeholder="🃏" lazy />
        </div>
        <div class="info">
          <div class="title-row">
            <h2>{{ deck.deckName }}</h2>
            <span class="status" :class="{ on: deck.isValid }">
              {{ deck.isValid ? '合法' : '未完成' }}
            </span>
          </div>
          <p class="meta">
            主 {{ deck.mainDeckSize ?? 0 }}/50
            <span class="sep">·</span>
            冲击 {{ deck.rushDeckSize ?? 0 }}/9
          </p>
          <div class="ops" @click.stop>
            <button v-if="deck.isValid" type="button" class="play" @click="playDeck(deck)">出战</button>
            <button type="button" :disabled="validatingId === deck.id" @click="handleValidate(deck)">
              校验
            </button>
            <button type="button" class="danger" @click="handleDelete(deck)">删除</button>
          </div>
        </div>
      </article>
    </div>

    <p v-if="loading" class="loading">加载中</p>
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
  margin-bottom: 14px;
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
  text-align: center;
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  background: var(--bg-surface);
}

.empty p {
  margin: 0 0 16px;
  color: var(--text-secondary);
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, var(--card-cover-w));
  grid-auto-rows: max-content;
  gap: 18px;
  align-content: start;
  justify-content: start;
}

.tile {
  display: flex;
  flex-direction: column;
  width: var(--card-cover-w);
  flex: none;
  align-self: start;
  padding: 0;
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  background: color-mix(in srgb, var(--bg-surface) 92%, transparent);
  color: inherit;
  overflow: hidden;
  cursor: pointer;
  box-shadow: var(--edge-highlight), var(--shadow-sm);
  transition: border-color var(--transition-fast), transform var(--transition-fast);
}

.tile:hover {
  transform: translateY(-3px);
  border-color: color-mix(in srgb, var(--accent) 45%, var(--border));
}

.tile.ok {
  border-color: color-mix(in srgb, var(--accent) 32%, var(--border));
}

.tile.dragging {
  opacity: 0.4;
}

.cover {
  position: relative;
  width: var(--card-cover-w);
  height: var(--card-cover-h);
  flex: none;
  background: var(--bg-surface-2);
}

.ops {
  display: flex;
  gap: 8px;
  margin-top: 10px;
}

.ops button {
  height: 28px;
  padding: 0 8px;
  border: none;
  border-radius: var(--radius-sm);
  background: transparent;
  color: var(--text-secondary);
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
}

.ops button.play {
  color: var(--accent-contrast);
  background: var(--accent);
  padding: 0 12px;
}

.ops button:hover:not(:disabled):not(.play) {
  color: var(--accent);
}

.ops button.danger:hover:not(:disabled) {
  color: var(--accent-red);
}

.ops button:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.info {
  padding: 12px 12px 14px;
}

.title-row {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

.title-row h2 {
  margin: 0;
  flex: 1;
  font-size: 15px;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.status {
  flex-shrink: 0;
  font-size: 11px;
  color: var(--text-secondary);
}

.status.on {
  color: var(--accent);
  font-weight: 600;
}

.meta {
  margin: 6px 0 0;
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
}

.sep {
  margin: 0 4px;
  opacity: 0.5;
}

.primary {
  height: 40px;
  padding: 0 18px;
  border: none;
  border-radius: var(--radius-sm);
  background: var(--accent);
  color: var(--accent-contrast);
  font-size: var(--font-size-sm);
  font-weight: 600;
  cursor: pointer;
}

.primary:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.loading {
  text-align: center;
  color: var(--text-secondary);
  font-size: var(--font-size-sm);
  margin-top: 40px;
}
</style>
