# -*- coding: utf-8 -*-
"""卡牌/产品幂等种子 SQL 生成（官网拉取与截图补齐共用）。"""
from __future__ import annotations

from collections import defaultdict
from pathlib import Path
from typing import Any


COLOR_MAP = {
    "红色": "RED",
    "黄色": "YELLOW",
    "蓝色": "BLUE",
    "绿色": "GREEN",
    "橙色": "ORANGE",
    "紫色": "PURPLE",
}

CARD_TYPE_MAP = {
    "character": "CHARACTER",
    "rush_point": "RUSH_POINT",
    "rush": "RUSH_POINT",
}


def sql_quote(value: str | None) -> str:
    if value is None:
        return "NULL"
    text = str(value).replace("\r\n", "\n").replace("\r", "\n").replace("\n", " ")
    return "'" + text.replace("'", "''") + "'"


def sql_num(value: int | None) -> str:
    return "NULL" if value is None else str(int(value))


def image_rel_path(product_code: str, card_code: str) -> str:
    return f"card/faces/{product_code}/{card_code}.png"


def ensure_card_code(card: dict[str, Any]) -> str:
    if card.get("card_code"):
        return str(card["card_code"])
    base = card.get("base_card_no")
    rarity = card.get("rarity")
    if not base or not rarity:
        raise ValueError(f"卡牌缺少 card_code / base_card_no+rarity: {card}")
    return f"{base}-{rarity}"


def ensure_product_code(card: dict[str, Any]) -> str:
    if card.get("product_code"):
        return str(card["product_code"])
    base = str(card.get("base_card_no") or "")
    if "-" not in base:
        raise ValueError(f"无法从卡牌推断 product_code: {card}")
    return base.split("-", 1)[0]


def normalize_seed_card(card: dict[str, Any]) -> dict[str, Any]:
    """统一为 SQL 所需字段。"""
    card_code = ensure_card_code(card)
    product_code = ensure_product_code(card)
    rarity = str(card.get("rarity") or card_code.rsplit("-", 1)[-1]).upper()
    base = card.get("base_card_no") or card_code[: -(len(rarity) + 1)]
    return {
        "card_code": card_code,
        "base_card_no": base,
        "product_code": product_code,
        "card_name": card.get("card_name"),
        "card_type": card.get("card_type") or "CHARACTER",
        "level": card.get("level"),
        "color": card.get("color"),
        "environment": card.get("environment"),
        "traits": card.get("traits"),
        "attack_range": card.get("attack_range"),
        "power": card.get("power"),
        "rarity": rarity,
        "effect_text": card.get("effect_text"),
        "effect_json": card.get("effect_json"),
        "image_path": card.get("image_path") or image_rel_path(product_code, card_code),
    }


def product_insert_sql(product: dict[str, Any]) -> str:
    code = product["product_code"]
    return (
        "INSERT INTO mtcg_product (product_code, product_name, release_date, description)\n"
        f"SELECT {sql_quote(code)}, {sql_quote(product.get('product_name'))}, "
        f"{sql_quote(product.get('release_date'))}, {sql_quote(product.get('description'))}\n"
        f"WHERE NOT EXISTS (SELECT 1 FROM mtcg_product WHERE product_code = {sql_quote(code)});"
    )


def card_insert_sql(card: dict[str, Any]) -> str:
    c = normalize_seed_card(card)
    return (
        "INSERT INTO mtcg_card (\n"
        "    card_code, product_code, card_name, card_type, level, color,\n"
        "    environment, traits, attack_range, power, rarity,\n"
        "    effect_text, effect_json, image_path\n"
        ")\n"
        "SELECT\n"
        f"    {sql_quote(c['card_code'])},\n"
        f"    {sql_quote(c['product_code'])},\n"
        f"    {sql_quote(c['card_name'])},\n"
        f"    {sql_quote(c['card_type'])},\n"
        f"    {sql_num(c['level'])},\n"
        f"    {sql_quote(c['color'])},\n"
        f"    {sql_quote(c['environment'])},\n"
        f"    {sql_quote(c['traits'])},\n"
        f"    {sql_num(c['attack_range'])},\n"
        f"    {sql_num(c['power'])},\n"
        f"    {sql_quote(c['rarity'])},\n"
        f"    {sql_quote(c['effect_text'])},\n"
        f"    {sql_quote(c['effect_json'])},\n"
        f"    {sql_quote(c['image_path'])}\n"
        f"WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = {sql_quote(c['card_code'])});"
    )


