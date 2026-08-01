#!/usr/bin/env python3
"""
生成 MTCG 5 张卡背/卡面（747x1042, radius=41）

v4: 参考宝可梦/MTG 卡背设计原则重做
- MTCG logo 缩小（占高度约 12%）
- 中心主视觉符号占 40-50%（真正的主角）
- 对称布局、大量留白
- 品牌色统一、底部小字版权
"""
from pathlib import Path
import math
import numpy as np
from PIL import Image, ImageDraw, ImageEnhance, ImageFilter, ImageFont

SIZE = (747, 1042)
RADIUS = 41
OUT_DIR = Path(r"d:\pengYuJun\Project\mtcg\assets\card\designs")
TEX_DIR = Path(__file__).parent / "tex"

FONT_IMPACT = "C:/Windows/Fonts/impact.ttf"
FONT_ARIAL = "C:/Windows/Fonts/arialbd.ttf"
FONT_SIMHEI = "C:/Windows/Fonts/simhei.ttf"

RED = (200, 16, 46, 255)
DARKRED = (139, 0, 0, 255)
SOFTRED = (215, 38, 61, 255)
BLACK = (10, 10, 10, 255)
WHITE = (255, 255, 255, 255)
DARK = (34, 34, 34, 255)
GOLD = (212, 175, 55, 255)
DARKGOLD = (160, 130, 40, 255)


def get_font(path, size):
    return ImageFont.truetype(path, size)


def draw_text_centered(draw, cx, cy, text, font, fill, stroke_w=0, stroke_fill=None):
    bbox = draw.textbbox((0, 0), text, font=font, stroke_width=stroke_w)
    w, h = bbox[2] - bbox[0], bbox[3] - bbox[1]
    x = cx - w // 2 - bbox[0]
    y = cy - h // 2 - bbox[1]
    draw.text((x, y), text, font=font, fill=fill,
              stroke_width=stroke_w, stroke_fill=stroke_fill)


def rounded_rect_mask(size, radius, upscale=4):
    w, h = size
    mw, mh = w * upscale, h * upscale
    mask = Image.new("L", (mw, mh), 0)
    draw = ImageDraw.Draw(mask)
    draw.rounded_rectangle((0, 0, mw, mh), radius * upscale, fill=255)
    return mask.resize((w, h), Image.LANCZOS)


def apply_corner(img, radius=RADIUS):
    mask = rounded_rect_mask(img.size, radius)
    base = img.convert("RGBA")
    arr = np.array(base)
    arr[:, :, 3] = np.minimum(arr[:, :, 3], np.array(mask))
    return Image.fromarray(arr)


def make_gradient_bg_fast(size, top_color, bottom_color):
    w, h = size
    arr = np.zeros((h, w, 4), dtype=np.uint8)
    for c in range(3):
        col = np.linspace(top_color[c], bottom_color[c], h).astype(np.uint8)
        arr[:, :, c] = col[:, np.newaxis]
    arr[:, :, 3] = 255
    return Image.fromarray(arr)


def load_texture(name):
    p = TEX_DIR / name
    if p.exists():
        return Image.open(str(p)).convert("RGBA")
    return None


