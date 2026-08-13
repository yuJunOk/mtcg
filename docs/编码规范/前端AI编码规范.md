# MTCG 前端 AI 编码规范

> 本文件是 AI 辅助前端开发的强制约束。每次编码前必须通读本文档。
> 后端规范见 [后端AI编码规范.md](./后端AI编码规范.md)。

---

## 0. 语言与基础约定

1. 所有解释、分析、总结使用中文
2. 代码保持 `TypeScript` / `Vue` 原始语法
3. 注释使用中文
4. 变量名、函数名、类型名统一使用英文
5. 避免使用 `any`，优先使用明确类型；无法确定时使用 `unknown`
6. 变量、函数参数、返回值、接口响应应显式声明类型

---

## 1. 技术基线

| 层 | 技术 | 说明 |
| --- | --- | --- |
| 框架 | Vue 3 + TypeScript | 统一 `<script setup lang="ts">` |
| 状态管理 | Pinia | 应用级状态 |
| 路由 | Vue Router 4 | 懒加载 |
| HTTP | Axios | 从 `@mtcg/common` 导入 |
| 管理后台 UI | Element Plus | 表格、表单、对话框 |
| 游戏端壳层 UI | Naive UI（game-pc） | 常规控件 + Toast/确认；图标用 emoji；**不用** Element Plus |
| 游戏渲染 | PixiJS 8 | 卡牌 Sprite、动画、拖拽 |
| 构建 | Vite 5 | npm workspaces Monorepo |
| 样式 | 纯 CSS / CSS Module | 管理后台用 scoped，游戏端按需 |

---

## 2. Vue 组件规范

### 2.1 语法与组织

- 统一 `<script setup lang="ts">`
- `Props`、`Emits` 必须显式声明类型
- 组件内部按顺序组织：**类型定义 → 状态 → 计算属性 → 方法 → 生命周期**
- 派生数据放入 `computed`，避免模板中重复计算
- 列表渲染必须提供稳定的 `key`

### 2.2 组件命名

| 类型 | 命名 | 示例 |
| --- | --- | --- |
| 页面视图 | `PascalCase + View` | `CardListView.vue` |
| 布局组件 | `PascalCase + Layout` | `MainLayout.vue` |
| 弹框 | `PascalCase + Dialog` | `CardFormDialog.vue` |
| 公共弹窗壳 | `MtcgDialog` | 关闭 / 可选全屏 / 确定取消 + slots |
| 选择器 | `PascalCase + Selector` | `ProductSelector.vue`、`CardFeatureSelector.vue` |
| 抽屉 | `PascalCase + Drawer` | `CardDetailDrawer.vue` |
| 路由 name | `kebab-case` | `card-list`、`product-list` |

新增业务路由默认使用懒加载：`() => import('@/views/xxx.vue')`

---

## 3. TypeScript 规范

### 3.1 类型定义

- 类型名使用语义化命名（如 `CardVO`、`CardQueryDTO`）
- 前后端共享类型定义在 `packages/common/src/types/`
- 迭代早期，VO 可以复制后端全部字段，避免后续扩展时反复补字段

### 3.2 字段命名

**原则：与后端 VO 字段名保持完全一致，禁止意译改名。**

```ts
// ✅ 禁止意译改名
cardType: row.cardType
cardTypeLabel: getCardTypeLabel(row.cardType)

// ❌
type: row.cardType
cardType: row.cardType === 'CHARACTER' ? '角色' : '冲击'
```

### 3.3 枚举

与后端枚举对应，每个后端枚举类对应一个独立前端文件：

```ts
// packages/common 或业务侧枚举映射（与后端 EnumCardType 对齐）
export const CardTypeOptions: Record<string, string> = {
  CHARACTER: '角色卡',
  RUSH_POINT: '冲击卡', // 禁止写成 IMPACT
}

export const CardType_DEFAULT = 'CHARACTER'

export function getCardTypeLabel(code: string): string {
  return CardTypeOptions[code] ?? code
}
```

---

## 4. 请求与错误处理

### 4.1 请求规范

**原则：所有 API 调用只使用 GET 和 POST 两种方法，与后端规范保持一致。**

| 操作 | HTTP | 调用方式 |
| --- | --- | --- |
| 查询列表/详情 | GET | `axios.get(url, { params })` |
| 新增/更新/删除/其他操作 | POST | `axios.post(url, data)` |

