// ========================================================
// 卡牌
// ========================================================

export interface CardVO {
  id: number
  cardCode: string
  name: string
  cardType: string
  level: number | null
  color: string | null
  rarity: string | null
  attackRange: number | null
  power: number | null
  traits: string[]
  effectText: string
  imageUrl: string | null
  productCode: string | null
  createTime: string
  updateTime: string
}

export interface CardQueryDTO {
  cardCode?: string
  name?: string
  cardType?: string
  color?: string
  rarity?: string
  productCode?: string
  page?: number
  size?: number
}

export interface CardCreateDTO {
  cardCode: string
  name: string
  cardType: string
  level?: number
  color?: string
  rarity?: string
  attackRange?: number
  power?: number
  traits?: string[]
  effectText?: string
  imageUrl?: string
  productCode?: string
}

export interface CardUpdateDTO {
  name?: string
  cardType?: string
  level?: number
  color?: string
  rarity?: string
  attackRange?: number
  power?: number
  traits?: string[]
  effectText?: string
  imageUrl?: string
  productCode?: string
}
