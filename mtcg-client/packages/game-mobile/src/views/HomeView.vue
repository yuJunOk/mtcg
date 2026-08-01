<script setup lang="ts">
const emit = defineEmits<{
  navigate: [view: string]
}>()

const modes = [
  { key: 'quick', icon: '⚡', label: '快速匹配', desc: '随机匹配' },
  { key: 'ranked', icon: '🏆', label: '排位赛', desc: '定级 3/10' },
  { key: 'friend', icon: '🤝', label: '好友对战', desc: '切磋' },
  { key: 'ai', icon: '🤖', label: 'AI 训练', desc: '练习' },
]

const tabs = [
  { key: 'home', icon: '🏠', label: '首页' },
  { key: 'battle', icon: '⚔️', label: '对战' },
  { key: 'collection', icon: '📦', label: '收藏' },
  { key: 'deck', icon: '🃏', label: '卡组' },
  { key: 'shop', icon: '🛒', label: '商店' },
]
</script>

<template>
  <div class="home-mobile">
    <!-- 顶部 -->
    <header class="top-bar">
      <div class="logo">MTCG</div>
      <div class="resources">
        <span>🪙 1,280</span>
        <span>💎 320</span>
      </div>
    </header>

    <!-- 内容区 -->
    <main class="content">
      <!-- 问候 -->
      <div class="greeting">
        <div class="avatar">U</div>
        <div class="greeting-text">
          <div class="hi">欢迎回来</div>
          <div class="name">PlayerOne <span class="rank-badge">青铜 III</span></div>
        </div>
      </div>

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
            <span class="mode-label">{{ m.label }}</span>
            <span class="mode-desc">{{ m.desc }}</span>
          </div>
        </div>
      </section>

      <!-- 战绩 -->
      <section class="section">
        <h2 class="section-title">我的战绩</h2>
        <div class="stats-row">
          <div class="stat-item">
            <span class="stat-val accent">67</span>
            <span class="stat-lbl">胜场</span>
          </div>
          <div class="stat-item">
            <span class="stat-val gold">54%</span>
            <span class="stat-lbl">胜率</span>
          </div>
          <div class="stat-item">
            <span class="stat-val blue">12</span>
            <span class="stat-lbl">卡组</span>
          </div>
          <div class="stat-item">
            <span class="stat-val purple">248</span>
            <span class="stat-lbl">卡牌</span>
          </div>
        </div>
      </section>

      <!-- 新闻 -->
      <section class="section">
        <h2 class="section-title">最新消息</h2>
        <div class="news-item">
          <span class="news-tag" style="background: var(--accent-gold)">新卡包</span>
          <span class="news-title">「时空裂隙」现已上线</span>
        </div>
        <div class="news-item">
          <span class="news-tag" style="background: var(--accent)">赛季</span>
          <span class="news-title">S1 赛季开启</span>
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

/* ===== 顶部 ===== */
.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px var(--space-md);
  background: var(--bg-surface);
  border-bottom: 1px solid var(--border);
  flex-shrink: 0;
}

.logo {
  font-size: 20px;
  font-weight: 700;
  color: var(--accent);
  letter-spacing: 2px;
}

.resources {
  display: flex;
  gap: var(--space-sm);
  font-size: var(--font-size-sm);
  color: var(--text-primary);
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

/* ===== 问候 ===== */
.greeting {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
}

.avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: var(--accent);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 18px;
  flex-shrink: 0;
}

.hi {
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
}

.name {
  font-size: var(--font-size-md);
  font-weight: 600;
  color: var(--text-primary);
  display: flex;
  align-items: center;
  gap: var(--space-xs);
}

.rank-badge {
  font-size: 11px;
  color: var(--accent-gold);
  background: rgba(247, 143, 63, 0.15);
  padding: 1px 8px;
  border-radius: 10px;
  font-weight: 500;
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

/* ===== 模式网格 ===== */
.mode-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-sm);
}

.mode-card {
  background: var(--bg-surface);
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  padding: var(--space-md);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.mode-card:active {
  background: var(--bg-surface-2);
  border-color: var(--accent);
  box-shadow: var(--shadow-glow);
}

.mode-icon {
  font-size: 28px;
}

.mode-label {
  font-size: var(--font-size-base);
  font-weight: 600;
  color: var(--text-primary);
}

.mode-desc {
  font-size: 11px;
  color: var(--text-secondary);
}

/* ===== 战绩 ===== */
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

.stat-val.accent { color: var(--accent); }
.stat-val.gold { color: var(--accent-gold); }
.stat-val.blue { color: var(--accent-blue); }
.stat-val.purple { color: var(--accent-purple); }

.stat-lbl {
  font-size: 11px;
  color: var(--text-secondary);
}

/* ===== 新闻 ===== */
.news-item {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  padding: 10px var(--space-md);
  background: var(--bg-surface);
  border-radius: var(--radius-sm);
  border: 1px solid var(--border);
  margin-bottom: var(--space-xs);
}

.news-tag {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 10px;
  color: #fff;
  font-weight: 500;
  flex-shrink: 0;
}

.news-title {
  font-size: var(--font-size-sm);
  color: var(--text-primary);
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
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