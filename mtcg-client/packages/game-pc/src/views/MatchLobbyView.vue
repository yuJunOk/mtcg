<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { deckApi, gameApi } from '@mtcg/common'
import { useGameStore } from '@mtcg/common/stores'
import type { CreateAIGameDTO, DeckVO } from '@mtcg/common'
import AiBattleDialog from '@/components/AiBattleDialog.vue'
import CardRail from '@/components/CardRail.vue'
import DeckCover from '@/components/DeckCover.vue'

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
      await enterBattle(result.gameId)
      return
    }
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
  } catch {
    // notifier
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
  try {
    await navigator.clipboard.writeText(String(id))
  } catch {
    window.prompt('复制对局 ID', String(id))
  }
}

async function handleJoin(): Promise<void> {
  const deck = requireDeck()
  if (!deck) return
  const id = Number(joinGameId.value.trim())
  if (!Number.isFinite(id) || id <= 0) {
    formError.value = '请填写对局 ID'
    return
  }
  busy.value = true
  try {
    const gameId = await gameApi.join(id, { deckId: deck.id })
    await enterBattle(gameId)
  } catch {
    // notifier
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
    await enterBattle(gameId)
  } catch {
    // 后端 5106，toast 已提示尚未开放
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

    <div v-if="loading" class="empty">加载中…</div>

    <div v-else-if="validDecks.length === 0" class="empty-room">
      <span class="empty-ico" aria-hidden="true">🃏</span>
      <p>还没有合法卡组，无法出战。</p>
      <p class="hint">主卡组 50 张角色 · 冲击卡组 9 张。</p>
      <button type="button" class="primary" @click="router.push('/decks')">去构筑</button>
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
          <div class="meta">
            <span class="chip ok">合法</span>
            <span class="chip">主 {{ selected?.mainDeckSize ?? 0 }}/50</span>
            <span class="chip rush">冲击 {{ selected?.rushDeckSize ?? 0 }}/9</span>
          </div>

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
              <button type="button" class="ghost sm" @click="copyRoomId">复制</button>
            </div>
            <p class="hint">把 ID 发给对手，对方在下方加入即可开打。</p>
            <button type="button" class="ghost" :disabled="busy" @click="handleCancelHost">
              取消房间
            </button>
          </div>

          <div v-else-if="phase === 'offer-ai'" class="status-box">
            <p class="status-title">没有匹配到对手</p>
            <p class="hint">可以创建房间等好友，或先与 AI 练手。</p>
            <div class="cta-row">
              <button type="button" class="primary" :disabled="busy" @click="handleCreateRoom">
                创建房间
              </button>
              <button type="button" class="ghost" :disabled="busy" @click="handleAi">
                与 AI 对战
              </button>
            </div>
            <button type="button" class="link" @click="phase = 'idle'">返回</button>
          </div>

          <div v-else class="cta-row main">
            <button type="button" class="primary" :disabled="busy" @click="handleMatch">
              <svg viewBox="0 0 16 16" width="16" height="16" aria-hidden="true">
                <path
                  fill="currentColor"
                  d="M8.9 1.6 3.2 8.2c-.2.3 0 .7.4.7h3.1l-.8 5.1c-.1.5.5.8.8.4l5.7-6.6c.2-.3 0-.7-.4-.7H8.9l.8-5.1c.1-.5-.5-.8-.8-.4Z"
                />
              </svg>
              在线匹配
            </button>
            <button type="button" class="ghost" :disabled="busy" @click="handleCreateRoom">
              <svg viewBox="0 0 16 16" width="16" height="16" aria-hidden="true">
                <path
                  fill="none"
                  stroke="currentColor"
                  stroke-width="1.4"
                  d="M3 5.5h10M5.5 3v5M10.5 3v5M3.5 8.5h9v4.2a1 1 0 0 1-1 1h-7a1 1 0 0 1-1-1V8.5Z"
                />
              </svg>
              创建房间
            </button>
            <button type="button" class="ghost" :disabled="busy" @click="handleAi">
              <svg viewBox="0 0 16 16" width="16" height="16" aria-hidden="true">
                <rect x="4" y="5" width="8" height="7" rx="1.5" fill="none" stroke="currentColor" stroke-width="1.4" />
                <circle cx="6.5" cy="8.2" r="0.9" fill="currentColor" />
                <circle cx="9.5" cy="8.2" r="0.9" fill="currentColor" />
                <path d="M8 2.5v2.2M5.2 3.6 6.4 5M10.8 3.6 9.6 5" fill="none" stroke="currentColor" stroke-width="1.3" stroke-linecap="round" />
              </svg>
              与 AI 对战
            </button>
          </div>

          <form class="join" @submit.prevent="handleJoin">
            <label class="field grow">
              <span>加入房间</span>
              <input
                v-model="joinGameId"
                type="text"
                inputmode="numeric"
                placeholder="输入对局 ID"
                :disabled="locked"
              />
            </label>
            <button class="ghost join-btn" type="submit" :disabled="locked">
              {{ busy ? '请稍候…' : '加入' }}
            </button>
          </form>
        </div>
      </section>

      <section class="block">
        <header class="sec">
          <h2>可出战卡组</h2>
          <button type="button" class="more" @click="router.push('/decks')">全部 →</button>
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

.empty {
  padding: 40px 0;
  color: var(--text-secondary);
}

.empty-room {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 48px 24px;
  text-align: center;
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  background: var(--bg-surface);
  box-shadow: var(--shadow-sm), var(--edge-highlight);
}

.empty-ico {
  font-size: 40px;
  line-height: 1;
  margin-bottom: 12px;
}

.empty-room p {
  margin: 0;
  color: var(--text-secondary);
}

.empty-room .hint {
  margin-top: 6px;
  font-size: var(--font-size-sm);
}

.empty-room .primary {
  margin-top: 20px;
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
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin: 12px 0 0;
}

.chip {
  display: inline-flex;
  align-items: center;
  height: 26px;
  padding: 0 10px;
  border-radius: 999px;
  border: 1px solid var(--border);
  background: color-mix(in srgb, var(--bg-surface-2) 80%, transparent);
  font-size: 12px;
  color: var(--text-secondary);
  font-variant-numeric: tabular-nums;
}

.chip.ok {
  color: var(--accent);
  border-color: color-mix(in srgb, var(--accent) 35%, var(--border));
  font-weight: 600;
}

.chip.rush {
  color: var(--accent-gold);
  border-color: color-mix(in srgb, var(--accent-gold) 30%, var(--border));
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

.ghost.sm {
  height: 32px;
  padding: 0 12px;
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
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

.field.grow {
  flex: 1;
  min-width: 0;
}

.field input {
  height: 40px;
  padding: 0 12px;
  border-radius: var(--radius-sm);
  border: 1px solid var(--border);
  background: var(--bg-surface-2);
  color: var(--text-primary);
  outline: none;
}

.field input:focus {
  border-color: var(--accent);
  box-shadow: var(--shadow-glow);
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

.more,
.link {
  border: none;
  background: transparent;
  color: var(--accent);
  font-size: var(--font-size-sm);
  font-weight: 600;
  cursor: pointer;
}

.status-box .link {
  margin-top: 12px;
  padding: 0;
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

.primary,
.ghost {
  height: 40px;
  padding: 0 16px;
  border-radius: var(--radius-sm);
  font-size: var(--font-size-sm);
  font-weight: 600;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 7px;
  width: auto;
}

.primary {
  border: none;
  background: var(--accent);
  color: var(--accent-contrast);
}

.ghost {
  border: 1px solid var(--border);
  background: color-mix(in srgb, var(--bg-surface) 70%, transparent);
  color: var(--text-primary);
  flex-shrink: 0;
}

.join-btn {
  height: 40px;
}

.primary svg,
.ghost svg {
  flex-shrink: 0;
}

.primary:disabled,
.ghost:disabled {
  opacity: 0.5;
  cursor: not-allowed;
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
