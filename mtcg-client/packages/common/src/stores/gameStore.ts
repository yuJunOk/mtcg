/**
 * 对局状态（对接 /games API + 短轮询）
 *
 * localPlayerId = String(userStore.userInfo.id)
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { gameApi } from '../api/gameApi'
import type {
  ActionRequestDTO,
  ActionResultVO,
  GameCreateDTO,
  GameStateVO,
  PlayerStateVO,
} from '../types/game'
import { useUserStore } from './userStore'

/** 轮询间隔（ms）；不可操作时拉取对手操作 */
const POLL_INTERVAL_MS = 1800

export const useGameStore = defineStore('game', () => {
  // ========== 状态 ==========
  const gameId = ref<number | null>(null)
  const gameState = ref<GameStateVO | null>(null)
  const localPlayerId = ref<string>('')
  const loading = ref(false)
  const error = ref<string | null>(null)
  const polling = ref(false)

  let pollTimer: ReturnType<typeof setInterval> | null = null

  // ========== 计算属性 ==========
  const localPlayer = computed<PlayerStateVO | null>(() => {
    const gs = gameState.value
    if (!gs || !localPlayerId.value) return null
    if (gs.player1?.playerId === localPlayerId.value) return gs.player1
    if (gs.player2?.playerId === localPlayerId.value) return gs.player2
    return null
  })

  const opponent = computed<PlayerStateVO | null>(() => {
    const gs = gameState.value
    if (!gs || !localPlayerId.value) return null
    if (gs.player1?.playerId === localPlayerId.value) return gs.player2
    if (gs.player2?.playerId === localPlayerId.value) return gs.player1
    return null
  })

  const isLocalPlayerActive = computed(
    () =>
      !!gameState.value &&
      !!localPlayerId.value &&
      gameState.value.activePlayerId === localPlayerId.value,
  )

  const isGameOver = computed(() => gameState.value?.status === 'FINISHED')

  const isLocalPlayerWinner = computed(() => {
    const gs = gameState.value
    if (!gs?.winner || gs.winner === 'DRAW' || !localPlayerId.value) return false
    if (gs.winner === 'PLAYER1') return gs.player1?.playerId === localPlayerId.value
    if (gs.winner === 'PLAYER2') return gs.player2?.playerId === localPlayerId.value
    return false
  })

  const availableActions = computed(() => gameState.value?.availableActions ?? [])

  // ========== 内部 ==========

  function syncLocalPlayerId(): void {
    const userStore = useUserStore()
    const id = userStore.userInfo?.id
    if (id != null) {
      localPlayerId.value = String(id)
    }
  }

  /** 是否应轮询：进行中且非本方操作回合 */
  function shouldPoll(): boolean {
    const gs = gameState.value
    if (!gs || gameId.value == null) return false
    if (gs.status === 'FINISHED') return false
    if (!localPlayerId.value) return false
    return gs.activePlayerId !== localPlayerId.value
  }

  function stopPolling(): void {
    if (pollTimer != null) {
      clearInterval(pollTimer)
      pollTimer = null
    }
    polling.value = false
  }

  function startPolling(): void {
    stopPolling()
    if (gameId.value == null) return
    polling.value = true
    pollTimer = setInterval(() => {
      void refreshQuiet()
    }, POLL_INTERVAL_MS)
  }

  function syncPolling(): void {
    if (shouldPoll()) {
      startPolling()
    } else {
      stopPolling()
    }
  }

  /** 静默刷新（轮询用，不打断 UI loading） */
  async function refreshQuiet(): Promise<void> {
    if (gameId.value == null) return
    try {
      const vo = await gameApi.getState(gameId.value)
      gameState.value = vo
      syncPolling()
    } catch {
      // 轮询失败不刷 error，避免打扰；下次再试
    }
  }

  // ========== 操作 ==========

  /** 设置本地玩家 ID（一般由 syncLocalPlayerId 自动填） */
  function setLocalPlayerId(id: string): void {
    localPlayerId.value = id
  }

  /** 创建对局并加载局面，返回 gameId */
  async function createGame(dto: GameCreateDTO): Promise<number> {
    syncLocalPlayerId()
    loading.value = true
    error.value = null
    try {
      const id = await gameApi.create(dto)
      await loadGame(id)
      return id
    } catch (e) {
      error.value = '创建对局失败'
      throw e
    } finally {
      loading.value = false
    }
  }

  /** 加载对局状态 */
  async function loadGame(id: number | string): Promise<GameStateVO> {
    syncLocalPlayerId()
    const numericId = typeof id === 'string' ? Number(id) : id
    if (!Number.isFinite(numericId) || numericId <= 0) {
      throw new Error('无效的对局 ID')
    }
    loading.value = true
    error.value = null
    try {
      const vo = await gameApi.getState(numericId)
      gameId.value = numericId
      gameState.value = vo
      syncPolling()
      return vo
    } catch (e) {
      error.value = '加载对局失败'
      throw e
    } finally {
      loading.value = false
    }
  }

  /** 执行操作；成功后用返回局面刷新 */
  async function doAction(dto: ActionRequestDTO): Promise<ActionResultVO> {
    if (gameId.value == null) {
      throw new Error('当前无对局')
    }
    loading.value = true
    error.value = null
    try {
      const result = await gameApi.executeAction(gameId.value, dto)
      if (result.gameState) {
        gameState.value = result.gameState
      } else {
        gameState.value = await gameApi.getState(gameId.value)
      }
      syncPolling()
      return result
    } catch (e) {
      error.value = '操作请求失败'
      throw e
    } finally {
      loading.value = false
    }
  }

  /** 认输并刷新局面 */
  async function surrender(): Promise<void> {
    if (gameId.value == null) {
      throw new Error('当前无对局')
    }
    loading.value = true
    error.value = null
    try {
      await gameApi.surrender(gameId.value)
      gameState.value = await gameApi.getState(gameId.value)
      syncPolling()
    } catch (e) {
      error.value = '认输失败'
      throw e
    } finally {
      loading.value = false
    }
  }

  /** 重置对局内存态并停轮询 */
  function reset(): void {
    stopPolling()
    gameId.value = null
    gameState.value = null
    localPlayerId.value = ''
    loading.value = false
    error.value = null
  }

  return {
    // 状态
    gameId,
    gameState,
    localPlayerId,
    loading,
    error,
    polling,
    // 计算
    isLocalPlayerActive,
    localPlayer,
    opponent,
    isGameOver,
    isLocalPlayerWinner,
    availableActions,
    // 操作
    setLocalPlayerId,
    createGame,
    loadGame,
    doAction,
    surrender,
    startPolling,
    stopPolling,
    syncPolling,
    reset,
  }
})
