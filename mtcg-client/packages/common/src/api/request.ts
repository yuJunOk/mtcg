/**
 * axios 统一封装
 *
 * 职责：
 * - Token 自动注入（从 localStorage 读取）
 * - 统一响应解包：{code, data, message} → 直接返回 data
 * - 错误码统一处理：401/1005/1006 → 清登录态跳转登录页
 * - 其他业务错误 → ElMessage.error
 */
import axios from 'axios'
import { ElMessage } from 'element-plus'

const TOKEN_KEY = 'mtcg_token'

// ========================================================
// axios 全局配置（一次性配置）
// ========================================================

axios.defaults.baseURL = '/api'
axios.defaults.timeout = 10_000
axios.defaults.headers.common['Content-Type'] = 'application/json'

// ========================================================
// 请求拦截器：注入 Token
// ========================================================

axios.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEY)
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

// ========================================================
// 响应拦截器：解包 + 错误处理
// ========================================================

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
// 统一响应解包
// ========================================================

/** 从 axios 响应中提取 Result<T> 的 data 部分 */
export function extractData<T>(r: { data: Record<string, unknown> }): T {
  return r.data['data'] as T
}

// ========================================================
// 导出 axios 实例（供子 API 文件使用）
// ========================================================

export { axios }
