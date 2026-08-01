#!/usr/bin/env python3
"""
MTCG 卡面提取工具

从官网截图中批量提取卡牌卡面区域，并应用圆角处理。

使用方法:
    # 处理当前目录下所有图片（输出到 extracted/ 子目录）
    python extract_card.py

    # 指定输入输出目录
    python extract_card.py 输入目录 -o 输出目录

    # 自定义圆角半径
    python extract_card.py --radius 15

    # 处理单个文件
    python extract_card.py screenshot.jpg -o output.png

依赖: Pillow (pip install Pillow)
"""
import sys
import argparse
from pathlib import Path

try:
    from PIL import Image, ImageDraw, ImageFilter
except ImportError:
    print("错误: 需要安装 Pillow 库")
    print("  pip install Pillow")
    sys.exit(1)


# ============================================================
# 卡面区域配置（基于 1080×2354 手机截图标定）
# ============================================================
# 截图来源: 安卓手机全屏截图，分辨率 1080×2354
# 卡面在深色页面背景中，坐标包含卡面黑色外边框
# 通过多点采样众数法确定卡面与页面背景的分界线
# 可通过命令行参数 --left/--top/--right/--bottom 微调
CARD_REGION = {
    "left": 168,
    "top": 695,
    "right": 915,
    "bottom": 1737,
}
# 圆角半径（像素），通过四角圆拟合计算得出（底部双角74点采样，中位数41）
CARD_CORNER_RADIUS = 41
# 参考截图尺寸（用于按比例缩放坐标）
REF_WIDTH = 1080
REF_HEIGHT = 2354

# 支持的图片格式
IMAGE_EXTENSIONS = {".png", ".jpg", ".jpeg", ".webp", ".bmp"}


# 超采样倍数：蒙版在更高分辨率绘制后降采样，实现抗锯齿
SUPERSAMPLE = 4


def apply_rounded_corners(img, radius):
    """为图片添加抗锯齿圆角，使四个角变为透明。

    使用超采样技术：在 4x 分辨率绘制圆角蒙版，再降采样回原尺寸，
    产生平滑的半透明边缘像素，消除锯齿感。

    Args:
        img: PIL Image 对象
        radius: 圆角半径（像素）

    Returns:
        RGBA 格式的 PIL Image，四角平滑透明
    """
    img = img.convert("RGBA")
    w, h = img.size

    # 在超采样分辨率下绘制蒙版
    sw, sh = w * SUPERSAMPLE, h * SUPERSAMPLE
    sr = radius * SUPERSAMPLE

    mask = Image.new("L", (sw, sh), 0)
    draw = ImageDraw.Draw(mask)

    if hasattr(draw, "rounded_rectangle"):
        draw.rounded_rectangle(
            [0, 0, sw - 1, sh - 1],
            radius=sr,
            fill=255,
        )
    else:
        _draw_rounded_rect_manual(draw, sw, sh, sr, fill=255)

    # 降采样回原尺寸，LANCZOS 滤波产生平滑的抗锯齿边缘
    try:
        mask = mask.resize((w, h), Image.Resampling.LANCZOS)
    except AttributeError:
        # 兼容旧版 Pillow
        mask = mask.resize((w, h), Image.LANCZOS)

    img.putalpha(mask)
    return img


def _draw_rounded_rect_manual(draw, w, h, radius, fill=255):
    """手动绘制圆角矩形（兼容旧版 Pillow）。"""
    import math

    # 四个角的圆心
    corners = [
        (radius, radius),              # 左上
        (w - 1 - radius, radius),      # 右上
        (radius, h - 1 - radius),      # 左下
        (w - 1 - radius, h - 1 - radius),  # 右下
    ]
    # 角的起始/结束角度
    angles = [180, 270, 90, 0]

    for (cx, cy), start in zip(corners, angles):
        draw.pieslice(
            [cx - radius, cy - radius, cx + radius, cy + radius],
            start=start,
            end=start + 90,
            fill=fill,
        )

    # 填充矩形主体（中间十字区域）
    draw.rectangle([radius, 0, w - 1 - radius, h - 1], fill=fill)
    draw.rectangle([0, radius, w - 1, h - 1 - radius], fill=fill)


