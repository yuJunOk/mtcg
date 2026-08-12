# 卡面公共模块（card_common）

> 完整手册：[卡牌素材补充指南](../../docs/设计文档/卡牌素材补充指南.md)

官网拉取与截图补齐 **共用**：

1. 卡面规格（1488×2080）
2. 幂等种子 SQL 生成（`seed_sql.py`）

## 模块

```
scripts/card_common/
├── spec.py            # 尺寸/圆角常量
├── normalize.py       # trim / fit / round
├── normalize_cli.py   # 批量规范化 CLI
├── seed_sql.py        # ★ INSERT 生成（整包 / 按产品拆分）
└── __init__.py
```

## 规格

| 项 | 值 |
|----|----|
| 画布 | **1488 × 2080** |
| 圆角 | 宽 × `41/747` ≈ **82px** |
| 格式 | RGBA PNG |

## SQL

```python
from card_common.seed_sql import build_monolith_sql, write_seed_by_product

# 官网：整包 seed-official-cards.sql
sql = build_monolith_sql(products, cards, header_lines=[...])

# 截图补齐：sql/seed-cards/{产品}.sql + seed-cards.sql 入口
write_seed_by_product(products, cards, out_dir=..., index_path=...)
```

字段与库表一致：`card_code, product_code, card_name, card_type, level, color, …, image_path`。

## 规范化 CLI

```bash
python scripts/card_common/normalize_cli.py assets/card/faces
python scripts/card_common/normalize_cli.py assets/card/faces --force
```
