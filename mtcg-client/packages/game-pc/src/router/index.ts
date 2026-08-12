import { createRouter, createWebHashHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@mtcg/common/stores'

declare module 'vue-router' {
  interface RouteMeta {
    /** 页面标题 */
    title?: string
    /** 公开页（无需登录） */
    public?: boolean
  }
}

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/LoginView.vue'),
    meta: { title: '登录', public: true },
  },
  {
    path: '/',
    name: 'home',
    component: () => import('@/views/HomeView.vue'),
    meta: { title: '首页' },
  },
  {
    path: '/decks',
    name: 'deck-list',
    component: () => import('@/views/DeckListView.vue'),
    meta: { title: '我的卡组' },
  },
  {
    path: '/decks/new',
    name: 'deck-create',
    component: () => import('@/views/DeckBuilderView.vue'),
    meta: { title: '新建卡组' },
  },
  {
    path: '/decks/:id',
    name: 'deck-builder',
    component: () => import('@/views/DeckBuilderView.vue'),
    meta: { title: '卡组构筑' },
  },
  {
    path: '/match',
    name: 'match',
    component: () => import('@/views/PlaceholderView.vue'),
    meta: { title: '对战大厅' },
  },
  {
    path: '/battle/:gameId?',
    name: 'battle',
    component: () => import('@/views/BattleView.vue'),
    meta: { title: '对战' },
  },
  {
    path: '/coming-soon',
    name: 'coming-soon',
    component: () => import('@/views/PlaceholderView.vue'),
    meta: { title: '即将推出' },
  },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes,
})

router.beforeEach(async (to) => {
  const userStore = useUserStore()

  if (to.meta.public) {
    if (userStore.isLoggedIn && to.path === '/login') {
      return { path: '/' }
    }
    return true
  }

  if (!userStore.isLoggedIn) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }

  if (!userStore.userInfo) {
    try {
      await userStore.fetchUserInfo()
    } catch {
      userStore.clearSession()
      return { path: '/login' }
    }
  }

  return true
})

router.afterEach((to) => {
  const title = to.meta.title
  document.title = title ? `${title} · MTCG` : 'MTCG'
})

export default router
