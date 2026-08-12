# 实现步骤：迭代七 — 对战 API 与游戏对战页

> 参照 [09-详细设计-迭代七](./09-详细设计-迭代七-对战API与对局持久化.md)；前端对齐 [01-需求分析](./01-需求分析.md)「迭代七：对战 API + 游戏对战页」
>
> **状态：⬜ 未开始**

---

## 总路线

```
阶段一（先能管）
  ├─ 迭代一：基础设施 + 用户系统（含用户管理）  ✅ 已完成
  └─ 迭代二：卡牌与产品管理                   ✅ 已完成
阶段二（先能玩）
  ├─ 迭代三：卡组构筑 + 卡牌收藏（后端）      ✅ 已完成（游戏端登录/卡组 UI 未交付，本迭代补）
  ├─ 迭代四：引擎状态模型 + 回合流程        ✅ 已完成
  ├─ 迭代五：行动与战斗处理器               ✅ 已完成
  ├─ 迭代六：效果系统 + 关键词              ✅ 已完成
  └─ 迭代七：对战 API + 游戏对战页          ← 本文档当前步骤
阶段三（再完善）
  └─ 迭代八~十：AI 对战 → 排位系统 → 系统管理
```

---

# 迭代七：对战 API 与游戏对战页

> 目标：
> 1. **后端**：引擎 → REST + 对局持久化（创建 / 查询 / 操作 / 认输 / 复盘 / 历史 / 统计 / 崩溃恢复）
> 2. **前端（game-pc）**：从「静态空壳」补到「能登录 → **构筑合法卡组** → 开局/进房 → 双人对完一局」
>
> 强制约束：引擎零侵入；混合持久化；无 WebSocket（短轮询）；前后端 VO 字段名一致；主交付 `game-pc`。
>
> **范围说明**：迭代三只交了卡组/收藏**后端**；本迭代补齐游戏端登录 + **卡组列表/构筑编辑器**（对接已有 `/decks`），再对接对战。不要求玩家靠 SQL/Swagger 造卡组。

---

## game-pc 现状盘点（2026-08 扫描）

> 结论：**不是「差一个 Battle 绑 API」**，而是客户端壳几乎未接入后端。迭代三需求里的「游戏端登录」实际未做（仅后端卡组 + 联调豁免）。

### 已有

| 项 | 说明 |
| --- | --- |
| 工程骨架 | Vite + Vue3 + Pinia + Electron 配置；`vue-router` **已依赖但未使用** |
| 代理 | `vite.config.ts` 已把 `/api` 代理到 `localhost:8080` |
| 主题 | 引入 `@mtcg/common` 的 dark/light CSS；Home 有主题切换 |
| 页面壳 | `App.vue` 用 `ref` 在 `HomeView` / `BattleView` 间切换（非路由） |
| `HomeView` | 侧栏 + Hero + 假统计/假探索/假卡组排行；用户写死 `PlayerOne` |
| `BattleView` | HUD/战区/手牌扇形 **视觉壳**；区域数量、场面、手牌多为常量/假数据 |
| common 可复用 | `authApi` / `userApi` / `userStore`（admin-web 已跑通）；`request.ts` 401 → `#/login` |
| 静态资源 | 卡背图、先后攻图、背景图等 |

### 没有 / 不可用

| 项 | 说明 |
| --- | --- |
| 登录 / 注册页 | **无**；全仓库仅 `admin-web` 有 `LoginPage`（Element Plus） |
| 路由与守卫 | **无** `createRouter`；未登录也能进「对战」壳 |
| 用户态接入 | game-pc **未** `useUserStore`；侧栏头像/昵称为假 |
| `deckApi` / 卡组类型 | common **无**；无法选合法卡组开局 |
| `gameApi` / 对战 VO | **无**；`gameStore` 的 `loadGame`/`doAction` 仍 throw TODO |
| 图鉴 / 收藏页 | 导航有、View 无；本迭代图鉴/收藏按「范围分层」占位；**卡组+拖拽必做** |
| 错误提示注入 | game-pc 未 `setHttpErrorNotifier`（失败可能只 console） |

### 对本迭代前端范围的含义

必须先补 **壳层 + 卡组构筑**，再谈对战：

