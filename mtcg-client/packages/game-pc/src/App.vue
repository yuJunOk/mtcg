<script setup lang="ts">
import { computed, watch } from 'vue'
import { NConfigProvider, darkTheme } from 'naive-ui'
import { setHttpErrorNotifier } from '@mtcg/common'
import { useThemeStore } from '@mtcg/common/stores'
import { buildThemeOverrides, syncFeedbackTheme, toast } from '@/feedback'

const themeStore = useThemeStore()

syncFeedbackTheme(themeStore.theme)
watch(
  () => themeStore.theme,
  (mode) => {
    syncFeedbackTheme(mode)
  },
)

setHttpErrorNotifier((message: string) => {
  toast.error(message)
})

const naiveTheme = computed(() => (themeStore.theme === 'dark' ? darkTheme : null))
const themeOverrides = computed(() => buildThemeOverrides(themeStore.theme))
</script>

<template>
  <!-- 必须撑满 #app，否则 html/body overflow:hidden 会裁掉下方内容且无法滚动 -->
  <NConfigProvider
    class="app-root"
    :theme="naiveTheme"
    :theme-overrides="themeOverrides"
  >
    <router-view />
  </NConfigProvider>
</template>

<style>
html,
body,
#app {
  margin: 0;
  height: 100%;
  overflow: hidden;
}

.app-root {
  height: 100%;
}
</style>
