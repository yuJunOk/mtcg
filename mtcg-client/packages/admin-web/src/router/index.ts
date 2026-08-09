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
    name: 'Login',
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
        name: 'Dashboard',
        component: () => import('@/views/DashboardView.vue'),
        meta: { title: '仪表盘' },
      },
      {
        path: 'system/users',
        name: 'UserList',
        component: () => import('@/views/system/UserListView.vue'),
        meta: { title: '用户管理', requiresAdmin: true },
      },
      {
        path: 'cards',
        name: 'Cards',
        component: () => import('@/views/card/CardListView.vue'),
        meta: { title: '卡牌管理' },
      },
      {
        path: 'products',
        name: 'Products',
        component: () => import('@/views/product/ProductListView.vue'),
        meta: { title: '产品管理' },
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

  // 公开路由（登录页）
  if (to.meta.public) {
    if (userStore.isLoggedIn && to.path === '/login') {
      return { path: '/' }
    }
    return true
  }

  // 未登录 → 跳登录页
  if (!userStore.isLoggedIn) {
    return { path: '/login' }
  }

  // 已登录但未拉取用户信息 → 先拉取
  if (!userStore.userInfo) {
    try {
      await userStore.fetchUserInfo()
    } catch {
      // 拉取失败由拦截器处理，忽略
    }
  }

  // 需要管理员权限
  if (to.meta.requiresAdmin && !userStore.isAdmin()) {
    return { path: '/' }
  }

  return true
})

export default router
