/**
 * axios 统一封装
 *
 * 职责：
 * - Token 自动注入（从 localStorage 读取）
 * - 统一响应解包：{code, data, message} → 直接返回 data
 * - 401/1005/1006：尝试 refresh 一次后重试；失败则清登录态并跳转登录页（不 reload）
 * - 其他业务错误 → 通过 setHttpErrorNotifier 注入的提示（管理端一般为 ElMessage.error）
 * - 局部 Loading 状态（通过 config.loadingRef 传入 Ref，自动管理）
 */
import axios, {
  type AxiosError,
  type AxiosRequestConfig,
  type AxiosResponse,
  type InternalAxiosRequestConfig,
} from 'axios'
import type { Ref } from 'vue'

const TOKEN_KEY = 'mtcg_token'
const REFRESH_KEY = 'mtcg_refresh_token'
const LOGOUT_EVENT = 'mtcg:logout'

/** 业务错误提示（由宿主注入，避免 common 强依赖 element-plus） */
type HttpErrorNotifier = (message: string) => void
let httpErrorNotifier: HttpErrorNotifier = (message) => {
  console.error(message)
}

/** 管理端启动时注入 ElMessage.error 等 */
export function setHttpErrorNotifier(notifier: HttpErrorNotifier): void {
  httpErrorNotifier = notifier
}

/** 扩展配置：标记已重试 / 跳过 refresh 流程 */
interface AuthRequestConfig extends InternalAxiosRequestConfig {
  _retry?: boolean
  _skipAuthRefresh?: boolean
}

let refreshing = false
let waitQueue: Array<(ok: boolean) => void> = []
/** 防止并发 401 重复跳转 */
let redirectingToLogin = false

// ========================================================
// axios 全局配置
// ========================================================

axios.defaults.baseURL = '/api'
axios.defaults.timeout = 10_000
axios.defaults.headers.common['Content-Type'] = 'application/json'

// ========================================================
// 请求拦截器：Token 注入
// ========================================================

axios.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEY)
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

// ========================================================
// 认证失效处理
// ========================================================

function isAuthCode(code: unknown): boolean {
  return code === 401 || code === 1005 || code === 1006
}

function isOnLoginPage(): boolean {
  const hash = window.location.hash || ''
  return hash.includes('/login')
}

/** 清除本地 Token 并跳转登录页（不整页刷新） */
export function clearAuthAndRedirect(): void {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(REFRESH_KEY)
  window.dispatchEvent(new Event(LOGOUT_EVENT))
  // 尽量同步清掉 Pinia 内存态（Pinia 可能尚未初始化，失败则忽略）
  void import('../stores/userStore')
    .then((mod) => {
      try {
        const store = mod.useUserStore()
        store.clearSession()
      } catch {
        // Pinia 未挂载时忽略
      }
    })
    .catch(() => {})

  if (redirectingToLogin || isOnLoginPage()) return
  redirectingToLogin = true
  // hash 路由：写成 #/login，避免部分环境下 hash 赋值异常
  window.location.hash = '#/login'
  // 下一轮事件循环后允许再次跳转（例如重新登录再过期）
  window.setTimeout(() => {
    redirectingToLogin = false
  }, 800)
}

async function tryRefreshAndRetry(error: AxiosError): Promise<AxiosResponse> {
  const config = error.config as AuthRequestConfig | undefined
  if (!config || config._retry || config._skipAuthRefresh) {
    clearAuthAndRedirect()
    return Promise.reject(error)
  }

  const url = config.url ?? ''
  if (url.includes('/auth/refresh') || url.includes('/auth/login') || url.includes('/auth/logout')) {
    clearAuthAndRedirect()
    return Promise.reject(error)
  }

  const refreshToken = localStorage.getItem(REFRESH_KEY)
  if (!refreshToken) {
    clearAuthAndRedirect()
    return Promise.reject(error)
  }

  if (refreshing) {
    return new Promise<AxiosResponse>((resolve, reject) => {
      waitQueue.push((ok) => {
        if (!ok) {
          reject(error)
          return
        }
        config._retry = true
        config.headers = config.headers ?? {}
        config.headers.Authorization = `Bearer ${localStorage.getItem(TOKEN_KEY) ?? ''}`
        resolve(axios(config))
      })
    })
  }

  refreshing = true
  config._retry = true
  try {
    // 使用裸 axios，跳过本文件的业务解包；标记跳过以免 refresh 失败再次触发 refresh
    const refreshResp = await axios.post(
      '/auth/refresh',
      { refreshToken },
      { _skipAuthRefresh: true } as AxiosRequestConfig,
    )
    const body = refreshResp.data as { code?: number; data?: { token?: string; refreshToken?: string }; message?: string }
    if (body.code !== 0 || !body.data?.token) {
      throw new Error(body.message || 'refresh failed')
    }
    localStorage.setItem(TOKEN_KEY, body.data.token)
    if (body.data.refreshToken) {
      localStorage.setItem(REFRESH_KEY, body.data.refreshToken)
    }
    waitQueue.forEach((cb) => cb(true))
    waitQueue = []
    config.headers = config.headers ?? {}
    config.headers.Authorization = `Bearer ${body.data.token}`
    return axios(config)
  } catch {
    waitQueue.forEach((cb) => cb(false))
    waitQueue = []
    clearAuthAndRedirect()
    return Promise.reject(error)
  } finally {
    refreshing = false
  }
}

