<script setup lang="ts">
import { computed } from 'vue'
import { useGameStore } from '@mtcg/common/stores'
import { Card } from '@mtcg/common/components'

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

        <!-- 对手区域一行 = 三列（相对我方镜像）：
             左列 = 角色卡组(1行/外) → 撤退区 → 裁剪区(靠中线)  ← 裁剪区和撤退区交换位置
             中列 = 基地(1行/外) → 菱形(靠中线) ; 卡槽从右到左 6→1 镜像
             右列 = 冲击卡组(1行/外) → 时间线(靠中线) -->
        <div class="row-area opponent-area">
          <!-- 左列：角色卡组(第1行) -> 撤退区 -> 虚空区（靠中线） -->
          <div class="col col-left opponent">
            <!-- 角色卡组 -->
            <div class="zone deck opponent-deck">
              <span class="zone-tag bottom-right">角色·38</span>
              <div class="stack-face">
                <Card back-type="character" class="stacked" />
              </div>
            </div>
            <div class="zone stack-zone retreat-stack">
              <span class="zone-tag bottom-right">撤退</span>
              <div class="stack-face">
                <Card back-type="character" class="stacked" />
              </div>
            </div>
            <div class="zone stack-zone crop-stack">
              <span class="zone-tag bottom-right">虚空</span>
              <div class="stack-face">
                <Card back-type="character" class="stacked" />
              </div>
            </div>
          </div>

          <!-- 中列：基地(第一行=顶部外) → 菱形(靠中线) ; 我方左对齐1→6，对方右对齐6→1镜像 -->
          <div class="col col-center">
            <div class="zone base opponent-base">
              <span class="base-tag left-top">基地</span>
              <div class="base-row row-reverse">
                <div class="base-slot" v-for="i in 6" :key="i" :data-slot="7 - i"></div>
              </div>
            </div>
            <!-- 对手半场菱形：侧翼(左) / 后卫+先锋(中) / 侧翼(右)，先锋在下（靠近中线） -->
            <div class="diamond opponent">
              <div class="side-cell cell-wrap">
                <span class="cell-label-out">侧翼区</span>
                <div class="slot">
                  <span class="cell-power"></span>
                  <span class="cell-attach"></span>
                </div>
              </div>
              <div class="center-cell">
                <div class="cell-wrap">
                  <span class="cell-label-out">后卫区</span>
                  <div class="slot empty">
                    <span class="cell-power"></span>
                    <span class="cell-attach"></span>
                  </div>
                </div>
                <div class="cell-wrap">
                  <span class="cell-label-out">先锋区</span>
                  <div class="slot">
                    <span class="cell-power"></span>
                    <span class="cell-attach"></span>
                  </div>
                </div>
              </div>
              <div class="side-cell cell-wrap">
                <span class="cell-label-out">侧翼区</span>
                <div class="slot">
                  <span class="cell-power"></span>
                  <span class="cell-attach"></span>
                </div>
              </div>
            </div>
          </div>

          <!-- 右列：冲击卡组(第一行=顶部外, 打横放节省高度) → 时间线(吃剩余高度, 3×3=9卡槽阵列) -->
          <div class="col col-right opponent">
            <div class="zone deck opponent-deck rush-deck">
              <span class="zone-tag bottom-right rush">冲击·9</span>
              <Card back-type="rush" horizontal />
            </div>
            <div class="zone timeline-zone">
              <span class="stack-label-out top-right">时间线</span>
              <div class="timeline-stack">
                <div class="timeline-slot" v-for="i in 9" :key="i" :data-slot="i" :style="{ '--idx': i - 1 }"></div>
              </div>
            </div>
          </div>
        </div>

        <!-- 回合说明一行（中线） -->
        <div class="row-midline">
          <span class="turn-label">你的回合 ~ 第 {{ turnCount }} 回合</span>
          <button class="btn-end-turn">结束回合</button>
        </div>

        <!-- 我方区域一行 = 三列（相对对方镜像）：
             左列 = 时间线(靠中线) → 冲击卡组(最后一行/外边缘底)
             中列 = 菱形(靠中线) → 基地(最后一行/外) ; 卡槽左对齐 1→6
             右列 = 裁剪区(靠中线) → 撤退区 → 角色卡组(最后一行/外边缘底) -->
        <div class="row-area local-area">
          <!-- 左列：时间线(3×3=9卡槽阵列, 吃剩余高度) → 冲击卡组(底部=最后一行, 打横放节省高度) -->
          <div class="col col-left local">
            <div class="zone timeline-zone local-timeline">
              <span class="stack-label-out bottom-left">时间线</span>
              <div class="timeline-stack">
                <div class="timeline-slot" v-for="i in 9" :key="i" :data-slot="i" :style="{ '--idx': i - 1 }"></div>
              </div>
            </div>
            <div class="zone deck local-deck rush-deck">
              <span class="zone-tag top-left rush">冲击·9</span>
              <Card back-type="rush" horizontal />
            </div>
          </div>

          <!-- 中列：菱形作战区(靠中线) + 基地区(底部外边缘) -->
          <div class="col col-center">
            <!-- 我方半场菱形：侧翼(左) / 先锋+后卫(中) / 侧翼(右)，先锋在上（靠近中线） -->
            <div class="diamond local">
              <div class="side-cell cell-wrap local">
                <div class="slot empty">
                  <span class="cell-power"></span>
                  <span class="cell-attach"></span>
                </div>
                <span class="cell-label-out">侧翼区</span>
              </div>
              <div class="center-cell">
                <div class="cell-wrap local">
                  <div class="slot">
                    <span class="cell-power"></span>
                    <span class="cell-attach"></span>
                  </div>
                  <span class="cell-label-out">先锋区</span>
                </div>
                <div class="cell-wrap local">
                  <div class="slot">
                    <span class="cell-power"></span>
                    <span class="cell-attach"></span>
                  </div>
                  <span class="cell-label-out">后卫区</span>
                </div>
              </div>
              <div class="side-cell cell-wrap local">
                <div class="slot empty">
                  <span class="cell-power"></span>
                  <span class="cell-attach"></span>
                </div>
                <span class="cell-label-out">侧翼区</span>
              </div>
            </div>
            <div class="zone base local-base">
              <span class="base-tag right-bottom">基地</span>
              <div class="base-row">
                <div class="base-slot" v-for="i in 6" :key="i" :data-slot="i"></div>
              </div>
            </div>
          </div>

          <!-- 右列：裁剪区 → 撤退区 → 角色卡组（底部=最后一行=外边缘，和我方左列冲击卡组位置一致） -->
          <div class="col col-right local">
            <div class="zone stack-zone crop-stack">
              <span class="zone-tag top-left">虚空</span>
              <div class="stack-face">
                <Card back-type="character" class="stacked" />
              </div>
            </div>
            <div class="zone stack-zone retreat-stack">
              <span class="zone-tag top-left">撤退</span>
              <div class="stack-face">
                <Card back-type="character" class="stacked" />
              </div>
            </div>
            <div class="zone deck local-deck">
              <span class="zone-tag top-left">角色·41</span>
              <div class="stack-face">
                <Card back-type="character" class="stacked" />
              </div>
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
  --card-aspect-inv: 1042 / 747;  /* 横版比例(打横用) */
  --card-gap: 3px;
  --card-h: 92px;
  --col-w: 110px;  /* 左右列宽度 */
  --deck-meta-h: 20px;   /* 卡组顶/底条带高度: 放标签/统计/摘要 */
  --base-meta-h: 18px;   /* 基地区标签条高度 */
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

