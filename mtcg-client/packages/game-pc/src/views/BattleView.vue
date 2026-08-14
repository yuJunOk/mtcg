<script setup lang="ts">
import { computed, ref, watch, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import type { CardInstanceVO, Zone } from '@mtcg/common'
import { useGameStore } from '@mtcg/common/stores'
import { Card } from '@mtcg/common/components'
import BattleFieldCard from '@/components/battle/BattleFieldCard.vue'
import { confirm } from '@/feedback'
import { NButton } from 'naive-ui'

const route = useRoute()
const router = useRouter()
const store = useGameStore()

/** 每个「行动窗口」（回合+阶段+当前玩家）时限；超时自动结束阶段，防止长考 */
const TURN_LIMIT_SEC = 60
const CLOCK_TICK_MS = 200
const WARN_SEC = 15
const DANGER_SEC = 5

/** 操作意图（可选；手牌点选后点目标也可直接推断） */
type PendingMode = 'BASE_DEPLOY' | 'SUMMON' | 'ATTACK' | null

interface FieldPick {
  zone: Zone
  index: number
  cardCode: string
  instanceId: string
}

const PHASE_ORDER = [
  'TURN_START',
  'DRAW',
  'ACTION',
  'COMBAT',
  'RESPONSE',
  'TURN_END',
] as const

const PHASE_LABELS: Record<string, string> = {
  TURN_START: '开始',
  DRAW: '抽卡',
  ACTION: '行动',
  COMBAT: '战斗',
  RESPONSE: '应答',
  TURN_END: '结束',
}

const PHASE_FULL: Record<string, string> = {
  TURN_START: '回合开始',
  DRAW: '抽卡阶段',
  ACTION: '行动阶段',
  COMBAT: '战斗阶段',
  RESPONSE: '应答阶段',
  TURN_END: '回合结束',
}

// ========== 状态 ==========
const isFullscreen = ref(false)
const selectedHandId = ref<string | null>(null)
const selectedHandCode = ref<string | null>(null)
const selectedField = ref<FieldPick | null>(null)
const pendingMode = ref<PendingMode>(null)
const actionError = ref<string | null>(null)

/** 当前行动窗口剩余秒（展示用，含小数平滑） */
const clockRemainSec = ref(TURN_LIMIT_SEC)
const clockDeadlineAt = ref(0)
const autoEnding = ref(false)

let clockTimer: ReturnType<typeof setInterval> | null = null

// ========== 计算属性 ==========
const turnCount = computed(() => store.gameState?.turnCount ?? 0)
const currentPhase = computed(() => store.gameState?.currentPhase ?? '')
const phaseLabel = computed(() => PHASE_FULL[currentPhase.value] ?? '—')
const phaseIndex = computed(() =>
  PHASE_ORDER.indexOf(currentPhase.value as (typeof PHASE_ORDER)[number]),
)

/** 时钟重置键：回合 / 阶段 / 当前玩家任一变化即重开计时 */
const clockWindowKey = computed(() => {
  const gs = store.gameState
  if (!gs || gs.status === 'FINISHED') return ''
  return `${gs.turnCount}|${gs.currentPhase}|${gs.activePlayerId}`
})

const clockDisplay = computed(() => {
  const s = Math.max(0, Math.ceil(clockRemainSec.value))
  const m = Math.floor(s / 60)
  const r = s % 60
  return `${m}:${r.toString().padStart(2, '0')}`
})

const clockRatio = computed(() =>
  Math.min(1, Math.max(0, clockRemainSec.value / TURN_LIMIT_SEC)),
)

const clockUrgent = computed(() => {
  if (store.isGameOver || !clockWindowKey.value) return ''
  if (clockRemainSec.value <= DANGER_SEC) return 'danger'
  if (clockRemainSec.value <= WARN_SEC) return 'warn'
  return ''
})

/** CSS 圆锥进度（不用 SVG 图标） */
const clockRingStyle = computed(() => {
  const pct = Math.round(clockRatio.value * 100)
  let color = 'var(--accent)'
  if (!store.isLocalPlayerActive) color = 'var(--accent-blue)'
  if (clockUrgent.value === 'warn') color = 'var(--accent-gold)'
  if (clockUrgent.value === 'danger') color = 'var(--accent-red)'
  return {
    background: `conic-gradient(${color} ${pct}%, var(--border) 0)`,
  }
})

const canAct = computed(
  () => store.isLocalPlayerActive && !store.loading && !store.isGameOver,
)

const hasEndPhase = computed(() => store.availableActions.includes('END_PHASE'))
const hasSurrender = computed(() => store.availableActions.includes('SURRENDER'))
const hasBaseDeploy = computed(() => store.availableActions.includes('BASE_DEPLOY'))
const hasSummon = computed(() => store.availableActions.includes('SUMMON'))
const hasAttack = computed(() => store.availableActions.includes('ATTACK'))

const localHand = computed(() => store.localPlayer?.hand ?? [])
const handCount = computed(() => localHand.value.length)

const opponentZones = computed(() => ({
  characterDeck: store.opponent?.deckCount ?? 0,
  rushDeck: store.opponent?.rushDeckCount ?? 0,
  retreat: store.opponent?.retreat?.length ?? 0,
  voidZone: store.opponent?.voidZone?.length ?? 0,
}))

const localZones = computed(() => ({
  characterDeck: store.localPlayer?.deckCount ?? 0,
  rushDeck: store.localPlayer?.rushDeckCount ?? 0,
  retreat: store.localPlayer?.retreat?.length ?? 0,
  voidZone: store.localPlayer?.voidZone?.length ?? 0,
}))

const opponentField = computed(() => {
  const f = store.opponent?.field
  return {
    vanguard: f?.vanguard ?? null,
    flankL: f?.flank?.[0] ?? null,
    flankR: f?.flank?.[1] ?? null,
    rearguard: f?.rearguard ?? null,
    base: padSlots(f?.base, 6),
  }
})

const localField = computed(() => {
  const f = store.localPlayer?.field
  return {
    vanguard: f?.vanguard ?? null,
    flankL: f?.flank?.[0] ?? null,
    flankR: f?.flank?.[1] ?? null,
    rearguard: f?.rearguard ?? null,
    base: padSlots(f?.base, 6),
  }
})

const opponentTimeline = computed(() => padSlots(store.opponent?.timeline, 9))
const localTimeline = computed(() => padSlots(store.localPlayer?.timeline, 9))

const opponentTimelineFilled = computed(
  () => opponentTimeline.value.filter((c) => !!c).length,
)
const localTimelineFilled = computed(
  () => localTimeline.value.filter((c) => !!c).length,
)

const resultLabel = computed(() => {
  if (!store.isGameOver) return ''
  if (store.gameState?.winner === 'DRAW') return '平局'
  return store.isLocalPlayerWinner ? '胜利' : '失败'
})

const hintText = computed(() => {
  if (actionError.value) return actionError.value
  if (autoEnding.value) return '超时，自动结束阶段…'
  if (
    store.isLocalPlayerActive &&
    !store.isGameOver &&
    clockUrgent.value === 'danger'
  ) {
    return '即将超时，将自动结束阶段'
  }
  if (!canAct.value) return store.isLocalPlayerActive ? '' : '等待对手行动'
  if (pendingMode.value === 'ATTACK' && selectedField.value) return '点选敌方目标'
  if (pendingMode.value === 'ATTACK') return '点选己方攻击角色'
  if (selectedHandCode.value) {
    if (pendingMode.value === 'BASE_DEPLOY') return '点选基地空位盖放'
    if (pendingMode.value === 'SUMMON') return '点选战区或基地空位'
    return '点选放置位置'
  }
  return '选择手牌或行动'
})

// ========== 方法 ==========
function padSlots(
  arr: (CardInstanceVO | null)[] | null | undefined,
  len: number,
): (CardInstanceVO | null)[] {
  const src = arr ?? []
  const out: (CardInstanceVO | null)[] = []
  for (let i = 0; i < len; i++) {
    out.push(src[i] ?? null)
  }
  return out
}

function clearSelection(): void {
  selectedHandId.value = null
  selectedHandCode.value = null
  selectedField.value = null
  pendingMode.value = null
}

function goHome(): void {
  router.push('/')
}

function toggleFullscreen(): void {
  if (!document.fullscreenElement) {
    void document.documentElement.requestFullscreen()
  } else {
    void document.exitFullscreen()
  }
}

function onFullscreenChange(): void {
  isFullscreen.value = !!document.fullscreenElement
}

function setPendingMode(mode: PendingMode): void {
  if (!canAct.value) return
  pendingMode.value = pendingMode.value === mode ? null : mode
  if (mode !== 'ATTACK') {
    selectedField.value = null
  }
  if (mode === 'ATTACK') {
    selectedHandId.value = null
    selectedHandCode.value = null
  }
}

function selectHand(card: CardInstanceVO): void {
  if (!canAct.value) return
  if (selectedHandId.value === card.instanceId) {
    selectedHandId.value = null
    selectedHandCode.value = null
    return
  }
  selectedHandId.value = card.instanceId
  selectedHandCode.value = card.cardCode
  selectedField.value = null
  if (pendingMode.value === 'ATTACK') {
    pendingMode.value = null
  }
}

async function runAction(
  dto: Parameters<typeof store.doAction>[0],
): Promise<void> {
  actionError.value = null
  try {
    await store.doAction(dto)
    clearSelection()
  } catch {
    // HTTP 业务错误已由全局 notifier 弹 Toast；此处只更新局内提示
    actionError.value = store.error ?? '操作失败'
  }
}

async function onEndPhase(): Promise<void> {
  if (!canAct.value || !hasEndPhase.value) return
  await runAction({ actionType: 'END_PHASE' })
}

async function onSurrender(): Promise<void> {
  if (!hasSurrender.value || store.loading || store.isGameOver) return
  const ok = await confirm({
    title: '认输',
    content: '确认认输？对局将立即结束。',
    positiveText: '认输',
    danger: true,
  })
  if (!ok) return
  await runAction({ actionType: 'SURRENDER' })
}

async function tryBaseDeploy(targetIndex: number): Promise<void> {
  if (!selectedHandCode.value || !hasBaseDeploy.value) return
  await runAction({
    actionType: 'BASE_DEPLOY',
    cardCode: selectedHandCode.value,
    targetZone: 'BASE',
    targetIndex,
  })
}

async function trySummon(targetZone: Zone, targetIndex: number): Promise<void> {
  if (!selectedHandCode.value || !hasSummon.value) return
  await runAction({
    actionType: 'SUMMON',
    cardCode: selectedHandCode.value,
    targetZone,
    targetIndex,
  })
}

async function onLocalBaseSlotClick(index: number): Promise<void> {
  if (!canAct.value || !selectedHandCode.value) return
  const slot = localField.value.base[index]
  if (slot) return

  if (pendingMode.value === 'SUMMON' && hasSummon.value) {
    await trySummon('BASE', index)
    return
  }
  if (pendingMode.value === 'BASE_DEPLOY' && hasBaseDeploy.value) {
    await tryBaseDeploy(index)
    return
  }
  if (hasBaseDeploy.value) {
    await tryBaseDeploy(index)
    return
  }
  if (hasSummon.value) {
    await trySummon('BASE', index)
  }
}

async function onLocalFieldSlotClick(
  zone: Zone,
  index: number,
  card: CardInstanceVO | null,
): Promise<void> {
  if (!canAct.value) return

  if (selectedHandCode.value && !card && hasSummon.value) {
    await trySummon(zone, index)
    return
  }

  if (card && card.cardCode && (hasAttack.value || pendingMode.value === 'ATTACK')) {
    if (
      selectedField.value?.instanceId === card.instanceId &&
      pendingMode.value === 'ATTACK'
    ) {
      selectedField.value = null
      return
    }
    selectedHandId.value = null
    selectedHandCode.value = null
    selectedField.value = {
      zone,
      index,
      cardCode: card.cardCode,
      instanceId: card.instanceId,
    }
    pendingMode.value = 'ATTACK'
  }
}

async function onOpponentFieldSlotClick(
  zone: Zone,
  index: number,
  card: CardInstanceVO | null,
): Promise<void> {
  if (!canAct.value || !card?.cardCode) return
  if (!hasAttack.value || !selectedField.value) return

  await runAction({
    actionType: 'ATTACK',
    cardCode: selectedField.value.cardCode,
    sourceZone: selectedField.value.zone,
    sourceIndex: selectedField.value.index,
    targetZone: zone,
    targetIndex: index,
    targetCardCode: card.cardCode,
  })
}

function isFieldSelected(instanceId: string | undefined): boolean {
  return !!instanceId && selectedField.value?.instanceId === instanceId
}

function fanCardStyle(
  index: number,
  total: number,
): { transform: string; zIndex: string } {
  if (total <= 1) return { transform: 'translateY(0)', zIndex: '10' }
  const center = (total - 1) / 2
  const offset = index - center
  const maxAngle = Math.min(10, 36 / Math.max(total - 1, 1))
  const angle = (offset / Math.max(center, 0.5)) * maxAngle
  const y = Math.abs(offset) * 2
  return {
    transform: `rotate(${angle}deg) translateY(${y}px)`,
    zIndex: String(10 + index),
  }
}

function resetClock(): void {
  autoEnding.value = false
  if (!clockWindowKey.value) {
    clockRemainSec.value = 0
    clockDeadlineAt.value = 0
    return
  }
  clockDeadlineAt.value = Date.now() + TURN_LIMIT_SEC * 1000
  clockRemainSec.value = TURN_LIMIT_SEC
}

function tickClock(): void {
  if (!clockWindowKey.value || store.isGameOver) {
    clockRemainSec.value = 0
    return
  }
  if (!clockDeadlineAt.value) {
    resetClock()
    return
  }
  const left = (clockDeadlineAt.value - Date.now()) / 1000
  clockRemainSec.value = Math.max(0, left)
  if (left <= 0) {
    void onClockTimeout()
  }
}

async function onClockTimeout(): Promise<void> {
  if (autoEnding.value || store.isGameOver || store.loading) return
  // 仅本方行动窗口强制推进；对手侧只展示耗尽，等轮询同步
  if (!store.isLocalPlayerActive) return
  if (!store.availableActions.includes('END_PHASE')) return
  autoEnding.value = true
  clearSelection()
  try {
    await store.doAction({ actionType: 'END_PHASE' })
  } catch {
    actionError.value = store.error ?? '超时自动结束失败'
    autoEnding.value = false
  }
}

function startClockLoop(): void {
  stopClockLoop()
  clockTimer = setInterval(tickClock, CLOCK_TICK_MS)
}

function stopClockLoop(): void {
  if (clockTimer != null) {
    clearInterval(clockTimer)
    clockTimer = null
  }
}

watch(
  clockWindowKey,
  (key) => {
    if (!key) {
      clockRemainSec.value = 0
      clockDeadlineAt.value = 0
      autoEnding.value = false
      return
    }
    resetClock()
  },
  { immediate: true },
)

onMounted(async () => {
  document.addEventListener('fullscreenchange', onFullscreenChange)
  startClockLoop()
  const raw = route.params.gameId
  const key =
    typeof raw === 'string'
      ? raw.trim()
      : Array.isArray(raw)
        ? String(raw[0] ?? '').trim()
        : ''
  if (key) {
    try {
      await store.loadGame(decodeURIComponent(key))
    } catch {
      actionError.value = store.error ?? '加载对局失败'
    }
  }
})

onUnmounted(() => {
  document.removeEventListener('fullscreenchange', onFullscreenChange)
  stopClockLoop()
  store.stopPolling()
})
</script>

<template>
  <div
    class="battle-pc"
    :class="{
      'is-my-turn': store.isLocalPlayerActive && !store.isGameOver,
      'is-opp-turn': !store.isLocalPlayerActive && !store.isGameOver,
    }"
  >
    <!-- 顶栏：导航 + 阶段步进 + 回合 -->
    <header class="chrome-top">
      <n-button size="tiny" quaternary @click="goHome" title="返回">←</n-button>

      <nav class="phase-rail" aria-label="回合阶段">
        <span
          v-for="(ph, i) in PHASE_ORDER"
          :key="ph"
          class="phase-step"
          :class="{
            done: phaseIndex > i,
            current: phaseIndex === i,
          }"
        >
          {{ PHASE_LABELS[ph] }}
        </span>
      </nav>

      <div class="turn-block">
        <span class="turn-no">R{{ turnCount }}</span>
        <span class="turn-phase">{{ phaseLabel }}</span>
        <div
          v-if="clockWindowKey"
          class="turn-clock"
          :class="[
            clockUrgent,
            store.isLocalPlayerActive ? 'mine' : 'theirs',
          ]"
          :title="`本阶段限时 ${TURN_LIMIT_SEC} 秒，超时自动结束阶段`"
        >
          <span class="clock-ring" :style="clockRingStyle" aria-hidden="true" />
          <span class="clock-num">⏱️ {{ clockDisplay }}</span>
        </div>
        <span
          class="turn-owner"
          :class="store.isLocalPlayerActive ? 'mine' : 'theirs'"
        >
          {{ store.isLocalPlayerActive ? '你的回合' : '对手回合' }}
        </span>
      </div>

      <n-button
        size="tiny"
        quaternary
        :title="isFullscreen ? '退出全屏' : '全屏'"
        @click="toggleFullscreen"
      >
        {{ isFullscreen ? '⬜' : '⛶' }}
      </n-button>
    </header>

    <!-- 对手仪表条 -->
    <section class="player-strip opp">
      <div class="identity">
        <span class="avatar" />
        <div class="id-text">
          <strong>{{ store.opponent?.playerId ?? '对手' }}</strong>
          <span>手牌 {{ store.opponent?.handCount ?? 0 }}</span>
        </div>
      </div>

      <div class="meters">
        <div class="meter" title="角色卡组">
          <div class="meter-face">
            <Card v-if="opponentZones.characterDeck > 0" back-type="character" />
          </div>
          <div class="meter-meta">
            <em>角色</em>
            <b>{{ opponentZones.characterDeck }}</b>
          </div>
        </div>
        <div class="meter" title="撤退区">
          <div class="meter-face dim">
            <Card v-if="opponentZones.retreat > 0" back-type="character" />
          </div>
          <div class="meter-meta">
            <em>撤退</em>
            <b>{{ opponentZones.retreat }}</b>
          </div>
        </div>
        <div class="meter" title="虚空区">
          <div class="meter-face dim">
            <Card v-if="opponentZones.voidZone > 0" back-type="character" />
          </div>
          <div class="meter-meta">
            <em>虚空</em>
            <b>{{ opponentZones.voidZone }}</b>
          </div>
        </div>
        <div class="meter rush" title="冲击卡组">
          <div class="meter-face">
            <Card v-if="opponentZones.rushDeck > 0" back-type="rush" />
          </div>
          <div class="meter-meta">
            <em>冲击</em>
            <b>{{ opponentZones.rushDeck }}</b>
          </div>
        </div>
      </div>

      <div class="timeline-meter" title="时间线（9 张冲击卡胜利）">
        <div class="tl-head">
          <span>时间线</span>
          <strong>{{ opponentTimelineFilled }}/9</strong>
        </div>
        <div class="tl-track" aria-label="对手时间线进度">
          <div
            v-for="(card, i) in opponentTimeline"
            :key="`ot-${i}`"
            class="tl-cell"
            :class="{ filled: !!card }"
            :title="card?.cardCode || `空位 ${i + 1}`"
          />
        </div>
      </div>
    </section>

    <!-- 中央战区舞台：基地 + 阵型槽 -->
    <main class="stage">
      <div class="arena">
        <div class="side opp-side">
          <div class="bench">
            <span class="bench-tag">基地</span>
            <div class="bench-row reverse">
              <div
                v-for="(card, i) in opponentField.base"
                :key="`ob-${i}`"
                class="bench-slot"
                :data-n="i + 1"
              >
                <BattleFieldCard v-if="card" :card="card" side="opponent" />
              </div>
            </div>
          </div>

          <div class="formation opp" aria-label="对手阵型">
            <div class="f-col">
              <button
                type="button"
                class="f-slot"
                data-label="侧翼"
                :class="{
                  'is-target':
                    canAct && hasAttack && !!selectedField && !!opponentField.flankL,
                }"
                @click="onOpponentFieldSlotClick('FLANK_LEFT', 0, opponentField.flankL)"
              >
                <div
                  v-if="opponentField.flankL"
                  class="f-card"
                  :class="{
                    'is-selected': isFieldSelected(opponentField.flankL.instanceId),
                  }"
                >
                  <BattleFieldCard :card="opponentField.flankL" side="opponent" />
                </div>
              </button>
            </div>
            <div class="f-col spine">
              <button
                type="button"
                class="f-slot"
                data-label="后卫"
                :class="{
                  'is-target':
                    canAct && hasAttack && !!selectedField && !!opponentField.rearguard,
                }"
                @click="onOpponentFieldSlotClick('REARGUARD', 0, opponentField.rearguard)"
              >
                <div
                  v-if="opponentField.rearguard"
                  class="f-card"
                  :class="{
                    'is-selected': isFieldSelected(opponentField.rearguard.instanceId),
                  }"
                >
                  <BattleFieldCard :card="opponentField.rearguard" side="opponent" />
                </div>
              </button>
              <button
                type="button"
                class="f-slot"
                data-label="先锋"
                :class="{
                  'is-target':
                    canAct && hasAttack && !!selectedField && !!opponentField.vanguard,
                }"
                @click="onOpponentFieldSlotClick('VANGUARD', 0, opponentField.vanguard)"
              >
                <div
                  v-if="opponentField.vanguard"
                  class="f-card"
                  :class="{
                    'is-selected': isFieldSelected(opponentField.vanguard.instanceId),
                  }"
                >
                  <BattleFieldCard :card="opponentField.vanguard" side="opponent" />
                </div>
              </button>
            </div>
            <div class="f-col">
              <button
                type="button"
                class="f-slot"
                data-label="侧翼"
                :class="{
                  'is-target':
                    canAct && hasAttack && !!selectedField && !!opponentField.flankR,
                }"
                @click="onOpponentFieldSlotClick('FLANK_RIGHT', 0, opponentField.flankR)"
              >
                <div
                  v-if="opponentField.flankR"
                  class="f-card"
                  :class="{
                    'is-selected': isFieldSelected(opponentField.flankR.instanceId),
                  }"
                >
                  <BattleFieldCard :card="opponentField.flankR" side="opponent" />
                </div>
              </button>
            </div>
          </div>
        </div>

        <div class="vs-line">
          <span>VS</span>
        </div>

        <div class="side local-side">
          <div class="formation local" aria-label="我方阵型">
            <div class="f-col">
              <button
                type="button"
                class="f-slot"
                data-label="侧翼"
                :class="{
                  'is-drop': canAct && !!selectedHandCode && hasSummon && !localField.flankL,
                }"
                @click="onLocalFieldSlotClick('FLANK_LEFT', 0, localField.flankL)"
              >
                <div
                  v-if="localField.flankL"
                  class="f-card"
                  :class="{
                    'is-selected': isFieldSelected(localField.flankL.instanceId),
                  }"
                >
                  <BattleFieldCard :card="localField.flankL" side="local" />
                </div>
              </button>
            </div>
            <div class="f-col spine">
              <button
                type="button"
                class="f-slot"
                data-label="先锋"
                :class="{
                  'is-drop':
                    canAct && !!selectedHandCode && hasSummon && !localField.vanguard,
                }"
                @click="onLocalFieldSlotClick('VANGUARD', 0, localField.vanguard)"
              >
                <div
                  v-if="localField.vanguard"
                  class="f-card"
                  :class="{
                    'is-selected': isFieldSelected(localField.vanguard.instanceId),
                  }"
                >
                  <BattleFieldCard :card="localField.vanguard" side="local" />
                </div>
              </button>
              <button
                type="button"
                class="f-slot"
                data-label="后卫"
                :class="{
                  'is-drop':
                    canAct && !!selectedHandCode && hasSummon && !localField.rearguard,
                }"
                @click="onLocalFieldSlotClick('REARGUARD', 0, localField.rearguard)"
              >
                <div
                  v-if="localField.rearguard"
                  class="f-card"
                  :class="{
                    'is-selected': isFieldSelected(localField.rearguard.instanceId),
                  }"
                >
                  <BattleFieldCard :card="localField.rearguard" side="local" />
                </div>
              </button>
            </div>
            <div class="f-col">
              <button
                type="button"
                class="f-slot"
                data-label="侧翼"
                :class="{
                  'is-drop': canAct && !!selectedHandCode && hasSummon && !localField.flankR,
                }"
                @click="onLocalFieldSlotClick('FLANK_RIGHT', 0, localField.flankR)"
              >
                <div
                  v-if="localField.flankR"
                  class="f-card"
                  :class="{
                    'is-selected': isFieldSelected(localField.flankR.instanceId),
                  }"
                >
                  <BattleFieldCard :card="localField.flankR" side="local" />
                </div>
              </button>
            </div>
          </div>

          <div class="bench">
            <span class="bench-tag">基地</span>
            <div class="bench-row">
              <div
                v-for="(card, i) in localField.base"
                :key="`lb-${i}`"
                class="bench-slot"
                :class="{
                  'is-drop':
                    canAct &&
                    !!selectedHandCode &&
                    !card &&
                    (hasBaseDeploy || hasSummon),
                  'is-selected': card && isFieldSelected(card.instanceId),
                }"
                :data-n="i + 1"
                @click="onLocalBaseSlotClick(i)"
              >
                <BattleFieldCard
                  v-if="card"
                  :card="card"
                  side="local"
                  @click.stop="onLocalFieldSlotClick('BASE', i, card)"
                />
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-if="store.isGameOver" class="finished-overlay">
        <div class="finished-panel">
          <p class="finished-title">{{ resultLabel }}</p>
          <p class="finished-sub">
            {{
              store.gameState?.winner === 'DRAW'
                ? '本局平局'
                : store.isLocalPlayerWinner
                  ? '你赢得了本局'
                  : '对手赢得了本局'
            }}
          </p>
          <n-button type="primary" size="small" @click="goHome">🏠 返回首页</n-button>
        </div>
      </div>
    </main>

    <!-- 我方仪表条 -->
    <section class="player-strip local">
      <div class="timeline-meter" title="时间线（9 张冲击卡胜利）">
        <div class="tl-head">
          <span>时间线</span>
          <strong>{{ localTimelineFilled }}/9</strong>
        </div>
        <div class="tl-track" aria-label="我方时间线进度">
          <div
            v-for="(card, i) in localTimeline"
            :key="`lt-${i}`"
            class="tl-cell"
            :class="{ filled: !!card }"
            :title="card?.cardCode || `空位 ${i + 1}`"
          />
        </div>
      </div>

      <div class="meters">
        <div class="meter rush" title="冲击卡组">
          <div class="meter-face">
            <Card v-if="localZones.rushDeck > 0" back-type="rush" />
          </div>
          <div class="meter-meta">
            <em>冲击</em>
            <b>{{ localZones.rushDeck }}</b>
          </div>
        </div>
        <div class="meter" title="虚空区">
          <div class="meter-face dim">
            <Card v-if="localZones.voidZone > 0" back-type="character" />
          </div>
          <div class="meter-meta">
            <em>虚空</em>
            <b>{{ localZones.voidZone }}</b>
          </div>
        </div>
        <div class="meter" title="撤退区">
          <div class="meter-face dim">
            <Card v-if="localZones.retreat > 0" back-type="character" />
          </div>
          <div class="meter-meta">
            <em>撤退</em>
            <b>{{ localZones.retreat }}</b>
          </div>
        </div>
        <div class="meter" title="角色卡组">
          <div class="meter-face">
            <Card v-if="localZones.characterDeck > 0" back-type="character" />
          </div>
          <div class="meter-meta">
            <em>角色</em>
            <b>{{ localZones.characterDeck }}</b>
          </div>
        </div>
      </div>
    </section>

    <!-- 操作坞：提示 + 指令 + 手牌 -->
    <footer class="command-dock">
      <div class="dock-left">
        <p class="hint" :class="{ error: !!actionError }">{{ hintText }}</p>
        <div class="cmds">
          <n-button
            v-if="hasBaseDeploy"
            size="small"
            :type="pendingMode === 'BASE_DEPLOY' ? 'primary' : 'default'"
            :secondary="pendingMode !== 'BASE_DEPLOY'"
            :disabled="!canAct"
            @click="setPendingMode('BASE_DEPLOY')"
          >
            🏕️ 基地部署
          </n-button>
          <n-button
            v-if="hasSummon"
            size="small"
            :type="pendingMode === 'SUMMON' ? 'primary' : 'default'"
            :secondary="pendingMode !== 'SUMMON'"
            :disabled="!canAct"
            @click="setPendingMode('SUMMON')"
          >
            ✨ 号召
          </n-button>
          <n-button
            v-if="hasAttack"
            size="small"
            :type="pendingMode === 'ATTACK' ? 'primary' : 'default'"
            :secondary="pendingMode !== 'ATTACK'"
            :disabled="!canAct"
            @click="setPendingMode('ATTACK')"
          >
            ⚔️ 攻击
          </n-button>
          <n-button
            v-if="hasEndPhase"
            type="primary"
            size="small"
            :disabled="!canAct"
            :loading="store.loading"
            @click="onEndPhase"
          >
            ▶️ 结束阶段
          </n-button>
          <n-button
            v-if="hasSurrender"
            type="error"
            size="small"
            secondary
            :disabled="store.loading || store.isGameOver"
            @click="onSurrender"
          >
            🏳️ 认输
          </n-button>
        </div>
      </div>

      <div class="hand-tray">
        <div
          v-for="(card, i) in localHand"
          :key="card.instanceId"
          class="hand-slot"
          :class="{ 'is-selected': selectedHandId === card.instanceId }"
          :style="fanCardStyle(i, handCount)"
          @click="selectHand(card)"
        >
          <BattleFieldCard :card="card" side="local" force-face-up />
        </div>
      </div>

      <div class="dock-right">
        <span class="hand-count">手牌 {{ handCount }}</span>
      </div>
    </footer>
  </div>
