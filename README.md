<div align="center">

<img src="./assets/banner-1.png" alt="漫威对战卡牌·超英击战" width="380">

# MTCG

**MTCG 规则引擎与在线对战平台**

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-green.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)
[![MyBatis-Flex](https://img.shields.io/badge/MyBatis--Flex-1.9-red.svg)](https://mybatis-flex.com/)
[![Vue](https://img.shields.io/badge/Vue-3.5-brightgreen.svg)](https://vuejs.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.5-blue.svg)](https://www.typescriptlang.org/)
[![Vite](https://img.shields.io/badge/Vite-5.4-purple.svg)](https://vitejs.dev/)
[![Element Plus](https://img.shields.io/badge/Element%20Plus-2.8-409eff.svg)](https://element-plus.org/)
[![PixiJS](https://img.shields.io/badge/PixiJS-8.4-e91e63.svg)](https://pixijs.com/)
[![License](https://img.shields.io/badge/License-CC%20BY--NC%204.0-blue.svg)](LICENSE)

</div>

---

## 游戏概要

> 多元宇宙的能量持续暴走，对撞事件让每条时间线互相纠缠侵蚀，无数个宇宙将因此毁灭。玩家扮演**时间线的守护者**，构筑超英卡组，在多元宇宙中击战。

两名玩家各持 **50 张角色卡组** + **9 张冲击卡组**，通过**号召**部署角色、在**战区**攻击对手，将冲击卡放入**时间线**达 9 张，或**对方卡组抽空**即可获胜。

| 核心机制 | 说明 |
| --- | --- |
| 号召系统 | Lv3 及以下直接放置；Lv4+ 须撤退合计 Lv 的角色 |
| 战区布局 | 先锋(1) + 侧翼(2) + 后卫(1)，攻击距离按线性路径计算 |
| 冲击卡 | 攻击对方破绽（空置战区）-> 冲击卡组顶 1 张放入时间线 |
| 关键词能力 | 应对、拦截、连击、强袭、空袭、唯一 |

---

## 项目结构

```
mtcg/                          ← 仓库根目录
├── docs/                      ← 全局文档（规则 + 设计 + 规范）
│   ├── 规则文档/              ← 综合规则书 + 术语表 + Q&A
│   ├── 设计文档/              ← 需求分析 + 概要设计 + 迭代一~九详细设计 + 实现步骤
│   ├── 编码规范/              ← 后端/前端 AI 编码规范
│   ├── 设计规范/              ← UI 设计系统
│   └── README.md
├── mtcg-server/               ← 后端（Java 17 + Spring Boot 3.5）
│   ├── src/main/java/com/aris/mtcg/
│   │   ├── advice/            ← 全局异常处理
│   │   ├── common/            ← 枚举、常量、异常、统一响应
│   │   ├── component/         ← 组件（环境后处理器等）
│   │   ├── config/            ← 配置（CORS 等）
│   │   ├── controller/        ← 控制器
│   │   ├── dao/               ← MyBatis-Flex Mapper
│   │   ├── domain/            ← 实体 / DTO / VO / Query
│   │   ├── engine/            ← 引擎（战斗 / 效果 / 规则）
│   │   ├── manager/           ← 管理层（JWT 等）
│   │   └── service/           ← 业务逻辑
│   ├── src/main/resources/
│   │   ├── sql/init.sql       ← 建表脚本
│   │   └── application.yml
│   └── pom.xml
├── mtcg-client/               ← 前端 Monorepo（Vue 3 + PixiJS + Element Plus）
│   └── packages/
│       ├── common/            ← 共享包：类型、API 封装、Pinia 状态、PixiJS 基类、主题样式
│       ├── admin-web/         ← 管理后台：Vue 3 + Element Plus
│       ├── game-pc/           ← PC 游戏端：横屏布局 + Electron 打包
│       └── game-mobile/       ← 移动游戏端：竖屏布局 + Capacitor 打包
├── assets/                    ← 静态资源（卡图、Banner）
├── scripts/                   ← 工具脚本（卡面提取、卡牌设计生成、规则文档提取）
├── AGENTS.md                  ← AI 编码规则入口
└── README.md
```

---

## 模块概览

| 模块 | 技术 | 说明 |
| --- | --- | --- |
| **mtcg-server** | Java 17 / Spring Boot 3.5 / MyBatis-Flex / PostgreSQL 16 | 后端服务（REST API + 规则引擎） |
| **mtcg-client** | Vue 3 / TypeScript / PixiJS / Pinia / Element Plus / Electron / Capacitor | 前端 Monorepo（管理后台 + PC/移动游戏客户端） |

---

## 功能模块

| 模块 | 说明 | 状态 |
| --- | --- | --- |
| 用户系统 | 注册 / 登录 / JWT 鉴权 / RBAC 权限 / 用户管理 CRUD | 🚧 开发中（迭代一） |
| 卡牌数据管理 | 角色卡 / 冲击卡 / 产品分类 / 产品 CRUD + 条件查询 + 批量导入 | 🚧 开发中（迭代二） |
| 卡组构筑 | 卡组创建 / 校验 / 收藏管理 / 导入导出 | 📋 设计完成 |
| 对战引擎 | 回合流程（迭代四）+ 行动与战斗（迭代五） | 📋 设计完成 |
| 效果系统 | 触发/持续/启动/应对型效果 + 关键词能力（拦截/强袭/空袭等） | 📋 设计完成 |
| 对战接口 | 创建对局 / 执行操作 / 复盘回放 / 持久化（迭代七） | 📋 设计完成 |
| AI 对战 | 启发式策略 / 局面评估 / 难度分级（迭代八） | 📋 设计完成 |
| 排位系统 | 段位 / 匹配 / 排行榜 / 赛季（迭代九） | 📋 设计完成 |
| 系统管理 | 系统配置 / 审计日志 / 体验增强（迭代十） | 📋 设计完成 |

---

## 开发路线

> 三阶段 MVP：先能管 → 再能玩 → 再完善

| 迭代 | 阶段 | 范围 | 状态 |
| --- | --- | --- | --- |
| 迭代一 | 先能管 | 基础设施 + 用户系统（含用户管理） | 🚧 开发中 |
| 迭代二 | 先能管 | 卡牌与产品管理 | 📋 待开始 |
| 迭代三 | 先能玩 | 卡组构筑 + 游戏端登录 | 📋 待开始 |
| 迭代四 | 先能玩 | 引擎状态模型 + 回合流程 | 📋 待开始 |
| 迭代五 | 先能玩 | 行动与战斗处理器 | 📋 待开始 |
| 迭代六 | 再完善 | 效果系统 + 关键词能力 | 📋 待开始 |
| 迭代七 | 先能玩 | 对战 API + 游戏对战页 | 📋 待开始 |
| 迭代八 | 再完善 | AI 对战 | 📋 待开始 |
| 迭代九 | 再完善 | 排位系统 + 匹配 + 排行榜 | 📋 待开始 |
| 迭代十 | 再完善 | 系统配置 + 审计日志 | 📋 待开始 |

> ⚠️ **迭代依赖链**：迭代四 → 迭代五 → 迭代六 → 迭代七 → 迭代八 → 迭代九 → 迭代十（迭代三可与迭代四并行开发）

---

## 快速导航

- [后端 README](./mtcg-server/README.md) - 后端详情 + 快速启动
- [前端 Monorepo](./mtcg-client/) - 游戏客户端（PC + 移动端）
- [文档导航](./docs/README.md) - 全局文档索引
- [AI 编码规则](./AGENTS.md) - 编码规范 + 设计文档索引

---

## 文档体系

| 文档 | 说明 |
| --- | --- |
| [文档导航](./docs/README.md) | 全局文档索引 |
| [规则文档](./docs/规则文档/) | 游戏规则书（综合规则书 + 术语表 + Q&A） |
| [设计文档](./docs/设计文档/) | 详细设计（需求分析 + 概要设计 + 迭代一~十 + 实现步骤） |
| [编码规范](./docs/编码规范/) | 后端 / 前端 AI 编码规范 |
| [UI 设计系统](./docs/设计规范/UI设计系统.md) | 色彩、字体、间距、组件等视觉标准 |

---

## 快速启动

### 后端

**环境要求**：JDK 17+ / PostgreSQL 16+ / Maven 3.9+

```sql
-- 数据库准备
CREATE DATABASE db_mtcg;
```

启动后会自动执行 `sql/init-schema.sql`（建表）和 `sql/init-data.sql`（初始化数据）。

```bash
cd mtcg-server
./mvnw spring-boot:run
```

验证：健康检查 `GET http://localhost:8081/api/health` | Swagger UI `http://localhost:8081/api/swagger-ui.html`

### 前端

**环境要求**：Node.js 18+ / npm 10+

```bash
cd mtcg-client
npm install          # 安装依赖（Monorepo workspace）

# 开发模式（三端独立启动）
npm run dev:admin    # 管理后台 → http://localhost:5175
npm run dev:pc       # PC 游戏端 → http://localhost:5176
npm run dev:mobile   # 移动游戏端 → http://localhost:5177

# 构建
npm run build:admin  # 管理后台
npm run build:pc     # PC 游戏端（含 Electron 打包）
npm run build:mobile # 移动游戏端
```

---

## 官方资源

- 官网：[marvelherorush.com](https://www.marvelherorush.com/cn/home)
- 综合规则书：[微信公众号](https://mp.weixin.qq.com/s/qWZcy5F4BK7_5rGf2KmWWQ)

---

<div align="center">

**该项目仅用于学习交流，不涉及任何商业用途。  
Marvel ©2026 MARVEL. Hero Rush © Card Fun.**

</div>
