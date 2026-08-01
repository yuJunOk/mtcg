<script setup lang="ts">
import { useThemeStore } from '@mtcg/common/stores'

const emit = defineEmits<{
  navigate: [view: string]
}>()

const theme = useThemeStore()

const exploreModules = [
  { key: 'cards', icon: '📇', title: '卡牌图鉴', desc: '浏览全部卡牌' },
  { key: 'deck-builder', icon: '🃏', title: '卡组构筑', desc: '可视化编辑器' },
  { key: 'my-decks', icon: '📦', title: '我的卡组', desc: '管理已保存卡组' },
  { key: 'collection', icon: '📚', title: '我的收藏', desc: '追踪收集进度' },
  { key: 'leaderboard', icon: '🏆', title: '排行榜', desc: '全服排位' },
  { key: 'career', icon: '📈', title: '生涯记录', desc: '对局历史' },
]

const popularDecks = [
  { rank: 1, name: '复仇者突击', wins: 2456 },
  { rank: 2, name: '银河护卫队', wins: 1892 },
  { rank: 3, name: 'X战警控制', wins: 1567 },
]

const tabs = [
  { key: 'home', icon: '🏠', label: '首页' },
  { key: 'battle', icon: '⚔️', label: '对战' },
  { key: 'decks', icon: '🃏', label: '卡组' },
  { key: 'cards', icon: '📇', label: '图鉴' },
  { key: 'more', icon: '⋯', label: '更多' },
]
</script>

<template>
  <div class="home-mobile">
    <!-- Hero Banner -->
    <header class="hero">
      <div class="hero-top">
        <span class="logo">MTCG</span>
        <div class="hero-resources">
          <span class="theme-btn" @click="theme.toggle()">
            {{ theme.theme === 'dark' ? '☀️' : '🌙' }}
          </span>
          <span>🪙 1,280</span>
          <span>💎 320</span>
        </div>
      </div>
      <h1 class="hero-title">超英集换式卡牌</h1>
      <p class="hero-desc">全自动规则引擎 · 漫威英雄集结</p>
      <div class="hero-stats">
        <span><strong>248</strong> 卡牌</span>
        <span><strong>12</strong> 扩展</span>
        <span><strong>4</strong> 赛制</span>
      </div>
      <button class="hero-cta" @click="emit('navigate', 'battle')">
        开始对战
      </button>
    </header>

    <!-- 内容区 -->
    <main class="content">
      <!-- 个人统计 -->
      <div class="stats-row">
        <div class="stat-item">
          <span class="stat-val" style="color: var(--accent)">248</span>
          <span class="stat-lbl">收集卡牌</span>
        </div>
        <div class="stat-item">
          <span class="stat-val" style="color: var(--accent-purple)">12</span>
          <span class="stat-lbl">卡组</span>
        </div>
        <div class="stat-item">
          <span class="stat-val" style="color: var(--accent-blue)">67</span>
          <span class="stat-lbl">胜场</span>
        </div>
        <div class="stat-item">
          <span class="stat-val" style="color: var(--accent-green)">54%</span>
          <span class="stat-lbl">胜率</span>
        </div>
      </div>

      <!-- 探索 -->
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
            <span class="explore-title">{{ m.title }}</span>
            <span class="explore-desc">{{ m.desc }}</span>
          </div>
        </div>
      </section>

      <!-- 热门卡组 -->
      <section class="section">
        <h2 class="section-title">热门卡组</h2>
        <div class="popular-list">
          <div v-for="d in popularDecks" :key="d.rank" class="popular-item">
            <span class="popular-rank">#{{ d.rank }}</span>
            <span class="popular-name">{{ d.name }}</span>
            <span class="popular-meta">{{ d.wins }} 胜</span>
          </div>
        </div>
      </section>
    </main>

    <!-- 底部导航 -->
    <nav class="bottom-nav">
      <a
        v-for="t in tabs"
        :key="t.key"
        class="nav-item"
        :class="{ active: t.key === 'home' }"
        @click="t.key === 'battle' ? emit('navigate', 'battle') : null"
      >
        <span class="nav-icon">{{ t.icon }}</span>
        <span class="nav-label">{{ t.label }}</span>
      </a>
    </nav>
  </div>
</template>