</template>

<style scoped>
.battle-pc {
  /* 卡面比例用小数，避免 calc(h * (747/1042)) 在部分浏览器里宽高塌成 0 */
  --card-aspect: 0.717;
  --card-h: clamp(64px, 9.5vh, 88px);
  --card-w: calc(var(--card-h) * 0.717);
  --hand-h: clamp(96px, 14vh, 128px);
  --hand-w: calc(var(--hand-h) * 0.717);
  --d-gap: clamp(4px, 0.7vw, 8px);
  --slot-inset: inset 0 1px 2px rgba(0, 0, 0, 0.35);

  display: grid;
  grid-template-rows: 40px auto minmax(0, 1fr) auto auto;
  height: 100dvh;
  overflow: hidden;
  background: var(--bg-base);
  color: var(--text-primary);
}

.battle-pc.is-my-turn {
  --owner-tint: var(--accent-soft);
}

.battle-pc.is-opp-turn {
  --owner-tint: color-mix(in srgb, var(--accent-blue) 14%, transparent);
}

/* ========== 顶栏 ========== */
.chrome-top {
  display: grid;
  grid-template-columns: 32px 1fr auto 32px;
  align-items: center;
  gap: 10px;
  padding: 0 12px;
  background: var(--shell-topbar-bg, var(--bg-surface));
  border-bottom: 1px solid var(--border);
  z-index: 8;
}

