<script setup lang="ts">
/**
 * 管理后台公共弹窗
 * - 关闭 / 可选全屏（同一行）
 * - 右下角取消、确定（可用 footer slot 覆盖）
 * - 全屏时内容区纵向撑满
 */
import { computed, ref, watch } from 'vue'

const props = withDefaults(
  defineProps<{
    modelValue: boolean
    title?: string
    width?: string | number
    fullscreenable?: boolean
    showFooter?: boolean
    confirmText?: string
    cancelText?: string
    confirmLoading?: boolean
    confirmDisabled?: boolean
    destroyOnClose?: boolean
  }>(),
  {
    title: '',
    width: '720px',
    fullscreenable: false,
    showFooter: true,
    confirmText: '确定',
    cancelText: '取消',
    confirmLoading: false,
    confirmDisabled: false,
    destroyOnClose: true,
  },
)

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  confirm: []
  cancel: []
}>()

const isFullscreen = ref(false)

const visible = computed({
  get: () => props.modelValue,
  set: (v: boolean) => emit('update:modelValue', v),
})

watch(
  () => props.modelValue,
  (open) => {
    if (!open) isFullscreen.value = false
  },
)

function handleCancel() {
  emit('cancel')
  visible.value = false
}

function handleConfirm() {
  emit('confirm')
}

function toggleFullscreen() {
  isFullscreen.value = !isFullscreen.value
}
</script>

<template>
  <el-dialog
    v-model="visible"
    :width="width"
    :fullscreen="isFullscreen"
    :show-close="false"
    :close-on-click-modal="false"
    :destroy-on-close="destroyOnClose"
    class="mtcg-dialog"
    :class="{ 'is-fullscreen': isFullscreen }"
  >
    <template #header>
      <div class="mtcg-dialog__header">
        <div class="mtcg-dialog__title-row">
          <span class="mtcg-dialog__title">{{ title }}</span>
          <slot name="title-extra" />
        </div>
        <div class="mtcg-dialog__actions">
          <slot name="header-extra" />
          <button
            v-if="fullscreenable"
            type="button"
            class="mtcg-dialog__icon-btn"
            :title="isFullscreen ? '退出全屏' : '全屏'"
            @click="toggleFullscreen"
          >
            <el-icon :size="16"><FullScreen /></el-icon>
          </button>
          <button
            type="button"
            class="mtcg-dialog__icon-btn"
            title="关闭"
            @click="handleCancel"
          >
            <el-icon :size="16"><Close /></el-icon>
          </button>
        </div>
      </div>
    </template>

    <div class="mtcg-dialog__body" :class="{ 'is-fill': isFullscreen }">
      <slot />
    </div>

    <template v-if="showFooter" #footer>
      <slot name="footer">
        <el-button @click="handleCancel">{{ cancelText }}</el-button>
        <el-button
          type="primary"
          :loading="confirmLoading"
          :disabled="confirmDisabled"
          @click="handleConfirm"
        >
          {{ confirmText }}
        </el-button>
      </slot>
    </template>
  </el-dialog>
</template>

<style scoped>
.mtcg-dialog__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  width: 100%;
  padding-right: 0;
}
.mtcg-dialog__title-row {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  min-width: 0;
}
.mtcg-dialog__title {
  min-width: 0;
  font-size: 16px;
  font-weight: 600;
  line-height: 24px;
  color: var(--el-text-color-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.mtcg-dialog__actions {
  display: flex;
  align-items: center;
  flex-shrink: 0;
  gap: 2px;
}
.mtcg-dialog__icon-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  padding: 0;
  border: none;
  border-radius: 4px;
  background: transparent;
  color: var(--el-color-info);
  cursor: pointer;
  outline: none;
}
.mtcg-dialog__icon-btn:hover {
  color: var(--el-color-primary);
  background: var(--el-fill-color-light);
}
.mtcg-dialog__body {
  min-height: 0;
}
.mtcg-dialog__body.is-fill {
  flex: 1;
  min-height: 0;
  height: 100%;
  display: flex;
  flex-direction: column;
}
</style>

<!-- dialog 挂到 body，需非 scoped 才能改 el-dialog 布局 -->
<style>
.el-dialog.mtcg-dialog.is-fullscreen {
  display: flex;
  flex-direction: column;
}
.el-dialog.mtcg-dialog.is-fullscreen .el-dialog__header {
  flex-shrink: 0;
}
.el-dialog.mtcg-dialog.is-fullscreen .el-dialog__body {
  flex: 1;
  min-height: 0;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}
.el-dialog.mtcg-dialog.is-fullscreen .el-dialog__footer {
  flex-shrink: 0;
}
.el-dialog.mtcg-dialog.is-fullscreen .mtcg-dialog__body {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}
.el-dialog.mtcg-dialog.is-fullscreen .mtcg-dialog__body > * {
  flex: 1;
  min-height: 0;
  height: 100% !important;
}
/* 选择器分栏在全屏下撑满 */
.el-dialog.mtcg-dialog.is-fullscreen .splitter-wrap {
  height: 100% !important;
  flex: 1;
  min-height: 0;
}
</style>
