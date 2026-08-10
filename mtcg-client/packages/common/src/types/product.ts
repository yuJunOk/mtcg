// ========================================================
// 产品
// ========================================================

export interface ProductVO {
  id: number
  productCode: string
  productName: string
  releaseDate: string | null
  description: string | null
  createTime: string
}

export interface ProductQueryDTO {
  productName?: string
  productCode?: string
  pageNum?: number
  pageSize?: number
}

export interface ProductCreateDTO {
  productCode: string
  productName: string
  releaseDate?: string
  description?: string
}

export interface ProductUpdateDTO {
  productName?: string
  releaseDate?: string
  description?: string
}
