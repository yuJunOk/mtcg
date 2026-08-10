/**
 * 用户状态管理
 *
 * 职责：
 * - 登录 / 退出 / Token（含 refreshToken）持久化
 * - 当前用户信息缓存
 * - 角色判断（isAdmin）
 * - 监听 mtcg:logout 事件同步清空内存态
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Ref } from 'vue'
import type { UserVO } from '../types/user'
import { authApi } from '../api/authApi'
import { userApi } from '../api/userApi'
import { TOKEN_KEY, REFRESH_KEY, LOGOUT_EVENT } from '../api/request'

export const useUserStore = defineStore('user', () => {
  // ========== 状态 ==========
  const token = ref<string | null>(localStorage.getItem(TOKEN_KEY))
  const refreshToken = ref<string | null>(localStorage.getItem(REFRESH_KEY))
  const userInfo = ref<UserVO | null>(null)

  // ========== 计算属性 ==========
  const isLoggedIn = computed(() => !!token.value)
  const role = computed(() => userInfo.value?.role ?? undefined)

  // ========== 操作 ==========

  /** 仅清空内存与本地 Token（不调登出接口） */
  function clearSession() {
    token.value = null
    refreshToken.value = null
    userInfo.value = null
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(REFRESH_KEY)
  }

  /**
   * 登录（自动关闭 loading）
   * @param dto 登录参数
   * @param loadingRef 可选，登录请求期间自动管理
   */
  async function login(dto: { usercode: string; password: string }, loadingRef?: Ref<boolean>) {
    const data = await authApi.login(dto, loadingRef)
    token.value = data.token ?? null
    refreshToken.value = data.refreshToken ?? null
    userInfo.value = data.user ?? null
    localStorage.setItem(TOKEN_KEY, data.token ?? '')
    if (data.refreshToken) {
      localStorage.setItem(REFRESH_KEY, data.refreshToken)
    } else {
      localStorage.removeItem(REFRESH_KEY)
    }
  }

  /**
   * 拉取当前用户信息（自动关闭 loading）
   */
  async function fetchUserInfo(loadingRef?: Ref<boolean>) {
    userInfo.value = await userApi.me(loadingRef)
  }

  /** 退出登录：尽力通知后端拉黑 access token，再清本地态 */
  async function logout() {
    try {
      await authApi.logout()
    } catch {
      // best effort：网络失败仍清本地
    }
    clearSession()
  }

  /** 是否为管理员（系统管理员或卡牌管理员） */
  function isAdmin() {
    return role.value === 'SYS_ADMIN' || role.value === 'CARD_ADMIN'
  }

  // 请求层清登录态时同步清空 Pinia（避免页面仍显示已登录）
  if (typeof window !== 'undefined') {
    window.addEventListener(LOGOUT_EVENT, () => {
      token.value = null
      refreshToken.value = null
      userInfo.value = null
    })
  }

  return {
    // 状态
    token,
    refreshToken,
    userInfo,
    // 计算
    isLoggedIn,
    role,
    // 操作
    login,
    fetchUserInfo,
    logout,
    clearSession,
    isAdmin,
  }
})
