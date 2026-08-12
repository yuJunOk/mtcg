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
from .seed_sql import (
    CARD_TYPE_MAP,
    COLOR_MAP,
    build_monolith_sql,
    card_insert_sql,
    image_rel_path,
    merge_cards,
    merge_products,
    normalize_seed_card,
    product_insert_sql,
    rebuild_merged_seed,
    sql_num,
    sql_quote,
    write_seed_by_product,
    write_seed_bundle,
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
    "CARD_TYPE_MAP",
    "CARD_WIDTH",
    "COLOR_MAP",
    "CORNER_RADIUS_RATIO",
    "apply_rounded_corners",
    "build_monolith_sql",
    "card_insert_sql",
    "corner_radius_for",
    "fit_to_canvas",
    "image_rel_path",
    "is_normalized",
    "merge_cards",
    "merge_products",
    "normalize_card_file",
    "normalize_card_image",
    "normalize_seed_card",
    "product_insert_sql",
    "rebuild_merged_seed",
    "sql_num",
    "sql_quote",
    "trim_transparent",
    "write_seed_bundle",
    "write_seed_by_product",
]
