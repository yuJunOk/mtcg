/**
 * API 统一客户端（聚合层，向后兼容）
 *
 * 已废弃，请使用按需导入：
 *   import { authApi, userApi, adminUserApi, adminProductApi, adminCardApi, dashboardApi } from '@mtcg/common/api'
 *   import type { UserVO } from '@mtcg/common/types'
 *
 * 本文件仅作过渡兼容，后续会删除。
 */
import { axios, extractData } from './request'
import type * as Gen from '../types'

// ========================================================
// 认证（匿名接口）
// ========================================================

class AuthApi {
  register = (dto: Gen.UserRegisterDTO) =>
    axios.post<Record<string, unknown>>('/auth/register', dto).then(r => extractData<number>(r))

  login = (dto: Gen.UserLoginDTO) =>
    axios.post<Record<string, unknown>>('/auth/login', dto).then(r => extractData<Gen.LoginVO>(r))
}

// ========================================================
// 用户个人资料（需登录）
// ========================================================

class UserApi {
  me = () =>
    axios.get<Record<string, unknown>>('/users/me').then(r => extractData<Gen.UserVO>(r))

  updateMe = (dto: Gen.UserUpdateDTO) =>
    axios.put<Record<string, unknown>>('/users/me', dto).then(() => undefined)

  changePassword = (dto: Gen.ChangePasswordDTO) =>
    axios.put<Record<string, unknown>>('/users/me/password', dto).then(() => undefined)
}

// ========================================================
// 管理员接口（需 SYS_ADMIN）
// ========================================================

class AdminApi {
  // --- 用户管理 ---
  listUsers = (query: Gen.UserQueryDTO) =>
    axios.get<Record<string, unknown>>('/admin/users', { params: { ...query } }).then(r => extractData<Gen.PageVO<UserVO>>(r))

  getUser = (id: number) =>
    axios.get<Record<string, unknown>>(`/admin/users/${id}`).then(r => extractData<Gen.UserVO>(r))

  createUser = (dto: Gen.AdminUserCreateDTO) =>
    axios.post<Record<string, unknown>>('/admin/users', dto).then(r => extractData<number>(r))

  updateUser = (id: number, dto: Gen.AdminUserUpdateDTO) =>
    axios.put<Record<string, unknown>>(`/admin/users/${id}`, dto).then(() => undefined)

  deleteUser = (id: number) =>
    axios.delete<Record<string, unknown>>(`/admin/users/${id}`).then(() => undefined)

  updateUserStatus = (id: number, status: string) =>
    axios.patch<Record<string, unknown>>(`/admin/users/${id}/status`, null, { params: { status } }).then(() => undefined)

  resetUserPassword = (id: number, dto: Gen.AdminResetPasswordDTO) =>
    axios.patch<Record<string, unknown>>(`/admin/users/${id}/password`, dto).then(() => undefined)

  // --- 产品管理 ---
  listProducts = (query: Gen.ProductQueryDTO) =>
    axios.get<Record<string, unknown>>('/admin/products', { params: { ...query } }).then(r => extractData<Gen.PageVO<ProductVO>>(r))

  getProduct = (id: number) =>
    axios.get<Record<string, unknown>>(`/admin/products/${id}`).then(r => extractData<Gen.ProductVO>(r))

  createProduct = (dto: Gen.ProductCreateDTO) =>
    axios.post<Record<string, unknown>>('/admin/products', dto).then(r => extractData<number>(r))

  updateProduct = (id: number, dto: Gen.ProductUpdateDTO) =>
    axios.put<Record<string, unknown>>(`/admin/products/${id}`, dto).then(() => undefined)

  deleteProduct = (id: number) =>
    axios.delete<Record<string, unknown>>(`/admin/products/${id}`).then(() => undefined)

  // --- 卡牌管理 ---
  listCards = (query: Gen.CardQueryDTO) =>
    axios.get<Record<string, unknown>>('/admin/cards', { params: { ...query } }).then(r => extractData<Gen.PageVO<CardVO>>(r))

  getCard = (id: number) =>
    axios.get<Record<string, unknown>>(`/admin/cards/${id}`).then(r => extractData<Gen.CardVO>(r))

  createCard = (dto: Gen.CardCreateDTO) =>
    axios.post<Record<string, unknown>>('/admin/cards', dto).then(r => extractData<number>(r))

  updateCard = (id: number, dto: Gen.CardUpdateDTO) =>
    axios.put<Record<string, unknown>>(`/admin/cards/${id}`, dto).then(() => undefined)

  deleteCard = (id: number) =>
    axios.delete<Record<string, unknown>>(`/admin/cards/${id}`).then(() => undefined)
}

// ========================================================
// 仪表盘接口
// ========================================================

class DashboardApi {
  getStats = () =>
    axios.get<Record<string, unknown>>('/admin/dashboard/stats').then(r => extractData<Gen.DashboardStatsVO>(r))

  health = () =>
    axios.get<Record<string, unknown>>('/health').then(r => extractData<Gen.HealthVO>(r))
}

// ========================================================
// 类型兼容（透传旧 generated/ 的类型路径）
// ========================================================

// View 文件从 '@mtcg/common/api' 导入类型，这里重新导出
export type {
  UserVO, LoginVO, UserQueryDTO, AdminUserCreateDTO, AdminUserUpdateDTO,
  AdminResetPasswordDTO, UserUpdateDTO, ChangePasswordDTO,
} from '../types/user'

export type {
  CardVO, CardQueryDTO, CardCreateDTO, CardUpdateDTO,
} from '../types/card'

export type {
  ProductVO, ProductQueryDTO, ProductCreateDTO, ProductUpdateDTO,
} from '../types/product'

export type {
  DashboardStatsVO, HealthVO,
} from '../types/dashboard'

// 临时类型别名（View 文件中使用）
type UserVO = Gen.UserVO
type ProductVO = Gen.ProductVO
type CardVO = Gen.CardVO

// ========================================================
// 默认导出（全局单例，兼容旧写法）
// ========================================================

export const client = {
  auth: new AuthApi(),
  user: new UserApi(),
  admin: new AdminApi(),
  dashboard: new DashboardApi(),
}
