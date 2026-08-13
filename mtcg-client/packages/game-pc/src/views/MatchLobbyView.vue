<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { NButton, NEmpty, NInput, NSpin, NTag, NSpace } from 'naive-ui'
import { deckApi, gameApi } from '@mtcg/common'
import { useGameStore } from '@mtcg/common/stores'
import type { CreateAIGameDTO, DeckVO } from '@mtcg/common'
import AiBattleDialog from '@/components/AiBattleDialog.vue'
import CardRail from '@/components/CardRail.vue'
import DeckCover from '@/components/DeckCover.vue'
import { toast } from '@/feedback'

const READY_DECK_KEY = 'mtcg_ready_deck_id'
const POLL_MS = 2000

type LobbyPhase = 'idle' | 'matching' | 'hosting' | 'offer-ai'

const router = useRouter()
const gameStore = useGameStore()

const loading = ref(false)
const busy = ref(false)
const decks = ref<DeckVO[]>([])
const selectedId = ref<number | null>(null)
const joinGameId = ref('')
const formError = ref('')
const phase = ref<LobbyPhase>('idle')
const hostedGameId = ref<number | null>(null)
const aiOpen = ref(false)

const validDecks = computed(() => decks.value.filter((d) => d.isValid === true))

const selected = computed<DeckVO | null>(() => {
  const id = selectedId.value
  if (id == null) return validDecks.value[0] ?? null
  return validDecks.value.find((d) => d.id === id) ?? validDecks.value[0] ?? null
})

const locked = computed(() => busy.value || phase.value === 'matching' || phase.value === 'hosting')

let pollTimer: ReturnType<typeof setInterval> | null = null

function pickDeck(deck: DeckVO): void {
  if (locked.value) return
  selectedId.value = deck.id
  localStorage.setItem(READY_DECK_KEY, String(deck.id))
}

function requireDeck(): DeckVO | null {
  formError.value = ''
  const deck = selected.value
  if (!deck) {
    formError.value = '请先完成一套合法卡组'
    toast.warning('请先完成一套合法卡组')
    return null
  }
  return deck
}

function stopPoll(): void {
  if (pollTimer != null) {
    clearInterval(pollTimer)
    pollTimer = null
  }
}

async function enterBattle(gameId: number): Promise<void> {
  stopPoll()
  await gameStore.loadGame(gameId)
  await router.push(`/battle/${gameId}`)
}

async function handleMatch(): Promise<void> {
  const deck = requireDeck()
  if (!deck) return
  phase.value = 'matching'
  busy.value = true
  try {
    const result = await gameApi.match({ deckId: deck.id })
    if (result.matched && result.gameId != null) {
      toast.success('匹配成功，进入对局')
      await enterBattle(result.gameId)
      return
    }
    toast.info('暂无对手，可开房或与 AI 练手')
    phase.value = 'offer-ai'
  } catch {
    phase.value = 'idle'
  } finally {
    busy.value = false
  }
}

async function handleCreateRoom(): Promise<void> {
  const deck = requireDeck()
  if (!deck) return
  busy.value = true
  try {
    const id = await gameApi.create({ deck1Id: deck.id, gameMode: 'CASUAL' })
    hostedGameId.value = id
    phase.value = 'hosting'
    startHostPoll(id)
    toast.success(`房间已创建，对局 ID ${id}`)
  } catch {
    phase.value = 'idle'
  } finally {
    busy.value = false
  }
}

function startHostPoll(gameId: number): void {
  stopPoll()
  pollTimer = setInterval(() => {
    void pollHost(gameId)
  }, POLL_MS)
}

async function pollHost(gameId: number): Promise<void> {
  try {
    const vo = await gameApi.getState(gameId)
    if (vo.status === 'IN_PROGRESS') {
      await enterBattle(gameId)
    }
  } catch {
    // 下次再试
  }
}

