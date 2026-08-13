<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { NButton, NEmpty, NInput, NPagination, NSelect, NSpin, NTag } from 'naive-ui'
import {
  CARD_ATTACK_RANGE_FILTER_OPTIONS,
  CARD_COLOR_OPTIONS,
  CARD_LEVEL_FILTER_OPTIONS,
  CARD_RARITY_FILTER_CODES,
  CARD_TRAIT_FILTER_OPTIONS,
  CARD_TYPE_OPTIONS,
  cardApi,
  deckApi,
  productApi,
  resolveCardImageUrl,
} from '@mtcg/common'
import type {
  CardColor,
  CardRarity,
  CardType,
  CardVO,
  DeckCardEntry,
  DeckValidateResultVO,
  ProductVO,
} from '@mtcg/common'
import { useThemeStore } from '@mtcg/common/stores'
import { confirm, toast } from '@/feedback'

useThemeStore()

const MAIN_TARGET = 50
const RUSH_TARGET = 9
const SAME_NAME_MAX = 3
const COLOR_MAX = 2

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
/** 封面卡编号（保存时提交；空则后端回退主卡组首张） */
const coverCardCode = ref('')
const coverPickerOpen = ref(false)
const pendingCover = ref('')
const mainDeck = ref<DeckCardEntry[]>([])
const rushDeck = ref<DeckCardEntry[]>([])
const serverValid = ref<boolean | null>(null)
const validateResult = ref<DeckValidateResultVO | null>(null)
/** 点选加入失败时的即时提示 */
const addHint = ref('')

/** cardCode → 展示元数据 */
const cardMeta = reactive<Record<string, CardVO>>({})

const pool = ref<CardVO[]>([])
const poolTotal = ref(0)
const poolPage = ref(1)
const poolPageSize = ref(50)
const poolLoading = ref(false)
const products = ref<ProductVO[]>([])
/** 高级筛选默认收起（对齐 Snap / Arena：顶栏简洁，细节按需展开） */
const advancedOpen = ref(false)
const filters = reactive({
  cardName: '',
  cardType: '' as '' | CardType,
  color: '' as '' | CardColor,
  productCode: '',
  rarity: '' as '' | CardRarity,
  level: null as number | null,
  attackRange: null as number | null,
  trait: '',
})

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

const mainCount = computed(() =>
  mainDeck.value.reduce((s, e) => s + (e.quantity || 0), 0),
)
const rushCount = computed(() =>
  rushDeck.value.reduce((s, e) => s + (e.quantity || 0), 0),
)
const mainColorCount = computed(() => {
  const set = new Set<string>()
  for (const e of mainDeck.value) {
    const color = cardMeta[e.cardCode]?.color
    if (color) set.add(color)
  }
  return set.size
})

const inDeckCount = computed(() => {
  const map: Record<string, number> = {}
  for (const e of [...mainDeck.value, ...rushDeck.value]) {
    map[e.cardCode] = (map[e.cardCode] ?? 0) + e.quantity
  }
  return map
})

let dragList: 'main' | 'rush' | null = null
let dragIndex = -1
let searchTimer: ReturnType<typeof setTimeout> | null = null

onMounted(async () => {
  await Promise.all([loadProducts(), loadPool(), loadDeckIfNeeded()])
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
      poolPageSize.value,
    ] as const,
  () => {
    poolPage.value = 1
    void loadPool()
  },
)

watch(
  () => filters.cardName,
  () => {
    if (searchTimer) clearTimeout(searchTimer)
    searchTimer = setTimeout(() => {
      poolPage.value = 1
      void loadPool()
    }, 280)
  },
)

async function loadProducts(): Promise<void> {
  try {
    const page = await productApi.list({ pageNum: 1, pageSize: 50 })
    products.value = page.records ?? []
  } catch {
    products.value = []
  }
}

