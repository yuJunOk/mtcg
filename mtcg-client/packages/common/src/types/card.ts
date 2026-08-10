// ========================================================
// 卡牌
// ========================================================

export interface CardVO {
  id: number
  cardCode: string
  cardName: string
  cardType: string
  level: number | null
  color: string | null
  environment: string | null
  traits: string | null
  rarity: string | null
  attackRange: number | null
  power: number | null
  effectText: string | null
  effectJson: string | null
  imagePath: string | null
  productCode: string | null
  createTime: string
  updateTime?: string
}

export interface CardQueryDTO {
  cardCode?: string
  cardName?: string
  cardType?: string
  color?: string
  rarity?: string
  productCode?: string
  pageNum?: number
  pageSize?: number
}

export interface CardCreateDTO {
  cardCode: string
  cardName: string
  cardType: string
  level?: number
  color?: string
  environment?: string
  rarity?: string
  attackRange?: number
  power?: number
  traits?: string
  effectText?: string
  effectJson?: string
  imagePath?: string
  productCode?: string
}

export interface CardUpdateDTO {
  cardName?: string
  cardType?: string
  level?: number
  color?: string
  environment?: string
  rarity?: string
  attackRange?: number
  power?: number
  traits?: string
  effectText?: string
  effectJson?: string
  imagePath?: string
  productCode?: string
}