.phase-rail {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 2px;
  min-width: 0;
  overflow: hidden;
}

.phase-step {
  position: relative;
  padding: 3px 8px;
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.02em;
  color: var(--text-secondary);
  border-radius: 999px;
}

.phase-step.done {
  color: var(--text-primary);
  opacity: 0.72;
}

.phase-step.current {
  color: var(--accent-contrast);
  background: var(--accent);
}

.turn-block {
  display: flex;
  align-items: center;
  gap: 8px;
  white-space: nowrap;
}

.turn-no {
  font-size: 12px;
  font-weight: 700;
  color: var(--text-secondary);
  font-variant-numeric: tabular-nums;
}

.turn-phase {
  font-size: 12px;
  font-weight: 600;
  color: var(--text-primary);
}

.turn-clock {
  position: relative;
  min-width: 64px;
  height: 40px;
  display: grid;
  place-items: center;
  flex-shrink: 0;
  padding: 0 4px;
}

.clock-ring {
  position: absolute;
  inset: 2px;
  border-radius: 50%;
  mask: radial-gradient(farthest-side, transparent calc(100% - 3px), #000 calc(100% - 2px));
  -webkit-mask: radial-gradient(farthest-side, transparent calc(100% - 3px), #000 calc(100% - 2px));
  transition: background 0.2s linear;
}

.clock-num {
  position: relative;
  z-index: 1;
  font-size: 10px;
  font-weight: 800;
  font-variant-numeric: tabular-nums;
  color: var(--text-primary);
  white-space: nowrap;
  letter-spacing: -0.02em;
}

.turn-clock.danger .clock-num {
  color: var(--accent-red);
  animation: clock-pulse 0.7s ease-in-out infinite;
}

@keyframes clock-pulse {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0.45;
  }
}

.turn-owner {
  padding: 2px 8px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 700;
  border: 1px solid var(--border);
}

.turn-owner.mine {
  color: var(--accent-contrast);
  background: var(--accent);
  border-color: transparent;
}

.turn-owner.theirs {
  color: var(--text-secondary);
  background: var(--bg-surface-2);
}

/* ========== 玩家仪表条 ========== */
.player-strip {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 4px 12px;
  min-height: 40px;
  background: var(--bg-surface);
  border-bottom: 1px solid var(--border);
  z-index: 6;
}

.player-strip.local {
  border-bottom: none;
  border-top: 1px solid var(--border);
  background: color-mix(in srgb, var(--bg-surface) 88%, var(--owner-tint, transparent));
}

.player-strip.opp {
  background: color-mix(in srgb, var(--bg-surface) 92%, transparent);
}

.identity {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
  min-width: 120px;
}

.avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: var(--accent-blue);
  border: 2px solid var(--bg-surface-2);
}

