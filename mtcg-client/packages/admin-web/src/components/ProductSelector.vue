<script setup lang="ts">
/**
 * 产品选择器
 * - multiple=false：单选（radio），v-model 为 string
 * - multiple=true：多选（checkbox），v-model 为 string[]
 */
import { ref, computed, watch, onMounted } from 'vue'
import { productApi } from '@mtcg/common/api'
import type { ProductVO } from '@mtcg/common/types'
import MtcgDialog from './MtcgDialog.vue'

const props = withDefaults(
  defineProps<{
    modelValue: string | string[]
    multiple?: boolean
    placeholder?: string
  }>(),
  {
    multiple: false,
    placeholder: '点击选择产品',
  },
)

const emit = defineEmits<{
  'update:modelValue': [value: string | string[]]
}>()

const loading = ref(false)
const dialogVisible = ref(false)
const allProducts = ref<ProductVO[]>([])
const keyword = ref('')
const draftCodes = ref<string[]>([])

const selectedCodes = computed(() => {
  const v = props.modelValue
  if (Array.isArray(v)) return v
  return v ? [v] : []
})

const displayText = computed(() => {
  if (!selectedCodes.value.length) return ''
  return selectedCodes.value
    .map((code) => {
      const p = allProducts.value.find((x) => x.productCode === code)
      return p ? `${p.productCode} - ${p.productName}` : code
    })
    .join('、')
})

const filteredProducts = computed(() => {
  const kw = keyword.value.trim().toLowerCase()
  if (!kw) return allProducts.value
  return allProducts.value.filter(
    (p) =>
      p.productCode.toLowerCase().includes(kw) ||
      p.productName.toLowerCase().includes(kw),
  )
})

const draftSet = computed(() => new Set(draftCodes.value))

const draftProducts = computed(() =>
  allProducts.value.filter((p) => draftSet.value.has(p.productCode)),
)

function isDraftSelected(code: string): boolean {
  return draftSet.value.has(code)
}

function selectRow(p: ProductVO) {
  if (props.multiple) {
    if (isDraftSelected(p.productCode)) {
      draftCodes.value = draftCodes.value.filter((c) => c !== p.productCode)
    } else {
      draftCodes.value = [...draftCodes.value, p.productCode]
    }
  } else {
    draftCodes.value = [p.productCode]
  }
}

function removeDraft(code: string) {
  draftCodes.value = draftCodes.value.filter((c) => c !== code)
}

function clearDraft() {
  draftCodes.value = []
}

function openDialog() {
  draftCodes.value = [...selectedCodes.value]
  keyword.value = ''
  dialogVisible.value = true
}

function handleClear(e: Event) {
  e.stopPropagation()
  emit('update:modelValue', props.multiple ? [] : '')
}

function handleConfirm() {
  if (props.multiple) {
    emit('update:modelValue', [...draftCodes.value])
  } else {
    emit('update:modelValue', draftCodes.value[0] ?? '')
  }
  dialogVisible.value = false
}

function handleCancel() {
  dialogVisible.value = false
}

async function loadProducts() {
  const page = await productApi.list({ pageNum: 1, pageSize: 200 }, loading)
  allProducts.value = page.records
}

watch(dialogVisible, (open) => {
  if (open && allProducts.value.length === 0) {
    void loadProducts()
  }
})

onMounted(() => {
  void loadProducts()
})
</script>

<template>
  <div class="product-selector" v-loading="loading">
    <el-input
      :model-value="displayText"
      readonly
      clearable
      class="selector-trigger"
      :placeholder="placeholder"
      @click="openDialog"
      @clear="handleClear"
    />

    <MtcgDialog
      v-model="dialogVisible"
      title="选择产品"
      width="720px"
      fullscreenable
      @confirm="handleConfirm"
      @cancel="handleCancel"
    >
      <template #title-extra>
        <el-tag size="small" :type="multiple ? 'warning' : 'primary'" effect="plain">
          {{ multiple ? '多选' : '单选' }}
        </el-tag>
      </template>

      <div class="splitter-wrap">
        <el-splitter>
          <el-splitter-panel size="60%" :min="180">
            <div class="pane">
              <div class="pane-head">
                <span>全部产品</span>
                <el-input
                  v-model="keyword"
                  clearable
                  placeholder="搜索编号/名称"
                  style="width: 160px"
                />
              </div>
              <div class="pane-body">
                <div
                  v-for="p in filteredProducts"
                  :key="p.productCode"
                  class="row"
                  :class="{ selected: isDraftSelected(p.productCode) }"
                  @click="selectRow(p)"
                >
                  <el-radio
                    v-if="!multiple"
                    :model-value="draftCodes[0] ?? ''"
                    :value="p.productCode"
                    @click.prevent
                  />
                  <el-checkbox
                    v-else
                    :model-value="isDraftSelected(p.productCode)"
                    @click.prevent
                  />
                  <span class="name">{{ p.productName }}</span>
                  <span class="code">{{ p.productCode }}</span>
                </div>
                <el-empty v-if="!filteredProducts.length" description="暂无产品" :image-size="48" />
              </div>
            </div>
          </el-splitter-panel>
          <el-splitter-panel size="40%" :min="160">
            <div class="pane">
              <div class="pane-head">
                <span>已选（{{ draftProducts.length }}）</span>
                <el-button v-if="draftProducts.length" link type="danger" @click="clearDraft">清空</el-button>
              </div>
              <div class="pane-body">
                <div v-for="p in draftProducts" :key="p.productCode" class="row">
                  <span class="name">{{ p.productName }}</span>
                  <span class="code">{{ p.productCode }}</span>
                  <el-button link type="danger" @click="removeDraft(p.productCode)">移除</el-button>
                </div>
                <el-empty v-if="!draftProducts.length" description="从左侧选择" :image-size="48" />
              </div>
            </div>
          </el-splitter-panel>
        </el-splitter>
      </div>
    </MtcgDialog>
  </div>
</template>

<style scoped>
.product-selector {
  width: 100%;
}
.selector-trigger {
  cursor: pointer;
}
.selector-trigger :deep(.el-input__wrapper),
.selector-trigger :deep(.el-input__inner) {
  cursor: pointer;
}
.splitter-wrap {
  height: 360px;
  border: 1px solid var(--el-border-color);
  border-radius: 6px;
  overflow: hidden;
}
.pane {
  height: 100%;
  display: flex;
  flex-direction: column;
  min-width: 0;
  overflow: hidden;
}
.pane-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  padding: 10px 12px;
  border-bottom: 1px solid var(--el-border-color-lighter);
  font-size: 13px;
  font-weight: 600;
  flex-shrink: 0;
}
.pane-body {
  flex: 1;
  min-height: 0;
  overflow: auto;
  padding: 6px;
}
.row {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 10px;
  border-radius: 4px;
  cursor: pointer;
}
.row:hover {
  background: var(--el-fill-color-light);
}
.row.selected {
  background: var(--el-color-primary-light-9);
}
.name {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 13px;
}
.code {
  flex-shrink: 0;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}
.row :deep(.el-radio),
.row :deep(.el-checkbox) {
  height: auto;
  margin-right: 0;
}
</style>