def extract_card(img_path, output_path, region=None, radius=CARD_CORNER_RADIUS):
    """从截图中提取卡面。

    Args:
        img_path: 截图文件路径
        output_path: 输出文件路径
        region: 卡面区域字典 {left, top, right, bottom}，为 None 则使用默认值
        radius: 圆角半径

    Returns:
        提取后的图片尺寸 (width, height)
    """
    img = Image.open(img_path)
    w, h = img.size

    # 按比例缩放坐标（适配不同分辨率的截图）
    scale_x = w / REF_WIDTH
    scale_y = h / REF_HEIGHT

    r = region or CARD_REGION
    left = int(r["left"] * scale_x)
    top = int(r["top"] * scale_y)
    right = int(r["right"] * scale_x)
    bottom = int(r["bottom"] * scale_y)
    corner_radius = max(1, int(radius * min(scale_x, scale_y)))

    # 裁剪卡面区域
    card = img.crop((left, top, right, bottom))

    # 画质增强：轻微锐化补偿 JPG 压缩伪影
    card = card.filter(ImageFilter.UnsharpMask(radius=1, percent=120, threshold=3))

    # 应用抗锯齿圆角
    card = apply_rounded_corners(card, corner_radius)

    # 保存为 PNG（无损，保留透明通道）
    card.save(output_path, "PNG", optimize=True)
    return card.size


def batch_extract(input_dir, output_dir, region=None, radius=CARD_CORNER_RADIUS):
    """批量提取目录下所有截图的卡面。

    Args:
        input_dir: 输入目录
        output_dir: 输出目录
        region: 卡面区域（None 使用默认值）
        radius: 圆角半径

    Returns:
        (成功数, 失败数)
    """
    input_dir = Path(input_dir)
    output_dir = Path(output_dir)
    output_dir.mkdir(parents=True, exist_ok=True)

    # 查找所有图片文件（排除以 _ 开头的文件）
    files = sorted(
        f
        for f in input_dir.iterdir()
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
    print("-" * 50)

    success = 0
    failed = 0
    for f in files:
        output_name = f.stem + "_card.png"
        output_path = output_dir / output_name
        try:
            size = extract_card(str(f), str(output_path), region, radius)
            print(f"  [OK] {f.name} -> {output_name} ({size[0]}x{size[1]})")
            success += 1
        except Exception as e:
            print(f"  [FAIL] {f.name} -> {e}")
            failed += 1

    print("-" * 50)
    print(f"完成: 成功 {success}，失败 {failed}")
    return success, failed


def main():
    parser = argparse.ArgumentParser(
        description="MTCG 卡面提取工具 - 从截图中批量提取卡牌卡面",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
示例:
  python extract_card.py                          # 处理当前目录所有图片
  python extract_card.py assets/card              # 处理指定目录
  python extract_card.py screenshot.jpg -o out.png  # 处理单个文件
  python extract_card.py --radius 20              # 自定义圆角半径
  python extract_card.py --left 160 --top 705     # 微调坐标
        """,
    )
    parser.add_argument(
        "input",
        nargs="?",
        default=".",
        help="输入文件或目录（默认当前目录）",
    )
    parser.add_argument(
        "-o",
        "--output",
        default=None,
        help="输出文件或目录（默认: 单文件时同目录 _card.png，目录时 extracted/ 子目录）",
    )
    parser.add_argument(
        "--radius",
        type=int,
        default=CARD_CORNER_RADIUS,
        help=f"圆角半径，默认 {CARD_CORNER_RADIUS}",
    )
    parser.add_argument(
        "--left",
        type=int,
        default=None,
        help=f"卡面左边界 x 坐标（默认 {CARD_REGION['left']}）",
    )
    parser.add_argument(
        "--top",
        type=int,
        default=None,
        help=f"卡面上边界 y 坐标（默认 {CARD_REGION['top']}）",
    )
    parser.add_argument(
        "--right",
        type=int,
        default=None,
        help=f"卡面右边界 x 坐标（默认 {CARD_REGION['right']}）",
    )
    parser.add_argument(
        "--bottom",
        type=int,
        default=None,
        help=f"卡面下边界 y 坐标（默认 {CARD_REGION['bottom']}）",
    )

    args = parser.parse_args()

    # 构建自定义区域
    region = None
    if any(v is not None for v in [args.left, args.top, args.right, args.bottom]):
        region = {
            "left": args.left if args.left is not None else CARD_REGION["left"],
            "top": args.top if args.top is not None else CARD_REGION["top"],
            "right": args.right if args.right is not None else CARD_REGION["right"],
            "bottom": args.bottom if args.bottom is not None else CARD_REGION["bottom"],
        }

    input_path = Path(args.input)

    # 单文件模式
    if input_path.is_file():
        output = args.output or str(input_path.parent / (input_path.stem + "_card.png"))
        try:
            size = extract_card(str(input_path), output, region, args.radius)
            print(f"[OK] {input_path.name} -> {output} ({size[0]}x{size[1]})")
        except Exception as e:
            print(f"[FAIL] {input_path.name} -> {e}")
            sys.exit(1)
    # 目录模式
    elif input_path.is_dir():
        output_dir = args.output or str(input_path / "extracted")
        batch_extract(str(input_path), output_dir, region, args.radius)
    else:
        print(f"错误: 路径不存在 - {input_path}")
        sys.exit(1)


if __name__ == "__main__":
    main()
