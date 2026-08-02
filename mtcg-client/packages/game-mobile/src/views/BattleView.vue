<script setup lang="ts">
/**
 * 移动端对战视图
 *
 * 布局：6 段垂直堆叠（顶栏/战区/中线/战区/底栏），全部用 flex-shrink: 0 防止互相挤压
 */
import { computed } from 'vue'
import { useGameStore } from '@mtcg/common/stores'

const emit = defineEmits<{
  navigate: [view: string]
}>()

const store = useGameStore()

const turnCount = computed(() => store.gameState?.turnCount ?? 1)
const phaseLabel = computed(() => store.gameState?.currentPhase ?? '行动阶段')

const opponentTimeline = computed(() => store.opponent?.timeline.length ?? 0)
const localTimeline = computed(() => store.localPlayer?.timeline.length ?? 0)
const opponentPercent = computed(() => `${(opponentTimeline.value / 9) * 100}%`)
const localPercent = computed(() => `${(localTimeline.value / 9) * 100}%`)
</script>

<template>
  <div class="battle-mobile">
    <!-- 顶部 HUD -->
    <header class="hud-top">
      <button class="btn-back" @click="emit('navigate', 'home')">←</button>
      <span class="phase-badge">{{ phaseLabel }}</span>
      <span class="turn-info">第{{ turnCount }}回合</span>
    </header>
    <div class="hud-sub">
      <span class="opponent">{{ store.opponent?.playerId ?? 'AI 对手' }}</span>
      <div class="timeline-mini">
        <div class="timeline-fill" :style="{ width: opponentPercent }"></div>
        <span>{{ opponentTimeline }}/9</span>
      </div>
    </div>

    <main class="canvas-area">
      <div class="battlefield">

        <!-- 对手半场 顶栏 -->
        <div class="row-top">
          <div class="zone deck">
            <img class="deck-back" src="/card_back_character.png" alt="角色卡组" />
            <span class="deck-tag">角色</span>
            <span class="deck-count">38</span>
          </div>
          <div class="zone base">
            <div class="base-row">
              <div class="base-slot" v-for="i in 6" :key="i"></div>
            </div>
          </div>
          <div class="zone deck">
            <img class="deck-back" src="/card_back_rush.png" alt="冲击卡组" />
            <span class="deck-tag">冲击</span>
            <span class="deck-count">9</span>
          </div>
        </div>

        <!-- 对手半场 战区 -->
        <div class="row-mid">
          <div class="side-stack">
            <div class="zone small">↩</div>
            <div class="zone small">✂</div>
          </div>

          <!-- 对手半场菱形：侧翼(左) / 后卫+先锋(中) / 侧翼(右)，先锋在下（靠近中线） -->
          <div class="diamond opponent">
            <div class="side-cell">
              <div class="slot">
                <div class="zone-label">侧翼区</div>
              </div>
            </div>
            <div class="center-cell">
              <div class="slot empty">
                <div class="zone-label">后卫区</div>
              </div>
              <div class="slot">
                <div class="zone-label">先锋区</div>
              </div>
            </div>
            <div class="side-cell">
              <div class="slot">
                <div class="zone-label">侧翼区</div>
              </div>
            </div>
          </div>

          <div class="side-stack">
            <div class="zone tall">⏱</div>
          </div>
        </div>

        <!-- 中线 -->
        <div class="row-midline">
          <span class="turn-label">你的回合</span>
          <button class="btn-end-turn">结束回合</button>
        </div>

        <!-- 我方半场 战区 -->
        <div class="row-mid">
          <div class="side-stack">
            <div class="zone tall">⏱</div>
          </div>

          <!-- 我方半场菱形：侧翼(左) / 先锋+后卫(中) / 侧翼(右)，先锋在上（靠近中线） -->
          <div class="diamond local">
            <div class="side-cell">
              <div class="slot empty">
                <div class="zone-label">侧翼区</div>
              </div>
            </div>
            <div class="center-cell">
              <div class="slot">
                <div class="zone-label">先锋区</div>
              </div>
              <div class="slot">
                <div class="zone-label">后卫区</div>
              </div>
            </div>
            <div class="side-cell">
              <div class="slot empty">
                <div class="zone-label">侧翼区</div>
              </div>
            </div>
          </div>

          <div class="side-stack">
            <div class="zone small">✂</div>
            <div class="zone small">↩</div>
          </div>
        </div>

        <!-- 我方半场 底栏 -->
        <div class="row-top">
          <div class="zone deck">
            <img class="deck-back" src="/card_back_rush.png" alt="冲击卡组" />
            <span class="deck-tag">冲击</span>
            <span class="deck-count">9</span>
          </div>
          <div class="zone base">
            <div class="base-row">
              <div class="base-slot" v-for="i in 6" :key="i"></div>
            </div>
          </div>
          <div class="zone deck">
            <img class="deck-back" src="/card_back_character.png" alt="角色卡组" />
            <span class="deck-tag">角色</span>
            <span class="deck-count">41</span>
          </div>
        </div>

      </div>
    </main>

    <!-- 底部 HUD -->
    <footer class="hud-bottom">
      <div class="hand-row">
        <span class="hand-label">手牌</span>
        <div class="hand-scroll">
          <div class="hand-card" v-for="i in 5" :key="i"></div>
        </div>
      </div>
      <div class="bottom-actions">
        <div class="timeline-mini">
          <div class="timeline-fill" :style="{ width: localPercent }"></div>
          <span>{{ localTimeline }}/9</span>
        </div>
        <div class="action-row">
          <button class="btn btn-primary">结束</button>
          <button class="btn btn-danger">认输</button>
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
  overflow: hidden;
  padding-top: env(safe-area-inset-top);
  padding-bottom: env(safe-area-inset-bottom);
  /* 卡牌真实比例：取自素材 assets/card/designs/*.png = 747×1042 */
  --card-aspect: 747 / 1042;
  --card-gap: 2px;
  --card-h: clamp(38px, 14vw, 52px);
}

