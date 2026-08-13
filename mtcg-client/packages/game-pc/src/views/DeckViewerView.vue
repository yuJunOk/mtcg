<script setup lang="ts">
/**
 * 卡组构筑查看器（只读）
 * - 浏览主卡组 / 冲击卡组卡面
 * - 点卡看详情；可跳转编辑 / 出战
 */
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { NButton, NEmpty, NInput, NSpin, NTag } from 'naive-ui'
import {
  cardApi,
  collectColorCodes,
  colorCodeToLabel,
  deckApi,
  deckStatusLabel,
  isDeckReady,
  resolveCardImageUrl,
} from '@mtcg/common'
import type { CardVO, DeckCardEntry, DeckVO } from '@mtcg/common'
import { useThemeStore } from '@mtcg/common/stores'
import CardDetailDialog from '@/components/CardDetailDialog.vue'
import DeckCover from '@/components/DeckCover.vue'
import { toast } from '@/feedback'

useThemeStore()

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const deck = ref<DeckVO | null>(null)
const cardMeta = reactive<Record<string, CardVO>>({})
const mainQuery = ref('')
const detailOpen = ref(false)
const detailCard = ref<CardVO | null>(null)

const deckKey = computed(() => {
  const raw = route.params.id
  const id = typeof raw === 'string' ? raw.trim() : Array.isArray(raw) ? raw[0]?.trim() : ''
  return id || ''
})

const mainEntries = computed(() => deck.value?.mainDeck ?? [])
const rushEntries = computed(() => deck.value?.rushDeck ?? [])

const filteredMain = computed(() => {
  const q = mainQuery.value.trim().toLowerCase()
  if (!q) return mainEntries.value
  return mainEntries.value.filter((e) => {
    const name = (cardMeta[e.cardCode]?.cardName ?? '').toLowerCase()
    return name.includes(q) || e.cardCode.toLowerCase().includes(q)
  })
})

const colorCodes = computed(() => {
  if (deck.value?.colors?.length) {
    return collectColorCodes(deck.value.colors)
  }
  return collectColorCodes(mainEntries.value.map((e) => cardMeta[e.cardCode]?.color))
})

const expandedMain = computed(() => expandEntries(filteredMain.value))
const expandedRush = computed(() => expandEntries(rushEntries.value))

function expandEntries(entries: DeckCardEntry[]): Array<{ cardCode: string; key: string }> {
  const rows: Array<{ cardCode: string; key: string }> = []
  for (const e of entries) {
    const qty = e.quantity || 0
    for (let i = 0; i < qty; i++) {
      rows.push({ cardCode: e.cardCode, key: `${e.cardCode}-${i}` })
    }
  }
  return rows
}

function displayName(code: string): string {
  return cardMeta[code]?.cardName ?? code
}

function cardImage(code: string): string {
  return resolveCardImageUrl(cardMeta[code]?.imagePath)
}

function onImgError(e: Event): void {
  const el = e.target as HTMLImageElement
  el.style.opacity = '0'
}

function rememberCard(card: CardVO): void {
  cardMeta[card.cardCode] = card
}

async function warmMeta(codes: string[]): Promise<void> {
  const missing = [...new Set(codes)].filter((c) => !cardMeta[c])
  await Promise.all(
    missing.map(async (code) => {
      try {
        const page = await cardApi.list({ cardCode: code, pageNum: 1, pageSize: 1 })
        const card = page.records?.[0]
        if (card) rememberCard(card)
      } catch {
        // ignore
      }
    }),
  )
}

async function loadDeck(): Promise<void> {
  if (!deckKey.value) {
    toast.warning('无效的卡组编码')
    await router.replace('/decks')
    return
  }
  try {
    const vo = await deckApi.get(deckKey.value, loading)
    deck.value = vo
    const codes = [
      ...(vo.mainDeck ?? []).map((e) => e.cardCode),
      ...(vo.rushDeck ?? []).map((e) => e.cardCode),
    ]
    await warmMeta(codes)
  } catch {
    deck.value = null
    toast.error('加载卡组失败')
  }
}

function openCard(code: string): void {
  const card = cardMeta[code]
  if (!card) {
    toast.info('卡牌信息尚未加载')
    return
  }
  detailCard.value = card
  detailOpen.value = true
}

function goEdit(): void {
  void router.push(`/decks/${deckKey.value}/edit`)
}

function goPlay(): void {
  if (!deck.value) return
  localStorage.setItem('mtcg_ready_deck_id', String(deck.value.id))
  void router.push('/match')
}

