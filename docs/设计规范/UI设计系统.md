# MTCG UI 设计系统

> 本文件是 MTCG 前端 UI 的唯一设计标准。所有页面生成必须严格遵循本文档，确保跨工具、跨模型的一致性。
>
> **设计理念**：暗色基调 + 卡片优先 + 清爽大气 + 漫威质感（仅限游戏端）
>
> **主题策略**：游戏端支持暗色（默认）/ 亮色双主题切换，管理后台使用 Element Plus 默认白色主题。

---

## 1. 设计理念

### 1.1 核心原则

| 原则 | 说明 |
| --- | --- |
| **卡片优先** | UI 服务于卡牌展示，卡牌始终是视觉焦点，UI 元素不抢夺注意力 |
| **暗色钢琴玻璃** | 深色背景 + 半透明玻璃质感面板，参考 Marvel Snap 的 "dark piano glass" |
| **舒适耐看** | 避免大面积红色，UI 主色用金/蓝，红色仅用于战斗语义（攻击/HP/危险），长时间游戏不疲劳 |
| **清爽克制** | 每屏核心信息控制在 5-9 项，减少认知负担，大面积留白 |
| **性能优先** | 优先 CSS 变量实现主题，避免运行时计算；动画用 transform/opacity |

### 1.2 适用场景

| 场景 | 渲染方式 | 主题 | 样式来源 |
| --- | --- | --- | --- |
| 管理后台（admin-web） | Vue 3 + Element Plus | Element Plus 默认白色 | 无额外样式，使用 Element Plus 原生 |
| 游戏客户端（game-pc / game-mobile） | Vue 3 外壳 + PixiJS 画布 | 暗色（默认）/ 亮色，用户可切换 | `theme-base.css` + `theme-dark.css` / `theme-light.css` + Pinia themeStore |

---

## 2. 色彩系统

> 游戏端支持暗色 / 亮色双主题，通过 `data-theme="dark"|"light"` 属性切换 CSS 变量。

### 2.1 暗色主题（默认）

| 变量 | 色值 | 说明 |
| --- | --- | --- |
| `--bg-base` | `#1B1E2B` | 暗蓝灰底，比纯黑柔和 |
| `--bg-surface` | `#232738` | 卡片/面板 |
| `--accent` | `#00D4AA` | 青绿魔法色，主强调 |
| `--accent-blue` | `#5C6BC0` | 靛蓝，辅助/信息 |
| `--accent-red` | `#E53935` | 仅战斗/危险 |

### 2.2 亮色主题

| 变量 | 色值 | 说明 |
| --- | --- | --- |
| `--bg-base` | `#F2F0EB` | 漫画纸米白 |
| `--bg-surface` | `#FFFFFF` | 卡片/面板 |
| `--accent` | `#E17055` | 珊瑚橙，温暖不刺眼 |
| `--accent-blue` | `#1565C0` | 美队盾蓝，辅助/信息 |
| `--accent-red` | `#D32F2F` | 仅战斗/危险 |

### 2.3 色彩使用规则

| 元素 | 使用色 | 示例 |
| --- | --- | --- |
| 页面背景 | `--bg-base` | 全局底色 |
| 卡片/面板 | `--bg-surface` | 表格、信息卡、侧边栏 |
| 弹窗 | `--bg-surface-3` | 模态框、下拉菜单 |
| 主按钮 | `--accent`（主题色） | 提交、确认、开始对战 |
| 次按钮 | `--bg-surface-2` | 取消、返回 |
| 导航选中 | `--accent` | 侧边栏/Tab 当前项 |
| 危险按钮 | `--accent-red` | 删除、重置、退出对战 |
| 成功状态 | `--accent-green` | 操作成功提示 |
| 警告状态 | `--accent` | 警告提示 |
| 攻击/HP/伤害 | `--accent-red` | 战斗界面中的攻击力、HP 条 |
| 普通文字 | `--text-primary` | 正文、标题 |
| 辅助文字 | `--text-secondary` | 描述、标签、时间戳 |
| 边框分割 | `--border` | 表格线、卡片边框 |
| 卡片悬浮 | `--bg-surface-2` + `--shadow-md` | hover 状态 |

> **红色使用禁令**：`--accent-red` 仅限战斗场景。常规 UI 导航、按钮、标签、统计数字等一律使用 `--accent`（主题色）或 `--accent-blue`。

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
| 危险按钮 | `--accent-red` | `#FFF` | 无 | `--radius-sm` | 32px |

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
| 游戏背景 | `0x1B1E2B`（暗）/ `0xF2F0EB`（亮） |
| 战区面板 | `0x232738`（暗）/ `0xFFFFFF`（亮） |
| 战区边框 | `0x2E3344`（暗）/ `0xE0DED8`（亮） |
| 高亮战区 | `0x00D4AA`（暗）/ `0xE17055`（亮） |
| 手牌背景 | `0x121212` |
| 卡牌文字 | `0xFFFFFF`（主）、`0x999999`（辅） |

### 8.2 卡牌渲染

- 卡牌尺寸：120×168px（PC），按比例缩放（移动端）
- 卡图用 `PIXI.Sprite` 加载，保持原始比例
- 卡牌选中：金色辉光边框（`--shadow-glow`）
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
- [ ] 主色使用 `--accent`（主题色），不自行定义主色；红色仅用于 `--accent-red`（战斗/危险）
- [ ] 圆角使用 `--radius-*` 变量
- [ ] 间距使用 `--space-*` 变量（4px 倍数）
- [ ] 按钮最小 32px 高度
- [ ] 阴影使用 `--shadow-*` 变量
- [ ] 动画时间 ≤ 400ms
- [ ] 管理后台使用 Element Plus 组件，非手写 UI
- [ ] 游戏客户端 PixiJS 色值与 CSS 变量保持一致
- [ ] 页面内容区撑满剩余高度（flex: 1）
- [ ] 分页/操作栏固定在底部

---

## 12. 参考来源

| 参考 | 链接 | 借鉴要点 |
| --- | --- | --- |
| TCG ONE | https://tcg.one/ | 功能模块卡片网格、Hero Banner + 统计数据、热门排行、Explore 卡片设计 |
| One Piece TCG Online | https://www.onepieceonlinetcg.com/play | 清爽蓝/海军色主题、3 列功能卡片、简洁克制配色、克制红色使用 |
| TCGSecret | https://tcgsecret.com/ | Hero CTA 双按钮、统计计数器、"How it works" 步骤引导、Coming Soon 占位、分类网格 |
| Marvel Snap | — | 暗色钢琴玻璃质感、卡片悬浮辉光效果、漫画半调网点元素 |
| PTCG Live | — | 系统预设头像方案、卡牌 3D 旋转动画、战区布局 |

### 12.1 色彩策略总结

游戏端支持暗色 / 亮色双主题，通过 `data-theme` 属性切换：
- **暗色（默认）**：暗蓝灰底 + 青绿魔法色，沉浸感强，长时间不疲劳
- **亮色**：漫画纸米白 + 珊瑚橙暖色，干净清爽，像翻漫画书
- 红色仅用于战斗/危险，两个主题均保持克制使用

### 12.2 主题切换实现

- 主题状态通过 Pinia `useThemeStore` 管理，持久化到 `localStorage`
- CSS 变量通过 `data-theme="dark"|"light"` 属性切换
- 文件结构：
  - `theme-base.css` — 重置 + 共享变量（圆角、间距、字体、过渡）
  - `theme-dark.css` — 暗色主题变量（`:root, [data-theme="dark"]`）
  - `theme-light.css` — 亮色主题变量（`[data-theme="light"]`）