def composite_texture_centered(base, tex, cx, cy, target_size, alpha=120, blur=0):
    if tex is None:
        return
    tex_resized = tex.resize(target_size, Image.LANCZOS)
    if blur > 0:
        tex_resized = tex_resized.filter(ImageFilter.GaussianBlur(blur))
    arr = np.array(tex_resized)
    if arr.shape[2] == 4:
        arr[:, :, 3] = (arr[:, :, 3] * alpha // 255).clip(0, 255)
    tex_resized = Image.fromarray(arr)
    x = cx - target_size[0] // 2
    y = cy - target_size[1] // 2
    base.alpha_composite(tex_resized, (x, y))


def composite_texture_fill(base, tex, alpha=100, blur=0):
    if tex is None:
        return
    tex_resized = tex.resize(base.size, Image.LANCZOS)
    if blur > 0:
        tex_resized = tex_resized.filter(ImageFilter.GaussianBlur(blur))
    arr = np.array(tex_resized)
    if arr.shape[2] == 4:
        arr[:, :, 3] = (arr[:, :, 3] * alpha // 255).clip(0, 255)
    tex_resized = Image.fromarray(arr)
    base.alpha_composite(tex_resized)


def draw_glow_text(img, cx, cy, text, font, fill, glow_color,
                   glow_radius=12, glow_alpha=120):
    tmp = ImageDraw.Draw(img)
    bbox = tmp.textbbox((0, 0), text, font=font)
    tw, th = bbox[2] - bbox[0], bbox[3] - bbox[1]
    pad = glow_radius * 4 + 20
    layer = Image.new("RGBA", (tw + pad * 2, th + pad * 2), (0, 0, 0, 0))
    ldraw = ImageDraw.Draw(layer)
    ldraw.text((pad - bbox[0], pad - bbox[1]), text, font=font,
               fill=(glow_color[0], glow_color[1], glow_color[2], glow_alpha))
    layer = layer.filter(ImageFilter.GaussianBlur(glow_radius))
    img.alpha_composite(layer, (cx - (tw + pad * 2) // 2, cy - (th + pad * 2) // 2))
    draw = ImageDraw.Draw(img)
    x = cx - tw // 2 - bbox[0]
    y = cy - th // 2 - bbox[1]
    draw.text((x, y), text, font=font, fill=fill)


def draw_glow_ellipse(img, cx, cy, radius, color, blur_radius=20, alpha=80):
    pad = blur_radius * 3
    layer = Image.new("RGBA", (radius * 2 + pad * 2, radius * 2 + pad * 2), (0, 0, 0, 0))
    draw = ImageDraw.Draw(layer)
    draw.ellipse((pad, pad, pad + radius * 2, pad + radius * 2), fill=color)
    layer = layer.filter(ImageFilter.GaussianBlur(blur_radius))
    arr = np.array(layer)
    arr[:, :, 3] = (arr[:, :, 3] * alpha // 255).clip(0, 255)
    layer = Image.fromarray(arr)
    img.alpha_composite(layer, (cx - radius - pad, cy - radius - pad))


def draw_outer_frame(draw, W, H, color1, color2, w1=2, w2=4):
    """统一外框：细线 + 粗线双框"""
    draw.rounded_rectangle((30, 30, W - 30, H - 30), RADIUS - 3, outline=color1, width=w1)
    draw.rounded_rectangle((40, 40, W - 40, H - 40), RADIUS - 8, outline=color2, width=w2)


def draw_copyright(draw, W, H, text="MTCG · MARVEL TRADING CARD GAME", color=None):
    """统一底部版权小字"""
    if color is None:
        color = (150, 150, 150, 255)
    f = get_font(FONT_ARIAL, 13)
    draw_text_centered(draw, W // 2, H - 55, text, f, color)


def draw_mtcg_small(draw, cx, cy, color, size=36):
    """小号 MTCG logo（顶部品牌标识）"""
    draw_text_centered(draw, cx, cy, "MTCG", get_font(FONT_IMPACT, size), color,
                       stroke_w=1, stroke_fill=color)


# ========== 1. Rush Point 卡背（白色主色调） ==========

def make_rush_point_back():
    W, H = SIZE
    img = Image.new("RGBA", (W, H), WHITE)
    draw = ImageDraw.Draw(img, "RGBA")

    tex_diamond = load_texture("_tex_diamond.png")

    # 1. 背景：极淡钻石纹理
    composite_texture_fill(img, tex_diamond, alpha=20, blur=1)

    # 2. 外框
    draw_outer_frame(draw, W, H, GOLD, (220, 220, 220, 255), w1=2, w2=3)

    # 3. 顶部小 MTCG
    draw_mtcg_small(draw, W // 2, 100, DARK, size=40)

    # 4. 顶部小装饰线
    draw.line((180, 140, W // 2 - 20, 140), fill=(200, 200, 200, 255), width=1)
    draw.line((W // 2 + 20, 140, W - 180, 140), fill=(200, 200, 200, 255), width=1)
    draw.polygon([(W // 2 - 6, 140), (W // 2, 134),
                  (W // 2 + 6, 140), (W // 2, 146)], fill=GOLD)

    # 5. 中心主视觉：大型 RP 圆形徽章（占中心 40%）
    cx, cy = W // 2, 480
    # 外环（金色粗）
    draw.ellipse((cx - 130, cy - 130, cx + 130, cy + 130), outline=GOLD, width=4)
    # 中环（灰色细）
    draw.ellipse((cx - 115, cy - 115, cx + 115, cy + 115), outline=(180, 180, 180, 255), width=1)
    # 内圆白底
    draw.ellipse((cx - 100, cy - 100, cx + 100, cy + 100), fill=(250, 250, 250, 255))
    # 红色内圈
    draw.ellipse((cx - 85, cy - 85, cx + 85, cy + 85), fill=RED)
    # 中心 RP 大字
    draw_text_centered(draw, cx, cy, "RP", get_font(FONT_IMPACT, 80), WHITE)

    # 6. 下方：RUSH POINT 标题
    draw_text_centered(draw, W // 2, 680, "RUSH POINT",
                       get_font(FONT_IMPACT, 42), DARK)

    # 7. 副标题
    draw_text_centered(draw, W // 2, 725, "SCORE CARD",
                       get_font(FONT_ARIAL, 20), (150, 150, 150, 255))

    # 8. 9 点计分刻度（超英击战 9 点规则）
    dot_y = 810
    n_dots = 9
    margin = 100
    spacing = (W - margin * 2) // (n_dots - 1)
    r = 9
    for i in range(n_dots):
        dx = margin + i * spacing
        draw.ellipse((dx - r, dot_y - r, dx + r, dot_y + r),
                     fill=WHITE, outline=GOLD, width=2)
        draw.ellipse((dx - 3, dot_y - 3, dx + 3, dot_y + 3), fill=RED)
    draw.line((margin, dot_y, W - margin, dot_y), fill=(200, 200, 200, 255), width=1)

    # 9. 底部版权
    draw_copyright(draw, W, H, color=(160, 160, 160, 255))

    return apply_corner(img)


# ========== 2. 角色卡卡背（黑色主色调） ==========

def make_character_back():
    W, H = SIZE
    img = Image.new("RGBA", (W, H), BLACK)
    draw = ImageDraw.Draw(img, "RGBA")

    tex_burst = load_texture("_tex_burst.png")

    # 1. 背景：爆炸光芒（极低透明度）
    composite_texture_centered(img, tex_burst, W // 2, 500, (800, 800), alpha=35, blur=3)

    # 2. 外框
    draw_outer_frame(draw, W, H, RED, (60, 60, 60, 255), w1=2, w2=3)

    # 3. 顶部小 MTCG（白色发光）
    draw_glow_text(img, W // 2, 110, "MTCG", get_font(FONT_IMPACT, 40),
                   WHITE, RED, glow_radius=12, glow_alpha=80)

    # 4. 分隔线
    draw.line((180, 155, W // 2 - 20, 155), fill=(60, 60, 60, 255), width=1)
    draw.line((W // 2 + 20, 155, W - 180, 155), fill=(60, 60, 60, 255), width=1)
    draw.polygon([(W // 2 - 6, 155), (W // 2, 149),
                  (W // 2 + 6, 155), (W // 2, 161)], fill=RED)

    # 5. 中心主视觉：大型 C 圆形徽章（角色卡标志）
    cx, cy = W // 2, 500
    # 红色光晕
    draw_glow_ellipse(img, cx, cy, 160, (200, 30, 50, 255), blur_radius=40, alpha=40)
    # 灰色模糊背景圆
    bg = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    bdraw = ImageDraw.Draw(bg)
    bdraw.ellipse((cx - 140, cy - 170, cx + 140, cy + 170), fill=(25, 25, 25, 100))
    bg = bg.filter(ImageFilter.GaussianBlur(20))
    img.alpha_composite(bg)
    # 双环
    d2 = ImageDraw.Draw(img, "RGBA")
    d2.ellipse((cx - 130, cy - 130, cx + 130, cy + 130), outline=(80, 80, 80, 200), width=2)
    d2.ellipse((cx - 118, cy - 118, cx + 118, cy + 118), outline=(50, 50, 50, 150), width=1)
    # 中心大 C 字母
    draw_text_centered(d2, cx, cy, "C", get_font(FONT_IMPACT, 140), (70, 70, 70, 220))

    # 6. 下方标题
    draw_text_centered(draw, W // 2, 700, "CHARACTER",
                       get_font(FONT_IMPACT, 38), (180, 30, 50, 255))

    # 7. 副标题
    draw_text_centered(draw, W // 2, 745, "HERO CARD",
                       get_font(FONT_ARIAL, 18), (100, 100, 100, 255))

    # 8. 底部装饰线
    draw.line((180, 830, W - 180, 830), fill=(50, 50, 50, 255), width=1)

    # 9. 底部版权
    draw_copyright(draw, W, H, color=(80, 80, 80, 255))

    return apply_corner(img)


# ========== 3. 先后卡卡背（红色主色调） ==========

def make_order_back():
    W, H = SIZE
    # 深红渐变背景（和 First 卡面的亮红区分）
    img = make_gradient_bg_fast(SIZE, (90, 5, 18), (50, 0, 10))
    draw = ImageDraw.Draw(img, "RGBA")

    tex_halftone = load_texture("_tex_halftone.png")

    # 1. 背景：半色调网点（低透明度，金色调）
    composite_texture_centered(img, tex_halftone, W // 2, H // 2, (900, 1200), alpha=25, blur=2)

    # 2. 外框：金色 + 暗红双线
    draw_outer_frame(draw, W, H, GOLD, (120, 10, 25, 255), w1=2, w2=3)

    # 3. 顶部小 MTCG（金色）
    draw_mtcg_small(draw, W // 2, 100, GOLD, size=40)

    # 4. 分隔线
    draw.line((180, 140, W // 2 - 20, 140), fill=(212, 175, 55, 100), width=1)
    draw.line((W // 2 + 20, 140, W - 180, 140), fill=(212, 175, 55, 100), width=1)
    draw.polygon([(W // 2 - 6, 140), (W // 2, 134),
                  (W // 2 + 6, 140), (W // 2, 146)], fill=GOLD)

    # 5. 中心主视觉：盾牌徽章（先后顺序符号）
    cx, cy = W // 2, 500
    draw_glow_ellipse(img, cx, cy, 140, (212, 175, 55, 255), blur_radius=25, alpha=25)
    # 盾牌（金色边）
    shield = [(cx, cy - 110), (cx + 95, cy - 85), (cx + 95, cy + 15),
              (cx, cy + 110), (cx - 95, cy + 15), (cx - 95, cy - 85)]
    draw.polygon(shield, fill=GOLD)
    inner = [(cx, cy - 90), (cx + 78, cy - 70), (cx + 78, cy + 10),
             (cx, cy + 90), (cx - 78, cy + 10), (cx - 78, cy - 70)]
    draw.polygon(inner, fill=(50, 0, 10, 255))
    # 上下双箭头（金色）
    draw.polygon([(cx, cy - 55), (cx - 22, cy - 22), (cx - 9, cy - 22),
                  (cx - 9, cy + 5), (cx + 9, cy + 5),
                  (cx + 9, cy - 22), (cx + 22, cy - 22)], fill=GOLD)
    draw.polygon([(cx, cy + 55), (cx + 22, cy + 22), (cx + 9, cy + 22),
                  (cx + 9, cy - 5), (cx - 9, cy - 5),
                  (cx - 9, cy + 22), (cx - 22, cy + 22)], fill=GOLD)

    # 6. 下方标题（金色）
    draw_text_centered(draw, W // 2, 700, "ORDER",
                       get_font(FONT_IMPACT, 42), GOLD)

    # 7. 副标题
    draw_text_centered(draw, W // 2, 745, "ORDER OF PLAY",
                       get_font(FONT_ARIAL, 18), (180, 140, 40, 255))

    # 8. 底部装饰线
    draw.line((180, 830, W - 180, 830), fill=(212, 175, 55, 60), width=1)

    # 9. 底部版权
    draw_copyright(draw, W, H, color=(180, 140, 40, 180))

    return apply_corner(img)


# ========== 4. First 先后卡卡面 ==========

def make_order_first():
    W, H = SIZE
    img = make_gradient_bg_fast(SIZE, (215, 38, 61), (140, 10, 25))
    draw = ImageDraw.Draw(img, "RGBA")

    tex_halftone = load_texture("_tex_halftone.png")

    # 1. 背景半色调（极淡）
    composite_texture_centered(img, tex_halftone, W // 2, 420, (700, 900), alpha=25, blur=4)

    # 2. 外框
    draw_outer_frame(draw, W, H, WHITE, (255, 200, 210, 100), w1=3, w2=2)

    # 3. 顶部小 MTCG
    draw_mtcg_small(draw, W // 2, 95, (255, 220, 225, 255), size=32)

    # 4. 分隔线
    draw.line((200, 130, W // 2 - 15, 130), fill=(255, 255, 255, 80), width=1)
    draw.line((W // 2 + 15, 130, W - 200, 130), fill=(255, 255, 255, 80), width=1)
    draw.polygon([(W // 2 - 5, 130), (W // 2, 125),
                  (W // 2 + 5, 130), (W // 2, 135)], fill=WHITE)

    # 5. 中心主视觉：巨大数字 "1"（占 45%）
    cy_num = 420
    draw_glow_text(img, W // 2, cy_num, "1", get_font(FONT_IMPACT, 360),
                   WHITE, (255, 255, 255, 255), glow_radius=35, glow_alpha=50)
    draw = ImageDraw.Draw(img, "RGBA")
    draw_text_centered(draw, W // 2, cy_num, "1", get_font(FONT_IMPACT, 360),
                       WHITE, stroke_w=4, stroke_fill=RED)

    # 6. 下方 FIRST
    draw_glow_text(img, W // 2, 700, "FIRST", get_font(FONT_IMPACT, 65),
                   WHITE, (255, 200, 200, 255), glow_radius=15, glow_alpha=80)

    # 7. 底部说明
    draw.line((180, 800, W - 180, 800), fill=(255, 255, 255, 50), width=1)
    draw_text_centered(draw, W // 2, 840, "FIRST HAND",
                       get_font(FONT_IMPACT, 24), (255, 220, 225, 255))
    draw_text_centered(draw, W // 2, 880, "先手 · 先攻 · 先出牌",
                       get_font(FONT_SIMHEI, 18), (255, 220, 225, 200))

    # 8. 底部版权
    draw_copyright(draw, W, H, color=(255, 200, 200, 180))

    return apply_corner(img)


# ========== 5. Second 先后卡卡面 ==========

def make_order_second():
    W, H = SIZE
    img = make_gradient_bg_fast(SIZE, (17, 17, 17), (102, 7, 7))
    draw = ImageDraw.Draw(img, "RGBA")

    tex_burst = load_texture("_tex_burst.png")

    # 1. 背景：爆炸光芒
    composite_texture_centered(img, tex_burst, W // 2, 420, (700, 900), alpha=30, blur=4)

    # 2. 外框
    draw_outer_frame(draw, W, H, RED, (100, 10, 20, 255), w1=3, w2=2)

    # 3. 顶部小 MTCG
    draw_mtcg_small(draw, W // 2, 95, (200, 60, 80, 255), size=32)

    # 4. 分隔线
    draw.line((200, 130, W // 2 - 15, 130), fill=(200, 30, 50, 120), width=1)
    draw.line((W // 2 + 15, 130, W - 200, 130), fill=(200, 30, 50, 120), width=1)
    draw.polygon([(W // 2 - 5, 130), (W // 2, 125),
                  (W // 2 + 5, 130), (W // 2, 135)], fill=RED)

    # 5. 中心主视觉：巨大数字 "2"
    cy_num = 420
    draw_glow_text(img, W // 2, cy_num, "2", get_font(FONT_IMPACT, 360),
                   RED, (200, 30, 50, 255), glow_radius=40, glow_alpha=80)
    draw = ImageDraw.Draw(img, "RGBA")
    draw_text_centered(draw, W // 2, cy_num, "2", get_font(FONT_IMPACT, 360),
                       RED, stroke_w=3, stroke_fill=WHITE)

    # 6. 下方 SECOND
    draw_glow_text(img, W // 2, 700, "SECOND", get_font(FONT_IMPACT, 55),
                   RED, (200, 30, 50, 255), glow_radius=15, glow_alpha=80)

    # 7. 底部说明
    draw.line((180, 800, W - 180, 800), fill=(200, 30, 50, 80), width=1)
    draw_text_centered(draw, W // 2, 840, "SECOND HAND",
                       get_font(FONT_IMPACT, 24), (255, 180, 180, 255))
    draw_text_centered(draw, W // 2, 880, "后手 · 后攻 · 后出牌",
                       get_font(FONT_SIMHEI, 18), (255, 180, 180, 200))

    # 8. 底部版权
    draw_copyright(draw, W, H, color=(255, 180, 180, 180))

    return apply_corner(img)


def main():
    OUT_DIR.mkdir(parents=True, exist_ok=True)
    jobs = [
        (make_rush_point_back, "card_back_rush.png"),
        (make_character_back, "card_back_character.png"),
        (make_order_back, "card_back_order.png"),
        (make_order_first, "order_first.png"),
        (make_order_second, "order_second.png"),
    ]
    for fn, name in jobs:
        print(f"生成 {name} ...", end=" ")
        img = fn()
        img.save(str(OUT_DIR / name), "PNG", optimize=True)
        print(f"OK ({img.size[0]}x{img.size[1]})")
    print(f"\n全部完成，输出目录: {OUT_DIR}")


if __name__ == "__main__":
    main()
