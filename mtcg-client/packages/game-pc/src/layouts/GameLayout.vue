<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { NButton } from 'naive-ui'
import { useThemeStore, useUserStore } from '@mtcg/common/stores'
import { confirm, toast } from '@/feedback'

const route = useRoute()
const router = useRouter()
const theme = useThemeStore()
const userStore = useUserStore()
const loggingOut = ref(false)

const displayName = computed(() => {
  const info = userStore.userInfo
  if (!info) return '加载中…'
  return info.username?.trim() || info.usercode || '玩家'
})

const displayUsercode = computed(() => userStore.userInfo?.usercode ?? '')

const avatarLetter = computed(() => {
  const name = displayName.value.trim()
  return name ? name.slice(0, 1).toUpperCase() : '?'
})

const navItems = [
  { to: '/', name: 'home', label: '首页' },
  { to: '/match', name: 'match', label: '对战' },
  { to: '/decks', name: 'deck-list', label: '卡组' },
  { to: '/cards', name: 'card-list', label: '卡表' },
  { to: '/products', name: 'product-list', label: '商品' },
] as const

function isNavOn(name: string): boolean {
  if (name === 'home') return route.name === 'home'
  if (name === 'match') return route.name === 'match'
  if (name === 'deck-list') return route.name === 'deck-list'
  if (name === 'card-list') return route.name === 'card-list'
  if (name === 'product-list') return route.name === 'product-list'
  return false
}

onMounted(async () => {
  if (!userStore.userInfo) {
    try {
      await userStore.fetchUserInfo()
    } catch {
      // ignore
    }
  }
})

async function handleLogout(): Promise<void> {
  if (loggingOut.value) return
  const ok = await confirm({
    title: '退出登录',
    content: '确定退出当前账号？',
    positiveText: '退出',
  })
  if (!ok) return
  loggingOut.value = true
  try {
    await userStore.logout()
    toast.success('已退出登录')
    await router.replace('/login')
  } finally {
    loggingOut.value = false
  }
}
</script>

<template>
  <div class="shell">
    <header class="topbar">
      <div class="bar-inner">
        <button type="button" class="brand" @click="router.push('/')">
          <span class="mark">M</span>
          <span class="logo">MTCG</span>
        </button>
        <nav class="nav">
          <button
            v-for="item in navItems"
            :key="item.name"
            type="button"
            class="nav-link"
            :class="{ on: isNavOn(item.name) }"
            @click="router.push(item.to)"
          >
            {{ item.label }}
          </button>
        </nav>
        <div class="right">
          <n-button
            quaternary
            circle
            class="theme-btn"
            :title="theme.theme === 'dark' ? '切换亮色' : '切换暗色'"
            @click="theme.toggle()"
          >
            {{ theme.theme === 'dark' ? '☀️' : '🌙' }}
          </n-button>
          <div class="account" :title="displayUsercode">
            <span class="avatar">{{ avatarLetter }}</span>
            <span class="name">{{ displayName }}</span>
          </div>
          <n-button quaternary :loading="loggingOut" @click="handleLogout">退出</n-button>
        </div>
      </div>
    </header>
    <main class="body">
      <router-view />
    </main>
  </div>
</template>

<style scoped>
.shell {
  position: relative;
  height: 100%;
  display: flex;
  flex-direction: column;
  background: var(--bg-base);
  color: var(--text-primary);
}

.topbar {
  position: relative;
  z-index: 1;
  height: var(--topbar-h);
  flex-shrink: 0;
  border-bottom: 1px solid var(--border);
  background: var(--shell-topbar-bg);
  box-shadow: var(--edge-highlight);
}

.bar-inner {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  gap: var(--space-lg);
  padding: 0 var(--space-lg);
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  border: none;
  background: transparent;
  color: inherit;
  cursor: pointer;
  padding: 0;
}

.mark {
  width: 28px;
  height: 28px;
  display: grid;
  place-items: center;
  border-radius: 8px;
  background: var(--accent);
  color: var(--accent-contrast);
  font-size: 13px;
  font-weight: 800;
  letter-spacing: 0;
}

.logo {
  font-weight: 800;
  letter-spacing: 0.12em;
  font-size: 15px;
}

.nav {
  display: flex;
  align-self: stretch;
  gap: 2px;
  flex: 1;
}

.nav-link {
  height: 100%;
  padding: 0 16px;
  border: none;
  border-bottom: 2px solid transparent;
  background: transparent;
  color: var(--text-secondary);
  cursor: pointer;
  font-size: 14px;
}

.nav-link.on {
  color: var(--text-primary);
  border-bottom-color: var(--accent);
  font-weight: 600;
}

.nav-link:hover {
  color: var(--text-primary);
}

.right {
  display: flex;
  align-items: center;
  gap: 4px;
}

.theme-btn {
  font-size: 16px;
}

.account {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 6px;
  min-width: 0;
}

.avatar {
  width: 28px;
  height: 28px;
  flex-shrink: 0;
  display: grid;
  place-items: center;
  border-radius: 50%;
  background: var(--accent);
  color: var(--accent-contrast);
  font-size: 12px;
  font-weight: 700;
}

.name {
  font-size: var(--font-size-sm);
  font-weight: 600;
  max-width: 9em;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.body {
  position: relative;
  z-index: 1;
  flex: 1;
  min-height: 0;
  overflow-x: hidden;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
}

@media (max-width: 860px) {
  .nav {
    display: none;
  }

  .name {
    display: none;
  }
}
</style>
