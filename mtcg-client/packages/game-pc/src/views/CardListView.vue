<script setup lang="ts">
import { computed, onMounted, onUnmounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  CARD_ATTACK_RANGE_FILTER_OPTIONS,
  CARD_COLOR_OPTIONS,
  CARD_LEVEL_FILTER_OPTIONS,
  CARD_RARITY_FILTER_CODES,
  CARD_RARITY_OPTIONS,
  CARD_TRAIT_FILTER_OPTIONS,
  CARD_TYPE_OPTIONS,
  cardApi,
  codeToDesc,
  productApi,
  resolveCardImageUrl,
} from '@mtcg/common'
import type { CardColor, CardRarity, CardType, CardVO, ProductVO } from '@mtcg/common'
import MtcgDialog from '@/components/MtcgDialog.vue'

//#region 状态
const route = useRoute()
const router = useRouter()

const loading = ref(false)
const cards = ref<CardVO[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(20)
const products = ref<ProductVO[]>([])
const advancedOpen = ref(false)
const detailOpen = ref(false)
const selected = ref<CardVO | null>(null)

const filters = reactive({
  cardName: '',
  cardType: '' as '' | CardType,
  color: '' as '' | CardColor,
  productCode: typeof route.query.product === 'string' ? route.query.product : '',
  rarity: '' as '' | CardRarity,
  level: null as number | null,
  attackRange: null as number | null,
  trait: '',
})

let searchTimer: ReturnType<typeof setTimeout> | null = null
//#endregion

//#region 计算属性
const pageCount = computed(() => Math.max(1, Math.ceil(total.value / pageSize.value)))

const hasActiveFilters = computed(() =>
  Boolean(
    filters.cardName.trim() ||
      filters.cardType ||
      filters.color ||
      filters.productCode ||
      filters.rarity ||
      filters.level != null ||
      filters.attackRange != null ||
      filters.trait,
  ),
)

const pageItems = computed((): Array<number | 'ellipsis'> => {
  const totalPages = pageCount.value
  const current = pageNum.value
  if (totalPages <= 7) {
    return Array.from({ length: totalPages }, (_, i) => i + 1)
  }
  const items: Array<number | 'ellipsis'> = [1]
  const start = Math.max(2, current - 1)
  const end = Math.min(totalPages - 1, current + 1)
  if (start > 2) items.push('ellipsis')
  for (let i = start; i <= end; i++) items.push(i)
  if (end < totalPages - 1) items.push('ellipsis')
  items.push(totalPages)
  return items
})

const selectedTypeLabel = computed(() =>
  selected.value ? codeToDesc(CARD_TYPE_OPTIONS, selected.value.cardType as CardType) : '',
)

const selectedColorLabel = computed(() => {
  const color = selected.value?.color
  if (!color) return ''
  return codeToDesc(CARD_COLOR_OPTIONS, color as CardColor)
})

const selectedRarityLabel = computed(() => {
  const rarity = selected.value?.rarity
  if (!rarity) return ''
  const desc = codeToDesc(CARD_RARITY_OPTIONS, rarity as CardRarity)
  return desc && desc !== rarity ? `${rarity} · ${desc}` : rarity
})
//#endregion

//#region 方法
function applyRouteQuery(): void {
  const product = route.query.product
  const next = typeof product === 'string' ? product : ''
  if (next === filters.productCode) return
  filters.productCode = next
}

function cardImage(card: CardVO): string {
  return resolveCardImageUrl(card.imagePath)
}

function onImgError(e: Event): void {
  const el = e.target as HTMLImageElement
  el.style.visibility = 'hidden'
}

function toggleChip(field: 'cardType' | 'color' | 'rarity' | 'trait', value: string): void {
  const current = filters[field] as string
  ;(filters[field] as string) = current === value ? '' : value
}

function toggleNumChip(field: 'level' | 'attackRange', value: number): void {
  filters[field] = filters[field] === value ? null : value
}

function clearFilters(): void {
  filters.cardName = ''
  filters.cardType = ''
  filters.color = ''
  filters.productCode = ''
  filters.rarity = ''
  filters.level = null
  filters.attackRange = null
  filters.trait = ''
  pageNum.value = 1
  if (route.query.product) {
    void router.replace({ path: '/cards', query: {} })
  }
  void loadCards()
}

async function loadProducts(): Promise<void> {
  try {
    const page = await productApi.list({ pageNum: 1, pageSize: 50 })
    products.value = page.records ?? []
  } catch {
    products.value = []
  }
}

async function loadCards(): Promise<void> {
  const page = await cardApi.list(
    {
      cardName: filters.cardName.trim() || undefined,
      cardType: filters.cardType || undefined,
      color: filters.color || undefined,
      productCode: filters.productCode || undefined,
      rarity: filters.rarity || undefined,
      level: filters.level ?? undefined,
      attackRange: filters.attackRange ?? undefined,
      trait: filters.trait || undefined,
      pageNum: pageNum.value,
      pageSize: pageSize.value,
    },
    loading,
  )
  cards.value = page.records ?? []
  total.value = page.total ?? 0
}

function goPage(n: number): void {
  if (n < 1 || n > pageCount.value || n === pageNum.value) return
  pageNum.value = n
  void loadCards()
}

function openDetail(card: CardVO): void {
  selected.value = card
  detailOpen.value = true
}

function closeDetail(): void {
  detailOpen.value = false
}
//#endregion

//#region 生命周期
onMounted(async () => {
  await Promise.all([loadProducts(), loadCards()])
})

watch(
  () =>
    [
      filters.cardType,
      filters.color,
      filters.productCode,
      filters.rarity,
      filters.level,
      filters.attackRange,
      filters.trait,
      pageSize.value,
    ] as const,
  () => {
    pageNum.value = 1
    void loadCards()
  },
)

watch(
  () => filters.cardName,
  () => {
    if (searchTimer) clearTimeout(searchTimer)
    searchTimer = setTimeout(() => {
      pageNum.value = 1
      void loadCards()
    }, 280)
  },
)

watch(
  () => route.query.product,
  () => {
    applyRouteQuery()
  },
)

onUnmounted(() => {
  if (searchTimer) clearTimeout(searchTimer)
})
//#endregion
</script>

<template>
  <div class="page">
    <header class="head">
      <h1>卡表</h1>
    </header>

    <div class="filter-bar">
      <input v-model="filters.cardName" class="search" type="search" placeholder="搜索卡牌" />
      <select v-model="filters.productCode" class="select">
        <option value="">商品系列</option>
        <option v-for="p in products" :key="p.productCode" :value="p.productCode">
          {{ p.productCode }}
        </option>
      </select>
      <div class="quick-colors">
        <button
          v-for="o in CARD_COLOR_OPTIONS"
          :key="o.code"
          type="button"
          class="color-dot"
          :class="[o.code.toLowerCase(), { on: filters.color === o.code }]"
          :title="o.desc + '色'"
          @click="toggleChip('color', o.code)"
        />
      </div>
      <button type="button" class="ghost sm" @click="advancedOpen = !advancedOpen">
        {{ advancedOpen ? '收起' : '高级筛选' }}
      </button>
      <button v-if="hasActiveFilters" type="button" class="link" @click="clearFilters">清除所选</button>
      <span class="count">共 {{ total }} 条结果</span>
    </div>

    <div v-show="advancedOpen" class="advanced">
      <div class="row">
        <span>类型</span>
        <div class="chips">
          <button type="button" class="chip" :class="{ on: !filters.cardType }" @click="filters.cardType = ''">
            全部
          </button>
          <button
            v-for="o in CARD_TYPE_OPTIONS"
            :key="o.code"
            type="button"
            class="chip"
            :class="{ on: filters.cardType === o.code }"
            @click="toggleChip('cardType', o.code)"
          >
            {{ o.desc }}
          </button>
        </div>
      </div>
      <div class="row">
        <span>稀有度</span>
        <div class="chips">
          <button type="button" class="chip" :class="{ on: !filters.rarity }" @click="filters.rarity = ''">
            全部
          </button>
          <button
            v-for="code in CARD_RARITY_FILTER_CODES"
            :key="code"
            type="button"
            class="chip mono"
            :class="{ on: filters.rarity === code }"
            @click="toggleChip('rarity', code)"
          >
            {{ code }}
          </button>
        </div>
      </div>
      <div class="row">
        <span>等级</span>
        <div class="chips">
          <button type="button" class="chip" :class="{ on: filters.level == null }" @click="filters.level = null">
            全部
          </button>
          <button
            v-for="lv in CARD_LEVEL_FILTER_OPTIONS"
            :key="lv"
            type="button"
            class="chip mono"
            :class="{ on: filters.level === lv }"
            @click="toggleNumChip('level', lv)"
          >
            {{ lv }}
          </button>
        </div>
      </div>
      <div class="row">
        <span>攻击距离</span>
        <div class="chips">
          <button
            type="button"
            class="chip"
            :class="{ on: filters.attackRange == null }"
            @click="filters.attackRange = null"
          >
            全部
          </button>
          <button
            v-for="r in CARD_ATTACK_RANGE_FILTER_OPTIONS"
            :key="r"
            type="button"
            class="chip mono"
            :class="{ on: filters.attackRange === r }"
            @click="toggleNumChip('attackRange', r)"
          >
            {{ r }}
          </button>
        </div>
      </div>
      <div class="row">
        <span>特征</span>
        <div class="chips wrap">
          <button type="button" class="chip" :class="{ on: !filters.trait }" @click="filters.trait = ''">全部</button>
          <button
            v-for="t in CARD_TRAIT_FILTER_OPTIONS"
            :key="t"
            type="button"
            class="chip"
            :class="{ on: filters.trait === t }"
            @click="toggleChip('trait', t)"
          >
            {{ t }}
          </button>
        </div>
      </div>
    </div>

    <p v-if="loading && cards.length === 0" class="hint">加载中…</p>
    <p v-else-if="!loading && cards.length === 0" class="hint">没有匹配卡牌</p>

    <div v-else class="grid">
      <button
        v-for="card in cards"
        :key="card.id"
        type="button"
        class="tile"
        :title="card.cardName"
        @click="openDetail(card)"
      >
        <span class="art">
          <img
            v-if="cardImage(card)"
            :src="cardImage(card)"
            :alt="card.cardName"
            loading="lazy"
            @error="onImgError"
          />
          <span v-else class="art-ph">{{ card.cardType === 'RUSH_POINT' ? '冲击' : '角色' }}</span>
        </span>
        <span class="tile-name">{{ card.cardName }}</span>
        <span class="tile-meta">{{ card.cardCode }}</span>
      </button>
    </div>

    <div class="pager">
      <label>
        每页
        <select v-model.number="pageSize" class="select compact">
          <option :value="20">20</option>
          <option :value="50">50</option>
          <option :value="100">100</option>
        </select>
        条
      </label>
      <button type="button" class="ghost sm" :disabled="pageNum <= 1" @click="goPage(pageNum - 1)">上一页</button>
      <template v-for="(item, idx) in pageItems" :key="`${item}-${idx}`">
        <span v-if="item === 'ellipsis'" class="ellipsis">…</span>
        <button
          v-else
          type="button"
          class="page-btn"
          :class="{ on: item === pageNum }"
          @click="goPage(item)"
        >
          {{ item }}
        </button>
      </template>
      <button type="button" class="ghost sm" :disabled="pageNum >= pageCount" @click="goPage(pageNum + 1)">
        下一页
      </button>
    </div>

    <MtcgDialog
      :open="detailOpen"
      :title="selected?.cardName ?? '卡牌'"
      width="720px"
      body-height="min(64vh, 560px)"
      :show-footer="false"
      fullscreenable
      @update:open="detailOpen = $event"
      @close="closeDetail"
    >
      <div v-if="selected" class="detail">
        <div class="detail-art">
          <img
            v-if="cardImage(selected)"
            :src="cardImage(selected)"
            :alt="selected.cardName"
            @error="onImgError"
          />
          <div v-else class="art-ph fill">无卡图</div>
        </div>
        <dl class="meta">
          <div>
            <dt>编号</dt>
            <dd>{{ selected.cardCode }}</dd>
          </div>
          <div>
            <dt>类型</dt>
            <dd>{{ selectedTypeLabel || selected.cardType }}</dd>
          </div>
          <div v-if="selectedColorLabel">
            <dt>颜色</dt>
            <dd>{{ selectedColorLabel }}</dd>
          </div>
          <div v-if="selected.rarity">
            <dt>稀有度</dt>
            <dd>{{ selectedRarityLabel }}</dd>
          </div>
          <div v-if="selected.level != null">
            <dt>等级</dt>
            <dd>{{ selected.level }}</dd>
          </div>
          <div v-if="selected.attackRange != null">
            <dt>攻击距离</dt>
            <dd>{{ selected.attackRange }}</dd>
          </div>
          <div v-if="selected.power != null">
            <dt>力量</dt>
            <dd>{{ selected.power }}</dd>
          </div>
          <div v-if="selected.traits">
            <dt>特征</dt>
            <dd>{{ selected.traits }}</dd>
          </div>
          <div v-if="selected.productCode">
            <dt>系列</dt>
            <dd>{{ selected.productCode }}</dd>
          </div>
          <div v-if="selected.effectText" class="effect">
            <dt>效果</dt>
            <dd>{{ selected.effectText }}</dd>
          </div>
        </dl>
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
  margin-bottom: 14px;
}

