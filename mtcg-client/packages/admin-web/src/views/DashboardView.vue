<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Refresh } from '@element-plus/icons-vue'
import { dashboardApi } from '@mtcg/common/api'
import type { AuditLogVO, DashboardStatsVO } from '@mtcg/common/types'

const router = useRouter()

const stats = ref<DashboardStatsVO>({
  cardCount: 0,
  productCount: 0,
  userCount: 0,
  todayBattleCount: 0,
})
const loading = ref(false)
const initialized = ref(false)
const lastRefresh = ref<string>('')
const recentActivities = ref<Array<{ type: string; text: string; time: string; color: string }>>([])

function mapActionMeta(action: string): { label: string; color: string } {
  const key = action.toUpperCase()
  if (key.includes('CREATE') || key.includes('ADD') || key === 'LOGIN') {
    return { label: '创建', color: '#67C23A' }
  }
  if (key.includes('UPDATE') || key.includes('STATUS') || key.includes('RESET')) {
    return { label: '更新', color: '#409EFF' }
  }
  if (key.includes('DELETE') || key.includes('REMOVE')) {
    return { label: '删除', color: '#F56C6C' }
  }
  if (key.includes('LOGOUT')) {
    return { label: '登出', color: '#909399' }
  }
  return { label: action, color: '#E6A23C' }
}

function formatActivityText(log: AuditLogVO): string {
  const actor = log.actorUsercode || '系统'
  const meta = mapActionMeta(log.action)
  const resource = [log.resourceType, log.resourceId].filter(Boolean).join(' ')
  const detail = log.detail ? `：${log.detail}` : ''
  return `${actor} ${meta.label}${resource ? ` ${resource}` : ''}${detail}`
}

function mapActivities(logs: AuditLogVO[]) {
  return logs.map((log) => {
    const meta = mapActionMeta(log.action)
    return {
      type: log.action,
      text: formatActivityText(log),
      time: log.createTime,
      color: meta.color,
    }
  })
}

async function loadStats() {
  loading.value = true
  initialized.value = false
  try {
    const [statsData, activities] = await Promise.all([
      dashboardApi.getStats(),
      dashboardApi.getRecentActivities(20).catch(() => [] as AuditLogVO[]),
    ])
    stats.value = statsData
    recentActivities.value = mapActivities(activities)
    lastRefresh.value = new Date().toLocaleString('zh-CN', { hour12: false })
    initialized.value = true
  } catch {
    stats.value = { cardCount: 0, productCount: 0, userCount: 0, todayBattleCount: 0 }
    recentActivities.value = []
  } finally {
    loading.value = false
  }
}

const statCards = computed(() => [
  { key: 'cardCount' as const, label: '卡牌总数', icon: 'Picture', color: '#409EFF', route: '/cards' },
  { key: 'productCount' as const, label: '产品总数', icon: 'Box', color: '#67C23A', route: '/products' },
  { key: 'userCount' as const, label: '用户总数', icon: 'User', color: '#E6A23C', route: '/system/users' },
  { key: 'todayBattleCount' as const, label: '今日对局', icon: 'Promotion', color: '#F56C6C', route: '#' },
])

const quickActions = computed(() => [
  { label: '新增卡牌', icon: 'Plus', route: '/cards', color: '#409EFF' },
  { label: '新增产品', icon: 'Plus', route: '/products', color: '#67C23A' },
  { label: '用户管理', icon: 'User', route: '/system/users', color: '#E6A23C' },
])

const systemStatus = computed(() => ({
  backend: {
    status: lastRefresh.value ? 'running' : 'idle',
    label: loading.value ? '检查中' : (lastRefresh.value ? '运行中' : '未连接'),
    hint: lastRefresh.value ? `正常运行 · ${lastRefresh.value}` : (loading.value ? '正在与后端通信...' : '无法连接到后端服务'),
  },
  database: {
    status: lastRefresh.value ? 'connected' : 'idle',
    label: lastRefresh.value ? '已连通' : '未验证',
    hint: lastRefresh.value ? '数据读取正常' : '等待验证...',
  },
}))