/* 对战区域 - 5 段式：顶 HUD / 对手区 / 中线 / 我方区 / 底 HUD
 *
 * 对手区 & 我方区 统一三列布局 (grid):
 *   ┌──────────┬───────────────────┬──────────┐
 *   │ 左列     │  中列             │  右列     │
 *   │ 裁剪区  │  基地区(对手)     │  时间线   │
 *   │ 撤退区  │  菱形作战区       │  冲击卡组 │
 *   │ 角色卡组│  菱形作战区(我方) │  时间线   │
 *   │         │  基地区(我方)     │          │
 *   └──────────┴───────────────────┴──────────┘
 *   注意: 我方区顺序: 左列(裁剪/撤退/角色卡组) 中列(菱形+基地) 右列(时间线+冲击卡组)
 *        方位保持: 对战中间是中线, 对手区上方, 我方区下方, 区域内部元素方位不变
 *
 * 标签外置原则:
 * - 卡组块: 两段式: 一条信息带(标签+统计+摘要一行) + 卡背
 *     对手侧: 信息带上 + 卡背下 (信息带靠外边缘)
 *     我方侧: 卡背上 + 信息带下 (信息带靠外边缘)
 * - 基地区: 只放 6 个卡槽阵列, "基地"标签用绝对定位挂在容器外边缘角落, 不挤占卡槽高度
 *     对手基地: 左下角外侧; 我方基地: 右上角外侧
 */

