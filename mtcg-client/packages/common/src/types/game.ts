/**
 * 对战 API 类型（与后端 GameStateVO / DTO 字段名一致）
 */

/** 对局状态 */
export type GameStatus = 'WAITING' | 'IN_PROGRESS' | 'FINISHED'

/** 先后攻 */
export type PlayerSide = 'FIRST' | 'SECOND'

/** 对局座位（胜负/先攻标记用） */
export type GameSeat = 'PLAYER1' | 'PLAYER2'

/** 对局模式 */
export type GameMode = 'CASUAL' | 'RANKED' | 'AI'

/** AI 难度（对齐迭代八 Difficulty） */
export type AIDifficulty = 'BEGINNER' | 'INTERMEDIATE' | 'EXPERT'

/** AI 打牌倾向（对齐迭代八 PlayStyle） */
export type AIPlayStyle = 'AGGRESSIVE' | 'DEFENSIVE' | 'CONTROL'

/** 创建 AI 对局入参（对齐 CreateAIGameRequest） */
export interface CreateAIGameDTO {
  humanDeckId: number
  aiDeckId: number
  difficulty: AIDifficulty
  playStyle: AIPlayStyle
  /** PLAYER1 我先 / PLAYER2 AI 先；空则随机 */
  firstPlayer?: GameSeat | null
}

/** 回合阶段（PhaseType 枚举名） */
export type PhaseType =
  | 'TURN_START'
  | 'DRAW'
  | 'ACTION'
  | 'COMBAT'
  | 'RESPONSE'
  | 'TURN_END'

/** 操作类型（ActionType 枚举名） */
export type ActionType =
  | 'MULLIGAN'
  | 'BASE_DEPLOY'
  | 'SUMMON'
  | 'MOVE'
  | 'ACTIVATE_EFFECT'
  | 'RESPONSE_SUMMON'
  | 'ADJUST_POSITION'
  | 'ATTACK'
  | 'INTERCEPT'
  | 'PASS'
  | 'END_PHASE'
  | 'SURRENDER'

/** 区域枚举名 */
export type Zone =
  | 'VANGUARD'
  | 'FLANK_LEFT'
  | 'FLANK_RIGHT'
  | 'REARGUARD'
  | 'BASE'
  | 'DECK'
  | 'RUSH_DECK'
  | 'HAND'
  | 'TIMELINE'
  | 'RETREAT'
  | 'VOID'

/** 场上/区域中的卡牌实例视图（对齐 CardInstanceVO） */
export interface CardInstanceVO {
  instanceId: string
  cardCode: string | null
  cardName: string | null
  level: number | null
  color: string | null
  currentPower: number | null
  currentRange: number | null
  isFaceDown: boolean | null
  enteredThisTurn: boolean | null
  movedThisTurn: boolean | null
  attackUsed: number | null
  interceptUsed: boolean | null
  attachedCards: CardInstanceVO[] | null
}

/** 场上区域视图（对齐 FieldZoneVO） */
export interface FieldZoneVO {
  vanguard: CardInstanceVO | null
  /** 侧翼，长度 2，空位以 null 占位 */
  flank: (CardInstanceVO | null)[] | null
  rearguard: CardInstanceVO | null
  /** 基地区，长度 ≤ 6 */
  base: (CardInstanceVO | null)[] | null
}

/** 单方玩家局面视图（对齐 PlayerStateVO） */
export interface PlayerStateVO {
  playerId: string
  /** FIRST / SECOND */
  side: PlayerSide | string | null
  deckCount: number | null
  rushDeckCount: number | null
  /** 本人完整；对手为空列表 */
  hand: CardInstanceVO[] | null
  handCount: number | null
  timeline: CardInstanceVO[] | null
  retreat: CardInstanceVO[] | null
  voidZone: CardInstanceVO[] | null
  field: FieldZoneVO | null
  /** 仅本人可见 */
  baseDeployCount: number | null
  /** 仅本人可见 */
  summonCount: number | null
}

/** 对局状态视图（对齐 GameStateVO） */
export interface GameStateVO {
  gameId: string
  /** WAITING / IN_PROGRESS / FINISHED */
  status: GameStatus | string
  turnCount: number | null
  currentPhase: PhaseType | string | null
  activePlayerId: string | null
  /** PLAYER1 / PLAYER2 / DRAW */
  winner: string | null
  player1: PlayerStateVO | null
  player2: PlayerStateVO | null
  availableActions: string[] | null
}

/** 创建对局入参（对齐 GameCreateDTO） */
export interface GameCreateDTO {
  deck1Id: number
  deck2Id?: number | null
  /** 可空：创建房间时不填 */
  player2Id?: number | null
  gameMode: GameMode
  /** PLAYER1 / PLAYER2；空则随机 */
  firstPlayer?: GameSeat | null
  mulligan1Indices?: number[] | null
  mulligan2Indices?: number[] | null
}

/** 加入房间 / 在线匹配 */
export interface GameJoinDTO {
  deckId: number
}

/** 在线匹配结果 */
export interface GameMatchVO {
  matched: boolean
  /** 开局后的对局编码（G-…）；未匹配时为 null */
  gameId: string | null
}

/** 执行操作入参（对齐 ActionRequestDTO；playerId 由服务端确定） */
export interface ActionRequestDTO {
  actionType: ActionType | string
  cardCode?: string | null
  sourceZone?: Zone | string | null
  sourceIndex?: number | null
  targetZone?: Zone | string | null
  targetIndex?: number | null
  targetCardCode?: string | null
  extras?: Record<string, unknown> | null
}

/** 执行操作结果（对齐 ActionResultVO） */
export interface ActionResultVO {
  success: boolean | null
  message: string | null
  phaseAdvanced: boolean | null
  gameEnded: boolean | null
  winner: string | null
  gameState: GameStateVO | null
}

/** 对局历史条目（对齐 GameHistoryVO） */
export interface GameHistoryVO {
  /** 对外业务编码 G-… */
  gameId: string
  opponentName: string | null
  /** PLAYER1 / PLAYER2 */
  selfSide: string | null
  /** WIN / LOSE / DRAW / UNFINISHED */
  result: string | null
  winner: string | null
  gameMode: string | null
  status: string | null
  deckName: string | null
  createTime: string | null
  endTime: string | null
}

/** 个人胜败统计（对齐 GameStatsVO） */
export interface GameStatsVO {
  totalGames: number | null
  wins: number | null
  losses: number | null
  draws: number | null
  winRate: number | null
}

/** 复盘流水单条（对齐 ActionReplayEntryVO） */
export interface ActionReplayEntryVO {
  seq: number | null
  turnCount: number | null
  phase: string | null
  playerId: string | null
  actionType: string | null
  /** 操作详情 JSON 字符串 */
  actionDetail: string | null
  timestamp: number | null
}

/** 复盘回放视图（对齐 ReplayVO） */
export interface ReplayVO {
  /** 对外业务编码 G-… */
  gameId: string
  player1Id: number | null
  player2Id: number | null
  winner: string | null
  gameMode: string | null
  createTime: string | null
  endTime: string | null
  actions: ActionReplayEntryVO[] | null
}