```
登录注册 + Hash 路由守卫
    → 首页显示真实用户 / 登出
    → deckApi + 我的卡组列表（含拖拽重排）+ 构筑编辑器（含条目拖拽排序）
    → gameApi + gameStore
    → 开局/进房（选 isValid 卡组）
    → BattleView 绑真实局面 + 轮询
```

### 范围分层（勿混为一谈）

| 能力 | 排期 | 理由 |
| --- | --- | --- |
| **卡组列表 / 构筑编辑器** | **迭代七必做** | 对战前置；迭代三只交后端 |
| **拖拽排序**（列表 `sort_order` + 构筑内条目数组序） | **迭代七必做** | 后端 `/decks/reorder` 与 JSON 数组序已就绪；交互简单，应做则做 |
| **独立图鉴产品页** | 迭代七之后（产品化） | 非 AI；构筑卡池用已有 `cardApi.list` 即可，图鉴是浏览体验加分 |
| **卡牌收藏游戏端 UI** | **迭代八之后、AI 辅助构筑之前**（可与 FR6.2 同批） | 后端迭代三已有（FR2.3）；详细设计写明收藏为 **AI 构筑辅助提供库存数据**。迭代八本体是 **AI 对战（FR6.1）**，不把收藏 UI 塞进八的核心范围 |
| **AI 辅助构筑（FR6.2）** | **迭代八之后** | [迭代八详细设计](./10-详细设计-迭代八-AI对战.md) 明确**不包含** FR6.2；与 AI 对战同属 FR6 族，排在对战 AI 交付之后 |
| **排行榜 / 生涯精修** | 迭代九前后 | 依赖对局历史/排位数据 |
| **Pixi 战场 / WebSocket / mobile 同步** | 后续增强 | 不挡「打完一局」 |

> 说明：收藏本身是玩家功能（FR2.3），不是「AI 算法」；但与 **AI 构筑辅助** 数据链绑在一起，故跟 FR6.2 同轨道排期，而不是跟拖拽排序一起丢进「以后再说」。
---

## 与现有代码的 as-built 对齐

### 后端

| 设计稿 | 现有 | 处理 |
| --- | --- | --- |
| `init.sql` | `init-schema.sql` | 追加建表 |
| `SecurityUtils` | 无 | `@RequestAttribute(ATTR_USER_ID)` |
| `/api/v1/games` | `context-path=/api` | `/api/games` |
| ErrorCode 1003–1007 | 与用户域冲突 | **5101–5199** |
| `ActionLog.seq` | 无 | 本迭代补；Service 落库前赋值 |
| create 返回 VO | §6.1 为 Long | 返回 `gameId` |
| Handler 已 logAction | 有 | Service 勿双写 |

### 前端

| 现状 | 处理 |
| --- | --- |
| `App.vue` 无路由 | 改 **Hash 路由**（`request.ts` 已写死 `#/login`） |
| 无登录页 | 新增游戏风 `LoginView`（**不用** Element Plus 搬 admin 页） |
| types 里引擎形 `GameState` | 对战路径改为后端 `GameStateVO` 字段 |
| 无 deck/game API | common 新增完整 `deckApi` + `gameApi`；卡池复用已有 `cardApi` |
| Home/Battle 假数据 | 对战主路径去假；侧栏「卡组」接通构筑（含拖拽）；图鉴/收藏/排行按「范围分层」占位或后置 |

### HTTP（仅 GET/POST）

| 操作 | 方法 | 路径 |
| --- | --- | --- |
| 我的卡组列表 | GET | `/decks` |
| 卡组详情 | GET | `/decks/{id}` |
| 创建卡组 | POST | `/decks` |
| 编辑卡组 | POST | `/decks/{id}` |
| 删除卡组 | POST | `/decks/{id}/delete` |
| 校验卡组 | POST | `/decks/{id}/validate` |
| 卡组列表拖拽重排 | POST | `/decks/reorder`（**本迭代必接**） |
| 卡牌分页（构筑卡池） | GET | `/cards`（已有） |
| 创建对局 | POST | `/games` |
| 查询局面 | GET | `/games/{id}` |
| 执行操作 | POST | `/games/{id}/actions` |
| 认输 | POST | `/games/{id}/surrender` |
| 复盘 | GET | `/games/{id}/replay` |
| 历史 | GET | `/games/history` |
| 统计 | GET | `/games/stats` |

