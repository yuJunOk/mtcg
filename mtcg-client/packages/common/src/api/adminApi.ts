/**
 * 管理员 API（需 SYS_ADMIN 角色）
 */
import { axios, extractData } from './request'
import type { PageVO } from '../types/common'
import type {
  UserVO, UserQueryDTO, AdminUserCreateDTO, AdminUserUpdateDTO, AdminResetPasswordDTO,
} from '../types/user'
import type { ProductVO, ProductQueryDTO, ProductCreateDTO, ProductUpdateDTO } from '../types/product'
import type { CardVO, CardQueryDTO, CardCreateDTO, CardUpdateDTO } from '../types/card'

// ========================================================
// 用户管理
// ========================================================

export const adminUserApi = {
  list: (query: UserQueryDTO) =>
    axios.get<Record<string, unknown>>('/admin/users', { params: { ...query } })
      .then(r => extractData<PageVO<UserVO>>(r)),

  get: (id: number) =>
    axios.get<Record<string, unknown>>(`/admin/users/${id}`)
      .then(r => extractData<UserVO>(r)),

  create: (dto: AdminUserCreateDTO) =>
    axios.post<Record<string, unknown>>('/admin/users', dto)
      .then(r => extractData<number>(r)),

  update: (id: number, dto: AdminUserUpdateDTO) =>
    axios.put<Record<string, unknown>>(`/admin/users/${id}`, dto)
      .then(() => undefined),

  delete: (id: number) =>
    axios.delete<Record<string, unknown>>(`/admin/users/${id}`)
      .then(() => undefined),

  updateStatus: (id: number, status: string) =>
    axios.patch<Record<string, unknown>>(`/admin/users/${id}/status`, null, { params: { status } })
      .then(() => undefined),

  resetPassword: (id: number, dto: AdminResetPasswordDTO) =>
    axios.patch<Record<string, unknown>>(`/admin/users/${id}/password`, dto)
      .then(() => undefined),
}

// ========================================================
// 产品管理
// ========================================================

export const adminProductApi = {
  list: (query: ProductQueryDTO) =>
    axios.get<Record<string, unknown>>('/admin/products', { params: { ...query } })
      .then(r => extractData<PageVO<ProductVO>>(r)),

  get: (id: number) =>
    axios.get<Record<string, unknown>>(`/admin/products/${id}`)
      .then(r => extractData<ProductVO>(r)),

  create: (dto: ProductCreateDTO) =>
    axios.post<Record<string, unknown>>('/admin/products', dto)
      .then(r => extractData<number>(r)),

  update: (id: number, dto: ProductUpdateDTO) =>
    axios.put<Record<string, unknown>>(`/admin/products/${id}`, dto)
      .then(() => undefined),

  delete: (id: number) =>
    axios.delete<Record<string, unknown>>(`/admin/products/${id}`)
      .then(() => undefined),

  listCards: (productCode: string, page?: number, size?: number) =>
    axios.get<Record<string, unknown>>(`/admin/products/${productCode}/cards`, { params: { page, size } })
      .then(r => extractData<PageVO<CardVO>>(r)),
}

// ========================================================
// 卡牌管理
// ========================================================

export const adminCardApi = {
  list: (query: CardQueryDTO) =>
    axios.get<Record<string, unknown>>('/admin/cards', { params: { ...query } })
      .then(r => extractData<PageVO<CardVO>>(r)),

  get: (id: number) =>
    axios.get<Record<string, unknown>>(`/admin/cards/${id}`)
      .then(r => extractData<CardVO>(r)),

  create: (dto: CardCreateDTO) =>
    axios.post<Record<string, unknown>>('/admin/cards', dto)
      .then(r => extractData<number>(r)),

  update: (id: number, dto: CardUpdateDTO) =>
    axios.put<Record<string, unknown>>(`/admin/cards/${id}`, dto)
      .then(() => undefined),

  delete: (id: number) =>
    axios.delete<Record<string, unknown>>(`/admin/cards/${id}`)
      .then(() => undefined),
}
