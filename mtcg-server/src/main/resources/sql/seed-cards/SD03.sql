-- 卡牌/产品种子：SD03 空间
-- product_code = SD03，共 1 张卡
-- 幂等：按 product_code / card_code 去重插入

BEGIN;

-- ========== 产品 ==========
INSERT INTO mtcg_product (product_code, product_name, release_date, description)
SELECT 'SD03', '空间', '2026-06-17', NULL
WHERE NOT EXISTS (SELECT 1 FROM mtcg_product WHERE product_code = 'SD03');

-- ========== 卡牌 ==========
INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD03-019-C',
    'SD03',
    '冲击卡',
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
    'card/faces/SD03/SD03-019-C.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD03-019-C');

COMMIT;
