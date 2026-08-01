# MTCG 项目 AI 编码规范

> 本文件是 AI 辅助开发的强制约束。每次编码前必须通读本文档。

---

## 0. 语言规则

1. 所有解释、分析、总结使用中文
2. 代码保持原始编程语言
3. 命令行保持原始格式
4. API / 库名称保持英文
5. 代码注释使用中文

---

## 1. 开发心法

### 1.1 编码前先思考

- 不要臆测，不确定就提问
- 多种方案时全部列出，不默默替用户做选择
- 有更简单的方案要主动说明
- 不清楚的地方先停下，指出困惑点

### 1.2 简单优先

- 只写解决问题所需的最少代码
- 不做臆测性设计，不为一次性代码做抽象
- 不添加未被要求的功能或灵活性
- 不为不可能发生的场景写错误处理
- 如果 50 行够，不要写 200 行

### 1.3 外科手术式改动

- 只改必须改的地方，不顺手优化相邻代码
- 不重构没有问题的部分
- 保持与现有风格一致
- 每一行变更都应能直接追溯到用户需求
- 删除本次改动导致的未使用 import / 变量

### 1.4 目标驱动执行

- 先定义成功标准，再编码
- 多步骤任务先给出计划：`1. [步骤] → 验证：[检查项]`
- 完成后验证是否达到目标

---

## 2. 项目分层规范

### 2.1 包结构（com.aris.mtcg）

```
com.aris.mtcg
├── controller          // API 接口层（@RestController）
├── service             // Service 接口
│   └── impl            // Service 实现（@Service）
├── manager             // Manager 通用能力层（AI、缓存等）
├── engine              // 引擎层（纯 POJO，不依赖 Spring）
├── dao                 // DAO 层（@Mapper，继承 BaseMapper）
├── domain
│   ├── entity          // DO 数据库映射对象
│   ├── dto             // DTO 入参对象
│   ├── vo              // VO 出参对象
│   ├── bo              // BO 业务对象
│   └── query           // 查询对象
├── common
│   ├── enums           // 枚举
│   ├── result          // Result<T>, ErrorCode
│   ├── exception       // BusinessException
│   └── constant        // 常量
├── advice              // 全局异常处理（@RestControllerAdvice）
├── component           // 拦截器、切面等组件
└── config              // Spring 配置
```

### 2.2 各层职责

| 层 | 职责 | 禁止 |
| --- | --- | --- |
| Controller | 接收请求、参数校验、调用 Service、包装 Result | 不含业务逻辑、不直接调 DAO |
| Service | 业务编排、对象转换、异常抛出 | 不含 HTTP 协议处理 |
| Manager | 通用能力封装（AI、缓存、第三方） | 不含具体业务逻辑 |
| Engine | 纯规则逻辑（回合、战斗、效果） | 不依赖 Spring、不调 DAO |
| DAO | 数据库操作 | 不含业务逻辑 |

### 2.3 命名规范

| 类型 | 命名 | 示例 |
| --- | --- | --- |
| Controller | `XxxController` | `CardController` |
| Service 接口 | `XxxService` | `CardService` |
| Service 实现 | `XxxServiceImpl` | `CardServiceImpl` |
| Mapper | `XxxMapper` | `CardMapper` |
| DO | `XxxDO` | `CardDO` |
| DTO | `XxxDTO` | `CardCreateDTO` |
| VO | `XxxVO` | `CardVO` |
| 枚举 | `EnumXxx` | `EnumCardType` |
| 异常 | `XxxException` | `BusinessException` |

---

## 3. 编码规范

### 3.1 枚举

统一采用 `code` + `desc` 模式，带 `Enum` 后缀，不实现接口：

```java
@Getter
public enum EnumColor {
    RED("RED", "红"),
    YELLOW("YELLOW", "黄");

    private final String code;
    private final String desc;

    EnumColor(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
```

- DB 存 `code` 字符串（VARCHAR），不存下标
- DDL 加 CHECK 约束防止脏数据

### 3.2 实体类（DO）

```java
@Data
@Table("mtcg_card")
public class CardDO {
    @Id(keyType = KeyType.Auto)
    private Long id;
    // 字段与表列一一对应，驼峰命名
}
```

- 用 `@Data` + `@Table`
- 主键用 `@Id(keyType = KeyType.Auto)`
- 枚举字段在 DO 中用 `String` 类型，Service 层做转换

### 3.3 DTO 与 VO

- **DTO**：入参，带 `@NotBlank` / `@NotNull` 校验注解
- **VO**：出参，面向前端展示
- DO 和 VO/DTO 不可混用，Service 层负责转换

