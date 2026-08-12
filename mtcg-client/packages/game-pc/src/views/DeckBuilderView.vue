<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  CARD_COLOR_OPTIONS,
  CARD_TYPE_OPTIONS,
  cardApi,
  deckApi,
} from '@mtcg/common'
import type { CardVO, DeckCardEntry, DeckValidateResultVO } from '@mtcg/common'
import { useThemeStore } from '@mtcg/common/stores'

useThemeStore()

const MAIN_TARGET = 50
const RUSH_TARGET = 9

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const saving = ref(false)
const dirty = ref(false)

const deckId = computed(() => {
  if (route.path.endsWith('/new') || route.params.id === 'new') {
    return null
  }
  const id = Number(route.params.id)
  return Number.isFinite(id) && id > 0 ? id : null
})

const isNew = computed(() => deckId.value === null)

const deckName = ref('新卡组')
const tags = ref('')
const mainDeck = ref<DeckCardEntry[]>([])
const rushDeck = ref<DeckCardEntry[]>([])
const serverValid = ref<boolean | null>(null)
const validateResult = ref<DeckValidateResultVO | null>(null)

/** cardCode → 展示元数据 */
const cardMeta = reactive<Record<string, CardVO>>({})

const pool = ref<CardVO[]>([])
const poolTotal = ref(0)
const poolPage = ref(1)
const poolPageSize = 24
const poolLoading = ref(false)
const filters = reactive({
  cardName: '',
  cardType: '' as '' | 'CHARACTER' | 'RUSH_POINT',
  color: '',
})

const mainCount = computed(() =>
  mainDeck.value.reduce((s, e) => s + (e.quantity || 0), 0),
)
const rushCount = computed(() =>
  rushDeck.value.reduce((s, e) => s + (e.quantity || 0), 0),
)

const poolPages = computed(() =>
  Math.max(1, Math.ceil(poolTotal.value / poolPageSize)),
)

let dragList: 'main' | 'rush' | null = null
let dragIndex = -1

onMounted(async () => {
  await Promise.all([loadPool(), loadDeckIfNeeded()])
})

watch(
  () => [filters.cardName, filters.cardType, filters.color] as const,
  () => {
    poolPage.value = 1
    void loadPool()
  },
)

async function loadDeckIfNeeded(): Promise<void> {
  if (isNew.value) {
    dirty.value = false
    return
  }
  const id = deckId.value!
  const vo = await deckApi.get(id, loading)
  deckName.value = vo.deckName
  tags.value = vo.tags ?? ''
  mainDeck.value = (vo.mainDeck ?? []).map((e) => ({
    cardCode: e.cardCode,
    quantity: e.quantity,
  }))
  rushDeck.value = (vo.rushDeck ?? []).map((e) => ({
    cardCode: e.cardCode,
    quantity: e.quantity,
  }))
  serverValid.value = vo.isValid
  dirty.value = false
  await warmMetaForDeck()
}

async function warmMetaForDeck(): Promise<void> {
  const codes = new Set([
    ...mainDeck.value.map((e) => e.cardCode),
    ...rushDeck.value.map((e) => e.cardCode),
  ])
  // 用名称模糊搜不到编号；尽量从已加载卡池补齐，其余显示编号
  for (const c of pool.value) {
    rememberCard(c)
  }
  // 再拉一页较大卡池提高命中
  if (codes.size > 0) {
    try {
      const page = await cardApi.list({ pageNum: 1, pageSize: 100 })
      for (const c of page.records ?? []) {
        rememberCard(c)
      }
    } catch {
      // ignore
    }
  }
}

function rememberCard(card: CardVO): void {
  cardMeta[card.cardCode] = card
}

async function loadPool(): Promise<void> {
  const page = await cardApi.list(
    {
      cardName: filters.cardName.trim() || undefined,
      cardType: filters.cardType || undefined,
      color: filters.color || undefined,
      pageNum: poolPage.value,
      pageSize: poolPageSize,
    },
    poolLoading,
  )
  pool.value = page.records ?? []
  poolTotal.value = page.total ?? 0
  for (const c of pool.value) {
    rememberCard(c)
  }
}