function copyCode(): void {
  const code = deck.value?.deckCode
  if (!code) return
  void navigator.clipboard.writeText(code).then(
    () => toast.success('卡组编码已复制'),
    () => toast.info(code),
  )
}

onMounted(() => {
  void loadDeck()
})
</script>

<template>
  <div class="viewer">
    <header class="topbar">
      <n-button quaternary @click="router.push('/decks')">←</n-button>
      <div class="title-block">
        <h1>{{ deck?.deckName || '卡组查看' }}</h1>
        <n-tag
          v-if="deck"
          size="small"
          :type="isDeckReady(deck) ? 'success' : 'default'"
          :bordered="false"
        >
          {{ deckStatusLabel(deck) }}
        </n-tag>
      </div>
      <div class="actions">
        <n-button
          v-if="deck && isDeckReady(deck)"
          type="primary"
          :disabled="loading"
          @click="goPlay"
        >
          出战
        </n-button>
        <n-button secondary :disabled="loading || !deck" @click="goEdit">编辑</n-button>
      </div>
    </header>

    <div v-if="loading && !deck" class="spin-wrap">
      <n-spin size="large" />
    </div>

    <div v-else-if="!deck" class="empty">
      <n-empty description="卡组不存在或无权查看">
        <template #extra>
          <n-button type="primary" @click="router.push('/decks')">返回列表</n-button>
        </template>
      </n-empty>
    </div>

    <div v-else class="body">
      <aside class="summary">
        <div class="cover-wrap">
          <DeckCover :image-path="deck.coverImagePath" placeholder="🃏" />
        </div>
        <dl class="meta">
          <div v-if="deck.deckCode">
            <dt>编码</dt>
            <dd>
              <button type="button" class="code" @click="copyCode">{{ deck.deckCode }}</button>
            </dd>
          </div>
          <div>
            <dt>主卡组</dt>
            <dd>{{ deck.mainDeckSize ?? 0 }}/50</dd>
          </div>
          <div>
            <dt>冲击卡组</dt>
            <dd>{{ deck.rushDeckSize ?? 0 }}/9</dd>
          </div>
          <div v-if="colorCodes.length">
            <dt>颜色</dt>
            <dd class="colors">
              <span
                v-for="c in colorCodes"
                :key="c"
                class="color-chip"
                :class="c.toLowerCase()"
                :title="colorCodeToLabel(c)"
              >
                {{ colorCodeToLabel(c) }}
              </span>
            </dd>
          </div>
          <div v-if="deck.tags">
            <dt>标签</dt>
            <dd>{{ deck.tags }}</dd>
          </div>
          <div>
            <dt>公开</dt>
            <dd>{{ deck.isPublic ? '是' : '否' }}</dd>
          </div>
          <div>
            <dt>可复制</dt>
            <dd>{{ deck.isCopyable ? '是' : '否' }}</dd>
          </div>
        </dl>
      </aside>

      <main class="board">
        <section class="zone">
          <div class="zone-head">
            <h2>主卡组</h2>
            <span>{{ deck.mainDeckSize ?? 0 }}/50</span>
          </div>
          <n-input
            v-if="mainEntries.length > 0"
            v-model:value="mainQuery"
            class="search"
            size="small"
            clearable
            placeholder="搜索（名称/编号）"
          />
          <div v-if="expandedMain.length === 0" class="zone-empty">
            {{ mainEntries.length === 0 ? '主卡组为空' : '没有匹配卡牌' }}
          </div>
          <div v-else class="face-grid">
            <button
              v-for="row in expandedMain"
              :key="row.key"
              type="button"
              class="face"
              :title="displayName(row.cardCode)"
              @click="openCard(row.cardCode)"
            >
              <img
                v-if="cardImage(row.cardCode)"
                :src="cardImage(row.cardCode)"
                :alt="displayName(row.cardCode)"
                loading="lazy"
                @error="onImgError"
              />
              <span v-else class="ph">{{ displayName(row.cardCode) }}</span>
            </button>
          </div>
        </section>

        <section class="zone rush">
          <div class="zone-head">
            <h2>冲击卡组</h2>
            <span>{{ deck.rushDeckSize ?? 0 }}/9</span>
          </div>
          <div v-if="expandedRush.length === 0" class="zone-empty">冲击卡组为空</div>
          <div v-else class="face-grid">
            <button
              v-for="row in expandedRush"
              :key="row.key"
              type="button"
              class="face"
              :title="displayName(row.cardCode)"
              @click="openCard(row.cardCode)"
            >
              <img
                v-if="cardImage(row.cardCode)"
                :src="cardImage(row.cardCode)"
                :alt="displayName(row.cardCode)"
                loading="lazy"
                @error="onImgError"
              />
              <span v-else class="ph">冲击</span>
            </button>
          </div>
        </section>
      </main>
    </div>

    <CardDetailDialog
      :open="detailOpen"
      :card="detailCard"
      @update:open="detailOpen = $event"
      @close="detailOpen = false"
    />
  </div>
