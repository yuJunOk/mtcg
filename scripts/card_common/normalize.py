# -*- coding: utf-8 -*-
"""卡面规范化：去透明边 → 统一尺寸 → 统一圆角。"""

from __future__ import annotations

from pathlib import Path

from PIL import Image, ImageDraw

from .spec import (
    ALPHA_THRESHOLD,
    CARD_HEIGHT,
    CARD_WIDTH,
    CORNER_RADIUS_RATIO,
    OUTPUT_FORMAT,
    OUTPUT_OPTIMIZE,
)

SUPERSAMPLE = 4


def corner_radius_for(size: tuple[int, int] | None = None) -> int:
    """按画布宽度计算标准圆角半径。"""
    w = (size or (CARD_WIDTH, CARD_HEIGHT))[0]
    return max(1, round(w * CORNER_RADIUS_RATIO))


def trim_transparent(img: Image.Image, threshold: int = ALPHA_THRESHOLD) -> Image.Image:
    """裁掉四周透明边，保留不透明内容 bbox。"""
    rgba = img.convert("RGBA")
    if threshold <= 0:
        bbox = rgba.getbbox()
    else:
        alpha = rgba.getchannel("A")
        # 阈值化后再取 bbox，忽略半透明杂边
        mask = alpha.point(lambda a: 255 if a > threshold else 0)
        bbox = mask.getbbox()
    if not bbox:
        return rgba
    return rgba.crop(bbox)


def fit_to_canvas(
    img: Image.Image,
    width: int = CARD_WIDTH,
    height: int = CARD_HEIGHT,
) -> Image.Image:
    """等比缩放并居中贴到固定画布（透明底），保证所有卡同等尺寸。"""
    rgba = img.convert("RGBA")
    src_w, src_h = rgba.size
    if src_w <= 0 or src_h <= 0:
        return Image.new("RGBA", (width, height), (0, 0, 0, 0))

    scale = min(width / src_w, height / src_h)
    new_w = max(1, round(src_w * scale))
    new_h = max(1, round(src_h * scale))
    try:
        resample = Image.Resampling.LANCZOS
    except AttributeError:
        resample = Image.LANCZOS
    scaled = rgba.resize((new_w, new_h), resample)

    canvas = Image.new("RGBA", (width, height), (0, 0, 0, 0))
    ox = (width - new_w) // 2
    oy = (height - new_h) // 2
    canvas.paste(scaled, (ox, oy), scaled)
    return canvas


def apply_rounded_corners(img: Image.Image, radius: int | None = None) -> Image.Image:
    """抗锯齿圆角（超采样），四角透明。"""
    rgba = img.convert("RGBA")
    w, h = rgba.size
    r = corner_radius_for((w, h)) if radius is None else max(1, radius)

    sw, sh = w * SUPERSAMPLE, h * SUPERSAMPLE
    sr = r * SUPERSAMPLE
    mask = Image.new("L", (sw, sh), 0)
    draw = ImageDraw.Draw(mask)
    if hasattr(draw, "rounded_rectangle"):
        draw.rounded_rectangle([0, 0, sw - 1, sh - 1], radius=sr, fill=255)
    else:
        draw.ellipse([0, 0, 2 * sr, 2 * sr], fill=255)
        draw.ellipse([sw - 1 - 2 * sr, 0, sw - 1, 2 * sr], fill=255)
        draw.ellipse([0, sh - 1 - 2 * sr, 2 * sr, sh - 1], fill=255)
        draw.ellipse([sw - 1 - 2 * sr, sh - 1 - 2 * sr, sw - 1, sh - 1], fill=255)
        draw.rectangle([sr, 0, sw - 1 - sr, sh - 1], fill=255)
        draw.rectangle([0, sr, sw - 1, sh - 1 - sr], fill=255)

    try:
        resample = Image.Resampling.LANCZOS
    except AttributeError:
        resample = Image.LANCZOS
    mask = mask.resize((w, h), resample)
    out = rgba.copy()
    out.putalpha(mask)
    return out


def normalize_card_image(
    img: Image.Image,
    *,
    width: int = CARD_WIDTH,
    height: int = CARD_HEIGHT,
    trim: bool = True,
    round_corners: bool = True,
    radius: int | None = None,
) -> Image.Image:
    """完整规范化流水线。"""
    out = img.convert("RGBA")
    if trim:
        out = trim_transparent(out)
    out = fit_to_canvas(out, width, height)
    if round_corners:
        out = apply_rounded_corners(out, radius)
    return out


def normalize_card_file(
    src: Path | str,
    dest: Path | str | None = None,
    **kwargs,
) -> tuple[int, int]:
    """规范化单个文件，返回输出尺寸。"""
    src_path = Path(src)
    dest_path = Path(dest) if dest else src_path
    dest_path.parent.mkdir(parents=True, exist_ok=True)
    img = Image.open(src_path)
    out = normalize_card_image(img, **kwargs)
    out.save(dest_path, OUTPUT_FORMAT, optimize=OUTPUT_OPTIMIZE)
    return out.size


def is_normalized(
    path: Path | str,
    *,
    width: int = CARD_WIDTH,
    height: int = CARD_HEIGHT,
    max_pad: int = 2,
) -> bool:
    """快速判断是否已是目标尺寸且几乎无透明边。"""
    p = Path(path)
    if not p.is_file():
        return False
    with Image.open(p) as im:
        if im.size != (width, height):
            return False
        rgba = im.convert("RGBA")
        bbox = rgba.getbbox()
        if not bbox:
            return False
        l, t, r, b = bbox
        return l <= max_pad and t <= max_pad and (width - r) <= max_pad and (height - b) <= max_pad
