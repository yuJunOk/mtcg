// ==================== 卡牌相关（前端特有，不含 CardVO 等 API 类型） ====================

export type { CardType, CardColor, CardRarity } from './card'
export * from './card'

// ==================== 区域（游戏引擎用） ====================

/** 区域 */
export type Zone =
  | 'VANGUARD' | 'FLANK_LEFT' | 'FLANK_RIGHT' | 'REARGUARD'
  | 'BASE'
  | 'DECK' | 'RUSH_DECK' | 'HAND' | 'TIMELINE' | 'RETREAT' | 'VOID'

/** 卡牌快照（不可变，来自后端） */
export interface CardSnapshot {
  cardCode: string
  name: string
  level: number | null
  color: string | null
  attackRange: number | null
  power: number | null
  traits: string[]
  effectText: string
  cardType: string
}

/** 卡牌实例（运行时） */
export interface CardInstance {
  instanceId: string
  snapshot: CardSnapshot
  currentZone: Zone
  currentPower: number
  currentRange: number
  enteredThisTurn: boolean
  movedThisTurn: boolean
  attackUsed: number
  interceptUsed: boolean
  isFaceDown: boolean
  attachedCards: CardInstance[]
}

// ==================== 场上区域 ====================

/** 场上区域 */
export interface FieldZone {
  vanguard: CardInstance | null
  flank: [CardInstance | null, CardInstance | null]
  rearguard: CardInstance | null
  base: (CardInstance | null)[]
}

// ==================== 玩家与对局 ====================

/** 回合阶段 */
export type PhaseType =
  | 'TURN_START' | 'DRAW' | 'ACTION' | 'COMBAT' | 'RESPONSE' | 'TURN_END'

/** 对局状态 */
export type GameStatus = 'WAITING' | 'IN_PROGRESS' | 'FINISHED'

/** 先后攻 */
export type PlayerSide = 'FIRST' | 'SECOND'

/** 玩家状态 */
export interface PlayerState {
  playerId: string
  side: PlayerSide
  deck: CardInstance[]
  rushDeck: CardInstance[]
  hand: CardInstance[]
  timeline: CardInstance[]
  retreat: CardInstance[]
  voidZone: CardInstance[]
  field: FieldZone
  baseDeployCount: number
  summonCount: number
}

/** 对局全局状态 */
export interface GameState {
  gameId: string
  activePlayer: PlayerState
  inactivePlayer: PlayerState
  firstPlayer: PlayerState
  turnCount: number
  currentPhase: PhaseType
  status: GameStatus
  winnerId: string | null
}

// ==================== 操作 ====================

/** 操作类型 */
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

/** 操作请求 */
export interface ActionRequest {
  gameId: string
  playerId: string
  actionType: ActionType
  payload: Record<string, unknown>
}

/** 操作响应 */
export interface ActionResponse {
  success: boolean
  gameState: GameState
  message?: string
  availableActions: ActionType[]
}

// ==================== 卡牌渲染 ====================

/** 卡牌渲染位置 */
export interface CardPosition {
  x: number
  y: number
  rotation: number
  scale: number
  zIndex: number
}

/** 卡牌渲染数据（PixiJS 用） */
export interface CardRenderData extends CardPosition {
  instance: CardInstance
  imageUrl: string
  backImageUrl: string
  isFaceDown: boolean
  selected: boolean
  highlight: 'none' | 'can-attack' | 'can-move' | 'can-target' | 'can-intercept'
}

// ==================== 用户相关（前端特有） ====================

export * from './user'

// ==================== 产品相关（前端特有） ====================

export * from './product'
