<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { NButton } from 'naive-ui'
import { deckApi, productApi, productCoverPath, resolveCardImageUrl } from '@mtcg/common'
import type { DeckVO, ProductVO } from '@mtcg/common'
import { HERO_TRIO_PATHS } from '@/constants/heroArt'
import CardRail from '@/components/CardRail.vue'
import DeckCover from '@/components/DeckCover.vue'

const router = useRouter()
const decks = ref<DeckVO[]>([])
const series = ref<ProductVO[]>([])

const previewDecks = computed(() => decks.value.slice(0, 8))
const deckCount = computed(() => decks.value.length)
const validCount = computed(() => decks.value.filter((d) => d.isValid === true).length)

/** 英雄区固定复联三巨头真卡面 */
const heroCards = HERO_TRIO_PATHS.map((path, i) => ({
  path,
  src: resolveCardImageUrl(path),
  delay: `${0.08 + i * 0.1}s`,
}))

const rosterHint = computed(() => {
  if (deckCount.value === 0) return '还没有卡组，先去构筑一套'
  if (validCount.value === 0) return `${deckCount.value} 套卡组 · 尚未有合法出战套`
  return `${validCount.value} 套可出战 · 共 ${deckCount.value} 套`
})

function onImgError(e: Event): void {
  const el = e.target as HTMLImageElement
  el.classList.add('is-broken')
}

onMounted(async () => {
  try {
    decks.value = await deckApi.list()
  } catch {
    decks.value = []
  }
  try {
    const page = await productApi.list({ pageNum: 1, pageSize: 8 })
    series.value = page.records ?? []
  } catch {
    series.value = []
  }
})
</script>

<template>
  <div class="page">
    <section class="hero">
      <div class="hero-art" aria-hidden="true" />
      <div class="hero-veil" aria-hidden="true" />
      <div class="hero-inner">
        <div class="hero-copy">
          <p class="kicker">超英击战</p>
          <h1>MTCG</h1>
          <p class="lead">集结超英，构筑击战。一张牌，扭转战局。</p>
          <div class="hero-actions">
            <n-button type="primary" size="large" @click="router.push('/match')">
              ⚡ 开始对战
            </n-button>
            <n-button size="large" @click="router.push('/decks')">
              🃏 管理卡组
            </n-button>
          </div>
          <p class="hero-status">{{ rosterHint }}</p>
        </div>

        <div class="hero-stage" aria-hidden="true">
          <div class="hero-stack">
            <span
              v-for="(card, i) in heroCards"
              :key="card.path"
              class="hero-card"
              :class="`slot-${i}`"
              :style="{ animationDelay: card.delay }"
            >
              <img :src="card.src" alt="" @error="onImgError" />
            </span>
          </div>
        </div>
      </div>
    </section>

    <div class="well">
      <p class="later">
        <n-button text type="primary" @click="router.push('/cards')">卡表</n-button>
        ·
        <n-button text type="primary" @click="router.push('/products')">商品</n-button>
        ·
        <n-button text class="muted-link" @click="router.push('/coming-soon')">排行</n-button>
        <span class="later-note">后续开放</span>
      </p>

      <section class="block">
        <header class="sec">
          <h2>我的卡组</h2>
          <n-button text type="primary" @click="router.push('/decks')">全部 →</n-button>
        </header>
        <div v-if="previewDecks.length === 0" class="empty">
          还没有卡组。
          <n-button text type="primary" @click="router.push('/decks/new')">去构筑</n-button>
        </div>
        <CardRail v-else :item-count="previewDecks.length">
          <button
            v-for="deck in previewDecks"
            :key="deck.id"
            type="button"
            class="strip-card"
            @click="router.push(`/decks/${deck.id}`)"
          >
            <span class="strip-art">
              <DeckCover :image-path="deck.coverImagePath" placeholder="🃏" />
            </span>
            <span class="strip-name">{{ deck.deckName }}</span>
            <span class="strip-meta" :class="{ on: deck.isValid }">
              {{ deck.isValid ? '合法' : '未完成' }}
              · {{ deck.mainDeckSize ?? 0 }}/50
            </span>
          </button>
        </CardRail>
      </section>

      <section class="block">
        <header class="sec">
          <h2>商品系列</h2>
          <n-button text type="primary" @click="router.push('/products')">全部 →</n-button>
        </header>
        <div v-if="series.length === 0" class="empty">暂无系列数据</div>
        <CardRail v-else :item-count="series.length">
          <button
            v-for="product in series"
            :key="product.id"
            type="button"
            class="strip-card"
            @click="router.push({ path: '/cards', query: { product: product.productCode } })"
          >
            <span class="strip-art">
              <DeckCover :image-path="productCoverPath(product)" placeholder="📦" />
            </span>
            <span class="strip-name">{{ product.productName || product.productCode }}</span>
            <span class="strip-meta">{{ product.productCode }}</span>
          </button>
        </CardRail>
      </section>
    </div>
  </div>
</template>

<style scoped>
.page {
  min-height: 100%;
  background: var(--bg-base);
}

.hero {
  position: relative;
  isolation: isolate;
  min-height: min(52vh, 480px);
  display: flex;
  align-items: center;
  overflow: hidden;
}

