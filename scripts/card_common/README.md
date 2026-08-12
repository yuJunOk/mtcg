# 卡面公共模块（card_common）

> 完整手册：[卡牌素材补充指南](../../docs/设计文档/卡牌素材补充指南.md)

官网拉取与截图补齐 **共用**：

1. 卡面规格（1488×2080）
2. 幂等种子 SQL 生成（`seed_sql.py`）

## 模块

```
scripts/card_common/
├── spec.py              # 尺寸/圆角常量
├── normalize.py         # trim / fit / round
├── normalize_cli.py     # 批量规范化 CLI
├── seed_sql.py          # ★ INSERT 生成（按产品 + 总文件）
├── rebuild_seed_cli.py  # 合并官网 out/ + catalogs → seed-cards
└── __init__.py
```

## 规格

| 项 | 值 |
|----|----|
| 画布 | **1488 × 2080** |
| 圆角 | 宽 × `41/747` ≈ **82px** |
| 格式 | RGBA PNG |

## SQL

统一产出：

- `sql/seed-cards/{产品}.sql` — 按产品
- `sql/seed-cards.sql` — 总文件（拼接，可直接全部执行）

```bash
python scripts/card_common/rebuild_seed_cli.py
```

```python
from card_common.seed_sql import rebuild_merged_seed, write_seed_by_product

# 推荐：合并官网 JSON + 截图 catalog
rebuild_merged_seed()

# 或仅写入给定 products/cards
write_seed_by_product(products, cards, out_dir=..., index_path=...)
```

字段与库表一致：`card_code, product_code, card_name, card_type, level, color, …, image_path`。

## 规范化 CLI

```bash
python scripts/card_common/normalize_cli.py assets/card/faces
python scripts/card_common/normalize_cli.py assets/card/faces --force
```