onMounted(() => {
  loadStats()
})
</script>

<template>
  <div class="dashboard">
    <!-- 顶部：页面标题 + 刷新 + 统计卡片 -->
    <div class="header-section">
      <div class="header-left">
        <div class="greeting">
          <span class="wave">Hi</span>
          <span>欢迎回来</span>
        </div>
        <h1 class="page-title">仪表盘概览</h1>
      </div>
      <div class="header-right">
        <span v-if="lastRefresh" class="last-update">
          <el-icon :size="12"><Clock /></el-icon>
          {{ lastRefresh }}
        </span>
        <el-button type="primary" :icon="Refresh" @click="loadStats" :loading="loading" round>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stats-row">
      <el-col v-for="s in statCards" :key="s.key" :xs="24" :sm="12" :md="12" :lg="6">
        <!-- 骨架屏 -->
        <div v-if="!initialized" class="stat-card skeleton">
          <div class="skeleton-icon" />
          <div class="skeleton-content">
            <div class="skeleton-value" />
            <div class="skeleton-label" />
          </div>
        </div>
        <!-- 实际内容 -->
        <div
          v-else
          class="stat-card"
          :class="{ 'is-loading': loading }"
          @click="s.route !== '#' && router.push(s.route)"
        >
          <div class="stat-icon-wrap" :style="{ background: s.color + '15' }">
            <el-icon :size="28" :style="{ color: s.color }">
              <component :is="s.icon" />
            </el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value" :style="{ color: s.color }">
              {{ stats?.[s.key] ?? 0 }}
            </div>
            <div class="stat-label">{{ s.label }}</div>
          </div>
          <div class="stat-arrow">
            <el-icon :size="16"><ArrowRight /></el-icon>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 第二行：快速操作 + 系统状态 -->
    <el-row :gutter="16" class="middle-row">
      <el-col :xs="24" :md="14">
        <el-card v-if="initialized" class="panel-card" shadow="never" v-loading="loading" element-loading-background="rgba(255,255,255,0.8)">
          <template #header>
            <div class="panel-header">
              <div class="panel-title">
                <el-icon :size="18" color="#409EFF"><Menu /></el-icon>
                <span>快速操作</span>
              </div>
              <span class="panel-desc">常用功能一键直达</span>
            </div>
          </template>
          <div class="action-grid">
            <div
              v-for="action in quickActions"
              :key="action.label"
              class="action-item"
              @click="router.push(action.route)"
              :style="{ '--accent-color': action.color }"
            >
              <div class="action-icon">
                <el-icon :size="24"><component :is="action.icon" /></el-icon>
              </div>
              <div class="action-label">{{ action.label }}</div>
            </div>
          </div>
        </el-card>
        <!-- 骨架屏 -->
        <div v-else class="panel-card skeleton-panel">
          <div class="skeleton-panel-header" />
          <div class="skeleton-action-grid">
            <div v-for="i in 3" :key="i" class="skeleton-action-item">
              <div class="skeleton-action-icon" />
              <div class="skeleton-action-label" />
            </div>
          </div>
        </div>
      </el-col>

      <el-col :xs="24" :md="10">
        <el-card v-if="initialized" class="panel-card" shadow="never" v-loading="loading" element-loading-background="rgba(255,255,255,0.8)">
          <template #header>
            <div class="panel-header">
              <div class="panel-title">
                <el-icon :size="18" color="#67C23A"><CircleCheck /></el-icon>
                <span>系统状态</span>
              </div>
              <span class="panel-desc">服务运行概况</span>
            </div>
          </template>
          <div class="status-list">
            <div class="status-item">
              <div class="status-left">
                <div class="status-dot" :class="systemStatus.backend.status" />
                <span class="status-name">后端服务</span>
              </div>
              <div class="status-right">
                <el-tag
                  :type="systemStatus.backend.status === 'running' ? 'success' : 'info'"
                  size="small"
                  effect="light"
                  round
                >
                  {{ systemStatus.backend.label }}
                </el-tag>
              </div>
            </div>
            <div class="status-item">
              <div class="status-left">
                <div class="status-dot" :class="systemStatus.database.status" />
                <span class="status-name">数据库</span>
              </div>
              <div class="status-right">
                <el-tag
                  :type="systemStatus.database.status === 'connected' ? 'success' : 'info'"
                  size="small"
                  effect="light"
                  round
                >
                  {{ systemStatus.database.label }}
                </el-tag>
              </div>
            </div>
            <div class="status-hint-row">
              <span class="status-hint">{{ systemStatus.backend.hint }}</span>
            </div>
          </div>
        </el-card>
        <!-- 骨架屏 -->
        <div v-else class="panel-card skeleton-panel">
          <div class="skeleton-panel-header" />
          <div class="skeleton-status-list">
            <div v-for="i in 2" :key="i" class="skeleton-status-item">
              <div class="skeleton-status-dot" />
              <div class="skeleton-status-name" />
              <div class="skeleton-status-tag" />
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 第三行：最近活动 -->
    <el-card v-if="initialized" class="panel-card activity-card" shadow="never" v-loading="loading" element-loading-background="rgba(255,255,255,0.8)">
      <template #header>
        <div class="panel-header">
          <div class="panel-title">
            <el-icon :size="18" color="#E6A23C"><Clock /></el-icon>
            <span>最近活动</span>
          </div>
          <span class="panel-desc">系统中的最新动态</span>
        </div>
      </template>
      <div v-if="recentActivities.length" class="activity-list">
        <div v-for="(item, index) in recentActivities" :key="index" class="activity-item">
          <div class="activity-dot" :style="{ background: item.color }" />
          <div class="activity-content">
            <div class="activity-text">{{ item.text }}</div>
            <div class="activity-time">{{ item.time }}</div>
          </div>
          <el-icon class="activity-arrow" :size="14"><ArrowRight /></el-icon>
        </div>
      </div>
      <el-empty v-else description="暂无最近活动" :image-size="72" />
    </el-card>
    <!-- 骨架屏 -->
    <div v-else class="panel-card activity-card skeleton-panel">
      <div class="skeleton-panel-header" />
      <div class="skeleton-activity-list">
        <div v-for="i in 4" :key="i" class="skeleton-activity-item">
          <div class="skeleton-activity-dot" />
          <div class="skeleton-activity-content">
            <div class="skeleton-activity-text" />
            <div class="skeleton-activity-time" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.dashboard {
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* ========== 顶部区域 ========== */
.header-section {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 20px;
  background: #fff;
  border-radius: 12px;
  border: 1px solid #eef0f3;
  flex-shrink: 0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.greeting {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #7a8499;
}

.wave {
  display: inline-block;
  animation: wave 2s ease-in-out infinite;
  transform-origin: 70% 70%;
}

@keyframes wave {
  0%, 100% { transform: rotate(0); }
  25% { transform: rotate(20deg); }
  75% { transform: rotate(-10deg); }
}

.page-title {
  font-size: 18px;
  font-weight: 700;
  color: #1a2a3a;
  margin: 0;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.last-update {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #909399;
}

/* ========== 统计卡片 ========== */
.stats-row {
  flex-shrink: 0;
}

.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  cursor: pointer;
  transition: all 0.25s ease;
  border: 1px solid #eef0f3;
  height: 100%;
  position: relative;
  overflow: hidden;
}

.stat-card::after {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.3), transparent);
  transition: left 0.5s ease;
}

