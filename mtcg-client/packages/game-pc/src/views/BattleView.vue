<script setup lang="ts">
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useGameStore } from '@mtcg/common/stores'
import { Card } from '@mtcg/common/components'

const route = useRoute()
const router = useRouter()
const store = useGameStore()

function goHome(): void {
  router.push('/')
}

/* 全屏切换 */
const isFullscreen = ref(false)
const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
  } else {
    document.exitFullscreen()
  }
}
const onFullscreenChange = () => {
  isFullscreen.value = !!document.fullscreenElement
}
onMounted(async () => {
  document.addEventListener('fullscreenchange', onFullscreenChange)
  const raw = route.params.gameId
  const id = typeof raw === 'string' ? Number(raw) : Number(Array.isArray(raw) ? raw[0] : raw)
  if (Number.isFinite(id) && id > 0) {
    try {
      await store.loadGame(id)
    } catch {
      // notifier
    }
  }
})
onUnmounted(() => {
  document.removeEventListener('fullscreenchange', onFullscreenChange)
  store.stopPolling()
})

const turnCount = computed(() => store.gameState?.turnCount ?? 1)
const phaseLabel = computed(() => store.gameState?.currentPhase ?? '行动阶段')

/* 手牌扇形布局: 根据索引计算每张卡的旋转角度、水平偏移和弧度上抬 */
const handCount = 5

/* 区域卡牌数量 (TODO: 接入真实游戏状态) */
const opponentZones = { characterDeck: 38, rushDeck: 9, retreat: 2, crop: 1 }
const localZones = { characterDeck: 41, rushDeck: 9, retreat: 0, crop: 3 }

/* 战区槽位是否有卡 */
const opponentField = { flankL: false, vanguard: true, rear: false, flankR: false }
const localField = { flankL: false, vanguard: true, rear: true, flankR: false }
const fanCardStyle = (index: number, total: number): { transform: string; zIndex: string } => {
  if (total <= 1) return { transform: 'none', zIndex: '100' }
  const center = (total - 1) / 2
  const offset = index - center
  const maxAngle = 14
  const angle = (offset / center) * maxAngle
  const spacing = 50
  const xOffset = offset * spacing
  const arcLift = 5
  const yOffset = Math.abs(offset) * arcLift
  return {
    transform: `translateX(${xOffset}px) translateY(${yOffset}px) rotate(${angle}deg)`,
    zIndex: String(100 - Math.round(Math.abs(offset) * 10)),
  }
}
</script>

<template>
  <div class="battle-pc">
    <!-- 顶部 HUD: 返回(左) | 对手信息(居中) | 全屏(右) -->
    <header class="hud-top">
      <button class="btn-back" @click="goHome">←</button>
      <div class="opponent-info">
        <span class="opponent-avatar"></span>
        <div class="opponent-meta">
          <span class="opponent-name">{{ store.opponent?.playerId ?? '对手' }}</span>
          <span class="opponent-hand">手牌 {{ store.opponent?.handCount ?? 0 }}</span>
        </div>
      </div>
      <button
        class="btn-fullscreen"
        @click="toggleFullscreen"
        :title="isFullscreen ? '退出全屏' : '全屏'"
      >
        <!-- 全屏: 四角向外 -->
        <svg v-if="!isFullscreen" viewBox="0 0 16 16" width="14" height="14" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round">
          <path d="M2.5 5.5V2.5h3M13.5 5.5V2.5h-3M2.5 10.5v3h3M13.5 10.5v3h-3" />
        </svg>
        <!-- 退出全屏: 四角向内 -->
        <svg v-else viewBox="0 0 16 16" width="14" height="14" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round">
          <path d="M5.5 2.5v3h-3M10.5 2.5v3h3M5.5 13.5v-3h-3M10.5 13.5v-3h3" />
        </svg>
      </button>
    </header>

    <!-- 战区 - 手牌扇形浮在底部 -->
    <main class="canvas-area">
      <div class="battlefield">

        <!-- 对手区域一行 = 三列（相对我方镜像）：
             左列 = 角色卡组(1行/外) → 撤退区 → 裁剪区(靠中线)
             中列 = 基地(1行/外) → 菱形(靠中线)
             右列 = 冲击卡组(1行/外) → 时间线(靠中线) -->
        <div class="row-area opponent-area">
          <!-- 左列：角色卡组(第1行) -> 撤退区 -> 虚空区（靠中线） -->
          <div class="col col-left opponent">
            <!-- 角色卡组 -->
            <div class="zone deck opponent-deck">
              <span class="zone-tag bottom-right">角色·{{ opponentZones.characterDeck }}</span>
              <div class="stack-face" v-if="opponentZones.characterDeck > 0">
                <Card back-type="character" class="stacked" />
              </div>
            </div>
            <div class="zone stack-zone retreat-stack">
              <span class="zone-tag bottom-right">撤退·{{ opponentZones.retreat }}</span>
              <div class="stack-face" v-if="opponentZones.retreat > 0">
                <Card back-type="character" class="stacked" />
              </div>
            </div>
            <div class="zone stack-zone crop-stack">
              <span class="zone-tag bottom-right">虚空·{{ opponentZones.crop }}</span>
              <div class="stack-face" v-if="opponentZones.crop > 0">
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
            <!-- 对手半场菱形：3列 (侧翼 | 中央双槽位 | 侧翼), 中间列垂直堆叠先锋+后卫
     屏幕布局：
     ┌─────┐ ┌─────────┐ ┌─────┐
     │侧翼L│ │  后卫   │ │侧翼R│   ← 外缘
     ├─────┤ ├─────────┤ ├─────┤
     │     │ │  先锋   │ │     │   ← 内缘(靠近中线)
     └─────┘ └─────────┘ └─────┘
     实时属性已内嵌在卡面边缘 (等级/战力/攻距/结附)
