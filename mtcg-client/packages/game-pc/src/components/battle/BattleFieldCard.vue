<script setup lang="ts">
import { computed } from 'vue'
import { resolveCardImageUrl } from '@mtcg/common'
import type { CardInstanceVO } from '@mtcg/common'
import { Card } from '@mtcg/common/components'

const props = withDefaults(
  defineProps<{
    card: CardInstanceVO
    side: 'local' | 'opponent'
    /** 冲击卡横置（时间线等） */
    horizontal?: boolean
    backType?: 'character' | 'rush'
    /** 手牌对己方始终正面 */
    forceFaceUp?: boolean
  }>(),
  {
    horizontal: false,
    backType: 'character',
    forceFaceUp: false,
  },
)

const image = computed(() => {
  const code = props.card.cardCode
  if (!code) return ''
  const product = code.split('-')[0]
  return resolveCardImageUrl(`card/faces/${product}/${code}.png`)
})

const faceDown = computed(() => {
  if (props.forceFaceUp) return false
  return !!props.card.isFaceDown || !props.card.cardCode
})
</script>

<template>
  <!-- Card 根节点是 absolute；必须由本宿主提供宽高 -->
  <div class="battle-card-host" :class="{ horizontal }">
    <Card
      :image="image"
      :is-face-down="faceDown"
      :level="card.level ?? undefined"
      :power="card.currentPower ?? undefined"
      :attack-range="card.currentRange ?? undefined"
      :back-type="backType"
      :horizontal="horizontal"
      :side="side"
    />
  </div>
</template>

<style scoped>
.battle-card-host {
  position: relative;
  display: block;
  width: 100%;
  height: 100%;
  min-width: 0;
  min-height: 0;
  overflow: hidden;
  border-radius: inherit;
}

.battle-card-host :deep(.card-face) {
  position: absolute !important;
  inset: 0 !important;
  top: 0 !important;
  left: 0 !important;
  width: auto !important;
  height: auto !important;
  transform: none !important;
  border-radius: inherit;
  pointer-events: none;
}

/* 横置：在宿主内旋转，宿主本身用横卡比例 */
.battle-card-host.horizontal :deep(.card-face) {
  top: 50% !important;
  left: 50% !important;
  width: calc(var(--card-h, 72px) * 0.717) !important;
  height: var(--card-h, 72px) !important;
  transform: translate(-50%, -50%) rotate(90deg) !important;
}
</style>
