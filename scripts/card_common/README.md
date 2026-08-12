# 卡面公共规格（card_common）

> 完整操作手册见：[卡牌素材补充指南](../../docs/设计文档/卡牌素材补充指南.md)

官网 CDN 拉取与截图提取 **共用同一套卡面规格**，避免 UI 拼卡时尺寸/透明边不一致。

## 统一规格

| 项 | 值 | 说明 |
|----|----|------|
| 画布 | **1488 × 2080** | 官网原图去透明边后约 1489×2080，取偶数 |
| 圆角 | 宽 × `41/747` ≈ **82px** | 与截图提取标定比例一致 |
| 格式 | RGBA PNG | 四角透明，无额外透明垫边 |

处理流水线：`去透明边 → 等比缩放居中贴画布 → 统一圆角`。

## 模块

```
scripts/card_common/
├── spec.py            # 尺寸/圆角常量
├── normalize.py       # trim / fit / round / normalize_*
├── normalize_cli.py   # 批量命令行
└── __init__.py
```

## 命令行

```bash
# 规范化官网已下载卡图（原地）
python scripts/card_common/normalize_cli.py assets/card/official

# 截图提取结果规范化到某系列目录（RP/推广包等）
python scripts/card_common/normalize_cli.py assets/card/extracted -o assets/card/official/PR01

# 强制重做
python scripts/card_common/normalize_cli.py assets/card/official --force
```

## 与两条流水线的关系

1. **官网卡表拉取** `scripts/官网卡表拉取/fetch_cards.py`  
   下载完成后自动 normalize；已是 1488×2080 的会跳过。

2. **截图卡面提取** `scripts/卡面提取/extract_card.py`  
   裁切后默认 normalize 到同一画布（可用 `--no-normalize` 关闭）。

推荐目录约定：

```
assets/card/
├── raw/                 # 截图原图（含 RP/推广包）
├── extracted/           # 截图提取中间结果（可选）
└── official/            # ★ 最终入 UI / SQL 的卡图
    ├── BP01/
    ├── SD01/
    ├── RP01/            # 冲击卡等（截图补齐）
    └── PR01/            # 推广包等
```

文件名继续用 `{编号}-{罕度}.png`，如 `BP01-001-MR.png`、`RP01-001-C.png`。
