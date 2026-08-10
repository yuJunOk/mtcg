/**
 * 公开卡牌 API（无需登录）
 */
import type { Ref } from 'vue'
import { http } from './request'
import type { PageVO } from '../types/common'
import type { CardVO, CardQueryDTO } from '../types/card'

export const cardApi = {
  list: (query: CardQueryDTO, loadingRef?: Ref<boolean>) =>
    http.getWithParams<PageVO<CardVO>>('/cards', { params: { ...query } }, loadingRef),

  get: (id: number, loadingRef?: Ref<boolean>) =>
    http.get<CardVO>(`/cards/${id}`, loadingRef),
}
