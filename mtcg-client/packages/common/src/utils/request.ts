import axios, { type AxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'

const TOKEN_KEY = 'mtcg_token'

const instance = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' },
})

// 业务错误码：未登录/Token 无效
const UNAUTH_CODES = [401, 1005, 1006]

// 请求拦截器：注入 token
instance.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEY)
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截器：解包统一响应 { code, message, data }
instance.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res && typeof res === 'object' && typeof res.code !== 'undefined') {
      if (res.code === 0) {
        return res.data
      }
      // 未登录 → 清 token + 跳登录
      if (UNAUTH_CODES.includes(Number(res.code))) {
        clearAuthAndRedirect()
      }
      // 业务错误统一弹提示
      const msg = res.message || '请求失败'
      ElMessage.error(msg)
      return Promise.reject(new Error(msg))
    }
    // 非标准格式直接返回
    return res
  },
  (error) => {
    const status = error.response?.status
    let message = error.message || '网络错误'
    if (status === 401) {
      clearAuthAndRedirect()
    } else if (error.response?.data?.message) {
      message = error.response.data.message
    } else if (status === 403) {
      message = '无操作权限'
    } else if (status === 404) {
      message = '请求的资源不存在'
    } else if (status && status >= 500) {
      message = '服务器开小差了，请稍后再试'
    }
    ElMessage.error(message)
    return Promise.reject(new Error(message))
  },
)

function clearAuthAndRedirect() {
  localStorage.removeItem(TOKEN_KEY)
  // hash 路由：跳到登录页，并强制刷新以重置内存中的 store 状态
  window.location.hash = '/login'
  window.location.reload()
}

/**
 * 由于响应拦截器已解包 res.data（业务数据），这里对类型做收窄，
 * 使调用方直接拿到业务数据而非 AxiosResponse。
 */
export const http = {
  get<T = unknown>(url: string, config?: AxiosRequestConfig): Promise<T> {
    return instance.get(url, config) as unknown as Promise<T>
  },
  post<T = unknown>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
    return instance.post(url, data, config) as unknown as Promise<T>
  },
  put<T = unknown>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
    return instance.put(url, data, config) as unknown as Promise<T>
  },
  patch<T = unknown>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
    return instance.patch(url, data, config) as unknown as Promise<T>
  },
  delete<T = unknown>(url: string, config?: AxiosRequestConfig): Promise<T> {
    return instance.delete(url, config) as unknown as Promise<T>
  },
}

export default http