.head h1 {
  margin: 0;
  font-size: var(--font-size-lg);
  font-weight: 700;
}

.filter-bar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.search,
.select {
  height: var(--ctrl-h);
  padding: 0 12px;
  border-radius: var(--radius-sm);
  border: 1px solid var(--border);
  background: var(--bg-surface-2);
  color: var(--text-primary);
  outline: none;
}

.search:focus,
.select:focus {
  border-color: var(--accent);
  box-shadow: var(--shadow-glow);
}

.search {
  flex: 1 1 180px;
  min-width: 160px;
}

.select.compact {
  height: 28px;
  padding: 0 8px;
}

.quick-colors {
  display: flex;
  gap: 6px;
}

.color-dot {
  width: 18px;
  height: 18px;
  border-radius: 50%;
  border: 2px solid transparent;
  cursor: pointer;
  padding: 0;
}

.color-dot.red {
  background: #e53935;
}
.color-dot.yellow {
  background: #f9a825;
}
.color-dot.blue {
  background: #5c6bc0;
}
.color-dot.green {
  background: #43a047;
}
.color-dot.orange {
  background: #fb8c00;
}
.color-dot.purple {
  background: #8e24aa;
}
.color-dot.on {
  box-shadow: 0 0 0 2px var(--bg-base), 0 0 0 4px var(--accent);
}

