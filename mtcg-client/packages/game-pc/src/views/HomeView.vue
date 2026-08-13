<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { deckApi, productApi, resolveCardImageUrl } from '@mtcg/common'
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

/** 英雄区固定复联三巨头真卡面，不用卡背/先后手草稿 */
const heroCards = HERO_TRIO_PATHS.map((path) => resolveCardImageUrl(path))

const rosterHint = computed(() => {
  if (deckCount.value === 0) return '还没有卡组，先去构筑一套'
  if (validCount.value === 0) return `${deckCount.value} 套卡组 · 尚未有合法出战套`
  return `${validCount.value} 套可出战 · 共 ${deckCount.value} 套`
})

function onImgError(e: Event): void {
  const el = e.target as HTMLImageElement
  el.style.visibility = 'hidden'
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
          <p class="kicker">漫威对战卡牌</p>
          <h1>超英击战</h1>
          <p class="lead">构筑超英卡组，进入大厅匹配。卡面是主角，规则写在卡上。</p>
          <div class="hero-actions">
            <button type="button" class="primary" @click="router.push('/match')">开始对战</button>
            <button type="button" class="ghost" @click="router.push('/decks')">管理卡组</button>
          </div>
        </div>
        <div class="hero-stack" aria-hidden="true">
          <span v-for="src in heroCards" :key="src" class="hero-card">
            <img :src="src" alt="" @error="onImgError" />
          </span>
        </div>
      </div>
    </section>

    <div class="well">
      <section class="block roster">
        <p class="roster-hint">{{ rosterHint }}</p>
        <p class="later">
          <button type="button" class="link muted" @click="router.push('/coming-soon')">图鉴</button>
          ·
          <button type="button" class="link muted" @click="router.push('/coming-soon')">排行</button>
          后续开放
        </p>
      </section>

      <section class="block">
        <header class="sec">
          <h2>我的卡组</h2>
          <button type="button" class="more" @click="router.push('/decks')">全部 →</button>
        </header>
        <div v-if="previewDecks.length === 0" class="empty">
          还没有卡组。
          <button type="button" class="link" @click="router.push('/decks/new')">去构筑</button>
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
        </header>
        <div v-if="series.length === 0" class="empty">暂无系列数据</div>
        <CardRail v-else :item-count="series.length">
          <div v-for="product in series" :key="product.id" class="strip-card static">
            <span class="strip-art">
              <DeckCover :image-path="product.imagePath" placeholder="📦" />
            </span>
            <span class="strip-name">{{ product.productName || product.productCode }}</span>
            <span class="strip-meta">{{ product.productCode }}</span>
          </div>
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
  min-height: min(72vh, 620px);
  display: flex;
  align-items: flex-end;
  overflow: hidden;
}

.hero-art {
  position: absolute;
  inset: 0;
  background: var(--shell-art) center / cover no-repeat;
  opacity: var(--shell-art-opacity);
  filter: var(--shell-art-filter);
}

.hero-veil {
  position: absolute;
  inset: 0;
  background: var(--shell-scrim);
}

.hero-inner {
  position: relative;
  z-index: 1;
  width: min(var(--shell-max), 100%);
  margin: 0 auto;
  padding: 56px var(--space-lg) 48px;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 32px;
}

.hero-copy {
  min-width: 0;
  flex: 1;
}

.hero-stack {
  display: flex;
  align-items: flex-end;
  flex-shrink: 0;
  padding: 8px 12px 0 0;
}

.hero-card {
  display: block;
  width: 240px;
  height: calc(240px * 2080 / 1488);
  flex: none;
  border-radius: var(--radius-md);
  overflow: hidden;
  background: var(--bg-surface-2);
  border: 1px solid var(--border);
  box-shadow: var(--shadow-lg);
  transition: transform var(--transition-fast);
}

.hero-card img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  object-position: center top;
}

.hero-card:nth-child(1) {
  transform: rotate(-9deg) translateY(18px);
  z-index: 1;
}

.hero-card:nth-child(2) {
  margin-left: -64px;
  transform: rotate(2deg) translateY(-6px);
  z-index: 2;
}

.hero-card:nth-child(3) {
  margin-left: -64px;
  transform: rotate(11deg) translateY(20px);
  z-index: 1;
}

.hero-card:hover {
  z-index: 3;
  transform: translateY(-8px) rotate(0deg);
}

.kicker {
  margin: 0;
  font-size: 13px;
  letter-spacing: 0.22em;
  font-weight: 700;
  color: var(--accent);
}

.hero-inner h1 {
  margin: 14px 0 0;
  font-size: clamp(40px, 6vw, 64px);
  font-weight: 800;
  letter-spacing: 0.06em;
  max-width: 12em;
  line-height: 1.12;
}

.lead {
  margin: 16px 0 0;
  color: var(--text-secondary);
  font-size: var(--font-size-md);
  max-width: 28em;
  line-height: 1.7;
}

.hero-actions {
  margin-top: 28px;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.well {
  width: min(var(--shell-max), 100%);
  margin: 0 auto;
  padding: 28px var(--space-lg) 72px;
}

.block + .block {
  margin-top: 36px;
}

.sec {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 14px;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--border);
}

.sec h2 {
  margin: 0;
  font-size: 18px;
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

.link.muted {
  color: var(--text-secondary);
  font-weight: 500;
}

.roster {
  display: flex;
  flex-wrap: wrap;
  align-items: baseline;
  justify-content: space-between;
  gap: 8px 16px;
}

.roster-hint {
  margin: 0;
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

.roster .later {
  margin: 0;
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

.strip-card.static {
  cursor: default;
}

.strip-card.static:hover {
  transform: none;
  border-color: var(--border);
}

.empty {
  padding: 20px 0;
  color: var(--text-secondary);
  font-size: var(--font-size-sm);
}

.later {
  margin: 12px 0 0;
  font-size: 12px;
  color: var(--text-disabled);
}

.primary,
.ghost {
  height: 40px;
  padding: 0 18px;
  border-radius: var(--radius-sm);
  font-size: var(--font-size-sm);
  font-weight: 600;
  cursor: pointer;
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
}

@media (max-width: 960px) {
  .hero-inner {
    flex-direction: column;
    align-items: flex-start;
  }

  .hero-stack {
    align-self: center;
    padding-right: 0;
  }

  .hero-card {
    width: 168px;
    height: calc(168px * 2080 / 1488);
  }

  .hero-card:nth-child(2),
  .hero-card:nth-child(3) {
    margin-left: -40px;
  }
}
</style>