.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  border-color: #dce4ee;
}

.stat-card:hover::after {
  left: 100%;
}

.stat-card.is-loading {
  pointer-events: none;
  opacity: 0.6;
}

.stat-icon-wrap {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: transform 0.25s ease;
}

.stat-card:hover .stat-icon-wrap {
  transform: scale(1.1);
}

.stat-content {
  flex: 1;
  min-width: 0;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  line-height: 1.1;
  transition: color 0.25s ease;
}

.stat-label {
  font-size: 13px;
  color: #7a8499;
  margin-top: 4px;
}

.stat-arrow {
  color: #c0c4cc;
  opacity: 0;
  transform: translateX(-4px);
  transition: all 0.25s ease;
}

.stat-card:hover .stat-arrow {
  opacity: 1;
  transform: translateX(0);
  color: #409EFF;
}

/* ========== 骨架屏 ========== */
.skeleton {
  cursor: default;
  animation: none;
  border: 1px solid #eef0f3;
}

.skeleton:hover {
  transform: none;
  box-shadow: none;
}

.skeleton::after {
  display: none;
}

.skeleton-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  background: linear-gradient(90deg, #f0f2f5 25%, #e8eaed 50%, #f0f2f5 75%);
  background-size: 200% 100%;
  animation: skeleton-pulse 1.5s ease-in-out infinite;
  flex-shrink: 0;
}

