<script setup lang="ts">
/**
 * 卡牌特征选择器
 * - multiple=true（默认）：多选 checkbox，v-model 为 number[]
 * - multiple=false：单选 radio，v-model 仍为 number[]（最多 1 个）
 */
import { ref, computed, watch, onMounted } from 'vue'
import { cardFeatureApi } from '@mtcg/common/api'
import type { CardFeatureVO } from '@mtcg/common/types'
import MtcgDialog from './MtcgDialog.vue'

const props = withDefaults(
  defineProps<{
    modelValue: number[]
    multiple?: boolean
    placeholder?: string
  }>(),
  {
    multiple: true,
    placeholder: '点击选择特征',
  },
)

const emit = defineEmits<{
  'update:modelValue': [value: number[]]
}>()

const loading = ref(false)
const dialogVisible = ref(false)
const allFeatures = ref<CardFeatureVO[]>([])
const keyword = ref('')
const draftIds = ref<number[]>([])

const selectedIds = computed(() => props.modelValue ?? [])

const selectedFeatures = computed(() => {
  const idSet = new Set(selectedIds.value)
  return allFeatures.value.filter((f) => idSet.has(f.id))
})

const displayText = computed(() => {
  if (!selectedFeatures.value.length) return ''
  return selectedFeatures.value.map((f) => f.name).join('、')
})

const draftSet = computed(() => new Set(draftIds.value))

const filteredFeatures = computed(() => {
  const kw = keyword.value.trim().toLowerCase()
  if (!kw) return allFeatures.value
  return allFeatures.value.filter(
    (f) => f.code.toLowerCase().includes(kw) || f.name.toLowerCase().includes(kw),
  )
})

const draftFeatures = computed(() => {
  const idSet = draftSet.value
  return allFeatures.value.filter((f) => idSet.has(f.id))
})

function isDraftSelected(id: number): boolean {
  return draftSet.value.has(id)
}

function selectRow(row: CardFeatureVO) {
  if (props.multiple) {
    if (isDraftSelected(row.id)) {
      draftIds.value = draftIds.value.filter((x) => x !== row.id)
    } else {
      draftIds.value = [...draftIds.value, row.id]
    }
  } else {
    draftIds.value = [row.id]
  }
}

function removeFeature(id: number) {
  draftIds.value = draftIds.value.filter((x) => x !== id)
}

function clearDraft() {
  draftIds.value = []
}

function openDialog() {
  draftIds.value = props.multiple
    ? [...selectedIds.value]
    : selectedIds.value.slice(0, 1)
  keyword.value = ''
  dialogVisible.value = true
}

function handleClear(e: Event) {
  e.stopPropagation()
  emit('update:modelValue', [])
}

function handleConfirm() {
  emit('update:modelValue', [...draftIds.value])
  dialogVisible.value = false
}

function handleCancel() {
  dialogVisible.value = false
}

async function loadFeatures() {
  allFeatures.value = await cardFeatureApi.list(loading)
}

watch(dialogVisible, (open) => {
  if (open && allFeatures.value.length === 0) {
    void loadFeatures()
  }
})

onMounted(() => {
  void loadFeatures()
})
</script>

<template>
  <div class="feature-selector" v-loading="loading">
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
      title="选择卡牌特征"
      width="760px"
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
          <el-splitter-panel size="55%" :min="180">
            <div class="pane">
              <div class="pane-head">
                <span>全部特征</span>
                <el-input
                  v-model="keyword"
                  clearable
                  placeholder="搜索编码/名称"
                  style="width: 160px"
                />
              </div>
              <div class="pane-body">
                <div
                  v-for="f in filteredFeatures"
                  :key="f.id"
                  class="row"
                  :class="{ selected: isDraftSelected(f.id) }"
                  @click="selectRow(f)"
                >
                  <el-radio
                    v-if="!multiple"
                    :model-value="draftIds[0] ?? null"
                    :value="f.id"
                    @click.prevent
                  />
                  <el-checkbox
                    v-else
                    :model-value="isDraftSelected(f.id)"
                    @click.prevent
                  />
                  <span class="name">{{ f.name }}</span>
                  <span class="code">{{ f.code }}</span>
                </div>
                <el-empty v-if="!filteredFeatures.length" description="暂无特征" :image-size="48" />
              </div>
            </div>
          </el-splitter-panel>
          <el-splitter-panel size="45%" :min="160">
            <div class="pane">
              <div class="pane-head">
                <span>已选（{{ draftFeatures.length }}）</span>
                <el-button v-if="draftFeatures.length" link type="danger" @click="clearDraft">清空</el-button>
              </div>
              <div class="pane-body">
                <div v-for="f in draftFeatures" :key="f.id" class="row">
                  <span class="name">{{ f.name }}</span>
                  <span class="code">{{ f.code }}</span>
                  <el-button link type="danger" @click="removeFeature(f.id)">移除</el-button>
                </div>
                <el-empty v-if="!draftFeatures.length" description="从左侧添加" :image-size="48" />
              </div>
            </div>
          </el-splitter-panel>
        </el-splitter>
      </div>
    </MtcgDialog>
  </div>
</template>

<style scoped>
.feature-selector {
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
.code {
  flex-shrink: 0;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}
.name {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 13px;
}
.row :deep(.el-radio),
.row :deep(.el-checkbox) {
  height: auto;
  margin-right: 0;
}
</style>
