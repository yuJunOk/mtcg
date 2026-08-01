<script setup lang="ts">
/**
 * PC 端对战视图
 * 横屏布局：对手在上，我方在下，中间是 PixiJS 画布
 */
import { ref, onMounted, onUnmounted } from 'vue'
import { useGameStore } from '@mtcg/common/stores'

const emit = defineEmits<{
  navigate: [view: string]
}>()

const store = useGameStore()
const canvasContainer = ref<HTMLDivElement>()

onMounted(() => {
  // TODO: 初始化 GameCanvas，加载对局
})

onUnmounted(() => {
  // TODO: 销毁 GameCanvas
})
</script>

<template>
  <div class="battle-pc">
    <!-- 顶部 HUD -->
    <header class="hud-top">
      <div class="hud-left">
        <button class="btn-back" @click="emit('navigate', 'home')">← 返回</button>
        <span class="phase-badge">{{ store.gameState?.currentPhase || '部署阶段' }}</span>
        <span class="turn-info">回合 {{ store.gameState?.turnCount || 1 }}</span>
      </div>
      <div class="opponent-info">
        <span class="player-name">对手: {{ store.opponent?.playerId || 'AI_Opponent' }}</span>
        <div class="timeline-bar">
          <div class="timeline-progress" :style="{ width: ((store.opponent?.timeline.length ?? 3) / 9 * 100) + '%' }"></div>
          <span class="timeline-text">{{ store.opponent?.timeline.length ?? 3 }} / 9</span>
        </div>
      </div>
    </header>

    <!-- 中间：PixiJS 游戏画布 -->
    <main class="canvas-area" ref="canvasContainer">
      <div id="game-canvas" class="game-canvas">
        <!-- 占位战区示意 -->
        <div class="battlefield-mock">
          <div class="zone-row opponent-zone">
            <div class="zone-card" v-for="i in 4" :key="'opp'+i">
              <div class="card-back">?</div>
              <span class="zone-label">{{ ['后', '侧', '侧', '前'][i-1] }}</span>
            </div>
          </div>
          <div class="timeline-mid">
            <span class="mid-label">时间线区域</span>
          </div>
          <div class="zone-row local-zone">
            <div class="zone-card" v-for="i in 4" :key="'local'+i">
              <div class="card-back">?</div>
              <span class="zone-label">{{ ['前', '侧', '侧', '后'][i-1] }}</span>
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- 底部 HUD -->
    <footer class="hud-bottom">
      <div class="hand-area">
        <span class="hand-label">手牌</span>
        <div class="hand-cards">
          <div class="hand-card" v-for="i in 5" :key="i">
            <div class="mini-card">{{ i }}</div>
          </div>
        </div>
      </div>
      <div class="action-bar">
        <div class="timeline-bar">
          <div class="timeline-progress" :style="{ width: ((store.localPlayer?.timeline.length ?? 2) / 9 * 100) + '%' }"></div>
          <span class="timeline-text">{{ store.localPlayer?.timeline.length ?? 2 }} / 9</span>
        </div>
        <button class="btn-action btn-primary">结束阶段</button>
        <button class="btn-action btn-danger">认输</button>
      </div>
    </footer>
  </div>
</template>

<style scoped>
.battle-pc {
  display: flex;
  flex-direction: column;
  height: 100vh;
  width: 100vw;
  background: var(--bg-base);
}

/* ===== 顶部 HUD ===== */
.hud-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px var(--space-md);
  background: var(--bg-surface);
  border-bottom: 1px solid var(--border);
  height: 48px;
  flex-shrink: 0;
}

.hud-left {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
}

.btn-back {
  background: transparent;
  border: 1px solid var(--border);
  color: var(--text-secondary);
  padding: 4px 10px;
  border-radius: var(--radius-sm);
  font-size: var(--font-size-xs);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.btn-back:hover {
  border-color: var(--accent);
  color: var(--accent);
}

.opponent-info {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  font-size: var(--font-size-base);
}

.phase-badge {
  background: var(--accent-blue);
  color: #fff;
  padding: 2px 10px;
  border-radius: 10px;
  font-size: var(--font-size-xs);
  font-weight: 600;
}

.turn-info {
  color: var(--text-secondary);
}

.player-name {
  color: var(--text-primary);
}

/* ===== 时间线进度条 ===== */
.timeline-bar {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  width: 200px;
  height: 20px;
  background: var(--bg-base);
  border-radius: 10px;
  overflow: hidden;
  position: relative;
  border: 1px solid var(--border);
}

.timeline-progress {
  height: 100%;
  background: linear-gradient(90deg, var(--accent-gold), var(--accent));
  border-radius: 10px;
  transition: width 0.3s ease;
}

.timeline-text {
  position: absolute;
  width: 100%;
  text-align: center;
  font-size: 11px;
  font-weight: 600;
  color: #fff;
  text-shadow: 0 0 4px rgba(0, 0, 0, 0.8);
}

/* ===== 游戏画布 ===== */
.canvas-area {
  flex: 1;
  overflow: hidden;
  position: relative;
}

.game-canvas {
  width: 100%;
  height: 100%;
}

/* ===== 战区占位示意 ===== */
.battlefield-mock {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: var(--space-md);
  gap: var(--space-md);
}

.zone-row {
  display: flex;
  gap: var(--space-md);
  justify-content: center;
  flex: 1;
}

.opponent-zone {
  align-items: flex-end;
}

.local-zone {
  align-items: flex-start;
}

.zone-card {
  width: 100px;
  height: 140px;
  background: var(--bg-surface);
  border: 2px dashed var(--border);
  border-radius: var(--radius-md);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--space-xs);
}

.card-back {
  font-size: 28px;
  color: var(--text-disabled);
  font-weight: 700;
}

.zone-label {
  font-size: 11px;
  color: var(--text-secondary);
}

.timeline-mid {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 32px;
}

.mid-label {
  font-size: var(--font-size-xs);
  color: var(--text-disabled);
  letter-spacing: 2px;
}

/* ===== 底部 HUD ===== */
.hud-bottom {
  padding: 8px var(--space-md);
  background: var(--bg-surface);
  border-top: 1px solid var(--border);
  flex-shrink: 0;
  display: flex;
  gap: var(--space-md);
  align-items: center;
}

.hand-area {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  flex: 1;
}

.hand-label {
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
  flex-shrink: 0;
}

.hand-cards {
  display: flex;
  gap: 4px;
}

.hand-card {
  width: 60px;
  height: 84px;
  background: var(--bg-surface-2);
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  transition: all var(--transition-fast);
  cursor: pointer;
}

.hand-card:hover {
  border-color: var(--accent);
  box-shadow: var(--shadow-glow);
  transform: translateY(-4px);
}

.mini-card {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  font-size: var(--font-size-xs);
  color: var(--text-disabled);
}

.action-bar {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
}

.btn-action {
  padding: 6px 20px;
  border: none;
  border-radius: var(--radius-sm);
  font-size: var(--font-size-base);
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transition-fast);
  white-space: nowrap;
}

.btn-primary {
  background: var(--accent-blue);
  color: #fff;
}

.btn-primary:hover {
  filter: brightness(1.15);
}

.btn-danger {
  background: var(--accent);
  color: #fff;
}

.btn-danger:hover {
  filter: brightness(1.15);
}
</style>