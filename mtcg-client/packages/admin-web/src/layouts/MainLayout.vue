<script setup lang="ts">
import { useRoute } from 'vue-router'

const route = useRoute()
</script>

<template>
  <el-container class="layout">
    <el-aside width="220px" class="sidebar">
      <div class="logo-area">
        <span class="logo-text">MTCG 管理后台</span>
      </div>
      <el-menu
        :default-active="route.path"
        router
        class="nav-menu"
      >
        <el-menu-item index="/dashboard">
          <el-icon><Monitor /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/cards">
          <el-icon><Tickets /></el-icon>
          <span>卡牌管理</span>
        </el-menu-item>
        <el-menu-item index="/products">
          <el-icon><Box /></el-icon>
          <span>产品管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container class="right-container">
      <el-header class="header">
        <span class="title">{{ route.meta.title }}</span>
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

.sidebar {
  background: #304156;
}

.logo-area {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-text {
  color: #fff;
  font-size: 16px;
  font-weight: 600;
}

.nav-menu {
  border-right: none;
}

.el-menu {
  background: #304156;
}

.el-menu-item {
  color: #bfcbd9;
}

.el-menu-item:hover {
  background: rgba(255, 255, 255, 0.06);
  color: #fff;
}

.el-menu-item.is-active {
  background: #409EFF;
  color: #fff;
}

.right-container {
  min-height: 0;
  flex-direction: column;
  display: flex;
}

.header {
  display: flex;
  align-items: center;
  padding: 0 20px;
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
  height: 56px;
  flex-shrink: 0;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

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