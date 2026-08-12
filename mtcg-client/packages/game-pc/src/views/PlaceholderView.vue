<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useThemeStore } from '@mtcg/common'

useThemeStore()

const route = useRoute()
const router = useRouter()

const title = computed(() => (typeof route.meta.title === 'string' ? route.meta.title : '即将推出'))
</script>

<template>
  <div class="placeholder-page">
    <div class="placeholder-panel">
      <p class="eyebrow">MTCG</p>
      <h1>{{ title }}</h1>
      <p class="desc">功能建设中，后续迭代开放。</p>
      <button type="button" class="btn-back" @click="router.push('/')">返回首页</button>
    </div>
  </div>
</template>

<style scoped>
.placeholder-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background:
    radial-gradient(ellipse 70% 45% at 50% 0%, rgba(0, 212, 170, 0.08), transparent 60%),
    var(--bg-base);
}

.placeholder-panel {
  width: min(420px, 100%);
  padding: 40px 32px;
  text-align: center;
  border-radius: 16px;
  background: var(--bg-surface);
  border: 1px solid var(--border);
  box-shadow: var(--shadow-md);
}

.eyebrow {
  margin: 0;
  letter-spacing: 0.16em;
  font-weight: 700;
  color: var(--accent);
}

h1 {
  margin: 12px 0 0;
  font-size: var(--font-size-xl);
  color: var(--text-primary);
}

.desc {
  margin: 12px 0 0;
  color: var(--text-secondary);
  font-size: var(--font-size-sm);
}

.btn-back {
  margin-top: 24px;
  height: 42px;
  padding: 0 20px;
  border: none;
  border-radius: 10px;
  background: var(--accent);
  color: #0b1220;
  font-weight: 600;
  cursor: pointer;
}
</style>