.id-text {
  display: flex;
  flex-direction: column;
  line-height: 1.15;
  gap: 1px;
}

.id-text strong {
  font-size: 13px;
  font-weight: 700;
}

.id-text span {
  font-size: 11px;
  color: var(--text-secondary);
}

.meters {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-shrink: 0;
}

.meter {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 3px 6px 3px 3px;
  background: var(--bg-surface-2);
  border: 1px solid var(--border-light);
  border-radius: var(--radius-sm);
  box-shadow: var(--edge-highlight, none);
}

.meter.rush {
  border-color: color-mix(in srgb, var(--accent-gold) 45%, var(--border));
}

.meter-face {
  position: relative;
  width: 22px;
  height: 30px;
  border-radius: 3px;
  overflow: hidden;
  background: var(--bg-base);
  border: 1px solid var(--border);
  flex-shrink: 0;
}

.meter-face.dim {
  opacity: 0.85;
}

/* Card 根节点是 absolute；宿主必须 relative，并关掉横置旋转以免撑破仪表 */
.meter-face :deep(.card-face) {
  position: absolute !important;
  inset: 0 !important;
  top: 0 !important;
  left: 0 !important;
  width: auto !important;
  height: auto !important;
  transform: none !important;
  border-radius: 2px !important;
  background-size: cover !important;
}

