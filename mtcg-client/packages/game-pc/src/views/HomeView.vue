<script setup lang="ts">
import { useThemeStore } from '@mtcg/common/stores'

interface ExploreModule {
  key: string
  icon: string
  title: string
  desc: string
  cta: string
}

interface PopularDeck {
  rank: number
  name: string
  format: string
  wins: number
}

interface StatItem {
  value: string
  label: string
  cssVar: string
}

const emit = defineEmits<{
  navigate: [view: string]
}>()

const theme = useThemeStore()

const exploreModules: ExploreModule[] = [
  {
    key: 'cards',
    icon: '📇',
    title: '卡牌图鉴',
    desc: '浏览全部卡牌，支持按类型、颜色、稀有度筛选',
    cta: '浏览图鉴',
  },
  {
    key: 'deck-builder',
    icon: '🃏',
    title: '卡组构筑',
    desc: '可视化卡组编辑器，支持格式校验与导入导出',
    cta: '构筑卡组',
  },
  {
    key: 'my-decks',
    icon: '📦',
    title: '我的卡组',
    desc: '管理已保存的卡组，分享或设为私密',
    cta: '查看卡组',
  },
  {
    key: 'collection',
    icon: '📚',
    title: '我的收藏',
    desc: '登记拥有的卡牌，追踪收集进度',
    cta: '查看收藏',
  },
  {
    key: 'leaderboard',
    icon: '🏆',
    title: '排行榜',
    desc: '全服排位、赛季战绩、胜率统计',
    cta: '查看排行',
  },
  {
    key: 'career',
    icon: '📈',
    title: '生涯记录',
    desc: '对局历史、成就徽章、打牌习惯分析',
    cta: '查看生涯',
  },
]

const popularDecks: PopularDeck[] = [
  { rank: 1, name: '复仇者突击', format: '标准', wins: 2456 },
  { rank: 2, name: '银河护卫队', format: '标准', wins: 1892 },
  { rank: 3, name: 'X战警控制', format: '标准', wins: 1567 },
  { rank: 4, name: '灭霸快攻', format: '扩展', wins: 1342 },
  { rank: 5, name: '蜘蛛侠联动', format: '标准', wins: 1108 },
]

const stats: StatItem[] = [
  { value: '248', label: '收集卡牌', cssVar: '--accent-red' },
  { value: '12', label: '卡组', cssVar: '--accent' },
  { value: '67', label: '胜场', cssVar: '--accent-blue' },
  { value: '54%', label: '胜率', cssVar: '--accent-green' },
]
</script>

