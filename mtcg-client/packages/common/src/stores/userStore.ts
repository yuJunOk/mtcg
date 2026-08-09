/**
 * 用户状态管理
 *
 * 职责：
 * - 登录 / 退出 / Token 持久化
 * - 当前用户信息缓存
 * - 角色判断（isAdmin）
 *
 * 依赖：
 * - client.auth.login：OpenAPI 生成，已自动解包
 * - client.user.me：OpenAPI 生成，已自动解包
 * - client.dashboard.getStats：仪表盘统计（解包后）
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { UserVO } from '../api'
import { client } from '../api'

const TOKEN_KEY = 'mtcg_token'

export const useUserStore = defineStore('user', () => {
  // ========== 状态 ==========
  const token = ref<string | null>(localStorage.getItem(TOKEN_KEY))
  const userInfo = ref<UserVO | null>(null)

  // ========== 计算属性 ==========
  const isLoggedIn = computed(() => !!token.value)
  const role = computed(() => userInfo.value?.role ?? undefined)

  // ========== 操作 ==========

  /** 登录 */
  async function login(dto: { username: string; password: string }) {
    const data = await client.auth.login(dto)
    token.value = data.token ?? null
    userInfo.value = data.user ?? null
    localStorage.setItem(TOKEN_KEY, data.token ?? '')
  }

  /** 拉取当前用户信息 */
  async function fetchUserInfo() {
    userInfo.value = await client.user.me()
  }

  /** 退出登录 */
  function logout() {
    token.value = null
    userInfo.value = null
    localStorage.removeItem(TOKEN_KEY)
  }

  /** 是否为管理员（系统管理员或卡牌管理员） */
  function isAdmin() {
    return role.value === 'SYS_ADMIN' || role.value === 'CARD_ADMIN'
  }

  return {
    // 状态
    token,
    userInfo,
    // 计算
    isLoggedIn,
    role,
    // 操作
    login,
    fetchUserInfo,
    logout,
    isAdmin,
  }
})
