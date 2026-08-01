<script setup lang="ts">
/**
 * 移动端对战视图
 * 竖屏布局：顶部对手信息，中间 PixiJS 画布，底部操作区
 */
import { ref, onMounted, onUnmounted } from 'vue'
import { useGameStore } from '@mtcg/common/stores'

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
      <div class="opponent-row">
        <span class="phase-badge">{{ store.gameState?.currentPhase }}</span>
        <span class="turn-info">回合 {{ store.gameState?.turnCount }}</span>
        <span class="player-name">{{ store.opponent?.playerId }}</span>
      </div>
      <div class="timeline-mini">
        <div class="timeline-fill" :style="{ width: ((store.opponent?.timeline.length ?? 0) / 9 * 100) + '%' }"></div>
        <span>{{ store.opponent?.timeline.length ?? 0 }}/9</span>
      </div>
    </header>

    <!-- 中间：PixiJS 画布 -->
    <main class="canvas-area" ref="canvasContainer">
      <div id="game-canvas" class="game-canvas"></div>
    </main>

    <!-- 底部：我方时间线 + 操作按钮 -->
    <footer class="hud-footer">
      <div class="timeline-mini">
        <div class="timeline-fill" :style="{ width: ((store.localPlayer?.timeline.length ?? 0) / 9 * 100) + '%' }"></div>
        <span>{{ store.localPlayer?.timeline.length ?? 0 }}/9</span>
      </div>
      <div class="action-row">
        <button
          v-if="store.isLocalPlayerActive"
          class="btn-action"
          @click="store.doAction('END_PHASE')"
        >
          结束阶段
        </button>
        <button class="btn-action btn-surrender" @click="store.doAction('SURRENDER')">
          认输
        </button>
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
  background: #0a0a1a;
}

/* ===== 顶部 HUD ===== */
.hud-header {
  padding: 8px 12px;
  background: rgba(0, 0, 0, 0.7);
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.opponent-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 13px;
}

.phase-badge {
  background: #4a90d9;
  color: #fff;
  padding: 2px 8px;
  border-radius: 8px;
  font-size: 11px;
  font-weight: bold;
}

.turn-info {
  color: #888;
  font-size: 12px;
}

.player-name {
  color: #ccc;
  font-size: 13px;
}

/* ===== 时间线 ===== */
.timeline-mini {
  display: flex;
  align-items: center;
  height: 16px;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 8px;
  overflow: hidden;
  position: relative;
}

.timeline-fill {
  height: 100%;
  background: linear-gradient(90deg, #f0c040, #f08020);
  border-radius: 8px;
  transition: width 0.3s ease;
}

.timeline-mini span {
  position: absolute;
  width: 100%;
  text-align: center;
  font-size: 10px;
  font-weight: bold;
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

/* ===== 底部操作栏 ===== */
.hud-footer {
  padding: 8px 12px;
  padding-bottom: max(8px, env(safe-area-inset-bottom));
  background: rgba(0, 0, 0, 0.7);
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.action-row {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.btn-action {
  flex: 1;
  padding: 10px 0;
  background: #4a90d9;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 15px;
  font-weight: bold;
  cursor: pointer;
  max-width: 160px;
}

.btn-action:active {
  background: #357abd;
}

.btn-surrender {
  background: #c0392b;
}

.btn-surrender:active {
  background: #a93226;
}
</style>