async function handleCancelHost(): Promise<void> {
  const id = hostedGameId.value
  if (id == null) return
  busy.value = true
  try {
    await gameApi.cancelWaiting(id)
    toast.info('已取消房间')
  } catch {
    // HTTP 错误由全局 notifier 提示
  } finally {
    stopPoll()
    hostedGameId.value = null
    phase.value = 'idle'
    busy.value = false
  }
}

async function copyRoomId(): Promise<void> {
  const id = hostedGameId.value
  if (id == null) return
  const text = String(id)
  try {
    await navigator.clipboard.writeText(text)
    toast.success('对局 ID 已复制')
  } catch {
    window.prompt('复制对局 ID', text)
    toast.info('请手动复制对局 ID')
  }
}

async function handleJoin(): Promise<void> {
  const deck = requireDeck()
  if (!deck) return
  const id = Number(joinGameId.value.trim())
  if (!Number.isFinite(id) || id <= 0) {
    formError.value = '请填写对局 ID'
    toast.warning('请填写对局 ID')
    return
  }
  busy.value = true
  try {
    const gameId = await gameApi.join(id, { deckId: deck.id })
    toast.success('已加入对局')
    await enterBattle(gameId)
  } catch {
    // HTTP 错误由全局 notifier 提示
  } finally {
    busy.value = false
  }
}

function handleAi(): void {
  if (!requireDeck()) return
  aiOpen.value = true
}

async function handleAiConfirm(dto: CreateAIGameDTO): Promise<void> {
  busy.value = true
  try {
    const gameId = await gameApi.createAi(dto)
    aiOpen.value = false
    toast.success('对局已创建')
    await enterBattle(gameId)
  } catch {
    // 后端业务错误由全局 notifier 提示
  } finally {
    busy.value = false
  }
}

onMounted(async () => {
  try {
    decks.value = await deckApi.list(undefined, loading)
  } catch {
    decks.value = []
  }
  const saved = Number(localStorage.getItem(READY_DECK_KEY))
  if (Number.isFinite(saved) && validDecks.value.some((d) => d.id === saved)) {
    selectedId.value = saved
  } else if (validDecks.value[0]) {
    selectedId.value = validDecks.value[0].id
  }
})

onUnmounted(() => {
  stopPoll()
})
</script>

