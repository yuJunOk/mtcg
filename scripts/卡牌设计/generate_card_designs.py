#!/usr/bin/env python3
"""
MTCG 卡牌设计 v5 — 清爽大气全新设计

设计理念：
  - 每张卡背有独立色彩主题，金色为统一点缀色
  - 几何图案为主视觉，简洁有力
  - 大量留白，克制装饰
  - 先后卡面用冷暖对比区分

输出：747×1042 圆角 R41 透明 PNG
"""

from pathlib import Path
import math
import numpy as np
from PIL import Image, ImageDraw, ImageFilter, ImageFont

# ===== 常量 =====
SIZE = (747, 1042)
RADIUS = 41
OUT_DIR = Path(r"d:\pengYuJun\Project\mtcg\assets\card\designs")
TEX_DIR = Path(__file__).parent / "tex"

# 字体
FONT_IMPACT = "C:/Windows/Fonts/impact.ttf"
FONT_ARIAL = "C:/Windows/Fonts/arialbd.ttf"
FONT_SIMHEI = "C:/Windows/Fonts/simhei.ttf"

# 通用色板
GOLD = (201, 168, 76, 255)          # 优雅金
GOLD_DIM = (160, 130, 55, 255)       # 暗金
GOLD_LIGHT = (220, 195, 120, 255)    # 淡金
SILVER = (192, 196, 204, 255)        # 银
SILVER_DIM = (150, 155, 165, 255)    # 暗银
WHITE = (255, 255, 255, 255)
BLACK = (10, 10, 10, 255)

# 主题色 —— 每张卡背独立
NAVY_TOP = (11, 26, 48)              # 计分卡 深海蓝
NAVY_BOT = (22, 38, 62)
CHARCOAL_TOP = (26, 26, 28)          # 角色卡 曜石黑
CHARCOAL_BOT = (14, 14, 16)
BURGUNDY_TOP = (31, 12, 20)          # 先后卡背 勃艮第红
BURGUNDY_BOT = (45, 17, 28)
CREAM_TOP = (250, 248, 242)          # 先手卡面 象牙白
CREAM_BOT = (238, 230, 208)
SLATE_TOP = (28, 30, 35)             # 后手卡面 暗夜灰
SLATE_BOT = (42, 45, 53)


# ===== 工具函数 =====

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


def make_gradient_bg(size, top_color, bottom_color):
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