> Controller：`/games/history`、`/games/stats` 写在 `/{id}` 之前。

---

## 步骤状态

| 步骤 | 内容 | 状态 |
| --- | --- | --- |
| 1 | 后端 - `init-schema.sql` 追加 `mtcg_game_record` | ✅ |
| 2 | 后端 - ErrorCode + ActionLog.seq | ✅ |
| 3 | 后端 - DO / DTO / VO | ⬜ |
| 4 | 后端 - GameMapper | ⬜ |
| 5 | 后端 - GameManager / Context / Serializer | ⬜ |
| 6 | 后端 - GameService 主链路 | ⬜ |
| 7 | 后端 - 历史 / 统计 / 复盘 / 隐私 | ⬜ |
| 8 | 后端 - GameController | ⬜ |
| 9 | 后端 - 单测 | ⬜ |
| 10 | 前端 - Hash 路由 + 登录/注册 + 守卫 | ⬜ |
| 11 | 前端 - 首页接用户态（去假用户） | ⬜ |
| 12 | 前端 - `deckApi` + 卡组类型（完整 CRUD） | ⬜ |
| 13 | 前端 - 我的卡组列表 + 构筑编辑器（含拖拽排序） | ⬜ |
| 14 | 前端 - 对战类型 + `gameApi` + `gameStore` | ⬜ |
| 15 | 前端 - 对战入口（创建 / 加入） | ⬜ |
| 16 | 前端 - `BattleView` 绑局面与操作 | ⬜ |
| 17 | 前端 - 轮询 + 双开联调 | ⬜ |
| 18 | 验收关闭 | ⬜ |

---

## 代码盘点

### 后端新增

| 文件 | 说明 |
| --- | --- |
| `init-schema.sql`（追加） | `mtcg_game_record` |
| `GameDO` + DTO/VO 一组 | 详细设计 §3 |
| `GameMapper` / `GameContext` / `GameManager` / `GameStateSerializer` | 持久化 + 缓存 |
| `GameService` / `Impl` / `GameController` | 业务 + REST |

### 后端修改

| 文件 | 说明 |
| --- | --- |
| `ErrorCode.java` | 5101–5199 |
| `ActionLog.java` | `seq` |

### 前端新增 / 修改