/* 对手区 & 我方区: 每区占可用区域 1 份, 用 grid 三列 左/中/右 */
.row-area {
  flex: 1 1 0;
  min-height: 0;
  display: grid;
  grid-template-columns: var(--col-w) 1fr var(--col-w);
  gap: var(--space-sm);
  align-items: stretch;
  justify-items: stretch;
  padding: 4px var(--space-md);
}

/* 通用列容器: 内部垂直堆叠 gap; 列内每块均分高度 (每块 flex:1) */
.col {
  display: flex;
  flex-direction: column;
  align-items: stretch;
  gap: var(--space-sm);
  min-width: 0;
  min-height: 0;
  width: 100%;
}

/* 左列整体对齐方向：
 *   对手左列 = flex-start => 角色卡组顶(第一行) → 撤退 → 裁剪(靠中线底)
 *   我方左列 = flex-end   => 时间线(靠中线顶) → 冲击卡组(最后一行=底部外边缘)
 */
.col-left.opponent { justify-content: flex-start; }
.col-left.local    { justify-content: flex-end; }

/* 中列: 基地在外边缘、菱形在中线侧  => space-between */
.col-center { justify-content: space-between; align-items: center; }

/* 右列整体对齐方向：
 *   对手右列 = flex-start => 冲击卡组顶(第一行) → 时间线(靠中线底)
 *   我方右列 = flex-end   => 裁剪(靠中线顶) → 撤退 → 角色卡组(最后一行=底部外边缘)
 */
.col-right.opponent { justify-content: flex-start; }
.col-right.local    { justify-content: flex-end; }

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

/* 虚空区 / 撤退区: 蓝灰色系卡槽, 和基地绿色区分 */
.zone.stack-zone {
  flex: 1 1 0;
  min-height: 0;
  width: 100%;
  max-width: 100%;
  overflow: visible;
  border-radius: var(--radius-md);
  border: 2px solid rgba(129, 140, 248, 0.65);
  background:
    repeating-linear-gradient(
      45deg,
      rgba(129, 140, 248, 0.05) 0 6px,
      transparent 6px 12px
    ),
    var(--bg-surface-2);
  box-shadow:
    inset 0 0 0 1px rgba(129, 140, 248, 0.3),
    inset 0 2px 6px rgba(0, 0, 0, 0.4),
    0 4px 14px rgba(0, 0, 0, 0.2);
}

/* 外置区域标签: 靛色系, 挂在卡槽外边缘
   暗色主题: 浅紫蓝底 + 浅色文字
   亮色主题: 靛蓝色底 + 白色文字 */
