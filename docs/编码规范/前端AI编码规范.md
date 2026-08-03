# MTCG 前端 AI 编码规范

> 本文件是 AI 辅助前端开发的强制约束。每次编码前必须通读本文档。
> 后端规范见 [后端AI编码规范.md](./后端AI编码规范.md)。

---

## 0. 语言与基础约定

1. 所有解释、分析、总结使用中文
2. 代码保持 `TypeScript` / `Vue` 原始语法
3. 注释使用中文
4. 变量名、函数名、类型名统一使用英文，不使用中文命名
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
| 抽屉 | `PascalCase + Drawer` | `CardDetailDrawer.vue` |
| 路由 name | `kebab-case` | `card-list`、`product-list` |

### 2.3 路由

- 新增业务路由默认使用懒加载：`() => import('@/views/xxx.vue')`
- 路由 `name` 使用 `kebab-case`

---

## 3. TypeScript 规范

### 3.1 类型定义

- 类型名使用语义化命名（如 `CardVO`、`CardQueryDTO`）
- 前后端共享类型定义在 `packages/common/src/types/`
- 迭代早期，VO 可以复制后端全部字段，避免后续扩展时反复补字段；后期稳定后再按需精简

### 3.2 字段命名

**原则：与后端 VO 字段名保持完全一致，禁止意译改名。**

| 规则 | 说明 |
| --- | --- |
| 字段名 | 与后端 VO 完全一致，不在前端映射层改名 |
| 布尔字段 | 后端 `isXxx` / `hasXxx` → 前端保持同名 |
| ID 字段 | 后端 `Long` 类型 → 前端用 `number`（MTCG 用自增 ID，无需精度问题） |
| 枚举字段 | 必须通过工具函数转换，禁止硬编码 `=== 'xxx'` |

```ts
// ✅ 禁止意译改名
cardType: row.cardType               // ✅ 保持原名
cardTypeLabel: getCardTypeLabel(row.cardType)  // ✅ 显示名用新 key

// ❌
type: row.cardType                   // ❌ 改名
cardType: row.cardType === 'CHARACTER' ? '角色' : '冲击'  // ❌ 硬编码
```

### 3.3 枚举

与后端枚举对应，格式统一：

```ts
// 每个后端枚举类对应一个独立前端文件
export const CardTypeOptions: Record<string, string> = {
  CHARACTER: '角色卡',
  IMPACT: '冲击卡',
}

export const CardType_DEFAULT = 'CHARACTER'

export function getCardTypeLabel(code: string): string {
  return CardTypeOptions[code] ?? code
}
```

---

## 4. 请求与错误处理

### 4.1 请求层

- 统一从 `@mtcg/common` 的 `api/` 目录导入请求方法
- 请求方法返回 Promise，必须显式声明返回类型
- 所有异步请求必须有错误处理（`.catch` 或 `try/catch`）

```ts
import { cardApi } from '@mtcg/common/api/cardApi'

// 单请求、分支简单 → 链式 .then/.catch
cardApi.list(query)
  .then((res) => {
    tableData.value = res.data.records ?? []
    total.value = res.data.total ?? 0
  })
  .catch(() => {
    tableData.value = []
    total.value = 0
  })
```

### 4.2 何时链式、何时 async/await

| 场景 | 建议 |
| --- | --- |
| 单请求、分支简单 | 链式 `.then/.catch` |
| 多请求顺序 await、中间变量多 | `async/await` |
| 需 `try/finally` 统一收尾 | `async/await` 或链式 `.finally` |

---

## 5. Element Plus 规范（管理后台）

### 5.1 通用

- 表单、表格工具栏、弹框底部操作区默认 `size="small"`
- 主操作按钮 `type="primary"`
- 危险操作 `type="danger"`，并配合 `ElMessageBox.confirm` 确认
- 弹框底部按钮统一右对齐，文案使用"确定 / 取消 / 关闭"

### 5.2 表格

- 分页置于 `el-card` 的 `#footer` 插槽（固定底部）
- 表格卡片 `flex: 1`，内容区撑满
- 表格加 `height="100%"` 让 Element Plus 自动计算滚动区域

### 5.3 表单

- 管理后台使用 Element Plus 原生表单校验（`el-form` + `rules`）
- 游戏端如需复杂表单，后续可引入 `vee-validate`

---

## 6. 状态管理（Pinia）

- 应用级状态优先使用 `Pinia`
- 本地持久化优先使用 `@vueuse/core` 的 `useStorage` / `useSessionStorage`
- 存储 key 统一定义在对应模块常量中
- 组件卸载时必须清理事件监听和定时器

---

## 7. 大文件组织

### 7.1 Region 分段

单文件超过 300 行时，用 `//#region 区域名` / `//#endregion` 分段：

```ts
//#region 类型定义
//#region 状态
//#region 计算属性
//#region 方法
//#region 生命周期
//#endregion
```

### 7.2 大文件拆分

**触发条件（满足任一）：**
- 单文件超过 800 行
- 同一页面有 3 个及以上独立功能区域
- 单个 `//#region` 超过 200 行

**拆分前必须询问确认，不要自动拆分。** 拆分方案：按功能区域拆为 Composables（`useXxx.ts`）。

### 7.3 函数组织

- **先声明后使用**：被调用的函数写在调用者后面
- **相关函数聚集**：同一功能的函数放在一起
- **纯函数优先**：无副作用的工具函数放前面
- **副作用后置**：请求、状态修改等放后面

---

## 8. 注释规范

- 不写"这是什么"（代码已说明）
- 写"为什么这样"（业务逻辑、特殊处理、注意事项）
- 复杂算法或边界处理必须注释
- 临时方案或待优化点用 `// TODO:` 或 `// FIXME:` 标记

---

## 9. 样式规范

- 管理后台：`<style scoped>`，页面内容区撑满用 flex 布局
- 游戏端：`<style scoped>`，PixiJS Canvas 内用引擎 API 控制样式
- 保持单文件组件风格，不拆分为独立 `.css` 文件（除非多页面共享）

---

## 10. 交付要求

- 修改代码时，优先补齐对应类型和错误处理
- 验证遵循"最小必要"原则：只做能覆盖本次改动风险的最小验证
- 纯文档、注释、样式、排版等轻量改动，若可通过 diff 或局部查看确认，默认不运行命令验证
- 不默认进行整体验证，不把 `npm run build` 作为常规收尾动作
- 说明变更时，优先给出受影响文件、核心原因、实际验证情况与剩余风险

---

## 11. Monorepo 跨包引用

- 所有包通过 `@mtcg/xxx` 引用，不使用相对路径跨包
- `packages/common` 负责类型定义和 API 封装，各端只负责 view 布局
- 新增共享能力优先放入 `common`，避免 game-pc 和 game-mobile 重复实现

```ts
// ✅
import { cardApi } from '@mtcg/common/api/cardApi'
import type { CardVO } from '@mtcg/common/types'

// ❌
import { cardApi } from '../../common/src/api/cardApi'
```

---

## 参考文档

- [后端 AI 编码规范](./后端AI编码规范.md)
- [UI 设计系统](../设计规范/UI设计系统.md)
- [概要设计 §7.2 前端技术栈](../设计文档/02-概要设计.md#72-前端技术栈mtcg-client)
- [实现步骤-迭代一](../设计文档/13-实现步骤-迭代一-用户系统与用户管理.md)