.skeleton-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.skeleton-value {
  height: 32px;
  width: 80px;
  border-radius: 8px;
  background: linear-gradient(90deg, #f0f2f5 25%, #e8eaed 50%, #f0f2f5 75%);
  background-size: 200% 100%;
  animation: skeleton-pulse 1.5s ease-in-out infinite;
}

.skeleton-label {
  height: 16px;
  width: 60px;
  border-radius: 4px;
  background: linear-gradient(90deg, #f0f2f5 25%, #e8eaed 50%, #f0f2f5 75%);
  background-size: 200% 100%;
  animation: skeleton-pulse 1.5s ease-in-out infinite;
}

@keyframes skeleton-pulse {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

/* ========== 面板卡片 ========== */
.middle-row {
  flex-shrink: 0;
}

.panel-card {
  border-radius: 12px;
  border: 1px solid #eef0f3;
  height: 100%;
}

.panel-card :deep(.el-card__header) {
  padding: 16px 20px;
  border-bottom: 1px solid #f0f2f5;
}

.panel-card :deep(.el-card__body) {
  padding: 20px;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.panel-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 600;
  color: #1a2a3a;
}

.panel-desc {
  font-size: 12px;
  color: #909399;
}

/* ========== 骨架面板 ========== */
.skeleton-panel {
  border-radius: 12px;
  border: 1px solid #eef0f3;
  background: #fff;
  padding: 16px 20px;
  min-height: 180px;
}

.skeleton-panel-header {
  height: 24px;
  width: 140px;
  border-radius: 8px;
  background: linear-gradient(90deg, #f0f2f5 25%, #e8eaed 50%, #f0f2f5 75%);
  background-size: 200% 100%;
  animation: skeleton-pulse 1.5s ease-in-out infinite;
  margin-bottom: 16px;
}

.skeleton-action-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.skeleton-action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 20px 12px;
  border-radius: 10px;
  background: #f8fafb;
}

.skeleton-action-icon {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  background: linear-gradient(90deg, #f0f2f5 25%, #e8eaed 50%, #f0f2f5 75%);
  background-size: 200% 100%;
  animation: skeleton-pulse 1.5s ease-in-out infinite;
}

.skeleton-action-label {
  height: 14px;
  width: 60px;
  border-radius: 4px;
  background: linear-gradient(90deg, #f0f2f5 25%, #e8eaed 50%, #f0f2f5 75%);
  background-size: 200% 100%;
  animation: skeleton-pulse 1.5s ease-in-out infinite;
}

.skeleton-status-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.skeleton-status-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.skeleton-status-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: linear-gradient(90deg, #f0f2f5 25%, #e8eaed 50%, #f0f2f5 75%);
  background-size: 200% 100%;
  animation: skeleton-pulse 1.5s ease-in-out infinite;
}

.skeleton-status-name {
  height: 16px;
  width: 80px;
  border-radius: 4px;
  background: linear-gradient(90deg, #f0f2f5 25%, #e8eaed 50%, #f0f2f5 75%);
  background-size: 200% 100%;
  animation: skeleton-pulse 1.5s ease-in-out infinite;
  flex: 1;
}

.skeleton-status-tag {
  height: 20px;
  width: 60px;
  border-radius: 10px;
  background: linear-gradient(90deg, #f0f2f5 25%, #e8eaed 50%, #f0f2f5 75%);
  background-size: 200% 100%;
  animation: skeleton-pulse 1.5s ease-in-out infinite;
}

.skeleton-activity-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.skeleton-activity-item {
  display: flex;
  align-items: center;
  gap: 14px;
}

.skeleton-activity-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: linear-gradient(90deg, #f0f2f5 25%, #e8eaed 50%, #f0f2f5 75%);
  background-size: 200% 100%;
  animation: skeleton-pulse 1.5s ease-in-out infinite;
  flex-shrink: 0;
}

.skeleton-activity-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.skeleton-activity-text {
  height: 14px;
  width: 200px;
  border-radius: 4px;
  background: linear-gradient(90deg, #f0f2f5 25%, #e8eaed 50%, #f0f2f5 75%);
  background-size: 200% 100%;
  animation: skeleton-pulse 1.5s ease-in-out infinite;
}

.skeleton-activity-time {
  height: 12px;
  width: 100px;
  border-radius: 4px;
  background: linear-gradient(90deg, #f0f2f5 25%, #e8eaed 50%, #f0f2f5 75%);
  background-size: 200% 100%;
  animation: skeleton-pulse 1.5s ease-in-out infinite;
}

/* ========== 快速操作 ========== */
.action-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 20px 12px;
  border-radius: 10px;
  background: #f8fafb;
  cursor: pointer;
  transition: all 0.25s ease;
  border: 1px solid transparent;
}

.action-item:hover {
  background: var(--accent-color, #409EFF);
  color: #fff;
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
}

.action-item:hover .action-label {
  color: #fff;
}

.action-item:hover .action-icon {
  background: rgba(255, 255, 255, 0.25);
  transform: scale(1.05);
}

.action-icon {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--accent-color, #409EFF);
  color: #fff;
  transition: all 0.25s ease;
}

.action-label {
  font-size: 13px;
  font-weight: 500;
  color: #303133;
  transition: color 0.25s ease;
}

/* ========== 系统状态 ========== */
.status-list {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.status-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 0;
  border-bottom: 1px solid #f5f5f5;
}

.status-item:last-child {
  border-bottom: none;
}

.status-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #dcdfe6;
  transition: all 0.3s ease;
}

.status-dot.running,
.status-dot.connected {
  background: #67C23A;
  box-shadow: 0 0 0 3px rgba(103, 194, 58, 0.2);
  animation: pulse 2s infinite;
}

.status-dot.idle {
  background: #909399;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.6;
    transform: scale(0.9);
  }
}

.status-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.status-hint-row {
  padding-top: 12px;
  border-top: 1px solid #f5f5f5;
  margin-top: 4px;
}

.status-hint {
  font-size: 12px;
  color: #909399;
}

/* ========== 最近活动 ========== */
.activity-card {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.activity-card :deep(.el-card__body) {
  flex: 1;
  overflow-y: auto;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 12px;
  border-radius: 8px;
  transition: all 0.2s ease;
  cursor: pointer;
}

.activity-item:hover {
  background: #f8fafb;
  transform: translateX(4px);
}

.activity-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;
  transition: transform 0.2s ease;
}

.activity-item:hover .activity-dot {
  transform: scale(1.3);
}

.activity-content {
  flex: 1;
  min-width: 0;
}

.activity-text {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.activity-time {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}

.activity-arrow {
  color: #c0c4cc;
  transition: all 0.2s ease;
}

.activity-item:hover .activity-arrow {
  color: #409EFF;
  transform: translateX(4px);
}

/* ========== 响应式 ========== */
@media (max-width: 1200px) {
  .action-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .header-section {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
  .header-left {
    flex-wrap: wrap;
  }
}
</style>
