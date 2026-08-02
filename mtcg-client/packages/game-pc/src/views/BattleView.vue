<script setup lang="ts">
/**
 * PC 端对战视图
 *
 * 布局（严格按原型图，**全部用绝对定位 + 百分比**，保证任何分辨率下都不重叠）：
 *
 *   ┌──────────────────────────────────────────────┐
 *   │ ← [阶段] 第3回合            对手名 [==时间线==] │  顶部 HUD
 *   ├──────────────────────────────────────────────┤
 *   │                                              │
 *   │  ┌─卡背─┐ ┌─────基地区(6槽)──────┐ ┌─卡背─┐ │
 *   │  │角色38│ │  □   □   □   □   □  □  │ │冲击9 │ │
 *   │  └──────┘ └──────────────────────┘ └──────┘ │
 *   │  ┌─撤退─┐                                ┌─┐ │
 *   │  └──────┘                                │时│ │
 *   │  ┌─裁剪─┐  ┌───┐  ┌─┐  ┌─┐  ┌───┐  ┌─┐  │间│ │
 *   │  └──────┘  │后 │  │侧│  │侧│  │先 │  │后│  │线│ │
 *   │            └───┘  └─┘  └─┘  └───┘  └─┘  │  │ │
 *   │                                          │  │ │
 *   │            ┌───┐  ┌─┐  ┌─┐  ┌───┐        │  │ │
 *   │            │后 │  │侧│  │侧│  │先 │        │  │ │
 *   │            └───┘  └─┘  └─┘  └───┘        └─┘ │
 *   │  ┌─裁剪─┐                                  │
 *   │  └──────┘                                  │
 *   │  ┌─撤退─┐                                  │
 *   │  └──────┘                                  │
 *   │  ┌─卡背─┐ ┌─────基地区(6槽)──────┐ ┌─卡背─┐│
 *   │  │冲击9 │ │  □   □   □   □   □  □  │ │角色41││
 *   │  └──────┘ └──────────────────────┘ └──────┘│
 *   ├──────────────────────────────────────────────┤
 *   │ 手牌 [卡][卡][卡][卡][卡]   [结束] [认输]    │  底部 HUD
 *   └──────────────────────────────────────────────┘
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
  <div class="battle-pc">
    <!-- 顶部 HUD -->
    <header class="hud-top">
      <button class="btn-back" @click="emit('navigate', 'home')">←</button>
      <span class="phase-badge">{{ phaseLabel }}</span>
      <span class="turn-info">第 {{ turnCount }} 回合</span>
      <span class="opponent">{{ store.opponent?.playerId ?? 'AI 对手' }}</span>
      <div class="timeline-mini">
        <div class="timeline-fill" :style="{ width: opponentPercent }"></div>
        <span>对手时间线 {{ opponentTimeline }} / 9</span>
      </div>
    </header>

    <!-- 战区 - 整块相对定位，内部元素全部绝对定位 -->
    <main class="canvas-area">
      <div class="battlefield">

        <!-- 对手半场：上下两行（基地区 + 菱形战区） -->
        <!-- 顶栏：卡组 / 基地区 / 冲击 -->
        <div class="row-top opponent">
          <div class="zone deck">
            <img class="deck-back" src="/card_back_character.png" alt="角色卡组" />
            <span class="deck-tag">角色卡组</span>
            <span class="deck-count">38</span>
          </div>
          <div class="zone base">
            <span class="zone-label">基地区 · 6 槽</span>
            <div class="base-row">
              <div class="base-slot" v-for="i in 6" :key="i"></div>
            </div>
          </div>
          <div class="zone deck">
            <img class="deck-back" src="/card_back_rush.png" alt="冲击卡组" />
            <span class="deck-tag">冲击卡组</span>
            <span class="deck-count">9</span>
          </div>
        </div>

        <!-- 战区主体：左列 + 菱形(居中) + 右列 -->
        <div class="row-mid">
          <!-- 左列：撤退 / 裁剪 -->
          <div class="side-stack">
            <div class="zone small">
              <div class="zone-icon">↩</div>
              <div class="zone-text">撤退</div>
            </div>
            <div class="zone small">
              <div class="zone-icon">✂</div>
              <div class="zone-text">裁剪</div>
            </div>
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

          <!-- 右列：时间线 -->
          <div class="side-stack">
            <div class="zone tall opponent-side">
              <div class="zone-label">时间线</div>
            </div>
          </div>
        </div>

        <!-- 中线 -->
        <div class="row-midline">
          <span class="turn-label">你的回合 ~ 第 {{ turnCount }} 回合</span>
          <button class="btn-end-turn">结束回合</button>
        </div>

        <!-- 我方半场 -->
        <div class="row-mid">
          <div class="side-stack">
            <div class="zone tall">
              <div class="zone-label">时间线</div>
            </div>
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
            <div class="zone small">
              <div class="zone-icon">✂</div>
              <div class="zone-text">裁剪</div>
            </div>
            <div class="zone small">
              <div class="zone-icon">↩</div>
              <div class="zone-text">撤退</div>
            </div>
          </div>
        </div>

        <div class="row-top local">
          <div class="zone deck">
            <img class="deck-back" src="/card_back_rush.png" alt="冲击卡组" />
            <span class="deck-tag">冲击卡组</span>
            <span class="deck-count">9</span>
          </div>
          <div class="zone base">
            <span class="zone-label">基地区 · 6 槽</span>
            <div class="base-row">
              <div class="base-slot" v-for="i in 6" :key="i"></div>
            </div>
          </div>
          <div class="zone deck">
            <img class="deck-back" src="/card_back_character.png" alt="角色卡组" />
            <span class="deck-tag">角色卡组</span>
            <span class="deck-count">41</span>
          </div>
        </div>

      </div>
    </main>

    <!-- 底部 HUD -->
    <footer class="hud-bottom">
      <div class="hand-area">
        <span class="hand-label">手牌</span>
        <div class="hand-cards">
          <div class="hand-card" v-for="i in 5" :key="i"></div>
        </div>
      </div>
      <div class="actions">
        <div class="timeline-mini">
          <div class="timeline-fill" :style="{ width: localPercent }"></div>
          <span>时间线 {{ localTimeline }} / 9</span>
        </div>
        <button class="btn btn-primary">结束阶段</button>
        <button class="btn btn-danger">认输</button>
      </div>
    </footer>
  </div>
</template>

<style scoped>
/* ============================================
   整体 - 5 段式：顶 HUD / 战区 / 中线 / 战区 / 底 HUD
   ============================================ */
