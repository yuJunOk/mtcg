#!/usr/bin/env python3
"""
MTCG 卡面提取工具

从官网/第三方截图中批量提取卡牌卡面，并规范化到与「官网卡表拉取」相同的统一规格。

使用方法:
    python extract_card.py
    python extract_card.py 输入目录 -o 输出目录
    python extract_card.py --radius 15
    python extract_card.py screenshot.jpg -o output.png

依赖: Pillow (pip install Pillow)
"""
from __future__ import annotations

import argparse
import sys
from pathlib import Path

try:
    from PIL import Image, ImageFilter
except ImportError:
    print("错误: 需要安装 Pillow 库")
    print("  pip install Pillow")
    sys.exit(1)

# 共享规格（与官网拉取同一套）
_SCRIPTS = Path(__file__).resolve().parents[1]
if str(_SCRIPTS) not in sys.path:
    sys.path.insert(0, str(_SCRIPTS))

from card_common.normalize import normalize_card_image  # noqa: E402
from card_common.spec import CARD_HEIGHT, CARD_WIDTH, CORNER_RADIUS_RATIO  # noqa: E402

# ============================================================
# 卡面区域配置（基于 1080×2354 手机截图标定）
# ============================================================
CARD_REGION = {
    "left": 168,
    "top": 695,
    "right": 915,
    "bottom": 1737,
}
# 圆角：默认跟随统一规格比例；可用 --radius 覆盖（按截图裁切尺寸）
CARD_CORNER_RADIUS = None  # None = 使用统一规格
REF_WIDTH = 1080
REF_HEIGHT = 2354
IMAGE_EXTENSIONS = {".png", ".jpg", ".jpeg", ".webp", ".bmp"}


def extract_card(
    img_path,
    output_path,
    region=None,
    radius=None,
    *,
    target_width: int = CARD_WIDTH,
    target_height: int = CARD_HEIGHT,
    normalize: bool = True,
):
    """从截图中提取卡面并规范化。

    Returns:
        提取后的图片尺寸 (width, height)
    """
    img = Image.open(img_path)
    w, h = img.size

    scale_x = w / REF_WIDTH
    scale_y = h / REF_HEIGHT

    r = region or CARD_REGION
    left = int(r["left"] * scale_x)
    top = int(r["top"] * scale_y)
    right = int(r["right"] * scale_x)
    bottom = int(r["bottom"] * scale_y)

    card = img.crop((left, top, right, bottom))
    card = card.filter(ImageFilter.UnsharpMask(radius=1, percent=120, threshold=3))

    if normalize:
        # 与官网拉取一致：去透明边 → 统一画布 → 统一圆角
        override_radius = None
        if radius is not None:
            # 用户指定的是「裁切图」上的半径，换算到目标宽度
            crop_w = max(1, right - left)
            override_radius = max(1, round(radius * (target_width / crop_w)))
        card = normalize_card_image(
            card,
            width=target_width,
            height=target_height,
            trim=True,
            round_corners=True,
            radius=override_radius,
        )
    else:
        # 旧行为：仅在裁切尺寸上做圆角（不推荐，规格会漂）
        crop_w = max(1, right - left)
        corner = (
            max(1, int(radius * min(scale_x, scale_y)))
            if radius is not None
            else max(1, round(crop_w * CORNER_RADIUS_RATIO))
        )
        from card_common.normalize import apply_rounded_corners

        card = apply_rounded_corners(card.convert("RGBA"), corner)

    Path(output_path).parent.mkdir(parents=True, exist_ok=True)
    card.save(output_path, "PNG", optimize=True)
    return card.size


