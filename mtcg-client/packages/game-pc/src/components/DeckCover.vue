<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { resolveCardImageUrl } from '@mtcg/common'

const props = withDefaults(
  defineProps<{
    /** 后端 coverImagePath / imagePath */
    imagePath?: string | null
    /** lazy 仅列表场景 */
    lazy?: boolean
    /** 无图时显示的文字（卡组名首字，避免一排相同 emoji） */
    label?: string | null
    /** 无 label 时的 emoji 占位：商品用 📦 */
    placeholder?: string
  }>(),
  { lazy: false, placeholder: '🃏' },
)

const failed = ref(false)

const src = computed(() => resolveCardImageUrl(props.imagePath))

const showImage = computed(() => Boolean(src.value) && !failed.value)

const letter = computed(() => {
  const raw = props.label?.trim()
  if (!raw) return ''
  return raw.slice(0, 1).toUpperCase()
})

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
  <span class="deck-cover">
    <img
      v-if="showImage"
      :src="src"
      alt=""
      :loading="lazy ? 'lazy' : undefined"
      @error="onError"
    />
    <span v-else-if="letter" class="ph-letter" aria-hidden="true">{{ letter }}</span>
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
  object-fit: contain;
  object-position: center top;
}

.ph {
  font-size: clamp(28px, 28%, 56px);
  line-height: 1;
  user-select: none;
}

.ph-letter {
  display: grid;
  place-items: center;
  width: 42%;
  aspect-ratio: 1;
  border-radius: var(--radius-sm);
  border: 1px solid var(--border);
  background: color-mix(in srgb, var(--accent-soft) 70%, var(--bg-surface));
  color: var(--accent);
  font-size: clamp(22px, 22%, 40px);
  font-weight: 800;
  letter-spacing: 0.04em;
  user-select: none;
}
</style>
