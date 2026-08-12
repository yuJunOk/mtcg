# MTCG 卡面提取工具

> 完整操作手册见：[卡牌素材补充指南](../../docs/设计文档/卡牌素材补充指南.md)

从 APP/其他平台截图中批量提取卡面，并规范化到与官网拉取 **相同的统一规格（1488×2080）**。

适用于官网卡表缺失的 **冲击卡（RP）**、**推广包** 等。

## 目录结构

```
scripts/卡面提取/
├── README.md
├── extract_card.py
├── run_extract.bat
└── fix_execution_policy.reg
```

共享规格见 [`scripts/card_common/`](../card_common/README.md)。

## 快速开始

### 1. 准备截图

```
assets/card/raw/
  SD04-时间/xxx.jpg
  RP01/xxx.jpg          # 冲击卡等
```

### 2. 提取（默认已规范化）

```bash
# 双击 run_extract.bat
# 或：
python scripts/卡面提取/extract_card.py assets/card/raw -o assets/card/extracted
```

输出为 **1488×2080** RGBA PNG（去边 + 统一圆角）。

### 3. 命名后放入 official

将提取结果按系列改名并放入：

```
assets/card/official/RP01/RP01-001-C.png
```

也可用公共 CLI：

```bash
python scripts/card_common/normalize_cli.py assets/card/extracted -o assets/card/official/RP01
```

## 参数

| 参数 | 说明 |
|------|------|
| `--left/top/right/bottom` | 微调截图裁切框（基准 1080×2354） |
| `--radius` | 裁切图上的圆角，会换算到目标宽度 |
| `--width/--height` | 目标画布（默认 1488×2080） |
| `--no-normalize` | 仅裁切+圆角，不统一尺寸（不推荐） |

## 依赖

- Python 3.8+
- Pillow >= 8.2.0
