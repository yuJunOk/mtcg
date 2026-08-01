# MTCG 卡牌设计 v5

生成 MTCG 游戏用的卡背和先后卡卡面（747×1042, 圆角 R41, 透明 PNG）。

## 设计理念

**清爽大气** —— 每张卡背有独立色彩主题，金色为统一点缀色，大量留白，克制装饰，几何图案为主视觉。

## 生成内容

| 文件 | 类型 | 色调 | 核心视觉 |
|------|------|------|----------|
| card_back_rush.png | 计分卡卡背 | 深海蓝 + 金 | 同心圆刻度环 + RP 徽章 + 9 点计分线 |
| card_back_character.png | 角色卡卡背 | 曜石黑 + 金 | 四角金框饰 + C 徽章 + 爆炸光芒背景 |
| card_back_order.png | 先后卡卡背 | 勃艮第红 + 金 | 罗盘十字 + O 徽章 |
| order_first.png | 先手卡面 | 象牙白 + 暗金 | 巨大数字 1 + FIRST |
| order_second.png | 后手卡面 | 暗夜灰 + 银 | 巨大数字 2 + SECOND |

## 使用方式

```bash
python generate_card_designs.py
```

输出到 `assets/card/designs/`。

## 素材

`tex/` 目录下 3 张 AI 生成的装饰纹理（极低透明度叠加，营造氛围）：
- `_tex_halftone.png` - 红色半色调网点
- `_tex_burst.png` - 美漫爆炸光芒
- `_tex_diamond.png` - 金色钻石几何纹理

## 依赖

- Python 3 + Pillow + numpy
- Windows 系统字体：Impact / Arial Bold / SimHei