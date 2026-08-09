/**
 * API 层统一入口
 *
 * 使用方式：
 *   // 方式一：按需导入（推荐）
 *   import { authApi, userApi, adminUserApi, adminProductApi, adminCardApi, dashboardApi } from '@mtcg/common/api'
 *
 *   // 方式二：统一 client（兼容旧写法，后续会废弃）
 *   import { client } from '@mtg/common/api'
 *   client.admin.listUsers({ ... })
 *
 * 特点：
 * - 所有方法已自动解包：返回 data 而非 {code, data, message} 包装
 * - Token 自动注入（无需手动处理）
 * - 错误码 401/1005/1006 → 自动跳转登录页
 * - 其他业务错误 → ElMessage.error
 */

// 子 API（推荐）
export { authApi } from './authApi'
export { userApi } from './userApi'
export { adminUserApi, adminProductApi, adminCardApi } from './adminApi'
export { dashboardApi } from './dashboardApi'

// 兼容层 client（旧写法用）
export { client } from './client'
export type * from './client'

// 底层 axios 实例（不推荐直接使用，仅特殊情况）
export { axios } from './request'