function displayName(code: string): string {
  return cardMeta[code]?.cardName ?? code
}

function displayColor(code: string): string {
  return cardMeta[code]?.color ?? ''
}

function markDirty(): void {
  dirty.value = true
  serverValid.value = null
  validateResult.value = null
}

function addCard(card: CardVO): void {
  rememberCard(card)
  if (card.cardType === 'RUSH_POINT') {
    bumpEntry(rushDeck, card.cardCode, 1)
  } else {
    bumpEntry(mainDeck, card.cardCode, 1)
  }
  markDirty()
}

function bumpEntry(list: typeof mainDeck, cardCode: string, delta: number): void {
  const idx = list.value.findIndex((e) => e.cardCode === cardCode)
  if (idx >= 0) {
    const next = list.value[idx].quantity + delta
    if (next <= 0) {
      list.value.splice(idx, 1)
    } else {
      list.value[idx] = { cardCode, quantity: next }
    }
  } else if (delta > 0) {
    list.value.push({ cardCode, quantity: delta })
  }
}

function changeQty(zone: 'main' | 'rush', index: number, delta: number): void {
  const list = zone === 'main' ? mainDeck : rushDeck
  const entry = list.value[index]
  if (!entry) return
  bumpEntry(list, entry.cardCode, delta)
  markDirty()
}

function removeEntry(zone: 'main' | 'rush', index: number): void {
  const list = zone === 'main' ? mainDeck : rushDeck
  list.value.splice(index, 1)
  markDirty()
}

function moveEntry(zone: 'main' | 'rush', index: number, dir: -1 | 1): void {
  const list = zone === 'main' ? mainDeck : rushDeck
  const to = index + dir
  if (to < 0 || to >= list.value.length) return
  const next = list.value.slice()
  const [item] = next.splice(index, 1)
  next.splice(to, 0, item)
  list.value = next
  markDirty()
}

function onEntryDragStart(zone: 'main' | 'rush', index: number, e: DragEvent): void {
  dragList = zone
  dragIndex = index
  e.dataTransfer?.setData('text/plain', `${zone}:${index}`)
  if (e.dataTransfer) e.dataTransfer.effectAllowed = 'move'
}

function onEntryDragOver(e: DragEvent): void {
  e.preventDefault()
}

function onEntryDrop(zone: 'main' | 'rush', toIndex: number, e: DragEvent): void {
  e.preventDefault()
  if (dragList !== zone || dragIndex < 0 || dragIndex === toIndex) {
    dragList = null
    dragIndex = -1
    return
  }
  const list = zone === 'main' ? mainDeck : rushDeck
  const next = list.value.slice()
  const [item] = next.splice(dragIndex, 1)
  next.splice(toIndex, 0, item)
  list.value = next
  dragList = null
  dragIndex = -1
  markDirty()
}

function onEntryDragEnd(): void {
  dragList = null
  dragIndex = -1
}

async function handleSave(): Promise<void> {
  if (!deckName.value.trim()) {
    window.alert('请填写卡组名称')
    return
  }
  const payload = {
    deckName: deckName.value.trim(),
    mainDeck: mainDeck.value.map((e) => ({
      cardCode: e.cardCode,
      quantity: e.quantity,
    })),
    rushDeck: rushDeck.value.map((e) => ({
      cardCode: e.cardCode,
      quantity: e.quantity,
    })),
    tags: tags.value.trim(),
  }
  try {
    if (isNew.value) {
      const id = await deckApi.create(payload, saving)
      dirty.value = false
      await router.replace(`/decks/${id}`)
      window.alert('已创建卡组')
    } else {
      await deckApi.update(deckId.value!, payload, saving)
      dirty.value = false
      const vo = await deckApi.get(deckId.value!)
      serverValid.value = vo.isValid
      window.alert('已保存')
    }
  } catch {
    // notifier
  }
}

