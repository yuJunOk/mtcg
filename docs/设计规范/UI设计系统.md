# MTCG UI 设计系统

> 本文件是 MTCG 前端 UI 的唯一设计标准。所有页面生成必须严格遵循本文档，确保跨工具、跨模型的一致性。
>
> **设计理念**：暗色基调 + 卡片优先 + 清爽大气 + 漫威漫画质感（仅限游戏端）
>
> **主题策略**：管理后台使用 Element Plus 默认白色主题，游戏端使用暗色主题。

---

## 1. 设计理念

### 1.1 核心原则

| 原则 | 说明 |
| --- | --- |
| **卡片优先** | UI 服务于卡牌展示，卡牌始终是视觉焦点，UI 元素不抢夺注意力 |
| **暗色钢琴玻璃** | 深色背景 + 半透明玻璃质感面板，参考 Marvel Snap 的 "dark piano glass" |
| **漫画未来感** | 致敬漫威漫画起源，融入半调网点、全息投影光感等微妙元素 |
| **清爽克制** | 每屏核心信息控制在 5-9 项，减少认知负担，大面积留白 |
| **性能优先** | 优先 CSS 变量实现主题，避免运行时计算；动画用 transform/opacity |

### 1.2 适用场景

| 场景 | 渲染方式 | 主题 | 样式来源 |
| --- | --- | --- | --- |
| 管理后台（admin-web） | Vue 3 + Element Plus | Element Plus 默认白色 | 无额外样式，使用 Element Plus 原生 |
| 游戏客户端（game-pc / game-mobile） | Vue 3 外壳 + PixiJS 画布 | 暗色主题 | `game-theme.css` CSS 变量 + PixiJS 内联样式 |
| 公共组件（common） | Vue 3 通用组件 | 跟随宿主 | 暗色主题 CSS 变量（`game-theme.css`） |

---

## 2. 色彩系统

### 2.1 基础色板

```
深色底色（由深到浅，用于 elevation 层级）：
  --color-bg-base:       #0D0D0D    ← 最底层背景
  --color-bg-elevated:   #121212    ← 主背景（Material Design 推荐值）
  --color-bg-surface:    #1A1A2E    ← 卡片/面板（带微蓝调）
  --color-bg-surface-2:  #24243A    ← 悬浮面板
  --color-bg-surface-3:  #2E2E4A    ← 弹窗/模态框
  --color-border:        #2A2A3E    ← 边框/分割线
  --color-border-light:  #3A3A50    ← 弱边框

文字色：
  --color-text-primary:   rgba(255,255,255,0.92)   ← 主文字
  --color-text-secondary: rgba(255,255,255,0.60)   ← 辅助文字
  --color-text-disabled:  rgba(255,255,255,0.38)   ← 禁用文字
  --color-text-link:      #6C9FFF                    ← 链接文字

漫威主题色：
  --color-marvel-red:     #E23636    ← 主强调色（能量/攻击/危险）
  --color-marvel-gold:    #F78F3F    ← 辅助强调色（稀有/胜利/高亮）
  --color-marvel-blue:    #518CCA    ← 信息色（防御/链接/冷静）
  --color-marvel-green:   #2ECC71    ← 成功色（回复/确认/安全）
  --color-marvel-purple:  #9B59B6    ← 特殊色（效果/魔力/稀有）
```

### 2.2 CSS 变量定义

```css
:root {
  /* 背景层级 */
  --bg-base:       #0D0D0D;
  --bg-elevated:   #121212;
  --bg-surface:    #1A1A2E;
  --bg-surface-2:  #24243A;
  --bg-surface-3:  #2E2E4A;

  /* 边框 */
  --border:        #2A2A3E;
  --border-light:  #3A3A50;

  /* 文字 */
  --text-primary:    rgba(255, 255, 255, 0.92);
  --text-secondary:  rgba(255, 255, 255, 0.60);
  --text-disabled:   rgba(255, 255, 255, 0.38);
  --text-link:       #6C9FFF;

  /* 主题色 */
  --accent:         #E23636;   /* 主强调 */
  --accent-gold:    #F78F3F;   /* 辅助强调 */
  --accent-blue:    #518CCA;   /* 信息 */
  --accent-green:   #2ECC71;   /* 成功 */
  --accent-purple:  #9B59B6;   /* 特殊 */

  /* 圆角 */
  --radius-sm:  4px;
  --radius-md:  8px;
  --radius-lg:  12px;
  --radius-xl:  16px;

  /* 阴影 */
  --shadow-sm:   0 2px 8px rgba(0, 0, 0, 0.4);
  --shadow-md:   0 4px 16px rgba(0, 0, 0, 0.5);
  --shadow-lg:   0 8px 32px rgba(0, 0, 0, 0.6);
  --shadow-glow: 0 0 20px rgba(226, 54, 54, 0.3);  /* 红色辉光 */

  /* 间距 */
  --space-xs:  4px;
  --space-sm:  8px;
  --space-md:  16px;
  --space-lg:  24px;
  --space-xl:  32px;
  --space-2xl: 48px;

  /* 字体 */
  --font-family: 'PingFang SC', 'Microsoft YaHei', 'Helvetica Neue', sans-serif;
  --font-size-xs:   12px;
  --font-size-sm:   13px;
  --font-size-base: 14px;
  --font-size-md:   16px;
  --font-size-lg:   20px;
  --font-size-xl:   24px;
  --font-size-2xl:  32px;

  /* 过渡 */
  --transition-fast: 150ms ease;
  --transition-base: 250ms ease;
  --transition-slow: 400ms ease;
}
```

