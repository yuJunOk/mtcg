/**
 * 认证 API（登录/注册匿名；logout 需携带 access token）
 */
import type { Ref } from 'vue'
import { http } from './request'
import type { LoginVO, UserLoginDTO, UserRegisterDTO } from '../types/user'

export const authApi = {
  register: (dto: UserRegisterDTO, loadingRef?: Ref<boolean>) =>
    http.post<number>('/auth/register', dto, loadingRef),

  login: (dto: UserLoginDTO, loadingRef?: Ref<boolean>) =>
    http.post<LoginVO>('/auth/login', dto, loadingRef),

  /** 使用 refreshToken 换取新的 access（及可选旋转后的 refresh） */
  refresh: (refreshToken: string, loadingRef?: Ref<boolean>) =>
    http.post<LoginVO>('/auth/refresh', { refreshToken }, loadingRef),

  /** 登出：后端拉黑当前 access token（尽力而为） */
  logout: (loadingRef?: Ref<boolean>) =>
    http.post<void>('/auth/logout', undefined, loadingRef),
}