.battle-pc {
  display: flex;
  flex-direction: column;
  height: 100dvh;
  background: var(--bg-base);
  overflow: hidden;
  /* 卡牌真实比例：取自素材 assets/card/designs/*.png = 747×1042 */
  --card-aspect: 747 / 1042;
  --card-gap: 3px;
  --card-h: 92px;
}

/* ============================================
   顶部 HUD
   ============================================ */
.hud-top {
  flex-shrink: 0;
  height: 44px;
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  padding: 0 var(--space-md);
  background: var(--bg-surface);
  border-bottom: 1px solid var(--border);
}

.btn-back {
  width: 28px;
  height: 28px;
  background: transparent;
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  color: var(--text-secondary);
  cursor: pointer;
  font-size: 14px;
}

.btn-back:hover {
  border-color: var(--accent);
  color: var(--accent);
}

.phase-badge {
  padding: 2px 10px;
  background: var(--accent-blue);
  color: #fff;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 600;
}

.turn-info {
  font-size: 13px;
  color: var(--text-secondary);
}

.opponent {
  margin-left: auto;
  font-size: 13px;
  color: var(--text-primary);
}

.timeline-mini {
  position: relative;
  width: 160px;
  height: 20px;
  background: var(--bg-base);
  border: 1px solid var(--border);
  border-radius: 10px;
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
  font-size: 11px;
  font-weight: 600;
  color: #fff;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.8);
  z-index: 1;
}

/* ============================================
   战区 - 6 段上下排列，每段高度固定 = 不重叠关键
   ============================================ */
.canvas-area {
  flex: 1;
  min-height: 0;
  padding: var(--space-md);
  overflow: hidden;
}

.battlefield {
  height: 100%;
  min-height: 0;
  display: flex;
  flex-direction: column;
  gap: var(--space-sm);
}

/* 顶/底栏：卡组靠边、基地区居中（两侧卡组等宽，space-between 天然居中） */
.row-top {
  flex-shrink: 0;
  width: 100%;
  height: calc(var(--card-h) + 8px);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-sm);
  padding: 4px var(--space-md);
}

/* 中线固定高度 */
.row-midline {
  flex-shrink: 0;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-md);
  background: var(--bg-surface);
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
}

.turn-label {
  font-size: 13px;
  color: var(--text-primary);
  font-weight: 600;
}

.btn-end-turn {
  height: 24px;
  padding: 0 16px;
  background: var(--accent);
  color: #fff;
  border: none;
  border-radius: var(--radius-sm);
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
}

.btn-end-turn:hover {
  filter: brightness(1.1);
}

/* 战区主体 - grid 三列：左列 / 菱形 / 右列 */
.row-mid {
  flex: 1 1 0;
  min-height: 0;
  display: grid;
  grid-template-columns: 100px 1fr 100px;
  gap: var(--space-sm);
  align-items: stretch;
  justify-items: stretch;
  padding: 0 var(--space-sm);
}

