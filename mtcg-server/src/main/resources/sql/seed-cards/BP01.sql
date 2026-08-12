-- 卡牌/产品种子：BP01 复仇者联盟
-- product_code = BP01，共 197 张卡
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
    'BP01-001-MR',
    'BP01',
    '「反物质」钢铁侠',
    'CHARACTER',
    6,
    'RED',
    NULL,
    '人类/复仇者联盟',
    1,
    6500,
    'MR',
    '触发【场上】：此卡因号召放置进场时，裁剪敌方场上1张LvX或以下的角色，X为因此卡号召撤退的卡牌数。',
    NULL,
    'card/faces/BP01/BP01-001-MR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-001-MR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-001-SEC',
    'BP01',
    '「反物质」钢铁侠',
    'CHARACTER',
    6,
    'RED',
    NULL,
    '人类/复仇者联盟',
    1,
    6500,
    'SEC',
    '触发【场上】：此卡因号召放置进场时，裁剪敌方场上1张LvX或以下的角色，X为因此卡号召撤退的卡牌数。',
    NULL,
    'card/faces/BP01/BP01-001-SEC.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-001-SEC');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-001-UR',
    'BP01',
    '「反物质」钢铁侠',
    'CHARACTER',
    6,
    'RED',
    NULL,
    '人类/复仇者联盟',
    1,
    6500,
    'UR',
    '触发【场上】：此卡因号召放置进场时，裁剪敌方场上1张LvX或以下的角色，X为因此卡号召撤退的卡牌数。',
    NULL,
    'card/faces/BP01/BP01-001-UR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-001-UR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-002-MR',
    'BP01',
    '「潜龙谍影」黑寡妇',
    'CHARACTER',
    3,
    'RED',
    NULL,
    '人类/复仇者联盟',
    1,
    2000,
    'MR',
    '应对·起动【手牌】：舍弃手牌的此卡。如此做后，敌方战区1张角色本回合获得战力-2000。',
    NULL,
    'card/faces/BP01/BP01-002-MR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-002-MR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-002-UR',
    'BP01',
    '「潜龙谍影」黑寡妇',
    'CHARACTER',
    3,
    'RED',
    NULL,
    '人类/复仇者联盟',
    1,
    2000,
    'UR',
    '应对·起动【手牌】：舍弃手牌的此卡。如此做后，敌方战区1张角色本回合获得战力-2000。',
    NULL,
    'card/faces/BP01/BP01-002-UR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-002-UR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-003-MR',
    'BP01',
    '「雷霆知音」雷神',
    'CHARACTER',
    6,
    'RED',
    NULL,
    '阿斯加德/复仇者联盟',
    3,
    3500,
    'MR',
    '触发【侧翼】：我方回合结束时，若此卡本回合未进行攻击，则敌方战区1张角色本回合获得战力-X，X为此卡的战力。',
    NULL,
    'card/faces/BP01/BP01-003-MR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-003-MR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-003-SEC',
    'BP01',
    '「雷霆知音」雷神',
    'CHARACTER',
    6,
    'RED',
    NULL,
    '阿斯加德/复仇者联盟',
    3,
    3500,
    'SEC',
    '触发【侧翼】：我方回合结束时，若此卡本回合未进行攻击，则敌方战区1张角色本回合获得战力-X，X为此卡的战力。',
    NULL,
    'card/faces/BP01/BP01-003-SEC.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-003-SEC');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-003-UR',
    'BP01',
    '「雷霆知音」雷神',
    'CHARACTER',
    6,
    'RED',
    NULL,
    '阿斯加德/复仇者联盟',
    3,
    3500,
    'UR',
    '触发【侧翼】：我方回合结束时，若此卡本回合未进行攻击，则敌方战区1张角色本回合获得战力-X，X为此卡的战力。',
    NULL,
    'card/faces/BP01/BP01-003-UR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-003-UR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-004-GR',
    'BP01',
    '「遇强则强」浩克',
    'CHARACTER',
    2,
    'RED',
    NULL,
    '人类/复仇者联盟',
    1,
    500,
    'GR',
    '常驻【场上】：若我方场上只存在红色角色，则此卡获得Lv+X、战力+X000，X为敌方战区角色数。',
    NULL,
    'card/faces/BP01/BP01-004-GR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-004-GR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-004-MR',
    'BP01',
    '「遇强则强」浩克',
    'CHARACTER',
    2,
    'RED',
    NULL,
    '人类/复仇者联盟',
    1,
    500,
    'MR',
    '常驻【场上】：若我方场上只存在红色角色，则此卡获得Lv+X、战力+X000，X为敌方战区角色数。',
    NULL,
    'card/faces/BP01/BP01-004-MR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-004-MR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-005-GR',
    'BP01',
    '「红房实验品」黑寡妇',
    'CHARACTER',
    3,
    'RED',
    NULL,
    '人类/复仇者联盟',
    1,
    2500,
    'GR',
    '触发【手牌】：我方红色角色攻击时，可以舍弃手牌的此卡。如此做后，我方战区1张红色角色本回合获得战力+3000。',
    NULL,
    'card/faces/BP01/BP01-005-GR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-005-GR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-006-GR',
    'BP01',
    '「寂静猎手」黑豹',
    'CHARACTER',
    5,
    'RED',
    NULL,
    '人类/复仇者联盟/瓦坎达',
    2,
    500,
    'GR',
    '常驻【场上】：若我方手牌数为奇数，则此卡获得R+2，否则此卡获得战力+5500。',
    NULL,
    'card/faces/BP01/BP01-006-GR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-006-GR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-006-MR',
    'BP01',
    '「寂静猎手」黑豹',
    'CHARACTER',
    5,
    'RED',
    NULL,
    '人类/复仇者联盟/瓦坎达',
    2,
    500,
    'MR',
    '常驻【场上】：若我方手牌数为奇数，则此卡获得R+2，否则此卡获得战力+5500。',
    NULL,
    'card/faces/BP01/BP01-006-MR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-006-MR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-007-GR',
    'BP01',
    '「虚空重构」幻视',
    'CHARACTER',
    6,
    'RED',
    NULL,
    '机械/复仇者联盟',
    1,
    5000,
    'GR',
    '触发【虚空】：撤退区的此卡放置进虚空区时，可以舍弃我方2张手牌。如此做后，把虚空区的此卡放置进我方场上，并且敌方先锋区角色本回合获得战力-1000。',
    NULL,
    'card/faces/BP01/BP01-007-GR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-007-GR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-007-MR',
    'BP01',
    '「虚空重构」幻视',
    'CHARACTER',
    6,
    'RED',
    NULL,
    '机械/复仇者联盟',
    1,
    5000,
    'MR',
    '触发【虚空】：撤退区的此卡放置进虚空区时，可以舍弃我方2张手牌。如此做后，把虚空区的此卡放置进我方场上，并且敌方先锋区角色本回合获得战力-1000。',
    NULL,
    'card/faces/BP01/BP01-007-MR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-007-MR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-008-GR',
    'BP01',
    '「卸载脉冲」战争机器',
    'CHARACTER',
    1,
    'RED',
    NULL,
    '人类/复仇者联盟',
    3,
    1500,
    'GR',
    '触发【场上】：此卡放置进场时，可以裁剪双方场上角色的所有结附卡。',
    NULL,
    'card/faces/BP01/BP01-008-GR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-008-GR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-009-GR',
    'BP01',
    '「组装脉冲」钢铁侠',
    'CHARACTER',
    3,
    'RED',
    NULL,
    '人类/复仇者联盟',
    1,
    3500,
    'GR',
    '起动【战区/回合1次】：若我方基地不存在卡牌，则裁剪我方撤退区1张Lv5或以上的红色角色。如此做后，敌方战区1张角色本回合获得战力-1000。',
    NULL,
    'card/faces/BP01/BP01-009-GR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-009-GR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-010-SR',
    'BP01',
    '「进化加载」奥创',
    'CHARACTER',
    1,
    'RED',
    NULL,
    '机械',
    1,
    500,
    'SR',
    '起动【手牌】：把手牌的此卡结附于我方场上1张Lv4或以上，特征含有【机械】的角色。 起动【后卫/回合1次】：展示我方基地1张盖卡。如此做后，若该盖卡为Lv1，特征含有【机械】的角色，则把该盖卡移回手牌。',
    NULL,
    'card/faces/BP01/BP01-010-SR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-010-SR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-011-SR',
    'BP01',
    '「疾风迅雷」雷神',
    'CHARACTER',
    3,
    'RED',
    NULL,
    '阿斯加德/复仇者联盟',
    1,
    3000,
    'SR',
    '触发【场上】：此卡因号召放置进场时，我方抽1张卡。如此做后，此卡本回合获得第2次攻击机会，该次机会只能攻击敌方角色。',
    NULL,
    'card/faces/BP01/BP01-011-SR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-011-SR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-012-SR',
    'BP01',
    '「雷霆闪充」雷神',
    'CHARACTER',
    3,
    'RED',
    NULL,
    '阿斯加德/复仇者联盟',
    1,
    3500,
    'SR',
    '触发【战区】：此卡因号召放置进场时，若我方基地存在红色角色，则可以把我方卡组顶2张卡盖放进我方基地。',
    NULL,
    'card/faces/BP01/BP01-012-SR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-012-SR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-013-SR',
    'BP01',
    '「瓦坎达万岁」黑豹',
    'CHARACTER',
    5,
    'RED',
    NULL,
    '人类/复仇者联盟/瓦坎达',
    1,
    500,
    'SR',
    '触发【场上】：此卡因号召放置进场时，把敌方战区1张特征含有【人类】，战力5000或以上的角色移动至敌方基地。如此做后，盖伏该角色。',
    NULL,
    'card/faces/BP01/BP01-013-SR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-013-SR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-014-SR',
    'BP01',
    '「防御程序」奥创',
    'CHARACTER',
    5,
    'RED',
    NULL,
    '机械',
    1,
    5000,
    'SR',
    '触发【场上】：此卡因号召放置进场时，若我方场上存在除此卡以外的名称含有【奥创】的角色，则可以撤退敌方战区1张Lv3或以下的角色。',
    NULL,
    'card/faces/BP01/BP01-014-SR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-014-SR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-015-SR',
    'BP01',
    '「瓦解射线」幻视',
    'CHARACTER',
    6,
    'RED',
    NULL,
    '机械/复仇者联盟',
    2,
    2500,
    'SR',
    '触发【场上】：此卡因号召放置进场时，可以裁剪敌方战区1张战力4000或以下的角色。',
    NULL,
    'card/faces/BP01/BP01-015-SR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-015-SR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-016-SR',
    'BP01',
    '「高维震荡」钢铁侠',
    'CHARACTER',
    3,
    'RED',
    NULL,
    '人类/复仇者联盟',
    1,
    3500,
    'SR',
    '触发【战区/回合1次】：我方Lv4或以上的角色因号召放置进场时，可以把敌方战区1张Lv3或以下的角色移动至敌方基地。',
    NULL,
    'card/faces/BP01/BP01-016-SR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-016-SR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-017-SR',
    'BP01',
    '「复仇暴怒」浩克',
    'CHARACTER',
    4,
    'RED',
    NULL,
    '人类/复仇者联盟',
    1,
    4000,
    'SR',
    '触发【场上】：此卡因号召放置进场时，可以裁剪敌方撤退区1张角色。如此做后，此卡本回合获得战力+1000。',
    NULL,
    'card/faces/BP01/BP01-017-SR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-017-SR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-018-MR',
    'BP01',
    '「对等打击」钢铁侠',
    'CHARACTER',
    3,
    'RED',
    NULL,
    '人类/复仇者联盟',
    1,
    2500,
    'MR',
    '触发【场上】：此卡因号召放置进场时，若双方战区角色数相比下我方较少，则敌方战区1张Lv3或以下的角色本回合获得战力-2000。',
    NULL,
    'card/faces/BP01/BP01-018-MR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-018-MR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-018-R',
    'BP01',
    '「对等打击」钢铁侠',
    'CHARACTER',
    3,
    'RED',
    NULL,
    '人类/复仇者联盟',
    1,
    2500,
    'R',
    '触发【场上】：此卡因号召放置进场时，若双方战区角色数相比下我方较少，则敌方战区1张Lv3或以下的角色本回合获得战力-2000。',
    NULL,
    'card/faces/BP01/BP01-018-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-018-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-019-R',
    'BP01',
    '「振金挖掘」黑豹',
    'CHARACTER',
    3,
    'RED',
    NULL,
    '人类/复仇者联盟/瓦坎达',
    2,
    1500,
    'R',
    '触发【战区/回合1次】：此卡攻击时，可以舍弃我方卡组顶1张卡。如此做后，此卡本回合获得战力+2000。',
    NULL,
    'card/faces/BP01/BP01-019-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-019-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-020-R',
    'BP01',
    '「撕裂一切」浩克',
    'CHARACTER',
    1,
    'RED',
    NULL,
    '人类/复仇者联盟',
    1,
    1000,
    'R',
    '触发【场上】：此卡因号召放置进场时，裁剪我方撤退区1张红色角色。如此做后，我方抽1张卡。',
    NULL,
    'card/faces/BP01/BP01-020-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-020-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-021-R',
    'BP01',
    '「雷霆呼唤」雷神',
    'CHARACTER',
    2,
    'RED',
    NULL,
    '阿斯加德/复仇者联盟',
    1,
    2000,
    'R',
    '触发【场上】：此卡因号召放置进场时，展示我方卡组顶3张卡，把其中1张特征含有【人类】的角色加入手牌，并且舍弃剩余的卡牌。',
    NULL,
    'card/faces/BP01/BP01-021-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-021-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-022-R',
    'BP01',
    '「顶级特工」黑寡妇',
    'CHARACTER',
    1,
    'RED',
    NULL,
    '人类/复仇者联盟',
    2,
    500,
    'R',
    '触发【场上】：此卡因号召放置进场时，若我方场上存在除此卡以外的特征含有【复仇者联盟】的角色，则可以撤退敌方场上1张Lv3或以下，拥有效果类型【常驻】的角色。',
    NULL,
    'card/faces/BP01/BP01-022-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-022-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-023-R',
    'BP01',
    '「MK44-肉搏特化」反浩克装甲',
    'CHARACTER',
    1,
    'RED',
    NULL,
    '机械',
    1,
    1000,
    'R',
    '应对·起动【场上/回合1次】：把场上的此卡结附于我方场上1张角色。如此做后，撤退该角色除此卡以外的所有结附卡。 常驻【场上】：此卡结附的角色获得战力+1000。',
    NULL,
    'card/faces/BP01/BP01-023-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-023-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-024-R',
    'BP01',
    '「同步瞄准」战争机器',
    'CHARACTER',
    2,
    'RED',
    NULL,
    '人类/复仇者联盟',
    2,
    500,
    'R',
    '常驻【战区】：我方战区所有名称含有【钢铁侠】的角色的R均变更为2。',
    NULL,
    'card/faces/BP01/BP01-024-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-024-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-025-R',
    'BP01',
    '「雷霆狂怒」雷神',
    'CHARACTER',
    6,
    'RED',
    NULL,
    '阿斯加德/复仇者联盟',
    2,
    5000,
    'R',
    '常驻【战区】：若我方战区只存在此卡，则此卡获得能力【连击】。',
    NULL,
    'card/faces/BP01/BP01-025-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-025-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-026-R',
    'BP01',
    '「临别赠礼」银影侠',
    'CHARACTER',
    3,
    'RED',
    NULL,
    '赞恩拉',
    2,
    2500,
    'R',
    '触发【撤退】：场上的此卡因号召放置进撤退区时，可以把我方卡组顶1张卡盖放进我方基地。如此做后，把撤退区的此卡移回卡组底。',
    NULL,
    'card/faces/BP01/BP01-026-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-026-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-027-R',
    'BP01',
    '「协同作战」钢铁侠',
    'CHARACTER',
    3,
    'RED',
    NULL,
    '人类/复仇者联盟',
    1,
    3000,
    'R',
    '常驻【战区】：若我方侧翼区2张角色原本的Lv相同，则敌方先锋区角色获得战力-1000。',
    NULL,
    'card/faces/BP01/BP01-027-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-027-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-028-R',
    'BP01',
    '「深入敌后」黑寡妇',
    'CHARACTER',
    1,
    'RED',
    NULL,
    '人类/复仇者联盟',
    1,
    500,
    'R',
    '起动【手牌】：若敌方基地存在角色，则把手牌的此卡放置进敌方基地。 触发【场上】：此卡放置进场时，撤退我方基地1张Lv3或以下的角色，由敌方选择。',
    NULL,
    'card/faces/BP01/BP01-028-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-028-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-029-R',
    'BP01',
    '「掩护作战」战争机器',
    'CHARACTER',
    3,
    'RED',
    NULL,
    '人类/复仇者联盟',
    1,
    2500,
    'R',
    '常驻【场上】：若我方侧翼区2张角色原本的Lv相同，则我方侧翼区所有角色的R均变更为2。',
    NULL,
    'card/faces/BP01/BP01-029-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-029-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-030-R',
    'BP01',
    '「加杠杆」反浩克装甲',
    'CHARACTER',
    1,
    'RED',
    NULL,
    '机械',
    1,
    500,
    'R',
    '起动【手牌】：把手牌的此卡结附于我方场上1张Lv1的红色角色。 常驻【场上】：此卡结附的角色的Lv变更为6。',
    NULL,
    'card/faces/BP01/BP01-030-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-030-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-031-MR',
    'BP01',
    '「智慧诅咒」奥创',
    'CHARACTER',
    6,
    'YELLOW',
    NULL,
    '机械',
    2,
    5000,
    'MR',
    '常驻【战区】：我方侧翼区所有特征含有【机械】的角色均获得战力+1000，若该角色名称含有【奥创】，则该角色额外获得R+1。',
    NULL,
    'card/faces/BP01/BP01-031-MR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-031-MR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-031-UR',
    'BP01',
    '「智慧诅咒」奥创',
    'CHARACTER',
    6,
    'YELLOW',
    NULL,
    '机械',
    2,
    5000,
    'UR',
    '常驻【战区】：我方侧翼区所有特征含有【机械】的角色均获得战力+1000，若该角色名称含有【奥创】，则该角色额外获得R+1。',
    NULL,
    'card/faces/BP01/BP01-031-UR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-031-UR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-032-MR',
    'BP01',
    '「多重打击」黑豹',
    'CHARACTER',
    6,
    'YELLOW',
    NULL,
    '人类/复仇者联盟/瓦坎达',
    2,
    5000,
    'MR',
    '唯一（常驻【场上】：我方场上不能存在其他和此卡名称相同的卡牌，此效果不能失去。） 触发【手牌】：我方黄色角色相杀时，可以把手牌的此卡放置进我方战区。如此做后，敌方战区1张角色本回合获得战力-1000。',
    NULL,
    'card/faces/BP01/BP01-032-MR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-032-MR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-032-UR',
    'BP01',
    '「多重打击」黑豹',
    'CHARACTER',
    6,
    'YELLOW',
    NULL,
    '人类/复仇者联盟/瓦坎达',
    2,
    5000,
    'UR',
    '唯一（常驻【场上】：我方场上不能存在其他和此卡名称相同的卡牌，此效果不能失去。） 触发【手牌】：我方黄色角色相杀时，可以把手牌的此卡放置进我方战区。如此做后，敌方战区1张角色本回合获得战力-1000。',
    NULL,
    'card/faces/BP01/BP01-032-UR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-032-UR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-033-MR',
    'BP01',
    '「援护射击」战争机器',
    'CHARACTER',
    4,
    'YELLOW',
    NULL,
    '人类/复仇者联盟',
    3,
    2500,
    'MR',
    '触发【后卫】：我方特征含有【复仇者联盟】的角色攻击时，敌方战区1张角色本回合获得战力-500。',
    NULL,
    'card/faces/BP01/BP01-033-MR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-033-MR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-033-UR',
    'BP01',
    '「援护射击」战争机器',
    'CHARACTER',
    4,
    'YELLOW',
    NULL,
    '人类/复仇者联盟',
    3,
    2500,
    'UR',
    '触发【后卫】：我方特征含有【复仇者联盟】的角色攻击时，敌方战区1张角色本回合获得战力-500。',
    NULL,
    'card/faces/BP01/BP01-033-UR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-033-UR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-034-GR',
    'BP01',
    '「智慧诅咒」钢铁侠',
    'CHARACTER',
    1,
    'YELLOW',
    NULL,
    '人类/复仇者联盟',
    2,
    500,
    'GR',
    '触发【场上】：此卡因号召放置进场时，展示我方卡组顶3张卡，把其中1张特征含有【机械】的角色加入手牌，并且舍弃剩余的卡牌。',
    NULL,
    'card/faces/BP01/BP01-034-GR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-034-GR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-035-GR',
    'BP01',
    '「MK44-战术特化」反浩克装甲',
    'CHARACTER',
    3,
    'YELLOW',
    NULL,
    '机械',
    1,
    3500,
    'GR',
    '应对·起动【场上/回合1次】：把场上的此卡结附于我方场上1张角色。如此做后，撤退该角色除此卡以外的所有结附卡。 触发【战区】：此卡结附于战区的角色时，该角色可以进行1次战区移动。',
    NULL,
    'card/faces/BP01/BP01-035-GR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-035-GR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-035-MR',
    'BP01',
    '「MK44-战术特化」反浩克装甲',
    'CHARACTER',
    3,
    'YELLOW',
    NULL,
    '机械',
    1,
    3500,
    'MR',
    '应对·起动【场上/回合1次】：把场上的此卡结附于我方场上1张角色。如此做后，撤退该角色除此卡以外的所有结附卡。 触发【战区】：此卡结附于战区的角色时，该角色可以进行1次战区移动。',
    NULL,
    'card/faces/BP01/BP01-035-MR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-035-MR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-036-GR',
    'BP01',
    '「不再孤独」浩克',
    'CHARACTER',
    5,
    'YELLOW',
    NULL,
    '人类/复仇者联盟',
    1,
    4000,
    'GR',
    '常驻【战区】：此卡获得战力+X000，X为我方战区除此卡以外的黄色角色数。',
    NULL,
    'card/faces/BP01/BP01-036-GR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-036-GR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-037-GR',
    'BP01',
    '「眷族号召」奥创',
    'CHARACTER',
    4,
    'YELLOW',
    NULL,
    '机械',
    2,
    3500,
    'GR',
    '触发【场上】：此卡因号召放置进场时，可以把我方撤退区1张Lv2或以下，名称含有【奥创】的角色放置进我方场上。',
    NULL,
    'card/faces/BP01/BP01-037-GR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-037-GR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-038-GR',
    'BP01',
    '「蜂回路转」黄蜂女',
    'CHARACTER',
    1,
    'YELLOW',
    NULL,
    '人类/复仇者联盟',
    2,
    500,
    'GR',
    '触发【战区】：敌方回合开始时，若我方先锋区存在除名称含有【黄蜂女】以外的黄色角色，则可以把战区的此卡和该角色互相替换。如此做后，此卡本回合获得战力+2500。',
    NULL,
    'card/faces/BP01/BP01-038-GR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-038-GR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-038-MR',
    'BP01',
    '「蜂回路转」黄蜂女',
    'CHARACTER',
    1,
    'YELLOW',
    NULL,
    '人类/复仇者联盟',
    2,
    500,
    'MR',
    '触发【战区】：敌方回合开始时，若我方先锋区存在除名称含有【黄蜂女】以外的黄色角色，则可以把战区的此卡和该角色互相替换。如此做后，此卡本回合获得战力+2500。',
    NULL,
    'card/faces/BP01/BP01-038-MR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-038-MR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-039-GR',
    'BP01',
    '「导电窜流」雷神',
    'CHARACTER',
    3,
    'YELLOW',
    NULL,
    '阿斯加德/复仇者联盟',
    1,
    3500,
    'GR',
    '起动【手牌】：若我方战区存在3张黄色角色，则把手牌的此卡放置进我方战区。',
    NULL,
    'card/faces/BP01/BP01-039-GR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-039-GR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-039-MR',
    'BP01',
    '「导电窜流」雷神',
    'CHARACTER',
    3,
    'YELLOW',
    NULL,
    '阿斯加德/复仇者联盟',
    1,
    3500,
    'MR',
    '起动【手牌】：若我方战区存在3张黄色角色，则把手牌的此卡放置进我方战区。',
    NULL,
    'card/faces/BP01/BP01-039-MR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-039-MR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-040-SR',
    'BP01',
    '「死与新生」奥创',
    'CHARACTER',
    2,
    'YELLOW',
    NULL,
    '机械',
    2,
    2500,
    'SR',
    '起动【手牌】：裁剪我方撤退区3张名称不同，特征含有【机械】的角色。如此做后，把手牌的此卡放置进我方场上。',
    NULL,
    'card/faces/BP01/BP01-040-SR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-040-SR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-041-SR',
    'BP01',
    '「影舞」黑豹',
    'CHARACTER',
    2,
    'YELLOW',
    NULL,
    '人类/复仇者联盟/瓦坎达',
    2,
    2500,
    'SR',
    '触发【战区】：此卡成功攻击破绽时，我方抽1张卡。如此做后，舍弃我方1张手牌。',
    NULL,
    'card/faces/BP01/BP01-041-SR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-041-SR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-042-SR',
    'BP01',
    '「家族誓盟」黑寡妇',
    'CHARACTER',
    3,
    'YELLOW',
    NULL,
    '人类/复仇者联盟',
    1,
    2500,
    'SR',
    '触发【场上】：此卡因号召放置进场时，把我方撤退区1张Lv2或以下的黄色角色放置进我方战区。',
    NULL,
    'card/faces/BP01/BP01-042-SR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-042-SR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-043-SR',
    'BP01',
    '「卸载重装」幻视',
    'CHARACTER',
    3,
    'YELLOW',
    NULL,
    '机械/复仇者联盟',
    1,
    3000,
    'SR',
    '触发【场上】：此卡因号召放置进场时，撤退我方场上1张名称含有【奥创】的角色。如此做后，我方抽1张卡，并且可以把我方手牌1张Lv3或以下，特征含有【机械】的角色放置进我方场上。',
    NULL,
    'card/faces/BP01/BP01-043-SR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-043-SR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-044-SR',
    'BP01',
    '「脱离掩护」战争机器',
    'CHARACTER',
    1,
    'YELLOW',
    NULL,
    '人类/复仇者联盟',
    1,
    2000,
    'SR',
    '触发【场上】：我方回合，敌方角色放置进场时，可以撤退此卡。如此做后，可以把我方手牌1张Lv4或以下,名称含有【钢铁侠】的角色和我方先锋区角色互相替换。',
    NULL,
    'card/faces/BP01/BP01-044-SR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-044-SR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-045-SR',
    'BP01',
    '「心灵投影」幻视',
    'CHARACTER',
    2,
    'YELLOW',
    NULL,
    '机械/复仇者联盟',
    1,
    2500,
    'SR',
    '触发（战区/回合1次）：此卡战力增加时，我方战区1张除此卡以外的特征含有【机械】的角色本回合获得战力+X，X为此卡该次的战力增加数。',
    NULL,
    'card/faces/BP01/BP01-045-SR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-045-SR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-046-MR',
    'BP01',
    '「班纳同化」浩克',
    'CHARACTER',
    6,
    'YELLOW',
    NULL,
    '人类/复仇者联盟',
    1,
    4000,
    'MR',
    '常驻【手牌】：此卡获得Lv-X，X为我方场上特征含有【复仇者联盟】的角色数。',
    NULL,
    'card/faces/BP01/BP01-046-MR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-046-MR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-046-SR',
    'BP01',
    '「班纳同化」浩克',
    'CHARACTER',
    6,
    'YELLOW',
    NULL,
    '人类/复仇者联盟',
    1,
    4000,
    'SR',
    '常驻【手牌】：此卡获得Lv-X，X为我方场上特征含有【复仇者联盟】的角色数。',
    NULL,
    'card/faces/BP01/BP01-046-SR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-046-SR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-047-SR',
    'BP01',
    '「中控系统」钢铁侠',
    'CHARACTER',
    1,
    'YELLOW',
    NULL,
    '人类/复仇者联盟',
    1,
    500,
    'SR',
    '应对·起动【场上/回合1次】:把我方场上1张名称含有【反浩克装甲】的结附卡解除至我方战区。如此做后，该卡本回合获得战力+1000。',
    NULL,
    'card/faces/BP01/BP01-047-SR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-047-SR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-048-R',
    'BP01',
    '「特工预感」黑寡妇',
    'CHARACTER',
    2,
    'YELLOW',
    NULL,
    '人类/复仇者联盟',
    1,
    1500,
    'R',
    '起动【战区/回合1次】：展示我方卡组顶3张卡，把其中最多3张卡按任意顺序放回卡组顶，并且把剩余的卡牌按任意顺序放回卡组底。',
    NULL,
    'card/faces/BP01/BP01-048-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-048-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-049-R',
    'BP01',
    '「私人恩怨」浩克',
    'CHARACTER',
    5,
    'YELLOW',
    NULL,
    '人类/复仇者联盟',
    1,
    5000,
    'R',
    '常驻【战区】:我方回合，此卡和敌方后卫区角色的距离变更为1。',
    NULL,
    'card/faces/BP01/BP01-049-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-049-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-050-R',
    'BP01',
    '「超导感电」雷神',
    'CHARACTER',
    3,
    'YELLOW',
    NULL,
    '阿斯加德/复仇者联盟',
    1,
    3500,
    'R',
    '触发【场上】：此卡因号召放置进场时，敌方场上1张角色本回合不能移动。',
    NULL,
    'card/faces/BP01/BP01-050-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-050-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-051-R',
    'BP01',
    '「援助打击」黑寡妇',
    'CHARACTER',
    2,
    'YELLOW',
    NULL,
    '人类/复仇者联盟',
    1,
    2000,
    'R',
    '触发【撤退】：场上的此卡因号召放置进撤退区时，我方场上1张黄色角色本回合获得战力+2000。',
    NULL,
    'card/faces/BP01/BP01-051-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-051-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-052-R',
    'BP01',
    '「追迹者」反浩克装甲',
    'CHARACTER',
    2,
    'YELLOW',
    NULL,
    '机械',
    1,
    2500,
    'R',
    '触发【手牌】：我方Lv4或以上，特征含有【机械】的角色放置进场时，若我方场上不存在名称含有【反浩克装甲】的角色，则可以把手牌的此卡放置进我方场上。',
    NULL,
    'card/faces/BP01/BP01-052-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-052-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-053-R',
    'BP01',
    '「下克上」反浩克装甲',
    'CHARACTER',
    3,
    'YELLOW',
    NULL,
    '机械',
    1,
    3000,
    'R',
    '触发【战区】：此卡攻击时，若攻击目标为Lv4或以上的角色，则此卡本回合获得战力+1500。',
    NULL,
    'card/faces/BP01/BP01-053-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-053-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-054-R',
    'BP01',
    '「眷族重塑」奥创',
    'CHARACTER',
    1,
    'YELLOW',
    NULL,
    '机械',
    1,
    1000,
    'R',
    '触发【场上】：此卡因号召放置进场时，可以舍弃我方手牌1张特征含有【机械】的角色。如此做后，把我方撤退区1张Lv4或以上，名称含有【奥创】的角色移回手牌。',
    NULL,
    'card/faces/BP01/BP01-054-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-054-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-055-R',
    'BP01',
    '「另辟蹊径」黄蜂女',
    'CHARACTER',
    1,
    'YELLOW',
    NULL,
    '人类/复仇者联盟',
    1,
    1000,
    'R',
    '常驻【战区】：敌方先锋区角色的战力每有3000，此卡获得R+1。',
    NULL,
    'card/faces/BP01/BP01-055-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-055-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-056-R',
    'BP01',
    '「短兵相接」雷神',
    'CHARACTER',
    4,
    'YELLOW',
    NULL,
    '阿斯加德/复仇者联盟',
    1,
    4000,
    'R',
    '常驻【战区】：若敌方先锋区角色的R为1，则此卡获得战力+1500。',
    NULL,
    'card/faces/BP01/BP01-056-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-056-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-057-R',
    'BP01',
    '「残械组装」幻视',
    'CHARACTER',
    5,
    'YELLOW',
    NULL,
    '机械/复仇者联盟',
    2,
    3500,
    'R',
    '触发【场上】：此卡因号召放置进场时，把我方撤退区1张Lv1，特征含有【机械】的角色放置进我方基地。',
    NULL,
    'card/faces/BP01/BP01-057-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-057-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-058-R',
    'BP01',
    '「超频加载」钢铁侠',
    'CHARACTER',
    6,
    'YELLOW',
    NULL,
    '人类/复仇者联盟',
    1,
    3500,
    'R',
    '常驻【场上】：若敌方战区角色的R合计数为3或以上，则此卡获得战力+3500。',
    NULL,
    'card/faces/BP01/BP01-058-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-058-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-059-R',
    'BP01',
    '「狩猎本能」黑豹',
    'CHARACTER',
    6,
    'YELLOW',
    NULL,
    '人类/复仇者联盟/瓦坎达',
    3,
    4000,
    'R',
    '触发【场上】：此卡因号召放置进场时，此卡本回合获得能力【强袭】（常驻【战区】：若此卡因攻击战胜，则判定此卡成功攻击破绽）。',
    NULL,
    'card/faces/BP01/BP01-059-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-059-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-060-R',
    'BP01',
    '「反坦克」战争机器',
    'CHARACTER',
    2,
    'YELLOW',
    NULL,
    '人类/复仇者联盟',
    1,
    2000,
    'R',
    '触发【场上】：此卡因号召放置进场时，敌方先锋区角色本回合获得战力-1000。',
    NULL,
    'card/faces/BP01/BP01-060-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-060-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-061-MR',
    'BP01',
    '「如影随形」新黑豹',
    'CHARACTER',
    6,
    'BLUE',
    NULL,
    '人类/瓦坎达',
    2,
    6000,
    'MR',
    '应对·起动【手牌】：撤退我方基地2张卡。如此做后，把手牌的此卡和我方战区1张Lv4或以上，和此卡名称不同的蓝色角色互相替换。',
    NULL,
    'card/faces/BP01/BP01-061-MR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-061-MR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-061-UR',
    'BP01',
    '「如影随形」新黑豹',
    'CHARACTER',
    6,
    'BLUE',
    NULL,
    '人类/瓦坎达',
    2,
    6000,
    'UR',
    '应对·起动【手牌】：撤退我方基地2张卡。如此做后，把手牌的此卡和我方战区1张Lv4或以上，和此卡名称不同的蓝色角色互相替换。',
    NULL,
    'card/faces/BP01/BP01-061-UR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-061-UR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-062-MR',
    'BP01',
    '「自由意志」美国队长',
    'CHARACTER',
    4,
    'BLUE',
    NULL,
    '人类/复仇者联盟',
    3,
    1000,
    'MR',
    '应对·起动【场上/回合1次】：此卡进行1次战基移动。如此做后，我方战区1张角色本回合获得战力+1000。 常驻【基地】：此卡免疫Lv4或以下的角色效果。',
    NULL,
    'card/faces/BP01/BP01-062-MR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-062-MR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-062-SEC',
    'BP01',
    '「自由意志」美国队长',
    'CHARACTER',
    4,
    'BLUE',
    NULL,
    '人类/复仇者联盟',
    3,
    1000,
    'SEC',
    '应对·起动【场上/回合1次】：此卡进行1次战基移动。如此做后，我方战区1张角色本回合获得战力+1000。 常驻【基地】：此卡免疫Lv4或以下的角色效果。',
    NULL,
    'card/faces/BP01/BP01-062-SEC.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-062-SEC');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-062-UR',
    'BP01',
    '「自由意志」美国队长',
    'CHARACTER',
    4,
    'BLUE',
    NULL,
    '人类/复仇者联盟',
    3,
    1000,
    'UR',
    '应对·起动【场上/回合1次】：此卡进行1次战基移动。如此做后，我方战区1张角色本回合获得战力+1000。 常驻【基地】：此卡免疫Lv4或以下的角色效果。',
    NULL,
    'card/faces/BP01/BP01-062-UR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-062-UR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-063-MR',
    'BP01',
    '「时间诱拐」洛基',
    'CHARACTER',
    4,
    'BLUE',
    NULL,
    '阿斯加德/时间犯',
    1,
    3500,
    'MR',
    '触发【手牌】：我方战区角色战力减少时，可以裁剪手牌的此卡。如此做后，裁剪敌方战区1张战力3500或以下的角色。',
    NULL,
    'card/faces/BP01/BP01-063-MR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-063-MR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-063-UR',
    'BP01',
    '「时间诱拐」洛基',
    'CHARACTER',
    4,
    'BLUE',
    NULL,
    '阿斯加德/时间犯',
    1,
    3500,
    'UR',
    '触发【手牌】：我方战区角色战力减少时，可以裁剪手牌的此卡。如此做后，裁剪敌方战区1张战力3500或以下的角色。',
    NULL,
    'card/faces/BP01/BP01-063-UR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-063-UR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-064-GR',
    'BP01',
    '「量子核心」蚁人',
    'CHARACTER',
    4,
    'BLUE',
    NULL,
    '人类/复仇者联盟',
    1,
    4000,
    'GR',
    '常驻【战区】：战斗阶段，若我方战区存在除此卡以外的蓝色角色，则此卡获得战力+2500。',
    NULL,
    'card/faces/BP01/BP01-064-GR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-064-GR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-065-GR',
    'BP01',
    '「物资装填」猎鹰',
    'CHARACTER',
    4,
    'BLUE',
    NULL,
    '人类/复仇者联盟',
    1,
    5500,
    'GR',
    '触发【撤退】：此卡因战败放置进撤退区时，把我方卡组顶2张卡盖放进我方基地。',
    NULL,
    'card/faces/BP01/BP01-065-GR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-065-GR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-065-MR',
    'BP01',
    '「物资装填」猎鹰',
    'CHARACTER',
    4,
    'BLUE',
    NULL,
    '人类/复仇者联盟',
    1,
    5500,
    'MR',
    '触发【撤退】：此卡因战败放置进撤退区时，把我方卡组顶2张卡盖放进我方基地。',
    NULL,
    'card/faces/BP01/BP01-065-MR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-065-MR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-066-GR',
    'BP01',
    '「力挽狂澜」美国队长',
    'CHARACTER',
    4,
    'BLUE',
    NULL,
    '人类/复仇者联盟',
    1,
    4500,
    'GR',
    '触发【手牌】：我方战区Lv4或以下，特征含有【人类】的角色因战败或敌方效果放置进撤退区时，可以裁剪手牌的此卡。如此做后，把该角色放置进我方场上。',
    NULL,
    'card/faces/BP01/BP01-066-GR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-066-GR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-067-GR',
    'BP01',
    '「敌意焦点」冬兵',
    'CHARACTER',
    3,
    'BLUE',
    NULL,
    '人类/复仇者联盟',
    1,
    4000,
    'GR',
    '触发【场上/回合1次】：敌方角色攻击时，可以把该角色的攻击目标变更为其他符合攻击规则的目标，由我方选择。',
    NULL,
    'card/faces/BP01/BP01-067-GR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-067-GR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-068-GR',
    'BP01',
    '「瓦坎达万岁」新黑豹',
    'CHARACTER',
    6,
    'BLUE',
    NULL,
    '人类/瓦坎达',
    2,
    4500,
    'GR',
    '触发【撤退】：此卡因战败放置进撤退区时，若战胜此卡的角色特征含有【人类】，则撤退该角色。',
    NULL,
    'card/faces/BP01/BP01-068-GR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-068-GR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-069-GR',
    'BP01',
    '「神奇呼唤」霹雳火',
    'CHARACTER',
    4,
    'BLUE',
    NULL,
    '人类/神奇四侠',
    1,
    1000,
    'GR',
    '起动【场上/回合1次】：把我方手牌1张LvX+1的角色和我方战区1张除此卡以外的角色互相替换，X为我方战区的该角色Lv。如此做后，我方抽1张卡，并且把场上的此卡移回卡组底。',
    NULL,
    'card/faces/BP01/BP01-069-GR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-069-GR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-070-SR',
    'BP01',
    '「时间引导」洛基',
    'CHARACTER',
    3,
    'BLUE',
    NULL,
    '阿斯加德/时间犯',
    1,
    1000,
    'SR',
    '触发【场上/回合1次】：我方蓝色角色放置进场时，可以把我方卡组顶1张卡盖放进我方基地。',
    NULL,
    'card/faces/BP01/BP01-070-SR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-070-SR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-071-SR',
    'BP01',
    '「移形换影」新黑豹',
    'CHARACTER',
    5,
    'BLUE',
    NULL,
    '人类/瓦坎达',
    1,
    5000,
    'SR',
    '常驻【先锋】：此卡获得战力+1000。 常驻【侧翼】：此卡获得R+2。',
    NULL,
    'card/faces/BP01/BP01-071-SR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-071-SR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-072-MR',
    'BP01',
    '「防护力场」隐形女',
    'CHARACTER',
    4,
    'BLUE',
    NULL,
    '人类/神奇四侠',
    2,
    3000,
    'MR',
    '触发【手牌】：我方破绽被攻击时，可以裁剪手牌的此卡。如此做后，把我方撤退区1张除名称含有【隐形女】以外的角色移回卡组底，并且敌方战区1张角色本回合获得2。',
    NULL,
    'card/faces/BP01/BP01-072-MR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-072-MR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-072-SR',
    'BP01',
    '「防护力场」隐形女',
    'CHARACTER',
    4,
    'BLUE',
    NULL,
    '人类/神奇四侠',
    2,
    3000,
    'SR',
    '触发【手牌】：我方破绽被攻击时，可以裁剪手牌的此卡。如此做后，把我方撤退区1张除名称含有【隐形女】以外的角色移回卡组底，并且敌方战区1张角色本回合获得2。',
    NULL,
    'card/faces/BP01/BP01-072-SR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-072-SR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-073-MR',
    'BP01',
    '「神奇灌注」霹雳火',
    'CHARACTER',
    4,
    'BLUE',
    NULL,
    '人类/神奇四侠',
    2,
    4000,
    'MR',
    '触发【场上/回合1次】：我方卡牌移回卡组底时，此卡本回合获得R+1、战力+X000，X为我方场上特征含有【神奇四侠】的卡牌数。',
    NULL,
    'card/faces/BP01/BP01-073-MR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-073-MR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-073-SR',
    'BP01',
    '「神奇灌注」霹雳火',
    'CHARACTER',
    4,
    'BLUE',
    NULL,
    '人类/神奇四侠',
    2,
    4000,
    'SR',
    '触发【场上/回合1次】：我方卡牌移回卡组底时，此卡本回合获得R+1、战力+X000，X为我方场上特征含有【神奇四侠】的卡牌数。',
    NULL,
    'card/faces/BP01/BP01-073-SR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-073-SR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-074-MR',
    'BP01',
    '「智慧诅咒」神奇先生',
    'CHARACTER',
    4,
    'BLUE',
    NULL,
    '人类/神奇四侠',
    4,
    2000,
    'MR',
    '应对·起动【后卫/回合1次】：若我方手牌数为4张，则把我方手牌1张Lv4的角色放置进我方场上，否则把我方撤退区1张角色移回卡组底。',
    NULL,
    'card/faces/BP01/BP01-074-MR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-074-MR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-074-SR',
    'BP01',
    '「智慧诅咒」神奇先生',
    'CHARACTER',
    4,
    'BLUE',
    NULL,
    '人类/神奇四侠',
    4,
    2000,
    'SR',
    '应对·起动【后卫/回合1次】：若我方手牌数为4张，则把我方手牌1张Lv4的角色放置进我方场上，否则把我方撤退区1张角色移回卡组底。',
    NULL,
    'card/faces/BP01/BP01-074-SR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-074-SR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-075-MR',
    'BP01',
    '「力量承托」石头人',
    'CHARACTER',
    4,
    'BLUE',
    NULL,
    '人类/神奇四侠',
    1,
    5000,
    'MR',
    '常驻【场上】：我方手牌所有特征含有【神奇四侠】的角色均获得Lv-1。',
    NULL,
    'card/faces/BP01/BP01-075-MR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-075-MR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-075-SR',
    'BP01',
    '「力量承托」石头人',
    'CHARACTER',
    4,
    'BLUE',
    NULL,
    '人类/神奇四侠',
    1,
    5000,
    'SR',
    '常驻【场上】：我方手牌所有特征含有【神奇四侠】的角色均获得Lv-1。',
    NULL,
    'card/faces/BP01/BP01-075-SR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-075-SR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-076-SR',
    'BP01',
    '「时间尽头」洛基',
    'CHARACTER',
    1,
    'BLUE',
    NULL,
    '阿斯加德/时间犯',
    1,
    500,
    'SR',
    '常驻【场上】：此卡获得战力+X000，X为我方撤退区名称含有【洛基】的角色数。',
    NULL,
    'card/faces/BP01/BP01-076-SR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-076-SR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-077-SR',
    'BP01',
    '「我需要你」美国队长',
    'CHARACTER',
    3,
    'BLUE',
    NULL,
    '人类/复仇者联盟',
    1,
    3500,
    'SR',
    '触发【场上】：此卡因号召放置进场时，我方抽1张卡。如此做后，把我方卡组顶1张卡盖放进我方基地。',
    NULL,
    'card/faces/BP01/BP01-077-SR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-077-SR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-078-R',
    'BP01',
    '「殊死空投」猎鹰',
    'CHARACTER',
    2,
    'BLUE',
    NULL,
    '人类/复仇者联盟',
    1,
    2000,
    'R',
    '触发【撤退】：此卡因战败放置进撤退区时，可以展示我方卡组顶1张卡，若该卡为Lv4的蓝色角色，则把该角色放置进我方基地，否则舍弃该角色。',
    NULL,
    'card/faces/BP01/BP01-078-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-078-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-079-R',
    'BP01',
    '「鹰之俯瞰」鹰眼',
    'CHARACTER',
    1,
    'BLUE',
    NULL,
    '人类/复仇者联盟',
    2,
    500,
    'R',
    '常驻【场上】：若敌方场上存在战力5000或以上的角色，则我方后卫区角色获得R+1。',
    NULL,
    'card/faces/BP01/BP01-079-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-079-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-080-R',
    'BP01',
    '「杀身成仁」美国队长',
    'CHARACTER',
    3,
    'BLUE',
    NULL,
    '人类/复仇者联盟',
    1,
    3000,
    'R',
    '触发【手牌】：敌方Lv3的角色攻击时，若双方战区角色Lv合计数相比下我方较低，则可以裁剪手牌的此卡。如此做后，撤退该角色。',
    NULL,
    'card/faces/BP01/BP01-080-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-080-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-081-R',
    'BP01',
    '「量子纠缠」蚁人',
    'CHARACTER',
    2,
    'BLUE',
    NULL,
    '人类/复仇者联盟',
    1,
    2000,
    'R',
    '触发【手牌】：我方蓝色角色被攻击时，可以把手牌的此卡结附于敌方攻击中的角色。 常驻【场上】：此卡结附的角色的R变更为1。',
    NULL,
    'card/faces/BP01/BP01-081-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-081-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-082-R',
    'BP01',
    '「感官剥离」夜魔侠',
    'CHARACTER',
    3,
    'BLUE',
    NULL,
    '人类/捍卫者联盟',
    1,
    3500,
    'R',
    '常驻【战区】：敌方先锋区特征含有【人类】的角色获得战力-500。',
    NULL,
    'card/faces/BP01/BP01-082-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-082-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-083-R',
    'BP01',
    '「自由守望」新美国队长',
    'CHARACTER',
    3,
    'BLUE',
    NULL,
    '人类/复仇者联盟',
    2,
    1500,
    'R',
    '应对（常驻【手牌】：此卡可以应对号召。）',
    NULL,
    'card/faces/BP01/BP01-083-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-083-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-084-R',
    'BP01',
    '「自由视野」新美国队长',
    'CHARACTER',
    1,
    'BLUE',
    NULL,
    '人类/复仇者联盟',
    1,
    1000,
    'R',
    '常驻【战区】：若双方战区角色数相同，则我方先锋区角色获得R+1。',
    NULL,
    'card/faces/BP01/BP01-084-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-084-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-085-R',
    'BP01',
    '「虚空放逐」绯红女巫',
    'CHARACTER',
    3,
    'BLUE',
    NULL,
    '变种人/复仇者联盟',
    2,
    2500,
    'R',
    '触发【场上】：回合结束时，可以裁剪我方撤退区1张角色。如此做后，我方场上1张角色进行1次战基移动。',
    NULL,
    'card/faces/BP01/BP01-085-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-085-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-086-R',
    'BP01',
    '「战术传授」夜魔侠',
    'CHARACTER',
    4,
    'BLUE',
    NULL,
    '人类/捍卫者联盟',
    1,
    4500,
    'R',
    '常驻【场上】：我方先锋区特征含有【人类】的角色获得战力+500。',
    NULL,
    'card/faces/BP01/BP01-086-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-086-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-087-R',
    'BP01',
    '「闪光箭」鹰眼',
    'CHARACTER',
    2,
    'BLUE',
    NULL,
    '人类/复仇者联盟',
    3,
    500,
    'R',
    '应对·起动【战区/回合1次】：舍弃我方手牌1张特征含有【复仇者联盟】的角色。如此做后，敌方战区1张角色本回合获得战力-1000。',
    NULL,
    'card/faces/BP01/BP01-087-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-087-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-088-R',
    'BP01',
    '「雷达感官」夜魔侠',
    'CHARACTER',
    3,
    'BLUE',
    NULL,
    '人类/捍卫者联盟',
    1,
    2500,
    'R',
    '常驻【战区】：若敌方战区存在战力5000或以上的角色，则此卡获得R+3。',
    NULL,
    'card/faces/BP01/BP01-088-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-088-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-089-R',
    'BP01',
    '「量子领域」蚁人',
    'CHARACTER',
    3,
    'BLUE',
    NULL,
    '人类/复仇者联盟',
    1,
    2500,
    'R',
    '常驻【战区】：若敌方战区只存在1张Lv6的角色，则此卡获得R+1，该角色获得1。',
    NULL,
    'card/faces/BP01/BP01-089-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-089-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-090-R',
    'BP01',
    '「自由裁决」美国队长',
    'CHARACTER',
    3,
    'BLUE',
    NULL,
    '人类/复仇者联盟',
    1,
    500,
    'R',
    '起动【手牌】：裁剪手牌的此卡。如此做后，我方本回合跳过战斗阶段，并且敌方战区1张角色本回合获得战力-X，X为我方战区角色的战力合计数。',
    NULL,
    'card/faces/BP01/BP01-090-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-090-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-091-MR',
    'BP01',
    '「黑客箭」鹰眼',
    'CHARACTER',
    6,
    'GREEN',
    NULL,
    '人类/复仇者联盟',
    4,
    5000,
    'MR',
    '触发【场上/回合1次】：敌方基地卡牌增加时，若我方场上只存在绿色角色，则可以舍弃我方1张手牌。如此做后，撤退敌方基地2张盖卡。',
    NULL,
    'card/faces/BP01/BP01-091-MR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-091-MR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-091-UR',
    'BP01',
    '「黑客箭」鹰眼',
    'CHARACTER',
    6,
    'GREEN',
    NULL,
    '人类/复仇者联盟',
    4,
    5000,
    'UR',
    '触发【场上/回合1次】：敌方基地卡牌增加时，若我方场上只存在绿色角色，则可以舍弃我方1张手牌。如此做后，撤退敌方基地2张盖卡。',
    NULL,
    'card/faces/BP01/BP01-091-UR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-091-UR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-092-MR',
    'BP01',
    '「量子叠加」蚁人',
    'CHARACTER',
    2,
    'GREEN',
    NULL,
    '人类/复仇者联盟',
    2,
    1500,
    'MR',
    '触发【撤退】：场上的此卡放置进撤退区时，裁剪撤退区的此卡。如此做后，若我方基地存在4张或以上的卡牌，则可以把我方手牌1张特征含有【复仇者联盟】的角色放置进我方基地。',
    NULL,
    'card/faces/BP01/BP01-092-MR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-092-MR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-092-UR',
    'BP01',
    '「量子叠加」蚁人',
    'CHARACTER',
    2,
    'GREEN',
    NULL,
    '人类/复仇者联盟',
    2,
    1500,
    'UR',
    '触发【撤退】：场上的此卡放置进撤退区时，裁剪撤退区的此卡。如此做后，若我方基地存在4张或以上的卡牌，则可以把我方手牌1张特征含有【复仇者联盟】的角色放置进我方基地。',
    NULL,
    'card/faces/BP01/BP01-092-UR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-092-UR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-093-MR',
    'BP01',
    '「雷霆知音」女雷神',
    'CHARACTER',
    4,
    'GREEN',
    NULL,
    '人类/阿斯加德',
    3,
    3500,
    'MR',
    '触发【侧翼】：此卡攻击时，若我方侧翼区除此卡以外的1张战力4000或以下的角色本回合未攻击，则此卡本回合获得战力+X，X为该角色的战力。如此做后，该角色本回合不能攻击。',
    NULL,
    'card/faces/BP01/BP01-093-MR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-093-MR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-093-UR',
    'BP01',
    '「雷霆知音」女雷神',
    'CHARACTER',
    4,
    'GREEN',
    NULL,
    '人类/阿斯加德',
    3,
    3500,
    'UR',
    '触发【侧翼】：此卡攻击时，若我方侧翼区除此卡以外的1张战力4000或以下的角色本回合未攻击，则此卡本回合获得战力+X，X为该角色的战力。如此做后，该角色本回合不能攻击。',
    NULL,
    'card/faces/BP01/BP01-093-UR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-093-UR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-094-GR',
    'BP01',
    '「故事之神」洛基',
    'CHARACTER',
    6,
    'GREEN',
    NULL,
    '阿斯加德',
    1,
    6500,
    'GR',
    '触发【场上】：我方回合结束时，可以撤退敌方场上1张LvX的角色或X张盖卡，X为我方战区本回合未进行攻击的角色数。',
    NULL,
    'card/faces/BP01/BP01-094-GR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-094-GR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-095-GR',
    'BP01',
    '「舍身取义」美国队长',
    'CHARACTER',
    3,
    'GREEN',
    NULL,
    '人类/复仇者联盟',
    1,
    4000,
    'GR',
    '触发【撤退】：此卡因效果舍弃放置进撤退区时，可以裁剪撤退区的此卡。如此做后，把敌方战区1张Lv3或以下的角色移动至敌方基地。',
    NULL,
    'card/faces/BP01/BP01-095-GR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-095-GR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-096-GR',
    'BP01',
    '「我身作盾」新美国队长',
    'CHARACTER',
    3,
    'GREEN',
    NULL,
    '人类/复仇者联盟',
    1,
    4000,
    'GR',
    '应对·起动【场上/回合1次】：若我方战区存在除此卡以外的名称含有【美国队长】的角色，则舍弃我方1张手牌。如此做后，把场上的此卡和该角色互相替换。',
    NULL,
    'card/faces/BP01/BP01-096-GR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-096-GR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-096-MR',
    'BP01',
    '「我身作盾」新美国队长',
    'CHARACTER',
    3,
    'GREEN',
    NULL,
    '人类/复仇者联盟',
    1,
    4000,
    'MR',
    '应对·起动【场上/回合1次】：若我方战区存在除此卡以外的名称含有【美国队长】的角色，则舍弃我方1张手牌。如此做后，把场上的此卡和该角色互相替换。',
    NULL,
    'card/faces/BP01/BP01-096-MR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-096-MR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-097-GR',
    'BP01',
    '「原初变种」纳摩',
    'CHARACTER',
    3,
    'GREEN',
    NULL,
    '变种人/亚特兰蒂斯',
    1,
    4000,
    'GR',
    '常驻【战区】：我方先锋区角色的R变更为3。',
    NULL,
    'card/faces/BP01/BP01-097-GR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-097-GR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-097-MR',
    'BP01',
    '「原初变种」纳摩',
    'CHARACTER',
    3,
    'GREEN',
    NULL,
    '变种人/亚特兰蒂斯',
    1,
    4000,
    'MR',
    '常驻【战区】：我方先锋区角色的R变更为3。',
    NULL,
    'card/faces/BP01/BP01-097-MR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-097-MR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-098-GR',
    'BP01',
    '「混沌灵视」绯红女巫',
    'CHARACTER',
    1,
    'GREEN',
    NULL,
    '变种人/复仇者联盟',
    2,
    500,
    'GR',
    '触发【撤退】：此卡因效果舍弃放置进撤退区时，可以把撤退区的此卡结附于敌方场上1张角色。如此做后，撤退该角色除此卡以外的所有结附卡。 常驻【场上】：此卡结附的角色只能攻击敌方Lv6的角色。',
    NULL,
    'card/faces/BP01/BP01-098-GR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-098-GR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-099-GR',
    'BP01',
    '「皮姆箭」鹰眼',
    'CHARACTER',
    3,
    'GREEN',
    NULL,
    '人类/复仇者联盟',
    2,
    2500,
    'GR',
    '触发【场上】：敌方Lv4或以上的角色放置进场时，可以舍弃我方2张手牌。如此做后，裁剪场上的此卡和敌方场上1张角色。',
    NULL,
    'card/faces/BP01/BP01-099-GR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-099-GR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-100-SR',
    'BP01',
    '「听声缴械」夜魔侠',
    'CHARACTER',
    1,
    'GREEN',
    NULL,
    '人类/捍卫者联盟',
    1,
    500,
    'SR',
    '应对（常驻【手牌】：此卡可以应对号召。） 触发【场上】：此卡放置进场时，敌方场上1张角色本回合获得1。',
    NULL,
    'card/faces/BP01/BP01-100-SR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-100-SR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-101-MR',
    'BP01',
    '「分导箭」鹰眼',
    'CHARACTER',
    3,
    'GREEN',
    NULL,
    '人类/复仇者联盟',
    2,
    2000,
    'MR',
    '触发【场上/回合1次】：我方破绽被攻击时，可以撤退我方基地最多3张卡。如此做后，可以把我方手牌1张原本LvX的角色放置进我方战区，X为因此效果撤退的卡牌数。',
    NULL,
    'card/faces/BP01/BP01-101-MR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-101-MR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-101-SR',
    'BP01',
    '「分导箭」鹰眼',
    'CHARACTER',
    3,
    'GREEN',
    NULL,
    '人类/复仇者联盟',
    2,
    2000,
    'SR',
    '触发【场上/回合1次】：我方破绽被攻击时，可以撤退我方基地最多3张卡。如此做后，可以把我方手牌1张原本LvX的角色放置进我方战区，X为因此效果撤退的卡牌数。',
    NULL,
    'card/faces/BP01/BP01-101-SR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-101-SR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-102-SR',
    'BP01',
    '「量子坍塌」蚁人',
    'CHARACTER',
    6,
    'GREEN',
    NULL,
    '人类/复仇者联盟',
    1,
    6000,
    'SR',
    '触发【撤退】：场上的此卡放置进撤退区时，裁剪撤退区的此卡。如此做后，裁剪敌方基地1张Lv3或以下的角色。',
    NULL,
    'card/faces/BP01/BP01-102-SR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-102-SR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-103-SR',
    'BP01',
    '「战术恐吓」冬兵',
    'CHARACTER',
    3,
    'GREEN',
    NULL,
    '人类/复仇者联盟',
    1,
    4000,
    'SR',
    '触发【场上】：此卡战基移动时，可以把敌方先锋区角色移动至敌方后卫区。',
    NULL,
    'card/faces/BP01/BP01-103-SR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-103-SR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-104-SR',
    'BP01',
    '「时光倒流」绯红女巫',
    'CHARACTER',
    6,
    'GREEN',
    NULL,
    '变种人/复仇者联盟',
    3,
    4000,
    'SR',
    '触发【撤退】：此卡因效果舍弃放置进撤退区时，可以把双方战区各1张Lv相同的角色移回卡组顶。 起动【战区/回合1次】：把我方撤退区1张角色移回卡组底。',
    NULL,
    'card/faces/BP01/BP01-104-SR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-104-SR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-105-SR',
    'BP01',
    '「精神同步」新黑豹 r',
    'CHARACTER',
    3,
    'GREEN',
    NULL,
    '人类/瓦坎达',
    1,
    3500,
    'SR',
    '触发【场上/回合1次】：我方角色攻击时，若攻击目标为战力5000或以上的角色，则该我方角色本回合获得战力+1000。',
    NULL,
    'card/faces/BP01/BP01-105-SR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-105-SR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-106-MR',
    'BP01',
    '「搜寻战友」猎鹰',
    'CHARACTER',
    1,
    'GREEN',
    NULL,
    '人类/复仇者联盟',
    2,
    500,
    'MR',
    '触发【场上】：此卡因号召放置进场时，可以舍弃我方手牌1张绿色角色。如此做后，我方抽2张卡。',
    NULL,
    'card/faces/BP01/BP01-106-MR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-106-MR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-106-SR',
    'BP01',
    '「搜寻战友」猎鹰',
    'CHARACTER',
    1,
    'GREEN',
    NULL,
    '人类/复仇者联盟',
    2,
    500,
    'SR',
    '触发【场上】：此卡因号召放置进场时，可以舍弃我方手牌1张绿色角色。如此做后，我方抽2张卡。',
    NULL,
    'card/faces/BP01/BP01-106-SR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-106-SR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-107-SR',
    'BP01',
    '「自由威光」美国队长',
    'CHARACTER',
    3,
    'GREEN',
    NULL,
    '人类/复仇者联盟',
    1,
    3000,
    'SR',
    '常驻【战区】：若我方战区存在战力4000或以上的绿色角色，则敌方战区所有Lv4或以下的角色均获得战力-500。 触发【撤退】：此卡因效果舍弃放置进撤退区时，可以把撤退区的此卡盖放进我方基地。',
    NULL,
    'card/faces/BP01/BP01-107-SR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-107-SR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-108-R',
    'BP01',
    '「安全气囊箭」鹰眼',
    'CHARACTER',
    3,
    'GREEN',
    NULL,
    '人类/复仇者联盟',
    2,
    2000,
    'R',
    '起动【场上/回合1次】：舍弃我方手牌1张特征含有【复仇者联盟】的角色。如此做后，把我方卡组顶2张卡盖放进我方基地。',
    NULL,
    'card/faces/BP01/BP01-108-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-108-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-109-R',
    'BP01',
    '「听声辨位」夜魔侠',
    'CHARACTER',
    1,
    'GREEN',
    NULL,
    '人类/捍卫者联盟',
    1,
    500,
    'R',
    '触发【场上】：此卡放置进场时，敌方把其1张手牌盖放进敌方基地。',
    NULL,
    'card/faces/BP01/BP01-109-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-109-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-110-R',
    'BP01',
    '「谎言之神」洛基',
    'CHARACTER',
    1,
    'GREEN',
    NULL,
    '阿斯加德',
    1,
    500,
    'R',
    '常驻【基地】：若我方战区不存在角色，则此卡的Lv变更为5。',
    NULL,
    'card/faces/BP01/BP01-110-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-110-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-111-R',
    'BP01',
    '「量子门」蚁人',
    'CHARACTER',
    3,
    'GREEN',
    NULL,
    '人类/复仇者联盟',
    2,
    2500,
    'R',
    '触发【撤退】：场上的此卡放置进撤退区时，裁剪撤退区的此卡。如此做后，我方场上1张角色进行1次战基移动。',
    NULL,
    'card/faces/BP01/BP01-111-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-111-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-112-R',
    'BP01',
    '「漆黑笼罩」夜魔侠',
    'CHARACTER',
    1,
    'GREEN',
    NULL,
    '人类/捍卫者联盟',
    1,
    2000,
    'R',
    '触发【场上】：此卡放置进场时，撤退敌方基地1张盖卡。',
    NULL,
    'card/faces/BP01/BP01-112-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-112-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-113-R',
    'BP01',
    '「突如其来」猎鹰',
    'CHARACTER',
    6,
    'GREEN',
    NULL,
    '人类/复仇者联盟',
    1,
    6000,
    'R',
    '触发【场上/回合1次】：敌方后卫区角色攻击时，此卡可以进行1次战基移动。',
    NULL,
    'card/faces/BP01/BP01-113-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-113-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-114-R',
    'BP01',
    '「打带跑」冬兵',
    'CHARACTER',
    3,
    'GREEN',
    NULL,
    '人类/复仇者联盟',
    2,
    2000,
    'R',
    '触发【场上】：此卡战基移动时，敌方战区1张角色本回合获得战力-1000。',
    NULL,
    'card/faces/BP01/BP01-114-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-114-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-115-R',
    'BP01',
    '「诡计之神」洛基',
    'CHARACTER',
    6,
    'GREEN',
    NULL,
    '阿斯加德',
    1,
    6000,
    'R',
    '常驻【场上】：敌方后卫区角色获得Lv-2。',
    NULL,
    'card/faces/BP01/BP01-115-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-115-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-116-R',
    'BP01',
    '「殿后撤离」猎鹰',
    'CHARACTER',
    3,
    'GREEN',
    NULL,
    '人类/复仇者联盟',
    1,
    3500,
    'R',
    '触发【场上】：敌方侧翼区角色攻击时，可以把我方战区1张角色移动至我方基地。如此做后，此卡进行1次战基移动。',
    NULL,
    'card/faces/BP01/BP01-116-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-116-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-117-R',
    'BP01',
    '「先祖赐福」新黑豹',
    'CHARACTER',
    1,
    'GREEN',
    NULL,
    '人类/瓦坎达',
    2,
    1500,
    'R',
    '常驻【战区】：若敌方先锋区角色的颜色和此卡不同，则我方先锋区角色获得R+1。',
    NULL,
    'card/faces/BP01/BP01-117-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-117-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-118-R',
    'BP01',
    '「跑带搜」冬兵',
    'CHARACTER',
    3,
    'GREEN',
    NULL,
    '人类/复仇者联盟',
    1,
    2500,
    'R',
    '触发【场上】：此卡战基移动时，我方抽1张卡。如此做后，舍弃我方1张手牌。',
    NULL,
    'card/faces/BP01/BP01-118-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-118-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-119-MR',
    'BP01',
    '「公平正义」美国队长',
    'CHARACTER',
    6,
    'GREEN',
    NULL,
    '人类/复仇者联盟',
    1,
    6000,
    'MR',
    '触发【撤退】：此卡因效果舍弃放置进撤退区时，可以把撤退区的此卡和我方场上1张Lv6的角色互相替换。',
    NULL,
    'card/faces/BP01/BP01-119-MR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-119-MR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-119-R',
    'BP01',
    '「公平正义」美国队长',
    'CHARACTER',
    6,
    'GREEN',
    NULL,
    '人类/复仇者联盟',
    1,
    6000,
    'R',
    '触发【撤退】：此卡因效果舍弃放置进撤退区时，可以把撤退区的此卡和我方场上1张Lv6的角色互相替换。',
    NULL,
    'card/faces/BP01/BP01-119-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-119-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'BP01-120-R',
    'BP01',
    '「量子隧穿」蚁人',
    'CHARACTER',
    3,
    'GREEN',
    NULL,
    '人类/复仇者联盟',
    1,
    1500,
    'R',
    '触发【撤退】：场上的此卡放置进撤退区时，裁剪撤退区的此卡。如此做后，把我方虚空区1张除名称含有【蚁人】以外的Lv3的角色移回卡组顶。',
    NULL,
    'card/faces/BP01/BP01-120-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'BP01-120-R');

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

COMMIT;
