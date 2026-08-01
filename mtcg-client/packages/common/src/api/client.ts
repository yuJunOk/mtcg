import axios from 'axios'
import type { ActionRequest, ActionResponse, GameState } from '../types'

const http = axios.create({
  baseURL: '/api/v1',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' },
})

/** 创建对局 */
export async function createGame(deckId1: string, deckId2: string): Promise<GameState> {
  const res = await http.post('/games', { deckId1, deckId2 })
  return res.data
}

/** 查询对局状态 */
export async function getGameState(gameId: string): Promise<GameState> {
  const res = await http.get(`/games/${gameId}`)
  return res.data
}

/** 执行操作 */
export async function executeAction(request: ActionRequest): Promise<ActionResponse> {
  const res = await http.post(`/games/${request.gameId}/actions`, request)
  return res.data
}

/** 认输 */
export async function surrender(gameId: string, playerId: string): Promise<GameState> {
  const res = await http.post(`/games/${gameId}/surrender`, { playerId })
  return res.data
}

export default http