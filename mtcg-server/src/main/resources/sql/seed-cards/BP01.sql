-- 卡牌/产品种子：BP01 复仇者联盟
-- product_code = BP01，共 44 张卡
-- 幂等：按 product_code / card_code 去重插入

BEGIN;

-- ========== 产品 ==========
INSERT INTO mtcg_product (product_code, product_name, release_date, description)
SELECT 'BP01', '复仇者联盟', '2026-06-17', NULL
WHERE NOT EXISTS (SELECT 1 FROM mtcg_product WHERE product_code = 'BP01');

-- ========== 卡牌 ==========
INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-150-C',
    'BP01',
    '时间',
    'RUSH_POINT',
    NULL,
    'GREEN',
    NULL,
    NULL,
    NULL,
    NULL,
    'C',
    NULL,
    NULL,
    'card/faces/BP01/BP01-150-C.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-150-C');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-150-SEC',
    'BP01',
    '时间',
    'RUSH_POINT',
    NULL,
    'GREEN',
    NULL,
    NULL,
    NULL,
    NULL,
    'SEC',
    NULL,
    NULL,
    'card/faces/BP01/BP01-150-SEC.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-150-SEC');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-149-C',
    'BP01',
    '空间',
    'RUSH_POINT',
    NULL,
    'BLUE',
    NULL,
    NULL,
    NULL,
    NULL,
    'C',
    NULL,
    NULL,
    'card/faces/BP01/BP01-149-C.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-149-C');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-149-SEC',
    'BP01',
    '空间',
    'RUSH_POINT',
    NULL,
    'BLUE',
    NULL,
    NULL,
    NULL,
    NULL,
    'SEC',
    NULL,
    NULL,
    'card/faces/BP01/BP01-149-SEC.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-149-SEC');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-148-C',
    'BP01',
    '心灵',
    'RUSH_POINT',
    NULL,
    'YELLOW',
    NULL,
    NULL,
    NULL,
    NULL,
    'C',
    NULL,
    NULL,
    'card/faces/BP01/BP01-148-C.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-148-C');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-148-SEC',
    'BP01',
    '心灵',
    'RUSH_POINT',
    NULL,
    'YELLOW',
    NULL,
    NULL,
    NULL,
    NULL,
    'SEC',
    NULL,
    NULL,
    'card/faces/BP01/BP01-148-SEC.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-148-SEC');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-147-C',
    'BP01',
    '现实',
    'RUSH_POINT',
    NULL,
    'RED',
    NULL,
    NULL,
    NULL,
    NULL,
    'C',
    NULL,
    NULL,
    'card/faces/BP01/BP01-147-C.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-147-C');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-147-SEC',
    'BP01',
    '现实',
    'RUSH_POINT',
    NULL,
    'RED',
    NULL,
    NULL,
    NULL,
    NULL,
    'SEC',
    NULL,
    NULL,
    'card/faces/BP01/BP01-147-SEC.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-147-SEC');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-146-C',
    'BP01',
    '冲击卡',
    'RUSH_POINT',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    'C',
    NULL,
    NULL,
    'card/faces/BP01/BP01-146-C.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-146-C');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-145-C',
    'BP01',
    '冲击卡',
    'RUSH_POINT',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    'C',
    NULL,
    NULL,
    'card/faces/BP01/BP01-145-C.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-145-C');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-144-C',
    'BP01',
    '冲击卡',
    'RUSH_POINT',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    'C',
    NULL,
    NULL,
    'card/faces/BP01/BP01-144-C.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-144-C');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-143-C',
    'BP01',
    '冲击卡',
    'RUSH_POINT',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    'C',
    NULL,
    NULL,
    'card/faces/BP01/BP01-143-C.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-143-C');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-142-C',
    'BP01',
    '冲击卡',
    'RUSH_POINT',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    'C',
    NULL,
    NULL,
    'card/faces/BP01/BP01-142-C.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-142-C');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-141-C',
    'BP01',
    '冲击卡',
    'RUSH_POINT',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    'C',
    NULL,
    NULL,
    'card/faces/BP01/BP01-141-C.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-141-C');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-140-C',
    'BP01',
    '冲击卡',
    'RUSH_POINT',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    'C',
    NULL,
    NULL,
    'card/faces/BP01/BP01-140-C.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-140-C');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-139-C',
    'BP01',
    '冲击卡',
    'RUSH_POINT',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    'C',
    NULL,
    NULL,
    'card/faces/BP01/BP01-139-C.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-139-C');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-138-C',
    'BP01',
    '冲击卡',
    'RUSH_POINT',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    'C',
    NULL,
    NULL,
    'card/faces/BP01/BP01-138-C.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-138-C');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-137-C',
    'BP01',
    '冲击卡',
    'RUSH_POINT',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    'C',
    NULL,
    NULL,
    'card/faces/BP01/BP01-137-C.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-137-C');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-136-C',
    'BP01',
    '冲击卡',
    'RUSH_POINT',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    'C',
    NULL,
    NULL,
    'card/faces/BP01/BP01-136-C.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-136-C');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-135-C',
    'BP01',
    '冲击卡',
    'RUSH_POINT',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    'C',
    NULL,
    NULL,
    'card/faces/BP01/BP01-135-C.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-135-C');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-134-C',
    'BP01',
    '冲击卡',
    'RUSH_POINT',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    'C',
    NULL,
    NULL,
    'card/faces/BP01/BP01-134-C.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-134-C');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-133-C',
    'BP01',
    '冲击卡',
    'RUSH_POINT',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    'C',
    NULL,
    NULL,
    'card/faces/BP01/BP01-133-C.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-133-C');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-132-C',
    'BP01',
    '冲击卡',
    'RUSH_POINT',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    'C',
    NULL,
    NULL,
    'card/faces/BP01/BP01-132-C.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-132-C');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-131-C',
    'BP01',
    '冲击卡',
    'RUSH_POINT',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    'C',
    NULL,
    NULL,
    'card/faces/BP01/BP01-131-C.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-131-C');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-130-C',
    'BP01',
    '冲击卡',
    'RUSH_POINT',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    'C',
    NULL,
    NULL,
    'card/faces/BP01/BP01-130-C.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-130-C');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-130-MR',
    'BP01',
    '冲击卡',
    'RUSH_POINT',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    'MR',
    NULL,
    NULL,
    'card/faces/BP01/BP01-130-MR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-130-MR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-129-C',
    'BP01',
    '冲击卡',
    'RUSH_POINT',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    'C',
    NULL,
    NULL,
    'card/faces/BP01/BP01-129-C.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-129-C');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-129-MR',
    'BP01',
    '冲击卡',
    'RUSH_POINT',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    'MR',
    NULL,
    NULL,
    'card/faces/BP01/BP01-129-MR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-129-MR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-128-C',
    'BP01',
    '冲击卡',
    'RUSH_POINT',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    'C',
    NULL,
    NULL,
    'card/faces/BP01/BP01-128-C.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-128-C');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-128-MR',
    'BP01',
    '冲击卡',
    'RUSH_POINT',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    'MR',
    NULL,
    NULL,
    'card/faces/BP01/BP01-128-MR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-128-MR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-127-C',
    'BP01',
    '冲击卡',
    'RUSH_POINT',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    'C',
    NULL,
    NULL,
    'card/faces/BP01/BP01-127-C.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-127-C');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-127-MR',
    'BP01',
    '冲击卡',
    'RUSH_POINT',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    'MR',
    NULL,
    NULL,
    'card/faces/BP01/BP01-127-MR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-127-MR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-126-C',
    'BP01',
    '冲击卡',
    'RUSH_POINT',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    'C',
    NULL,
    NULL,
    'card/faces/BP01/BP01-126-C.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-126-C');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-126-MR',
    'BP01',
    '冲击卡',
    'RUSH_POINT',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    'MR',
    NULL,
    NULL,
    'card/faces/BP01/BP01-126-MR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-126-MR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-125-C',
    'BP01',
    '冲击卡',
    'RUSH_POINT',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    'C',
    NULL,
    NULL,
    'card/faces/BP01/BP01-125-C.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-125-C');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-124-C',
    'BP01',
    '冲击卡',
    'RUSH_POINT',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    'C',
    NULL,
    NULL,
    'card/faces/BP01/BP01-124-C.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-124-C');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-124-MR',
    'BP01',
    '冲击卡',
    'RUSH_POINT',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    'MR',
    NULL,
    NULL,
    'card/faces/BP01/BP01-124-MR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-124-MR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-123-C',
    'BP01',
    '冲击卡',
    'RUSH_POINT',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    'C',
    NULL,
    NULL,
    'card/faces/BP01/BP01-123-C.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-123-C');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-123-MR',
    'BP01',
    '冲击卡',
    'RUSH_POINT',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    'MR',
    NULL,
    NULL,
    'card/faces/BP01/BP01-123-MR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-123-MR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-122-C',
    'BP01',
    '冲击卡',
    'RUSH_POINT',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    'C',
    NULL,
    NULL,
    'card/faces/BP01/BP01-122-C.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-122-C');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-122-MR',
    'BP01',
    '冲击卡',
    'RUSH_POINT',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    'MR',
    NULL,
    NULL,
    'card/faces/BP01/BP01-122-MR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-122-MR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-121-C',
    'BP01',
    '冲击卡',
    'RUSH_POINT',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    'C',
    NULL,
    NULL,
    'card/faces/BP01/BP01-121-C.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-121-C');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-121-MR',
    'BP01',
    '冲击卡',
    'RUSH_POINT',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    'MR',
    NULL,
    NULL,
    'card/faces/BP01/BP01-121-MR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-121-MR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-121-SEC',
    'BP01',
    '冲击卡',
    'RUSH_POINT',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    'SEC',
    NULL,
    NULL,
    'card/faces/BP01/BP01-121-SEC.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-121-SEC');

COMMIT;