.zone-tag {
  position: absolute;
  font-size: 9px;
  color: #fff;
  font-weight: 700;
  letter-spacing: 1px;
  white-space: nowrap;
  background: #5C6BC0;
  padding: 1px 8px;
  border: 1px solid rgba(92, 107, 192, 0.8);
  border-radius: 6px;
  line-height: 1.4;
  pointer-events: none;
  z-index: 2;
  text-shadow: 0 1px 1px rgba(0, 0, 0, 0.3);
}
.zone-tag.top-left     { top: -10px; left: 4px; }
.zone-tag.top-right    { top: -10px; right: 4px; }
.zone-tag.bottom-left  { bottom: -10px; left: 4px; }
.zone-tag.bottom-right { bottom: -10px; right: 4px; }

/* 时间线区域: 冲击卡组打横后占高变小, 时间线吃全部剩余高度 (flex:1)
 * 9 个卡槽打横堆叠: 每张横版卡只露顶部一小条, 后面叠在前面上面 */
.zone.timeline-zone {
  flex: 1 1 0;
  min-height: 0;
  width: 100%;
  max-width: 100%;
  overflow: hidden;
  padding: 4px;
  background:
    repeating-linear-gradient(
      135deg,
      rgba(148, 163, 184, 0.06) 0 8px,
      transparent 8px 16px
    ),
    var(--bg-surface-2);
  border-color: var(--border-light);
  box-shadow: inset 0 0 0 1px rgba(148, 163, 184, 0.22);
}

/* 堆叠容器: relative + 子元素 absolute 按 --idx 偏移 */
.timeline-stack {
  position: relative;
  width: 100%;
  height: 100%;
}

/* 时间线 9 槽: 打横 + 堆叠, 平分占满整个高度
 * --idx: 0~8, 每张偏移 = (容器高度 - 卡高) / 8, 第0张顶、第8张底
 * 横版卡比例 w:h = 1042:747; 列宽110px-内边距8px=102px => 卡高≈73px */
.timeline-slot {
  --tl-card-h: calc(102px * 747 / 1042);
  position: absolute;
  top: calc(var(--idx) * (100% - var(--tl-card-h)) / 8);
  left: 0;
  right: 0;
  margin: 0 auto;
  width: 100%;
  aspect-ratio: var(--card-aspect-inv);
  background: radial-gradient(circle at 50% 30%, rgba(148,163,184,0.18), transparent 60%), var(--bg-base);
  border: 1.5px solid rgba(148, 163, 184, 0.55);
  border-radius: var(--radius-sm);
  box-shadow: inset 0 2px 5px rgba(0, 0, 0, 0.28), 0 1px 3px rgba(0, 0, 0, 0.3);
}
.timeline-slot::after {
  content: attr(data-slot);
  position: absolute;
  bottom: 1px;
  right: 2px;
  font-size: 7px;
  letter-spacing: 0.5px;
  color: rgba(148, 163, 184, 0.85);
  font-weight: 700;
  pointer-events: none;
}

/* 对方时间线: 序号在左上, 和我方右下相对 */
.timeline-zone:not(.local-timeline) .timeline-slot::after {
  bottom: auto;
  right: auto;
  top: 1px;
  left: 2px;
}

/* 我方时间线: 堆叠朝向反转 (第0张底部 -> 第8张顶部), 和对方相对 */
.local-timeline .timeline-slot {
  top: auto;
  bottom: calc(var(--idx) * (100% - var(--tl-card-h)) / 8);
}

/* 堆叠区域内部的卡面视觉 (card-face.s1 居中堆叠) */
.stack-face {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 4px;
}

/* 外置堆叠区/时间线区标签: 默认左上方, top-right 变体右上方 */
.stack-label-out {
  position: absolute;
  top: 2px;
  left: 4px;
  font-size: 9px;
  color: #fff;
  letter-spacing: 1px;
  font-weight: 700;
  white-space: nowrap;
  background: rgba(0, 0, 0, 0.72);
  padding: 1px 8px;
  border-radius: 8px;
  line-height: 1.4;
  pointer-events: none;
  z-index: 3;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.6);
}