/* 顶部 HUD */
.hud-top {
  flex-shrink: 0;
  height: 32px;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 0 8px;
  background: var(--bg-surface);
  border-bottom: 1px solid var(--border);
}

.btn-back {
  width: 24px;
  height: 24px;
  background: transparent;
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  color: var(--text-secondary);
  font-size: 12px;
  cursor: pointer;
}

.phase-badge {
  padding: 1px 6px;
  background: var(--accent-blue);
  color: #fff;
  border-radius: 6px;
  font-size: 9px;
  font-weight: 600;
}

.turn-info {
  font-size: 10px;
  color: var(--text-secondary);
}

.hud-sub {
  flex-shrink: 0;
  height: 20px;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 0 8px;
  background: var(--bg-surface);
  border-bottom: 1px solid var(--border);
}

.opponent {
  font-size: 10px;
  color: var(--text-primary);
}

.timeline-mini {
  position: relative;
  flex: 1;
  height: 12px;
  background: var(--bg-base);
  border: 1px solid var(--border);
  border-radius: 6px;
  overflow: hidden;
}

.timeline-fill {
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  background: linear-gradient(90deg, var(--accent), var(--accent-blue));
  transition: width 0.3s ease;
}

.timeline-mini span {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 8px;
  font-weight: 600;
  color: #fff;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.8);
  z-index: 1;
}

/* 战区 - 6 段堆叠 */
.canvas-area {
  flex: 1;
  min-height: 0;
  padding: 4px;
  overflow: hidden;
}

