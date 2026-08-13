<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { NButton, NEmpty, NSpin, NTag } from 'naive-ui'
import { deckApi } from '@mtcg/common'
import type { DeckVO } from '@mtcg/common'
import DeckCover from '@/components/DeckCover.vue'
import { confirm, toast } from '@/feedback'

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
    if (result.valid) {
      noticeText.value = `「${deck.deckName}」校验通过`
      toast.success(`「${deck.deckName}」校验通过`)
    } else {
      const detail = result.errors.join('；')
      noticeText.value = detail
      toast.warning(detail || '校验未通过')
    }
    await loadDecks()
  } catch {
    // HTTP 错误由全局 notifier 提示
  } finally {
    validatingId.value = null
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
    await deckApi.delete(deck.id, loading)
    toast.success('卡组已删除')
    await loadDecks()
  } catch {
    // HTTP 错误由全局 notifier 提示
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
      <n-button type="primary" :disabled="loading" @click="router.push('/decks/new')">
        新建卡组
      </n-button>
    </header>

    <p v-if="noticeText" class="notice">{{ noticeText }}</p>
    <p v-else-if="decks.length > 0 && !hasValidDeck" class="notice">
      暂无合法卡组，完成构筑并校验后方可开打
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
            <n-tag
              size="small"
              :type="deck.isValid ? 'success' : 'default'"
              :bordered="false"
            >
              {{ deck.isValid ? '合法' : '未完成' }}
            </n-tag>
          </div>
          <p class="meta">
            主 {{ deck.mainDeckSize ?? 0 }}/50
            <span class="sep">·</span>
            冲击 {{ deck.rushDeckSize ?? 0 }}/9
          </p>
          <div class="ops" @click.stop>
            <n-button
              v-if="deck.isValid"
              type="primary"
              size="tiny"
              @click="playDeck(deck)"
            >
              出战
            </n-button>
            <n-button
              size="tiny"
              quaternary
              :loading="validatingId === deck.id"
              @click="handleValidate(deck)"
            >
              校验
            </n-button>
            <n-button size="tiny" quaternary type="error" @click="handleDelete(deck)">
              删除
            </n-button>
          </div>
        </div>
      </article>
    </div>

    <div v-if="loading && decks.length > 0" class="spin-wrap subtle">
      <n-spin size="small" />
    </div>
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
  gap: 4px;
  margin-top: 10px;
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

.meta {
  margin: 6px 0 0;
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
}

.sep {
  margin: 0 4px;
  opacity: 0.5;
}
</style>