async function handleValidate(): Promise<void> {
  if (isNew.value || dirty.value) {
    window.alert('请先保存卡组再校验')
    return
  }
  try {
    const result = await deckApi.validate(deckId.value!, saving)
    validateResult.value = result
    serverValid.value = result.valid
    if (result.valid) {
      window.alert('校验通过')
    }
  } catch {
    // notifier
  }
}

async function handleBack(): Promise<void> {
  if (dirty.value && !window.confirm('有未保存的修改，确定返回？')) {
    return
  }
  await router.push('/decks')
}

function prevPoolPage(): void {
  if (poolPage.value <= 1) return
  poolPage.value -= 1
  void loadPool()
}

function nextPoolPage(): void {
  if (poolPage.value >= poolPages.value) return
  poolPage.value += 1
  void loadPool()
}
</script>

<template>
  <div class="builder-page">
    <header class="top-bar">
      <button type="button" class="btn-ghost" @click="handleBack">← 返回</button>
      <div class="title-block">
        <input
          v-model="deckName"
          class="name-input"
          maxlength="64"
          placeholder="卡组名称"
          @input="markDirty"
        />
        <input
          v-model="tags"
          class="tags-input"
          maxlength="256"
          placeholder="标签（逗号分隔，可选）"
          @input="markDirty"
        />
      </div>
      <div class="counts">
        <span :class="{ warn: mainCount !== MAIN_TARGET }">
          主 {{ mainCount }}/{{ MAIN_TARGET }}
        </span>
        <span :class="{ warn: rushCount !== RUSH_TARGET }">
          冲击 {{ rushCount }}/{{ RUSH_TARGET }}
        </span>
        <span
          v-if="serverValid !== null"
          class="valid-pill"
          :class="serverValid ? 'ok' : 'bad'"
        >
          {{ serverValid ? '合法' : '未合法' }}
        </span>
        <span v-if="dirty" class="dirty">未保存</span>
      </div>
      <div class="top-actions">
        <button type="button" class="btn-ghost" :disabled="saving" @click="handleValidate">
          校验
        </button>
        <button type="button" class="btn-primary" :disabled="saving || loading" @click="handleSave">
          {{ saving ? '保存中…' : '保存' }}
        </button>
      </div>
    </header>

    <div v-if="validateResult && !validateResult.valid" class="error-box">
      <p v-for="(err, i) in validateResult.errors" :key="i">{{ err }}</p>
    </div>

    <div class="builder-layout">
      <!-- 卡池 -->
      <section class="pool-panel">
        <h2>卡池</h2>
        <div class="filters">
          <input
            v-model="filters.cardName"
            class="filter-input"
            placeholder="名称关键词"
          />
          <select v-model="filters.cardType" class="filter-select">
            <option value="">全部类型</option>
            <option
              v-for="o in CARD_TYPE_OPTIONS"
              :key="o.code"
              :value="o.code"
            >
              {{ o.desc }}
            </option>
          </select>
          <select v-model="filters.color" class="filter-select">
            <option value="">全部颜色</option>
            <option
              v-for="o in CARD_COLOR_OPTIONS"
              :key="o.code"
              :value="o.code"
            >
              {{ o.desc }}
            </option>
          </select>
        </div>
        <div class="pool-grid">
          <button
            v-for="card in pool"
            :key="card.id"
            type="button"
            class="pool-card"
            @click="addCard(card)"
          >
            <div class="pool-code">{{ card.cardCode }}</div>
            <div class="pool-name">{{ card.cardName }}</div>
            <div class="pool-meta">
              {{ card.cardType === 'RUSH_POINT' ? '冲击' : '角色' }}
              <template v-if="card.color"> · {{ card.color }}</template>
              <template v-if="card.level != null"> · Lv{{ card.level }}</template>
            </div>
            <span class="pool-add">+</span>
          </button>
        </div>
        <div class="pager">
          <button type="button" class="btn-ghost" :disabled="poolPage <= 1" @click="prevPoolPage">
            上一页
          </button>
          <span>{{ poolPage }} / {{ poolPages }}</span>
          <button
            type="button"
            class="btn-ghost"
            :disabled="poolPage >= poolPages"
            @click="nextPoolPage"
          >
            下一页
          </button>
        </div>
        <p v-if="poolLoading" class="muted">卡池加载中…</p>
      </section>

      <!-- 主 / 冲击 -->
      <section class="deck-panels">
        <div class="zone-panel">
          <h2>主卡组（角色 · {{ mainCount }}/{{ MAIN_TARGET }}）</h2>
          <ul class="entry-list" @dragover="onEntryDragOver">
            <li
              v-for="(entry, index) in mainDeck"
              :key="entry.cardCode + '-' + index"
              class="entry-row"
              draggable="true"
              @dragstart="onEntryDragStart('main', index, $event)"
              @drop="onEntryDrop('main', index, $event)"
              @dragend="onEntryDragEnd"
            >
              <span class="drag">⋮⋮</span>
              <div class="entry-info">
                <div class="entry-name">{{ displayName(entry.cardCode) }}</div>
                <div class="entry-sub">
                  {{ entry.cardCode }}
                  <template v-if="displayColor(entry.cardCode)">
                    · {{ displayColor(entry.cardCode) }}
                  </template>
                </div>
              </div>
              <div class="qty">
                <button type="button" @click="changeQty('main', index, -1)">−</button>
                <span>{{ entry.quantity }}</span>
                <button type="button" @click="changeQty('main', index, 1)">+</button>
              </div>
              <div class="entry-tools">
                <button type="button" title="上移" @click="moveEntry('main', index, -1)">↑</button>
                <button type="button" title="下移" @click="moveEntry('main', index, 1)">↓</button>
                <button type="button" title="移除" @click="removeEntry('main', index)">×</button>
              </div>
            </li>
          </ul>
          <p v-if="mainDeck.length === 0" class="muted">从左侧卡池添加角色卡</p>
        </div>

        <div class="zone-panel">
          <h2>冲击卡组（{{ rushCount }}/{{ RUSH_TARGET }}）</h2>
          <ul class="entry-list" @dragover="onEntryDragOver">
            <li
              v-for="(entry, index) in rushDeck"
              :key="entry.cardCode + '-' + index"
              class="entry-row"
              draggable="true"
              @dragstart="onEntryDragStart('rush', index, $event)"
              @drop="onEntryDrop('rush', index, $event)"
              @dragend="onEntryDragEnd"
            >
              <span class="drag">⋮⋮</span>
              <div class="entry-info">
                <div class="entry-name">{{ displayName(entry.cardCode) }}</div>
                <div class="entry-sub">{{ entry.cardCode }}</div>
              </div>
              <div class="qty">
                <button type="button" @click="changeQty('rush', index, -1)">−</button>
                <span>{{ entry.quantity }}</span>
                <button type="button" @click="changeQty('rush', index, 1)">+</button>
              </div>
              <div class="entry-tools">
                <button type="button" title="上移" @click="moveEntry('rush', index, -1)">↑</button>
                <button type="button" title="下移" @click="moveEntry('rush', index, 1)">↓</button>
                <button type="button" title="移除" @click="removeEntry('rush', index)">×</button>
              </div>
            </li>
          </ul>
          <p v-if="rushDeck.length === 0" class="muted">从左侧卡池添加冲击卡</p>
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped>
.builder-page {
  min-height: 100vh;
  padding: 16px 20px 40px;
  background: var(--bg-base);
  color: var(--text-primary);
}