<template>
  <div class="home-pc">
    <!-- 左侧导航 -->
    <aside class="sidebar">
      <div class="logo">
        <span class="logo-text">MTCG</span>
        <span class="logo-sub">超英集换式卡牌</span>
      </div>
      <nav class="nav-menu">
        <a class="nav-item active">
          <span class="nav-icon">🏠</span>
          <span>首页</span>
        </a>
        <a class="nav-item" @click="emit('navigate', 'battle')">
          <span class="nav-icon">⚔️</span>
          <span>对战</span>
        </a>
        <a class="nav-item">
          <span class="nav-icon">📇</span>
          <span>图鉴</span>
        </a>
        <a class="nav-item">
          <span class="nav-icon">🃏</span>
          <span>卡组</span>
        </a>
        <a class="nav-item">
          <span class="nav-icon">📚</span>
          <span>收藏</span>
        </a>
        <a class="nav-item">
          <span class="nav-icon">🏆</span>
          <span>排行</span>
        </a>
      </nav>
      <div class="user-info">
        <div class="avatar">U</div>
        <div class="user-detail">
          <div class="username">PlayerOne</div>
          <div class="rank">青铜 III</div>
        </div>
      </div>
      <div class="theme-toggle" @click="theme.toggle()">
        {{ theme.theme === 'dark' ? '☀️ 亮色' : '🌙 暗色' }}
      </div>
    </aside>

    <!-- 主内容区 -->
    <main class="main-content">
      <!-- Hero Banner -->
      <section class="hero">
        <div class="hero-content">
          <h1 class="hero-title">MTCG 超英集换式卡牌</h1>
          <p class="hero-desc">全自动规则引擎 · 漫威英雄集结 · 策略对战</p>
          <div class="hero-stats">
            <span class="hero-stat"><strong>248</strong> 张卡牌</span>
            <span class="hero-stat"><strong>12</strong> 个扩展</span>
            <span class="hero-stat"><strong>4</strong> 种赛制</span>
          </div>
          <button class="hero-cta" @click="emit('navigate', 'battle')">
            开始对战
          </button>
        </div>
      </section>

      <!-- 个人统计 -->
      <section class="stats-bar">
        <div class="stat-item" v-for="s in stats" :key="s.label">
          <span class="stat-value" :style="{ color: `var(${s.cssVar})` }">{{ s.value }}</span>
          <span class="stat-label">{{ s.label }}</span>
        </div>
      </section>

      <!-- 功能模块 Explore Grid -->
      <section class="section">
        <h2 class="section-title">探索</h2>
        <div class="explore-grid">
          <div
            v-for="m in exploreModules"
            :key="m.key"
            class="explore-card"
            @click="m.key === 'deck-builder' ? emit('navigate', 'battle') : null"
          >
            <span class="explore-icon">{{ m.icon }}</span>
            <div class="explore-info">
              <h3 class="explore-title">{{ m.title }}</h3>
              <p class="explore-desc">{{ m.desc }}</p>
            </div>
            <span class="explore-cta">{{ m.cta }} →</span>
          </div>
        </div>
      </section>

      <!-- 热门卡组 + 社区 -->
      <div class="bottom-row">
        <section class="section popular-section">
          <h2 class="section-title">热门卡组</h2>
          <div class="popular-list">
            <div
              v-for="d in popularDecks"
              :key="d.rank"
              class="popular-item"
            >
              <span class="popular-rank">#{{ d.rank }}</span>
              <div class="popular-info">
                <span class="popular-name">{{ d.name }}</span>
                <span class="popular-meta">{{ d.format }} · {{ d.wins }} 胜</span>
              </div>
            </div>
          </div>
        </section>

        <section class="section community-section">
          <h2 class="section-title">加入社区</h2>
          <p class="community-desc">与数千名玩家一起对战、交流、构筑卡组。</p>
          <div class="community-links">
            <a class="community-link">💬 Discord</a>
            <a class="community-link">📋 论坛</a>
            <a class="community-link">📖 规则书</a>
          </div>
        </section>
      </div>
    </main>
  </div>
</template>

<style scoped>
.home-pc {
  display: flex;
  height: 100vh;
  background: var(--bg-base);
}

/* ===== 侧边栏 ===== */
.sidebar {
  width: 220px;
  background: var(--bg-surface);
  border-right: 1px solid var(--border);
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}

.logo {
  padding: var(--space-lg) var(--space-md);
  border-bottom: 1px solid var(--border);
}

.logo-text {
  font-size: var(--font-size-xl);
  font-weight: 700;
  color: var(--accent);
  display: block;
  letter-spacing: 2px;
}

.logo-sub {
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
  margin-top: 2px;
}

.nav-menu {
  flex: 1;
  padding: var(--space-sm);
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  padding: 10px var(--space-md);
  border-radius: var(--radius-md);
  color: var(--text-secondary);
  font-size: var(--font-size-base);
  cursor: pointer;
  transition: all var(--transition-fast);
  text-decoration: none;
}

.nav-item:hover {
  background: var(--bg-surface-2);
  color: var(--text-primary);
}

.nav-item.active {
  background: var(--accent);
  color: #fff;
}

.nav-icon {
  font-size: 18px;
  width: 24px;
  text-align: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  padding: var(--space-md);
  border-top: 1px solid var(--border);
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--accent);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: var(--font-size-md);
}

.user-detail {
  flex: 1;
  min-width: 0;
}

.username {
  font-size: var(--font-size-sm);
  color: var(--text-primary);
  font-weight: 500;
}

.rank {
  font-size: var(--font-size-xs);
  color: var(--accent);
}

.theme-toggle {
  padding: 8px var(--space-md);
  border-top: 1px solid var(--border);
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
  cursor: pointer;
  text-align: center;
  transition: all var(--transition-fast);
  user-select: none;
}

.theme-toggle:hover {
  color: var(--accent);
  background: var(--bg-surface-2);
}

/* ===== 主内容区 ===== */
.main-content {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: var(--space-lg);
}

/* ===== Hero Banner ===== */
.hero {
  background-image:
    linear-gradient(rgba(27, 30, 43, 0.55), rgba(27, 30, 43, 0.72)),
    url('/background.png');
  background-size: cover;
  background-position: center;
  border-bottom: 1px solid var(--border);
  padding: 64px var(--space-xl) 56px;
}

