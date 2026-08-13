-- 卡牌/产品种子：SD02 心灵
-- product_code = SD02，共 21 张卡
-- 幂等：按 product_code / card_code 去重插入

BEGIN;

-- ========== 产品 ==========
INSERT INTO mtcg_product (product_code, product_name, release_date, description, category)
SELECT 'SD02', '心灵', '2026-06-17', NULL, 'STARTER'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_product WHERE product_code = 'SD02');

-- ========== 卡牌 ==========
INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD02-001-MR',
    'SD02',
    '「算力共享」奥创',
    'CHARACTER',
    6,
    'YELLOW',
    'S1',
    '机械',
    1,
    500,
    'MR',
    '唯一（常驻【场上】：我方场上不能存在其他和此卡名称相同的卡牌，此效果不能失去。） 常驻【战区】：此卡获得战力+X000，X为我方基地盖卡数。 常驻【战区】：若我方撤退区存在9张或以上的黄色角色，则我方战区所有Lv1，特征含有【机械】的角色均获得战力+X000，X为我方基地盖卡数。',
    NULL,
    'card/faces/SD02/SD02-001-MR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD02-001-MR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD02-001-SEC',
    'SD02',
    '「算力共享」奥创',
    'CHARACTER',
    6,
    'YELLOW',
    'S1',
    '机械',
    1,
    500,
    'SEC',
    '唯一（常驻【场上】：我方场上不能存在其他和此卡名称相同的卡牌，此效果不能失去。） 常驻【战区】：此卡获得战力+X000，X为我方基地盖卡数。 常驻【战区】：若我方撤退区存在9张或以上的黄色角色，则我方战区所有Lv1，特征含有【机械】的角色均获得战力+X000，X为我方基地盖卡数。',
    NULL,
    'card/faces/SD02/SD02-001-SEC.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD02-001-SEC');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD02-001-UR',
    'SD02',
    '「算力共享」奥创',
    'CHARACTER',
    6,
    'YELLOW',
    'S1',
    '机械',
    1,
    500,
    'UR',
    '唯一（常驻【场上】：我方场上不能存在其他和此卡名称相同的卡牌，此效果不能失去。） 常驻【战区】：此卡获得战力+X000，X为我方基地盖卡数。 常驻【战区】：若我方撤退区存在9张或以上的黄色角色，则我方战区所有Lv1，特征含有【机械】的角色均获得战力+X000，X为我方基地盖卡数。',
    NULL,
    'card/faces/SD02/SD02-001-UR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD02-001-UR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD02-002-GR',
    'SD02',
    '「算力驱动」幻视',
    'CHARACTER',
    6,
    'YELLOW',
    'S1',
    '机械/复仇者联盟',
    1,
    6000,
    'GR',
    '唯一（常驻【场上】：我方场上不能存在其他和此卡名称相同的卡牌，此效果不能失去。） 常驻【战区】：此卡获得R+X，X为我方基地盖卡数。 触发【战区】：我方战区特征含有【机械】的角色因战败放置进撤退区时，把该角色盖放进我方基地。',
    NULL,
    'card/faces/SD02/SD02-002-GR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD02-002-GR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD02-003-GR',
    'SD02',
    '「进退自如」黑豹',
    'CHARACTER',
    6,
    'YELLOW',
    'S1',
    '人类/复仇者联盟/瓦坎达',
    2,
    6000,
    'GR',
    '常驻【先锋】：此卡不会因相杀撤退。 常驻【后卫】：敌方先锋区角色获得战力-500。',
    NULL,
    'card/faces/SD02/SD02-003-GR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD02-003-GR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD02-004-SR',
    'SD02',
    '「坚实守卫」浩克',
    'CHARACTER',
    3,
    'YELLOW',
    'S1',
    '人类/复仇者联盟',
    1,
    3000,
    'SR',
    '应对（常驻【手牌】：此卡可以应对号召。）',
    NULL,
    'card/faces/SD02/SD02-004-SR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD02-004-SR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD02-005-SR',
    'SD02',
    '「机骸潮汐」奥创',
    'CHARACTER',
    1,
    'YELLOW',
    'S1',
    '机械',
    1,
    500,
    'SR',
    '起动【基地/回合1次】：舍弃我方卡组顶3张卡。如此做后，我方抽1张卡，并且盖伏此卡。',
    NULL,
    'card/faces/SD02/SD02-005-SR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD02-005-SR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD02-006-SR',
    'SD02',
    '「等价交换」钢铁侠',
    'CHARACTER',
    4,
    'YELLOW',
    'S1',
    '人类/复仇者联盟',
    1,
    3000,
    'SR',
    '触发【战区】：此卡因号召放置进场时，可以把我方撤退区2张Lv1，特征含有【机械】的角色结附于此卡。 起动【战区/回合1次】：把战区的此卡的1张Lv1的结附卡解除至我方基地。',
    NULL,
    'card/faces/SD02/SD02-006-SR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD02-006-SR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD02-007-R',
    'SD02',
    '「暴怒失控」红浩克',
    'CHARACTER',
    6,
    'YELLOW',
    'S1',
    '人类',
    2,
    3000,
    'R',
    '起动【手牌】：若我方撤退区只存在黄色角色，并且角色数为9张或以上，则把手牌的此卡放置进我方场上。',
    NULL,
    'card/faces/SD02/SD02-007-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD02-007-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD02-008-R',
    'SD02',
    '「机骸加载」幻视',
    'CHARACTER',
    1,
    'YELLOW',
    'S1',
    '机械/复仇者联盟',
    1,
    500,
    'R',
    '起动【基地/回合1次】：我方战区1张角色本回合获得R+1。如此做后，盖伏此卡。',
    NULL,
    'card/faces/SD02/SD02-008-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD02-008-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD02-009-R',
    'SD02',
    '「机骸引爆」幻视',
    'CHARACTER',
    1,
    'YELLOW',
    'S1',
    '机械/复仇者联盟',
    1,
    500,
    'R',
    '起动【基地/回合1次】：敌方战区1张角色本回合获得战力-1000。如此做后，盖伏此卡。',
    NULL,
    'card/faces/SD02/SD02-009-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD02-009-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD02-010-R',
    'SD02',
    '「机骸回收」战争机器',
    'CHARACTER',
    1,
    'YELLOW',
    'S1',
    '人类/复仇者联盟',
    1,
    1500,
    'R',
    '触发【战区】：此卡因号召放置进场时，把我方撤退区1张Lv1，特征含有【机械】的角色放置进我方基地。',
    NULL,
    'card/faces/SD02/SD02-010-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD02-010-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD02-011-R',
    'SD02',
    '「雷霆共鸣」雷神',
    'CHARACTER',
    3,
    'YELLOW',
    'S1',
    '阿斯加德/复仇者联盟',
    1,
    1000,
    'R',
    '触发【后卫/回合1次】：我方原本Lv3或以下的角色战败时，展示我方基地1张盖卡。如此做后，若该盖卡和该角色Lv相同，则翻开该盖卡。',
    NULL,
    'card/faces/SD02/SD02-011-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD02-011-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD02-012-R',
    'SD02',
    '「英雄档案」雷神',
    'CHARACTER',
    3,
    'YELLOW',
    'S1',
    '阿斯加德/复仇者联盟',
    4,
    2500,
    'R',
    '无效果',
    NULL,
    'card/faces/SD02/SD02-012-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD02-012-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD02-013-R',
    'SD02',
    '「英雄档案」幻视',
    'CHARACTER',
    2,
    'YELLOW',
    'S1',
    '机械/复仇者联盟',
    3,
    2500,
    'R',
    '无效果',
    NULL,
    'card/faces/SD02/SD02-013-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD02-013-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD02-014-R',
    'SD02',
    '「下克上」反浩克装甲',
    'CHARACTER',
    3,
    'YELLOW',
    'S1',
    '机械',
    1,
    3000,
    'R',
    '触发【战区】：此卡攻击时，若攻击目标为Lv4或以上的角色，则此卡本回合获得战力+1500。',
    NULL,
    'card/faces/SD02/SD02-014-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD02-014-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD02-015-R',
    'SD02',
    '「残械组装」幻视',
    'CHARACTER',
    5,
    'YELLOW',
    'S1',
    '机械/复仇者联盟',
    2,
    3500,
    'R',
    '触发【场上】：此卡因号召放置进场时，把我方撤退区1张Lv1，特征含有【机械】的角色放置进我方基地。',
    NULL,
    'card/faces/SD02/SD02-015-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD02-015-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD02-016-R',
    'SD02',
    '「狩猎本能」黑豹',
    'CHARACTER',
    6,
    'YELLOW',
    'S1',
    '人类/复仇者联盟/瓦坎达',
    3,
    4000,
    'R',
    '触发【场上】：此卡因号召放置进场时，此卡本回合获得能力【强袭】（常驻【战区】：若此卡因攻击战胜，则判定此卡成功攻击破绽）。',
    NULL,
    'card/faces/SD02/SD02-016-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD02-016-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD02-017-R',
    'SD02',
    '「反坦克」战争机器',
    'CHARACTER',
    2,
    'YELLOW',
    'S1',
    '人类/复仇者联盟',
    1,
    2000,
    'R',
    '触发【场上】：此卡因号召放置进场时，敌方先锋区角色本回合获得战力-1000。',
    NULL,
    'card/faces/SD02/SD02-017-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD02-017-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD02-018-R',
    'SD02',
    '「短兵相接」雷神',
    'CHARACTER',
    4,
    'YELLOW',
    'S1',
    '阿斯加德/复仇者联盟',
    1,
    4000,
    'R',
    '常驻【战区】：若敌方先锋区角色的R为1，则此卡获得战力+1500。',
    NULL,
    'card/faces/SD02/SD02-018-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD02-018-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD02-019-C',
    'SD02',
    '冲击卡',
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
    'card/faces/SD02/SD02-019-C.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD02-019-C');

COMMIT;
