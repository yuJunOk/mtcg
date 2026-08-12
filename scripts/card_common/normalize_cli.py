#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
批量规范化卡图：去透明边 + 统一画布尺寸 + 统一圆角。

用法（仓库根目录）:
  python scripts/card_common/normalize_cli.py assets/card/faces
  python scripts/card_common/normalize_cli.py assets/card/extracted -o assets/card/faces/RP01
  python scripts/card_common/normalize_cli.py assets/card/faces --force
"""

from __future__ import annotations

import argparse
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
if str(ROOT) not in sys.path:
    sys.path.insert(0, str(ROOT))

from card_common.normalize import is_normalized, normalize_card_file  # noqa: E402
from card_common.spec import CARD_HEIGHT, CARD_WIDTH  # noqa: E402

IMAGE_EXT = {".png", ".jpg", ".jpeg", ".webp", ".bmp"}


def iter_images(path: Path) -> list[Path]:
    if path.is_file():
        return [path] if path.suffix.lower() in IMAGE_EXT else []
    return sorted(
        p
        for p in path.rglob("*")
        if p.is_file() and p.suffix.lower() in IMAGE_EXT and not p.name.startswith("_")
    )


def main() -> int:
    parser = argparse.ArgumentParser(description="批量规范化卡图到统一规格")
    parser.add_argument("input", type=Path, help="输入文件或目录")
    parser.add_argument("-o", "--output", type=Path, default=None, help="输出目录（默认原地覆盖）")
    parser.add_argument("--width", type=int, default=CARD_WIDTH)
    parser.add_argument("--height", type=int, default=CARD_HEIGHT)
    parser.add_argument("--force", action="store_true", help="已规范化的也强制重做")
    parser.add_argument("--no-round", action="store_true", help="不重做圆角")
    parser.add_argument("--no-trim", action="store_true", help="不去透明边")
    args = parser.parse_args()

    files = iter_images(args.input)
    if not files:
        print(f"未找到图片: {args.input}")
        return 1

    ok = skip = fail = 0
    print(f"目标尺寸 {args.width}x{args.height}，共 {len(files)} 张")
    for src in files:
        if args.output:
            # 保持相对子目录结构（若输入是目录）
            if args.input.is_dir():
                rel = src.relative_to(args.input)
                dest = args.output / rel
            else:
                dest = args.output / src.name
            dest = dest.with_suffix(".png")
        else:
            dest = src.with_suffix(".png")

        if not args.force and dest.exists() and is_normalized(dest, width=args.width, height=args.height):
            skip += 1
            continue
        try:
            size = normalize_card_file(
                src,
                dest,
                width=args.width,
                height=args.height,
                trim=not args.no_trim,
                round_corners=not args.no_round,
            )
            print(f"  [OK] {src.name} -> {dest.name} ({size[0]}x{size[1]})")
            ok += 1
        except Exception as exc:  # noqa: BLE001
            print(f"  [FAIL] {src}: {exc}")
            fail += 1

    print(f"完成 ok={ok} skip={skip} fail={fail}")
    return 2 if fail else 0


if __name__ == "__main__":
    raise SystemExit(main())
