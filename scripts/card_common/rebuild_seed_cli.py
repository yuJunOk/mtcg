#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""合并官网 out/ + 截图 catalogs，重写 seed-cards/{产品}.sql 与总文件 seed-cards.sql。"""
from __future__ import annotations

import argparse
import sys
from pathlib import Path

_SCRIPTS = Path(__file__).resolve().parents[1]
if str(_SCRIPTS) not in sys.path:
    sys.path.insert(0, str(_SCRIPTS))

from card_common.seed_sql import (  # noqa: E402
    DEFAULT_CATALOG_DIR,
    DEFAULT_OFFICIAL_OUT,
    DEFAULT_SEED_ALL,
    DEFAULT_SEED_DIR,
    rebuild_merged_seed,
)


def main() -> int:
    p = argparse.ArgumentParser(description="合并官网+截图补齐，生成 seed-cards")
    p.add_argument("--official-out", type=Path, default=DEFAULT_OFFICIAL_OUT)
    p.add_argument("--catalog-dir", type=Path, default=DEFAULT_CATALOG_DIR)
    p.add_argument("--seed-dir", type=Path, default=DEFAULT_SEED_DIR)
    p.add_argument("--seed-all", type=Path, default=DEFAULT_SEED_ALL)
    args = p.parse_args()

    written = rebuild_merged_seed(
        official_out=args.official_out,
        catalog_dir=args.catalog_dir,
        out_dir=args.seed_dir,
        index_path=args.seed_all,
    )
    total = sum(n for _, n in written)
    print(f"OK: {args.seed_all} ← {len(written)} products / {total} cards")
    for code, n in written:
        print(f"  {code}: {n}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