.stack-label-out.top-right {
  left: auto;
  right: 4px;
}

.stack-label-out.bottom-left {
  top: auto;
  bottom: 2px;
  left: 4px;
}

.zone.base {
  border-color: var(--accent-green);
  background:
    repeating-linear-gradient(
      45deg,
      rgba(74, 222, 128, 0.03) 0 6px,
      transparent 6px 12px
    ),
    var(--bg-surface-2);
  /* 高度 = 纯 1 卡高, 6 个卡槽撑满; 标签挂在外边缘, 不占内部空间 */
  height: var(--card-h);
  width: calc((var(--card-h) * var(--card-aspect)) * 6 + var(--card-gap) * 5 + 8px);
  flex-shrink: 0;
  padding: 4px;
  justify-content: center;
  align-items: stretch;
  align-self: center;
  position: relative;
  overflow: visible; /* 允许 base-tag 超出容器绘制在边缘角落 */
  box-shadow: inset 0 0 0 1px rgba(74, 222, 128, 0.25), 0 4px 14px rgba(0, 0, 0, 0.15);
  border-radius: var(--radius-md);
}

/* 基地角落标签: 对手侧左下角外、我方侧右上角外, 不挤卡槽空间
 * 文字简化为 "基地"
 */
.base-tag {
  position: absolute;
  font-size: 9px;
  color: var(--accent-green);
  font-weight: 700;
  letter-spacing: 1px;
  white-space: nowrap;
  background: var(--bg-surface-2);
  padding: 1px 8px;
  border: 1px solid var(--accent-green);
  border-radius: 6px;
  line-height: 1.4;
  pointer-events: none;
  z-index: 2;
  text-shadow: 0 1px 1px rgba(0, 0, 0, 0.4);
}

/* 4 个角落定位：基地标签放在“卡槽没填充的空侧”更自然
 * 我方基地: 卡槽左→右 1..6 填充 -> 空侧=右 -> 右下 right-bottom
 * 对方基地: 卡槽右→左 6..1 填充 -> 空侧=左 -> 左上 left-top
 * (旧定位 left-bottom / right-top 保留作复用)
 */
.base-tag.left-bottom {
  left: 4px;
  bottom: -10px;
  border-top-left-radius: 2px;
}
.base-tag.right-top {
  right: 4px;
  top: -10px;
  border-bottom-right-radius: 2px;
}
.base-tag.left-top {
  left: 4px;
  top: -10px;
  border-bottom-left-radius: 2px;
}
.base-tag.right-bottom {
  right: 4px;
  bottom: -10px;
  border-top-right-radius: 2px;
}

/* 卡组块: 两段式 (信息带 + 卡背), 信息带一行放 标签+剩余数+摘要, 完全不压卡面
 * 对手侧: 信息带上 / 卡背下
 * 我方侧: 卡背上 / 信息带下
 */
/* ============================================
   卡组块: 蓝灰色系卡槽 (和虚空/撤退区统一, 和基地绿色区分)
   ============================================ */
.zone.deck {
  position: relative;
  flex: 1 1 0;
  min-height: 0;
  width: 100%;
  max-width: 100%;
  overflow: visible;
  border-radius: var(--radius-md);
  border: 2px solid rgba(129, 140, 248, 0.65);
  background:
    repeating-linear-gradient(
      45deg,
      rgba(129, 140, 248, 0.05) 0 6px,
      transparent 6px 12px
    ),
    var(--bg-surface-2);
  box-shadow:
    inset 0 0 0 1px rgba(129, 140, 248, 0.3),
    inset 0 2px 6px rgba(0, 0, 0, 0.4),
    0 4px 14px rgba(0, 0, 0, 0.2);
}

