#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
截图补齐流水线（与官网拉取共用规格 + 种子 SQL 格式）

子命令:
  prepare  从 raw 截图批量裁切卡面，并生成待填 catalog 模板
  apply    按 catalog.json 命名写入 faces/，并生成 seed-cards/*.sql
  sql      仅根据 catalog 重生成 SQL（不改图片）

示例（仓库根目录）:
  python scripts/卡面提取/pipeline.py prepare assets/card/raw -o assets/card/extracted/batch1
  # 编辑 assets/card/extracted/batch1/catalog.json 填编号/罕度/字段
  python scripts/卡面提取/pipeline.py apply assets/card/extracted/batch1/catalog.json
"""
from __future__ import annotations

import argparse
import json
import shutil
import sys
from pathlib import Path
from typing import Any

from PIL import Image

_SCRIPTS = Path(__file__).resolve().parents[1]
if str(_SCRIPTS) not in sys.path:
    sys.path.insert(0, str(_SCRIPTS))

from card_common.seed_sql import (  # noqa: E402
    ensure_card_code,
    ensure_product_code,
    image_rel_path,
    normalize_seed_card,
    rebuild_merged_seed,
)
from card_common.spec import CARD_HEIGHT, CARD_WIDTH  # noqa: E402

# 复用 extract_card
import importlib.util

_EXTRACT_PATH = Path(__file__).resolve().parent / "extract_card.py"
_spec = importlib.util.spec_from_file_location("extract_card", _EXTRACT_PATH)
assert _spec and _spec.loader
extract_card = importlib.util.module_from_spec(_spec)
_spec.loader.exec_module(extract_card)

REPO_ROOT = Path(__file__).resolve().parents[2]
DEFAULT_RAW = REPO_ROOT / "assets" / "card" / "raw"
DEFAULT_FACES = REPO_ROOT / "assets" / "card" / "faces"
DEFAULT_SQL_DIR = REPO_ROOT / "mtcg-server" / "src" / "main" / "resources" / "sql"
IMAGE_EXTS = {".png", ".jpg", ".jpeg", ".webp", ".bmp"}


def _list_images(raw_dir: Path) -> list[Path]:
    files = sorted(
        f
        for f in raw_dir.rglob("*")
        if f.is_file() and f.suffix.lower() in IMAGE_EXTS and not f.name.startswith("_")
    )
    return files


def _crop_code_badge(raw_path: Path, out_path: Path) -> None:
    img = Image.open(raw_path)
    w, h = img.size
    region = extract_card.CARD_REGION
    sx, sy = w / extract_card.REF_WIDTH, h / extract_card.REF_HEIGHT
    box = (
        int(region["left"] * sx),
        int(region["top"] * sy),
        int(region["right"] * sx),
        int(region["bottom"] * sy),
    )
    card = img.crop(box)
    cw, ch = card.size
    code = card.crop((int(cw * 0.50), int(ch * 0.01), int(cw * 0.995), int(ch * 0.09)))
    code = code.resize((code.width * 3, code.height * 3), Image.Resampling.LANCZOS)
    out_path.parent.mkdir(parents=True, exist_ok=True)
    code.save(out_path)


def cmd_prepare(args: argparse.Namespace) -> int:
    raw_dir = Path(args.input).resolve()
    out_dir = Path(args.output).resolve()
    cards_dir = out_dir / "cards"
    badges_dir = out_dir / "code_badges"
    cards_dir.mkdir(parents=True, exist_ok=True)
    badges_dir.mkdir(parents=True, exist_ok=True)

    files = _list_images(raw_dir)
    if not files:
        print(f"未找到图片: {raw_dir}")
        return 1

    print(f"找到 {len(files)} 张截图")
    print(f"输出: {out_dir}")
    catalog_cards: list[dict[str, Any]] = []

    for i, f in enumerate(files):
        try:
            rel = f.relative_to(raw_dir)
        except ValueError:
            rel = Path(f.name)
        stem = f.stem
        card_out = cards_dir / rel.parent / f"{stem}_card.png"
        badge_out = badges_dir / rel.parent / f"{stem}_code.png"
        card_out.parent.mkdir(parents=True, exist_ok=True)
        size = extract_card.extract_card(
            str(f),
            str(card_out),
            target_width=args.width,
            target_height=args.height,
            normalize=not args.no_normalize,
        )
        _crop_code_badge(f, badge_out)
        source = rel.as_posix()
        catalog_cards.append(
            {
                "source": source,
                "extracted_card": str(card_out.relative_to(out_dir)).replace("\\", "/"),
                "code_badge": str(badge_out.relative_to(out_dir)).replace("\\", "/"),
                "base_card_no": "",
                "rarity": "",
                "card_name": "",
                "card_type": "RUSH_POINT",
                "level": None,
                "color": None,
                "environment": None,
                "traits": None,
                "attack_range": None,
                "power": None,
                "effect_text": None,
                "effect_json": None,
                "_note": "请根据 code_badge / 卡面填写 base_card_no + rarity；角色卡再补 name/level/…",
            }
        )
        print(f"  [{i+1}/{len(files)}] {source} -> {size[0]}x{size[1]}")

    catalog = {
        "batch": out_dir.name,
        "note": "填写 base_card_no + rarity 后执行: python scripts/卡面提取/pipeline.py apply <本文件>",
        "raw_dir": str(raw_dir.relative_to(REPO_ROOT)).replace("\\", "/")
        if raw_dir.is_relative_to(REPO_ROOT)
        else str(raw_dir),
        "extracted_dir": str(out_dir.relative_to(REPO_ROOT)).replace("\\", "/")
        if out_dir.is_relative_to(REPO_ROOT)
        else str(out_dir),
        "products": [
            {
                "product_code": "PB01",
                "product_name": "推广包01",
                "release_date": None,
                "description": "按需修改/增删产品",
            }
        ],
        "cards": catalog_cards,
    }
    catalog_path = out_dir / "catalog.json"
    catalog_path.write_text(json.dumps(catalog, ensure_ascii=False, indent=2), encoding="utf-8")
    print("-" * 50)
    print(f"已生成模板: {catalog_path}")
    print("下一步: 看 code_badges/，填写 catalog.json 的 base_card_no / rarity（角色卡补全字段）")
    print(f"然后: python scripts/卡面提取/pipeline.py apply {catalog_path}")
    return 0


def _resolve_raw_path(catalog: dict[str, Any], source: str, catalog_path: Path) -> Path:
    raw_dir = catalog.get("raw_dir") or "assets/card/raw"
    p = Path(raw_dir)
    if not p.is_absolute():
        p = REPO_ROOT / p
    cand = p / source
    if cand.is_file():
        return cand
    # 兼容 catalog 与 raw 同级
    alt = catalog_path.parent / source
    if alt.is_file():
        return alt
    raise FileNotFoundError(f"找不到源图: {source} (tried {cand})")


def _resolve_extracted_card(catalog: dict[str, Any], card: dict[str, Any], catalog_path: Path) -> Path:
    extracted_dir = catalog.get("extracted_dir")
    rel = card.get("extracted_card")
    if extracted_dir and rel:
        base = Path(extracted_dir)
        if not base.is_absolute():
            base = REPO_ROOT / base
        path = base / rel
        if path.is_file():
            return path
    # 相对 catalog 目录
    if rel:
        path = catalog_path.parent / rel
        if path.is_file():
            return path
    # 现切
    raw = _resolve_raw_path(catalog, card["source"], catalog_path)
    tmp = catalog_path.parent / "_tmp_cards" / f"{Path(card['source']).stem}_card.png"
    tmp.parent.mkdir(parents=True, exist_ok=True)
    extract_card.extract_card(str(raw), str(tmp))
    return tmp


def _load_catalog(path: Path) -> dict[str, Any]:
    data = json.loads(path.read_text(encoding="utf-8"))
    if "cards" not in data:
        raise ValueError("catalog 缺少 cards 数组")
    return data


def _validated_cards(catalog: dict[str, Any]) -> list[dict[str, Any]]:
    out: list[dict[str, Any]] = []
    seen: set[str] = set()
    for i, raw in enumerate(catalog["cards"]):
        if not raw.get("base_card_no") or not raw.get("rarity"):
            raise ValueError(f"cards[{i}] 缺少 base_card_no 或 rarity: {raw.get('source')}")
        c = normalize_seed_card(raw)
        if c["card_code"] in seen:
            print(f"  [skip dup] {c['card_code']} <- {raw.get('source')}")
            continue
        seen.add(c["card_code"])
        # 保留 source 供拷图
        c["_source"] = raw.get("source")
        c["_extracted_card"] = raw.get("extracted_card")
        out.append(c)
    return out


def cmd_apply(args: argparse.Namespace) -> int:
    catalog_path = Path(args.catalog).resolve()
    catalog = _load_catalog(catalog_path)
    cards = _validated_cards(catalog)
    products = catalog.get("products") or []
    faces = Path(args.faces).resolve()
    sql_dir = Path(args.sql_dir).resolve()
    seed_dir = sql_dir / "seed-cards"
    index_path = sql_dir / "seed-cards.sql"

    print(f"catalog: {catalog_path}")
    print(f"cards: {len(cards)}")
    print(f"faces: {faces}")

    for c in cards:
        # 还原字段给 resolver
        stub = {
            "source": c.get("_source"),
            "extracted_card": c.get("_extracted_card"),
        }
        src_card = _resolve_extracted_card(catalog, stub, catalog_path)
        dest = faces / c["product_code"] / f"{c['card_code']}.png"
        dest.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(src_card, dest)
        c["image_path"] = image_rel_path(c["product_code"], c["card_code"])
        print(f"  [img] {c['card_code']} <- {stub.get('source')}")

    # 去掉内部字段；与官网 out/ 合并后写 seed-cards
    seed_cards = [{k: v for k, v in c.items() if not k.startswith("_")} for c in cards]
    written = rebuild_merged_seed(
        extra_products=products,
        extra_cards=seed_cards,
        out_dir=seed_dir,
        index_path=index_path,
        title_prefix="卡牌/产品种子",
    )
    print("-" * 50)
    for code, n in written:
        print(f"  [sql] seed-cards/{code}.sql ({n})")
    print(f"总文件: {index_path}")
    return 0


def cmd_sql(args: argparse.Namespace) -> int:
    catalog_path = Path(args.catalog).resolve()
    catalog = _load_catalog(catalog_path)
    cards = _validated_cards(catalog)
    products = catalog.get("products") or []
    sql_dir = Path(args.sql_dir).resolve()
    for c in cards:
        c["image_path"] = image_rel_path(c["product_code"], c["card_code"])
    seed_cards = [{k: v for k, v in c.items() if not k.startswith("_")} for c in cards]
    written = rebuild_merged_seed(
        extra_products=products,
        extra_cards=seed_cards,
        out_dir=sql_dir / "seed-cards",
        index_path=sql_dir / "seed-cards.sql",
    )
    print(f"已重生成 SQL，产品数={len(written)}，总卡数={sum(n for _, n in written)}")
    return 0


def build_parser() -> argparse.ArgumentParser:
    p = argparse.ArgumentParser(description="截图补齐流水线（裁切 → catalog → faces + seed-cards）")
    sub = p.add_subparsers(dest="cmd", required=True)

    p_prep = sub.add_parser("prepare", help="裁切卡面 + 生成 catalog 模板")
    p_prep.add_argument("input", nargs="?", default=str(DEFAULT_RAW), help="raw 截图目录")
    p_prep.add_argument("-o", "--output", required=True, help="中间输出目录（含 catalog.json）")
    p_prep.add_argument("--width", type=int, default=CARD_WIDTH)
    p_prep.add_argument("--height", type=int, default=CARD_HEIGHT)
    p_prep.add_argument("--no-normalize", action="store_true")
    p_prep.set_defaults(func=cmd_prepare)

    p_apply = sub.add_parser("apply", help="按 catalog 写入 faces 并生成 SQL")
    p_apply.add_argument("catalog", help="已填写的 catalog.json")
    p_apply.add_argument("--faces", default=str(DEFAULT_FACES), help="卡面输出目录")
    p_apply.add_argument("--sql-dir", default=str(DEFAULT_SQL_DIR))
    p_apply.set_defaults(func=cmd_apply)

    p_sql = sub.add_parser("sql", help="仅根据 catalog 重生成 seed-cards SQL")
    p_sql.add_argument("catalog", help="catalog.json")
    p_sql.add_argument("--sql-dir", default=str(DEFAULT_SQL_DIR))
    p_sql.set_defaults(func=cmd_sql)

    return p


def main() -> None:
    parser = build_parser()
    args = parser.parse_args()
    raise SystemExit(args.func(args))


if __name__ == "__main__":
    main()
