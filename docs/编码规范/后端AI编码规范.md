# MTCG 项目 AI 编码规范（后端）

> 本文件是 AI 辅助后端开发的强制约束。每次编码前必须通读本文档。
> 前端规范见 [前端AI编码规范.md](./前端AI编码规范.md)。

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
- 如果 50 行够，不要写 200 行

### 1.3 外科手术式改动

- 只改必须改的地方，不顺手优化相邻代码
- 不重构没有问题的部分
- 保持与现有风格一致
- 每一行变更都应能直接追溯到用户需求

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

| 层 | 职责 |
| --- | --- |
| Controller | 接收请求、参数校验、调用 Service、包装 Result |
| Service | 业务编排、对象转换、异常抛出 |
| Manager | 通用能力封装（AI、缓存、第三方） |
| Engine | 纯规则逻辑（回合、战斗、效果） |
| DAO | 数据库操作 |

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

> **强制要求**：代码必须通过 Spotless 格式化检查，保持统一的代码风格。

### 3.1 API 方法规范

**原则：所有 API 只使用 GET 和 POST 两种 HTTP 方法，通过路径区分操作类型。**

| 操作 | HTTP | 路径格式 | 示例 |
| --- | --- | --- | --- |
| 查询列表 | GET | `/xxx` | `GET /admin/cards` |
| 查询详情 | GET | `/xxx/{id}` | `GET /admin/cards/{id}` |
| 新增 | POST | `/xxx` | `POST /admin/cards` |
| 更新 | POST | `/xxx/{id}` | `POST /admin/cards/{id}` |
| 删除 | POST | `/xxx/{id}/delete` | `POST /admin/cards/{id}/delete` |
| 单一操作 | POST | `/xxx/{id}/action` | `POST /admin/users/{id}/status` |

```java
// ✅ 正确示例
@PostMapping("/{id}")
public Result<Void> update(@PathVariable Long id) { ... }

@PostMapping("/{id}/delete")
public Result<Void> delete(@PathVariable Long id) { ... }

// ❌ 错误示例（禁止使用 PUT、DELETE、PATCH）
@PutMapping("/{id}")
@DeleteMapping("/{id}")
@PatchMapping("/{id}/status")
```

### 3.2 命名规范

- 包名统一小写：`com.aris.mtcg.service`
- 类名、接口名、枚举名使用 UpperCamelCase
- 方法名、参数名、成员变量、局部变量使用 lowerCamelCase
- 枚举以 `Enum` 开头，异常类以 `Exception` 结尾

### 3.3 编码格式

- 缩进采用 4 个空格，禁止使用 Tab
- 大括号 `{}` 必须使用，即使只有一行代码
- 二元运算符两边必须加空格：`=` / `+` / `-` / `*` / `/` / `%` / `&&` / `||` / `<` / `>` / `==`
- 左括号后和右括号前加空格：`if (user != null)`
- 常量全部大写，单词间用下划线隔开：`MAX_RETRY_COUNT`

### 3.4 枚举

统一采用 `code` + `desc` 模式，带 `Enum` 前缀：

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

### 3.5 实体类（DO）

```java
@Data
@Table("mtcg_card")
public class CardDO {
    @Id(keyType = KeyType.Auto)
    private Long id;
}
```

- 用 `@Data` + `@Table`，主键用 `@Id(keyType = KeyType.Auto)`
- 枚举字段在 DO 中用 `String` 类型，Service 层做转换

### 3.6 实体转换规范

**任何需要相互转换的实体（DO、DTO、VO、BO 等），统一使用静态工厂方法。**

```java
@Data
public class CardVO {
    private Long id;
    private String cardCode;

    // === 静态工厂方法：转换为本类 ===

    /**
     * 从 DO 转换为本类
     */
    public static CardVO fromDO(CardDO card) {
        if (card == null) {
            return null;
        }
        CardVO vo = new CardVO();
        vo.setId(card.getId());
        vo.setCardCode(card.getCardCode());
        // ...
        return vo;
    }

    /**
     * 从 DTO 转换为本类
     */
    public static CardVO fromDTO(CardCreateDTO dto) {
        if (dto == null) {
            return null;
        }
        CardVO vo = new CardVO();
        vo.setCardName(dto.getCardName());
        // ...
        return vo;
    }

    // === toXxx 方法：转换为他类 ===

    /**
     * 转换为本类的 DO 对象
     */
    public static CardDO toDO(CardVO vo) {
        if (vo == null) {
            return null;
        }
        CardDO card = new CardDO();
        card.setId(vo.getId());
        card.setCardCode(vo.getCardCode());
        // ...
        return card;
    }

    /**
     * 转换为本类的 DTO 对象
     */
    public static CardCreateDTO toDTO(CardVO vo) {
        // ...
    }
}
```

```java
// ✅ 使用方式
CardVO vo = CardVO.fromDO(card);
CardVO vo = CardVO.fromDTO(dto);
CardDO card = CardVO.toDO(vo);
```

**命名规则：**
- `fromXxx`：静态方法，参数为 Xxx，转换为本类
- `toXxx`：静态方法，本类为参数，转换为 Xxx

