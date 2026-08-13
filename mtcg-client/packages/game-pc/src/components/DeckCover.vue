<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { resolveCardImageUrl } from '@mtcg/common'

const props = withDefaults(
  defineProps<{
    /** 后端 coverImagePath / imagePath */
    imagePath?: string | null
    /** lazy 仅列表场景 */
    lazy?: boolean
    /** 无图时 emoji：卡组 🃏，商品 📦 */
    placeholder?: string
    /** contain=完整卡比；cover=裁切铺满（列表瓦片） */
    fit?: 'contain' | 'cover'
  }>(),
  { lazy: false, placeholder: '🃏', fit: 'contain' },
)

const failed = ref(false)

const src = computed(() => resolveCardImageUrl(props.imagePath))

const showImage = computed(() => Boolean(src.value) && !failed.value)

watch(
  () => props.imagePath,
  () => {
    failed.value = false
  },
)

function onError(): void {
  failed.value = true
}
</script>

<template>
  <span class="deck-cover" :class="fit">
    <img
      v-if="showImage"
      :src="src"
      alt=""
      :loading="lazy ? 'lazy' : undefined"
      @error="onError"
    />
    <span v-else class="ph" aria-hidden="true">{{ placeholder }}</span>
  </span>
</template>

<style scoped>
.deck-cover {
  display: grid;
  place-items: center;
  width: 100%;
  height: 100%;
  background: var(--bg-surface-2);
  overflow: hidden;
}

.deck-cover img {
  width: 100%;
  height: 100%;
}

.deck-cover.contain img {
  object-fit: contain;
  object-position: center top;
}

.deck-cover.cover img {
  object-fit: cover;
  object-position: center 18%;
}

.ph {
  font-size: clamp(28px, 28%, 56px);
  line-height: 1;
  user-select: none;
}
</style>