| 文件 | 说明 |
| --- | --- |
| `game-pc/src/router/index.ts` | Hash 路由 + `beforeEach` 登录守卫 |
| `game-pc/src/views/LoginView.vue` | 登录（可含简易注册入口） |
| `game-pc/src/main.ts` / `App.vue` | 挂路由；错误 notifier |
| `game-pc/src/views/HomeView.vue` | 真实用户 / 登出；对战/卡组入口跳路由 |
| `game-pc/src/views/DeckListView.vue` | 我的卡组列表（新建/删除/编辑/校验/合法标记/**拖拽重排**） |
| `game-pc/src/views/DeckBuilderView.vue` | 构筑编辑器（卡池 + 主/冲击区 + 保存/校验/**条目拖拽排序**） |
| `game-pc/src/views/MatchLobbyView.vue` | 创建/加入对局（选合法卡组） |
| `game-pc/src/views/BattleView.vue` | 绑真实局面 |
| `common/.../api/deckApi.ts` + `types/deck.ts` | 卡组 CRUD / validate / **reorder** |
| `common/.../api/gameApi.ts` + `types/game.ts` | 对战 API / VO |
| `common/.../stores/gameStore.ts` | 真实接入 + 轮询 |

### 本迭代不做（已排期，见「范围分层」）

| 项 | 归属 |
| --- | --- |
| WebSocket | 未来 |
| AI 对战（FR6.1） | **迭代八** |
| 排位 | **迭代九** |
| 独立图鉴产品页 | 迭代七后产品化（构筑不依赖它） |
| 卡牌收藏游戏端 UI | **迭代八之后 → 接 AI 辅助构筑**（后端已有） |
| AI 辅助构筑（FR6.2） | **迭代八之后**（八的设计已排除） |
| 完整 Pixi 战场 / game-mobile 同步 / 精美复盘播放器 | 后续增强 |

---

## 步骤详解

### 步骤 1–9：后端

与先前规划相同，摘要如下（细节仍以详细设计 §2–§8 为准）：

1. **建表** `mtcg_game_record` → `init-schema.sql`
2. **ErrorCode** `5101+`；**ActionLog.seq**
3. **DO/DTO/VO**
4. **GameMapper**（history/stats）
5. **GameManager / Context / Serializer**
6. **GameService**：create / get / execute / surrender / recover（注意 player1≠firstPlayer、流水勿双写）
7. **历史/复盘/隐私裁剪** + `availableActions`
8. **GameController** `/games`
9. **单测** Serializer / Service / 引擎回归

**检验**：`mvnw.cmd compile` + 相关 test。建议后端先可用，再并行前端壳。

---

### 步骤 10：Hash 路由 + 登录/注册 + 守卫

**背景**：`request.clearAuthAndRedirect` 固定跳转 `window.location.hash = '#/login'`，game-pc **必须**用 `createWebHashHistory`。

1. 新建 `router/index.ts`：
   - `/login` 公开
   - `/` Home、`/decks` 卡组列表、`/decks/:id` 构筑器、`/match` 大厅、`/battle/:gameId?` 对战 — 需登录
2. `beforeEach`：无 token 且非公开页 → `/login`；已登录访问 login → `/`
3. `LoginView.vue`（游戏暗色风，纯 CSS，**不**依赖 Element Plus）：
   - 玩家编号 + 密码 → `userStore.login`
   - 可选：注册折叠区调 `authApi.register` 后自动登录
4. `main.ts`：`app.use(router)`；`setHttpErrorNotifier`（简单 toast/alert 即可）
5. `App.vue`：改为 `<router-view />`，去掉手动 `currentView` 切换

**检验**：未登录打开 `/` → 跳登录；登录后进首页；清 token / 401 回到 `#/login`。

---

### 步骤 11：首页接用户态

改 `HomeView.vue`：

1. 侧栏用户区：`userStore.userInfo`（昵称 / usercode）；无则 `fetchUserInfo`
2. 登出按钮 → `userStore.logout` → `/login`
3. 「开始对战」→ `router.push('/match')`
4. 侧栏「卡组」/探索「卡组构筑」「我的卡组」→ `/decks`（**接通**，不要再占位）
5. 图鉴/收藏/排行：侧栏可点但进占位页或「即将推出」（排期见范围分层）
6. 假统计可暂留或标演示——不要求完整生涯页

**检验**：登录后侧栏显示真实账号；点「卡组」能进列表路由。

---

### 步骤 12：deckApi + 卡组类型（完整 CRUD）

common 对齐迭代三后端 VO/DTO（字段名一致）：

| 类型 | 用途 |
| --- | --- |
| `DeckCardEntry` | `{ cardCode, quantity }` |
| `DeckVO` | 列表/详情 |
| `DeckCreateDTO` / `DeckUpdateDTO` | 创建/编辑 |
| `DeckValidateResultVO` | 校验结果（错误列表等，按后端字段） |

`deckApi.ts`：

```text
list(tag?) / get(id) / create(dto) / update(id, dto)
delete(id) / validate(id) / reorder(dto)   // reorder 本迭代必做
```

卡池继续用已有 `cardApi.list` / `cardApi.get`，无需新后端。

**检验**：登录后 `list` 能返回（空列表也行）；`reorder` 类型与后端 `DeckReorderDTO` 对齐。

---

### 步骤 13：我的卡组列表 + 构筑编辑器（含拖拽）

后端 `/decks*` 已就绪，本步补游戏端 UI（暗色主题，纯 CSS，复用 `Card` 组件优先）。

#### 13.1 `DeckListView.vue`（`/decks`）

- 列出本人卡组：名称、主/冲击张数、`isValid` 标记、标签
- 操作：新建、编辑、删除确认、手动「校验」
- **拖拽重排列表**：拖动卡片改序 → 调用 `deckApi.reorder`（`POST /decks/reorder`）；失败则回滚本地顺序并提示
- 无合法卡组时引导去构筑（**不要**让用户去 Swagger/SQL）

#### 13.2 `DeckBuilderView.vue`（`/decks/new` 或 `/decks/:id`）

完整构筑闭环：

| 区域 | 行为 |
| --- | --- |
| 卡池（左） | `cardApi.list` 分页/筛选（类型、颜色、名称关键词）；点选或「+」加入 |
| 主卡组（右上） | 目标 **50** 张；`cardCode`/名/数量；+/- 改 quantity；同编号合并一条 |
| 冲击卡组（右下） | 目标 **9** 张；同上 |
| **条目拖拽排序** | 主/冲击区内拖拽改 `List` 顺序；保存时按数组序整表 `update`（对齐迭代三：数组序=卡组内排序） |
| 顶栏 | 卡组名；实时张数；`isValid`/校验摘要 |
| 保存 | 新建 → `create`；已有 → `update` |
| 校验 | `validate`；展示后端错误条目 |
| 返回 | 回列表；未保存可 confirm |

实现提示：可用 HTML5 DnD 或轻量库（如 `sortablejs`）；不引入 Element Plus。无障碍兜底：保留上移/下移按钮亦可，但**拖拽为主验收项**。

**规则对齐**：合法性以服务端为准；大厅只列 `isValid===true`（或开打前再 validate）。

**检验**：

1. 从零新建 → 凑齐 50+9 → 保存 → validate 通过  
2. 构筑内拖拽改序 → 保存 → 再打开顺序保持  
3. 列表拖拽 → `reorder` 成功 → 刷新后顺序保持  

---

### 步骤 14：对战类型 + gameApi + gameStore

1. `types/game.ts`：**严格对齐**后端 `GameStateVO` / `PlayerStateVO` / `ActionRequestDTO` / `ActionResultVO` 等
2. 废弃对战 UI 对旧引擎形 `GameState` 的依赖
3. `gameApi`：create / getState / executeAction / surrender / replay / history / stats
4. 改写 `gameStore`：`createGame` / `loadGame` / `doAction` / `surrender` / 轮询启停

`localPlayerId` = `String(userStore.userInfo.id)`。

**检验**：能 create → getState（需已有合法卡组，用步骤 13 造）。

---

### 步骤 15：对战入口（创建 / 加入）

`MatchLobbyView.vue`：

| 模式 | 表单 |
| --- | --- |
| 创建 | 己方**合法**卡组、对手用户 ID、对手卡组 ID、`gameMode=CASUAL`、可选先攻/调度 |
| 加入 | 输入 `gameId` → `loadGame` → `/battle/:gameId` |

- 己方卡组下拉：`deckApi.list` 过滤 `isValid===true`
- 若无合法卡组：按钮引导去 `/decks`，**禁止**灰开打且无说明

**检验**：账号 A/B 各自用游戏端构筑合法卡组后，A 创建、B 加入。

---

### 步骤 16：BattleView 绑局面与操作

改造现有壳（保留布局/样式）：

| 区域 | 数据源 |
| --- | --- |
| 回合 / 阶段 | `gameState.turnCount` / `currentPhase` |
| 对手信息 | `opponent.playerId` + `handCount` |
| 区域计数 | `deckCount` / `rushDeckCount` / retreat 等 |
| 战区 | `field.vanguard` / `flank` / `rearguard` / `base` |
| 手牌 | `localPlayer.hand` |
| 按钮 | `availableActions`（至少 END_PHASE、SURRENDER，以及已暴露的部署/号召/攻击） |
| 结束 | `status===FINISHED` 展示胜负 |

去掉主路径写死假数据。选目标可先「点选源 + 点选目标」。

**检验**：操作后己方手牌/场面变化；对手只见手牌数量。

---

### 步骤 17：轮询 + 双开联调

1. 不可操作时 `startPolling`（约 1.5–2s）；轮到自己或结束则停
2. `onUnmounted` 清理 timer
3. 双开：A/B 登录 → 各构筑或选用合法卡组 → 开打 → 轮询同步 → 认输或打完

**检验**：两名玩家不依赖 Swagger/SQL，纯游戏端打完一局。

---

### 步骤 18：验收关闭

对照下方清单打勾；更新本文档状态与 `AGENTS.md`。

---

## 步骤依赖关系

```
S1..S8[后端对战] --> S9[后端测试]
S3[VO] --> S14[gameApi]
S8 --> S14
S10[登录路由] --> S11[首页]
S11 --> S12[deckApi]
S12 --> S13[构筑UI]
S13 --> S15[大厅]
S14 --> S15
S14 --> S16[BattleView]
S15 --> S16
S16 --> S17[轮询联调]
S9 --> S18
S17 --> S18[验收]
```

建议节奏：后端 1–9 ∥ 前端壳 10–11；构筑 12–13 不依赖对战 API；大厅/战斗依赖构筑 + game API。

---

## 实现注意（易错点）

### 后端

1. ErrorCode 用 `5101+`
2. `player1`（发起方）≠ `firstPlayer`（先攻）
3. 流水勿双写；只补 seq
4. `/games/history` 与 `/{id}` 路由顺序

### 前端（针对空壳）

1. **必须先登录**，再谈对战；不要在无 Token 下硬调 `/games`
2. **必须 Hash 路由**，否则 401 跳转对不上
3. 游戏登录页不要照搬 admin Element Plus 皮肤；跟 UI 设计系统暗色变量
4. 类型以后端 VO 为准，勿混用旧引擎 `GameState`
5. 无合法卡组时引导去构筑，勿依赖 SQL/Swagger 造数据
6. 轮询 timer 必须清理；双开用两个浏览器配置档，避免 Token 互相覆盖
7. 首页：卡组接通；图鉴/收藏/排行按「范围分层」占位（收藏留给 AI 轨道）
8. 拖拽失败要回滚，避免 UI 序与 DB 不一致

---

## 迭代七验收标准

### 后端

- [ ] 对局 CRUD 主链路 + 认输 + 复盘/历史/统计
- [ ] 隐私裁剪与参与方校验
- [ ] 崩溃恢复可用
- [ ] `engine` 无 Spring/HTTP；编译与单测通过

### 前端（空壳 → 可构筑可打）

- [ ] 未登录进入受保护页会到 `#/login`
- [ ] 可注册/登录游戏端，首页显示真实用户，可登出
- [ ] **可在游戏端完成**：创建卡组 → 从卡池加入主/冲击 → 保存 → 校验通过
- [ ] **拖拽**：卡组列表可重排并持久化；构筑内条目可拖拽改序并保存后保持
- [ ] 卡组列表展示合法标记；可编辑/删除
- [ ] 能用本人合法卡组创建/加入对局（无合法卡组时引导去构筑）
- [ ] `BattleView` 主路径为真实 VO
- [ ] 双开可操作并靠轮询同步，能打完或认输结束一局
- [ ] game-pc `build` / typecheck 通过

---

## 后续迭代概览（排期）

| 批次 | 目标 | 说明 |
| --- | --- | --- |
| **迭代八** | AI 对战（FR6.1） | 启发式 AI、难度；**不含**收藏 UI、**不含** AI 构筑辅助 |
| **迭代八之后（AI 扩展）** | ① 卡牌收藏游戏端 UI → ② AI 辅助构筑（FR6.2） | 收藏后端已有；为 FR6.2 提供库存；可同迭代文档拆两步 |
| **迭代九** | 排位 | 匹配 + 积分；大厅可替换手填对手 |
| **产品化后续** | 独立图鉴页、排行/生涯精修 | 非 AI 主链 |
| **体验后续** | WebSocket 替轮询、Pixi 战场、mobile 同步 | 增强 |

---

## 参照索引

| 文档 | 用途 |
| --- | --- |
| [01-需求分析](./01-需求分析.md) | 迭代七交付；迭代三「游戏端登录」债务 |
| [09-详细设计-迭代七](./09-详细设计-迭代七-对战API与对局持久化.md) | 后端设计 |
| [10-详细设计-迭代八](./10-详细设计-迭代八-AI对战.md) | AI 对战范围（不含 FR6.2） |
| [15-实现步骤-迭代三](./15-实现步骤-迭代三-卡组构筑与卡牌收藏.md) | 卡组/收藏后端；拖拽与数组序约定 |
| [前端 AI 编码规范](../编码规范/前端AI编码规范.md) | Vue / API / 类型 |
| [UI 设计系统](../设计规范/UI设计系统.md) | 游戏端暗色主题 |
| [后端 AI 编码规范](../编码规范/后端AI编码规范.md) | 分层 / GET·POST |
