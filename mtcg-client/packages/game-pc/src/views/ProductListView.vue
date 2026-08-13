<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { NButton, NEmpty, NPagination, NSpin, NTag } from 'naive-ui'
import {
  PRODUCT_CATEGORY_OPTIONS,
  getProductCategoryLabel,
  productApi,
  productCoverPath,
  resolveProductCategory,
} from '@mtcg/common'
import type { ProductCategory, ProductVO } from '@mtcg/common'
import DeckCover from '@/components/DeckCover.vue'

type ViewMode = 'grid' | 'list'

//#region 状态
const router = useRouter()

const loading = ref(false)
const allProducts = ref<ProductVO[]>([])
const category = ref<'' | ProductCategory>('')
const pageNum = ref(1)
const pageSize = ref(10)
const viewMode = ref<ViewMode>('grid')
//#endregion

//#region 计算属性
const filtered = computed(() => {
  const list = allProducts.value
  if (!category.value) return list
  return list.filter((p) => resolveProductCategory(p.productCode) === category.value)
})

const total = computed(() => filtered.value.length)

const pageCount = computed(() => Math.max(1, Math.ceil(total.value / pageSize.value)))

const pageRows = computed(() => {
  const start = (pageNum.value - 1) * pageSize.value
  return filtered.value.slice(start, start + pageSize.value)
})
//#endregion

//#region 方法
function formatDate(raw: string | null | undefined): string {
  if (!raw) return '—'
  return raw.length >= 10 ? raw.slice(0, 10) : raw
}

function categoryOf(p: ProductVO): string {
  return getProductCategoryLabel(resolveProductCategory(p.productCode))
}

function coverOf(p: ProductVO): string | null {
  return productCoverPath(p)
}

function displayName(p: ProductVO): string {
  return p.productName || p.productCode
}

function openProduct(p: ProductVO): void {
  void router.push({ path: '/cards', query: { product: p.productCode } })
}

function setCategory(next: '' | ProductCategory, checked: boolean): void {
  if (checked) {
    category.value = next
  } else if (category.value === next) {
    category.value = ''
  }
}

function toggleViewMode(): void {
  viewMode.value = viewMode.value === 'grid' ? 'list' : 'grid'
}

async function loadProducts(): Promise<void> {
  const page = await productApi.list({ pageNum: 1, pageSize: 100 }, loading)
  allProducts.value = page.records ?? []
}
//#endregion

//#region 生命周期
onMounted(() => {
  void loadProducts()
})

watch([category, pageSize], () => {
  pageNum.value = 1
})

watch(pageCount, (n) => {
  if (pageNum.value > n) pageNum.value = n
})
//#endregion
</script>

<template>
  <div class="page">
    <header class="head">
      <div class="title-block">
        <h1>📦 商品</h1>
        <p class="hint">按系列浏览 · 点进卡表筛选</p>
      </div>
    </header>

    <section class="filter-panel">
      <div class="filter-bar">
        <div class="chips">
          <n-tag
            checkable
            :checked="!category"
            size="small"
            @update:checked="(v) => v && setCategory('', true)"
          >
            全部
          </n-tag>
          <n-tag
            v-for="o in PRODUCT_CATEGORY_OPTIONS"
            :key="o.code"
            checkable
            :checked="category === o.code"
            size="small"
            @update:checked="(v) => setCategory(o.code, v)"
          >
            {{ o.desc }}
          </n-tag>
        </div>
        <n-button size="small" secondary class="view-toggle" @click="toggleViewMode">
          {{ viewMode === 'grid' ? '☰ 列表' : '▦ 网格' }}
        </n-button>
      </div>
    </section>

    <div class="result-bar">
      <span class="count">共 <strong>{{ total }}</strong> 条</span>
    </div>

    <div v-if="loading && allProducts.length === 0" class="spin-wrap">
      <n-spin size="large" />
    </div>
    <div v-else-if="!loading && pageRows.length === 0" class="empty">
      <n-empty description="暂无商品">
        <template #icon>
          <span class="empty-ico" aria-hidden="true">📦</span>
        </template>
      </n-empty>
    </div>

    <div v-else-if="viewMode === 'grid'" class="grid">
      <button
        v-for="p in pageRows"
        :key="p.id"
        type="button"
        class="tile"
        @click="openProduct(p)"
      >
        <span class="cover">
          <DeckCover :image-path="coverOf(p)" placeholder="📦" lazy />
        </span>
        <span class="tile-body">
          <n-tag size="tiny" :bordered="false" round class="badge">
            {{ categoryOf(p) }}
          </n-tag>
          <span class="name">{{ displayName(p) }}【{{ p.productCode }}】</span>
          <span class="meta">发售日期：{{ formatDate(p.releaseDate) }}</span>
        </span>
      </button>
    </div>

    <div v-else class="list">
      <button
        v-for="p in pageRows"
        :key="p.id"
        type="button"
        class="row"
        @click="openProduct(p)"
      >
        <span class="row-cover">
          <DeckCover :image-path="coverOf(p)" placeholder="📦" lazy />
        </span>
        <span class="row-body">
          <span class="name">{{ displayName(p) }}</span>
          <span class="row-meta">
            <span class="code">{{ p.productCode }}</span>
            <n-tag size="tiny" :bordered="false" round class="badge">
              {{ categoryOf(p) }}
            </n-tag>
            <span class="date">发售 {{ formatDate(p.releaseDate) }}</span>
          </span>
        </span>
      </button>
    </div>

    <div v-if="total > 0" class="pager">
      <n-pagination
        v-model:page="pageNum"
        v-model:page-size="pageSize"
        :item-count="total"
        show-size-picker
        :page-sizes="[10, 20, 50, 100]"
      />
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
  margin-bottom: 14px;
}

