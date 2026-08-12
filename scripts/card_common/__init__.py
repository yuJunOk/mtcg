# -*- coding: utf-8 -*-
"""卡面公共模块：统一规格与规范化。"""

from .normalize import (
    apply_rounded_corners,
    corner_radius_for,
    fit_to_canvas,
    is_normalized,
    normalize_card_file,
    normalize_card_image,
    trim_transparent,
)
from .spec import (
    ALPHA_THRESHOLD,
    CARD_HEIGHT,
    CARD_WIDTH,
    CORNER_RADIUS_RATIO,
)

__all__ = [
    "ALPHA_THRESHOLD",
    "CARD_HEIGHT",
    "CARD_WIDTH",
    "CORNER_RADIUS_RATIO",
    "apply_rounded_corners",
    "corner_radius_for",
    "fit_to_canvas",
    "is_normalized",
    "normalize_card_file",
    "normalize_card_image",
    "trim_transparent",
]
