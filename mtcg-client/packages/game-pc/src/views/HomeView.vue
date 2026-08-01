<script setup lang="ts">
import { ref } from 'vue'

const emit = defineEmits<{
  navigate: [view: string]
}>()

const modes = [
  { key: 'quick', icon: '⚡', label: '快速匹配', desc: '随机匹配对手' },
  { key: 'ranked', icon: '🏆', label: '排位赛', desc: '青铜 · 进行 3/10 定级赛' },
  { key: 'friend', icon: '🤝', label: '好友对战', desc: '与好友切磋' },
  { key: 'ai', icon: '🤖', label: 'AI 训练', desc: '练习模式' },
]

const news = [
  { title: '新卡包「时空裂隙」现已上线', date: '08-01', tag: '新卡包', color: 'var(--accent-gold)' },
  { title: '排位赛 S1 赛季开启', date: '07-28', tag: '赛季', color: 'var(--accent)' },
  { title: '平衡性调整公告 v1.0.2', date: '07-25', tag: '公告', color: 'var(--accent-blue)' },
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
          <span class="nav-icon">📦</span>
          <span>我的收藏</span>
        </a>
        <a class="nav-item">
          <span class="nav-icon">🃏</span>
          <span>卡组构筑</span>
        </a>
        <a class="nav-item">
          <span class="nav-icon">🛒</span>
          <span>商店</span>
        </a>
      </nav>
      <div class="user-info">
        <div class="avatar">U</div>
        <div class="user-detail">
          <div class="username">PlayerOne</div>
          <div class="rank">青铜 III</div>
        </div>
      </div>
    </aside>

    <!-- 主内容区 -->
    <main class="main-content">
      <!-- 顶部 -->
      <header class="top-bar">
        <div class="greeting">欢迎回来，PlayerOne</div>
        <div class="resources">
          <span class="resource">🪙 1,280</span>
          <span class="resource">💎 320</span>
        </div>
      </header>

      <!-- 对战模式 -->
      <section class="section">
        <h2 class="section-title">选择模式</h2>
        <div class="mode-grid">
          <div
            v-for="m in modes"
            :key="m.key"
            class="mode-card"
            @click="emit('navigate', 'battle')"
          >
            <span class="mode-icon">{{ m.icon }}</span>
            <div class="mode-info">
              <div class="mode-label">{{ m.label }}</div>
              <div class="mode-desc">{{ m.desc }}</div>
            </div>
          </div>
        </div>
      </section>

      <!-- 新闻 + 统计 -->
      <div class="bottom-row">
        <section class="section news-section">
          <h2 class="section-title">最新消息</h2>
          <div class="news-list">
            <div v-for="n in news" :key="n.title" class="news-item">
              <span class="news-tag" :style="{ background: n.color }">{{ n.tag }}</span>
              <span class="news-title">{{ n.title }}</span>
              <span class="news-date">{{ n.date }}</span>
            </div>
          </div>
        </section>

        <section class="section stats-section">
          <h2 class="section-title">我的战绩</h2>
          <div class="stats-grid">
            <div class="stat-card">
              <div class="stat-value accent">67</div>
              <div class="stat-label">总胜场</div>
            </div>
            <div class="stat-card">
              <div class="stat-value gold">54%</div>
              <div class="stat-label">胜率</div>
            </div>
            <div class="stat-card">
              <div class="stat-value blue">12</div>
              <div class="stat-label">收藏卡组</div>
            </div>
            <div class="stat-card">
              <div class="stat-value purple">248</div>
              <div class="stat-label">收集卡牌</div>
            </div>
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
  color: var(--accent-gold);
}

/* ===== 主内容区 ===== */
.main-content {
  flex: 1;
  overflow-y: auto;
  padding: var(--space-lg);
  display: flex;
  flex-direction: column;
  gap: var(--space-lg);
}

.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.greeting {
  font-size: var(--font-size-lg);
  font-weight: 600;
  color: var(--text-primary);
}

.resources {
  display: flex;
  gap: var(--space-md);
}

.resource {
  background: var(--bg-surface);
  padding: 6px 14px;
  border-radius: var(--radius-sm);
  font-size: var(--font-size-sm);
  color: var(--text-primary);
  border: 1px solid var(--border);
}

/* ===== 区块 ===== */
.section {
  flex-shrink: 0;
}

.section-title {
  font-size: var(--font-size-md);
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--space-sm);
}

/* ===== 模式卡片 ===== */
.mode-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--space-md);
}

.mode-card {
  background: var(--bg-surface);
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  padding: var(--space-lg);
  cursor: pointer;
  transition: all var(--transition-fast);
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: var(--space-sm);
}

.mode-card:hover {
  background: var(--bg-surface-2);
  border-color: var(--accent);
  box-shadow: var(--shadow-glow);
  transform: translateY(-2px);
}

.mode-icon {
  font-size: 36px;
}

.mode-label {
  font-size: var(--font-size-md);
  font-weight: 600;
  color: var(--text-primary);
}

.mode-desc {
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
}

/* ===== 底部双栏 ===== */
.bottom-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-lg);
  flex: 1;
  min-height: 0;
}

.news-section {
  min-width: 0;
}

.news-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-xs);
}

.news-item {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  padding: 10px var(--space-md);
  background: var(--bg-surface);
  border-radius: var(--radius-sm);
  border: 1px solid var(--border);
  transition: background var(--transition-fast);
}

.news-item:hover {
  background: var(--bg-surface-2);
}

.news-tag {
  font-size: var(--font-size-xs);
  padding: 2px 8px;
  border-radius: 10px;
  color: #fff;
  font-weight: 500;
  flex-shrink: 0;
}

.news-title {
  flex: 1;
  font-size: var(--font-size-sm);
  color: var(--text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.news-date {
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
  flex-shrink: 0;
}

/* ===== 统计卡片 ===== */
.stats-section {
  min-width: 0;
}

.stats-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-sm);
}

.stat-card {
  background: var(--bg-surface);
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  padding: var(--space-md);
  text-align: center;
}

.stat-value {
  font-size: var(--font-size-2xl);
  font-weight: 700;
}

.stat-value.accent { color: var(--accent); }
.stat-value.gold { color: var(--accent-gold); }
.stat-value.blue { color: var(--accent-blue); }
.stat-value.purple { color: var(--accent-purple); }

.stat-label {
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
  margin-top: 4px;
}
</style>