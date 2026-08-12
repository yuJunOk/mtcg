-- 卡牌/产品种子：EB01 赛事包01
-- product_code = EB01，共 6 张卡
-- 幂等：按 product_code / card_code 去重插入

BEGIN;

-- ========== 产品 ==========
INSERT INTO mtcg_product (product_code, product_name, release_date, description)
SELECT 'EB01', '赛事包01', NULL, '赛事包'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_product WHERE product_code = 'EB01');

-- ========== 卡牌 ==========
INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'EB01-004-ER',
    'EB01',
    '冲击卡',
    'RUSH_POINT',
    NULL,
    'RED',
    NULL,
    NULL,
    NULL,
    NULL,
    'ER',
    NULL,
    NULL,
    'card/faces/EB01/EB01-004-ER.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'EB01-004-ER');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'EB01-005-ER',
    'EB01',
    '冲击卡',
    'RUSH_POINT',
    NULL,
    'RED',
    NULL,
    NULL,
    NULL,
    NULL,
    'ER',
    NULL,
    NULL,
    'card/faces/EB01/EB01-005-ER.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'EB01-005-ER');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'EB01-006-ER',
    'EB01',
    '「压制打击」浩克',
    'CHARACTER',
    6,
    'RED',
    NULL,
    '人类/复仇者联盟',
    1,
    6000,
    'ER',
    '触发【战区】：敌方Lv6的角色放置进场时，此卡和该角色本回合均获得战力-1000。',
    NULL,
    'card/faces/EB01/EB01-006-ER.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'EB01-006-ER');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'EB01-007-ER',
    'EB01',
    '「心灵共鸣」幻视',
    'CHARACTER',
    3,
    'YELLOW',
    NULL,
    '机械/复仇者联盟',
    3,
    2500,
    'ER',
    '启动【战区/回合1次】：撤退我方场上1张Lv3、特征含有【复仇者联盟】的角色。如此做后，可以把我方手牌1张Lv3、特征含有【复仇者联盟】的角色放置进我方战区。',
    NULL,
    'card/faces/EB01/EB01-007-ER.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'EB01-007-ER');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'EB01-008-ER',
    'EB01',
    '「混沌本源」绯红女巫',
    'CHARACTER',
    6,
    'BLUE',
    NULL,
    '变种人/复仇者联盟',
    3,
    500,
    'ER',
    '触发【场上/回合1次】：此卡战基移动时，此卡本回合获得战力+X000，X为我方虚空区角色数。',
    NULL,
    'card/faces/EB01/EB01-008-ER.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'EB01-008-ER');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'EB01-009-ER',
    'EB01',
    '「以一敌二」冬兵',
    'CHARACTER',
    3,
    'GREEN',
    NULL,
    '人类/复仇者联盟',
    2,
    3000,
    'ER',
    '触发【战区】：此卡战胜时，可以撤退此卡和敌方战区1张Lv3或以下的角色。',
    NULL,
    'card/faces/EB01/EB01-009-ER.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'EB01-009-ER');

COMMIT;