```ts
// ✅ 正确示例（经 http 封装；领域 API 内使用）
import { http } from './request'

http.get('/admin/cards', { params: { pageNum: 1, pageSize: 20 } })
http.get('/admin/cards/1')
http.post('/admin/cards', cardData)
http.post('/admin/cards/1', cardData)
http.post('/admin/cards/1/delete')
http.post('/admin/users/1/status', { status: 'DISABLED' })

// ❌ 错误示例（禁止使用 PUT、DELETE、PATCH）
axios.put('/admin/cards/1', cardData)
axios.delete('/admin/cards/1')
```

### 4.2 请求层

统一从 `@mtcg/common` 的 `api/` 导入领域 API 或类型。返回 Promise，必须显式声明返回类型。

```ts
// ✅ 领域 API（已解包 data；Token / 401 refresh 由 request 层处理）
import { cardApi, productApi, cardFeatureApi } from '@mtcg/common/api'
import type { CardVO, CardQueryDTO } from '@mtcg/common/types'

const page = await cardApi.list({ pageNum: 1, pageSize: 20 })

// ❌ 不要从旧路径导入
import { client } from '@mtcg/common/api'
import { http } from '@mtcg/common/utils/request'
```

> 业务码 `401` 可能随 HTTP 200 返回：`request.ts` 在成功拦截器内触发 refresh / 清登录态并跳转 `#/login`，业务代码无需重复处理。

### 4.2 何时链式、何时 async/await

| 场景 | 建议 |
| --- | --- |
| 单请求、分支简单 | 链式 `.then/.catch` |
| 多请求顺序 await、中间变量多 | `async/await` |
| 需 `try/finally` 统一收尾 | `async/await` |

### 4.4 错误处理约定

| 错误类型 | 处理方式 |
| --- | --- |
| 网络错误 / 500 | 提示"网络异常，请稍后重试"，记录日志 |
| 401 未认证 | 跳转登录页 |
| 403 无权限 | 提示"无权限操作" |
| 业务校验错误 | 提示后端返回的错误信息 |
| 前端参数校验 | 实时校验，不等后端返回 |

---

## 5. 状态管理（Pinia）

### 5.1 ref vs reactive 选择原则

| 场景 | 推荐 | 原因 |
| --- | --- | --- |
| 基础类型 | `ref()` | `reactive` 不支持直接替换整个对象 |
| 对象/数组 | `ref()` 或 `reactive` | 复杂对象建议 `ref`，避免响应式丢失 |
| 解构响应式对象 | 使用 `toRefs` | 保持解构后的响应性 |

```ts
// ✅ 基础类型用 ref
const count = ref(0)
const name = ref('')

// ✅ 解构响应式对象用 toRefs
const state = reactive({ count: 0, name: '' })
const { count, name } = toRefs(state)

// ❌ 避免响应式丢失
const obj = reactive({ a: 1 })
let { a } = obj  // a 失去响应式
```

### 5.2 本地持久化

- 本地持久化优先使用 `@vueuse/core` 的 `useStorage` / `useSessionStorage`
- 存储 key 统一定义在对应模块常量中
- 组件卸载时必须清理事件监听和定时器

---

## 6. Element Plus 规范（管理后台）

### 6.1 通用

- 表单、表格、弹框**默认使用 Element Plus 默认尺寸**，不要全局强加 `size="small"`（除非局部信息密度明确需要）
- 主操作按钮 `type="primary"`
- 危险操作 `type="danger"`，并配合 `ElMessageBox.confirm` 确认
- 弹框底部按钮统一右对齐，文案使用「确定 / 取消 / 关闭」
- 业务弹窗优先基于 `MtcgDialog`（支持全屏、`title-extra` / `header-extra` / `footer` slots）
- 资源选择用 `XxxSelector`：表单项为只读 input 触发；弹窗内左右栏用 `el-splitter`；单选用 `multiple=false`（radio），多选默认 checkbox
- 特征等纯文本摘要不要用彩色 Tag 背景硬编码业务色

### 6.2 表格

- 分页置于 `el-card` 的 `#footer` 插槽（固定底部）
- 表格卡片 `flex: 1`，内容区撑满
- 表格加 `height="100%"` 让 Element Plus 自动计算滚动区域
- 列表页搜索区：`el-form--inline` 的 `form-item` 去掉多余 `margin-bottom`，避免操作行下大块空白