def composite_texture_fill(base, tex, alpha=100, blur=0):
    if tex is None:
        return
    tex_resized = tex.resize(base.size, Image.LANCZOS)
    if blur > 0:
        tex_resized = tex_resized.filter(ImageFilter.GaussianBlur(blur))
    arr = np.array(tex_resized)
    if arr.shape[2] == 4:
        arr[:, :, 3] = (arr[:, :, 3] * alpha // 255).clip(0, 255)
    base.alpha_composite(Image.fromarray(arr))


def composite_texture_centered(base, tex, cx, cy, target_w, target_h, alpha=120, blur=0):
    if tex is None:
        return
    tex_resized = tex.resize((target_w, target_h), Image.LANCZOS)
    if blur > 0:
        tex_resized = tex_resized.filter(ImageFilter.GaussianBlur(blur))
    arr = np.array(tex_resized)
    if arr.shape[2] == 4:
        arr[:, :, 3] = (arr[:, :, 3] * alpha // 255).clip(0, 255)
    base.alpha_composite(Image.fromarray(arr), (cx - target_w // 2, cy - target_h // 2))


def draw_glow_ellipse(img, cx, cy, radius, color, blur_radius=20, alpha=80):
    pad = blur_radius * 3
    size = radius * 2 + pad * 2
    layer = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(layer)
    draw.ellipse((pad, pad, pad + radius * 2, pad + radius * 2), fill=color)
    layer = layer.filter(ImageFilter.GaussianBlur(blur_radius))
    arr = np.array(layer)
    arr[:, :, 3] = (arr[:, :, 3] * alpha // 255).clip(0, 255)
    img.alpha_composite(Image.fromarray(arr), (cx - radius - pad, cy - radius - pad))


def draw_glow_text(img, cx, cy, text, font, fill, glow_color, glow_radius=12, glow_alpha=120):
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


# ===== 装饰元素绘制 =====

def draw_elegant_border(draw, W, H, color, inner_color=None):
    """细双线框 —— 优雅克制"""
    draw.rounded_rectangle((28, 28, W - 28, H - 28), RADIUS - 3,
                           outline=(color[0], color[1], color[2], 100), width=1)
    inner = inner_color or color
    draw.rounded_rectangle((38, 38, W - 38, H - 38), RADIUS - 8,
                           outline=inner, width=2)


def draw_mtcg_top(draw, W, color, size=34):
    """顶部小号 MTCG 标识"""
    draw_text_centered(draw, W // 2, 90, "MTCG",
                       get_font(FONT_IMPACT, size), color)


def draw_divider(draw, W, y, color, has_diamond=True):
    """分隔线 + 菱形点缀"""
    alpha = (color[0], color[1], color[2], 80)
    draw.line((160, y, W // 2 - 18, y), fill=alpha, width=1)
    draw.line((W // 2 + 18, y, W - 160, y), fill=alpha, width=1)
    if has_diamond:
        sz = 5
        draw.polygon([(W // 2 - sz, y), (W // 2, y - sz),
                      (W // 2 + sz, y), (W // 2, y + sz)], fill=color)


def draw_copyright(draw, W, H, text="MTCG · MARVEL TRADING CARD GAME", color=None):
    if color is None:
        color = (140, 140, 140, 255)
    f = get_font(FONT_ARIAL, 12)
    draw_text_centered(draw, W // 2, H - 50, text, f, color)


def draw_corner_brackets(draw, W, H, color, inset=60, length=50, width=2):
    """四角 L 形装饰 —— 像画框角码"""
    pts = [
        (inset, inset), (W - inset, inset),
        (inset, H - inset), (W - inset, H - inset),
    ]
    for px, py in pts:
        dx = length if px == inset else -length
        dy = length if py == inset else -length
        # 水平线
        draw.line((px, py, px + dx, py), fill=color, width=width)
        # 垂直线
        draw.line((px, py, px, py + dy), fill=color, width=width)


def draw_concentric_rings(draw, cx, cy, outer_r, inner_r, color, tick_count=12, tick_len=8):
    """同心圆环 + 刻度线 —— 用于计分卡"""
    # 外圈
    draw.ellipse((cx - outer_r, cy - outer_r, cx + outer_r, cy + outer_r),
                 outline=color, width=2)
    # 内圈
    draw.ellipse((cx - inner_r, cy - inner_r, cx + inner_r, cy + inner_r),
                 outline=(color[0], color[1], color[2], 120), width=1)
    # 刻度线
    for i in range(tick_count):
        angle = math.radians(i * 360 / tick_count - 90)
        x1 = cx + (outer_r - 2) * math.cos(angle)
        y1 = cy + (outer_r - 2) * math.sin(angle)
        x2 = cx + (outer_r - tick_len) * math.cos(angle)
        y2 = cy + (outer_r - tick_len) * math.sin(angle)
        draw.line((x1, y1, x2, y2), fill=color, width=1)


def draw_compass_cross(draw, cx, cy, arm_len, color, width=2):
    """罗盘十字 —— 用于先后卡背"""
    # 四个方向臂
    draw.line((cx, cy - arm_len, cx, cy + arm_len), fill=color, width=width)
    draw.line((cx - arm_len, cy, cx + arm_len, cy), fill=color, width=width)
    # 对角细线
    diag = int(arm_len * 0.6)
    alpha = (color[0], color[1], color[2], 80)
    draw.line((cx - diag, cy - diag, cx + diag, cy + diag), fill=alpha, width=1)
    draw.line((cx + diag, cy - diag, cx - diag, cy + diag), fill=alpha, width=1)
    # 端点小圆
    dot_r = 4
    for angle in [0, 90, 180, 270]:
        rad = math.radians(angle)
        dx = int(arm_len * math.cos(rad))
        dy = int(arm_len * math.sin(rad))
        draw.ellipse((cx + dx - dot_r, cy + dy - dot_r,
                      cx + dx + dot_r, cy + dy + dot_r), fill=color)


# ===== 1. 计分卡卡背 —— 深海蓝 + 金 =====

def make_rush_point_back():
    W, H = SIZE
    img = make_gradient_bg(SIZE, NAVY_TOP, NAVY_BOT)
    draw = ImageDraw.Draw(img, "RGBA")

    # 纹理：极淡钻石纹理
    tex_diamond = load_texture("_tex_diamond.png")
    composite_texture_fill(img, tex_diamond, alpha=12, blur=1)

    # 边框
    draw_elegant_border(draw, W, H, GOLD)

    # 顶部 MTCG
    draw_mtcg_top(draw, W, GOLD, size=34)

    # 分隔线
    draw_divider(draw, W, 130, GOLD)

    # 中心主视觉：同心圆环 + RP
    cx, cy = W // 2, 475
    draw_glow_ellipse(img, cx, cy, 150, (201, 168, 76, 255), blur_radius=30, alpha=20)
    draw_concentric_rings(draw, cx, cy, 145, 125, GOLD, tick_count=12, tick_len=8)
    # RP 文字
    draw_text_centered(draw, cx, cy, "RP", get_font(FONT_IMPACT, 85), GOLD)

    # 标题
    draw_text_centered(draw, W // 2, 700, "SCORE CARD",
                       get_font(FONT_IMPACT, 36), GOLD)

    # 副标题
    draw_text_centered(draw, W // 2, 742, "RUSH POINT",
                       get_font(FONT_ARIAL, 17), (201, 168, 76, 160))

    # 9 点计分刻度
    dot_y = 820
    n_dots = 9
    margin = 120
    spacing = (W - margin * 2) // (n_dots - 1)
    r = 7
    line_color = (201, 168, 76, 60)
    draw.line((margin, dot_y, W - margin, dot_y), fill=line_color, width=1)
    for i in range(n_dots):
        dx = margin + i * spacing
        draw.ellipse((dx - r, dot_y - r, dx + r, dot_y + r),
                     fill=GOLD, outline=(201, 168, 76, 180), width=1)

    draw_copyright(draw, W, H, color=(140, 160, 180, 180))
    return apply_corner(img)


# ===== 2. 角色卡卡背 —— 曜石黑 + 金 =====

def make_character_back():
    W, H = SIZE
    img = make_gradient_bg(SIZE, CHARCOAL_TOP, CHARCOAL_BOT)
    draw = ImageDraw.Draw(img, "RGBA")

    # 纹理：极淡爆炸光芒（中心）
    tex_burst = load_texture("_tex_burst.png")
    composite_texture_centered(img, tex_burst, W // 2, 480, 700, 700, alpha=18, blur=3)

    # 边框
    draw_elegant_border(draw, W, H, GOLD)

    # 四角 L 形装饰
    draw_corner_brackets(draw, W, H, GOLD, inset=70, length=55, width=2)

    # 顶部 MTCG
    draw_mtcg_top(draw, W, GOLD, size=34)

    # 分隔线
    draw_divider(draw, W, 130, GOLD)

    # 中心主视觉：金色圆环 + C
    cx, cy = W // 2, 485
    draw_glow_ellipse(img, cx, cy, 140, (201, 168, 76, 255), blur_radius=35, alpha=22)
    # 外圈
    draw.ellipse((cx - 140, cy - 140, cx + 140, cy + 140),
                 outline=GOLD, width=2)
    # 内圈
    draw.ellipse((cx - 122, cy - 122, cx + 122, cy + 122),
                 outline=(201, 168, 76, 100), width=1)
    # C 字母
    draw_text_centered(draw, cx, cy, "C", get_font(FONT_IMPACT, 135), GOLD)

    # 标题
    draw_text_centered(draw, W // 2, 715, "CHARACTER",
                       get_font(FONT_IMPACT, 34), GOLD)

    # 副标题
    draw_text_centered(draw, W // 2, 758, "HERO CARD",
                       get_font(FONT_ARIAL, 16), (201, 168, 76, 140))

    draw_copyright(draw, W, H, color=(100, 100, 100, 180))
    return apply_corner(img)


# ===== 3. 先后卡卡背 —— 勃艮第红 + 金 =====

def make_order_back():
    W, H = SIZE
    img = make_gradient_bg(SIZE, BURGUNDY_TOP, BURGUNDY_BOT)
    draw = ImageDraw.Draw(img, "RGBA")

    # 纹理：极淡半色调
    tex_halftone = load_texture("_tex_halftone.png")
    composite_texture_centered(img, tex_halftone, W // 2, H // 2, 800, 1100, alpha=14, blur=2)

    # 边框
    draw_elegant_border(draw, W, H, GOLD)

    # 顶部 MTCG
    draw_mtcg_top(draw, W, GOLD, size=34)

    # 分隔线
    draw_divider(draw, W, 130, GOLD)

    # 中心主视觉：罗盘十字 + O
    cx, cy = W // 2, 485
    draw_glow_ellipse(img, cx, cy, 130, (201, 168, 76, 255), blur_radius=25, alpha=18)
    draw_compass_cross(draw, cx, cy, 130, GOLD, width=2)
    # 中心圆
    draw.ellipse((cx - 50, cy - 50, cx + 50, cy + 50),
                 outline=GOLD, width=2)
    draw.ellipse((cx - 42, cy - 42, cx + 42, cy + 42),
                 fill=(35, 14, 22, 255), outline=(201, 168, 76, 80), width=1)
    # O 字母
    draw_text_centered(draw, cx, cy, "O", get_font(FONT_IMPACT, 62), GOLD)

    # 标题
    draw_text_centered(draw, W // 2, 715, "ORDER",
                       get_font(FONT_IMPACT, 38), GOLD)

    # 副标题
    draw_text_centered(draw, W // 2, 758, "ORDER OF PLAY",
                       get_font(FONT_ARIAL, 16), (201, 168, 76, 140))

    draw_copyright(draw, W, H, color=(180, 140, 70, 160))
    return apply_corner(img)


# ===== 4. 先手卡面 —— 象牙白 + 暗金 =====

def make_order_first():
    W, H = SIZE
    img = make_gradient_bg(SIZE, CREAM_TOP, CREAM_BOT)
    draw = ImageDraw.Draw(img, "RGBA")

    # 边框
    draw_elegant_border(draw, W, H, GOLD_DIM)

    # 顶部 MTCG
    draw_mtcg_top(draw, W, GOLD_DIM, size=30)

    # 分隔线
    draw_divider(draw, W, 125, GOLD_DIM)

    # 中心主视觉：巨大 "1"
    cy_num = 440
    num_color = (120, 85, 20, 255)     # 深暗金
    draw_glow_text(img, W // 2, cy_num, "1", get_font(FONT_IMPACT, 380),
                   num_color, (200, 180, 130, 255), glow_radius=30, glow_alpha=35)
    draw = ImageDraw.Draw(img, "RGBA")
    draw_text_centered(draw, W // 2, cy_num, "1", get_font(FONT_IMPACT, 380),
                       num_color, stroke_w=3, stroke_fill=GOLD_DIM)

    # FIRST 标题
    draw_text_centered(draw, W // 2, 720, "FIRST",
                       get_font(FONT_IMPACT, 58), GOLD_DIM)

    # 底部说明
    draw.line((180, 800, W - 180, 800), fill=(139, 105, 20, 60), width=1)
    draw_text_centered(draw, W // 2, 845, "FIRST HAND",
                       get_font(FONT_IMPACT, 22), (139, 105, 20, 200))
    draw_text_centered(draw, W // 2, 885, "先手 · 先攻 · 先出牌",
                       get_font(FONT_SIMHEI, 17), (139, 105, 20, 180))

    draw_copyright(draw, W, H, color=(160, 140, 100, 180))
    return apply_corner(img)


# ===== 5. 后手卡面 —— 暗夜灰 + 银 =====

def make_order_second():
    W, H = SIZE
    img = make_gradient_bg(SIZE, SLATE_TOP, SLATE_BOT)
    draw = ImageDraw.Draw(img, "RGBA")

    # 边框
    draw_elegant_border(draw, W, H, SILVER, inner_color=SILVER_DIM)

    # 顶部 MTCG
    draw_mtcg_top(draw, W, SILVER, size=30)

    # 分隔线
    draw_divider(draw, W, 125, SILVER)

    # 中心主视觉：巨大 "2"
    cy_num = 440
    draw_glow_text(img, W // 2, cy_num, "2", get_font(FONT_IMPACT, 380),
                   SILVER, (192, 196, 204, 255), glow_radius=35, glow_alpha=45)
    draw = ImageDraw.Draw(img, "RGBA")
    draw_text_centered(draw, W // 2, cy_num, "2", get_font(FONT_IMPACT, 380),
                       SILVER, stroke_w=3, stroke_fill=SILVER_DIM)

    # SECOND 标题
    draw_text_centered(draw, W // 2, 720, "SECOND",
                       get_font(FONT_IMPACT, 50), SILVER)

    # 底部说明
    draw.line((180, 800, W - 180, 800), fill=(192, 196, 204, 60), width=1)
    draw_text_centered(draw, W // 2, 845, "SECOND HAND",
                       get_font(FONT_IMPACT, 22), (192, 196, 204, 200))
    draw_text_centered(draw, W // 2, 885, "后手 · 后攻 · 后出牌",
                       get_font(FONT_SIMHEI, 17), (192, 196, 204, 180))

    draw_copyright(draw, W, H, color=(160, 165, 175, 180))
    return apply_corner(img)


# ===== 主入口 =====

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