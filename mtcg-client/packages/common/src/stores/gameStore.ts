import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { GameState, PlayerState, ActionType, CardRenderData } from '../types'
import { getGameState, executeAction } from '../api/client'

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

  /** 加载对局状态 */
  async function loadGame(gameId: string) {
    loading.value = true
    error.value = null
    try {
      gameState.value = await getGameState(gameId)
    } catch (e) {
      error.value = '加载对局失败'
      throw e
    } finally {
      loading.value = false
    }
  }

  /** 执行操作 */
  async function doAction(actionType: ActionType, payload: Record<string, unknown> = {}) {
    if (!gameState.value) return
    loading.value = true
    error.value = null
    try {
      const res = await executeAction({
        gameId: gameState.value.gameId,
        playerId: localPlayerId.value,
        actionType,
        payload,
      })
      if (res.success) {
        gameState.value = res.gameState
      } else {
        error.value = res.message || '操作失败'
      }
      return res
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