.meter-meta {
  display: flex;
  flex-direction: column;
  line-height: 1.1;
}

.meter-meta em {
  font-style: normal;
  font-size: 9px;
  font-weight: 700;
  letter-spacing: 0.04em;
  color: var(--text-secondary);
}

.meter-meta b {
  font-size: 13px;
  font-weight: 700;
  font-variant-numeric: tabular-nums;
}

/* 时间线 = 胜利进度条 */
.timeline-meter {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.tl-head {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  padding: 0 2px;
}

.tl-head span {
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.06em;
  color: var(--text-secondary);
}

.tl-head strong {
  font-size: 12px;
  font-weight: 700;
  color: var(--accent-gold);
  font-variant-numeric: tabular-nums;
}

.tl-track {
  display: grid;
  grid-template-columns: repeat(9, minmax(0, 1fr));
  gap: 3px;
  height: 10px;
}

.tl-cell {
  border-radius: 3px;
  background: var(--bg-base);
  border: 1px solid var(--border-light);
}

.tl-cell.filled {
  border-color: color-mix(in srgb, var(--accent-gold) 65%, var(--border));
  background: color-mix(in srgb, var(--accent-gold) 62%, var(--bg-surface-2));
}

/* ========== 中央舞台 ========== */
.stage {
  position: relative;
  min-height: 0;
  overflow: hidden;
  isolation: isolate;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 8px 16px;
}

.stage::before {
  content: '';
  position: absolute;
  inset: 0;
  z-index: 0;
  background: var(--shell-art) center 35% / cover no-repeat;
  opacity: 0.08;
  filter: var(--shell-art-filter);
  pointer-events: none;
}

.stage::after {
  content: '';
  position: absolute;
  inset: 0;
  z-index: 0;
  background:
    radial-gradient(
      ellipse 70% 55% at 50% 50%,
      var(--owner-tint, transparent),
      transparent 70%
    ),
    var(--bg-elevated);
  pointer-events: none;
}

.arena {
  position: relative;
  z-index: 1;
  width: min(920px, 100%);
  height: 100%;
  display: grid;
  grid-template-rows: 1fr auto 1fr;
  gap: 4px;
  padding: 8px 14px;
  background: var(--bg-surface);
  border: 1px solid var(--border-light);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-md), var(--edge-highlight, none);
}

