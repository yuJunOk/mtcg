<script setup lang="ts">
import { computed, onMounted, onUnmounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { NButton, NEmpty, NInput, NPagination, NSelect, NSpin, NTag } from 'naive-ui'
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
const productOptions = computed(() => [
  { label: '商品系列', value: '' },
  ...products.value.map((p) => ({ label: p.productCode, value: p.productCode })),
])

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

function toggleChip(field: 'cardType' | 'color' | 'rarity' | 'trait', value: string, checked: boolean): void {
  if (checked) {
    ;(filters[field] as string) = value
  } else if (filters[field] === value) {
    ;(filters[field] as string) = ''
  }
}

function toggleNumChip(field: 'level' | 'attackRange', value: number, checked: boolean): void {
  if (checked) {
    filters[field] = value
  } else if (filters[field] === value) {
    filters[field] = null
  }
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

function onPageChange(page: number): void {
  pageNum.value = page
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
      <n-input
        v-model:value="filters.cardName"
        class="search"
        clearable
        placeholder="搜索卡牌"
      />
      <n-select
        v-model:value="filters.productCode"
        class="select"
        :options="productOptions"
        :consistent-menu-width="false"
      />
      <div class="quick-colors">
        <button
          v-for="o in CARD_COLOR_OPTIONS"
          :key="o.code"
          type="button"
          class="color-dot"
          :class="[o.code.toLowerCase(), { on: filters.color === o.code }]"
          :title="o.desc + '色'"
          @click="toggleChip('color', o.code, filters.color !== o.code)"
        />
      </div>
      <n-button size="small" @click="advancedOpen = !advancedOpen">
        {{ advancedOpen ? '收起' : '高级筛选' }}
      </n-button>
      <n-button v-if="hasActiveFilters" text type="primary" @click="clearFilters">
        清除所选
      </n-button>
      <span class="count">共 {{ total }} 条结果</span>
    </div>

    <div v-show="advancedOpen" class="advanced">
      <div class="row">
        <span>类型</span>
        <div class="chips">
          <n-tag
            checkable
            size="small"
            :checked="!filters.cardType"
            @update:checked="(v) => v && (filters.cardType = '')"
          >
            全部
          </n-tag>
          <n-tag
            v-for="o in CARD_TYPE_OPTIONS"
            :key="o.code"
            checkable
            size="small"
            :checked="filters.cardType === o.code"
            @update:checked="(v) => toggleChip('cardType', o.code, v)"
          >
            {{ o.desc }}
          </n-tag>
        </div>
      </div>
      <div class="row">
        <span>稀有度</span>
        <div class="chips">
          <n-tag
            checkable
            size="small"
            :checked="!filters.rarity"
            @update:checked="(v) => v && (filters.rarity = '')"
          >
            全部
          </n-tag>
          <n-tag
            v-for="code in CARD_RARITY_FILTER_CODES"
            :key="code"
            checkable
            size="small"
            class="mono"
            :checked="filters.rarity === code"
            @update:checked="(v) => toggleChip('rarity', code, v)"
          >
            {{ code }}
          </n-tag>
        </div>
      </div>
      <div class="row">
        <span>等级</span>
        <div class="chips">
          <n-tag
            checkable
            size="small"
            :checked="filters.level == null"
            @update:checked="(v) => v && (filters.level = null)"
          >
            全部
          </n-tag>
          <n-tag
            v-for="lv in CARD_LEVEL_FILTER_OPTIONS"
            :key="lv"
            checkable
            size="small"
            class="mono"
            :checked="filters.level === lv"
            @update:checked="(v) => toggleNumChip('level', lv, v)"
          >
            {{ lv }}
          </n-tag>
        </div>
      </div>
      <div class="row">
        <span>攻击距离</span>
        <div class="chips">
          <n-tag
            checkable
            size="small"
            :checked="filters.attackRange == null"
            @update:checked="(v) => v && (filters.attackRange = null)"
          >
            全部
          </n-tag>
          <n-tag
            v-for="r in CARD_ATTACK_RANGE_FILTER_OPTIONS"
            :key="r"
            checkable
            size="small"
            class="mono"
            :checked="filters.attackRange === r"
            @update:checked="(v) => toggleNumChip('attackRange', r, v)"
          >
            {{ r }}
          </n-tag>
        </div>
      </div>
      <div class="row">
        <span>特征</span>
        <div class="chips wrap">
          <n-tag
            checkable
            size="small"
            :checked="!filters.trait"
            @update:checked="(v) => v && (filters.trait = '')"
          >
            全部
          </n-tag>
          <n-tag
            v-for="t in CARD_TRAIT_FILTER_OPTIONS"
            :key="t"
            checkable
            size="small"
            :checked="filters.trait === t"
            @update:checked="(v) => toggleChip('trait', t, v)"
          >
            {{ t }}
          </n-tag>
        </div>
      </div>
    </div>

    <div v-if="loading && cards.length === 0" class="spin-wrap">
      <n-spin size="large" />
    </div>
    <div v-else-if="!loading && cards.length === 0" class="empty">
      <n-empty description="没有匹配卡牌" />
    </div>

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

    <div v-if="total > 0" class="pager">
      <n-pagination
        :page="pageNum"
        :page-size="pageSize"
        :item-count="total"
        show-size-picker
        :page-sizes="[20, 50, 100]"
        @update:page="onPageChange"
        @update:page-size="(s) => (pageSize = s)"
      />
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

.search {
  flex: 1 1 180px;
  min-width: 160px;
  max-width: 280px;
}

.select {
  width: 140px;
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

.mono {
  font-family: Consolas, 'Courier New', monospace;
}

.spin-wrap,
.empty {
  padding: 48px 0;
  display: flex;
  justify-content: center;
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

.pager {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: center;
  margin-top: 20px;
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
