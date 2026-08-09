/**
 * 用户个人资料 API（需登录）
 */
import { axios, extractData } from './request'
import type { UserVO, UserUpdateDTO, ChangePasswordDTO } from '../types/user'

export const userApi = {
  me: () =>
    axios.get<Record<string, unknown>>('/users/me')
      .then(r => extractData<UserVO>(r)),

  updateMe: (dto: UserUpdateDTO) =>
    axios.put<Record<string, unknown>>('/users/me', dto)
      .then(() => undefined),

  changePassword: (dto: ChangePasswordDTO) =>
    axios.put<Record<string, unknown>>('/users/me/password', dto)
      .then(() => undefined),
}