/* ============================================
   通用区域（zone）
   ============================================ */
.zone {
  position: relative;
  background: var(--bg-surface);
  border: 2px solid var(--border);
  border-radius: var(--radius-md);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4px;
  min-width: 0;
  min-height: 0;
}

.zone.small {
  height: 60px;
  flex-shrink: 0;
  width: 100%;
}

.zone.tall {
  flex: 1 1 0;
  min-height: 0;
  width: 100%;
}

.zone.base {
  border-color: var(--accent-green);
  background: var(--bg-surface-2);
  /* 高度 = 1 个卡高，宽度 = 6 个卡宽（由内部 slot 撑开） */
  height: var(--card-h);
  flex-shrink: 0;
  padding: 0;
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

.zone-label {
  position: absolute;
  top: 2px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 9px;
  color: #fff;
  letter-spacing: 1px;
  font-weight: 700;
  pointer-events: none;
  white-space: nowrap;
  background: rgba(0, 0, 0, 0.75);
  padding: 1px 6px;
  border-radius: 8px;
  z-index: 2;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.6);
}

/* 对手半场所有标签置底（菱形 + 基地区 + 时间线） */
.row-top.opponent .zone-label,
.zone.opponent-side .zone-label,
.diamond.opponent .zone-label {
  top: auto;
  bottom: 2px;
}

.zone-icon {
  font-size: 18px;
  color: var(--accent);
}

.zone-text {
  font-size: 10px;
  color: var(--text-secondary);
  margin-top: 2px;
}

/* 卡组标签：我方置顶（靠近中线），对手置底（靠近中线） */
.deck-tag {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  top: 2px;
  font-size: 9px;
  color: #fff;
  letter-spacing: 1px;
  font-weight: 700;
  white-space: nowrap;
  background: rgba(0, 0, 0, 0.78);
  padding: 1px 6px;
  border-radius: 8px;
  z-index: 2;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.6);
}

/* 剩余统计数：居中醒目 */
.deck-count {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 18px;
  font-weight: 700;
  color: #fff;
  background: rgba(0, 0, 0, 0.72);
  padding: 0 8px;
  border-radius: 10px;
  z-index: 2;
  min-width: 22px;
  text-align: center;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.8);
}

/* 对手半场卡组标签置底（靠近中线侧） */
.row-top.opponent .deck-tag {
  top: auto;
  bottom: 2px;
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
  border-radius: var(--radius-sm);
  flex-shrink: 0;
}

/* ============================================
   侧边栈（撤退/裁剪/时间线）
   ============================================ */
.side-stack {
  display: flex;
  flex-direction: column;
  gap: var(--space-sm);
  align-items: stretch;
  justify-content: center;
  min-height: 0;
  min-width: 0;
}

/* ============================================
   菱形战区 - 在 grid cell 内 flex 居中
   ============================================ */
.diamond {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-sm);
  min-height: 0;
  min-width: 0;
  width: 100%;
  height: 100%;
}

.side-cell {
  display: flex;
  align-items: center;
  justify-content: center;
}

.center-cell {
  display: flex;
  flex-direction: column;
  gap: var(--space-sm);
  align-items: center;
  justify-content: center;
  min-height: 0;
}

/* 卡位 - 统一使用卡牌素材比例 747:1042 */
.slot {
  width: 80px;
  aspect-ratio: var(--card-aspect);
  background: var(--bg-surface);
  border: 2px solid var(--border);
  border-radius: var(--radius-md);
  position: relative;
}

/* 空槽统一虚线 */
.slot.empty {
  border-style: dashed;
  border-color: var(--border-light);
}

/* ============================================
   底部 HUD
   ============================================ */
.hud-bottom {
  flex-shrink: 0;
  height: 88px;
  display: flex;
  align-items: center;
  gap: var(--space-md);
  padding: var(--space-sm) var(--space-md);
  background: var(--bg-surface);
  border-top: 1px solid var(--border);
}

.hand-area {
  flex: 1;
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  min-width: 0;
}

.hand-label {
  font-size: 12px;
  color: var(--text-secondary);
  flex-shrink: 0;
}

.hand-cards {
  flex: 1;
  display: flex;
  gap: var(--space-xs);
  overflow-x: auto;
}

.hand-card {
  width: 50px;
  height: 70px;
  flex-shrink: 0;
  background: var(--bg-surface-2);
  border: 2px solid var(--border);
  border-radius: var(--radius-sm);
}

.hand-card:hover {
  border-color: var(--accent);
}

.actions {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  flex-shrink: 0;
}

.btn {
  height: 30px;
  padding: 0 14px;
  border: none;
  border-radius: var(--radius-sm);
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  white-space: nowrap;
}

.btn:hover {
  filter: brightness(1.1);
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