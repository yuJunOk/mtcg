/**
 * 认证 API（匿名接口，无需登录）
 */
import { axios, extractData } from './request'
import type { LoginVO, UserLoginDTO, UserRegisterDTO } from '../types/user'

export const authApi = {
  register: (dto: UserRegisterDTO) =>
    axios.post<Record<string, unknown>>('/auth/register', dto)
      .then(r => extractData<number>(r)),

  login: (dto: UserLoginDTO) =>
    axios.post<Record<string, unknown>>('/auth/login', dto)
      .then(r => extractData<LoginVO>(r)),
}