.count {
  margin-left: auto;
  font-size: 12px;
  color: var(--text-secondary);
}

.advanced {
  margin-bottom: 14px;
  padding: 10px 12px;
  border-radius: var(--radius-md);
  border: 1px solid var(--border);
  background: var(--bg-surface);
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.row {
  display: grid;
  grid-template-columns: 64px 1fr;
  gap: 8px;
  align-items: start;
}

.row > span {
  font-size: 12px;
  color: var(--text-secondary);
  padding-top: 5px;
}

.chips {
  display: flex;
  flex-wrap: nowrap;
  gap: 6px;
  overflow-x: auto;
}

.chips.wrap {
  flex-wrap: wrap;
  overflow: visible;
}

.chip {
  flex: 0 0 auto;
  height: 26px;
  padding: 0 9px;
  border-radius: 999px;
  border: 1px solid var(--border);
  background: transparent;
  color: var(--text-secondary);
  font-size: 12px;
  cursor: pointer;
}

.chip.mono {
  min-width: 34px;
  font-family: Consolas, 'Courier New', monospace;
}

.chip.on {
  border-color: var(--accent);
  color: var(--accent);
  background: color-mix(in srgb, var(--accent) 12%, transparent);
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, var(--card-face-w));
  grid-auto-rows: max-content;
  gap: 14px 12px;
  align-content: start;
  align-items: start;
  justify-content: start;
}

