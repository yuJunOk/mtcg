// ========================================================
// 认证
// ========================================================

export interface LoginVO {
  token: string
  user: UserVO
}

export interface UserLoginDTO {
  username: string
  password: string
}

export interface UserRegisterDTO {
  username: string
  password: string
  nickname?: string
}

// ========================================================
// 用户（个人）
// ========================================================

export interface UserVO {
  id: number
  username: string
  nickname: string
  role: string
  status: string
  createTime: string
  updateTime: string
}

export interface UserUpdateDTO {
  nickname?: string
}

export interface ChangePasswordDTO {
  oldPassword: string
  newPassword: string
}

// ========================================================
// 用户管理（管理员）
// ========================================================

export interface UserQueryDTO {
  username?: string
  nickname?: string
  role?: string
  status?: string
  page?: number
  size?: number
}

export interface AdminUserCreateDTO {
  username: string
  password: string
  nickname?: string
  role: string
}

export interface AdminUserUpdateDTO {
  nickname?: string
  role?: string
  status?: string
}

export interface AdminResetPasswordDTO {
  newPassword: string
}