### 3.4 Service 层

- 接口和实现分离：`XxxService` + `XxxServiceImpl`
- 实现类用 `@Service` 注解
- 业务异常用 `BusinessException` 包装，带 `ErrorCode`
- DO ↔ DTO/VO 转换在 Service 层完成

### 3.5 Controller 层

- 用 `@RestController` + `@RequestMapping("/v1/xxx")`
- 统一返回 `Result<T>`
- 路径风格：RESTful（`GET /cards`、`POST /cards`、`PUT /cards/{id}`）
- 全局前缀 `/api` 由 `context-path` 配置

### 3.6 DAO 层

- 继承 `BaseMapper<XxxDO>`，用 `@Mapper` 注解
- 简单 CRUD 直接用 BaseMapper 方法
- 复杂查询用 `QueryWrapper` 或自定义 `@Select`

### 3.7 全局异常处理

- `@RestControllerAdvice` 放在 `advice` 包
- 捕获 `BusinessException` 返回对应 ErrorCode
- 捕获 `MethodArgumentNotValidException` 返回参数校验错误
- 捕获 `Exception` 返回 500 系统错误

---

## 4. 注释规范

- 类/方法写一两句中文：做什么、边界
- 不写长 Javadoc
- 少用 `{@code}` / `{@link}`
- 规则引擎代码在注释中标注规则条款编号（如 `// 303.2.a.4.3.1`）

---

## 5. 方法拆分原则

- 默认一段业务流程写在一个方法里，从上到下读通
- 不为「整洁」拆成多个 private 方法
- 仅在以下情况才拆：
  - 同一逻辑多处复用
  - 单方法明显过长（>80 行）且拆后每段自成一体
  - 层次边界要求（如 DAO/Service/Controller 职责分离）

---

## 6. 数据库规范

- 不追求三大范式，允许合理冗余（如 card 表冗余 product_code）
- 不用数据库外键，关系完整性在应用层校验
- 枚举字段用 VARCHAR + CHECK 约束
- 表名小写，字段名下划线命名（MyBatis-Flex 自动驼峰转换）
- 每张表必须有 `create_time` 和 `update_time`

---

## 7. 引擎层特殊规范

- `engine` 包是纯 POJO，**零 Spring 依赖**
- 不用 `@Component` / `@Service` / `@Autowired`
- 通过 `new` 或工厂方法创建
- 枚举为纯 Java 枚举，不依赖 common 包
- 可独立单元测试，无需启动 Spring 容器
- 规则条款编号在代码注释中标注

---

## 8. 前端规范

### 8.1 模块命名

| 模块 | 目录名 | 说明 |
| --- | --- | --- |
| 后端 | `mtcg-server` | Java + Spring Boot |
| 前端 Monorepo | `mtcg-client` | Vue 3 系列项目，含管理后台 + 游戏客户端 |
| 管理后台 | `mtcg-client/packages/admin-web` | Vue 3 + Element Plus，**禁止**写成 `mtcg-admin-web` / `mtcg-client-admin` / `mtcg-client-admin-web` |
| PC 游戏端 | `mtcg-client/packages/game-pc` | Vue 3 + PixiJS + Electron，**禁止**写成 `mtcg-pc` / `game-pc-web` |
| 移动游戏端 | `mtcg-client/packages/game-mobile` | Vue 3 + PixiJS + Capacitor，**禁止**写成 `mtcg-mobile` / `game-mobile-web` |
| 前端共享包 | `mtcg-client/packages/common` | TS 类型 / API 封装 / Pinia / PixiJS 基类，**禁止**写成 `shared` / `common-web` |

### 8.1.1 自有项目 vs 官方游戏命名区分（重要）

**自有文档中，MTCG 和官方游戏名称必须区分使用，禁止混用：**

| 上下文 | 使用名称 | 示例 |
| --- | --- | --- |
| 我们开发的项目/系统/代码 | `MTCG` | `MTCG 后端程序`、`MTCG_USER` 表、`MTCG_JWT_SECRET` |
| 官方游戏本身 | `《超英击战》` | `《超英击战》规则`、`《超英击战》卡牌游戏` |
| 官方规则书/文档 | 保留原名 | 标题 `《超英击战》综合规则书`、来源 `官方微信公众号` |
| 版权声明 | 保留原名 | `Hero Rush © Card Fun`、`Marvel ©2026 MARVEL` |
| 官方译名 | `Hero Rush TCG` | 仅在引用官方英文名时使用 |