/* 冲击卡组: 琥珀色主题, 打横放, flex-shrink:0 让时间线吃剩余高度 */
.zone.deck.rush-deck {
  flex: 0 0 auto;
  height: auto;
  aspect-ratio: var(--card-aspect-inv);
  border-color: rgba(251, 146, 60, 0.7);
  background:
    repeating-linear-gradient(
      45deg,
      rgba(251, 146, 60, 0.04) 0 6px,
      transparent 6px 12px
    ),
    var(--bg-surface-2);
  box-shadow:
    inset 0 0 0 1px rgba(251, 146, 60, 0.25),
    inset 0 2px 6px rgba(0, 0, 0, 0.4),
    0 4px 14px rgba(0, 0, 0, 0.2);
}

/* 堆叠区卡面: Card 组件需要居中绝对定位 */
.stack-face :deep(.card-face.stacked) {
  position: absolute;
  inset: 4px;
  border-radius: var(--radius-sm);
  border: 2px solid var(--border);
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

/* 基地区：6 个卡槽位，高度 = 1 卡高，宽度 = 6 卡宽 + 5 gap */
.base-row {
  display: flex;
  gap: var(--card-gap);
  height: 100%;
  flex-shrink: 0;
  align-items: stretch;
}

/* 对方卡槽：右对齐镜像 (slot 顺序 6→1) */
.base-row.row-reverse {
  flex-direction: row-reverse;
}

.base-slot {
  height: 100%;
  aspect-ratio: var(--card-aspect);
  background:
    radial-gradient(circle at 50% 30%, rgba(74, 222, 128, 0.12), transparent 60%),
    var(--bg-base);
  border: 1.5px solid rgba(74, 222, 128, 0.5);
  border-radius: var(--radius-sm);
  flex-shrink: 0;
  position: relative;
  box-shadow: inset 0 2px 6px rgba(0, 0, 0, 0.25);
  transition: border-color 0.2s, background 0.2s;
}

/* 加个空槽序号底纹，视觉不那么空 */
.base-slot::before {
  content: attr(data-slot);
  position: absolute;
  bottom: 2px;
  right: 4px;
  font-size: 9px;
  color: rgba(74, 222, 128, 0.4);
  font-weight: 700;
  letter-spacing: 1px;
  pointer-events: none;
}

/* ============================================
   菱形战区 - 在中列内部占满剩余空间，前后上下布局
   ============================================ */
.diamond {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-sm);
  min-height: 0;
  min-width: 0;
  width: 100%;
  flex: 1 1 0;  /* 在中列占满基地之外的剩余空间 */
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

/* 菱形卡位包装: 标签外置 - 对手侧标签在槽上、我方侧标签在槽下
 * 结构: cell-wrap = label-out (外置标签) + slot (卡槽本身) + 可选 power/attach
 * 卡槽内部不叠标签, 仅保留 power/结附点的角落小徽章, 且不卡主卡面核心区
 */
.cell-wrap {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  gap: 2px;
  min-width: 0;
}

/* 对手侧 (默认): 标签在上, 卡槽在下 */
.cell-wrap .cell-label-out { order: 0; }
.cell-wrap .slot         { order: 1; }

/* 我方侧 (加 .local): 卡槽在上, 标签在下 */
.cell-wrap.local {
  justify-content: flex-start;
}
.cell-wrap.local .cell-label-out { order: 2; }
.cell-wrap.local .slot         { order: 0; }

/* 外置标签: 完全不进入卡槽区域, 因此不遮挡卡面 */
.cell-label-out {
  font-size: 9px;
  color: #fff;
  letter-spacing: 1px;
  font-weight: 700;
  white-space: nowrap;
  background: rgba(0, 0, 0, 0.7);
  padding: 1px 8px;
  border-radius: 8px;
  line-height: 1.4;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.6);
  pointer-events: none;
  flex-shrink: 0;
}

/* 卡位 - 统一使用卡牌素材比例 747:1042 */
.slot {
  width: 80px;
  aspect-ratio: var(--card-aspect);
  background: var(--bg-surface);
  border: 2px solid var(--border);
  border-radius: var(--radius-md);
  position: relative;
  /* 卡槽内的 power/结附 小徽章角落放置, 避开中央主图案区 (各留 4px 边) */
  padding: 4px;
}