.battlefield {
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.row-top {
  flex-shrink: 0;
  width: 100%;
  height: calc(var(--card-h) + 4px);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 3px;
  padding: 0 6px;
}

.row-midline {
  flex-shrink: 0;
  height: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background: var(--bg-surface);
  border: 1px solid var(--accent);
  border-radius: var(--radius-sm);
}

.turn-label {
  font-size: 10px;
  color: var(--text-primary);
  font-weight: 600;
}

.btn-end-turn {
  height: 18px;
  padding: 0 10px;
  background: var(--accent);
  color: #fff;
  border: none;
  border-radius: var(--radius-sm);
  font-size: 10px;
  font-weight: 600;
  cursor: pointer;
}

.row-mid {
  flex: 1;
  min-height: 0;
  display: grid;
  grid-template-columns: 36px 1fr 36px;
  gap: 3px;
}

/* 通用区域 */
.zone {
  background: var(--bg-surface);
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  color: var(--text-secondary);
  min-width: 0;
  min-height: 0;
}

.zone.small {
  flex: 1;
}

.zone.tall {
  height: 100%;
}

.zone.base {
  border-color: var(--accent-green);
  background: var(--bg-surface-2);
  padding: 0;
  flex-shrink: 0;
  height: var(--card-h);
  justify-content: center;
}

.zone.deck {
  position: relative;
  /* 卡组 = 1 张卡背：高度 = 卡高，宽度由卡牌比例自动撑开 */
  height: var(--card-h);
  aspect-ratio: var(--card-aspect);
  padding: 0;
  overflow: hidden;
  flex-shrink: 0;
}

/* 卡背图填满卡组 */
.deck-back {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  pointer-events: none;
}

/* 卡组标签：我方置顶（靠近中线），对手置底（靠近中线） */
.deck-tag {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  top: 1px;
  font-size: 7px;
  color: #fff;
  font-weight: 700;
  white-space: nowrap;
  background: rgba(0, 0, 0, 0.78);
  padding: 0 4px;
  border-radius: 6px;
  z-index: 2;
  text-shadow: 0 1px 1px rgba(0, 0, 0, 0.6);
}

/* 剩余统计数：居中 */
.deck-count {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 13px;
  font-weight: 700;
  color: #fff;
  background: rgba(0, 0, 0, 0.72);
  padding: 0 6px;
  border-radius: 8px;
  z-index: 2;
  min-width: 16px;
  text-align: center;
  text-shadow: 0 1px 1px rgba(0, 0, 0, 0.8);
}

/* 对手半场（首个 row-top）卡组标签置底（靠近中线侧） */
.battlefield > .row-top:first-child .deck-tag {
  top: auto;
  bottom: 1px;
}

/* 基地区：6 个卡槽位，高度 = 1 卡高，宽度 = 6 卡宽 + 5 gap */
.base-row {
  display: flex;
  gap: var(--card-gap);
  height: 100%;
  align-items: stretch;
}

.base-slot {
  height: 100%;
  aspect-ratio: var(--card-aspect);
  background: var(--bg-base);
  border: 1px dashed var(--border-light);
  border-radius: 1px;
  flex-shrink: 0;
}

/* 侧边栈 */
.side-stack {
  display: flex;
  flex-direction: column;
  gap: 3px;
  min-width: 0;
}

/* 菱形 - 3 列 grid：侧翼 / 先锋+后卫 / 侧翼 */
.diamond {
  display: grid;
  grid-template-columns: auto auto auto;
  gap: 4px;
  align-items: center;
  justify-content: center;
  justify-self: center;
  min-height: 0;
  min-width: 0;
}

.side-cell {
  display: flex;
  align-items: center;
  justify-content: center;
}

.center-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
  align-items: center;
  justify-content: center;
  min-height: 0;
}

/* 卡位 - 统一使用卡牌素材比例 747:1042 */
.slot {
  height: 25vw;
  max-height: 84px;
  min-height: 62px;
  aspect-ratio: var(--card-aspect);
  background: var(--bg-surface);
  border: 1.5px solid var(--border);
  border-radius: var(--radius-sm);
  position: relative;
}

/* 空槽统一虚线 */
.slot.empty {
  border-style: dashed;
  border-color: var(--border-light);
}

/* 战区标签 - 顶部小标签（亮暗主题通用） */
.zone-label {
  position: absolute;
  top: 1px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 7px;
  color: #fff;
  text-align: center;
  pointer-events: none;
  white-space: nowrap;
  background: rgba(0, 0, 0, 0.75);
  padding: 0 4px;
  border-radius: 6px;
  z-index: 2;
  font-weight: 700;
  text-shadow: 0 1px 1px rgba(0, 0, 0, 0.6);
}

/* 对手半场菱形：标签置底 */
.diamond.opponent .zone-label {
  top: auto;
  bottom: 1px;
}

/* 底部 HUD */
.hud-bottom {
  flex-shrink: 0;
  padding: 4px 8px;
  padding-bottom: max(4px, env(safe-area-inset-bottom));
  background: var(--bg-surface);
  border-top: 1px solid var(--border);
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.hand-row {
  display: flex;
  align-items: center;
  gap: 6px;
  height: 50px;
}

.hand-label {
  font-size: 9px;
  color: var(--text-secondary);
  flex-shrink: 0;
}

.hand-scroll {
  flex: 1;
  display: flex;
  gap: 4px;
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
}

.hand-scroll::-webkit-scrollbar {
  display: none;
}

.hand-card {
  width: 34px;
  height: 48px;
  flex-shrink: 0;
  background: var(--bg-surface-2);
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
}

.bottom-actions {
  display: flex;
  align-items: center;
  gap: 6px;
  height: 28px;
}

.action-row {
  display: flex;
  gap: 4px;
}

.btn {
  height: 24px;
  padding: 0 10px;
  border: none;
  border-radius: var(--radius-sm);
  font-size: 10px;
  font-weight: 600;
  cursor: pointer;
  white-space: nowrap;
}

.btn-primary {
  background: var(--accent-blue);
  color: #fff;
}

.btn-danger {
  background: var(--accent-red);
  color: #fff;
}
</style>