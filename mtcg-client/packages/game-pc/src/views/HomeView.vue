<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { NButton, NTag } from 'naive-ui'
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
          <p class="kicker">MTCG</p>
          <h1>超英击战</h1>
          <p class="lead">集结超英，构筑击战。一张牌，扭转战局。</p>
          <div class="hero-actions">
            <n-button type="primary" size="large" class="cta-main" @click="router.push('/match')">
              ⚡ 开始对战
            </n-button>
            <n-button size="large" secondary class="cta-sub" @click="router.push('/decks')">
              🃏 管理卡组
            </n-button>
          </div>
          <n-tag size="small" :bordered="false" class="hero-status" round>
            {{ rosterHint }}
          </n-tag>
        </div>

        <div class="hero-stage" aria-hidden="true">
          <div class="hero-glow" />
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
      <nav class="quick" aria-label="快捷入口">
        <n-button secondary @click="router.push('/cards')">🎴 卡表</n-button>
        <n-button secondary @click="router.push('/products')">📦 商品</n-button>
        <n-button quaternary disabled>🏆 排行 · 后续</n-button>
      </nav>

      <section class="block">
        <header class="sec">
          <div class="sec-text">
            <h2>🃏 我的卡组</h2>
            <p class="sec-hint">出战用的牌组，点封面进入编辑</p>
          </div>
          <n-button text type="primary" @click="router.push('/decks')">全部 →</n-button>
        </header>
        <div v-if="previewDecks.length === 0" class="empty panel">
          <span class="empty-ico" aria-hidden="true">🃏</span>
          <p>还没有卡组</p>
          <n-button type="primary" secondary @click="router.push('/decks/new')">✨ 去构筑</n-button>
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
              {{ deck.isValid ? '✓ 合法' : '… 未完成' }}
              · {{ deck.mainDeckSize ?? 0 }}/50
            </span>
          </button>
        </CardRail>
      </section>

      <section class="block">
        <header class="sec">
          <div class="sec-text">
            <h2>📦 商品系列</h2>
            <p class="sec-hint">点系列进入该系列卡表</p>
          </div>
          <n-button text type="primary" @click="router.push('/products')">全部 →</n-button>
        </header>
        <div v-if="series.length === 0" class="empty panel">
          <span class="empty-ico" aria-hidden="true">📦</span>
          <p>暂无系列数据</p>
        </div>
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
  min-height: min(58vh, 540px);
  display: flex;
  align-items: center;
  overflow: hidden;
  border-bottom: 1px solid var(--border);
}

.hero-art {
  position: absolute;
  inset: 0;
  background: var(--shell-art) center / cover no-repeat;
  opacity: 1;
  filter: none;
  transform: scale(1.02);
}

/* 左侧保可读，右侧让氛围图与叠卡透出 */
.hero-veil {
  position: absolute;
  inset: 0;
  background:
    linear-gradient(
      105deg,
      color-mix(in srgb, var(--bg-base) 82%, transparent) 0%,
      color-mix(in srgb, var(--bg-base) 36%, transparent) 40%,
      color-mix(in srgb, var(--bg-base) 8%, transparent) 70%,
      transparent 100%
    ),
    linear-gradient(
      180deg,
      transparent 50%,
      color-mix(in srgb, var(--bg-base) 55%, transparent) 100%
    );
}

.hero-inner {
  position: relative;
  z-index: 1;
  width: min(var(--shell-max), 100%);
  margin: 0 auto;
  padding: 36px var(--space-lg) 32px;
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(340px, 500px);
  align-items: center;
  gap: 20px 32px;
}

.hero-copy {
  min-width: 0;
}

.kicker {
  margin: 0;
  display: inline-block;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 11px;
  letter-spacing: 0.22em;
  font-weight: 700;
  color: var(--accent);
  background: color-mix(in srgb, var(--accent) 14%, transparent);
  border: 1px solid color-mix(in srgb, var(--accent) 28%, var(--border));
}

.hero-inner h1 {
  margin: 14px 0 0;
  font-size: clamp(40px, 5.6vw, 64px);
  font-weight: 800;
  letter-spacing: 0.04em;
  line-height: 1.05;
  color: var(--text-primary);
  text-shadow: 0 1px 0 color-mix(in srgb, var(--bg-base) 40%, transparent);
}

