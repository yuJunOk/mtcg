<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useThemeStore, useUserStore } from '@mtcg/common/stores'

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
  if (!window.confirm('确定退出登录？')) return
  loggingOut.value = true
  try {
    await userStore.logout()
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
          <button
            type="button"
            class="icon-btn"
            :title="theme.theme === 'dark' ? '切换亮色' : '切换暗色'"
            @click="theme.toggle()"
          >
            <svg v-if="theme.theme === 'dark'" viewBox="0 0 16 16" width="16" height="16" aria-hidden="true">
              <circle cx="8" cy="8" r="3.2" fill="currentColor" />
              <g stroke="currentColor" stroke-width="1.4" stroke-linecap="round">
                <path d="M8 1.4v1.6M8 13v1.6M1.4 8h1.6M13 8h1.6M3.1 3.1l1.1 1.1M11.8 11.8l1.1 1.1M3.1 12.9l1.1-1.1M11.8 4.2l1.1-1.1" />
              </g>
            </svg>
            <svg v-else viewBox="0 0 16 16" width="16" height="16" aria-hidden="true">
              <path
                fill="currentColor"
                d="M11.4 10.2A5.2 5.2 0 0 1 6.2 3.4 5.4 5.4 0 1 0 11.4 10.2Z"
              />
            </svg>
          </button>
          <div class="account" :title="displayUsercode">
            <span class="avatar">{{ avatarLetter }}</span>
            <span class="name">{{ displayName }}</span>
          </div>
          <button type="button" class="text-btn" :disabled="loggingOut" @click="handleLogout">
            退出
          </button>
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

.icon-btn,
.text-btn {
  border: none;
  background: transparent;
  color: var(--text-secondary);
  cursor: pointer;
}

.icon-btn {
  width: 36px;
  height: 36px;
  display: grid;
  place-items: center;
  border-radius: 50%;
}

.icon-btn:hover,
.text-btn:hover:not(:disabled) {
  color: var(--accent);
  background: var(--accent-soft);
}

.text-btn {
  height: 32px;
  padding: 0 10px;
  border-radius: var(--radius-sm);
  font-size: var(--font-size-sm);
}

.text-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
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
  overflow: auto;
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
