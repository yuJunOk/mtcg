<script setup lang="ts">
/**
 * PC 端对战视图
 * 横屏布局：对手在上，我方在下，中间是 PixiJS 画布
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
  <div class="battle-pc">
    <!-- 顶部 HUD -->
    <header class="hud-top">
      <div class="opponent-info">
        <span class="phase-badge">{{ store.gameState?.currentPhase }}</span>
        <span class="turn-info">回合 {{ store.gameState?.turnCount }}</span>
        <span class="player-name">对手: {{ store.opponent?.playerId }}</span>
      </div>
      <div class="timeline-bar">
        <div class="timeline-progress" :style="{ width: ((store.opponent?.timeline.length ?? 0) / 9 * 100) + '%' }"></div>
        <span class="timeline-text">{{ store.opponent?.timeline.length ?? 0 }} / 9</span>
      </div>
    </header>

    <!-- 中间：PixiJS 游戏画布 -->
    <main class="canvas-area" ref="canvasContainer">
      <div id="game-canvas" class="game-canvas"></div>
    </main>

    <!-- 底部 HUD -->
    <footer class="hud-bottom">
      <div class="timeline-bar">
        <div class="timeline-progress" :style="{ width: ((store.localPlayer?.timeline.length ?? 0) / 9 * 100) + '%' }"></div>
        <span class="timeline-text">{{ store.localPlayer?.timeline.length ?? 0 }} / 9</span>
      </div>
      <div class="action-bar">
        <button
          v-if="store.isLocalPlayerActive"
          class="btn-action"
          @click="store.doAction('END_PHASE')"
        >
          结束阶段
        </button>
        <button class="btn-action btn-danger" @click="store.doAction('SURRENDER')">
          认输
        </button>
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
  background: #0a0a1a;
}

/* ===== 顶部 HUD ===== */
.hud-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 16px;
  background: rgba(0, 0, 0, 0.6);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  height: 48px;
  flex-shrink: 0;
}

.opponent-info {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
}

.phase-badge {
  background: #4a90d9;
  color: #fff;
  padding: 2px 10px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: bold;
}

.turn-info {
  color: #888;
}

.player-name {
  color: #ccc;
}

/* ===== 时间线进度条 ===== */
.timeline-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 200px;
  height: 20px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  overflow: hidden;
  position: relative;
}

.timeline-progress {
  height: 100%;
  background: linear-gradient(90deg, #f0c040, #f08020);
  border-radius: 10px;
  transition: width 0.3s ease;
}

.timeline-text {
  position: absolute;
  width: 100%;
  text-align: center;
  font-size: 11px;
  font-weight: bold;
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

/* ===== 底部 HUD ===== */
.hud-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 16px;
  background: rgba(0, 0, 0, 0.6);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  height: 48px;
  flex-shrink: 0;
}

.action-bar {
  display: flex;
  gap: 8px;
}

.btn-action {
  padding: 6px 20px;
  background: #4a90d9;
  color: #fff;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-action:hover {
  background: #357abd;
}

.btn-danger {
  background: #c0392b;
}

.btn-danger:hover {
  background: #a93226;
}
</style>