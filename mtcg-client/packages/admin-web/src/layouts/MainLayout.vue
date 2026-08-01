<script setup lang="ts">
import { useRoute } from 'vue-router'

const route = useRoute()
</script>

<template>
  <el-container class="layout">
    <el-aside width="200px">
      <div class="logo">MTCG 管理后台</div>
      <el-menu :default-active="route.path" router>
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

<style scoped>
.layout {
  height: 100vh;
}

.right-container {
  min-height: 0;
  flex-direction: column;
  display: flex;
}

.el-aside {
  background: #1a1a2e;
  border-right: 1px solid rgba(255, 255, 255, 0.08);
}

.logo {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #e0e0e0;
  font-size: 16px;
  font-weight: bold;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.el-menu {
  border-right: none;
  background: #1a1a2e;
}

.el-menu-item {
  color: #a0a0a0;
}

.el-menu-item:hover {
  background: rgba(255, 255, 255, 0.06);
  color: #e0e0e0;
}

.el-menu-item.is-active {
  background: #4a90d9;
  color: #fff;
}

.header {
  display: flex;
  align-items: center;
  padding: 0 20px;
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
  height: 56px;
}

.title {
  font-size: 16px;
  font-weight: 500;
}

.el-main {
  background: #f5f5f5;
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