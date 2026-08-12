# 官网卡表拉取

> 完整手册：[卡牌素材补充指南](../../docs/设计文档/卡牌素材补充指南.md)

从 [超英击战官方卡表](https://www.marvelherorush.com/cn/cards) 拉取元数据与卡图，生成 JSON 与幂等 SQL。

卡图与截图补齐写入 **同一目录/规格**；SQL 由共享模块 `scripts/card_common/seed_sql.py` 生成。

## 输出

| 路径 | 说明 |
|------|------|
| `assets/card/faces/{系列}/{编号}-{罕度}.png` | 卡图（1488×2080） |
| `scripts/官网卡表拉取/out/cards.json` | 卡牌 JSON |
| `scripts/官网卡表拉取/out/products.json` | 产品 JSON |
| `scripts/官网卡表拉取/out/seed-official-cards.sql` | SQL 副本 |
| `mtcg-server/src/main/resources/sql/seed-official-cards.sql` | 正式种子 |

官网缺少的冲击卡/推广包等 → 走 `scripts/卡面提取/pipeline.py`，种子写到 `sql/seed-cards/`。

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
