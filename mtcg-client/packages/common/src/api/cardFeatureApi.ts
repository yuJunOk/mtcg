/**
 * 卡牌特征 API（需管理员角色）
 *
 * 所有方法支持可选的 loadingRef 参数，自动管理加载状态
 */
import type { Ref } from 'vue'
import { http } from './request'
import type { CardFeatureVO, CardFeatureCreateDTO, CardFeatureUpdateDTO } from '../types/card-feature'

export const cardFeatureApi = {
  list: (loadingRef?: Ref<boolean>) =>
    http.get<CardFeatureVO[]>('/cards/features', loadingRef),

  get: (id: number, loadingRef?: Ref<boolean>) =>
    http.get<CardFeatureVO>(`/cards/features/${id}`, loadingRef),

  create: (dto: CardFeatureCreateDTO, loadingRef?: Ref<boolean>) =>
    http.post<number>('/cards/features', dto, loadingRef),

  update: (id: number, dto: CardFeatureUpdateDTO, loadingRef?: Ref<boolean>) =>
    http.post<void>('/cards/features/' + id, dto, loadingRef),

  delete: (id: number, loadingRef?: Ref<boolean>) =>
    http.post<void>('/cards/features/' + id + '/delete', undefined, loadingRef),
}