**判断标准**：描述的是"我们做的系统"用 MTCG，描述的是"官方游戏/规则/概念"用《超英击战》或 Hero Rush TCG。

### 8.2 前端目录结构

```
src/
├── api/             // 接口请求（OpenAPI 生成 + 手写补充）
├── assets/          // 静态资源
├── components/      // 通用组件
├── layouts/         // 布局组件
├── router/          // 路由配置（含路由守卫）
├── stores/          // Pinia 状态管理
├── utils/           // 工具函数
├── views/           // 页面视图
└── main.ts
```

### 8.3 接口请求规范

- 使用 **OpenAPI Generator** 根据后端 Swagger 文档自动生成 API 请求方法，保证前后端接口契约一致
- 后端 Swagger JSON 地址：`http://localhost:8081/api/v3/api-docs`
- 生成的代码放在 `src/api/generated/`，**禁止手动修改**生成代码
- 手写补充接口放在 `src/api/` 对应模块文件中
- axios 实例封装在 `src/utils/request.ts`，处理 baseURL、token、统一错误拦截

### 8.4 枚举对齐规范

- 前端枚举必须与后端 `common/enums` 下的枚举**逐一对应**，不可随意增减
- 每个后端枚举类对应一个前端枚举文件，**不合并到同一个文件**：

| 后端 | 前端 | 说明 |
| --- | --- | --- |
| `EnumCardType.java` | `src/utils/enums/card-type.ts` | 卡牌类型 |
| `EnumColor.java` | `src/utils/enums/color.ts` | 颜色 |
| `EnumRarity.java` | `src/utils/enums/rarity.ts` | 稀有度 |
| `EnumProductType.java` | `src/utils/enums/product-type.ts` | 产品类型 |

- 格式统一为 `{ code, desc }` 数组，提供 `codeToDesc(options, code)` 工具函数

### 8.5 鉴权与路由保护

- 路由 `meta.requiresAuth` 标记是否需要登录
- 路由守卫检查 token，未登录跳转登录页
- 管理后台路由 `meta.requiresAdmin` 标记需要管理员权限
- axios 请求拦截器自动携带 `Authorization: Bearer <token>` 头
- 响应拦截器识别 401 自动跳转登录页

---

## 9. 实现步骤状态跟踪

每完成一个实现步骤，需更新实现步骤文档中对应步骤的状态标记：

| 标记 | 含义 |
| --- | --- |
| `🔲` | 未开始 |
| `🚧` | 进行中 |
| `✅` | 已完成 |

调整实现步骤时，**不得覆盖已完成步骤的状态**，只增改未完成部分。

---

## 10. 设计文档引用

编码前必须对照对应迭代的详细设计文档：

| 迭代 | 设计文档 |
| --- | --- |
| 迭代一 | [03-详细设计-迭代一](./设计文档/03-详细设计-迭代一-卡牌数据落地.md) |
| 迭代二 | [04-详细设计-迭代二](./设计文档/04-详细设计-迭代二-用户系统与权限管理.md) |
| 迭代三 | [05-详细设计-迭代三](./设计文档/05-详细设计-迭代三-卡组构筑与卡牌收藏.md) |
| 迭代四 | [06-详细设计-迭代四](./设计文档/06-详细设计-迭代四-引擎状态模型与回合流程.md) |
| 迭代五 | [07-详细设计-迭代五](./设计文档/07-详细设计-迭代五-引擎行动与战斗.md) |
| 迭代六 | [08-详细设计-迭代六](./设计文档/08-详细设计-迭代六-效果系统与关键词能力.md) |
| 迭代七 | [09-详细设计-迭代七](./设计文档/09-详细设计-迭代七-对战API与对局持久化.md) |
| 迭代八 | [10-详细设计-迭代八](./设计文档/10-详细设计-迭代八-AI对战.md) |
| 迭代九 | [11-详细设计-迭代九](./设计文档/11-详细设计-迭代九-排位系统与打牌习惯分析.md) |
| 迭代十 | [12-详细设计-迭代十](./设计文档/12-详细设计-迭代十-系统管理.md) |

实现步骤指南：
- [13-实现步骤-迭代一](./设计文档/13-实现步骤-迭代一-卡牌数据落地.md)

规则文档：
- [综合规则书 v1.01](./规则文档/02-综合规则书-v1.01.md)
- [术语表与关键词速查](./规则文档/03-术语表与关键词速查.md)
- [区域模型与回合流程](./规则文档/04-区域模型与回合流程.md)
