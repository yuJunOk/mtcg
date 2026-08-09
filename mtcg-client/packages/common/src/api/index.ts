/**
 * API 层统一入口
 *
 * 使用方式：
 *   import { client } from '@mtcg/common/api'
 *   const user = await client.user.me()
 *   const result = await client.auth.login({ username, password })
 *
 * 说明：
 * - 所有 API 方法已自动解包：返回 data 而非 {code, data, message} 包装
 * - Token 自动注入（无需手动处理）
 * - 错误码 401/1005/1006 → 自动跳转登录页
 * - 其他业务错误 → ElMessage.error
 */
export { client, client as default } from './client'
export type * from './client'
export * from './generated'
