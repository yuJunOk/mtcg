<script setup lang="ts">
/**
 * 游戏端公共弹窗壳
 * - 标题行：标题 + 可选全屏 + 关闭（X）
 * - 内容区：定高可滚；全屏时纵向撑满
 * - 底栏右侧：取消 / 确定（可用 footer slot 覆盖）
 */
import { computed, onMounted, onUnmounted, ref, useId, watch } from 'vue'

const props = withDefaults(
  defineProps<{
    open: boolean
    title?: string
    width?: string
    /** 内容区高度（非全屏）；支持 CSS 长度 */
    bodyHeight?: string
    fullscreenable?: boolean
    showFooter?: boolean
    confirmText?: string
    cancelText?: string
    confirmLoading?: boolean
    confirmDisabled?: boolean
    /** 关闭 / 取消是否禁用（如提交中） */
    closeDisabled?: boolean
    closeOnMask?: boolean
  }>(),
  {
    title: '',
    width: '520px',
    bodyHeight: 'min(56vh, 480px)',
    fullscreenable: false,
    showFooter: true,
    confirmText: '确定',
    cancelText: '取消',
    confirmLoading: false,
    confirmDisabled: false,
    closeDisabled: false,
    closeOnMask: true,
  },
)

const emit = defineEmits<{
  'update:open': [value: boolean]
  close: []
  confirm: []
  cancel: []
}>()

const isFullscreen = ref(false)
const titleId = useId()

const panelStyle = computed(() => {
  if (isFullscreen.value) {
    return { width: '100%', maxWidth: '100%', height: '100%' }
  }
  return { width: `min(${props.width}, 100%)` }
})

const bodyStyle = computed(() => {
  if (isFullscreen.value) {
    return { height: 'auto', flex: '1', minHeight: '0' }
  }
  return { height: props.bodyHeight }
})

watch(
  () => props.open,
  (open) => {
    if (!open) isFullscreen.value = false
  },
)

function requestClose(): void {
  if (props.closeDisabled) return
  emit('cancel')
  emit('close')
  emit('update:open', false)
}

function handleConfirm(): void {
  if (props.confirmDisabled || props.confirmLoading) return
  emit('confirm')
}

function toggleFullscreen(): void {
  isFullscreen.value = !isFullscreen.value
}

function onKeydown(e: KeyboardEvent): void {
  if (e.key === 'Escape' && props.open && !props.closeDisabled) {
    requestClose()
  }
}

onMounted(() => {
  window.addEventListener('keydown', onKeydown)
})

onUnmounted(() => {
  window.removeEventListener('keydown', onKeydown)
})
</script>

<template>
  <Teleport to="body">
    <div
      v-if="open"
      class="mask"
      :class="{ 'is-fullscreen': isFullscreen }"
      @click.self="closeOnMask && requestClose()"
    >
      <div
        class="panel"
        :class="{ 'is-fullscreen': isFullscreen }"
        :style="panelStyle"
        role="dialog"
        aria-modal="true"
        :aria-labelledby="titleId"
      >
        <header class="head">
          <div class="title-row">
            <h2 :id="titleId" class="title">{{ title }}</h2>
            <slot name="title-extra" />
          </div>
          <div class="head-actions">
            <slot name="header-extra" />
            <button
              v-if="fullscreenable"
              type="button"
              class="icon-btn"
              :title="isFullscreen ? '退出全屏' : '全屏'"
              :disabled="closeDisabled"
              @click="toggleFullscreen"
            >
              <svg v-if="!isFullscreen" viewBox="0 0 16 16" width="14" height="14" aria-hidden="true">
                <path
                  fill="currentColor"
                  d="M2 2h4v1.5H3.5V6H2V2zm8 0h4v4h-1.5V3.5H10V2zM2 10h1.5v2.5H6V14H2v-4zm8 2.5V14h4v-4h-1.5v2.5H10z"
                />
              </svg>
              <svg v-else viewBox="0 0 16 16" width="14" height="14" aria-hidden="true">
                <path
                  fill="currentColor"
                  d="M6 2v4H2V4.5h2.5V2H6zm8 2.5V6h-4V2h1.5v2.5H14zM2 10h4v4H4.5v-2.5H2V10zm8 0h4v1.5h-2.5V14H10v-4z"
                />
              </svg>
            </button>
            <button
              type="button"
              class="icon-btn"
              title="关闭"
              :disabled="closeDisabled"
              @click="requestClose"
            >
              <svg viewBox="0 0 16 16" width="14" height="14" aria-hidden="true">
                <path
                  fill="currentColor"
                  d="M3.2 3.2a.75.75 0 0 1 1.06 0L8 6.94l3.74-3.74a.75.75 0 1 1 1.06 1.06L9.06 8l3.74 3.74a.75.75 0 1 1-1.06 1.06L8 9.06l-3.74 3.74a.75.75 0 1 1-1.06-1.06L6.94 8 3.2 4.26a.75.75 0 0 1 0-1.06z"
                />
              </svg>
            </button>
          </div>
        </header>

        <div class="body" :style="bodyStyle">
          <slot />
        </div>

        <footer v-if="showFooter" class="foot">
          <slot name="footer">
            <button type="button" class="ghost" :disabled="closeDisabled" @click="requestClose">
              {{ cancelText }}
            </button>
            <button
              type="button"
              class="primary"
              :disabled="confirmDisabled || confirmLoading"
              @click="handleConfirm"
            >
              {{ confirmLoading ? '请稍候…' : confirmText }}
            </button>
          </slot>
        </footer>
      </div>
    </div>
  </Teleport>
</template>

<style scoped>
.mask {
  position: fixed;
  inset: 0;
  z-index: 40;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background: color-mix(in srgb, var(--bg-base) 78%, transparent);
}

.mask.is-fullscreen {
  padding: 0;
}

.panel {
  display: flex;
  flex-direction: column;
  max-height: min(90vh, 860px);
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  background: var(--bg-surface);
  box-shadow: var(--shadow-lg);
  overflow: hidden;
}

.panel.is-fullscreen {
  max-height: none;
  border-radius: 0;
}

.head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-shrink: 0;
  min-height: 52px;
  padding: 0 12px 0 18px;
  border-bottom: 1px solid var(--border);
}

.title-row {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  min-width: 0;
}

.title {
  margin: 0;
  min-width: 0;
  font-size: 16px;
  font-weight: 700;
  line-height: 24px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.head-actions {
  display: flex;
  align-items: center;
  flex-shrink: 0;
  gap: 2px;
}

.icon-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  padding: 0;
  border: none;
  border-radius: var(--radius-sm);
  background: transparent;
  color: var(--text-secondary);
  cursor: pointer;
}

.icon-btn:hover:not(:disabled) {
  color: var(--text-primary);
  background: var(--bg-surface-2);
}

.icon-btn:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.body {
  min-height: 0;
  overflow: auto;
  padding: 16px 18px;
  scrollbar-width: thin;
}

.foot {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
  min-height: 64px;
  padding: 12px 18px;
  border-top: 1px solid var(--border);
  background: var(--bg-surface);
}

.primary,
.ghost {
  height: 36px;
  padding: 0 16px;
  border-radius: var(--radius-sm);
  font-size: var(--font-size-sm);
  font-weight: 600;
  cursor: pointer;
}

.primary {
  border: none;
  background: var(--accent);
  color: var(--accent-contrast);
}

.ghost {
  border: 1px solid var(--border);
  background: transparent;
  color: var(--text-primary);
}

.primary:disabled,
.ghost:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
