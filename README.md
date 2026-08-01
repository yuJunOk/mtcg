<div align="center">

<img src="./assets/banner-1.png" alt="漫威对战卡牌·超英击战" width="380">

# 超英击战 · MTCG

**漫威对战卡牌规则引擎与在线对战平台**

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-green.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)
[![MyBatis-Flex](https://img.shields.io/badge/MyBatis--Flex-1.9-red.svg)](https://mybatis-flex.com/)
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
│   ├── 规则文档/
│   ├── 设计文档/
│   ├── 编码规范/
│   └── README.md
├── mtcg-server/               ← 后端（Java + Spring Boot）
│   ├── docs/                  ← 后端技术方案研究
│   ├── README.md
│   └── ...
├── mtcg-client/               ← 前端 Monorepo（Vue 3 + PixiJS）
│   ├── packages/
│   │   ├── common/            ← 共享包：PixiJS 引擎、Pinia 状态、API
│   │   ├── game-pc/           ← PC 端：横屏布局 + Electron 打包
│   │   └── game-mobile/       ← 移动端：竖屏布局 + Capacitor 打包
│   └── package.json
├── assets/                    ← 静态资源（卡图、Banner）
├── scripts/                   ← 工具脚本
├── .gitignore
└── README.md
```

---

## 模块概览

| 模块 | 技术 | 说明 |
| --- | --- | --- |
| **mtcg-server** | Java 17 / Spring Boot 3.5 / MyBatis-Flex / PostgreSQL 16 | 后端服务 |
| **mtcg-client** | Vue 3 / PixiJS / Pinia / Electron / Capacitor | 前端 Monorepo（PC + 移动端游戏客户端） |

---

## 功能模块

| 模块 | 说明 | 状态 |
| --- | --- | --- |
| 卡牌数据管理 | 角色卡 / 冲击卡 / 产品包 CRUD + 条件查询 + 批量导入 | 🚧 开发中 |
| 用户系统 | 注册 / 登录 / JWT 鉴权 / RBAC 权限 | 📋 设计完成 |
| 卡组构筑 | 卡组创建 / 校验 / 收藏管理 / 导入导出 | 📋 设计完成 |
| 对战引擎 | 回合流程 / 战斗系统 / 效果系统 / 关键词能力 | 📋 设计完成 |
| 对战接口 | 创建对局 / 执行操作 / 复盘回放 / 持久化 | 📋 设计完成 |
| AI 对战 | 启发式策略 / 局面评估 / 难度分级 | 📋 设计完成 |
| 排位系统 | 段位 / 匹配 / 排行榜 / 赛季 | 📋 设计完成 |
| 系统管理 | 用户管理 / 配置 / 审计日志 | 📋 设计完成 |

---

## 开发路线

| 迭代 | 范围 | 状态 |
| --- | --- | --- |
| 迭代一 | 卡牌数据落地 | 🚧 开发中 |
| 迭代二 | 用户系统 + 权限管理 | 📋 待开始 |
| 迭代三 | 卡组构筑 + 卡牌收藏 | 📋 待开始 |
| 迭代四 | 引擎核心：状态模型 + 回合流程 | 📋 待开始 |
| 迭代五 | 引擎核心：行动 + 战斗 | 📋 待开始 |
| 迭代六 | 效果系统 + 关键词能力 | 📋 待开始 |
| 迭代七 | 对战 API + 对局持久化 | 📋 待开始 |
| 迭代八 | AI 对战 | 📋 待开始 |
| 迭代九 | 排位系统 + 打牌习惯分析 | 📋 待开始 |
| 迭代十 | 系统管理 | 📋 待开始 |

---

## 快速导航

- [后端 README](./mtcg-server/README.md) - 后端详情 + 快速启动
- [前端 Monorepo](./mtcg-client/) - 游戏客户端（PC + 移动端）
- [文档导航](./docs/README.md) - 全局文档索引

---

## 文档体系

### 全局文档

| 文档 | 说明 |
| --- | --- |
| [文档导航](./docs/README.md) | 全局文档索引 |
| [规则文档](./docs/规则文档/) | 游戏规则书（所有模块共用） |
| [设计文档](./docs/设计文档/) | 详细设计（需求 + 概要 + 迭代一~十） |
| [编码规范](./docs/编码规范/) | 编码规范（所有模块通用） |

---

## 快速启动（后端）

### 环境要求

- JDK 17+
- PostgreSQL 16+
- Maven 3.9+

### 数据库准备

```sql
CREATE DATABASE db_mtcg;
```

### 运行

```bash
cd mtcg-server
./mvnw spring-boot:run
```

### 验证

- 健康检查：`GET http://localhost:8081/api/health`
- Swagger UI：`http://localhost:8081/api/swagger-ui.html`

---

## 官方资源

- 官网：[marvelherorush.com](https://www.marvelherorush.com/cn/home)
- 综合规则书：[微信公众号](https://mp.weixin.qq.com/s/qWZcy5F4BK7_5rGf2KmWWQ)

---

<div align="center">

**该项目仅用于学习交流，不涉及任何商业用途。  
Marvel ©2026 MARVEL. Hero Rush © Card Fun.**

</div>