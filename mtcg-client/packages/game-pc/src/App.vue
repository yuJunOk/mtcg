<script setup lang="ts">
import { ref } from 'vue'
import HomeView from './views/HomeView.vue'
import BattleView from './views/BattleView.vue'

const currentView = ref<'home' | 'battle'>('home')
</script>

<template>
  <HomeView v-if="currentView === 'home'" @navigate="currentView = 'battle'" />
  <BattleView v-else @navigate="currentView = 'home'" />
</template>