### 2.3 色彩使用规则

| 元素 | 使用色 | 示例 |
| --- | --- | --- |
| 页面背景 | `--bg-base` | 全局底色 |
| 卡片/面板 | `--bg-surface` | 表格、信息卡、侧边栏 |
| 弹窗 | `--bg-surface-3` | 模态框、下拉菜单 |
| 主按钮 | `--accent`（红底白字） | 提交、确认、攻击 |
| 次按钮 | `--bg-surface-2`（暗底白字） | 取消、返回 |
| 危险按钮 | `--accent`（红底白字） | 删除、重置 |
| 成功状态 | `--accent-green` | 操作成功提示 |
| 警告状态 | `--accent-gold` | 警告提示 |
| 普通文字 | `--text-primary` | 正文、标题 |
| 辅助文字 | `--text-secondary` | 描述、标签、时间戳 |
| 边框分割 | `--border` | 表格线、卡片边框 |
| 卡片悬浮 | `--bg-surface-2` + `--shadow-md` | hover 状态 |

---

## 3. 字体排版

### 3.1 字体族

```
首选：PingFang SC（macOS/iOS）
备选：Microsoft YaHei（Windows）
回退：Helvetica Neue, Arial, sans-serif
等宽：Consolas, 'Courier New', monospace（代码/数据）
```

### 3.2 字体大小层级

| 层级 | 大小 | 行高 | 用途 |
| --- | --- | --- | --- |
| H1 | 32px | 1.3 | 页面主标题 |
| H2 | 24px | 1.3 | 区块标题 |
| H3 | 20px | 1.4 | 卡片标题 |
| H4 | 16px | 1.4 | 子标题 |
| Body | 14px | 1.6 | 正文 |
| Caption | 13px | 1.5 | 辅助说明 |
| Small | 12px | 1.5 | 标签、角标 |

### 3.3 字重

- 标题：`600`（semibold）
- 正文：`400`（normal）
- 强调：`500`（medium）
- 数字/数据：`600`（数字更醒目）

---

## 4. 间距系统

统一使用 4px 倍数：

```
--space-xs:  4px    ← 紧凑间距（图标与文字）
--space-sm:  8px    ← 小间距（表单行内间距）
--space-md:  16px   ← 标准间距（卡片内边距）
--space-lg:  24px   ← 大间距（段落间距、页面边距）
--space-xl:  32px   ← 区块间距
--space-2xl: 48px   ← 页面级间距（顶部导航与内容区）
```

### 4.1 页面内边距

- PC 端：`--space-lg`（24px）
- 移动端：`--space-md`（16px）

---

## 5. 圆角

```css
--radius-sm:  4px;   /* 标签、小按钮、输入框 */
--radius-md:  8px;   /* 卡片、面板、表格 */
--radius-lg:  12px;  /* 弹窗、大按钮 */
--radius-xl:  16px;  /* 大卡片、游戏区域 */
```

**规则**：同一组件内圆角保持一致，不混用。

---

## 6. 阴影与层级

暗色背景中，阴影主要靠**亮度差异**表现层级，而非传统阴影：

| 层级 | 背景色 | 阴影 | 使用场景 |
| --- | --- | --- | --- |
| 0（基底） | `--bg-base` | 无 | 页面背景 |
| 1（表面） | `--bg-surface` | `--shadow-sm` | 卡片、表格 |
| 2（悬浮） | `--bg-surface-2` | `--shadow-md` | hover 卡片、下拉菜单 |
| 3（弹窗） | `--bg-surface-3` | `--shadow-lg` | 模态框、对话框 |
| 4（辉光） | 任意 | `--shadow-glow` | 技能特效、焦点高亮 |

---

## 7. 组件设计规范

### 7.1 按钮

| 类型 | 背景 | 文字色 | 边框 | 圆角 | 高度 |
| --- | --- | --- | --- | --- | --- |
| 主按钮 | `--accent` | `#FFF` | 无 | `--radius-sm` | 32px |
| 次按钮 | `--bg-surface-2` | `--text-primary` | `--border` | `--radius-sm` | 32px |
| 文字按钮 | 透明 | `--text-link` | 无 | - | - |
| 危险按钮 | `--accent` | `#FFF` | 无 | `--radius-sm` | 32px |

