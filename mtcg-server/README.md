<div align="center">

<img src="../assets/banner-2.png" alt="漫威对战卡牌·超英击战" width="380">

# 超英击战 · MTCG 后端

**《超英击战》TCG 规则引擎与在线对战平台 - 后端服务**

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-green.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)
[![MyBatis-Flex](https://img.shields.io/badge/MyBatis--Flex-1.9-red.svg)](https://mybatis-flex.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

</div>

---

## 技术栈

| 层级 | 技术 | 说明 |
| --- | --- | --- |
| 语言 | Java 17 | LTS 版本 |
| 框架 | Spring Boot 3.5 | Web 框架 |
| ORM | MyBatis-Flex 1.9 | 数据访问层 |
| 数据库 | PostgreSQL 16 | OLTP 事务 + JSONB + 全文检索 |
| API 文档 | SpringDoc 2.8 | OpenAPI 3 / Swagger UI |
| 工具 | Lombok / Fastjson2 / Commons Lang3 | |
| AI（未来） | Spring AI | AI 对战与智能辅助 |

---

## 后端包结构

```
com.aris.mtcg
├── controller          # API 接口层（@RestController）
├── service             # Service 业务层
│   └── impl            # Service 实现
├── manager             # Manager 通用能力层（AI、缓存）
├── engine              # 规则引擎层（纯 POJO，不依赖 Spring）
│   ├── model           # 状态模型
│   ├── phase           # 回合阶段
│   ├── action          # 操作处理
│   ├── combat          # 战斗系统
│   ├── effect          # 效果系统
│   ├── keyword         # 关键词能力
│   └── rule            # 规则常量与校验
├── dao                 # 数据访问层（BaseMapper）
├── domain              # 领域模型
│   ├── entity          # DO 数据库映射
│   ├── dto             # DTO 入参
│   ├── vo              # VO 出参
│   └── query           # 查询对象
├── common              # 公共模块
│   ├── enums           # 枚举（Enum 前缀命名）
│   ├── result          # 统一响应体
│   ├── exception       # 业务异常
│   └── constant        # 常量
├── advice              # 全局异常处理
└── config              # Spring 配置
```

---

## 环境要求

- JDK 17+
- PostgreSQL 16+
- Maven 3.9+

---

## 数据库准备

```sql
CREATE DATABASE db_mtcg;
```

---

## 运行

```bash
# 克隆仓库
git clone https://github.com/你的用户名/mtcg.git
cd mtcg/mtcg-server

# 启动应用
./mvnw spring-boot:run
```

---

## 验证

- 健康检查：`GET http://localhost:8081/api/health`
- Swagger UI：`http://localhost:8081/api/swagger-ui.html`

---

## 文档体系

### 全局文档

| 文档 | 说明 |
| --- | --- |
| [文档导航](../docs/README.md) | 全局文档索引 |
| [规则文档](../docs/规则文档/) | 游戏规则书（所有模块共用） |
| [设计文档](../docs/设计文档/) | 详细设计（需求 + 概要 + 迭代一~十） |
| [编码规范](../docs/编码规范/) | 编码规范（所有模块通用） |

### 后端专属文档

| 文档 | 说明 |
| --- | --- |
| [后端文档导航](./docs/README.md) | 后端技术方案研究（架构演进、性能优化、算法研究等） |
