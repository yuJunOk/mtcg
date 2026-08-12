-- 卡牌/产品种子：PB01 推广包01
-- product_code = PB01，共 11 张卡
-- 幂等：按 product_code / card_code 去重插入

BEGIN;

-- ========== 产品 ==========
INSERT INTO mtcg_product (product_code, product_name, release_date, description)
SELECT 'PB01', '推广包01', NULL, '推广包'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_product WHERE product_code = 'PB01');

-- ========== 卡牌 ==========
INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'PB01-001-PR',
    'PB01',
    '「毁灭者」德拉克斯',
    'CHARACTER',
    6,
    'RED',
    NULL,
    '银河护卫队',
    1,
    6000,
    'PR',
    '触发【战区】：此卡战胜时，我方战区除此卡以外的所有特征含有【银河护卫队】的角色本回合均获得R+1。',
    NULL,
    'card/faces/PB01/PB01-001-PR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'PB01-001-PR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'PB01-002-PR',
    'PB01',
    '「暖心小兔子」火箭浣熊',
    'CHARACTER',
    3,
    'YELLOW',
    NULL,
    '银河护卫队',
    3,
    500,
    'PR',
    '触发【战区】：此卡攻击时，若攻击目标为本回合被攻击过的角色，则撤退该角色。',
    NULL,
    'card/faces/PB01/PB01-002-PR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'PB01-002-PR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'PB01-003-PR',
    'PB01',
    '「银河舞者」星爵',
    'CHARACTER',
    4,
    'BLUE',
    NULL,
    '银河护卫队',
    2,
    2500,
    'PR',
    '唯一【常驻】【场上】：我方场上不能存在其他和此卡名称相同的卡牌，此效果不能失去。 触发【基地】：敌方角色因效果放置进场时，可以裁剪基地的此卡。如此做后，把该角色移回卡组底。',
    NULL,
    'card/faces/PB01/PB01-003-PR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'PB01-003-PR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'PB01-004-PR',
    'PB01',
    '「危险姐妹」星云',
    'CHARACTER',
    1,
    'BLUE',
    NULL,
    '银河护卫队',
    1,
    500,
    'PR',
    '常驻【战区】：此卡获得R+X、战力+X000，X为我方战区除此卡以外的特征含有【银河护卫队】的角色数。',
    NULL,
    'card/faces/PB01/PB01-004-PR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'PB01-004-PR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'PB01-005-PR',
    'PB01',
    '「危险姐妹」卡摩拉',
    'CHARACTER',
    5,
    'GREEN',
    NULL,
    '银河护卫队',
    2,
    4000,
    'PR',
    '启动【手牌】：若我方战区存在2张或以上特征含有【银河护卫队】的角色，则把手牌的此卡放置进我方场上。',
    NULL,
    'card/faces/PB01/PB01-005-PR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'PB01-005-PR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'PB01-006-PR',
    'PB01',
    '「我是格鲁特」格鲁特',
    'CHARACTER',
    2,
    'GREEN',
    NULL,
    '银河护卫队',
    1,
    7500,
    'PR',
    '常驻【战区】：此卡获得战力-X000，X为我方手牌数。',
    NULL,
    'card/faces/PB01/PB01-006-PR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'PB01-006-PR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'PB01-007-PR',
    'PB01',
    '「先决打击」钢铁侠',
    'CHARACTER',
    3,
    'RED',
    NULL,
    '人类/复仇者联盟',
    2,
    1500,
    'PR',
    '触发【战区】：此卡攻击时，若攻击目标为战力高于此卡的角色，则该角色本回合获得战力-2000。',
    NULL,
    'card/faces/PB01/PB01-007-PR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'PB01-007-PR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'PB01-008-PR',
    'PB01',
    '「雷霆传送」雷神',
    'CHARACTER',
    6,
    'YELLOW',
    NULL,
    '阿斯加德/复仇者联盟',
    3,
    4000,
    'PR',
    '常驻【手牌】：若我方战区只存在黄色角色，则此卡获得能力【应对】（常驻【手牌】：此卡可以应对号召）。',
    NULL,
    'card/faces/PB01/PB01-008-PR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'PB01-008-PR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'PB01-009-PR',
    'PB01',
    '「神兵天降」浩克',
    'CHARACTER',
    6,
    'YELLOW',
    NULL,
    '人类/复仇者联盟',
    1,
    6000,
    'PR',
    '启动【手牌】：撤退我方基地6张卡。如此做后，把手牌的此卡放置进我方场上。',
    NULL,
    'card/faces/PB01/PB01-009-PR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'PB01-009-PR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'PB01-010-PR',
    'PB01',
    '「深入敌后」美国队长',
    'CHARACTER',
    4,
    'BLUE',
    NULL,
    '人类/复仇者联盟',
    0,
    4500,
    'PR',
    '启动【手牌】：若双方基地盖卡数相比我方较多，则把手牌的此卡放置进敌方战区。 触发【战区】：此卡放置进场时，撤退我方战区1张战力4500或以下的角色。',
    NULL,
    'card/faces/PB01/PB01-010-PR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'PB01-010-PR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'PB01-011-PR',
    'PB01',
    '「星体访客」奇异博士',
    'CHARACTER',
    3,
    'GREEN',
    NULL,
    '人类/复仇者联盟/卡玛泰姬',
    2,
    2000,
    'PR',
    '启动【虚空/回合1次】：裁剪我方撤退区1张Lv4或以上的角色。如此做后，把虚空区的此卡放置进我方场上。',
    NULL,
    'card/faces/PB01/PB01-011-PR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'PB01-011-PR');

COMMIT;