-->
            <div class="diamond opponent">
              <!-- 第1列: 侧翼L -->
              <div class="diamond-col col-flank">
                <div class="battle-cell flank" data-zone="flank" data-zone-label="侧翼">
                  <div class="bc-slot" v-if="opponentField.flankL">
                    <Card :is-face-down="true" back-type="character" side="opponent" />
                  </div>
                </div>
              </div>

              <!-- 第2列: 中央槽位列 (后卫上+先锋下) -->
              <div class="diamond-col col-center-vanguard">
                <div class="battle-cell rear" data-zone="rear" data-zone-label="后卫">
                  <div class="bc-slot" v-if="opponentField.rear">
                    <Card :is-face-down="true" back-type="character" side="opponent" />
                  </div>
                </div>
                <div class="battle-cell vanguard" data-zone="vanguard" data-zone-label="先锋">
                  <div class="bc-slot has-card" v-if="opponentField.vanguard">
                    <Card :is-face-down="false" theme="character" side="opponent"
                      level="5" :power="15000" attack-range="2" />
                  </div>
                </div>
              </div>

              <!-- 第3列: 侧翼R -->
              <div class="diamond-col col-flank">
                <div class="battle-cell flank" data-zone="flank" data-zone-label="侧翼">
                  <div class="bc-slot" v-if="opponentField.flankR">
                    <Card :is-face-down="true" back-type="character" side="opponent" />
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 右列：冲击卡组(第一行=顶部外, 打横放节省高度) → 时间线(吃剩余高度, 3×3=9卡槽阵列) -->
          <div class="col col-right opponent">
            <div class="zone deck opponent-deck rush-deck">
              <span class="zone-tag bottom-right rush">冲击·{{ opponentZones.rushDeck }}</span>
              <Card v-if="opponentZones.rushDeck > 0" back-type="rush" horizontal />
            </div>
            <div class="zone timeline-zone">
              <span class="stack-label-out top-right">时间线</span>
              <div class="timeline-stack">
                <div class="timeline-slot" v-for="i in 9" :key="i" :data-slot="i" :style="{ '--idx': i - 1 }"></div>
              </div>
            </div>
          </div>
        </div>

        <!-- 中线: 居中状态药丸(回合+计时) | 右侧操作按钮(认输分隔防误触) -->
        <div class="row-midline">
          <span class="turn-badge">
            第{{ turnCount }}回合 · {{ phaseLabel }}
          </span>
          <div class="mid-actions">
            <button class="btn-phase">结束阶段</button>
            <button class="btn-end-turn">结束回合</button>
            <span class="action-divider"></span>
            <button class="btn-surrender">认输</button>
          </div>
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
              <span class="zone-tag top-left rush">冲击·{{ localZones.rushDeck }}</span>
              <Card v-if="localZones.rushDeck > 0" back-type="rush" horizontal />
            </div>
          </div>

          <!-- 中列：菱形作战区(靠中线) + 基地区(底部外边缘) -->
          <div class="col col-center">
            <!-- 我方半场菱形：3列 (侧翼 | 中央双槽位 | 侧翼), 中间列垂直堆叠先锋+后卫
     屏幕布局：
     ┌─────┐ ┌─────────┐ ┌─────┐
     │侧翼L│ │  先锋   │ │侧翼R│   ← 内缘(靠近中线)
     ├─────┤ ├─────────┤ ├─────┤
     │     │ │  后卫   │ │     │   ← 外缘(远离中线)
     └─────┘ └─────────┘ └─────┘
     实时属性已内嵌在卡面边缘 (等级/战力/攻距/结附)