.title-block {
  display: flex;
  flex-wrap: wrap;
  align-items: baseline;
  gap: 8px 12px;
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

.filter-panel {
  margin-bottom: 12px;
  padding: 12px 14px;
  border-radius: var(--radius-md);
  border: 1px solid var(--border);
  background: var(--bg-surface);
}

.filter-bar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px 12px;
}

.chips {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  flex: 1 1 auto;
}

.view-toggle {
  margin-left: auto;
}

.result-bar {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.count {
  font-size: 13px;
  color: var(--text-secondary);
}

.count strong {
  color: var(--text-primary);
  font-size: 16px;
  font-weight: 700;
  margin: 0 2px;
}

.spin-wrap,
.empty {
  padding: 48px 0;
  display: flex;
  justify-content: center;
}

.empty-ico {
  font-size: 36px;
  line-height: 1;
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
  padding: 0;
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  background: var(--bg-surface);
  color: inherit;
  overflow: hidden;
  cursor: pointer;
  text-align: left;
  box-shadow: 0 2px 10px color-mix(in srgb, #000 8%, transparent);
  transition:
    transform var(--transition-fast),
    border-color var(--transition-fast),
    box-shadow var(--transition-fast);
}

.tile:hover {
  transform: translateY(-4px);
  border-color: var(--accent);
  box-shadow: 0 10px 24px color-mix(in srgb, #000 14%, transparent);
}

.cover {
  display: block;
  width: var(--card-cover-w);
  height: var(--card-cover-h);
  flex: none;
  background: var(--bg-surface-2);
}

.tile-body {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 10px 12px 12px;
}

.badge {
  align-self: flex-start;
  background: var(--bg-surface-2);
  color: var(--text-secondary);
  font-weight: 600;
}

.name {
  font-size: 13px;
  font-weight: 700;
  line-height: 1.4;
}

.meta {
  font-size: 11px;
  color: var(--text-secondary);
}

.list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.row {
  display: flex;
  align-items: stretch;
  gap: 14px;
  width: 100%;
  padding: 10px 12px;
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  background: var(--bg-surface);
  color: inherit;
  cursor: pointer;
  text-align: left;
  box-shadow: 0 1px 6px color-mix(in srgb, #000 6%, transparent);
  transition:
    transform var(--transition-fast),
    border-color var(--transition-fast),
    box-shadow var(--transition-fast);
}

.row:hover {
  transform: translateY(-2px);
  border-color: var(--accent);
  box-shadow: 0 6px 16px color-mix(in srgb, #000 12%, transparent);
}

.row-cover {
  flex: none;
  width: 72px;
  height: calc(72px * 2080 / 1488);
  border-radius: var(--radius-sm);
  overflow: hidden;
  background: var(--bg-surface-2);
}

.row-cover :deep(.deck-cover img) {
  object-fit: cover;
}

.row-body {
  flex: 1 1 auto;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 6px;
}

.row-meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px 12px;
  font-size: 12px;
  color: var(--text-secondary);
}

.code {
  font-family: Consolas, 'Courier New', monospace;
  font-weight: 600;
}

.date {
  color: var(--text-secondary);
}

.pager {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: center;
  margin-top: 20px;
}
</style>