.side {
  min-height: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.opp-side {
  justify-content: flex-start;
}

.local-side {
  justify-content: flex-end;
}

.vs-line {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 18px;
}

.vs-line span {
  font-size: 10px;
  font-weight: 800;
  letter-spacing: 0.2em;
  color: var(--text-secondary);
  padding: 0 10px;
  border-top: 1px solid var(--border-light);
  border-bottom: 1px solid var(--border-light);
  line-height: 16px;
}

/* 基地长条 */
.bench {
  position: relative;
  flex-shrink: 0;
  height: var(--card-h);
  width: calc(var(--card-w) * 6 + 3px * 5 + 8px);
  max-width: 100%;
  padding: 4px;
  background: var(--bg-surface-2);
  border: 1px solid var(--border-light);
  border-radius: var(--radius-md);
  box-shadow: var(--slot-inset);
}

.bench-tag {
  position: absolute;
  z-index: 2;
  top: -9px;
  left: 8px;
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.05em;
  color: var(--text-primary);
  background: var(--bg-surface-3);
  border: 1px solid var(--border-light);
  border-radius: var(--radius-sm);
  padding: 0 6px;
  line-height: 1.45;
  pointer-events: none;
}

.local-side .bench-tag {
  top: auto;
  bottom: -9px;
  left: auto;
  right: 8px;
}

.bench-row {
  display: flex;
  gap: 3px;
  height: 100%;
}

.bench-row.reverse {
  flex-direction: row-reverse;
}

.bench-slot {
  position: relative;
  height: 100%;
  aspect-ratio: var(--card-aspect);
  flex-shrink: 0;
  background: var(--bg-elevated);
  border: 1px solid var(--border-light);
  border-radius: var(--radius-sm);
  box-shadow: var(--slot-inset);
  overflow: hidden;
  transition: border-color var(--transition-fast);
}

.bench-slot :deep(.card-face) {
  position: absolute;
  inset: 0;
  border-radius: inherit;
}

.bench-slot.is-drop {
  border-color: var(--accent);
  cursor: pointer;
}

.bench-slot.is-selected {
  border-color: var(--accent);
  box-shadow: var(--shadow-glow);
}

.bench-slot::after {
  content: attr(data-n);
  position: absolute;
  right: 2px;
  bottom: 1px;
  font-size: 9px;
  font-weight: 700;
  color: var(--text-secondary);
  pointer-events: none;
  z-index: 1;
}

/* 阵型槽：十字布局，无菱形装饰 */
.formation {
  position: relative;
  display: grid;
  grid-template-columns: repeat(3, calc(var(--card-w) + 6px));
  gap: var(--d-gap);
  align-items: center;
  justify-content: center;
  padding: 6px 8px;
}

.f-col {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--d-gap);
}

