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
  CreateAIGameDTO,
  GameCreateDTO,
  GameHistoryVO,
  GameJoinDTO,
  GameMatchVO,
  GameStateVO,
  GameStatsVO,
  ReplayVO,
} from '../types/game'

export const gameApi = {
  /** 创建对局或等待房间，返回 gameId */
  create: (dto: GameCreateDTO, loadingRef?: Ref<boolean>) =>
    http.post<number>('/games', dto, loadingRef),

  /** 创建 AI 对局（迭代八未交付时后端返回尚未开放） */
  createAi: (dto: CreateAIGameDTO, loadingRef?: Ref<boolean>) =>
    http.post<number>(
      '/games',
      {
        deck1Id: dto.humanDeckId,
        deck2Id: dto.aiDeckId,
        gameMode: 'AI',
        firstPlayer: dto.firstPlayer,
      } satisfies GameCreateDTO,
      loadingRef,
    ),

  /** 在线匹配 */
  match: (dto: GameJoinDTO, loadingRef?: Ref<boolean>) =>
    http.post<GameMatchVO>('/games/match', dto, loadingRef),

  /** 加入等待房间，返回 gameId */
  join: (id: number, dto: GameJoinDTO, loadingRef?: Ref<boolean>) =>
    http.post<number>(`/games/${id}/join`, dto, loadingRef),

  /** 取消本人等待房间 */
  cancelWaiting: (id: number, loadingRef?: Ref<boolean>) =>
    http.post<void>(`/games/${id}/cancel`, undefined, loadingRef),

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
