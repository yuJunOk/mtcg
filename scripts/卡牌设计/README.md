# MTCG 卡牌设计

生成 MTCG 游戏用的卡背和先后卡卡面（747×1042, 圆角 R41, 透明 PNG）。

## 生成内容

| 文件 | 类型 | 主色调 | 说明 |
|------|------|--------|------|
| card_back_rush.png | 计分卡卡背 | 白+金+红 | RP 圆形徽章 + 9 点计分刻度 |
| card_back_character.png | 角色卡卡背 | 黑+红 | C 圆环徽章 + 爆炸光芒背景 |
| card_back_order.png | 先后卡卡背 | 深红+金 | 盾牌双箭头徽章 |
| order_first.png | 先后卡卡面 | 亮红+白 | 巨大数字 1 + FIRST |
| order_second.png | 先后卡卡面 | 黑红+红 | 巨大数字 2 + SECOND |

## 使用方式

```bash
python generate_card_designs.py
```

输出到 `assets/card/designs/`。

## 设计原则

参考宝可梦/MTG 卡背设计：
- MTCG logo 小号置顶（占高度约 8%，不抢主视觉）
- 中心主视觉符号占 40-50%（徽章/数字）
- 严格对称布局，大量留白
- 品牌色统一，底部小字版权

## 素材

`tex/` 目录下 3 张 AI 生成的装饰纹理：
- `_tex_halftone.png` - 红色半色调网点
- `_tex_burst.png` - 美漫爆炸光芒
- `_tex_diamond.png` - 金色钻石几何纹理

## 依赖

- Python 3 + Pillow
- Windows 系统字体：Impact / Arial Bold / SimHei
