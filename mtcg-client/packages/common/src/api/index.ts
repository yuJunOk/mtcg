/**
 * API 层统一入口
 *
 * 使用方式：
 *   // 公开 API（无需登录）
 *   import { authApi, userApi, cardApi, productApi, cardFeatureApi } from '@mtcg/common/api'
 *
 *   // 管理员 API（需登录 Token）
 *   import { adminUserApi, adminProductApi, adminCardApi, dashboardApi } from '@mtcg/common/api'
 *
 * 特点：
 * - 所有方法已自动解包：返回 data 而非 {code, data, message} 包装
 * - Token 自动注入（无需手动处理）
 * - 错误码 401/1005/1006 → 尝试 refresh，失败则跳转登录页（不 reload）
 * - 其他业务错误 → setHttpErrorNotifier 注入的提示（管理端一般为 ElMessage.error）
 */

// 公开 API（无需登录）
export { authApi } from './authApi'
export { userApi } from './userApi'
export { cardApi } from './cardApi'
export { productApi } from './productApi'
export { cardFeatureApi } from './cardFeatureApi'

// 管理员 API（需登录 Token）
export { adminUserApi, adminProductApi, adminCardApi } from './adminApi'
export { dashboardApi } from './dashboardApi'

export { setHttpErrorNotifier } from './request'
