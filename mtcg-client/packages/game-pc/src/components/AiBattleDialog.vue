<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import type { AIDifficulty, AIPlayStyle, CreateAIGameDTO, DeckVO, GameSeat } from '@mtcg/common'
import DeckCover from '@/components/DeckCover.vue'
import MtcgDialog from '@/components/MtcgDialog.vue'

const PREF_KEY = 'mtcg_ai_pref'

interface AiPref {
  difficulty: AIDifficulty
  playStyle: AIPlayStyle
  firstPlayer: GameSeat | ''
  aiDeckId: number | null
}

const props = defineProps<{
  open: boolean
  busy: boolean
  humanDeck: DeckVO
  decks: DeckVO[]
}>()

const emit = defineEmits<{
  close: []
  confirm: [dto: CreateAIGameDTO]
}>()

const difficulty = ref<AIDifficulty>('INTERMEDIATE')
const playStyle = ref<AIPlayStyle>('AGGRESSIVE')
const firstPlayer = ref<GameSeat | ''>('')
const aiDeckId = ref<number | null>(null)

const difficulties: { id: AIDifficulty; label: string; hint: string }[] = [
  { id: 'BEGINNER', label: '新手', hint: '常出次优' },
  { id: 'INTERMEDIATE', label: '进阶', hint: '多数选优' },
  { id: 'EXPERT', label: '专家', hint: '几乎最优' },
]

const styles: { id: AIPlayStyle; label: string }[] = [
  { id: 'AGGRESSIVE', label: '激进' },
  { id: 'DEFENSIVE', label: '防守' },
  { id: 'CONTROL', label: '控制' },
]

const seats: { id: GameSeat | ''; label: string }[] = [
  { id: '', label: '随机' },
  { id: 'PLAYER1', label: '我先手' },
  { id: 'PLAYER2', label: 'AI 先手' },
]

const aiDeck = computed<DeckVO | null>(() => {
  const id = aiDeckId.value
  if (id == null) return props.decks[0] ?? null
  return props.decks.find((d) => d.id === id) ?? props.decks[0] ?? null
})

const difficultyHint = computed(
  () => difficulties.find((d) => d.id === difficulty.value)?.hint ?? '',
)

function loadPref(): AiPref | null {
  try {
    const raw = localStorage.getItem(PREF_KEY)
    if (!raw) return null
    return JSON.parse(raw) as AiPref
  } catch {
    return null
  }
}

function savePref(): void {
  const pref: AiPref = {
    difficulty: difficulty.value,
    playStyle: playStyle.value,
    firstPlayer: firstPlayer.value,
    aiDeckId: aiDeckId.value,
  }
  localStorage.setItem(PREF_KEY, JSON.stringify(pref))
}

function hydrate(): void {
  const pref = loadPref()
  difficulty.value = pref?.difficulty ?? 'INTERMEDIATE'
  playStyle.value = pref?.playStyle ?? 'AGGRESSIVE'
  firstPlayer.value = pref?.firstPlayer ?? ''
  const savedId = pref?.aiDeckId
  if (savedId != null && props.decks.some((d) => d.id === savedId)) {
    aiDeckId.value = savedId
  } else {
    aiDeckId.value = props.decks[0]?.id ?? props.humanDeck.id
  }
}

function submit(): void {
  const deck = aiDeck.value
  if (!deck) return
  savePref()
  const dto: CreateAIGameDTO = {
    humanDeckId: props.humanDeck.id,
    aiDeckId: deck.id,
    difficulty: difficulty.value,
    playStyle: playStyle.value,
  }
  if (firstPlayer.value) {
    dto.firstPlayer = firstPlayer.value
  }
  emit('confirm', dto)
}

watch(
  () => props.open,
  (open) => {
    if (open) hydrate()
  },
)
</script>