.lead {
  margin: 14px 0 0;
  color: var(--text-secondary);
  font-size: var(--font-size-md);
  max-width: 28em;
  line-height: 1.65;
}

.hero-actions {
  margin-top: 22px;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.cta-main {
  min-width: 148px;
  font-weight: 700;
}

.cta-sub {
  min-width: 140px;
  font-weight: 600;
}

.hero-status {
  margin-top: 14px;
  background: color-mix(in srgb, var(--bg-surface) 88%, transparent) !important;
  color: var(--text-secondary) !important;
}

.hero-stage {
  position: relative;
  display: flex;
  justify-content: flex-end;
  align-items: flex-end;
  height: 360px;
}

.hero-glow {
  position: absolute;
  right: 8%;
  bottom: 8%;
  width: 70%;
  height: 55%;
  border-radius: 50%;
  background: color-mix(in srgb, var(--accent) 22%, transparent);
  filter: blur(36px);
  pointer-events: none;
}

.hero-stack {
  position: relative;
  width: 460px;
  height: 360px;
}

.hero-card {
  position: absolute;
  bottom: 0;
  display: block;
  width: 220px;
  height: calc(220px * 2080 / 1488);
  border-radius: var(--radius-md);
  overflow: hidden;
  background: var(--bg-surface);
  border: 1px solid var(--border-light);
  box-shadow: var(--shadow-lg);
  animation: hero-in 560ms cubic-bezier(0.22, 1, 0.36, 1) both;
  transition: transform 180ms ease, box-shadow 180ms ease;
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
  transform: rotate(-11deg) translateY(12px);
  z-index: 1;
}

.hero-card.slot-1 {
  left: 28%;
  transform: rotate(1deg) translateY(-10px);
  z-index: 3;
}

.hero-card.slot-2 {
  left: 54%;
  transform: rotate(12deg) translateY(14px);
  z-index: 2;
}

.hero-card:hover {
  z-index: 4;
  transform: translateY(-10px) rotate(0deg) scale(1.02);
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
  padding: 20px var(--space-lg) 40px;
}

.quick {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 8px;
}

.block {
  margin-top: 22px;
  padding: 16px 16px 14px;
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  background: var(--bg-surface);
  box-shadow: var(--shadow-sm), var(--edge-highlight);
}

.block + .block {
  margin-top: 18px;
}

.sec {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
  padding-bottom: 10px;
  border-bottom: 1px solid var(--border);
}

.sec-text {
  min-width: 0;
}

.sec h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
}

.sec-hint {
  margin: 4px 0 0;
  font-size: 12px;
  color: var(--text-secondary);
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
  background: var(--bg-elevated);
  color: inherit;
  cursor: pointer;
  text-align: left;
  box-shadow: var(--shadow-sm);
  transition:
    transform var(--transition-fast),
    border-color var(--transition-fast),
    box-shadow var(--transition-fast);
}

.strip-card:hover {
  transform: translateY(-4px);
  border-color: color-mix(in srgb, var(--accent) 45%, var(--border));
  box-shadow: var(--shadow-md);
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
  font-weight: 600;
}

.empty {
  padding: 8px 0;
  color: var(--text-secondary);
  font-size: var(--font-size-sm);
}

.empty.panel {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 28px 16px;
  text-align: center;
  border-radius: var(--radius-md);
  background: var(--bg-surface-2);
}

.empty-ico {
  font-size: 28px;
  line-height: 1;
}

.empty.panel p {
  margin: 0;
}

@media (max-width: 960px) {
  .hero {
    min-height: auto;
  }

  .hero-inner {
    grid-template-columns: 1fr;
    padding-top: 22px;
    padding-bottom: 18px;
  }

  .hero-stage {
    height: 280px;
    justify-content: center;
    order: -1;
  }

  .hero-stack {
    width: 360px;
    height: 280px;
  }

  .hero-card {
    width: 168px;
    height: calc(168px * 2080 / 1488);
  }

  .block {
    padding: 12px;
  }
}

@media (prefers-reduced-motion: reduce) {
  .hero-card {
    animation: none;
  }
}
</style>