def build_monolith_sql(
    products: list[dict[str, Any]],
    cards: list[dict[str, Any]],
    *,
    header_lines: list[str] | None = None,
) -> str:
    lines: list[str] = list(
        header_lines
        or [
            "-- 卡牌种子数据（自动生成）",
            "-- card_code 格式: {编号}-{罕度}，如 BP01-001-MR",
            "-- 幂等：按 product_code / card_code 去重插入",
        ]
    )
    lines.extend(["", "BEGIN;", "", "-- ========== 产品 =========="])
    for p in products:
        lines.append(product_insert_sql(p))
        lines.append("")
    lines.append("-- ========== 卡牌 ==========")
    for c in cards:
        lines.append(card_insert_sql(c))
        lines.append("")
    lines.append("COMMIT;")
    lines.append("")
    return "\n".join(lines)


def write_seed_by_product(
    products: list[dict[str, Any]],
    cards: list[dict[str, Any]],
    *,
    out_dir: Path,
    index_path: Path,
    title_prefix: str = "卡牌/产品种子",
) -> list[tuple[str, int]]:
    """按产品拆分写入 seed-cards/{CODE}.sql，并生成入口 index。"""
    out_dir = Path(out_dir)
    out_dir.mkdir(parents=True, exist_ok=True)
    for old in out_dir.glob("*.sql"):
        old.unlink()

    product_map = {p["product_code"]: p for p in products}
    by_product: dict[str, list[dict[str, Any]]] = defaultdict(list)
    for raw in cards:
        c = normalize_seed_card(raw)
        by_product[c["product_code"]].append(c)
        if c["product_code"] not in product_map:
            product_map[c["product_code"]] = {
                "product_code": c["product_code"],
                "product_name": c["product_code"],
                "release_date": None,
                "description": None,
            }

    # 稳定顺序：先按传入 products 顺序，再补遗漏
    ordered_codes: list[str] = []
    for p in products:
        code = p["product_code"]
        if code in by_product and code not in ordered_codes:
            ordered_codes.append(code)
    for code in sorted(by_product.keys()):
        if code not in ordered_codes:
            ordered_codes.append(code)

    written: list[tuple[str, int]] = []
    for code in ordered_codes:
        prod_cards = by_product[code]
        p = product_map[code]
        title = f"{code} {p.get('product_name') or ''}".strip()
        lines = [
            f"-- {title_prefix}：{title}",
            f"-- product_code = {code}，共 {len(prod_cards)} 张卡",
            "-- 幂等：按 product_code / card_code 去重插入",
            "",
            "BEGIN;",
            "",
            "-- ========== 产品 ==========",
            product_insert_sql(p),
            "",
            "-- ========== 卡牌 ==========",
        ]
        for c in prod_cards:
            lines.append(card_insert_sql(c))
            lines.append("")
        lines.append("COMMIT;")
        lines.append("")
        (out_dir / f"{code}.sql").write_text("\n".join(lines), encoding="utf-8")
        written.append((code, len(prod_cards)))

    index_lines = [
        f"-- {title_prefix}入口（按产品拆分）",
        "-- 官网整包: seed-official-cards.sql",
        "-- 本目录分文件: seed-cards/{产品编码}.sql",
        "-- 全部执行（psql）: \\i .../sql/seed-cards.sql",
        "-- 单产品执行: \\i .../sql/seed-cards/BP01.sql",
        "",
    ]
    for code, n in written:
        index_lines.append(f"-- {code}（{n}）")
        index_lines.append(f"\\ir seed-cards/{code}.sql")
        index_lines.append("")
    Path(index_path).write_text("\n".join(index_lines), encoding="utf-8")
    return written