.tile {
  display: flex;
  flex-direction: column;
  width: var(--card-face-w);
  padding: 0;
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  overflow: hidden;
  background: var(--bg-surface);
  color: inherit;
  cursor: pointer;
  text-align: left;
  transition: transform var(--transition-fast), border-color var(--transition-fast);
}

.tile:hover {
  transform: translateY(-2px);
  border-color: var(--accent);
}

.art {
  position: relative;
  display: block;
  width: var(--card-face-w);
  height: var(--card-face-h);
  flex: none;
  background: var(--bg-surface-2);
}

.art img,
.detail-art img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  object-position: center top;
  display: block;
}

.art-ph {
  position: absolute;
  inset: 0;
  display: grid;
  place-items: center;
  font-size: 11px;
  color: var(--text-secondary);
}

.art-ph.fill {
  position: relative;
  min-height: 200px;
}

.tile-name {
  padding: 8px 8px 0;
  font-size: 12px;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.tile-meta {
  padding: 2px 8px 8px;
  font-size: 11px;
  color: var(--text-secondary);
}

.hint {
  padding: 24px 0;
  color: var(--text-secondary);
  font-size: var(--font-size-sm);
}

.pager {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 20px;
  font-size: 12px;
  color: var(--text-secondary);
}

.pager label {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-right: auto;
}

.page-btn {
  min-width: 32px;
  height: 30px;
  padding: 0 8px;
  border-radius: var(--radius-sm);
  border: 1px solid var(--border);
  background: transparent;
  color: var(--text-secondary);
  cursor: pointer;
}

.page-btn.on {
  border-color: var(--accent);
  color: var(--accent);
  font-weight: 600;
}

.ellipsis {
  padding: 0 2px;
}

.ghost,
.link {
  border-radius: var(--radius-sm);
  cursor: pointer;
  font-size: 13px;
}

.ghost {
  height: 36px;
  padding: 0 12px;
  border: 1px solid var(--border);
  background: transparent;
  color: var(--text-secondary);
}

.ghost.sm {
  height: 30px;
  padding: 0 10px;
  font-size: 12px;
}

.ghost:hover:not(:disabled) {
  color: var(--accent);
  border-color: var(--accent);
}

.link {
  border: none;
  background: transparent;
  color: var(--accent);
  padding: 0 4px;
  font-weight: 600;
}

.ghost:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.detail {
  display: grid;
  grid-template-columns: 240px 1fr;
  gap: 20px;
  align-items: start;
}

.detail-art {
  width: 240px;
  height: calc(240px * 2080 / 1488);
  border-radius: var(--radius-md);
  overflow: hidden;
  background: var(--bg-surface-2);
  border: 1px solid var(--border);
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
  font-size: 12px;
  color: var(--text-secondary);
}

.meta dd {
  margin: 0;
  font-size: 13px;
  font-weight: 600;
}

.meta .effect dd {
  font-weight: 400;
  line-height: 1.7;
  white-space: pre-wrap;
}

@media (max-width: 720px) {
  .detail {
    grid-template-columns: 1fr;
  }

  .detail-art {
    width: var(--card-face-w);
    height: var(--card-face-h);
    justify-self: center;
  }
}
</style>