</template>

<style scoped>
.viewer {
  --deck-col: 240px;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--bg-base);
  color: var(--text-primary);
}

.topbar {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
  min-height: 52px;
  padding: 8px 16px;
  border-bottom: 1px solid var(--border);
  background: var(--bg-surface);
}

.title-block {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
  flex: 1;
}

.title-block h1 {
  margin: 0;
  font-size: 16px;
  font-weight: 700;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

.spin-wrap,
.empty {
  flex: 1;
  display: grid;
  place-items: center;
  padding: 48px 24px;
}

.body {
  flex: 1;
  min-height: 0;
  display: grid;
  grid-template-columns: var(--deck-col) 1fr;
  overflow: hidden;
}

.summary {
  border-right: 1px solid var(--border);
  padding: 16px;
  overflow: auto;
  background: var(--bg-surface);
}

.cover-wrap {
  width: 100%;
  aspect-ratio: 1488 / 2080;
  border-radius: var(--radius-md);
  overflow: hidden;
  border: 1px solid var(--border);
  background: var(--bg-surface-2);
  margin-bottom: 14px;
}

.cover-wrap :deep(.deck-cover),
.cover-wrap :deep(img) {
  width: 100%;
  height: 100%;
  object-fit: contain;
  object-position: center top;
}

.meta {
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.meta > div {
  display: grid;
  grid-template-columns: 72px 1fr;
  gap: 8px;
}

.meta dt {
  margin: 0;
  font-size: 12px;
  color: var(--text-secondary);
}

.meta dd {
  margin: 0;
  font-size: 13px;
  word-break: break-all;
}

.colors {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.color-chip {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 2px 8px 2px 6px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
  background: var(--bg-surface-2);
  border: 1px solid var(--border);
}

.color-chip::before {
  content: '';
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: currentColor;
}

.color-chip.red {
  color: #e53935;
}
.color-chip.yellow {
  color: #f9a825;
}
.color-chip.blue {
  color: #5c6bc0;
}
.color-chip.green {
  color: #43a047;
}
.color-chip.orange {
  color: #fb8c00;
}
.color-chip.purple {
  color: #8e24aa;
}

.code {
  padding: 0;
  border: none;
  background: none;
  color: var(--accent);
  font: inherit;
  cursor: pointer;
}

.code:hover {
  text-decoration: underline;
}

.board {
  min-height: 0;
  overflow: auto;
  padding: 16px 18px 28px;
}

.zone + .zone {
  margin-top: 22px;
}

.zone-head {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 10px;
}

.zone-head h2 {
  margin: 0;
  font-size: 14px;
  font-weight: 700;
}

.zone.rush .zone-head h2 {
  color: var(--accent-gold);
}

.zone-head span {
  font-size: 12px;
  color: var(--text-secondary);
}

.search {
  margin-bottom: 12px;
  max-width: 280px;
}

.zone-empty {
  padding: 24px;
  border: 1px dashed var(--border);
  border-radius: var(--radius-md);
  color: var(--text-secondary);
  font-size: 13px;
  text-align: center;
}

.face-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, var(--card-face-w));
  grid-auto-rows: max-content;
  gap: 12px 10px;
  align-content: start;
  justify-content: start;
}

.face {
  position: relative;
  width: var(--card-face-w);
  height: var(--card-face-h);
  padding: 0;
  border: 1px solid var(--border);
  border-radius: 10px;
  overflow: hidden;
  background: var(--bg-surface);
  cursor: pointer;
  appearance: none;
  transition: transform 0.15s ease, border-color 0.15s ease, box-shadow 0.15s ease;
}

.face:hover {
  transform: translateY(-2px);
  border-color: var(--accent);
  box-shadow: var(--shadow-md);
}

.face img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  object-position: center top;
  display: block;
}

.face .ph {
  display: grid;
  place-items: center;
  height: 100%;
  padding: 8px;
  font-size: 11px;
  color: var(--text-secondary);
  text-align: center;
}

@media (max-width: 900px) {
  .body {
    grid-template-columns: 1fr;
  }

  .summary {
    border-right: none;
    border-bottom: 1px solid var(--border);
    display: grid;
    grid-template-columns: 120px 1fr;
    gap: 14px;
    align-items: start;
  }

  .cover-wrap {
    margin-bottom: 0;
  }
}
</style>
