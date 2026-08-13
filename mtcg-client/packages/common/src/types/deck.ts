/**
 * 卡组类型（与后端 DeckVO / DTO 字段名一致）
 */

/** 卡组内有序条目；数组序 = 卡组内排序 */
export interface DeckCardEntry {
  cardCode: string
  quantity: number
}

/** 卡组展示对象 */
export interface DeckVO {
  id: number
  userId: number
  deckName: string
  mainDeck: DeckCardEntry[]
  rushDeck: DeckCardEntry[]
  isValid: boolean | null
  sortOrder: number | null
  tags: string | null
  /** 封面卡编号 */
  coverCardCode: string | null
  /** 封面卡图路径（由封面卡派生） */
  coverImagePath: string | null
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
