// ========================================================
// 卡牌特征
// ========================================================

export interface CardFeatureVO {
  id: number
  code: string
  name: string
  bgColor: string | null
  createTime: string
}

export interface CardFeatureCreateDTO {
  code: string
  name: string
  bgColor?: string
}

export interface CardFeatureUpdateDTO {
  name?: string
  bgColor?: string
}
