/**
 * 卡组 API（需登录）
 *
 * 对齐后端 DeckController：GET/POST /decks*
 */
import type { Ref } from 'vue'
import { http } from './request'
import type {
  DeckCreateDTO,
  DeckReorderDTO,
  DeckUpdateDTO,
  DeckValidateResultVO,
  DeckVO,
} from '../types/deck'

export const deckApi = {
  /** 我的卡组列表（按 sortOrder；tag 非空时精确包含筛选） */
  list: (tag?: string, loadingRef?: Ref<boolean>) =>
    http.getWithParams<DeckVO[]>(
      '/decks',
      { params: tag ? { tag } : {} },
      loadingRef,
    ),

  /** 卡组详情 */
  get: (id: number, loadingRef?: Ref<boolean>) =>
    http.get<DeckVO>(`/decks/${id}`, loadingRef),

  /** 创建卡组，返回 deckId */
  create: (dto: DeckCreateDTO, loadingRef?: Ref<boolean>) =>
    http.post<number>('/decks', dto, loadingRef),

  /** 编辑卡组 */
  update: (id: number, dto: DeckUpdateDTO, loadingRef?: Ref<boolean>) =>
    http.post<void>(`/decks/${id}`, dto, loadingRef),

  /** 删除卡组 */
  delete: (id: number, loadingRef?: Ref<boolean>) =>
    http.post<void>(`/decks/${id}/delete`, undefined, loadingRef),

  /** 校验卡组合法性 */
  validate: (id: number, loadingRef?: Ref<boolean>) =>
    http.post<DeckValidateResultVO>(`/decks/${id}/validate`, undefined, loadingRef),

  /** 卡组列表拖拽重排 */
  reorder: (dto: DeckReorderDTO, loadingRef?: Ref<boolean>) =>
    http.post<void>('/decks/reorder', dto, loadingRef),
}