<template>
  <MtcgDialog
    :open="open"
    title="与 AI 对战"
    width="560px"
    body-height="min(56vh, 520px)"
    fullscreenable
    confirm-text="开始对战"
    :confirm-loading="busy"
    :confirm-disabled="!aiDeck"
    :close-disabled="busy"
    @close="emit('close')"
    @cancel="emit('close')"
    @confirm="submit"
  >
    <p class="lead">选对手卡组与难度。自动行棋下一迭代开放。</p>

    <section class="matchup">
      <div class="side">
        <span class="badge">我</span>
        <div class="cover">
          <DeckCover :image-path="humanDeck.coverImagePath" placeholder="🃏" />
        </div>
        <p class="side-name">{{ humanDeck.deckName }}</p>
      </div>
      <div class="vs" aria-hidden="true">VS</div>
      <div class="side">
        <span class="badge ai">AI</span>
        <div class="cover">
          <DeckCover :image-path="aiDeck?.coverImagePath" placeholder="🃏" />
        </div>
        <p class="side-name">{{ aiDeck?.deckName ?? '选择卡组' }}</p>
      </div>
    </section>

    <section class="field">
      <div class="field-label">
        <span>AI 卡组</span>
        <em>{{ decks.length }} 套可选</em>
      </div>
      <div class="strip">
        <button
          v-for="deck in decks"
          :key="deck.id"
          type="button"
          class="tile"
          :class="{ on: aiDeck?.id === deck.id }"
          :disabled="busy"
          @click="aiDeckId = deck.id"
        >
          <span class="art">
            <DeckCover :image-path="deck.coverImagePath" placeholder="🃏" />
          </span>
          <span class="name">{{ deck.deckName }}</span>
        </button>
      </div>
    </section>

    <section class="field">
      <div class="field-label">
        <span>难度</span>
        <em>{{ difficultyHint }}</em>
      </div>
      <div class="seg" role="group" aria-label="难度">
        <button
          v-for="item in difficulties"
          :key="item.id"
          type="button"
          class="seg-item"
          :class="{ on: difficulty === item.id }"
          :disabled="busy"
          @click="difficulty = item.id"
        >
          {{ item.label }}
        </button>
      </div>
    </section>

    <section class="field">
      <div class="field-label"><span>打牌倾向</span></div>
      <div class="seg" role="group" aria-label="打牌倾向">
        <button
          v-for="item in styles"
          :key="item.id"
          type="button"
          class="seg-item"
          :class="{ on: playStyle === item.id }"
          :disabled="busy"
          @click="playStyle = item.id"
        >
          {{ item.label }}
        </button>
      </div>
    </section>

    <section class="field">
      <div class="field-label"><span>先后手</span></div>
      <div class="seg" role="group" aria-label="先后手">
        <button
          v-for="item in seats"
          :key="item.label"
          type="button"
          class="seg-item"
          :class="{ on: firstPlayer === item.id }"
          :disabled="busy"
          @click="firstPlayer = item.id"
        >
          {{ item.label }}
        </button>
      </div>
    </section>
  </MtcgDialog>
</template>

<style scoped>
.lead {
  margin: 0 0 16px;
  color: var(--text-secondary);
  font-size: var(--font-size-sm);
  line-height: 1.55;
}

.matchup {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  align-items: end;
  gap: 12px;
  padding: 16px;
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  background: var(--bg-surface-2);
}

.side {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

.badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 28px;
  height: 22px;
  padding: 0 8px;
  border-radius: 999px;
  background: var(--accent-soft);
  color: var(--accent);
  font-size: 11px;
  font-weight: 700;
}

.badge.ai {
  background: color-mix(in srgb, var(--text-secondary) 16%, transparent);
  color: var(--text-secondary);
}

.cover {
  width: 112px;
  height: calc(112px * 2080 / 1488);
  border-radius: var(--radius-sm);
  overflow: hidden;
  border: 1px solid var(--border);
  background: var(--bg-surface);
  box-shadow: var(--shadow-sm);
}

.side-name {
  margin: 0;
  max-width: 100%;
  font-size: 12px;
  font-weight: 600;
  text-align: center;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.vs {
  align-self: center;
  padding-bottom: 28px;
  font-size: 13px;
  font-weight: 800;
  letter-spacing: 0.12em;
  color: var(--text-disabled);
}

.field {
  margin-top: 18px;
}

.field-label {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 8px;
  font-size: 13px;
  font-weight: 700;
}

.field-label em {
  font-style: normal;
  font-weight: 500;
  font-size: 12px;
  color: var(--text-secondary);
}

.strip {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding: 4px 2px 6px;
  scrollbar-width: none;
}

.strip::-webkit-scrollbar {
  display: none;
}

.tile {
  flex: 0 0 76px;
  width: 76px;
  padding: 0;
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  overflow: hidden;
  background: var(--bg-surface-2);
  color: inherit;
  cursor: pointer;
  text-align: left;
  transition: border-color var(--transition-fast), transform var(--transition-fast);
}

.tile:hover:not(:disabled) {
  transform: translateY(-2px);
}

.tile.on {
  border-color: var(--accent);
}

.art {
  display: block;
  width: 76px;
  height: calc(76px * 2080 / 1488);
  background: var(--bg-surface-2);
}

.name {
  display: block;
  padding: 5px 6px;
  font-size: 10px;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.seg {
  display: grid;
  grid-auto-flow: column;
  grid-auto-columns: 1fr;
  gap: 0;
  padding: 3px;
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  background: var(--bg-surface-2);
}

.seg-item {
  height: 36px;
  border: none;
  border-radius: calc(var(--radius-sm) - 2px);
  background: transparent;
  color: var(--text-secondary);
  font-size: var(--font-size-sm);
  font-weight: 600;
  cursor: pointer;
  transition:
    background var(--transition-fast),
    color var(--transition-fast);
}

.seg-item.on {
  background: var(--bg-surface);
  color: var(--text-primary);
  box-shadow: var(--shadow-sm);
}

.seg-item:hover:not(:disabled):not(.on) {
  color: var(--text-primary);
}

.tile:disabled,
.seg-item:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

@media (max-width: 520px) {
  .matchup {
    gap: 8px;
    padding: 12px;
  }

  .cover {
    width: 88px;
    height: calc(88px * 2080 / 1488);
  }
}
</style>
