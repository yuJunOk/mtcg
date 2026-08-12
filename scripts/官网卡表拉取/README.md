# 官网卡表拉取

> 完整操作手册见：[卡牌素材补充指南](../../docs/设计文档/卡牌素材补充指南.md)

从 [超英击战官方卡表](https://www.marvelherorush.com/cn/cards) 拉取元数据与卡图，生成 JSON 与幂等 SQL。

## 输出

| 路径 | 说明 |
|------|------|
| `assets/card/official/{系列}/{编号}-{罕度}.png` | 卡图（**1488×2080**，已去透明边） |

下载后会自动调用 `scripts/card_common` 做规范化，与截图提取规格一致。

若只需规范化已有图（不去重新下载）：

```bash
python scripts/card_common/normalize_cli.py assets/card/official
```

官网卡表目前可能缺少冲击卡/推广包，可用 `scripts/卡面提取` 截图补齐后放入同目录。
| `scripts/官网卡表拉取/out/cards.json` | 卡牌 JSON |
| `scripts/官网卡表拉取/out/products.json` | 产品 JSON |
| `scripts/官网卡表拉取/out/seed-official-cards.sql` | SQL 副本 |
| `mtcg-server/src/main/resources/sql/seed-official-cards.sql` | 正式种子 SQL |

`card_code` / 文件名格式：`BP01-001-MR`（编号 + 罕度，每条罕度各一行）。

## 运行

在仓库根目录：

```bash
# 全量（数据 + 图片；完好图跳过，残缺图自动重下）
python scripts/官网卡表拉取/fetch_cards.py

# 仅 JSON/SQL
python scripts/官网卡表拉取/fetch_cards.py --skip-images

# 强制重下全部卡图
python scripts/官网卡表拉取/fetch_cards.py --force-images

# 调整并发（默认 3；分片下载后可适当提高到 4）
python scripts/官网卡表拉取/fetch_cards.py --workers 3
```

Windows 若 `python` 不在 PATH，可用本机 Python 绝对路径。

下载策略：
- 用 `Range` **512KB 分片**拉取（CDN 支持 `Accept-Ranges: bytes`），规避整文件断流
- 校验 `Content-Length` / PNG 魔数 / `IEND` /（可选）Pillow 解码
- 本地完好图跳过，残缺或缺失自动重下

## API

- Base: `https://server.marvelherorush.com`
- `GET /marvel/product/list`
- `GET /marvel/card/list`
- 图片 CDN: `https://marvel-tcg.janime.cn/...`（签名 URL，需紧跟列表请求下载）

## 注意

- 卡图仅供本地开发，勿公开分发
- 官网系列字段可能带尾空格（如 `SD01 `），脚本已 `strip`
- 当前官网卡表仅有角色卡，无冲击卡
