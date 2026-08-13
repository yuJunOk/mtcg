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
  /** 创建对局或等待房间，返回 gameCode（G-…） */
  create: (dto: GameCreateDTO, loadingRef?: Ref<boolean>) =>
    http.post<string>('/games', dto, loadingRef),

  /** 创建 AI 对局（迭代八未交付时后端返回尚未开放） */
  createAi: (dto: CreateAIGameDTO, loadingRef?: Ref<boolean>) =>
    http.post<string>(
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

  /** 加入等待房间，返回 gameCode（id 可为数字或 G- 编码） */
  join: (idOrCode: number | string, dto: GameJoinDTO, loadingRef?: Ref<boolean>) =>
    http.post<string>(`/games/${idOrCode}/join`, dto, loadingRef),

  /** 取消本人等待房间 */
  cancelWaiting: (idOrCode: number | string, loadingRef?: Ref<boolean>) =>
    http.post<void>(`/games/${idOrCode}/cancel`, undefined, loadingRef),

  /** 查询对局状态（含隐私裁剪） */
  getState: (idOrCode: number | string, loadingRef?: Ref<boolean>) =>
    http.get<GameStateVO>(`/games/${idOrCode}`, loadingRef),

  /** 执行操作 */
  executeAction: (
    idOrCode: number | string,
    dto: ActionRequestDTO,
    loadingRef?: Ref<boolean>,
  ) => http.post<ActionResultVO>(`/games/${idOrCode}/actions`, dto, loadingRef),

  /** 认输 */
  surrender: (idOrCode: number | string, loadingRef?: Ref<boolean>) =>
    http.post<void>(`/games/${idOrCode}/surrender`, undefined, loadingRef),

  /** 复盘数据 */
  getReplay: (idOrCode: number | string, loadingRef?: Ref<boolean>) =>
    http.get<ReplayVO>(`/games/${idOrCode}/replay`, loadingRef),

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
