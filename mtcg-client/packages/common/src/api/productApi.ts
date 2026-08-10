/**
 * 公开产品 API（无需登录）
 */
import type { Ref } from 'vue'
import { http } from './request'
import type { PageVO } from '../types/common'
import type { ProductVO, ProductQueryDTO } from '../types/product'
import type { CardVO } from '../types/card'

export const productApi = {
  list: (query: ProductQueryDTO, loadingRef?: Ref<boolean>) =>
    http.getWithParams<PageVO<ProductVO>>('/products', { params: { ...query } }, loadingRef),

  get: (id: number, loadingRef?: Ref<boolean>) =>
    http.get<ProductVO>(`/products/${id}`, loadingRef),

  listCards: (productCode: string, pageNum = 1, pageSize = 20, loadingRef?: Ref<boolean>) =>
    http.getWithParams<PageVO<CardVO>>(
      `/products/${productCode}/cards`,
      { params: { pageNum, pageSize } },
      loadingRef,
    ),
}
