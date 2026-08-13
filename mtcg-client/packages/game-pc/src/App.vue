<script setup lang="ts">
import { ref } from 'vue'
import { setHttpErrorNotifier } from '@mtcg/common'
import { useThemeStore } from '@mtcg/common/stores'

useThemeStore()

const toastMessage = ref('')
let hideTimer: ReturnType<typeof setTimeout> | undefined

setHttpErrorNotifier((message: string) => {
  toastMessage.value = message
  if (hideTimer) clearTimeout(hideTimer)
  hideTimer = setTimeout(() => {
    toastMessage.value = ''
  }, 3200)
})
</script>

<template>
  <router-view />
  <Transition name="toast">
    <div v-if="toastMessage" class="app-toast" role="status">{{ toastMessage }}</div>
  </Transition>
</template>

<style>
html,
body,
#app {
  margin: 0;
  min-height: 100%;
}

.app-toast {
  position: fixed;
  left: 50%;
  bottom: 36px;
  z-index: 9999;
  transform: translateX(-50%);
  max-width: min(480px, calc(100vw - 32px));
  padding: 12px 18px;
  border-radius: var(--radius-md);
  background: var(--bg-surface-3);
  border: 1px solid var(--border-light);
  color: var(--text-primary);
  box-shadow: var(--shadow-md);
  font-size: var(--font-size-sm);
  text-align: center;
}

.toast-enter-active,
.toast-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translate(-50%, 8px);
}
</style>