<template>
  <div class="page">
    <header class="head">
      <div>
        <h1>备战室</h1>
        <p class="head-hint">选定出战卡组，匹配或开房开战</p>
      </div>
    </header>

    <div v-if="loading" class="spin-wrap">
      <n-spin size="large" />
    </div>

    <div v-else-if="validDecks.length === 0" class="empty-room">
      <n-empty description="还没有合法卡组，无法出战。">
        <template #icon>
          <span class="empty-ico">🃏</span>
        </template>
        <template #extra>
          <p class="hint">主卡组 50 张角色 · 冲击卡组 9 张。</p>
          <n-button type="primary" @click="router.push('/decks')">去构筑</n-button>
        </template>
      </n-empty>
    </div>

    <template v-else>
      <section class="ready">
        <div class="stage">
          <div class="stage-glow" aria-hidden="true" />
          <div class="cover">
            <DeckCover :image-path="selected?.coverImagePath" placeholder="🃏" />
          </div>
        </div>

        <div class="brief">
          <p class="ready-kicker">出战卡组</p>
          <h2>{{ selected?.deckName }}</h2>
          <n-space class="meta" :size="8">
            <n-tag type="success" size="small" :bordered="false">合法</n-tag>
            <n-tag size="small" :bordered="false">主 {{ selected?.mainDeckSize ?? 0 }}/50</n-tag>
            <n-tag type="warning" size="small" :bordered="false">
              冲击 {{ selected?.rushDeckSize ?? 0 }}/9
            </n-tag>
          </n-space>

          <p v-if="formError" class="banner">{{ formError }}</p>

          <div v-if="phase === 'matching'" class="status-box matching">
            <span class="pulse" aria-hidden="true" />
            <div>
              <p class="status-title">正在匹配对手…</p>
              <p class="hint">扫描空闲房间，稍候片刻</p>
            </div>
          </div>

          <div v-else-if="phase === 'hosting'" class="status-box">
            <p class="status-title">房间已创建，等待对手加入</p>
            <div class="room-row">
              <p class="room-id">对局 ID {{ hostedGameId }}</p>
              <n-button size="small" @click="copyRoomId">复制</n-button>
            </div>
            <p class="hint">把 ID 发给对手，对方在下方加入即可开打。</p>
            <n-button class="cancel-host" :disabled="busy" @click="handleCancelHost">
              取消房间
            </n-button>
          </div>

          <div v-else-if="phase === 'offer-ai'" class="status-box">
            <p class="status-title">没有匹配到对手</p>
            <p class="hint">可以创建房间等好友，或先与 AI 练手。</p>
            <div class="cta-row">
              <n-button type="primary" :loading="busy" @click="handleCreateRoom">
                🏠 创建房间
              </n-button>
              <n-button :disabled="busy" @click="handleAi">🤖 与 AI 对战</n-button>
            </div>
            <n-button text type="primary" class="back-link" @click="phase = 'idle'">返回</n-button>
          </div>

          <div v-else class="cta-row main">
            <n-button type="primary" :loading="busy" @click="handleMatch">
              ⚡ 在线匹配
            </n-button>
            <n-button :disabled="busy" @click="handleCreateRoom">🏠 创建房间</n-button>
            <n-button :disabled="busy" @click="handleAi">🤖 与 AI 对战</n-button>
          </div>

          <form class="join" @submit.prevent="handleJoin">
            <div class="field grow">
              <span class="field-label">加入房间</span>
              <n-input
                v-model:value="joinGameId"
                placeholder="输入对局 ID"
                :disabled="locked"
                inputmode="numeric"
              />
            </div>
            <n-button attr-type="submit" :loading="busy" :disabled="locked">加入</n-button>
          </form>
        </div>
      </section>

      <section class="block">
        <header class="sec">
          <h2>可出战卡组</h2>
          <n-button text type="primary" @click="router.push('/decks')">全部 →</n-button>
        </header>
        <CardRail :item-count="validDecks.length">
          <button
            v-for="deck in validDecks"
            :key="deck.id"
            type="button"
            class="strip-card"
            :class="{ on: selected?.id === deck.id }"
            :disabled="locked"
            @click="pickDeck(deck)"
          >
            <span class="strip-art">
              <DeckCover :image-path="deck.coverImagePath" placeholder="🃏" />
            </span>
            <span class="strip-name">{{ deck.deckName }}</span>
          </button>
        </CardRail>
      </section>
    </template>

    <AiBattleDialog
      v-if="selected"
      :open="aiOpen"
      :busy="busy"
      :human-deck="selected"
      :decks="validDecks"
      @close="aiOpen = false"
      @confirm="handleAiConfirm"
    />
  </div>
</template>

<style scoped>
.page {
  width: min(var(--shell-max), 100%);
  margin: 0 auto;
  padding: 18px var(--space-lg) 48px;
}

.head {
  margin-bottom: 16px;
}

.head h1 {
  margin: 0;
  font-size: 22px;
  font-weight: 700;
}

