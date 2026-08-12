# MTCG 卡面提取 / 截图补齐

> 完整手册：[卡牌素材补充指南](../../docs/设计文档/卡牌素材补充指南.md)

从 APP 截图批量提取卡面，写入与官网拉取 **相同的** `assets/card/faces/` 与幂等种子 SQL。

## 目录

```
scripts/卡面提取/
├── README.md
├── extract_card.py      # 底层裁切（也可单独用）
├── pipeline.py          # ★ 推荐：prepare → apply → sql
├── catalogs/            # 已填写 catalog 存档
├── run_extract.bat
└── fix_execution_policy.reg
```

共享：`scripts/card_common/`（规格 + `seed_sql.py`）

## 推荐流程

```bash
# 1) 裁切 + 编号徽章 + catalog 模板
python scripts/卡面提取/pipeline.py prepare assets/card/raw -o assets/card/extracted/batch1

# 2) 编辑 catalog.json：填 base_card_no / rarity；角色卡补 name/level/color/…
#    可参考 catalogs/2026-08-12.json

# 3) 写入 faces + 按产品生成 sql/seed-cards/*.sql
python scripts/卡面提取/pipeline.py apply assets/card/extracted/batch1/catalog.json

# 仅重生成 SQL
python scripts/卡面提取/pipeline.py sql scripts/卡面提取/catalogs/2026-08-12.json
```

## catalog 字段

| 字段 | 必填 | 说明 |
|------|------|------|
| `source` | 是 | 相对 `raw_dir` 的截图路径 |
| `base_card_no` | 是 | 如 `PB01-001` |
| `rarity` | 是 | 如 `PR` / `C` / `SEC` |
| `card_name` | 建议 | 卡名 |
| `card_type` | 建议 | `CHARACTER` / `RUSH_POINT` |
| `level` / `color` / `traits` / `attack_range` / `power` / `effect_text` | 角色卡建议 | 与官网 JSON 字段对齐 |

`products[]` 里声明新产品的 `product_code` / `product_name`。

## 仅裁切

```bash
python scripts/卡面提取/extract_card.py assets/card/raw -o assets/card/extracted
```

| 参数 | 说明 |
|------|------|
| `--left/top/right/bottom` | 微调裁切框（基准 1080×2354） |
| `--no-normalize` | 不统一到 1488×2080（不推荐） |

## 依赖

Python 3.8+、Pillow ≥ 8.2
