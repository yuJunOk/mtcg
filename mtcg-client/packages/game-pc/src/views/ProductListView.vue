<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { NEmpty, NPagination, NSpin, NTag } from 'naive-ui'
import {
  PRODUCT_CATEGORY_OPTIONS,
  getProductCategoryLabel,
  productApi,
  productCoverPath,
  resolveProductCategory,
} from '@mtcg/common'
import type { ProductCategory, ProductVO } from '@mtcg/common'
import DeckCover from '@/components/DeckCover.vue'

//#region 状态
const router = useRouter()

const loading = ref(false)
const allProducts = ref<ProductVO[]>([])
const category = ref<'' | ProductCategory>('')
const pageNum = ref(1)
const pageSize = ref(10)
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
      <h1>商品</h1>
    </header>

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
      <span class="count">共 {{ total }} 条</span>
    </div>

    <div v-if="loading && allProducts.length === 0" class="spin-wrap">
      <n-spin size="large" />
    </div>
    <div v-else-if="!loading && pageRows.length === 0" class="empty">
      <n-empty description="暂无商品" />
    </div>

    <div v-else class="grid">
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
        <span class="badge">{{ categoryOf(p) }}</span>
        <span class="name">{{ p.productName || p.productCode }}【{{ p.productCode }}】</span>
        <span class="meta">发售日期：{{ formatDate(p.releaseDate) }}</span>
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

.head h1 {
  margin: 0;
  font-size: var(--font-size-lg);
  font-weight: 700;
}

.filter-bar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px 12px;
  margin-bottom: 14px;
}

.chips {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.count {
  margin-left: auto;
  font-size: 12px;
  color: var(--text-secondary);
}

.spin-wrap,
.empty {
  padding: 48px 0;
  display: flex;
  justify-content: center;
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
  transition: transform var(--transition-fast), border-color var(--transition-fast);
}

.tile:hover {
  transform: translateY(-2px);
  border-color: var(--accent);
}

.cover {
  display: block;
  width: var(--card-cover-w);
  height: var(--card-cover-h);
  flex: none;
  background: var(--bg-surface-2);
}

.badge {
  margin: 10px 10px 0;
  font-size: 11px;
  font-weight: 600;
  color: var(--text-secondary);
}

.name {
  margin: 4px 10px 0;
  font-size: 13px;
  font-weight: 700;
  line-height: 1.4;
}

.meta {
  margin: 4px 10px 12px;
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
</style>
