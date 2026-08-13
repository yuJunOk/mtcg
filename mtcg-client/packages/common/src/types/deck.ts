/**
 * 卡组类型（与后端 DeckVO / DTO 字段名一致）
 */

/** 卡组状态（与后端 EnumDeckStatus 一致） */
export type DeckStatus = 'READY' | 'DRAFT'

/** 卡组状态选项 */
export const DECK_STATUS_OPTIONS: Array<{ code: DeckStatus; desc: string }> = [
  { code: 'READY', desc: '可用' },
  { code: 'DRAFT', desc: '草稿' },
]

/** 解析卡组状态：优先 status，回退 isValid */
export function resolveDeckStatus(deck: {
  status?: string | null
  isValid?: boolean | null
}): DeckStatus {
  if (deck.status === 'READY' || deck.status === 'DRAFT') return deck.status
  return deck.isValid === true ? 'READY' : 'DRAFT'
}

export function deckStatusLabel(deck: {
  status?: string | null
  isValid?: boolean | null
}): string {
  const status = resolveDeckStatus(deck)
  return DECK_STATUS_OPTIONS.find((o) => o.code === status)?.desc ?? status
}

export function isDeckReady(deck: {
  status?: string | null
  isValid?: boolean | null
}): boolean {
  return resolveDeckStatus(deck) === 'READY'
}

/** 卡组内有序条目；数组序 = 卡组内排序 */
export interface DeckCardEntry {
  cardCode: string
  quantity: number
}

/** 卡组展示对象 */
export interface DeckVO {
  id: number
  /** 对外业务编码，如 D-A3F8Q2NW */
  deckCode: string
  userId: number
  deckName: string
  mainDeck: DeckCardEntry[]
  rushDeck: DeckCardEntry[]
  /** @deprecated 请优先用 status；与 READY/DRAFT 同步 */
  isValid: boolean | null
  /** READY=可用 / DRAFT=草稿 */
  status: DeckStatus | string | null
  /** 是否公开（卡组广场预留） */
  isPublic: boolean | null
  /** 是否允许他人按编码复制 */
  isCopyable: boolean | null
  sortOrder: number | null
  tags: string | null
  /** 封面卡编号 */
  coverCardCode: string | null
  /** 封面卡图路径（由封面卡派生） */
  coverImagePath: string | null
  /** 主卡组颜色 code（RED/GREEN…）；展示用 formatColorLabels */
  colors: string[] | null
  mainDeckSize: number
  rushDeckSize: number
  createTime: string | null
  updateTime: string | null
}

/** 创建卡组入参 */
export interface DeckCreateDTO {
  deckName: string
  mainDeck: DeckCardEntry[]
  rushDeck: DeckCardEntry[]
  /** 可选，逗号分隔标签 */
  tags?: string
  /** 封面卡编号 */
  coverCardCode?: string
  isPublic?: boolean
  isCopyable?: boolean
}

/** 编辑卡组入参（全部可选；deck 非空时整表覆盖） */
export interface DeckUpdateDTO {
  deckName?: string
  mainDeck?: DeckCardEntry[]
  rushDeck?: DeckCardEntry[]
  /** 非 null 时覆盖（空串表示清空） */
  tags?: string
  /** 非 null 时覆盖（空串表示清空并回退第一张） */
  coverCardCode?: string
  isPublic?: boolean
  isCopyable?: boolean
}

/** 按编码复制卡组 */
export interface DeckCopyDTO {
  deckCode: string
}

/** 卡组列表批量重排入参 */
export interface DeckReorderItem {
  id: number
  sortOrder: number
}

export interface DeckReorderDTO {
  items: DeckReorderItem[]
}

/** 卡组校验结果 */
export interface DeckValidateResultVO {
  valid: boolean
  errors: string[]
  mainDeckCount: number
  rushDeckCount: number
  colors: string[]
  nameCount: Record<string, number>
}
