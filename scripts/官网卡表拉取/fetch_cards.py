#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
从官网 API 拉取卡表元数据与卡图，生成 JSON 与 PostgreSQL INSERT SQL。

API:
  - https://server.marvelherorush.com/marvel/product/list
  - https://server.marvelherorush.com/marvel/card/list
  - https://marvel-tcg.janime.cn/{image.key}?e=...&token=...

用法（在仓库根目录）:
  python scripts/官网卡表拉取/fetch_cards.py
  python scripts/官网卡表拉取/fetch_cards.py --skip-images   # 仅数据
  python scripts/官网卡表拉取/fetch_cards.py --workers 4
  python scripts/官网卡表拉取/fetch_cards.py --force-images  # 强制重下全部卡图
"""

from __future__ import annotations

import argparse
import http.client
import json
import re
import sys
import time
import urllib.error
import urllib.parse
import urllib.request
from concurrent.futures import ThreadPoolExecutor, as_completed
from pathlib import Path
from typing import Any

# 共享卡面规格
_SCRIPTS_DIR = Path(__file__).resolve().parents[1]
if str(_SCRIPTS_DIR) not in sys.path:
    sys.path.insert(0, str(_SCRIPTS_DIR))

from card_common.normalize import is_normalized, normalize_card_file  # noqa: E402
from card_common.seed_sql import (  # noqa: E402
    CARD_TYPE_MAP,
    COLOR_MAP,
    DEFAULT_SEED_ALL,
    DEFAULT_SEED_DIR,
    rebuild_merged_seed,
)
from card_common.spec import CARD_HEIGHT, CARD_WIDTH  # noqa: E402

API_BASE = "https://server.marvelherorush.com"
LANGUAGE = "zh-CN"
PAGE_SIZE = 100
USER_AGENT = "mtcg-official-card-fetcher/1.2"
PNG_MAGIC = b"\x89PNG\r\n\x1a\n"
PNG_IEND = b"IEND\xaeB`\x82"
# CDN 偶发断流；完整卡图通常 >1MB（1559x2150 PNG）
MIN_VALID_PNG_BYTES = 200_000

REPO_ROOT = Path(__file__).resolve().parents[2]
DEFAULT_IMAGE_DIR = REPO_ROOT / "assets" / "card" / "faces"
DEFAULT_OUT_DIR = Path(__file__).resolve().parent / "out"
DEFAULT_SQL_DIR = DEFAULT_SEED_DIR
DEFAULT_SQL_ALL = DEFAULT_SEED_ALL


def http_get_json(path: str, params: dict[str, Any]) -> dict[str, Any]:
    qs = urllib.parse.urlencode({k: v for k, v in params.items() if v is not None and v != ""})
    url = f"{API_BASE}{path}?{qs}"
    req = urllib.request.Request(
        url,
        headers={
            "Accept": "application/json",
            "Accept-Language": LANGUAGE,
            "User-Agent": USER_AGENT,
        },
    )
    with urllib.request.urlopen(req, timeout=60) as resp:
        raw = resp.read().decode("utf-8")
    return json.loads(raw)


def is_valid_png(path: Path, *, deep: bool = False) -> bool:
    """校验本地 PNG：魔数、IEND 结尾、最小体积；deep=True 时再完整解码。"""
    try:
        if not path.is_file():
            return False
        size = path.stat().st_size
        if size < MIN_VALID_PNG_BYTES:
            return False
        with open(path, "rb") as f:
            magic = f.read(8)
            if magic != PNG_MAGIC:
                return False
            f.seek(max(0, size - 12))
            tail = f.read()
        if not tail.endswith(PNG_IEND):
            return False
        if not deep:
            return True
        try:
            from PIL import Image

            with Image.open(path) as im:
                im.load()
                w, h = im.size
                if w < 100 or h < 100:
                    return False
        except ImportError:
            pass
        except Exception:  # noqa: BLE001 — 截断/损坏图
            return False
        return True
    except OSError:
        return False


def _probe_content_length(url: str) -> int | None:
    """用 Range: bytes=0-0 探测总大小（CDN 支持 206）。"""
    req = urllib.request.Request(
        url,
        headers={
            "User-Agent": USER_AGENT,
            "Range": "bytes=0-0",
            "Connection": "close",
        },
    )
    with urllib.request.urlopen(req, timeout=30) as resp:
        cr = resp.headers.get("Content-Range") or ""
        # Content-Range: bytes 0-0/4662793
        if "/" in cr:
            total = cr.rsplit("/", 1)[-1].strip()
            if total.isdigit():
                resp.read()
                return int(total)
        cl = resp.headers.get("Content-Length")
        resp.read()
        if cl and cl.isdigit():
            return int(cl)
    return None


def _download_range(url: str, start: int, end: int) -> bytes:
    """下载闭区间 [start, end] 字节。"""
    req = urllib.request.Request(
        url,
        headers={
            "User-Agent": USER_AGENT,
            "Accept": "image/png,image/*;q=0.8,*/*;q=0.5",
            "Range": f"bytes={start}-{end}",
            "Connection": "close",
        },
    )
    with urllib.request.urlopen(req, timeout=60) as resp:
        expected = end - start + 1
        body = resp.read(expected)
        if len(body) != expected:
            raise http.client.IncompleteRead(body, expected - len(body))
        return body


def http_download(url: str, dest: Path, retries: int = 6) -> None:
    """分片 Range 下载并校验；规避 CDN 整文件 IncompleteRead。"""
    dest.parent.mkdir(parents=True, exist_ok=True)
    tmp = dest.with_suffix(dest.suffix + ".part")
    last_err: Exception | None = None
    chunk_size = 256 * 1024  # 256KB，单片更快失败更快重试

    for attempt in range(1, retries + 1):
        try:
            if tmp.exists():
                tmp.unlink(missing_ok=True)

            total = _probe_content_length(url)
            if total is None or total < MIN_VALID_PNG_BYTES:
                # 回退：整文件下载
                req = urllib.request.Request(
                    url,
                    headers={
                        "User-Agent": USER_AGENT,
                        "Accept": "image/png,image/*;q=0.8,*/*;q=0.5",
                        "Connection": "close",
                    },
                )
                with urllib.request.urlopen(req, timeout=120) as resp:
                    cl = resp.headers.get("Content-Length")
                    expected = int(cl) if cl else None
                    body = resp.read() if expected is None else resp.read(expected)
                    if expected is not None and len(body) != expected:
                        raise http.client.IncompleteRead(body, expected - len(body))
                tmp.write_bytes(body)
            else:
                # 先在内存拼好再落盘，避免留下 0 字节 .part 误导
                chunks: list[bytes] = []
                offset = 0
                while offset < total:
                    end = min(offset + chunk_size - 1, total - 1)
                    piece: bytes | None = None
                    piece_err: Exception | None = None
                    for _ in range(6):
                        try:
                            piece = _download_range(url, offset, end)
                            break
                        except Exception as exc:  # noqa: BLE001
                            piece_err = exc
                            time.sleep(0.5)
                    if piece is None:
                        raise RuntimeError(f"分片 {offset}-{end} 失败: {piece_err}")
                    chunks.append(piece)
                    offset = end + 1
                tmp.write_bytes(b"".join(chunks))

            if not is_valid_png(tmp, deep=True):
                raise RuntimeError(f"PNG 校验失败 size={tmp.stat().st_size}")
            tmp.replace(dest)
            # 去透明边并统一到与截图提取相同的画布规格
            normalize_card_file(dest, dest)
            return
        except Exception as exc:  # noqa: BLE001 — 整轮重试
            last_err = exc
            if tmp.exists():
                tmp.unlink(missing_ok=True)
            time.sleep(min(1.2 * attempt, 8))
    raise RuntimeError(f"下载失败 {dest.name}: {last_err}")


def strip_text(value: Any) -> str:
    if value is None:
        return ""
    return str(value).replace("\r\n", "\n").strip()


def parse_level(raw: Any) -> int | None:
    text = strip_text(raw)
    if not text:
        return None
    m = re.search(r"(\d+)", text)
    return int(m.group(1)) if m else None


def parse_int(raw: Any) -> int | None:
    text = strip_text(raw)
    if not text:
        return None
    try:
        return int(text)
    except ValueError:
        return None


def normalize_product(raw: dict[str, Any]) -> dict[str, Any]:
    series = strip_text(raw.get("series"))
    name = strip_text(raw.get("name")) or series
    sale_ms = raw.get("sale_time")
    release_date = None
    if isinstance(sale_ms, (int, float)) and sale_ms > 0:
        # 毫秒时间戳 → YYYY-MM-DD（UTC）
        release_date = time.strftime("%Y-%m-%d", time.gmtime(sale_ms / 1000.0))
    return {
        "source_id": strip_text(raw.get("id")),
        "product_code": series,
        "product_name": name,
        "category": strip_text(raw.get("category")),
        "release_date": release_date,
        "description": None,
        "cover_image_key": (raw.get("cover_image") or {}).get("key"),
        "cover_image_url": (raw.get("cover_image") or {}).get("url"),
    }


def normalize_card(raw: dict[str, Any]) -> dict[str, Any]:
    card_no = strip_text(raw.get("card_no"))
    rarity = strip_text(raw.get("rarity")).upper()
    product_code = strip_text(raw.get("product_series"))
    card_code = f"{card_no}-{rarity}"
    attr = strip_text(raw.get("attribute"))
    card_type_raw = strip_text(raw.get("card_type")).lower()
    image = raw.get("image") or {}
    image_key = strip_text(image.get("key"))
    image_url = strip_text(image.get("url"))
    rel_path = f"card/faces/{product_code}/{card_code}.png"
    return {
        "source_id": strip_text(raw.get("id")),
        "card_code": card_code,
        "base_card_no": card_no,
        "product_code": product_code,
        "card_name": strip_text(raw.get("name")),
        "card_type": CARD_TYPE_MAP.get(card_type_raw, "CHARACTER"),
        "level": parse_level(raw.get("level")),
        "color": COLOR_MAP.get(attr),
        "color_raw": attr,
        "environment": strip_text(raw.get("environment")) or None,
        "traits": strip_text(raw.get("feature")) or None,
        "attack_range": parse_int(raw.get("attack_range")),
        "power": parse_int(raw.get("power")),
        "rarity": rarity,
        "effect_text": strip_text(raw.get("effect")) or None,
        "effect_json": None,
        "image_key": image_key,
        "image_url": image_url,
        "image_path": rel_path,
        "language": strip_text(raw.get("language")) or LANGUAGE,
    }


def fetch_products() -> list[dict[str, Any]]:
    data = http_get_json("/marvel/product/list", {"page": 1, "page_size": 50, "language": LANGUAGE})
    items = [normalize_product(x) for x in (data.get("list") or [])]
    # 稳定排序：BP 在前，再按 code
    items.sort(key=lambda p: (0 if p["product_code"].startswith("BP") else 1, p["product_code"]))
    return items


def fetch_all_cards() -> list[dict[str, Any]]:
    page = 1
    total = None
    cards: list[dict[str, Any]] = []
    while True:
        data = http_get_json(
            "/marvel/card/list",
            {"page": page, "page_size": PAGE_SIZE, "language": LANGUAGE},
        )
        batch = data.get("list") or []
        if total is None:
            total = int(data.get("total") or 0)
            print(f"[list] total={total}, page_size={PAGE_SIZE}")
        cards.extend(normalize_card(x) for x in batch)
        print(f"[list] page={page} got={len(batch)} accumulated={len(cards)}")
        if not batch:
            break
        if total is not None and len(cards) >= total:
            break
        page += 1
        if page > 50:
            raise RuntimeError("分页异常，超过 50 页")
    # 稳定排序
    cards.sort(key=lambda c: (c["product_code"], c["base_card_no"], c["rarity"]))
    return cards


def download_images(
    cards: list[dict[str, Any]],
    image_dir: Path,
    workers: int,
    force: bool = False,
) -> tuple[int, int, int, int]:
    """返回 (downloaded_ok, skipped, failed, normalized_only)。"""
    download_tasks: list[tuple[dict[str, Any], Path, str]] = []
    normalize_only: list[Path] = []
    skipped = 0
    corrupt = 0
    for card in cards:
        dest = image_dir / card["product_code"] / f"{card['card_code']}.png"
        if not force and is_normalized(dest):
            skipped += 1
            continue
        if not force and is_valid_png(dest):
            # CDN 原图完好但未去透明边 / 未统一尺寸 → 只规范化
            normalize_only.append(dest)
            continue
        if dest.exists() and not force:
            corrupt += 1
            dest.unlink(missing_ok=True)
        url = card.get("image_url")
        if not url:
            print(f"[img] 缺少 URL: {card['card_code']}")
            continue
        download_tasks.append((card, dest, url))

    ok = 0
    failed = 0
    norm_ok = 0

    if normalize_only:
        print(f"[img] 仅规范化（去透明边）={len(normalize_only)} 张 -> {CARD_WIDTH}x{CARD_HEIGHT}")
        for dest in normalize_only:
            try:
                normalize_card_file(dest, dest)
                norm_ok += 1
            except Exception as exc:  # noqa: BLE001
                failed += 1
                print(f"[img] NORM FAIL {dest.name}: {exc}")

    if not download_tasks:
        print(
            f"[img] 无需下载 skipped={skipped} normalized={norm_ok} "
            f"corrupt_fixed={corrupt}"
        )
        return ok, skipped, failed, norm_ok

    print(
        f"[img] 待下载={len(download_tasks)}, 完好跳过={skipped}, "
        f"损坏重下={corrupt}, force={force}, workers={workers}"
    )

    def _one(item: tuple[dict[str, Any], Path, str]) -> tuple[str, bool, str]:
        card, dest, url = item
        try:
            http_download(url, dest)
            return card["card_code"], True, ""
        except Exception as exc:  # noqa: BLE001
            return card["card_code"], False, str(exc)

    done = 0
    with ThreadPoolExecutor(max_workers=max(1, workers)) as pool:
        futures = [pool.submit(_one, t) for t in download_tasks]
        for fut in as_completed(futures):
            code, success, err = fut.result()
            done += 1
            if success:
                ok += 1
            else:
                failed += 1
                print(f"[img] FAIL {code}: {err}")
            if done % 10 == 0 or done == len(download_tasks):
                print(
                    f"[img] progress {done}/{len(download_tasks)} ok={ok} fail={failed}",
                    flush=True,
                )
    return ok, skipped, failed, norm_ok


def write_seed_sql(out_dir: Path, seed_dir: Path, seed_all: Path) -> list[tuple[str, int]]:
    """根据 out/ JSON + 截图 catalogs 合并写入 seed-cards。"""
    return rebuild_merged_seed(
        official_out=out_dir,
        out_dir=seed_dir,
        index_path=seed_all,
        title_prefix="卡牌/产品种子",
    )


def write_json(path: Path, data: Any) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")


def main() -> int:
    parser = argparse.ArgumentParser(description="拉取官网卡表资源与种子数据")
    parser.add_argument("--skip-images", action="store_true", help="不下图片，仅生成 JSON/SQL")
    parser.add_argument("--force-images", action="store_true", help="强制重下全部卡图（忽略本地已有）")
    parser.add_argument("--workers", type=int, default=3, help="图片下载并发数（CDN 易断流，默认 3）")
    parser.add_argument("--image-dir", type=Path, default=DEFAULT_IMAGE_DIR)
    parser.add_argument("--out-dir", type=Path, default=DEFAULT_OUT_DIR)
    parser.add_argument("--seed-dir", type=Path, default=DEFAULT_SEED_DIR)
    parser.add_argument("--seed-all", type=Path, default=DEFAULT_SEED_ALL)
    args = parser.parse_args()

    print(f"[init] repo={REPO_ROOT}")
    print(f"[init] image_dir={args.image_dir}")
    print(f"[init] out_dir={args.out_dir}")
    print(f"[init] seed_dir={args.seed_dir}")
    print(f"[init] seed_all={args.seed_all}")

    products = fetch_products()
    print(f"[product] count={len(products)} codes={[p['product_code'] for p in products]}")

    cards = fetch_all_cards()
    print(f"[card] count={len(cards)}")

    # 校验唯一 card_code
    codes = [c["card_code"] for c in cards]
    if len(codes) != len(set(codes)):
        dup = sorted({x for x in codes if codes.count(x) > 1})
        raise RuntimeError(f"card_code 重复: {dup[:20]}")

    meta = {
        "source": "https://www.marvelherorush.com/cn/cards",
        "api_base": API_BASE,
        "language": LANGUAGE,
        "fetched_at": time.strftime("%Y-%m-%dT%H:%M:%SZ", time.gmtime()),
        "product_count": len(products),
        "card_count": len(cards),
        "card_code_format": "{card_no}-{rarity}",
        "image_path_format": "card/faces/{product_code}/{card_code}.png",
        "card_canvas": f"{CARD_WIDTH}x{CARD_HEIGHT}",
    }

    args.out_dir.mkdir(parents=True, exist_ok=True)
    write_json(args.out_dir / "meta.json", meta)
    write_json(args.out_dir / "products.json", products)
    write_json(args.out_dir / "cards.json", cards)
    print(f"[json] wrote {args.out_dir / 'products.json'}")
    print(f"[json] wrote {args.out_dir / 'cards.json'}")

    written = write_seed_sql(args.out_dir, args.seed_dir, args.seed_all)
    total = sum(n for _, n in written)
    print(f"[sql] wrote {args.seed_all} ({len(written)} products / {total} cards)")
    for code, n in written:
        print(f"  - seed-cards/{code}.sql ({n})")

    if args.skip_images:
        print("[img] skipped")
        return 0

    ok, skipped, failed, normalized = download_images(
        cards, args.image_dir, args.workers, force=args.force_images
    )
    print(
        f"[img] done downloaded={ok} normalized={normalized} "
        f"skipped={skipped} failed={failed}"
    )

    # 最终确认：完好 PNG 且已是统一规格
    bad = [
        p.name
        for p in args.image_dir.rglob("*.png")
        if not is_valid_png(p) or not is_normalized(p)
    ]
    if bad:
        print(
            f"[warn] 仍有未规范化/无效 PNG {len(bad)} 张，可再跑一次: {bad[:10]}",
            file=sys.stderr,
        )
        return 2
    if failed:
        print("[warn] 有失败项，可重跑脚本（已规范化会跳过）", file=sys.stderr)
        return 2
    print(f"[img] 全部 PNG 校验通过（{CARD_WIDTH}x{CARD_HEIGHT}）")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
