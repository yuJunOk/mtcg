/**
 * 用户个人资料 API（需登录）
 */
import type { Ref } from 'vue'
import { http } from './request'
import type { UserVO, UserUpdateDTO, ChangePasswordDTO } from '../types/user'

export const userApi = {
  me: (loadingRef?: Ref<boolean>) =>
    http.get<UserVO>('/users/me', loadingRef),

  updateMe: (dto: UserUpdateDTO, loadingRef?: Ref<boolean>) =>
    http.post<void>('/users/me', dto, loadingRef),

  changePassword: (dto: ChangePasswordDTO, loadingRef?: Ref<boolean>) =>
    http.post<void>('/users/me/password', dto, loadingRef),
}