-->
            <div class="diamond local">
              <!-- 第1列: 侧翼L -->
              <div class="diamond-col col-flank">
                <div class="battle-cell flank" data-zone="flank" data-zone-label="侧翼">
                  <div class="bc-slot" v-if="localField.flankL">
                    <Card :is-face-down="true" back-type="character" side="local" />
                  </div>
                </div>
              </div>

              <!-- 第2列: 中央槽位列 (先锋上+后卫下) -->
              <div class="diamond-col col-center-vanguard">
                <div class="battle-cell vanguard" data-zone="vanguard" data-zone-label="先锋">
                  <div class="bc-slot has-card has-attack" v-if="localField.vanguard">
                    <Card :is-face-down="false" theme="character" side="local"
                      level="3" :power="8000" attack-range="1" />
                  </div>
                </div>
                <div class="battle-cell rear" data-zone="rear" data-zone-label="后卫">
                  <div class="bc-slot has-card" v-if="localField.rear">
                    <Card :is-face-down="false" theme="character" side="local"
                      level="4" :power="24000" attack-range="3" />
                  </div>
                </div>
              </div>

              <!-- 第3列: 侧翼R -->
              <div class="diamond-col col-flank">
                <div class="battle-cell flank" data-zone="flank" data-zone-label="侧翼">
                  <div class="bc-slot" v-if="localField.flankR">
                    <Card :is-face-down="true" back-type="character" side="local" />
                  </div>
                </div>
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
              <span class="zone-tag top-left">虚空·{{ localZones.crop }}</span>
              <div class="stack-face" v-if="localZones.crop > 0">
                <Card back-type="character" class="stacked" />
              </div>
            </div>
            <div class="zone stack-zone retreat-stack">
              <span class="zone-tag top-left">撤退·{{ localZones.retreat }}</span>
              <div class="stack-face" v-if="localZones.retreat > 0">
                <Card back-type="character" class="stacked" />
              </div>
            </div>
            <div class="zone deck local-deck">
              <span class="zone-tag top-left">角色·{{ localZones.characterDeck }}</span>
              <div class="stack-face" v-if="localZones.characterDeck > 0">
                <Card back-type="character" class="stacked" />
              </div>
            </div>
          </div>
        </div>

      </div>
    </main>

    <!-- 底部 HUD: 扇形手牌 -->
    <footer class="hud-bottom">
      <div class="hand-fan">
        <div
          v-for="i in handCount"
          :key="i"
          class="hand-slot"
          :style="fanCardStyle(i - 1, handCount)"
        >
          <div class="hand-card-inner">
            <Card :is-face-down="true" back-type="character" side="local" />
          </div>
        </div>
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
  --slot-inset: inset 0 1px 0 color-mix(in srgb, var(--text-primary) 6%, transparent),
    inset 0 2px 8px rgba(0, 0, 0, 0.28);
}

/* ============================================
   顶部 HUD
   ============================================ */
.hud-top {
  flex-shrink: 0;
  height: 44px;
  display: flex;
  align-items: center;
  padding: 0 var(--space-md);
  background: color-mix(in srgb, var(--bg-surface) 78%, transparent);
  border-bottom: 1px solid var(--border);
  position: relative;
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

/* 全屏按钮: margin-left:auto 推到最右 */
.btn-fullscreen {
  margin-left: auto;
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  color: var(--text-secondary);
  cursor: pointer;
  padding: 0;
}

.btn-fullscreen:hover {
  border-color: var(--accent);
  color: var(--accent);
}

/* 对手信息: 绝对居中, 头像 + 名字 + 手牌数 */
.opponent-info {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 3px 14px 3px 4px;
  background: var(--bg-surface-2);
  border: 1px solid var(--border);
  border-radius: 20px;
}

.opponent-avatar {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  background: var(--accent-blue);
  border: 2px solid var(--bg-surface);
  flex-shrink: 0;
}

.opponent-meta {
  display: flex;
  align-items: center;
  gap: 6px;
  line-height: 1.2;
}

.opponent-name {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-primary);
}