- 最小可点击区域：32×32px（PC），44×44px（移动端）
- 按钮间距：`--space-sm`（8px）
- 悬停：亮度提升 10%
- 按下：缩放 0.97

### 7.2 卡片

```css
.card {
  background: var(--bg-surface);
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  padding: var(--space-md);
  transition: background var(--transition-fast);
}
.card:hover {
  background: var(--bg-surface-2);
  box-shadow: var(--shadow-md);
}
```

### 7.3 输入框

```css
.input {
  background: var(--bg-base);
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  color: var(--text-primary);
  height: 32px;
  padding: 0 var(--space-sm);
}
.input:focus {
  border-color: var(--accent);
  box-shadow: var(--shadow-glow);
}
```

### 7.4 表格（管理后台）

- 表头背景：`--bg-surface`
- 行背景：`--bg-elevated`
- 行 hover：`--bg-surface`
- 边框色：`--border`
- 分页器：置底、右对齐

### 7.5 弹窗（管理后台）

```css
.dialog {
  background: var(--bg-surface-3);
  border: 1px solid var(--border-light);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-lg);
}
.dialog-header {
  border-bottom: 1px solid var(--border);
  padding: var(--space-md) var(--space-lg);
}
.dialog-footer {
  border-top: 1px solid var(--border);
  padding: var(--space-md) var(--space-lg);
  text-align: right;
}
```

### 7.6 导航（管理后台）

- 侧边栏背景：`--bg-surface`
- 侧边栏宽度：220px（折叠后 64px）
- 菜单项高度：44px
- 选中态：`--accent` 背景，白色文字
- Logo 区域高度：56px

---

## 8. 游戏客户端专属规范

### 8.1 PixiJS 画布色彩

PixiJS 内渲染时，直接用 16 进制色值，与 CSS 变量保持一致：

| 元素 | 色值 |
| --- | --- |
| 游戏背景 | `0x0D0D0D` |
| 战区面板 | `0x1A1A2E` |
| 战区边框 | `0x2A2A3E` |
| 高亮战区 | `0xE23636`（辉光） |
| 手牌背景 | `0x121212` |
| 卡牌文字 | `0xFFFFFF`（主）、`0x999999`（辅） |

### 8.2 卡牌渲染

- 卡牌尺寸：120×168px（PC），按比例缩放（移动端）
- 卡图用 `PIXI.Sprite` 加载，保持原始比例
- 卡牌选中：红色辉光边框（`--shadow-glow`）
- 卡牌悬停：`scale: 1.05`，`zIndex` 提升

### 8.3 对战布局

**PC 端（横屏）**：
```
┌──────────────────────────────────┐
│  对手信息栏（头像、卡组数、时间线）│
├──────────────────────────────────┤
│  对手基地    [后][侧][侧][前]     │
│                                  │
│        时间线区域                │
│                                  │
│  我方基地    [后][侧][侧][前]     │
├──────────────────────────────────┤
│  我方手牌区（横排）  + 行动按钮   │
└──────────────────────────────────┘
```

**移动端（竖屏）**：上下布局更紧凑，手牌横滑，按钮右下方 FAB

### 8.4 动画规范

- 卡牌入场：从手牌区平滑移动到目标位置，250ms ease
- 攻击动画：卡牌前冲 → 碰撞 → 回弹，400ms
- 时间线推进：卡片翻转 3D 效果，400ms
- 粒子特效：使用 `@pixi/particle-emitter`，红色/金色粒子
- 过渡动画：始终用 `transform` + `opacity`，避免 `width/height` 动画

---

## 9. 管理后台样式（admin-web）

管理后台使用 Element Plus 默认白色主题，**不应用暗色设计系统**。样式规范参见 [前端 AI 编码规范 §6](../编码规范/前端AI编码规范.md#6-element-plus 使用规范)。

---

## 10. 响应式断点

| 断点 | 宽度 | 适用端 |
| --- | --- | --- |
| Mobile | < 768px | game-mobile |
| Tablet | 768px - 1024px | game-mobile 横屏 |
| Desktop | > 1024px | game-pc、admin-web |

---

## 11. AI 生成检查清单

每次生成页面时，AI 必须自查：

- [ ] 背景色使用 `--bg-base`（非纯黑 `#000`）
- [ ] 卡片/面板使用 `--bg-surface` 系列
- [ ] 文字使用 `--text-primary` / `--text-secondary`
- [ ] 主色使用 `--accent`（红），不自行定义主色
- [ ] 圆角使用 `--radius-*` 变量
- [ ] 间距使用 `--space-*` 变量（4px 倍数）
- [ ] 按钮最小 32px 高度
- [ ] 阴影使用 `--shadow-*` 变量
- [ ] 动画时间 ≤ 400ms
- [ ] 管理后台使用 Element Plus 组件，非手写 UI
- [ ] 游戏客户端 PixiJS 色值与 CSS 变量保持一致
- [ ] 页面内容区撑满剩余高度（flex: 1）
- [ ] 分页/操作栏固定在底部