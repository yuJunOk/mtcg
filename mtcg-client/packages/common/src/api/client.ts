/**
 * OpenAPI 客户端
 *
 * 使用方式：
 *   import { client } from '@mtcg/common/api'
 *   const user = await client.user.me()        // 已解包，直接是 UserVO
 *   const result = await client.auth.login(dto) // 已解包，直接是 LoginVO
 *
 * 职责：
 * - Token 自动注入（axios 请求拦截器，从 localStorage 读取）
 * - 统一响应解包：{code, data, message} → 直接返回 data
 * - 错误码统一处理：401/1005/1006 → 清登录态跳转登录页
 * - 其他业务错误 → ElMessage.error
 *
 * 后端接口变更后：
 *   1. 确保后端运行在 localhost:8081
 *   2. 重新拉取：
 *      npx openapi --input http://localhost:8081/api/v3/api-docs \
 *        --output ./packages/common/src/api/generated --client axios --name MTCGClient
 *   3. 覆盖 packages/common/src/api/generated/ 目录
 *   4. 在对应 Api 类中添加新方法即可
 */
import axios from 'axios'
import { ElMessage } from 'element-plus'
import type * as Gen from './generated'

// ========================================================
// axios 全局配置与拦截器（一次性配置）
// ========================================================

const TOKEN_KEY = 'mtcg_token'

axios.defaults.baseURL = '/api'
axios.defaults.timeout = 10_000
axios.defaults.headers.common['Content-Type'] = 'application/json'

axios.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEY)
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

axios.interceptors.response.use(
  (response) => response,
  (error) => {
    const res = error.response?.data as Record<string, unknown> | undefined
    const code = res?.code ?? error.response?.status

    // 未登录 / Token 失效 → 清登录态 + 强制刷新跳转登录页
    if (code === 401 || code === 1005 || code === 1006) {
      localStorage.removeItem(TOKEN_KEY)
      window.location.hash = '/login'
      window.location.reload()
      return Promise.reject(error)
    }

    // 业务错误统一弹提示
    let msg = error.message || '网络错误'
    if (res?.message) msg = res.message as string
    else if (error.response?.status === 403) msg = '无操作权限'
    else if (error.response?.status === 404) msg = '请求的资源不存在'
    else if (error.response?.status && error.response.status >= 500) msg = '服务器开小差了，请稍后再试'

    ElMessage.error(msg)
    return Promise.reject(error)
  },
)

// ========================================================
// 统一响应解包（内部使用）
// ========================================================

/** 从 axios 响应中提取 Result<T> 的 data 部分 */
function extractData<T>(r: { data: Record<string, unknown> }): T {
  return r.data['data'] as T
}

// ========================================================
// API 客户端
// ========================================================

export class MTCGApi {
  readonly user = new UserApi()
  readonly auth = new AuthApi()
  readonly admin = new AdminApi()
  readonly dashboard = new DashboardApi()
}

// ========================================================
// 认证相关（匿名接口）
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
    axios.get<Record<string, unknown>>('/admin/users', { params: { ...query } }).then(r => extractData<Gen.PageVOUserVO>(r))

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
    axios.get<Record<string, unknown>>('/admin/products', { params: { ...query } }).then(r => extractData<Gen.PageVOProductVO>(r))

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
    axios.get<Record<string, unknown>>('/admin/cards', { params: { ...query } }).then(r => extractData<Gen.PageVOCardVO>(r))

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
// 默认导出（全局单例）
// ========================================================

export const client = new MTCGApi()

// ========================================================
// 类型透传（从 generated 目录）
// ========================================================

export * from './generated'