def batch_extract(
    input_dir,
    output_dir,
    region=None,
    radius=None,
    *,
    target_width: int = CARD_WIDTH,
    target_height: int = CARD_HEIGHT,
    normalize: bool = True,
):
    """批量提取目录下所有截图的卡面。"""
    input_dir = Path(input_dir)
    output_dir = Path(output_dir)
    output_dir.mkdir(parents=True, exist_ok=True)

    files = sorted(
        f
        for f in input_dir.rglob("*")
        if f.suffix.lower() in IMAGE_EXTENSIONS
        and not f.name.startswith("_")
        and f.is_file()
    )

    if not files:
        print(f"未找到图片文件: {input_dir}")
        print(f"支持的格式: {', '.join(IMAGE_EXTENSIONS)}")
        return 0, 0

    print(f"找到 {len(files)} 张图片")
    print(f"输出目录: {output_dir}")
    print(f"统一规格: {target_width}x{target_height}（与官网卡表一致）")
    print("-" * 50)

    success = 0
    failed = 0
    for f in files:
        # 保留相对子目录（如 raw/SD04-时间/...）
        try:
            rel = f.relative_to(input_dir)
        except ValueError:
            rel = Path(f.name)
        output_path = output_dir / rel.parent / (f.stem + "_card.png")
        try:
            size = extract_card(
                str(f),
                str(output_path),
                region,
                radius,
                target_width=target_width,
                target_height=target_height,
                normalize=normalize,
            )
            print(f"  [OK] {rel} -> {output_path.relative_to(output_dir)} ({size[0]}x{size[1]})")
            success += 1
        except Exception as e:
            print(f"  [FAIL] {rel} -> {e}")
            failed += 1

    print("-" * 50)
    print(f"完成: 成功 {success}，失败 {failed}")
    return success, failed


def main():
    parser = argparse.ArgumentParser(
        description="MTCG 卡面提取工具 - 从截图提取并规范化到统一卡面规格",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog=f"""
统一规格: {CARD_WIDTH}x{CARD_HEIGHT}（与 scripts/官网卡表拉取 一致）

示例:
  python extract_card.py assets/card/raw -o assets/card/extracted
  python extract_card.py screenshot.jpg -o BP01-120-PR.png
  python extract_card.py --no-normalize   # 仅裁切（旧行为）
        """,
    )
    parser.add_argument("input", nargs="?", default=".", help="输入文件或目录")
    parser.add_argument("-o", "--output", default=None, help="输出文件或目录")
    parser.add_argument(
        "--radius",
        type=int,
        default=None,
        help="裁切图上的圆角半径（默认按统一比例换算）",
    )
    parser.add_argument("--left", type=int, default=None)
    parser.add_argument("--top", type=int, default=None)
    parser.add_argument("--right", type=int, default=None)
    parser.add_argument("--bottom", type=int, default=None)
    parser.add_argument("--width", type=int, default=CARD_WIDTH, help=f"目标宽，默认 {CARD_WIDTH}")
    parser.add_argument("--height", type=int, default=CARD_HEIGHT, help=f"目标高，默认 {CARD_HEIGHT}")
    parser.add_argument(
        "--no-normalize",
        action="store_true",
        help="不做统一尺寸规范化（仅裁切+圆角）",
    )

    args = parser.parse_args()

    region = None
    if any(v is not None for v in [args.left, args.top, args.right, args.bottom]):
        region = {
            "left": args.left if args.left is not None else CARD_REGION["left"],
            "top": args.top if args.top is not None else CARD_REGION["top"],
            "right": args.right if args.right is not None else CARD_REGION["right"],
            "bottom": args.bottom if args.bottom is not None else CARD_REGION["bottom"],
        }

    input_path = Path(args.input)
    normalize = not args.no_normalize

    if input_path.is_file():
        output = args.output or str(input_path.parent / (input_path.stem + "_card.png"))
        try:
            size = extract_card(
                str(input_path),
                output,
                region,
                args.radius,
                target_width=args.width,
                target_height=args.height,
                normalize=normalize,
            )
            print(f"[OK] {input_path.name} -> {output} ({size[0]}x{size[1]})")
        except Exception as e:
            print(f"[FAIL] {input_path.name} -> {e}")
            sys.exit(1)
    elif input_path.is_dir():
        output_dir = args.output or str(input_path / "extracted")
        batch_extract(
            str(input_path),
            output_dir,
            region,
            args.radius,
            target_width=args.width,
            target_height=args.height,
            normalize=normalize,
        )
    else:
        print(f"错误: 路径不存在 - {input_path}")
        sys.exit(1)


if __name__ == "__main__":
    main()
