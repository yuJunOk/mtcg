<script setup lang="ts">
/**
 * 移动端对战视图
 * 竖屏布局：顶部对手信息，中间 PixiJS 画布，底部操作区
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
  <div class="battle-mobile">
    <!-- 顶部：对手信息 + 时间线 -->
    <header class="hud-header">
      <div class="header-row">
        <button class="btn-back" @click="emit('navigate', 'home')">←</button>
        <span class="phase-badge">{{ store.gameState?.currentPhase || '部署阶段' }}</span>
        <span class="turn-info">回合 {{ store.gameState?.turnCount || 1 }}</span>
        <span class="player-name">{{ store.opponent?.playerId || 'AI_Opponent' }}</span>
      </div>
      <div class="timeline-mini">
        <div class="timeline-fill" :style="{ width: ((store.opponent?.timeline.length ?? 3) / 9 * 100) + '%' }"></div>
        <span>{{ store.opponent?.timeline.length ?? 3 }}/9</span>
      </div>
    </header>

    <!-- 中间：PixiJS 画布 -->
    <main class="canvas-area" ref="canvasContainer">
      <div id="game-canvas" class="game-canvas">
        <!-- 战区占位示意 -->
        <div class="battlefield-mock">
          <div class="zone-row">
            <div class="zone-card" v-for="i in 4" :key="'opp'+i">
              <span class="zone-label">{{ ['后', '侧', '侧', '前'][i-1] }}</span>
            </div>
          </div>
          <div class="mid-divider">时间线</div>
          <div class="zone-row">
            <div class="zone-card" v-for="i in 4" :key="'local'+i">
              <span class="zone-label">{{ ['前', '侧', '侧', '后'][i-1] }}</span>
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- 底部：手牌 + 操作 -->
    <footer class="hud-footer">
      <div class="hand-scroll">
        <div class="hand-card" v-for="i in 5" :key="i">{{ i }}</div>
      </div>
      <div class="bottom-row">
        <div class="timeline-mini">
          <div class="timeline-fill" :style="{ width: ((store.localPlayer?.timeline.length ?? 2) / 9 * 100) + '%' }"></div>
          <span>{{ store.localPlayer?.timeline.length ?? 2 }}/9</span>
        </div>
        <div class="action-row">
          <button class="btn-action btn-primary">结束阶段</button>
          <button class="btn-action btn-danger">认输</button>
        </div>
      </div>
    </footer>
  </div>
</template>

<style scoped>
.battle-mobile {
  display: flex;
  flex-direction: column;
  height: 100dvh;
  width: 100vw;
  background: var(--bg-base);
}

/* ===== 顶部 HUD ===== */
.hud-header {
  padding: 8px var(--space-sm);
  background: var(--bg-surface);
  border-bottom: 1px solid var(--border);
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.header-row {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  font-size: 13px;
}

.btn-back {
  background: transparent;
  border: 1px solid var(--border);
  color: var(--text-secondary);
  padding: 2px 8px;
  border-radius: var(--radius-sm);
  font-size: 14px;
  cursor: pointer;
  line-height: 1;
}

.btn-back:active {
  border-color: var(--accent);
  color: var(--accent);
}

.phase-badge {
  background: var(--accent-blue);
  color: #fff;
  padding: 2px 8px;
  border-radius: 8px;
  font-size: 11px;
  font-weight: 600;
}

.turn-info {
  color: var(--text-secondary);
  font-size: var(--font-size-xs);
}

.player-name {
  color: var(--text-primary);
  font-size: 13px;
  margin-left: auto;
}

/* ===== 时间线 ===== */
.timeline-mini {
  display: flex;
  align-items: center;
  height: 16px;
  background: var(--bg-base);
  border-radius: 8px;
  overflow: hidden;
  position: relative;
  border: 1px solid var(--border);
}

.timeline-fill {
  height: 100%;
  background: linear-gradient(90deg, var(--accent-gold), var(--accent));
  border-radius: 8px;
  transition: width 0.3s ease;
}

.timeline-mini span {
  position: absolute;
  width: 100%;
  text-align: center;
  font-size: 10px;
  font-weight: 600;
  color: #fff;
  text-shadow: 0 0 3px rgba(0, 0, 0, 0.8);
}

/* ===== 画布 ===== */
.canvas-area {
  flex: 1;
  overflow: hidden;
  position: relative;
}

.game-canvas {
  width: 100%;
  height: 100%;
}

.battlefield-mock {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: var(--space-sm);
  gap: var(--space-sm);
  justify-content: center;
}

.zone-row {
  display: flex;
  gap: var(--space-sm);
  justify-content: center;
}

.zone-card {
  width: 64px;
  height: 90px;
  background: var(--bg-surface);
  border: 2px dashed var(--border);
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
}

.zone-label {
  font-size: 11px;
  color: var(--text-secondary);
}

.mid-divider {
  text-align: center;
  font-size: 11px;
  color: var(--text-disabled);
  padding: 4px 0;
  letter-spacing: 2px;
}

/* ===== 底部操作栏 ===== */
.hud-footer {
  padding: 8px var(--space-sm);
  padding-bottom: max(8px, env(safe-area-inset-bottom));
  background: var(--bg-surface);
  border-top: 1px solid var(--border);
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.hand-scroll {
  display: flex;
  gap: 4px;
  overflow-x: auto;
  padding-bottom: 4px;
}

.hand-card {
  width: 48px;
  height: 67px;
  background: var(--bg-surface-2);
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  color: var(--text-disabled);
  cursor: pointer;
}

.hand-card:active {
  border-color: var(--accent);
  box-shadow: var(--shadow-glow);
}

.bottom-row {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.action-row {
  display: flex;
  gap: var(--space-sm);
}

.btn-action {
  flex: 1;
  padding: 10px 0;
  border: none;
  border-radius: var(--radius-sm);
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
}

.btn-primary {
  background: var(--accent-blue);
  color: #fff;
}

.btn-primary:active {
  filter: brightness(1.15);
}

.btn-danger {
  background: var(--accent);
  color: #fff;
}

.btn-danger:active {
  filter: brightness(1.15);
}
</style>