# 官网卡表拉取

> 完整手册：[卡牌素材补充指南](../../docs/设计文档/卡牌素材补充指南.md)

从 [超英击战官方卡表](https://www.marvelherorush.com/cn/cards) 拉取元数据与卡图，生成 JSON，并与截图补齐 **合并** 写入统一种子。

卡图与截图补齐写入 **同一目录/规格**；SQL 由共享模块 `scripts/card_common/seed_sql.py` 生成。

## 输出

| 路径 | 说明 |
|------|------|
| `assets/card/faces/{系列}/{编号}-{罕度}.png` | 卡图（1488×2080，不入库） |
| `scripts/官网卡表拉取/out/cards.json` | 卡牌 JSON |
| `scripts/官网卡表拉取/out/products.json` | 产品 JSON |
| `sql/seed-cards/{产品}.sql` | 按产品种子（含截图补齐） |
| `sql/seed-cards.sql` | 总种子（可直接全部执行） |

官网缺少的冲击卡/推广包等 → `scripts/卡面提取/pipeline.py`；仅重生成 SQL → `scripts/card_common/rebuild_seed_cli.py`。

## 运行

```bash
python scripts/官网卡表拉取/fetch_cards.py
python scripts/官网卡表拉取/fetch_cards.py --skip-images
python scripts/官网卡表拉取/fetch_cards.py --force-images
python scripts/官网卡表拉取/fetch_cards.py --workers 3
```

下载：Range 512KB 分片；校验 PNG；完好跳过；自动 normalize。

## API

- Base: `https://server.marvelherorush.com`
- `GET /marvel/product/list`、`GET /marvel/card/list`
- CDN: `https://marvel-tcg.janime.cn/...`

## 注意

- 卡图仅供本地开发
- 系列字段可能带尾空格，脚本已 `strip`
- 当前官网卡表通常仅有角色卡