/* 亮色主题: 背景图更亮，遮罩更透 */
[data-theme="light"] .hero {
  background-image:
    linear-gradient(rgba(30, 30, 48, 0.35), rgba(30, 30, 48, 0.50)),
    url('/background.png');
}

.hero-content {
  max-width: 800px;
}

.hero-title {
  font-size: 32px;
  font-weight: 700;
  color: #fff;
  margin-bottom: var(--space-sm);
  text-shadow: 0 2px 16px rgba(0, 0, 0, 0.85);
}

.hero-desc {
  font-size: var(--font-size-md);
  color: rgba(255, 255, 255, 0.88);
  margin-bottom: var(--space-md);
  text-shadow: 0 1px 8px rgba(0, 0, 0, 0.75);
}

.hero-stats {
  display: flex;
  gap: var(--space-lg);
  margin-bottom: var(--space-lg);
}

.hero-stat {
  font-size: var(--font-size-sm);
  color: rgba(255, 255, 255, 0.85);
  text-shadow: 0 1px 6px rgba(0, 0, 0, 0.7);
}

.hero-stat strong {
  color: var(--accent);
  font-size: var(--font-size-lg);
  margin-right: 4px;
  text-shadow: 0 0 14px rgba(0, 212, 170, 0.7);
}

.hero-cta {
  display: inline-block;
  padding: 12px 32px;
  background: var(--accent);
  color: #fff;
  border: none;
  border-radius: var(--radius-md);
  font-size: var(--font-size-md);
  font-weight: 600;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.hero-cta:hover {
  filter: brightness(1.1);
  box-shadow: var(--shadow-glow);
  transform: translateY(-1px);
}

/* ===== 统计条 ===== */
.stats-bar {
  display: flex;
  gap: var(--space-md);
  padding: 0 var(--space-xl);
}

.stat-item {
  flex: 1;
  background: var(--bg-surface);
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  padding: var(--space-md);
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-value {
  font-size: var(--font-size-2xl);
  font-weight: 700;
}

.stat-label {
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
}

/* ===== 区块 ===== */
.section {
  padding: 0 var(--space-xl);
}

.section-title {
  font-size: var(--font-size-lg);
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--space-md);
}

/* ===== Explore 网格 ===== */
.explore-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--space-md);
}

.explore-card {
  background: var(--bg-surface);
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  padding: var(--space-lg);
  cursor: pointer;
  transition: all var(--transition-fast);
  display: flex;
  flex-direction: column;
  gap: var(--space-sm);
}

.explore-card:hover {
  background: var(--bg-surface-2);
  border-color: var(--accent);
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.explore-icon {
  font-size: 32px;
}

.explore-title {
  font-size: var(--font-size-md);
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
}

.explore-desc {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  line-height: 1.5;
  margin: 0;
  flex: 1;
}

.explore-cta {
  font-size: var(--font-size-sm);
  color: var(--accent);
  font-weight: 500;
}

/* ===== 底部双栏 ===== */
.bottom-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-lg);
  padding: 0 var(--space-xl) var(--space-xl);
}

.popular-section {
  padding: 0;
}

.popular-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-xs);
}

.popular-item {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  padding: 10px var(--space-md);
  background: var(--bg-surface);
  border-radius: var(--radius-md);
  border: 1px solid var(--border);
  transition: background var(--transition-fast);
  cursor: pointer;
}

.popular-item:hover {
  background: var(--bg-surface-2);
}

.popular-rank {
  font-size: var(--font-size-lg);
  font-weight: 700;
  width: 40px;
  flex-shrink: 0;
  color: var(--accent);
}

.popular-info {
  display: flex;
  flex-direction: column;
}

.popular-name {
  font-size: var(--font-size-base);
  color: var(--text-primary);
  font-weight: 500;
}

.popular-meta {
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
}

/* ===== 社区 ===== */
.community-section {
  padding: 0;
}

.community-desc {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  margin-bottom: var(--space-md);
  line-height: 1.6;
}

.community-links {
  display: flex;
  gap: var(--space-sm);
}

.community-link {
  padding: 8px 16px;
  background: var(--bg-surface);
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  color: var(--text-secondary);
  font-size: var(--font-size-sm);
  cursor: pointer;
  text-decoration: none;
  transition: all var(--transition-fast);
}

.community-link:hover {
  border-color: var(--accent-blue);
  color: var(--accent-blue);
  background: var(--bg-surface-2);
}
</style>