- **DTO**：入参，带 `@NotBlank` / `@NotNull` 校验注解
- **VO**：出参，面向前端展示
- DO 和 VO/DTO 不可混用，Service 层负责转换

### 3.7 魔法值禁止

不允许任何魔法值直接出现在代码中：

```java
// ✅
private static final int MAX_RETRY_COUNT = 3;
if (retryCount > MAX_RETRY_COUNT) {
    throw new BusinessException("重试次数超限");
}

// ❌
if (retryCount > 3) { ... }
```

### 3.8 异常处理

- 不要捕获大的异常类（如 `Exception`），应捕获具体异常
- 不要用 `System.out.println`，应使用日志框架
- 异常信息必须包含排查相关信息

---

## 4. 日志规范

### 4.1 日志级别使用场景

| 级别 | 使用场景 |
| --- | --- |
| `log.debug` | 开发调试信息，线上默认关闭 |
| `log.info` | 业务关键节点（开始/成功/完成），如"用户登录成功"、"订单创建" |
| `log.warn` | 可恢复的异常、潜在风险，如"库存不足，使用默认值" |
| `log.error` | 系统异常、不可恢复的错误，如"数据库连接失败" |

### 4.2 日志规范

- 日志信息使用 `{}` 占位符，禁止字符串拼接：`log.info("userId={}", userId)`
- 日志中包含关键业务参数，便于排查
- 避免在循环中打印日志
- 敏感信息（密码、Token）禁止打印

```java
// ✅
log.info("创建卡牌 cardId={}, cardType={}", cardId, cardType);

// ❌
log.info("创建卡牌" + cardId);
log.error("出错了" + e.getMessage());
```

---

## 5. 事务规范

### 5.1 默认行为

`@Transactional` 默认：
- 只对 `RuntimeException` 和 `Error` 自动回滚
- 对 `checked exception` 不会自动回滚
- 传播行为默认为 `REQUIRED`

### 5.2 常见场景

| 场景 | 配置 |
| --- | --- |
| 默认业务方法 | `@Transactional` |
| 只读查询 | `@Transactional(readOnly = true)` |
| 需捕获异常不回滚 | `@Transactional(rollbackFor = Exception.class)` |
| 跨类调用事务生效 | 配置 `proxyTargetClass = true` |

### 5.3 事务边界

- 事务应控制在 Service 层，Controller 不加事务
- 避免在事务方法中调用外部接口（可能导致事务超时）
- 大批量操作注意事务时长，可用编程式事务拆分

---

## 6. 分页查询规范

### 6.1 统一分页方式

使用 MyBatis-Flex 的 `Page` 类进行分页：

```java
// Controller
@GetMapping("/cards")
public Result<Page<CardVO>> list(CardPageDTO dto) {
    return Result.success(cardService.page(dto));
}

// Service
public Page<CardVO> page(CardPageDTO dto) {
    Page.of(dto.getPageNum(), dto.getPageSize());
    List<CardDO> list = cardMapper.selectByPage(queryWrapper);
    Page<CardVO> result = Page.of(dto.getPageNum(), dto.getPageSize());
    result.setTotal(total); // 设置总数
    result.setRecords(ConvertUtil.copyList(list, CardVO.class));
    return result;
}
```

### 6.2 分页参数

| 参数 | 说明 | 默认值 |
| --- | --- | --- |
| `pageNum` | 页码 | 1 |
| `pageSize` | 每页条数 | 10 |

---

## 7. 数据库规范

- 不追求三大范式，允许合理冗余（如 card 表冗余 product_code）
- 不用数据库外键，关系完整性在应用层校验
- 枚举字段用 VARCHAR + CHECK 约束
- 表名小写，字段名下划线命名（MyBatis-Flex 自动驼峰转换）
- 每张表必须有 `create_time` 和 `update_time`

---

## 8. 引擎层特殊规范

- `engine` 包是纯 POJO，**零 Spring 依赖**
- 不用 `@Component` / `@Service` / `@Autowired`
- 通过 `new` 或工厂方法创建
- 枚举为纯 Java 枚举，不依赖 common 包
- 可独立单元测试，无需启动 Spring 容器
- 规则条款编号在代码注释中标注

---

## 9. 注释规范

- 类/方法写一两句中文：做什么、边界
- 不写长 Javadoc，少用 `{@code}` / `{@link}`
- 规则引擎代码在注释中标注规则条款编号（如 `// 303.2.a.4.3.1`）

---

## 10. 方法拆分原则

- 默认一段业务流程写在一个方法里，从上到下读通
- 不为「整洁」拆成多个 private 方法
- 仅在以下情况才拆：
  - 同一逻辑多处复用
  - 单方法明显过长（>80 行）且拆后每段自成一体
  - 层次边界要求（如 DAO/Service/Controller 职责分离）

---

## 参考文档

| 文档 | 说明 |
| --- | --- |
| [综合规则书 v1.01](../规则文档/02-综合规则书-v1.01.md) | 裁定基准 |
| [术语表与关键词速查](../规则文档/03-术语表与关键词速查.md) | 引擎枚举映射 |
| [区域模型与回合流程](../规则文档/04-区域模型与回合流程.md) | 代码建模参考 |