.top-bar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.title-block {
  display: flex;
  flex-direction: column;
  gap: 6px;
  flex: 1;
  min-width: 200px;
}

.name-input,
.tags-input,
.filter-input,
.filter-select {
  height: 36px;
  padding: 0 12px;
  border-radius: 8px;
  border: 1px solid var(--border);
  background: var(--bg-surface-2);
  color: var(--text-primary);
}

.name-input {
  font-size: var(--font-size-md);
  font-weight: 600;
}

.counts {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

.counts .warn {
  color: var(--accent);
}

.valid-pill {
  padding: 2px 8px;
  border-radius: 999px;
  border: 1px solid var(--border);
}

.valid-pill.ok {
  color: var(--accent);
}

.valid-pill.bad {
  color: var(--text-secondary);
}

.dirty {
  color: var(--accent-blue);
}

.top-actions {
  display: flex;
  gap: 8px;
}

.error-box {
  margin-bottom: 12px;
  padding: 12px 14px;
  border-radius: 10px;
  background: rgba(229, 57, 53, 0.1);
  color: var(--accent-red);
  font-size: var(--font-size-sm);
}

.error-box p {
  margin: 0 0 4px;
}

.builder-layout {
  display: grid;
  grid-template-columns: minmax(280px, 1.1fr) minmax(320px, 1fr);
  gap: 16px;
  align-items: start;
}

@media (max-width: 960px) {
  .builder-layout {
    grid-template-columns: 1fr;
  }
}

.pool-panel,
.zone-panel {
  border-radius: 12px;
  border: 1px solid var(--border);
  background: var(--bg-surface);
  padding: 14px;
}

.pool-panel h2,
.zone-panel h2 {
  margin: 0 0 12px;
  font-size: var(--font-size-md);
}

.filters {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.filter-input {
  flex: 1;
  min-width: 120px;
}

.pool-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 8px;
  max-height: 62vh;
  overflow: auto;
}

.pool-card {
  position: relative;
  text-align: left;
  padding: 10px;
  border-radius: 10px;
  border: 1px solid var(--border);
  background: var(--bg-surface-2);
  color: var(--text-primary);
  cursor: pointer;
}

.pool-card:hover {
  border-color: var(--accent);
}

.pool-code {
  font-size: 11px;
  color: var(--text-secondary);
}

.pool-name {
  margin-top: 4px;
  font-size: var(--font-size-sm);
  font-weight: 600;
  line-height: 1.3;
}

.pool-meta {
  margin-top: 4px;
  font-size: 11px;
  color: var(--text-secondary);
}

.pool-add {
  position: absolute;
  right: 8px;
  top: 8px;
  color: var(--accent);
  font-weight: 700;
}

.pager {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-top: 12px;
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

.deck-panels {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.entry-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
  max-height: 36vh;
  overflow: auto;
}

.entry-row {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 10px;
  border-radius: 8px;
  background: var(--bg-surface-2);
  border: 1px solid var(--border);
}

.drag {
  color: var(--text-disabled);
  cursor: grab;
  user-select: none;
}

.entry-info {
  flex: 1;
  min-width: 0;
}

.entry-name {
  font-size: var(--font-size-sm);
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.entry-sub {
  font-size: 11px;
  color: var(--text-secondary);
}

.qty,
.entry-tools {
  display: flex;
  align-items: center;
  gap: 4px;
}

.qty button,
.entry-tools button {
  width: 26px;
  height: 26px;
  border-radius: 6px;
  border: 1px solid var(--border);
  background: transparent;
  color: var(--text-secondary);
  cursor: pointer;
}

.qty span {
  min-width: 18px;
  text-align: center;
  font-size: var(--font-size-sm);
}

.btn-primary,
.btn-ghost {
  height: 36px;
  padding: 0 14px;
  border-radius: 8px;
  font-size: var(--font-size-sm);
  cursor: pointer;
}

.btn-primary {
  border: none;
  background: var(--accent);
  color: #0b1220;
  font-weight: 600;
}

.btn-ghost {
  border: 1px solid var(--border);
  background: transparent;
  color: var(--text-secondary);
}

.btn-primary:disabled,
.btn-ghost:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.muted {
  margin: 8px 0 0;
  color: var(--text-secondary);
  font-size: var(--font-size-sm);
}
</style>