async function loadDeckIfNeeded(): Promise<void> {
  if (isNew.value) {
    coverCardCode.value = ''
    dirty.value = false
    return
  }
  const id = deckId.value!
  const vo = await deckApi.get(id, loading)
  deckName.value = vo.deckName
  tags.value = vo.tags ?? ''
  coverCardCode.value = vo.coverCardCode ?? ''
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
  const codes = [
    ...mainDeck.value.map((e) => e.cardCode),
    ...rushDeck.value.map((e) => e.cardCode),
  ]
  for (const c of pool.value) {
    rememberCard(c)
  }
  const missing = codes.filter((code) => !cardMeta[code])
  await Promise.all(
    missing.slice(0, 80).map(async (code) => {
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

function rememberCard(card: CardVO): void {
  cardMeta[card.cardCode] = card
}

async function loadPool(): Promise<void> {
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
      pageNum: poolPage.value,
      pageSize: poolPageSize.value,
    },
    poolLoading,
  )
  pool.value = page.records ?? []
  poolTotal.value = page.total ?? 0
  for (const c of pool.value) {
    rememberCard(c)
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
  poolPage.value = 1
  void loadPool()
}

function toggleChip(
  field: 'cardType' | 'color' | 'rarity' | 'trait',
  value: string,
  checked: boolean,
): void {
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

function cardImage(codeOrCard: string | CardVO): string {
  const card = typeof codeOrCard === 'string' ? cardMeta[codeOrCard] : codeOrCard
  return resolveCardImageUrl(card?.imagePath)
}

function displayName(code: string): string {
  return cardMeta[code]?.cardName ?? code
}

function onImgError(e: Event): void {
  const el = e.target as HTMLImageElement
  el.style.opacity = '0'
}

function markDirty(): void {
  dirty.value = true
  serverValid.value = null
  validateResult.value = null
}

function addCard(card: CardVO): void {
  rememberCard(card)
  const reason = reasonCannotAdd(card)
  if (reason) {
    addHint.value = reason
    return
  }
  if (card.cardType === 'RUSH_POINT') {
    bumpEntry(rushDeck, card.cardCode, 1)
  } else {
    bumpEntry(mainDeck, card.cardCode, 1)
  }
  addHint.value = ''
  markDirty()
}

function nameQtyInMain(cardName: string): number {
  return mainDeck.value.reduce((sum, e) => {
    const name = cardMeta[e.cardCode]?.cardName
    return name === cardName ? sum + e.quantity : sum
  }, 0)
}

function mainColors(): Set<string> {
  const set = new Set<string>()
  for (const e of mainDeck.value) {
    const color = cardMeta[e.cardCode]?.color
    if (color) set.add(color)
  }
  return set
}

/** 点选加入前的构筑限制（101.1 / 101.2） */
function reasonCannotAdd(card: CardVO): string | null {
  if (card.cardType === 'RUSH_POINT') {
    if (rushCount.value >= RUSH_TARGET) return '冲击卡组最多 9 张'
    return null
  }
  if (mainCount.value >= MAIN_TARGET) return '主卡组最多 50 张'
  if (card.cardName && nameQtyInMain(card.cardName) >= SAME_NAME_MAX) {
    return `同名卡最多 ${SAME_NAME_MAX} 张`
  }
  if (card.color) {
    const colors = mainColors()
    if (!colors.has(card.color) && colors.size >= COLOR_MAX) {
      return `主卡组最多 ${COLOR_MAX} 种颜色`
    }
  }
  return null
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
  if (delta > 0) {
    const card = cardMeta[entry.cardCode]
    if (card) {
      const reason = reasonCannotAdd(card)
      if (reason) {
        addHint.value = reason
        return
      }
    }
  }
  bumpEntry(list, entry.cardCode, delta)
  addHint.value = ''
  markDirty()
}

function removeEntry(zone: 'main' | 'rush', index: number): void {
  const list = zone === 'main' ? mainDeck : rushDeck
  const entry = list.value[index]
  if (!entry) return
  list.value.splice(index, 1)
  if (coverCardCode.value === entry.cardCode) {
    coverCardCode.value = ''
  }
  addHint.value = ''
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
    toast.warning('请填写卡组名称')
    return
  }
  if (mainDeck.value.length === 0) {
    coverCardCode.value = ''
    await persistDeck()
    return
  }
  const stillInDeck = mainDeck.value.some((e) => e.cardCode === coverCardCode.value)
  pendingCover.value = stillInDeck ? coverCardCode.value : mainDeck.value[0].cardCode
  coverPickerOpen.value = true
}

function cancelCoverPicker(): void {
  if (saving.value) return
  coverPickerOpen.value = false
}

async function confirmCoverAndSave(): Promise<void> {
  if (!pendingCover.value) {
    toast.warning('请选择一张角色卡作为封面')
    return
  }
  coverCardCode.value = pendingCover.value
  coverPickerOpen.value = false
  await persistDeck()
}

async function persistDeck(): Promise<void> {
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
    coverCardCode: coverCardCode.value.trim(),
  }
  try {
    if (isNew.value) {
      const id = await deckApi.create(payload, saving)
      dirty.value = false
      toast.success('卡组已创建')
      await router.replace(`/decks/${id}`)
    } else {
      await deckApi.update(deckId.value!, payload, saving)
      dirty.value = false
      const vo = await deckApi.get(deckId.value!)
      serverValid.value = vo.isValid
      toast.success('卡组已保存')
    }
  } catch {
    // HTTP 错误由全局 notifier 提示
  }
}

async function handleValidate(): Promise<void> {
  if (isNew.value || dirty.value) {
    toast.warning('请先保存卡组再校验')
    return
  }
  try {
    const result = await deckApi.validate(deckId.value!, saving)
    validateResult.value = result
    serverValid.value = result.valid
    if (result.valid) {
      toast.success('校验通过')
    } else {
      toast.warning(result.errors.join('；') || '校验未通过')
    }
  } catch {
    // HTTP 错误由全局 notifier 提示
  }
}

async function handleBack(): Promise<void> {
  if (dirty.value) {
    const ok = await confirm({
      title: '未保存的修改',
      content: '有未保存的修改，确定返回？',
      positiveText: '返回',
    })
    if (!ok) return
  }
  await router.push('/decks')
}

function onPoolPageChange(page: number): void {
  poolPage.value = page
  void loadPool()
}
</script>

<template>
  <div class="builder">
    <!-- 顶栏：Snap / Arena 式工具条 -->
    <header class="topbar">
      <n-button quaternary @click="handleBack">←</n-button>
      <n-input
        v-model:value="deckName"
        class="name"
        maxlength="64"
        placeholder="卡组名称"
        @update:value="markDirty"
      />
      <span class="stat" :class="{ ok: mainCount === MAIN_TARGET, over: mainCount > MAIN_TARGET }">
        主 {{ mainCount }}/{{ MAIN_TARGET }}
      </span>
      <span class="stat rush" :class="{ ok: rushCount === RUSH_TARGET, over: rushCount > RUSH_TARGET }">
        冲击 {{ rushCount }}/{{ RUSH_TARGET }}
      </span>
      <span class="stat" :class="{ over: mainColorCount > COLOR_MAX }">
        {{ mainColorCount }}/{{ COLOR_MAX }} 色
      </span>
      <n-tag
        v-if="serverValid !== null"
        size="small"
        :type="serverValid ? 'success' : 'default'"
        :bordered="false"
      >
        {{ serverValid ? '合法' : '未合法' }}
      </n-tag>
      <n-tag v-if="dirty" size="small" type="info" :bordered="false">未保存</n-tag>
      <div class="actions">
        <n-button :disabled="saving" @click="handleValidate">校验</n-button>
        <n-button type="primary" :disabled="saving || loading" :loading="saving" @click="handleSave">
          保存
        </n-button>
      </div>
    </header>

    <div v-if="addHint" class="errors">
      <p>{{ addHint }}</p>
    </div>
    <div v-if="validateResult && !validateResult.valid" class="errors">
      <p v-for="(err, i) in validateResult.errors" :key="i">{{ err }}</p>
    </div>

    <!-- 主体：左卡表 · 右竖卡池 -->
    <div class="workspace">
      <aside class="deck-side">
        <section class="zone">
          <div class="zone-head">
            <h2>主卡组</h2>
            <span :class="{ warn: mainCount !== MAIN_TARGET }">{{ mainCount }}/{{ MAIN_TARGET }}</span>
          </div>
          <div class="entry-list" @dragover="onEntryDragOver">
            <div
              v-for="(entry, index) in mainDeck"
              :key="entry.cardCode + '-m-' + index"
              class="entry"
              draggable="true"
              @dragstart="onEntryDragStart('main', index, $event)"
              @drop="onEntryDrop('main', index, $event)"
              @dragend="onEntryDragEnd"
            >
              <img
                v-if="cardImage(entry.cardCode)"
                class="thumb"
                :src="cardImage(entry.cardCode)"
                :alt="displayName(entry.cardCode)"
                @error="onImgError"
              />
              <div v-else class="thumb ph" />
              <div class="entry-info">
                <span class="ename">{{ displayName(entry.cardCode) }}</span>
                <span class="ecode">{{ entry.cardCode }}</span>
              </div>
              <div class="qty-ctrl">
                <n-button size="tiny" secondary @click.stop="changeQty('main', index, -1)">−</n-button>
                <span>{{ entry.quantity }}</span>
                <n-button size="tiny" secondary @click.stop="changeQty('main', index, 1)">+</n-button>
                <n-button size="tiny" quaternary type="error" @click.stop="removeEntry('main', index)">
                  ✕
                </n-button>
              </div>
            </div>
            <p v-if="mainDeck.length === 0" class="hint">从右侧卡池点选角色卡</p>
          </div>
        </section>

        <section class="zone rush">
          <div class="zone-head">
            <h2>冲击卡组</h2>
            <span :class="{ warn: rushCount !== RUSH_TARGET }">{{ rushCount }}/{{ RUSH_TARGET }}</span>
          </div>
          <div class="entry-list" @dragover="onEntryDragOver">
            <div
              v-for="(entry, index) in rushDeck"
              :key="entry.cardCode + '-r-' + index"
              class="entry"
              draggable="true"
              @dragstart="onEntryDragStart('rush', index, $event)"
              @drop="onEntryDrop('rush', index, $event)"
              @dragend="onEntryDragEnd"
            >
              <img
                v-if="cardImage(entry.cardCode)"
                class="thumb"
                :src="cardImage(entry.cardCode)"
                :alt="displayName(entry.cardCode)"
                @error="onImgError"
              />
              <div v-else class="thumb ph" />
              <div class="entry-info">
                <span class="ename">{{ displayName(entry.cardCode) }}</span>
                <span class="ecode">{{ entry.cardCode }}</span>
              </div>
              <div class="qty-ctrl">
                <n-button size="tiny" secondary @click.stop="changeQty('rush', index, -1)">−</n-button>
                <span>{{ entry.quantity }}</span>
                <n-button size="tiny" secondary @click.stop="changeQty('rush', index, 1)">+</n-button>
                <n-button size="tiny" quaternary type="error" @click.stop="removeEntry('rush', index)">
                  ✕
                </n-button>
              </div>
            </div>
            <p v-if="rushDeck.length === 0" class="hint">从右侧卡池点选冲击卡</p>
          </div>
        </section>
      </aside>

      <section class="pool-side">
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
          <n-button v-if="hasActiveFilters" text type="primary" @click="clearFilters">清除</n-button>
          <span class="count">共 {{ poolTotal }} 条</span>
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

        <div v-if="poolLoading && pool.length === 0" class="spin-wrap">
          <n-spin size="medium" />
        </div>
        <div v-else-if="!poolLoading && pool.length === 0" class="empty">
          <n-empty description="没有匹配卡牌" size="small" />
        </div>
        <div v-else class="pool-grid">
          <button
            v-for="card in pool"
            :key="card.id"
            type="button"
            class="pool-card"
            :class="{ owned: (inDeckCount[card.cardCode] ?? 0) > 0 }"
            :title="card.cardName"
            @click="addCard(card)"
          >
            <div class="art">
              <img
                v-if="cardImage(card)"
                :src="cardImage(card)"
                :alt="card.cardName"
                loading="lazy"
                @error="onImgError"
              />
              <div v-else class="art-ph">{{ card.cardType === 'RUSH_POINT' ? '冲击' : '角色' }}</div>
              <span v-if="card.rarity" class="rarity">{{ card.rarity }}</span>
              <span v-if="(inDeckCount[card.cardCode] ?? 0) > 0" class="in-deck">
                ×{{ inDeckCount[card.cardCode] }}
              </span>
            </div>
          </button>
        </div>

        <div v-if="poolTotal > 0" class="pager">
          <n-pagination
            :page="poolPage"
            :page-size="poolPageSize"
            :item-count="poolTotal"
            show-size-picker
            :page-sizes="[20, 50, 100]"
            @update:page="onPoolPageChange"
            @update:page-size="(s) => (poolPageSize = s)"
          />
        </div>
      </section>
    </div>

    <div v-if="coverPickerOpen" class="cover-mask" @click.self="cancelCoverPicker">
      <div class="cover-panel" role="dialog" aria-labelledby="cover-title">
        <h3 id="cover-title">选择封面</h3>
        <p>点选一张角色卡作为卡组封面</p>
        <div class="cover-grid">
          <button
            v-for="entry in mainDeck"
            :key="entry.cardCode"
            type="button"
            class="cover-pick"
            :class="{ on: pendingCover === entry.cardCode }"
            @click="pendingCover = entry.cardCode"
          >
            <img
              v-if="cardImage(entry.cardCode)"
              :src="cardImage(entry.cardCode)"
              :alt="displayName(entry.cardCode)"
              @error="onImgError"
            />
            <span>{{ displayName(entry.cardCode) }}</span>
          </button>
        </div>
        <div class="cover-actions">
          <n-button :disabled="saving" @click="cancelCoverPicker">取消</n-button>
          <n-button
            type="primary"
            :disabled="saving || !pendingCover"
            :loading="saving"
            @click="confirmCoverAndSave"
          >
            确定保存
          </n-button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.builder {
  height: 100dvh;
  display: flex;
  flex-direction: column;
  color: var(--text-primary);
  background: var(--bg-base);
  overflow: hidden;
}

.topbar {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
  min-height: var(--topbar-h);
  padding: 8px 16px;
  border-bottom: 1px solid var(--border);
  background: var(--bg-surface);
  box-shadow: var(--edge-highlight);
  z-index: 20;
}

.name {
  width: 180px;
  flex: 0 1 220px;
}

.stat {
  flex-shrink: 0;
  font-size: 13px;
  font-weight: 600;
  color: var(--text-secondary);
  white-space: nowrap;
}
.stat.ok { color: var(--accent); }
.stat.over { color: var(--accent-red); }
.stat.rush.ok { color: var(--accent-gold); }

.actions {
  display: flex;
  gap: 8px;
  margin-left: auto;
  flex-shrink: 0;
}

.errors {
  margin: 8px 16px 0;
  padding: 10px 12px;
  border-radius: 10px;
  background: rgba(229, 57, 53, 0.1);
  color: var(--accent-red);
  font-size: 13px;
}
.errors p { margin: 0 0 2px; }

.workspace {
  flex: 1;
  display: grid;
  grid-template-columns: var(--deck-col) var(--pool-col);
  gap: 0;
  min-height: 0;
  overflow: hidden;
}

@media (max-width: 1100px) {
  .workspace { grid-template-columns: 1fr; }
  .deck-side {
    border-right: none;
    border-bottom: 1px solid var(--border);
    max-height: 42vh;
  }
  .zone.rush { max-height: none; }
}

.deck-side {
  display: flex;
  flex-direction: column;
  gap: 12px;
  min-height: 0;
  padding: 12px;
  border-right: 1px solid var(--border);
  background: color-mix(in srgb, var(--bg-surface) 55%, transparent);
  overflow: hidden;
}

.zone {
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.zone:first-child {
  flex: 1;
}

.zone.rush {
  flex: 0 1 auto;
  max-height: 38%;
}

.zone-head {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 6px;
  flex-shrink: 0;
}

.zone-head h2 {
  margin: 0;
  font-size: 13px;
  font-weight: 600;
}

.zone-head span {
  font-size: 12px;
  color: var(--text-secondary);
}
.zone-head .warn { color: var(--accent); }

.zone.rush .zone-head h2 { color: var(--accent-gold); }

.entry-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-height: 0;
  overflow: auto;
}

.entry {
  display: grid;
  grid-template-columns: var(--card-thumb-w) minmax(0, 1fr) auto;
  gap: 8px;
  align-items: center;
  padding: 4px 6px;
  border-radius: 8px;
  border: 1px solid var(--border);
  background: var(--bg-surface);
  cursor: grab;
  transition: border-color 0.15s ease;
}

.entry:hover {
  border-color: var(--accent);
}

.thumb {
  width: var(--card-thumb-w);
  height: var(--card-thumb-h);
  object-fit: contain;
  object-position: top;
  border-radius: 4px;
  display: block;
  background: var(--bg-surface-2);
  flex: none;
}

.thumb.ph {
  width: var(--card-thumb-w);
  height: var(--card-thumb-h);
}

.entry-info {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.ename {
  font-size: 13px;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.ecode {
  font-size: 11px;
  color: var(--text-secondary);
}

.qty-ctrl {
  display: flex;
  align-items: center;
  gap: 4px;
}

.qty-ctrl span {
  min-width: 1.2em;
  text-align: center;
  font-size: 13px;
  font-weight: 700;
}

.pool-side {
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow: hidden;
  padding: 12px 14px 16px;
}

.filter-bar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
  flex-shrink: 0;
}

.search {
  flex: 1 1 160px;
  min-width: 140px;
  max-width: 240px;
}

.select {
  width: 130px;
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
.color-dot.red { background: #e53935; }
.color-dot.yellow { background: #f9a825; }
.color-dot.blue { background: #5c6bc0; }
.color-dot.green { background: #43a047; }
.color-dot.orange { background: #fb8c00; }
.color-dot.purple { background: #8e24aa; }
.color-dot.on {
  box-shadow: 0 0 0 2px var(--bg-base), 0 0 0 4px var(--accent);
}

.count {
  margin-left: auto;
  font-size: 12px;
  color: var(--text-secondary);
}

.advanced {
  margin-bottom: 10px;
  padding: 10px 12px;
  border-radius: 12px;
  border: 1px solid var(--border);
  background: var(--bg-surface);
  display: flex;
  flex-direction: column;
  gap: 8px;
  max-height: 220px;
  overflow: auto;
  flex-shrink: 0;
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

.chips.wrap { flex-wrap: wrap; overflow: visible; }

.mono {
  font-family: Consolas, monospace;
}

.spin-wrap,
.empty {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 120px;
}

.pool-grid {
  flex: 1 1 0;
  min-height: 0;
  overflow-x: hidden;
  overflow-y: auto;
  display: grid;
  grid-template-columns: repeat(auto-fill, var(--card-face-w));
  grid-auto-rows: max-content;
  gap: 12px 10px;
  padding: 2px 2px 16px;
  align-content: start;
  align-items: start;
  justify-content: start;
}

.pool-card {
  display: flex;
  flex-direction: column;
  width: var(--card-face-w);
  height: auto;
  flex: none;
  align-self: start;
  padding: 0;
  border: 1px solid var(--border);
  border-radius: 10px;
  background: var(--bg-surface);
  color: inherit;
  cursor: pointer;
  overflow: hidden;
  text-align: left;
  appearance: none;
  transition: transform 0.15s ease, border-color 0.15s ease, box-shadow 0.15s ease;
}

.pool-card:hover {
  transform: translateY(-2px);
  border-color: var(--accent);
  box-shadow: var(--shadow-md);
}

.pool-card.owned {
  border-color: color-mix(in srgb, var(--accent) 40%, var(--border));
}

.art {
  position: relative;
  width: var(--card-face-w);
  height: var(--card-face-h);
  flex: none;
  overflow: hidden;
  background: var(--bg-surface-2);
}

.art img {
  position: absolute;
  inset: 0;
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

.rarity {
  position: absolute;
  left: 5px;
  top: 5px;
  padding: 1px 5px;
  border-radius: 999px;
  font-size: 10px;
  background: rgba(0, 0, 0, 0.55);
  color: #fff;
}

.in-deck {
  position: absolute;
  right: 5px;
  top: 5px;
  padding: 1px 6px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 700;
  background: var(--accent);
  color: var(--accent-contrast);
}

.pager {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 12px;
  flex-shrink: 0;
}

.hint {
  margin: 8px 0;
  color: var(--text-secondary);
  font-size: 12px;
}

.cover-mask {
  position: fixed;
  inset: 0;
  z-index: 40;
  display: grid;
  place-items: center;
  padding: 24px;
  background: color-mix(in srgb, var(--bg-base) 55%, transparent);
}

.cover-panel {
  width: min(720px, 100%);
  max-height: min(80vh, 720px);
  display: flex;
  flex-direction: column;
  padding: 20px 20px 16px;
  border-radius: var(--radius-lg);
  border: 1px solid var(--border);
  background: var(--bg-surface);
  box-shadow: var(--shadow-lg);
}

.cover-panel h3 {
  margin: 0;
  font-size: 18px;
}

.cover-panel > p {
  margin: 6px 0 14px;
  font-size: 13px;
  color: var(--text-secondary);
}

.cover-grid {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  display: grid;
  grid-template-columns: repeat(auto-fill, var(--card-face-w));
  grid-auto-rows: max-content;
  gap: 10px;
  justify-content: start;
  align-content: start;
}

.cover-pick {
  display: flex;
  flex-direction: column;
  width: var(--card-face-w);
  padding: 0;
  border: 2px solid var(--border);
  border-radius: 10px;
  background: var(--bg-surface-2);
  color: inherit;
  cursor: pointer;
  overflow: hidden;
  text-align: left;
}

.cover-pick.on {
  border-color: var(--accent);
}

.cover-pick img {
  width: var(--card-face-w);
  height: var(--card-face-h);
  object-fit: contain;
  object-position: center top;
  display: block;
  background: var(--bg-surface-2);
}

.cover-pick span {
  display: block;
  padding: 6px 8px 8px;
  font-size: 12px;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.cover-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 14px;
  flex-shrink: 0;
}
</style>
