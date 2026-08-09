# AI 辅助项目开发指南

> 适用于 AI 编码助手深度参与的中大型软件开发项目。

---

## 一、基本原则

### 1.1 核心原则

| 原则 | 说明 |
| --- | --- |
| **Spec First** | 先有设计或步骤文档，再让 AI 实现 |
| **一步步来** | 每次只做一件事，做完审查再继续 |
| **人拍板，AI 干活** | 设计决策、技术选型必须人定；AI 负责执行和文档 |
| **交付物即文档** | 代码 + 文档一起交付，不能只交代码 |

### 1.2 人机协作分工

| 环节 | 人负责 | AI 负责 |
| --- | --- | --- |
| 需求/设计 | 提供业务知识、拍板决策 | 整理、分析、提出选项 |
| 实现步骤 | 审查完整性 | 读详细设计，产出步骤文档 |
| 编码 | 审查质量 | 按步骤实现代码 |
| 文档 | 关键决策记录 | 注释、Javadoc、README |
| 审查 | 终审 | Review、NPE 检查 |

### 1.3 沟通技巧

```
✅ 好的指令：
"按 14-实现步骤-迭代二.md，只实现步骤1-2，其他不要动"
"帮我出实现步骤文档，参照 04-详细设计-迭代二.md"
"这个 Service 有 bug，帮我修第 40 行，不要改其他地方"

❌ 差的指令：
"帮我完成迭代二"（太大）
"优化一下代码"（不明确）
"先做个登录功能"（无设计文档）
```

---

## 二、AI 使用场景

### 2.1 写文档（AI 强项）

- **实现步骤文档**：参照详细设计，列出文件清单 + 关键代码 + 验收标准
- **代码注释 / Javadoc**
- **变更日志**（基于 git diff）
- **README 的技术栈 / 工程结构部分**

**不适合交给 AI**：需求决策、规则裁定、路线图

### 2.2 写代码（AI 强项）

- **CRUD**：Controller / Service / DAO 增删改查
- **DTO / VO 转换**
- **单元测试**
- **配置类**：Spring Config、MyBatis 映射

**需要人盯着**：业务逻辑、性能敏感代码、安全相关代码

### 2.3 代码审查（AI 强项）

```
"帮我 review 最近改的 5 个文件，重点检查 NPE"
"这个 toVO 方法能否优化"
```

---

## 三、项目开发流程

```
详细设计  →  实现步骤文档  →  AI 实现  →  人工审查  →  提交
                 ↑                ↓
            (AI 产出)          (git diff 查看)
```

### 3.1 实现步骤文档格式

每个步骤文档包含：前置依赖 → 数据库 → 实现步骤（含文件名、关键代码、设计理由）→ 验收标准。

**示例结构**：

```markdown
## 前置依赖
## 数据库（是否需要变更）
## 后端实现
  - 步骤1：补充 ErrorCode（🔲）
    文件：common/result/ErrorCode.java
    理由：建立卡牌/产品专属错误码域，便于分类处理
    关键代码：
      CARD_NOT_FOUND(3001, "卡牌不存在"),
  - 步骤2：CardServiceImpl 改用 ErrorCode（🔲）
## 前端实现
  - 步骤1：AdminApi 新增方法（🔲）
## 验收标准
  - [ ] 后端编译通过
  - [ ] 接口正常
```

### 3.2 审核与推进

- AI 每步完成后，**人用 `git diff` 审查**改动范围
- 确认无多余改动 → 人说"继续" → AI 做下一步
- 有问题 → 描述清楚哪里不对 → AI 只修这个问题

### 3.3 提交规范

```bash
git add .
git status          # 先看改了什么文件
git diff --stat    # 再看改动大小
git commit -m "feat(迭代二): 完成卡牌与产品管理 CRUD"
```

格式：`type(范围): 描述` — feat / fix / docs / refactor / test

---

## 四、文档体系

```
docs/
├── 规则文档/              ← 游戏规则（裁定基准）
├── 设计文档/              ← 需求 + 概要 + 详细设计 + 实现步骤
├── 编码规范/              ← 前后端编码规范
└── 设计规范/              ← UI设计系统
```

| 文档 | 谁读 | 什么时候 |
| --- | --- | --- |
| 编码规范 | AI | 每次编码前 |
| 详细设计 | AI + 人 | 开始新迭代前 |
| 实现步骤 | AI | 实现过程中 |
| 规则文档 | 人 | 裁定争议时 |

---

## 五、协同规范

### 5.1 前后端枚举对齐