---

## 6A. 游戏端 UI（game-pc / Naive UI）

> 对战**局面渲染**仍用 Pixi / 自研战场组件；壳层与工具页的**常规交互控件**优先 Naive。**禁止** Element Plus 进入 game-pc。

### 原则

1. **能用 Naive 就用 Naive**：`NButton` / `NInput` / `NForm` / `NCheckbox` / `NSpin` / `NEmpty` / `NTag` / `NPagination` / `NModal` / `NSelect` / `NSwitch` / `NSpace` 等；不要手写原生 `button`/`input` 当常规控件
2. **图标**：禁止业务里自写 `<svg>` 图标。Naive 不自带图标库 → **用 emoji**（写在按钮文案或默认槽即可）。功能图（如计时环）用 CSS/emoji，勿手绘 SVG 图标
3. **例外**：品牌字标、卡面/轨道点选、战场落点等「非常规表单控件」可保留原生结构 + 设计 token CSS
4. **反馈**：`@/feedback` 的 `toast` / `confirm`；`App.vue` 接 `setHttpErrorNotifier(toast.error)`；禁止 `window.alert` / `window.confirm`
5. 主题跟随 `themeStore`（青绿 primary）；同一失败勿重复 Toast

### 业务弹窗

优先 `NModal`；若已有 `MtcgDialog` 壳且结构复杂，内部按钮也须改为 `NButton`，图标用 emoji。

---

## 7. 环境变量规范

### 7.1 文件命名

| 文件 | 用途 |
| --- | --- |
| `.env` | 默认值，所有环境共享 |
| `.env.development` | 开发环境 |
| `.env.production` | 生产环境 |

### 7.2 变量命名

- 前端环境变量必须以 `VITE_` 开头
- 后端 API 地址：`VITE_API_BASE_URL`
- 开启调试模式：`VITE_DEBUG=true`

```bash
# .env.development
VITE_API_BASE_URL=http://localhost:8081/api
VITE_DEBUG=true

# .env.production
VITE_API_BASE_URL=https://api.mtcg.com/api
VITE_DEBUG=false
```

---

## 8. 大文件组织

### 8.1 Region 分段

单文件超过 300 行时，用 `//#region 区域名` / `//#endregion` 分段：

```ts
//#region 类型定义
//#region 状态
//#region 计算属性
//#region 方法
//#region 生命周期
//#endregion
```

### 8.2 大文件拆分

**触发条件（满足任一）：**
- 单文件超过 800 行
- 同一页面有 3 个及以上独立功能区域
- 单个 `//#region` 超过 200 行

拆分前必须询问确认，拆分方案：按功能区域拆为 Composables（`useXxx.ts`）。

### 8.3 函数组织

- **先声明后使用**：被调用的函数写在调用者后面
- **相关函数聚集**：同一功能的函数放在一起
- **纯函数优先**：无副作用的工具函数放前面
- **副作用后置**：请求、状态修改等放后面

---

## 9. 注释规范

- 不写"这是什么"（代码已说明）
- 写"为什么这样"（业务逻辑、特殊处理、注意事项）
- 复杂算法或边界处理必须注释
- 临时方案或待优化点用 `// TODO:` 或 `// FIXME:` 标记

---

## 10. 样式规范

- 管理后台：`<style scoped>`，页面内容区撑满用 flex 布局
- 游戏端：`<style scoped>`，PixiJS Canvas 内用引擎 API 控制样式
- 保持单文件组件风格，不拆分为独立 `.css` 文件（除非多页面共享）

---

## 11. 交付要求

- 修改代码时，优先补齐对应类型和错误处理
- 验证遵循"最小必要"原则：只做能覆盖本次改动风险的最小验证
- 纯文档、注释、样式、排版等轻量改动，若可通过 diff 或局部查看确认，默认不运行命令验证
- 不默认进行整体验证，不把 `npm run build` 作为常规收尾动作
- 说明变更时，优先给出受影响文件、核心原因、实际验证情况与剩余风险

---

## 参考文档

- [后端 AI 编码规范](./后端AI编码规范.md)
- [UI 设计系统](../设计规范/UI设计系统.md)
