<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const title = computed(() => (typeof route.meta.title === 'string' ? route.meta.title : '即将推出'))
</script>

<template>
  <div class="page">
    <header class="head">
      <p class="kicker">后续</p>
      <h1>{{ title }}</h1>
      <p class="desc">功能建设中，后续迭代开放。</p>
    </header>
    <button type="button" class="ghost" @click="router.push('/')">返回首页</button>
  </div>
</template>

<style scoped>
.page {
  width: min(var(--shell-max), 100%);
  margin: 0 auto;
  padding: 36px var(--space-lg) 64px;
}

.kicker {
  margin: 0;
  font-size: 12px;
  letter-spacing: 0.16em;
  font-weight: 700;
  color: var(--accent);
}

h1 {
  margin: 8px 0 0;
  font-size: var(--font-size-2xl);
  font-weight: 700;
}

.desc {
  margin: 10px 0 0;
  color: var(--text-secondary);
  font-size: var(--font-size-sm);
}

.ghost {
  margin-top: 28px;
  height: 40px;
  padding: 0 18px;
  border-radius: var(--radius-sm);
  border: 1px solid var(--border);
  background: transparent;
  color: var(--text-secondary);
  font-size: var(--font-size-sm);
  font-weight: 600;
  cursor: pointer;
}
</style>
