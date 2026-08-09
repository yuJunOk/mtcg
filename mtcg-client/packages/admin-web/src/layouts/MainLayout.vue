<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@mtcg/common/stores'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const displayName = computed(
  () => userStore.userInfo?.nickname || userStore.userInfo?.username || ''
)

const displayAvatar = computed(
  () => userStore.userInfo?.avatar || ''
)

const roleLabel = computed(() => {
  switch (userStore.role) {
    case 'SYS_ADMIN': return '系统管理员'
    case 'CARD_ADMIN': return '卡牌管理员'
    case 'PLAYER': return '玩家'
    case 'AI': return 'AI'
    default: return ''
  }
})

const roleTagType = computed<'primary' | 'warning' | 'info'>(() => {
  switch (userStore.role) {
    case 'SYS_ADMIN': return 'primary'
    case 'CARD_ADMIN': return 'warning'
    default: return 'info'
  }
})

const menuItems = computed(() => {
  const items = [
    { index: '/dashboard', icon: 'HomeFilled', label: '仪表盘' },
    { index: '/cards', icon: 'Picture', label: '卡牌管理' },
    { index: '/products', icon: 'Box', label: '产品管理' },
  ]
  if (userStore.isAdmin()) {
    items.push({ index: '/system', icon: 'Setting', label: '系统管理', isSub: true, children: [
      { index: '/system/users', icon: 'User', label: '用户管理' },
    ]})
  }
  return items
})

function handleCommand(command: string) {
  if (command === 'logout') {
    handleLogout()
  } else if (command === 'profile') {
    // 预留：跳转个人中心
    ElMessage.info('个人中心开发中')
  }
}

function handleLogout() {
  userStore.logout()
  router.push('/login')
}

onMounted(async () => {
  if (userStore.isLoggedIn && !userStore.userInfo) {
    try {
      await userStore.fetchUserInfo()
    } catch {
      // 忽略，由拦截器统一处理
    }
  }
})
</script>

<template>
  <el-container class="layout">
    <el-aside width="220px" class="sidebar">
      <!-- Logo 区域 -->
      <div class="logo-area">
        <div class="logo-mark">
          <span>M</span>
        </div>
        <div class="logo-info">
          <div class="logo-title-row">
            <div class="logo-title">MTCG</div>
            <div class="version-tag">v1.0.0</div>
          </div>
          <div class="logo-subtitle">管理后台</div>
        </div>
      </div>

      <!-- 导航菜单 -->
      <el-menu
        :default-active="route.path"
        router
        class="nav-menu"
        :unique-opened="true"
        background-color="transparent"
        text-color="#8b95a7"
        active-text-color="#409EFF"
      >
        <template v-for="item in menuItems" :key="item.index">
          <el-menu-item v-if="!item.isSub" :index="item.index">
            <el-icon><component :is="item.icon" /></el-icon>
            <template #title>{{ item.label }}</template>
          </el-menu-item>
          <el-sub-menu v-else :index="item.index">
            <template #title>
              <el-icon><component :is="item.icon" /></el-icon>
              <span>{{ item.label }}</span>
            </template>
            <el-menu-item
              v-for="child in item.children!"
              :key="child.index"
              :index="child.index"
            >
              <el-icon><component :is="child.icon" /></el-icon>
              <template #title>{{ child.label }}</template>
            </el-menu-item>
          </el-sub-menu>
        </template>
      </el-menu>
    </el-aside>

    <el-container class="right-container">
      <el-header class="header" height="60px">
        <!-- 面包屑 / 页面标题 -->
        <div class="header-left">
          <span class="page-title">{{ route.meta.title }}</span>
        </div>

        <!-- 右侧操作区 -->
        <div class="header-right">
          <!-- 用户下拉菜单 -->
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="user-trigger">
              <el-avatar :size="36" class="user-avatar" :src="displayAvatar || undefined">
                {{ displayName.charAt(0).toUpperCase() }}
              </el-avatar>
              <div class="user-info">
                <div class="user-name">{{ displayName }}</div>
              </div>
              <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu class="user-dropdown-menu">
                <div class="dropdown-header">
                  <el-avatar :size="44" :src="displayAvatar || undefined">
                    {{ displayName.charAt(0).toUpperCase() }}
                  </el-avatar>
                  <div class="dropdown-user-info">
                    <div class="dropdown-user-name">{{ displayName }}</div>
                    <el-tag :type="roleTagType" size="small" effect="dark">{{ roleLabel }}</el-tag>
                  </div>
                </div>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  <span>个人中心</span>
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  <span>退出登录</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main>
        <div class="page-root">
          <router-view />
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.layout {
  height: 100vh;
}

