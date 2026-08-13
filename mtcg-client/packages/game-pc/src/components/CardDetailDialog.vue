<script setup lang="ts">
/**
 * 卡牌详情弹窗（卡表 / 构筑共用）
 * 点击卡图可放大预览。
 */
import { computed } from 'vue'
import { NImage } from 'naive-ui'
import {
  CARD_COLOR_OPTIONS,
  CARD_RARITY_OPTIONS,
  CARD_TYPE_OPTIONS,
  codeToDesc,
  resolveCardImageUrl,
} from '@mtcg/common'
import type { CardColor, CardRarity, CardType, CardVO } from '@mtcg/common'
import MtcgDialog from '@/components/MtcgDialog.vue'

const props = withDefaults(
  defineProps<{
    open: boolean
    card: CardVO | null
  }>(),
  {},
)

const emit = defineEmits<{
  'update:open': [value: boolean]
  close: []
}>()

const title = computed(() =>
  props.card?.cardName ? `🃏 ${props.card.cardName}` : '卡牌详情',
)

const typeLabel = computed(() =>
  props.card ? codeToDesc(CARD_TYPE_OPTIONS, props.card.cardType as CardType) : '',
)

const colorLabel = computed(() => {
  const color = props.card?.color
  return color ? codeToDesc(CARD_COLOR_OPTIONS, color as CardColor) : ''
})

const rarityLabel = computed(() => {
  const rarity = props.card?.rarity
  if (!rarity) return ''
  const desc = codeToDesc(CARD_RARITY_OPTIONS, rarity as CardRarity)
  return desc && desc !== rarity ? `${rarity} · ${desc}` : rarity
})

const imageUrl = computed(() =>
  props.card ? resolveCardImageUrl(props.card.imagePath) : '',
)

function onClose(): void {
  emit('close')
  emit('update:open', false)
}
</script>

<template>
  <MtcgDialog
    :open="open"
    :title="title"
    width="720px"
    body-height="min(64vh, 560px)"
    :show-footer="false"
    fullscreenable
    @update:open="emit('update:open', $event)"
    @close="onClose"
  >
    <div v-if="card" class="detail">
      <div class="detail-art">
        <span class="art-ph" aria-hidden="true">🃏</span>
        <n-image
          v-if="imageUrl"
          class="preview-img"
          :src="imageUrl"
          :alt="card.cardName"
          object-fit="contain"
          :img-props="{ style: { width: '100%', height: '100%', objectPosition: 'center top' } }"
        />
        <p v-if="imageUrl" class="preview-hint">点击卡图可放大预览</p>
      </div>
      <dl class="meta">
        <div>
          <dt>编号</dt>
          <dd>{{ card.cardCode }}</dd>
        </div>
        <div>
          <dt>类型</dt>
          <dd>{{ typeLabel || card.cardType }}</dd>
        </div>
        <div v-if="colorLabel">
          <dt>颜色</dt>
          <dd>{{ colorLabel }}</dd>
        </div>
        <div v-if="card.rarity">
          <dt>稀有度</dt>
          <dd>{{ rarityLabel }}</dd>
        </div>
        <div v-if="card.level != null">
          <dt>等级</dt>
          <dd>{{ card.level }}</dd>
        </div>
        <div v-if="card.attackRange != null">
          <dt>攻击距离</dt>
          <dd>{{ card.attackRange }}</dd>
        </div>
        <div v-if="card.power != null">
          <dt>力量</dt>
          <dd>{{ card.power }}</dd>
        </div>
        <div v-if="card.traits">
          <dt>特征</dt>
          <dd>{{ card.traits }}</dd>
        </div>
        <div v-if="card.productCode">
          <dt>系列</dt>
          <dd>{{ card.productCode }}</dd>
        </div>
        <div v-if="card.effectText" class="effect">
          <dt>效果</dt>
          <dd>{{ card.effectText }}</dd>
        </div>
        <div v-else class="effect">
          <dt>效果</dt>
          <dd class="muted">无效果文本</dd>
        </div>
      </dl>
    </div>
  </MtcgDialog>
</template>

<style scoped>
.detail {
  display: grid;
  grid-template-columns: 240px 1fr;
  gap: 20px;
  align-items: start;
}

.detail-art {
  position: relative;
  width: 240px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.detail-art :deep(.n-image) {
  position: relative;
  z-index: 1;
  width: 240px;
  height: calc(240px * 2080 / 1488);
  border-radius: var(--radius-md);
  overflow: hidden;
  border: 1px solid var(--border);
  background: var(--bg-surface-2);
  cursor: zoom-in;
}

.detail-art :deep(.n-image img) {
  width: 100%;
  height: 100%;
  object-fit: contain;
  object-position: center top;
}

.art-ph {
  position: absolute;
  top: 0;
  left: 0;
  width: 240px;
  height: calc(240px * 2080 / 1488);
  z-index: 0;
  display: grid;
  place-items: center;
  font-size: 40px;
  line-height: 1;
  color: var(--text-secondary);
  pointer-events: none;
  border-radius: var(--radius-md);
  border: 1px solid var(--border);
  background: var(--bg-surface-2);
}

.preview-hint {
  margin: 0;
  font-size: 11px;
  color: var(--text-secondary);
  text-align: center;
}

.meta {
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.meta > div {
  display: grid;
  grid-template-columns: 88px 1fr;
  gap: 8px;
  align-items: start;
}

.meta dt {
  margin: 0;
  font-size: 12px;
  color: var(--text-secondary);
}

.meta dd {
  margin: 0;
  font-size: 14px;
  color: var(--text-primary);
  white-space: pre-wrap;
  word-break: break-word;
}

.meta .effect {
  grid-template-columns: 1fr;
  padding-top: 8px;
  border-top: 1px solid var(--border);
}

.meta .effect dt {
  margin-bottom: 4px;
}

.muted {
  color: var(--text-secondary);
}

@media (max-width: 640px) {
  .detail {
    grid-template-columns: 1fr;
  }

  .detail-art,
  .detail-art :deep(.n-image),
  .art-ph {
    width: min(240px, 100%);
    margin: 0 auto;
  }

  .detail-art :deep(.n-image),
  .art-ph {
    height: auto;
    aspect-ratio: 1488 / 2080;
  }
}
</style>
