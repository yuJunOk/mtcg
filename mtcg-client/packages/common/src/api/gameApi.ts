/**
 * 对战 API（需登录）
 *
 * 对齐后端 GameController：GET/POST /games*
 */
import type { Ref } from 'vue'
import { http } from './request'
import type { PageVO } from '../types/common'
import type {
  ActionRequestDTO,
  ActionResultVO,
  GameCreateDTO,
  GameHistoryVO,
  GameStateVO,
  GameStatsVO,
  ReplayVO,
} from '../types/game'

export const gameApi = {
  /** 创建对局，返回 gameId */
  create: (dto: GameCreateDTO, loadingRef?: Ref<boolean>) =>
    http.post<number>('/games', dto, loadingRef),

  /** 查询对局状态（含隐私裁剪） */
  getState: (id: number, loadingRef?: Ref<boolean>) =>
    http.get<GameStateVO>(`/games/${id}`, loadingRef),

  /** 执行操作 */
  executeAction: (id: number, dto: ActionRequestDTO, loadingRef?: Ref<boolean>) =>
    http.post<ActionResultVO>(`/games/${id}/actions`, dto, loadingRef),

  /** 认输 */
  surrender: (id: number, loadingRef?: Ref<boolean>) =>
    http.post<void>(`/games/${id}/surrender`, undefined, loadingRef),

  /** 复盘数据 */
  getReplay: (id: number, loadingRef?: Ref<boolean>) =>
    http.get<ReplayVO>(`/games/${id}/replay`, loadingRef),

  /** 个人对局历史 */
  listHistory: (page = 1, size = 20, loadingRef?: Ref<boolean>) =>
    http.getWithParams<PageVO<GameHistoryVO>>(
      '/games/history',
      { params: { page, size } },
      loadingRef,
    ),

  /** 胜败统计 */
  getStats: (loadingRef?: Ref<boolean>) =>
    http.get<GameStatsVO>('/games/stats', loadingRef),
}
