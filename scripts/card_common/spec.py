# -*- coding: utf-8 -*-
"""卡面资源统一规格（官网拉取 / 截图提取共用）。"""

# 画布尺寸：官网 CDN 去透明边后约 1489×2080，取偶数便于缩放
CARD_WIDTH = 1488
CARD_HEIGHT = 2080

# 圆角：截图提取标定 radius=41 @ ~747 宽 → 比例约 0.0549
CORNER_RADIUS_RATIO = 41 / 747

# alpha 低于此值视为透明边（去边时忽略）
ALPHA_THRESHOLD = 8

# 输出
OUTPUT_FORMAT = "PNG"
OUTPUT_OPTIMIZE = True