/* 空槽统一虚线 */
.slot.empty {
  border-style: dashed;
  border-color: var(--border-light);
}

/* 卡槽内角落小徽章: 实时战力 (左上) + 结附卡计数 (右下)
 * 尺寸极小且靠四角, 不遮挡卡面中央主要插画
 */
.cell-power {
  position: absolute;
  top: 2px;
  left: 2px;
  min-width: 14px;
  height: 14px;
  padding: 0 3px;
  background: var(--accent-red);
  color: #fff;
  font-size: 9px;
  font-weight: 700;
  line-height: 14px;
  text-align: center;
  border-radius: 4px;
  pointer-events: none;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.6);
}

.cell-attach {
  position: absolute;
  bottom: 2px;
  right: 2px;
  min-width: 14px;
  height: 14px;
  padding: 0 3px;
  background: var(--accent-blue);
  color: #fff;
  font-size: 9px;
  font-weight: 700;
  line-height: 14px;
  text-align: center;
  border-radius: 4px;
  pointer-events: none;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.6);
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

/* ============================================
   亮色主题覆盖：确保虚空区/撤退区/卡组在浅色背景下清晰可见
   ============================================ */
/* 虚空区/撤退区标签亮色主题 */
[data-theme="light"] .zone-tag {
  background: #5C6BC0;
  border-color: rgba(92, 107, 192, 0.8);
  color: #fff;
}

/* 虚空区/撤退区背景亮色主题 */
[data-theme="light"] .zone.stack-zone {
  background:
    repeating-linear-gradient(
      45deg,
      rgba(129, 140, 248, 0.08) 0 6px,
      transparent 6px 12px
    ),
    #E8EAF6;
  box-shadow:
    inset 0 0 0 1px rgba(92, 107, 192, 0.5),
    inset 0 2px 6px rgba(0, 0, 0, 0.15),
    0 4px 14px rgba(0, 0, 0, 0.1);
}

/* 卡组块背景亮色主题 */
[data-theme="light"] .zone.deck {
  background:
    repeating-linear-gradient(
      45deg,
      rgba(129, 140, 248, 0.08) 0 6px,
      transparent 6px 12px
    ),
    #E8EAF6;
  box-shadow:
    inset 0 0 0 1px rgba(92, 107, 192, 0.5),
    inset 0 2px 6px rgba(0, 0, 0, 0.15),
    0 4px 14px rgba(0, 0, 0, 0.1);
}

/* 冲击卡组背景亮色主题 */
[data-theme="light"] .zone.deck.rush-deck {
  background:
    repeating-linear-gradient(
      45deg,
      rgba(251, 146, 60, 0.08) 0 6px,
      transparent 6px 12px
    ),
    #FFF3E0;
  box-shadow:
    inset 0 0 0 1px rgba(251, 146, 60, 0.5),
    inset 0 2px 6px rgba(0, 0, 0, 0.15),
    0 4px 14px rgba(0, 0, 0, 0.1);
}

/* 冲击卡组标签橙色 */
.zone-tag.rush {
  background: #FB8C00;
  border-color: rgba(251, 140, 0, 0.8);
}

/* 基地标签亮色主题 */
[data-theme="light"] .base-tag {
  background: #fff;
  border-color: var(--accent-green);
  color: var(--accent-green);
}

/* 基地区背景亮色主题 */
[data-theme="light"] .zone.base {
  background:
    repeating-linear-gradient(
      45deg,
      rgba(74, 222, 128, 0.06) 0 6px,
      transparent 6px 12px
    ),
    #E8F5E9;
  box-shadow: inset 0 0 0 1px rgba(46, 125, 50, 0.4), 0 4px 14px rgba(0, 0, 0, 0.1);
}

/* 基地卡槽序号亮色主题 */
[data-theme="light"] .base-slot::before {
  color: rgba(46, 125, 50, 0.6);
}
</style>