import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { GameState, PlayerState, ActionType } from '../types'

export const useGameStore = defineStore('game', () => {
  // ========== 状态 ==========
  const gameState = ref<GameState | null>(null)
  const localPlayerId = ref<string>('')
  const loading = ref(false)
  const error = ref<string | null>(null)

  // ========== 计算属性 ==========
  const isLocalPlayerActive = computed(() =>
    gameState.value?.activePlayer?.playerId === localPlayerId.value
  )

  const localPlayer = computed<PlayerState | null>(() => {
    if (!gameState.value) return null
    return gameState.value.activePlayer.playerId === localPlayerId.value
      ? gameState.value.activePlayer
      : gameState.value.inactivePlayer
  })

  const opponent = computed<PlayerState | null>(() => {
    if (!gameState.value) return null
    return gameState.value.activePlayer.playerId === localPlayerId.value
      ? gameState.value.inactivePlayer
      : gameState.value.activePlayer
  })

  const isGameOver = computed(() =>
    gameState.value?.status === 'FINISHED'
  )

  const isLocalPlayerWinner = computed(() =>
    gameState.value?.winnerId === localPlayerId.value
  )

  // ========== 操作 ==========

  /** 加载对局状态（迭代四/五接入） */
  async function loadGame(_gameId: string) {
    loading.value = true
    error.value = null
    try {
      // TODO(迭代四/五): await client.game.getState(_gameId)
      throw new Error('loadGame 未接入，请先完成迭代四/五')
    } catch (e) {
      error.value = '加载对局失败'
      throw e
    } finally {
      loading.value = false
    }
  }

  /** 执行操作（迭代四/五接入） */
  async function doAction(_actionType: ActionType, _payload: Record<string, unknown> = {}) {
    if (!gameState.value) return
    loading.value = true
    error.value = null
    try {
      // TODO(迭代四/五): await client.game.executeAction(...)
      throw new Error('doAction 未接入，请先完成迭代四/五')
    } catch (e) {
      error.value = '操作请求失败'
      throw e
    } finally {
      loading.value = false
    }
  }

  /** 设置本地玩家 ID */
  function setLocalPlayerId(id: string) {
    localPlayerId.value = id
  }

  /** 重置 */
  function reset() {
    gameState.value = null
    localPlayerId.value = ''
    loading.value = false
    error.value = null
  }

  return {
    // 状态
    gameState,
    localPlayerId,
    loading,
    error,
    // 计算
    isLocalPlayerActive,
    localPlayer,
    opponent,
    isGameOver,
    isLocalPlayerWinner,
    // 操作
    loadGame,
    doAction,
    setLocalPlayerId,
    reset,
  }
})