/* ========== 侧边栏 ========== */
.sidebar {
  background: #1e2a3a;
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

/* 左侧彩条装饰 */
.sidebar::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 3px;
  background: linear-gradient(180deg, #409EFF, #67C23A, #E6A23C);
}

.sidebar::after {
  content: '';
  position: absolute;
  top: -60px;
  right: -40px;
  width: 140px;
  height: 140px;
  background: radial-gradient(circle, rgba(64, 158, 255, 0.12), transparent 70%);
  border-radius: 50%;
}

.logo-area {
  height: 64px;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  position: relative;
  z-index: 1;
}

.logo-mark {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: linear-gradient(135deg, #409EFF 0%, #2b6cb0 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.35);
}

.logo-mark span {
  color: #fff;
  font-size: 18px;
  font-weight: 800;
}

.logo-info {
  display: flex;
  flex-direction: column;
}

.logo-title-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.logo-title {
  color: #fff;
  font-size: 16px;
  font-weight: 700;
  letter-spacing: 1px;
  line-height: 1.2;
}

.logo-subtitle {
  color: #6b7a8f;
  font-size: 11px;
  margin-top: 2px;
}

.version-tag {
  font-size: 10px;
  color: #6b7a8f;
  background: rgba(255, 255, 255, 0.06);
  padding: 2px 6px;
  border-radius: 4px;
  letter-spacing: 0.5px;
}

.nav-menu {
  flex: 1;
  border-right: none;
  padding: 16px 12px;
  position: relative;
  z-index: 1;
  overflow-y: auto;
}

.nav-menu :deep(.el-menu) {
  background: transparent;
}

.nav-menu :deep(.el-menu-item),
.nav-menu :deep(.el-sub-menu__title) {
  height: 42px;
  line-height: 42px;
  margin: 4px 0;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 400;
}

.nav-menu :deep(.el-menu-item .el-icon),
.nav-menu :deep(.el-sub-menu__title .el-icon) {
  font-size: 18px;
  margin-right: 10px;
  color: #6b7a8f;
}

.nav-menu :deep(.el-menu-item:hover),
.nav-menu :deep(.el-sub-menu__title:hover) {
  background: rgba(255, 255, 255, 0.06);
  color: #fff !important;
}

.nav-menu :deep(.el-menu-item:hover .el-icon),
.nav-menu :deep(.el-sub-menu__title:hover .el-icon) {
  color: #fff;
}

/* 激活态：左侧彩条 + 浅蓝背景 */
.nav-menu :deep(.el-menu-item.is-active) {
  background: rgba(64, 158, 255, 0.15);
  color: #fff !important;
  font-weight: 500;
  position: relative;
}

.nav-menu :deep(.el-menu-item.is-active::before) {
  content: '';
  position: absolute;
  left: -12px;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 20px;
  background: #409EFF;
  border-radius: 2px;
}

.nav-menu :deep(.el-menu-item.is-active .el-icon) {
  color: #409EFF;
}

.nav-menu :deep(.el-sub-menu .el-menu-item) {
  padding-left: 46px !important;
  font-size: 13px;
}

.nav-menu :deep(.el-sub-menu__icon-arrow) {
  color: #6b7a8f;
}

/* 滚动条 */
.nav-menu::-webkit-scrollbar {
  width: 4px;
}
.nav-menu::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 2px;
}

/* ========== 右侧布局 ========== */
.right-container {
  min-height: 0;
  flex-direction: column;
  display: flex;
  background: #f0f2f5;
}

/* ========== 顶部导航 ========== */
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  background: #fff;
  border-bottom: 1px solid #eef0f3;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.03);
  flex-shrink: 0;
}

.header-left {
  display: flex;
  align-items: center;
}

.page-title {
  font-size: 17px;
  font-weight: 600;
  color: #1a2a3a;
  position: relative;
  padding-left: 12px;
}

.page-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 16px;
  background: linear-gradient(180deg, #409EFF, #2b6cb0);
  border-radius: 2px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 用户触发按钮 */
.user-trigger {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 12px 6px 6px;
  border-radius: 24px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.user-trigger:hover {
  background: #f5f7fa;
}

.user-avatar {
  background: linear-gradient(135deg, #409EFF, #2b6cb0);
  font-weight: 600;
  flex-shrink: 0;
}

.user-info {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #1a2a3a;
  line-height: 1.2;
}

.user-role-row {
  display: flex;
  align-items: center;
  margin-top: 2px;
}

.dropdown-icon {
  color: #909399;
  font-size: 14px;
}

/* 用户下拉菜单 */
.user-dropdown-menu {
  padding: 0 !important;
  min-width: 200px !important;
  border-radius: 12px !important;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12) !important;
  border: none !important;
  overflow: hidden;
}

.dropdown-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: linear-gradient(135deg, #f8fafc, #eef2f7);
  border-bottom: 1px solid #f0f0f0;
}

.dropdown-user-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.dropdown-user-name {
  font-size: 15px;
  font-weight: 600;
  color: #1a2a3a;
}

.user-dropdown-menu :deep(.el-dropdown-menu__item) {
  padding: 10px 16px;
  font-size: 14px;
  color: #303133;
}

.user-dropdown-menu :deep(.el-dropdown-menu__item:hover) {
  background: #f5f7fa;
  color: #409EFF;
}

.user-dropdown-menu :deep(.el-dropdown-menu__item .el-icon) {
  margin-right: 8px;
  font-size: 16px;
}

/* ========== 主内容区 ========== */
.el-main {
  background: #f0f2f5;
  padding: 20px;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.page-root {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}
</style>