.hero-art {
  position: absolute;
  inset: 0;
  background: var(--shell-art) center / cover no-repeat;
  opacity: 1;
  filter: none;
}

/* 左侧保可读，右侧让氛围图透出；勿叠过重的 shell-scrim */
.hero-veil {
  position: absolute;
  inset: 0;
  background:
    linear-gradient(
      105deg,
      color-mix(in srgb, var(--bg-base) 78%, transparent) 0%,
      color-mix(in srgb, var(--bg-base) 42%, transparent) 38%,
      color-mix(in srgb, var(--bg-base) 12%, transparent) 68%,
      transparent 100%
    ),
    linear-gradient(
      180deg,
      transparent 55%,
      color-mix(in srgb, var(--bg-base) 45%, transparent) 100%
    );
}

.hero-inner {
  position: relative;
  z-index: 1;
  width: min(var(--shell-max), 100%);
  margin: 0 auto;
  padding: 28px var(--space-lg) 24px;
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(320px, 480px);
  align-items: center;
  gap: 16px 28px;
}

.hero-copy {
  min-width: 0;
}

.kicker {
  margin: 0;
  font-size: 12px;
  letter-spacing: 0.2em;
  font-weight: 700;
  color: var(--accent);
}

.hero-inner h1 {
  margin: 8px 0 0;
  font-size: clamp(44px, 6vw, 68px);
  font-weight: 800;
  letter-spacing: 0.06em;
  line-height: 1;
  color: var(--text-primary);
}

.lead {
  margin: 10px 0 0;
  color: var(--text-secondary);
  font-size: var(--font-size-sm);
  max-width: 26em;
  line-height: 1.55;
}

.hero-actions {
  margin-top: 18px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.hero-status {
  margin: 10px 0 0;
  font-size: 12px;
  color: var(--text-secondary);
}

.hero-stage {
  display: flex;
  justify-content: flex-end;
  align-items: flex-end;
  height: 340px;
}

.hero-stack {
  position: relative;
  width: 440px;
  height: 340px;
}

.hero-card {
  position: absolute;
  bottom: 0;
  display: block;
  width: 210px;
  height: calc(210px * 2080 / 1488);
  border-radius: var(--radius-md);
  overflow: hidden;
  background: var(--bg-surface);
  border: 1px solid var(--border);
  box-shadow: var(--shadow-lg);
  animation: hero-in 520ms cubic-bezier(0.22, 1, 0.36, 1) both;
  transition: transform 160ms ease, box-shadow 160ms ease;
}

.hero-card img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  object-position: center top;
  background: var(--bg-surface-2);
}

.hero-card img.is-broken {
  opacity: 0;
}

.hero-card.slot-0 {
  left: 0;
  transform: rotate(-10deg) translateY(10px);
  z-index: 1;
}

.hero-card.slot-1 {
  left: 28%;
  transform: rotate(1deg) translateY(-6px);
  z-index: 3;
}

.hero-card.slot-2 {
  left: 54%;
  transform: rotate(11deg) translateY(12px);
  z-index: 2;
}

.hero-card:hover {
  z-index: 4;
  transform: translateY(-8px) rotate(0deg);
  box-shadow: var(--shadow-lg), var(--shadow-glow);
}

@keyframes hero-in {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.well {
  width: min(var(--shell-max), 100%);
  margin: 0 auto;
  padding: 4px var(--space-lg) 32px;
}

.block {
  margin-top: 18px;
}

.block + .block {
  margin-top: 22px;
}

.sec {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 10px;
  padding-bottom: 6px;
  border-bottom: 1px solid var(--border);
}

.sec h2 {
  margin: 0;
  font-size: 17px;
  font-weight: 700;
}

.muted-link {
  color: var(--text-secondary) !important;
  font-weight: 500;
}

.later {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: flex-end;
  gap: 6px;
  margin: 0 0 2px;
  font-size: 12px;
  color: var(--text-disabled);
}

.later-note {
  margin-left: 2px;
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

.strip-card:hover {
  transform: translateY(-3px);
  border-color: color-mix(in srgb, var(--accent) 40%, var(--border));
}

.strip-art {
  display: block;
  width: var(--card-face-w);
  height: var(--card-face-h);
  flex: none;
  background: var(--bg-surface-2);
}

.strip-name {
  padding: 8px 8px 0;
  font-size: 12px;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.strip-meta {
  padding: 2px 8px 8px;
  font-size: 11px;
  color: var(--text-secondary);
}

.strip-meta.on {
  color: var(--accent);
}

.empty {
  padding: 8px 0;
  color: var(--text-secondary);
  font-size: var(--font-size-sm);
}

@media (max-width: 960px) {
  .hero {
    min-height: auto;
  }

  .hero-inner {
    grid-template-columns: 1fr;
    padding-top: 20px;
    padding-bottom: 12px;
  }

  .hero-stage {
    height: 260px;
    justify-content: center;
    order: -1;
  }

  .hero-stack {
    width: 340px;
    height: 260px;
  }

  .hero-card {
    width: 160px;
    height: calc(160px * 2080 / 1488);
  }
}

@media (prefers-reduced-motion: reduce) {
  .hero-card {
    animation: none;
  }
}
</style>
