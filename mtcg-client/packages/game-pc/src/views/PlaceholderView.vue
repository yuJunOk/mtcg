<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { NButton, NEmpty } from 'naive-ui'

const route = useRoute()
const router = useRouter()

const title = computed(() => (typeof route.meta.title === 'string' ? route.meta.title : '即将推出'))
</script>

<template>
  <div class="page">
    <n-empty :description="`${title} · 功能建设中，后续迭代开放。`">
      <template #extra>
        <n-button @click="router.push('/')">返回首页</n-button>
      </template>
    </n-empty>
  </div>
</template>

<style scoped>
.page {
  width: min(var(--shell-max), 100%);
  margin: 0 auto;
  padding: 48px var(--space-lg);
  display: flex;
  justify-content: center;
}
</style>
