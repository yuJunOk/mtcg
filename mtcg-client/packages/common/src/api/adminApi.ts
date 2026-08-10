/**
 * 管理员 API（需 SYS_ADMIN 角色）
 *
 * 所有方法支持可选的 loadingRef 参数，自动管理加载状态
 */
import type { Ref } from 'vue'
import { http } from './request'
import type { PageVO } from '../types/common'
import type {
  UserVO, UserQueryDTO, AdminUserCreateDTO, AdminUserUpdateDTO, AdminResetPasswordDTO,
} from '../types/user'
import type { ProductVO, ProductQueryDTO, ProductCreateDTO, ProductUpdateDTO } from '../types/product'
import type { CardVO, CardQueryDTO, CardCreateDTO, CardUpdateDTO } from '../types/card'
import type { CardFeatureVO } from '../types/card-feature'

// ========================================================
// 用户管理
// ========================================================

export const adminUserApi = {
  list: (query: UserQueryDTO, loadingRef?: Ref<boolean>) =>
    http.getWithParams<PageVO<UserVO>>('/admin/users', { params: { ...query } }, loadingRef),

  get: (id: number, loadingRef?: Ref<boolean>) =>
    http.get<UserVO>(`/admin/users/${id}`, loadingRef),

  createUser: (dto: AdminUserCreateDTO, loadingRef?: Ref<boolean>) =>
    http.post<number>('/admin/users', dto, loadingRef),

  updateUser: (id: number, dto: AdminUserUpdateDTO, loadingRef?: Ref<boolean>) =>
    http.post<void>('/admin/users/' + id, dto, loadingRef),

  deleteUser: (id: number, loadingRef?: Ref<boolean>) =>
    http.post<void>('/admin/users/' + id + '/delete', undefined, loadingRef),

  updateUserStatus: (id: number, status: string, loadingRef?: Ref<boolean>) =>
    http.postWithConfig<void>('/admin/users/' + id + '/status', undefined, { params: { status } }, loadingRef),

  resetUserPassword: (id: number, dto: AdminResetPasswordDTO, loadingRef?: Ref<boolean>) =>
    http.post<void>('/admin/users/' + id + '/password', dto, loadingRef),
}

// ========================================================
// 产品管理
// ========================================================

export const adminProductApi = {
  list: (query: ProductQueryDTO, loadingRef?: Ref<boolean>) =>
    http.getWithParams<PageVO<ProductVO>>('/admin/products', { params: { ...query } }, loadingRef),

  create: (dto: ProductCreateDTO, loadingRef?: Ref<boolean>) =>
    http.post<number>('/admin/products', dto, loadingRef),

  update: (id: number, dto: ProductUpdateDTO, loadingRef?: Ref<boolean>) =>
    http.post<void>('/admin/products/' + id, dto, loadingRef),

  delete: (id: number, loadingRef?: Ref<boolean>) =>
    http.post<void>('/admin/products/' + id + '/delete', undefined, loadingRef),
}

// ========================================================
// 卡牌管理
// ========================================================

export const adminCardApi = {
  create: (dto: CardCreateDTO, loadingRef?: Ref<boolean>) =>
    http.post<number>('/admin/cards', dto, loadingRef),

  update: (id: number, dto: CardUpdateDTO, loadingRef?: Ref<boolean>) =>
    http.post<void>('/admin/cards/' + id, dto, loadingRef),

  delete: (id: number, loadingRef?: Ref<boolean>) =>
    http.post<void>('/admin/cards/' + id + '/delete', undefined, loadingRef),

  uploadImage: (cardId: number, formData: FormData, loadingRef?: Ref<boolean>) =>
    http.postWithConfig<string>('/admin/cards/' + cardId + '/image', formData, {}, loadingRef),

  listFeatures: (cardId: number, loadingRef?: Ref<boolean>) =>
    http.get<CardFeatureVO[]>(`/admin/cards/${cardId}/features`, loadingRef),

  addFeature: (cardId: number, featureId: number, loadingRef?: Ref<boolean>) =>
    http.post<void>(`/admin/cards/${cardId}/features/${featureId}`, undefined, loadingRef),

  removeFeature: (cardId: number, featureId: number, loadingRef?: Ref<boolean>) =>
    http.post<void>(`/admin/cards/${cardId}/features/${featureId}/delete`, undefined, loadingRef),
}