.opponent-hand {
  font-size: 11px;
  color: var(--text-secondary);
  padding-left: 6px;
  border-left: 1px solid var(--border);
}

/* ============================================
   战区 - 6 段上下排列，每段高度固定 = 不重叠关键
   ============================================ */
.canvas-area {
  flex: 1;
  min-height: 0;
  padding: var(--space-md);
  overflow: auto;          /* 窗口过小时出现滚动条而非堆叠 */
  position: relative;
  isolation: isolate;
}

.canvas-area::before {
  content: '';
  position: absolute;
  inset: 0;
  z-index: 0;
  background: var(--shell-art) center 28% / cover no-repeat;
  opacity: 0.22;
  filter: var(--shell-art-filter);
  pointer-events: none;
}

.canvas-area::after {
  content: '';
  position: absolute;
  inset: 0;
  z-index: 0;
  background: color-mix(in srgb, var(--bg-base) 62%, transparent);
  pointer-events: none;
}

.battlefield {
  position: relative;
  z-index: 1;
  height: 100%;
  min-width: 600px;
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
/* 中列给菱形足够空间(菱形宽 ~498px + 基地) */
.row-area {
  flex: 1 1 0;
  min-height: 0;
  min-width: 580px;
  display: grid;
  grid-template-columns: var(--col-w) minmax(304px, 1fr) var(--col-w);
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
.col-center { justify-content: space-between; align-items: stretch; }

/* 菱形在 col-center 内 水平居中 (align-self 在 flex 列容器里 = 水平方向) */
.col-center > .diamond { align-self: center; margin: 0 auto; }

/* 右列整体对齐方向：
 *   对手右列 = flex-start => 冲击卡组顶(第一行) → 时间线(靠中线底)
 *   我方右列 = flex-end   => 裁剪(靠中线顶) → 撤退 → 角色卡组(最后一行=底部外边缘)
 */
.col-right.opponent { justify-content: flex-start; }
.col-right.local    { justify-content: flex-end; }

/* 中线: flex 两端对齐, 回合药丸居中, 操作组靠右 */
.row-midline {
  flex-shrink: 0;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 var(--space-lg);
  background: var(--bg-surface);
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  position: relative;
}

/* 回合信息药丸: 绝对居中, 蓝色圆角 */
.turn-badge {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  padding: 4px 18px;
  background: var(--accent);
  color: var(--accent-contrast);
  border-radius: 14px;
  font-size: 12px;
  font-weight: 600;
  white-space: nowrap;
  letter-spacing: 0.5px;
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 药丸内分隔点 */
.badge-sep {
  opacity: 0.5;
  margin: 0 2px;
}

/* 药丸内计时: 等宽数字 */
.badge-time {
  font-variant-numeric: tabular-nums;
  font-weight: 500;
  opacity: 0.85;
}

/* 右侧操作组: 纯按钮, 无信息 */
.mid-actions {
  position: absolute;
  right: var(--space-lg);
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-phase {
  height: 26px;
  padding: 0 12px;
  background: transparent;
  color: var(--text-secondary);
  border: 1px solid transparent;
  border-radius: var(--radius-sm);
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: color var(--transition-fast), border-color var(--transition-fast);
}

.btn-phase:hover {
  border-color: var(--accent);
  color: var(--accent);
}

.btn-end-turn {
  height: 26px;
  padding: 0 16px;
  background: var(--accent);
  color: var(--accent-contrast);
  border: none;
  border-radius: var(--radius-sm);
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: border-color var(--transition-fast), filter var(--transition-fast);
}

.btn-end-turn:hover {
  filter: brightness(1.06);
}

/* 分隔线: 视觉隔离认输按钮, 防误触 */
.action-divider {
  width: 1px;
  height: 16px;
  background: var(--border);
  margin: 0 4px;
}

.btn-surrender {
  height: 26px;
  padding: 0 12px;
  background: transparent;
  color: var(--accent-red, #ef4444);
  border: 1px solid var(--accent-red, #ef4444);
  border-radius: var(--radius-sm);
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.15s ease;
}

.btn-surrender:hover {
  background: var(--accent-red, #ef4444);
  color: #fff;
}

/* ============================================
   通用区域（zone）
   ============================================ */
.zone {
  position: relative;
  background: var(--bg-surface);
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4px;
  min-width: 0;
  min-height: 0;
}

/* 虚空 / 撤退：刻入桌面的槽，不用斜纹私货色 */
.zone.stack-zone {
  flex: 1 1 0;
  min-height: 0;
  width: 100%;
  max-width: 100%;
  overflow: visible;
  border-radius: var(--radius-md);
  border: 1px solid var(--border);
  background: var(--bg-surface-2);
  box-shadow: var(--slot-inset);
}

/* 外置区域标签: 靛色系, 挂在卡槽外边缘
   暗色主题: 浅紫蓝底 + 浅色文字
   亮色主题: 靛蓝色底 + 白色文字 */
.zone-tag {
  position: absolute;
  font-size: 11px;
  color: var(--text-secondary);
  font-weight: 700;
  letter-spacing: 0.04em;
  white-space: nowrap;
  background: var(--bg-surface);
  padding: 1px 8px;
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  line-height: 1.4;
  pointer-events: none;
  z-index: 2;
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
  background: var(--bg-surface-2);
  border-color: var(--border);
  box-shadow: var(--slot-inset);
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
  background: var(--bg-base);
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  box-shadow: var(--slot-inset);
}
.timeline-slot::after {
  content: attr(data-slot);
  position: absolute;
  bottom: 1px;
  right: 2px;
  font-size: 9px;
  letter-spacing: 0.04em;
  color: var(--text-disabled);
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
  font-size: 11px;
  color: var(--text-secondary);
  letter-spacing: 0.04em;
  font-weight: 700;
  white-space: nowrap;
  background: var(--bg-surface);
  padding: 1px 8px;
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  line-height: 1.4;
  pointer-events: none;
  z-index: 3;
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
  border-color: var(--border);
  background: var(--bg-surface-2);
  height: var(--card-h);
  width: calc((var(--card-h) * var(--card-aspect)) * 6 + var(--card-gap) * 5 + 8px);
  flex-shrink: 0;
  padding: 4px;
  justify-content: center;
  align-items: stretch;
  align-self: center;
  position: relative;
  overflow: visible;
  box-shadow: var(--slot-inset);
  border-radius: var(--radius-md);
}

/* 基地角落标签: 对手侧左下角外、我方侧右上角外, 不挤卡槽空间
 * 文字简化为 "基地"
 */
.base-tag {
  position: absolute;
  font-size: 11px;
  color: var(--text-secondary);
  font-weight: 700;
  letter-spacing: 0.04em;
  white-space: nowrap;
  background: var(--bg-surface);
  padding: 1px 8px;
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  line-height: 1.4;
  pointer-events: none;
  z-index: 2;
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
  border: 1px solid var(--border);
  background: var(--bg-surface-2);
  box-shadow: var(--slot-inset);
}

/* 冲击卡组：金边区分，仍走 token */
.zone.deck.rush-deck {
  flex: 0 0 auto;
  height: auto;
  aspect-ratio: var(--card-aspect-inv);
  border-color: color-mix(in srgb, var(--accent-gold) 55%, var(--border));
  background: var(--bg-surface-2);
  box-shadow: var(--slot-inset);
}

/* 堆叠区卡面: Card 组件需要居中绝对定位 */
.stack-face :deep(.card-face.stacked) {
  position: absolute;
  inset: 4px;
  border-radius: var(--radius-sm);
  border: 2px solid var(--border);
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
  background: var(--bg-base);
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  flex-shrink: 0;
  position: relative;
  box-shadow: var(--slot-inset);
  transition: border-color var(--transition-fast);
}

.base-slot::before {
  content: attr(data-slot);
  position: absolute;
  bottom: 2px;
  right: 4px;
  font-size: 10px;
  color: var(--text-disabled);
  font-weight: 700;
  letter-spacing: 0.04em;
  pointer-events: none;
}

/* ============================================
   菱形战区 - 3列布局
   - 第1列: 侧翼
   - 第2列: 中央 (垂直堆叠 2 槽位: 先锋 + 后卫)
   - 第3列: 侧翼
   列宽: 单个 battle-cell = 卡宽76 + padding8 = 84px
   3列总宽 84*3+24=276px
   ============================================ */
.diamond {
  display: grid;
  grid-template-columns: 84px 84px 84px;
  gap: 14px;
  align-items: center;
  justify-content: center;
  min-height: 0;
  min-width: 304px;
  flex: 0 0 auto;
  padding: 6px 8px;
  margin: 0 auto;
  position: relative;
}

/* 第2列(中央): 垂直堆叠 双槽位 */
.diamond-col.col-center-vanguard {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 14px;
  min-width: 0;
  min-height: 0;
}

/* 第1/3列(侧翼): 单槽位 居中 */
.diamond-col.col-flank {
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 0;
  min-height: 0;
}

/* ============================================
   战斗单元: 仅包含卡牌, 居中对齐
   ============================================ */
.battle-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  width: 84px;
  height: 108px;
  border-radius: 6px;
  background: transparent;
  border: none;
  box-shadow: none;
}

/* 空槽位: 虚线占位 + 位置名称 */
.battle-cell:not(:has(.bc-slot))::before {
  content: attr(data-zone-label);
  position: absolute;
  inset: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px dashed var(--border);
  border-radius: var(--radius-sm);
  background: var(--bg-surface-2);
  box-shadow: var(--slot-inset);
  font-size: 11px;
  color: var(--text-disabled);
  font-weight: 600;
  letter-spacing: 0.04em;
  pointer-events: none;
}

.battle-cell .bc-slot:not(.has-card)::before {
  content: attr(data-zone-label);
  position: absolute;
  inset: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px dashed var(--border);
  border-radius: var(--radius-sm);
  font-size: 11px;
  color: var(--text-disabled);
  font-weight: 600;
  letter-spacing: 0.04em;
  pointer-events: none;
}

.battle-cell .bc-slot {
  border-radius: 5px;
  flex-shrink: 0;
}

/* ============================================
   卡槽容器
   ============================================ */
.bc-slot {
  position: relative;
  width: 76px;
  height: 108px;     /* 747:1042 ≈ 0.717 → 76x106 ≈ 实际显示 */
  border-radius: 5px;
  flex-shrink: 0;
  transition: transform 0.15s, filter 0.15s;
}

/* Card 组件绝对定位在 bc-slot 内 */
.bc-slot :deep(.card-face) {
  border-radius: 5px;
}

/* 整槽 hover 抬起 */
.battle-cell:hover .bc-slot {
  transform: translateY(-3px);
}

.bc-slot.has-attack::before {
  content: '';
  position: absolute;
  inset: -3px;
  border-radius: 7px;
  border: 2px solid var(--accent-red);
  box-shadow: var(--shadow-glow);
  z-index: 1;
  pointer-events: none;
}

/* 实时属性徽章已移至 Card.vue 组件内, 通过 level/power/attackRange/attachCount 属性渲染 */


/* ============================================
   底部 HUD: 扇形手牌
   ============================================ */
.hud-bottom {
  flex-shrink: 0;
  height: 118px;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  padding: 0 0 10px;
  background: transparent;
  overflow: visible;
}

/* 手牌扇形容器 */
.hand-fan {
  position: relative;
  width: 280px;
  height: 100px;
}

.hand-slot {
  position: absolute;
  bottom: 0;
  left: 50%;
  width: 72px;
  height: 100px;
  margin-left: -36px;
  transform-origin: bottom center;
  transition: transform 0.25s ease;
}

/* 内层: hover 时上浮 + 放大, 与外层 transform 分离 */
.hand-card-inner {
  width: 100%;
  height: 100%;
  position: relative;
  transition: transform 0.25s cubic-bezier(0.2, 0, 0.2, 1.2), filter 0.25s ease;
}

.hand-slot:hover {
  z-index: 999 !important;
}

.hand-slot:hover .hand-card-inner {
  transform: translateY(-28px) scale(1.22);
}

.hand-card-inner :deep(.card-face) {
  border-radius: 4px;
}

.zone-tag.rush {
  color: var(--accent-gold);
  border-color: color-mix(in srgb, var(--accent-gold) 45%, var(--border));
}
</style>