前端枚举文件与后端 `common/enums` **逐一对应**，每个后端枚举 → 一个独立前端文件。增删改查时能快速定位。

### 5.2 Monorepo 共享约定

`packages/common` 是 PC 和移动端的共享层：PixiJS 引擎、Pinia 状态、API 封装、TS 类型。两端只需各自实现 view 层。

### 5.3 API 与类型分包规范（本项目现状）

**已废弃 OpenAPI 生成**，改用手动分包。原因是：响应解包 `{code,data,message}→data` 逻辑无法注入到 `generated/request.ts`，用手写 axios 更可控。

```
api/
├── request.ts      ← axios 封装（Token 注入 + 响应解包 + 错误处理）
├── authApi.ts      ← 认证（匿名）
├── userApi.ts      ← 用户个人资料
├── adminApi.ts     ← 管理员（用户/产品/卡牌管理）
├── dashboardApi.ts ← 仪表盘
└── index.ts        ← 统一导出（子 API + axios）

types/
├── common.ts       ← PageVO<T> 等通用类型
├── user.ts         ← UserVO + LoginVO + 用户相关 DTO
├── card.ts         ← CardVO + 卡牌相关 DTO
├── card-ui.ts      ← 前端 UI 类型（下拉选项 + 工具函数）
├── product.ts      ← ProductVO + 产品相关 DTO
├── dashboard.ts    ← DashboardStatsVO + HealthVO
└── index.ts        ← 统一导出（export * from './xxx'）
```

**新增 API 方法规范**：

```ts
// 在对应 api/xxxApi.ts 文件末尾追加，格式：
export const xxxApi = {
  方法名: (参数: 类型) =>
    axios.get<Record<string, unknown>>('/admin/路径', { params: { ... } })
      .then(r => extractData<返回类型>(r)),
}
```

**新增类型**：在 `types/xxx.ts` 中定义 VO/DTO，`types/index.ts` 末尾追加 `export * from './xxx'`。

### 5.4 实现步骤状态跟踪

每个步骤标注状态：`🔲 未开始` / `🚧 进行中` / `✅ 已完成`。调整步骤时不得覆盖已完成步骤的状态。

---

## 六、Git 常用命令

```bash
# 查看改动
git diff --stat        # 文件列表
git diff              # 详细改动
git diff -- docs/     # 只看文档改动

# 撤销改动
git checkout -- 文件   # 撤销单个文件
git checkout -- .      # 撤销全部

# 状态
git status            # 当前状态
git log --oneline -5  # 最近 5 条提交
```

---

## 七、常见问题

**Q: AI 把文件改乱了？**
A: `git checkout -- 文件` 立即恢复，只让 AI 做一个小改动。

**Q: AI 改动了不该改的？**
A: 每次实现前 `git add .` 记录起点，实现后 `git diff` 核查范围。

**Q: AI 跳过文档直接写代码？**
A: 明确要求"先出步骤文档，我审查后再实现代码"。

**Q: AI 重复造轮子？**
A: 先问"这个功能是否已有"，再决定是否新建。

---

## 八、检查清单

每次开始新迭代前：

- [ ] 详细设计文档已读完
- [ ] 实现步骤文档已出（或更新）
- [ ] 代码无未提交改动（干净起点）

每次提交前：

- [ ] `git diff` 看过改动范围
- [ ] 无多余文件
- [ ] 提交信息符合规范

---

## 九、原始经验记录

以下是从项目实践中沉淀的经验：

### 9.1 命名一致性

问题：AI 在不同轮次对同一模块使用不同命名（如 `admin-web` / `mtcg-admin-web` / `mtcg-client-admin`），导致文档和代码不一致。

对策：项目初期确定命名规范，写入编码规范并禁止变更。

```
mtcg-                  ← 项目前缀
├── server             ← 后端不带终端后缀
└── client             ← 前端 Monorepo
    └── packages/
        ├── common
        ├── admin-web  ← 管理后台
        ├── game-pc    ← PC 端
        └── game-mobile← 移动端
```

### 9.2 README 内容归属

全局内容（游戏概要 / 功能模块 / 开发路线）放根 README；模块专属内容放模块 README。不得混放。

### 9.3 图片素材验证

下载图片后用 `Format-Hex` 验证文件头：PNG = `89 50 4E 47`。不能只写入 URL 文本就当文件存在。

### 9.4 许可证与版权声明

声明"非商业用途"时选 CC BY-NC 4.0，不用 MIT。

---

> AI 是放大器：好的流程让 AI 放大效率，坏的流程让 AI 放大混乱。