// ========================================================
// 响应拦截器：解包 + 错误处理
// ========================================================

/**
 * 将业务码 401（HTTP 仍可能是 200）转为可走 refresh 的 AxiosError。
 * 注意：success 拦截器里 Promise.reject 不会进入同链的 error 拦截器，
 * 必须在此处直接调用 tryRefreshAndRetry，否则不会清登录态/跳转。
 */
function toAuthError(response: { data: unknown; config: InternalAxiosRequestConfig }): AxiosError {
  return {
    response: { status: 401, data: response.data, statusText: 'Unauthorized', headers: {}, config: response.config },
    config: response.config,
    isAxiosError: true,
    toJSON: () => ({}),
    name: 'AuthError',
    message: 'Unauthorized',
  } as AxiosError
}

axios.interceptors.response.use(
  (response) => {
    const data = response.data as { code?: number } | undefined
    if (data && isAuthCode(data.code)) {
      const cfg = response.config as AuthRequestConfig
      if (cfg._skipAuthRefresh) {
        clearAuthAndRedirect()
        return Promise.reject(toAuthError(response))
      }
      return tryRefreshAndRetry(toAuthError(response))
    }
    return response
  },
  (error: AxiosError) => {
    const res = error.response?.data as Record<string, unknown> | undefined
    const code = res?.code ?? error.response?.status
    const cfg = error.config as AuthRequestConfig | undefined

    if (isAuthCode(code) && !cfg?._skipAuthRefresh) {
      return tryRefreshAndRetry(error)
    }

    if (isAuthCode(code)) {
      clearAuthAndRedirect()
      return Promise.reject(error)
    }

    let msg = error.message || '网络错误'
    if (res?.message) msg = res.message as string
    else if (error.response?.status === 403) msg = '无操作权限'
    else if (error.response?.status === 404) msg = '请求的资源不存在'
    else if (error.response?.status === 429) msg = '请求过于频繁，请稍后再试'
    else if (error.response?.status && error.response.status >= 500) msg = '服务器开小差了，请稍后再试'

    httpErrorNotifier(msg)
    return Promise.reject(error)
  },
)

// ========================================================
// 统一响应解包
// ========================================================

function extractData<T>(r: { data: Record<string, unknown> }): T {
  const res = r.data
  if (res.code !== 0) {
    // 鉴权失败应由拦截器跳转；此处兜底，避免再弹「Token 无效」干扰
    if (isAuthCode(res.code)) {
      clearAuthAndRedirect()
      throw new Error((res.message as string) || 'Unauthorized')
    }
    httpErrorNotifier((res.message as string) || '请求失败')
    throw new Error((res.message as string) || '请求失败')
  }
  return res['data'] as T
}

// ========================================================
// 统一包装：自动管理 loading + 解包
// ========================================================

/**
 * 包装 axios Promise，自动：
 * 1. 请求发起 → loadingRef.value = true
 * 2. settled（无论成功/失败）→ loadingRef.value = false
 * 3. 解包 {code, data, message}
 */
async function wrap<T>(promise: Promise<unknown>, loadingRef?: Ref<boolean>): Promise<T> {
  if (loadingRef) loadingRef.value = true
  try {
    const r = await promise
    if (loadingRef) loadingRef.value = false
    return extractData<T>(r as { data: Record<string, unknown> })
  } catch (e) {
    if (loadingRef) loadingRef.value = false
    throw e
  }
}

// ========================================================
// 带 loading 的请求方法（供 API 层使用）
// ========================================================

export const http = {
  get: function <T>(url: string, loadingRef?: Ref<boolean>): Promise<T> {
    return wrap<T>(axios.get(url), loadingRef)
  },

  post: function <T>(url: string, data?: unknown, loadingRef?: Ref<boolean>): Promise<T> {
    return wrap<T>(axios.post(url, data), loadingRef)
  },

  // 原始 axios 实例（带 config 参数的 GET 请求，如带 params）
  getWithParams: function <T>(
    url: string,
    config: Record<string, unknown> = {},
    loadingRef?: Ref<boolean>,
  ): Promise<T> {
    if (loadingRef) loadingRef.value = true
    return axios.get(url, config)
      .then((r) => {
        if (loadingRef) loadingRef.value = false
        return extractData<T>(r as { data: Record<string, unknown> })
      })
      .catch((e) => {
        if (loadingRef) loadingRef.value = false
        throw e
      }) as Promise<T>
  },

  // 带 config 的 POST（如 query params 或 FormData）
  postWithConfig: function <T>(
    url: string,
    data: unknown,
    config: Record<string, unknown> = {},
    loadingRef?: Ref<boolean>,
  ): Promise<T> {
    if (loadingRef) loadingRef.value = true
    return axios.post(url, data, config)
      .then((r) => {
        if (loadingRef) loadingRef.value = false
        return extractData<T>(r as { data: Record<string, unknown> })
      })
      .catch((e) => {
        if (loadingRef) loadingRef.value = false
        throw e
      }) as Promise<T>
  },
}

export { wrap, TOKEN_KEY, REFRESH_KEY, LOGOUT_EVENT }