.f-slot {
  position: relative;
  width: calc(var(--card-w) + 6px);
  height: calc(var(--card-h) + 6px);
  padding: 0;
  border: none;
  background: transparent;
  display: grid;
  place-items: center;
  cursor: default;
}

.f-slot.is-drop,
.f-slot.is-target {
  cursor: pointer;
}

.f-slot:not(:has(.f-card))::before {
  content: attr(data-label);
  position: absolute;
  inset: 3px;
  display: grid;
  place-items: center;
  border: 1px solid var(--border-light);
  border-radius: var(--radius-sm);
  background: var(--bg-elevated);
  box-shadow: var(--slot-inset);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.06em;
  color: var(--text-secondary);
  pointer-events: none;
}

.f-slot.is-drop:not(:has(.f-card))::before {
  border-color: var(--accent);
  color: var(--accent);
  background: color-mix(in srgb, var(--accent) 12%, var(--bg-elevated));
}

.f-slot.is-target:has(.f-card) {
  outline: 2px solid var(--accent);
  outline-offset: 1px;
  border-radius: var(--radius-sm);
}

.f-card {
  position: relative;
  width: var(--card-w);
  height: var(--card-h);
  border-radius: 5px;
  border: 2px solid transparent;
  overflow: hidden;
  transition: transform 0.15s, border-color var(--transition-fast);
}

