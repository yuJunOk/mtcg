// ========================================================
// 认证
// ========================================================

export interface LoginVO {
  token: string
  /** 刷新令牌（可选，后端启用 refresh 后返回） */
  refreshToken?: string
  user: UserVO
}

export interface UserLoginDTO {
  usercode: string
  password: string
}

export interface UserRegisterDTO {
  password: string
  username?: string
}

// ========================================================
// 用户（个人）
// ========================================================

export interface UserVO {
  id: number
  usercode: string
  username: string
  avatar: string | null
  role: string
  status: string
  createTime: string
}

export interface UserUpdateDTO {
  username?: string
}

export interface ChangePasswordDTO {
  oldPassword: string
  newPassword: string
}

// ========================================================
// 用户管理（管理员）
// ========================================================

export interface UserQueryDTO {
  usercode?: string
  username?: string
  role?: string
  status?: string
  pageNum?: number
  pageSize?: number
}

export interface AdminUserCreateDTO {
  password: string
  username?: string
  role: string
}

export interface AdminUserUpdateDTO {
  username?: string
  role?: string
  status?: string
}

export interface AdminResetPasswordDTO {
  newPassword: string
}
