import { createRouter, createWebHashHistory } from 'vue-router'

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    {
      path: '/',
      component: () => import('@/views/DashboardView.vue'),
      meta: { title: '仪表盘' },
    },
    {
      path: '/cards',
      name: 'Cards',
      component: () => import('@/views/card/CardListView.vue'),
      meta: { title: '卡牌管理' },
    },
    {
      path: '/products',
      name: 'Products',
      component: () => import('@/views/product/ProductListView.vue'),
      meta: { title: '产品管理' },
    },
  ],
})

export default router