// ==================== 导出所有类型 ====================

export * from './common'
export * from './user'
export * from './card' // API 类型（CardVO + DTO）
export * from './card-ui' // 前端 UI 类型（下拉选项 + 工具函数）
export * from './product'
export * from './product-ui'
export * from './dashboard'
export * from './deck'
export * from './game'

// ==================== 前端特有类型 ====================

/** 用户角色 */
export type UserRole = 'PLAYER' | 'CARD_ADMIN' | 'SYS_ADMIN' | 'AI'

/** 用户状态 */
export type UserStatus = 'ACTIVE' | 'DISABLED'

// ==================== 引擎 / Pixi 渲染用（非 API VO） ====================
// 对战 REST 请用 types/game.ts 的 *VO / *DTO；下列保留给 Card.vue / GameCanvas。

import type { ActionType, GameStatus, PhaseType, PlayerSide, Zone } from './game'

/** 卡牌快照（不可变，来自后端引擎模型） */
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

/** 卡牌实例（引擎运行时；UI 组件可选绑定） */
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

/** 场上区域（引擎形） */
export interface FieldZone {
  vanguard: CardInstance | null
  flank: [CardInstance | null, CardInstance | null]
  rearguard: CardInstance | null
  base: (CardInstance | null)[]
}

/** 玩家状态（引擎形；对战 API 请用 PlayerStateVO） */
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

/** 对局全局状态（引擎形；对战 API 请用 GameStateVO） */
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

/** @deprecated 请用 ActionRequestDTO */
export interface ActionRequest {
  gameId: string
  playerId: string
  actionType: ActionType
  payload: Record<string, unknown>
}

/** @deprecated 请用 ActionResultVO */
export interface ActionResponse {
  success: boolean
  gameState: GameState
  message?: string
  availableActions: ActionType[]
}

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
