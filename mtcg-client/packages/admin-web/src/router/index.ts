import { createRouter, createWebHashHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@mtcg/common/stores'

declare module 'vue-router' {
  interface RouteMeta {
    title?: string
    public?: boolean
    requiresAdmin?: boolean
  }
}

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/auth/LoginPage.vue'),
    meta: { title: '登录', public: true },
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'dashboard',
        component: () => import('@/views/DashboardView.vue'),
        meta: { title: '仪表盘' },
      },
      {
        path: 'cards',
        name: 'card-list',
        component: () => import('@/views/card/CardListView.vue'),
        meta: { title: '卡牌管理' },
      },
      {
        path: 'products',
        name: 'product-list',
        component: () => import('@/views/product/ProductListView.vue'),
        meta: { title: '产品管理' },
      },
      {
        path: 'cards/features',
        name: 'card-feature-list',
        component: () => import('@/views/card/CardFeatureListView.vue'),
        meta: { title: '卡牌特征管理', requiresAdmin: true },
      },
      {
        path: 'system/users',
        name: 'user-list',
        component: () => import('@/views/system/UserListView.vue'),
        meta: { title: '用户管理', requiresAdmin: true },
      },
    ],
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
    return { path: '/login' }
  }

  if (!userStore.userInfo) {
    try {
      await userStore.fetchUserInfo()
    } catch {
      if (!userStore.isLoggedIn) return { path: '/login' }
    }
  }

  if (to.meta.requiresAdmin && !userStore.isAdmin()) {
    return { path: '/' }
  }

  return true
})

export default router