<style scoped>
.home-mobile {
  display: flex;
  flex-direction: column;
  height: 100dvh;
  background: var(--bg-base);
}

/* ===== Hero Banner ===== */
.hero {
  background: linear-gradient(135deg, var(--bg-surface) 0%, var(--bg-surface-2) 50%, var(--bg-surface) 100%);
  padding: var(--space-md) var(--space-md) var(--space-lg);
  flex-shrink: 0;
}

.hero-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-md);
}

.logo {
  font-size: 20px;
  font-weight: 700;
  color: var(--accent);
  letter-spacing: 2px;
}

.hero-resources {
  display: flex;
  gap: var(--space-sm);
  font-size: var(--font-size-sm);
  color: var(--text-primary);
  align-items: center;
}

.theme-btn {
  cursor: pointer;
  font-size: 16px;
  padding: 2px 4px;
  border-radius: var(--radius-sm);
  transition: background var(--transition-fast);
}

.theme-btn:active {
  background: var(--bg-surface-2);
}

.hero-title {
  font-size: 22px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.hero-desc {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  margin-bottom: var(--space-sm);
}

.hero-stats {
  display: flex;
  gap: var(--space-md);
  margin-bottom: var(--space-md);
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
}

.hero-stats strong {
  color: var(--accent);
  font-size: var(--font-size-base);
  margin-right: 2px;
}

.hero-cta {
  width: 100%;
  padding: 12px 0;
  background: var(--accent);
  color: #fff;
  border: none;
  border-radius: var(--radius-md);
  font-size: var(--font-size-md);
  font-weight: 600;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.hero-cta:active {
  filter: brightness(1.15);
  box-shadow: var(--shadow-glow);
}

/* ===== 内容 ===== */
.content {
  flex: 1;
  overflow-y: auto;
  padding: var(--space-md);
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
  -webkit-overflow-scrolling: touch;
}

/* ===== 统计 ===== */
.stats-row {
  display: flex;
  gap: var(--space-sm);
}

.stat-item {
  flex: 1;
  background: var(--bg-surface);
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  padding: var(--space-sm);
  text-align: center;
  display: flex;
  flex-direction: column;
}

.stat-val {
  font-size: 22px;
  font-weight: 700;
}

.stat-lbl {
  font-size: 11px;
  color: var(--text-secondary);
}

/* ===== 区块 ===== */
.section {
  flex-shrink: 0;
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--space-sm);
}

/* ===== Explore 网格 ===== */
.explore-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-sm);
}

.explore-card {
  background: var(--bg-surface);
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  padding: var(--space-md);
  display: flex;
  flex-direction: column;
  gap: 2px;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.explore-card:active {
  background: var(--bg-surface-2);
  border-color: var(--accent);
  box-shadow: var(--shadow-glow);
}

.explore-icon {
  font-size: 24px;
}

.explore-title {
  font-size: var(--font-size-sm);
  font-weight: 600;
  color: var(--text-primary);
}

.explore-desc {
  font-size: 11px;
  color: var(--text-secondary);
}

/* ===== 热门卡组 ===== */
.popular-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-xs);
}

.popular-item {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  padding: 10px var(--space-md);
  background: var(--bg-surface);
  border-radius: var(--radius-md);
  border: 1px solid var(--border);
  cursor: pointer;
}

.popular-item:active {
  background: var(--bg-surface-2);
}

.popular-rank {
  font-size: var(--font-size-md);
  font-weight: 700;
  width: 32px;
  flex-shrink: 0;
  color: var(--accent);
}

.popular-name {
  font-size: var(--font-size-sm);
  color: var(--text-primary);
  font-weight: 500;
}

.popular-meta {
  font-size: 11px;
  color: var(--text-secondary);
  margin-left: auto;
}

/* ===== 底部导航 ===== */
.bottom-nav {
  display: flex;
  background: var(--bg-surface);
  border-top: 1px solid var(--border);
  padding: 6px 0;
  padding-bottom: max(6px, env(safe-area-inset-bottom));
  flex-shrink: 0;
}

.nav-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  padding: 4px 0;
  color: var(--text-secondary);
  font-size: 10px;
  cursor: pointer;
  transition: color var(--transition-fast);
}

.nav-item.active {
  color: var(--accent);
}

.nav-icon {
  font-size: 20px;
}
</style>