.head-hint {
  margin: 6px 0 0;
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

.spin-wrap {
  display: flex;
  justify-content: center;
  padding: 48px 0;
}

.empty-room {
  padding: 48px 24px;
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  background: var(--bg-surface);
  box-shadow: var(--shadow-sm), var(--edge-highlight);
}

.empty-ico {
  font-size: 40px;
  line-height: 1;
}

.empty-room .hint {
  margin: 0 0 16px;
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

.ready {
  display: grid;
  grid-template-columns: 260px minmax(0, 1fr);
  gap: 32px;
  align-items: start;
  padding: 22px 24px;
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  background:
    linear-gradient(
      135deg,
      color-mix(in srgb, var(--accent) 6%, var(--bg-surface)) 0%,
      var(--bg-surface) 42%
    );
  box-shadow: var(--shadow-sm), var(--edge-highlight);
}

.stage {
  position: relative;
  display: flex;
  justify-content: center;
  padding: 8px 0;
}

.stage-glow {
  position: absolute;
  inset: 18% 8% 8%;
  border-radius: 50%;
  background: color-mix(in srgb, var(--accent) 18%, transparent);
  filter: blur(28px);
  pointer-events: none;
}

.cover {
  position: relative;
  z-index: 1;
  width: 240px;
  height: calc(240px * 2080 / 1488);
  flex: none;
  border-radius: var(--radius-md);
  overflow: hidden;
  background: var(--bg-surface-2);
  border: 1px solid var(--border);
  box-shadow: var(--shadow-lg);
}

.ready-kicker {
  margin: 0;
  font-size: 12px;
  letter-spacing: 0.18em;
  font-weight: 700;
  color: var(--accent);
}

.brief h2 {
  margin: 8px 0 0;
  font-size: 24px;
  font-weight: 700;
  line-height: 1.2;
}

.meta {
  margin: 12px 0 0;
}

.banner {
  margin: 14px 0 0;
  padding: 10px 12px;
  border-radius: var(--radius-sm);
  background: color-mix(in srgb, var(--accent-red) 12%, transparent);
  color: var(--accent-red);
  font-size: var(--font-size-sm);
}

.status-box {
  margin-top: 18px;
  padding: 14px 16px;
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  background: var(--bg-surface-2);
}

.status-box.matching {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.status-title {
  margin: 0;
  font-weight: 600;
}

.status-box .hint {
  margin-top: 6px;
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

.pulse {
  width: 10px;
  height: 10px;
  margin-top: 5px;
  border-radius: 50%;
  background: var(--accent);
  box-shadow: 0 0 0 0 color-mix(in srgb, var(--accent) 45%, transparent);
  animation: pulse 1.2s ease-out infinite;
  flex-shrink: 0;
}

@keyframes pulse {
  70% {
    box-shadow: 0 0 0 10px transparent;
  }
  100% {
    box-shadow: 0 0 0 0 transparent;
  }
}

.room-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
  margin-top: 10px;
}

.room-id {
  margin: 0;
  font-weight: 700;
  font-variant-numeric: tabular-nums;
  font-size: 15px;
}

.cancel-host {
  margin-top: 12px;
}

.cta-row {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 12px;
}

.cta-row.main {
  margin-top: 18px;
}

.back-link {
  margin-top: 12px;
}

.join {
  display: flex;
  align-items: flex-end;
  gap: 10px;
  margin-top: 18px;
  padding-top: 16px;
  border-top: 1px solid var(--border);
}

.field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.field.grow {
  flex: 1;
  min-width: 0;
}

.field-label {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

.block {
  margin-top: 28px;
}

.sec {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--border);
}

.sec h2 {
  margin: 0;
  font-size: 17px;
  font-weight: 700;
}

.strip-card {
  flex: 0 0 var(--card-face-w);
  width: var(--card-face-w);
  display: flex;
  flex-direction: column;
  padding: 0;
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  overflow: hidden;
  background: var(--bg-surface);
  color: inherit;
  cursor: pointer;
  text-align: left;
  transition: transform var(--transition-fast), border-color var(--transition-fast);
}

.strip-card:hover:not(:disabled) {
  transform: translateY(-3px);
}

.strip-card.on {
  border-color: var(--accent);
  box-shadow: var(--shadow-glow);
}

.strip-card:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

.strip-art {
  display: block;
  width: var(--card-face-w);
  height: var(--card-face-h);
  flex: none;
  background: var(--bg-surface-2);
}

.strip-name {
  padding: 8px;
  font-size: 12px;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

@media (max-width: 860px) {
  .ready {
    grid-template-columns: 1fr;
    justify-items: center;
  }

  .brief {
    width: 100%;
  }
}

@media (prefers-reduced-motion: reduce) {
  .pulse {
    animation: none;
  }
}
</style>
