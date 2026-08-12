-- 卡牌/产品种子：TB01 宝藏包01
-- product_code = TB01，共 1 张卡
-- 幂等：按 product_code / card_code 去重插入

BEGIN;

-- ========== 产品 ==========
INSERT INTO mtcg_product (product_code, product_name, release_date, description)
SELECT 'TB01', '宝藏包01', NULL, '宝藏包'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_product WHERE product_code = 'TB01');

-- ========== 卡牌 ==========
INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'TB01-001-TR',
    'TB01',
    '「斗界之主」毁灭博士',
    'CHARACTER',
    6,
    'GREEN',
    NULL,
    '斗界',
    2,
    2500,
    'TR',
    '唯一 应对 触发【场上】：此卡因号召放置进场时，可以裁剪敌方场上1张Lv2或以下的卡牌。',
    NULL,
    'card/faces/TB01/TB01-001-TR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'TB01-001-TR');

COMMIT;
