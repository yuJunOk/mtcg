/**
 * 卡组 API（需登录）
 *
 * 对齐后端 DeckController：GET/POST /decks*
 */
import type { Ref } from 'vue'
import { http } from './request'
import type {
  DeckCopyDTO,
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

  /** 卡组详情（id 可为数字主键或 D- 编码） */
  get: (idOrCode: number | string, loadingRef?: Ref<boolean>) =>
    http.get<DeckVO>(`/decks/${idOrCode}`, loadingRef),

  /** 创建卡组，返回 deckCode */
  create: (dto: DeckCreateDTO, loadingRef?: Ref<boolean>) =>
    http.post<string>('/decks', dto, loadingRef),

  /** 编辑卡组 */
  update: (idOrCode: number | string, dto: DeckUpdateDTO, loadingRef?: Ref<boolean>) =>
    http.post<void>(`/decks/${idOrCode}`, dto, loadingRef),

  /** 删除卡组 */
  delete: (idOrCode: number | string, loadingRef?: Ref<boolean>) =>
    http.post<void>(`/decks/${idOrCode}/delete`, undefined, loadingRef),

  /** 校验卡组合法性 */
  validate: (idOrCode: number | string, loadingRef?: Ref<boolean>) =>
    http.post<DeckValidateResultVO>(`/decks/${idOrCode}/validate`, undefined, loadingRef),

  /** 按编码复制他人（或自己的）卡组，返回新 deckCode */
  copyByCode: (dto: DeckCopyDTO, loadingRef?: Ref<boolean>) =>
    http.post<string>('/decks/copy', dto, loadingRef),

  /** 卡组列表拖拽重排 */
  reorder: (dto: DeckReorderDTO, loadingRef?: Ref<boolean>) =>
    http.post<void>('/decks/reorder', dto, loadingRef),
}