.f-card :deep(.card-face) {
  position: absolute;
  inset: 0;
  border-radius: inherit;
}

.f-card.is-selected {
  border-color: var(--accent);
  box-shadow: var(--shadow-glow);
}

.f-slot:hover .f-card {
  transform: translateY(-2px);
}

/* ========== 操作坞 ========== */
.command-dock {
  display: grid;
  grid-template-columns: minmax(140px, 1fr) auto minmax(80px, 1fr);
  align-items: end;
  gap: 8px;
  padding: 4px 12px 8px;
  background: color-mix(in srgb, var(--bg-surface) 94%, transparent);
  border-top: 1px solid var(--border);
  z-index: 8;
  min-height: calc(var(--hand-h) + 28px);
  overflow: visible;
}

.dock-left {
  display: flex;
  flex-direction: column;
  gap: 6px;
  justify-content: flex-end;
  min-width: 0;
  padding-bottom: 6px;
}

.hint {
  margin: 0;
  font-size: 11px;
  color: var(--text-secondary);
  min-height: 14px;
}

.hint.error {
  color: var(--accent-red);
}

.cmds {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

/* 手牌托盘：flex 重叠扇形，宽高写死，不再依赖 absolute 塌缩 */
.hand-tray {
  display: flex;
  align-items: flex-end;
  justify-content: center;
  height: var(--hand-h);
  max-width: min(640px, 58vw);
  padding: 0 8px;
  overflow: visible;
}

.hand-slot {
  position: relative;
  flex: 0 0 auto;
  width: var(--hand-w);
  height: var(--hand-h);
  margin-left: calc(var(--hand-w) * -0.42);
  transform-origin: bottom center;
  cursor: pointer;
  border-radius: 6px;
  border: 2px solid transparent;
  overflow: hidden;
  box-shadow: var(--shadow-sm);
  background: var(--bg-surface-2);
  transition: transform 0.18s ease, border-color var(--transition-fast),
    margin 0.18s ease;
}

.hand-slot:first-child {
  margin-left: 0;
}

.hand-slot.is-selected {
  border-color: var(--accent);
  box-shadow: var(--shadow-glow);
  z-index: 50 !important;
  margin-bottom: 8px;
}

.hand-slot:hover {
  z-index: 60 !important;
  transform: translateY(-18px) scale(1.06) !important;
}

.dock-right {
  display: flex;
  justify-content: flex-end;
  align-items: flex-end;
  padding-bottom: 10px;
}

.hand-count {
  font-size: 11px;
  font-weight: 700;
  color: var(--text-secondary);
  font-variant-numeric: tabular-nums;
}

/* ========== 终局 ========== */
.finished-overlay {
  position: absolute;
  inset: 0;
  z-index: 40;
  display: grid;
  place-items: center;
  background: color-mix(in srgb, var(--bg-base) 58%, transparent);
}

.finished-panel {
  min-width: 220px;
  padding: 24px 28px;
  text-align: center;
  background: var(--bg-surface);
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-md);
}

.finished-title {
  margin: 0 0 8px;
  font-size: 22px;
  font-weight: 700;
}

.finished-sub {
  margin: 0 0 16px;
  font-size: 13px;
  color: var(--text-secondary);
}

@media (max-width: 900px) {
  .phase-rail {
    display: none;
  }

  .chrome-top {
    grid-template-columns: 32px 1fr 32px;
  }

  .turn-block {
    justify-self: center;
  }

  .meters {
    display: none;
  }

  .player-strip.opp .identity {
    min-width: auto;
  }
}
</style>
