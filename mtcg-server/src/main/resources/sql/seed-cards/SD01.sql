-- 卡牌/产品种子：SD01 现实
-- product_code = SD01，共 21 张卡
-- 幂等：按 product_code / card_code 去重插入

BEGIN;

-- ========== 产品 ==========
INSERT INTO mtcg_product (product_code, product_name, release_date, description)
SELECT 'SD01', '现实', '2026-06-17', NULL
WHERE NOT EXISTS (SELECT 1 FROM mtcg_product WHERE product_code = 'SD01');

-- ========== 卡牌 ==========
INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD01-001-MR',
    'SD01',
    '「自毁程式」钢铁侠',
    'CHARACTER',
    6,
    'RED',
    NULL,
    '人类/复仇者联盟',
    3,
    3500,
    'MR',
    '触发【战区/回合1次】：此卡被结附时，若敌方战区存在Lv5或以上的角色，则可以撤退此卡的所有结附卡。如此做后，裁剪敌方场上1张LvX或以下的角色，X为因此效果撤退的结附卡Lv合计数。',
    NULL,
    'card/faces/SD01/SD01-001-MR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD01-001-MR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD01-001-SEC',
    'SD01',
    '「自毁程式」钢铁侠',
    'CHARACTER',
    6,
    'RED',
    NULL,
    '人类/复仇者联盟',
    3,
    3500,
    'SEC',
    '触发【战区/回合1次】：此卡被结附时，若敌方战区存在Lv5或以上的角色，则可以撤退此卡的所有结附卡。如此做后，裁剪敌方场上1张LvX或以下的角色，X为因此效果撤退的结附卡Lv合计数。',
    NULL,
    'card/faces/SD01/SD01-001-SEC.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD01-001-SEC');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD01-001-UR',
    'SD01',
    '「自毁程式」钢铁侠',
    'CHARACTER',
    6,
    'RED',
    NULL,
    '人类/复仇者联盟',
    3,
    3500,
    'UR',
    '触发【战区/回合1次】：此卡被结附时，若敌方战区存在Lv5或以上的角色，则可以撤退此卡的所有结附卡。如此做后，裁剪敌方场上1张LvX或以下的角色，X为因此效果撤退的结附卡Lv合计数。',
    NULL,
    'card/faces/SD01/SD01-001-UR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD01-001-UR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD01-002-GR',
    'SD01',
    '「MK44-远程特化」反浩克装甲',
    'CHARACTER',
    6,
    'RED',
    NULL,
    '机械',
    2,
    2500,
    'GR',
    '应对·起动【场上/回合1次】：把场上的此卡结附于我方场上1张角色。如此做后，撤退该角色1张除此卡以外的结附卡。 常驻【场上】：此卡结附的角色获得R+2、战力+2500。',
    NULL,
    'card/faces/SD01/SD01-002-GR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD01-002-GR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD01-003-GR',
    'SD01',
    '「协同作战」战争机器',
    'CHARACTER',
    2,
    'RED',
    NULL,
    '人类/复仇者联盟',
    1,
    3500,
    'GR',
    '触发【战区/回合1次】：我方角色的R或战力增加时，敌方战区1张角色本回合获得战力-1000。',
    NULL,
    'card/faces/SD01/SD01-003-GR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD01-003-GR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD01-004-SR',
    'SD01',
    '「弹幕防护」战争机器',
    'CHARACTER',
    3,
    'RED',
    NULL,
    '人类/复仇者联盟',
    2,
    2000,
    'SR',
    '常驻【战区】：敌方战斗阶段，此卡获得战力+X000,X为敌方战区角色数。',
    NULL,
    'card/faces/SD01/SD01-004-SR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD01-004-SR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD01-005-SR',
    'SD01',
    '「充盈呼唤」雷神',
    'CHARACTER',
    3,
    'RED',
    NULL,
    '阿斯加德/复仇者联盟',
    1,
    3500,
    'SR',
    '触发【场上】：此卡因号召放置进场时，可以撤退我方基地2张卡。如此做后，若因此效果撤退的卡牌均为红色，则我方抽2张卡。',
    NULL,
    'card/faces/SD01/SD01-005-SR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD01-005-SR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD01-006-SR',
    'SD01',
    '「寡妇蜇」黑寡妇',
    'CHARACTER',
    5,
    'RED',
    NULL,
    '人类/复仇者联盟',
    3,
    2500,
    'SR',
    '起动【手牌】：舍弃手牌的此卡。如此做后，撤退我方战区1张角色，我方基地1张卡，和敌方战区1张Lv5或以下的角色。',
    NULL,
    'card/faces/SD01/SD01-006-SR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD01-006-SR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD01-007-R',
    'SD01',
    '「火力投放」钢铁侠',
    'CHARACTER',
    2,
    'RED',
    NULL,
    '人类/复仇者联盟',
    1,
    2500,
    'R',
    '触发【场上】：此卡因号召放置进场时，可以舍弃我方1张手牌。如此做后，敌方战区1张Lv5或以下的角色本回合获得战力-2000。',
    NULL,
    'card/faces/SD01/SD01-007-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD01-007-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD01-008-R',
    'SD01',
    '「狂怒消散」浩克',
    'CHARACTER',
    5,
    'RED',
    NULL,
    '人类/复仇者联盟',
    1,
    3500,
    'R',
    '常驻【手牌】：若双方场上均不存在Lv4或以上的角色，则此卡获得Lv-2。',
    NULL,
    'card/faces/SD01/SD01-008-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD01-008-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD01-009-R',
    'SD01',
    '「MK44-脉冲特化」反浩克装甲',
    'CHARACTER',
    4,
    'RED',
    NULL,
    '机械',
    2,
    2500,
    'R',
    '起动【手牌】：舍弃我方1张除此卡以外的手牌。如此做后，把手牌的此卡结附于我方场上1张角色。 触发【撤退】：场上的此卡放置进撤退区时，展示我方基地1张盖卡。如此做后，把我方撤退区2张和该盖卡Lv相同的角色盖放进我方基地。',
    NULL,
    'card/faces/SD01/SD01-009-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD01-009-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD01-010-R',
    'SD01',
    '「红房魅影」黑寡妇',
    'CHARACTER',
    3,
    'RED',
    NULL,
    '人类/复仇者联盟',
    1,
    3500,
    'R',
    '起动【手牌】：把手牌的此卡结附于我方场上1张特征含有【人类】的角色。 常驻【场上】：此卡结附的角色获得R+1。',
    NULL,
    'card/faces/SD01/SD01-010-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD01-010-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD01-011-R',
    'SD01',
    '「对等打击」黑豹',
    'CHARACTER',
    6,
    'RED',
    NULL,
    '人类/复仇者联盟/瓦坎达',
    1,
    5000,
    'R',
    '应对（常驻【手牌】：此卡可以应对号召。）',
    NULL,
    'card/faces/SD01/SD01-011-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD01-011-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD01-012-R',
    'SD01',
    '「英雄档案」钢铁侠',
    'CHARACTER',
    6,
    'RED',
    NULL,
    '人类/复仇者联盟',
    2,
    6000,
    'R',
    '无效果',
    NULL,
    'card/faces/SD01/SD01-012-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD01-012-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD01-013-R',
    'SD01',
    '「英雄档案」浩克',
    'CHARACTER',
    3,
    'RED',
    NULL,
    '人类/复仇者联盟',
    1,
    4500,
    'R',
    '无效果',
    NULL,
    'card/faces/SD01/SD01-013-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD01-013-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD01-014-R',
    'SD01',
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
    'card/faces/SD01/SD01-014-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD01-014-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD01-015-R',
    'SD01',
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
    'card/faces/SD01/SD01-015-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD01-015-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD01-016-R',
    'SD01',
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
    'card/faces/SD01/SD01-016-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD01-016-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD01-017-R',
    'SD01',
    '「雷霆狂怒」雷神',
    'CHARACTER',
    6,
    'RED',
    NULL,
    '阿斯加德/复仇者联盟',
    2,
    5000,
    'R',
    '常驻【战区】：若我方战区只存在此卡，则此卡获得能力【连击】（常驻【战区】：此卡拥有第2次攻击机会）。',
    NULL,
    'card/faces/SD01/SD01-017-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD01-017-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD01-018-R',
    'SD01',
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
    'card/faces/SD01/SD01-018-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD01-018-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD01-019-C',
    'SD01',
    '冲击卡',
    'RUSH_POINT',
    NULL,
    'ORANGE',
    NULL,
    NULL,
    NULL,
    NULL,
    'C',
    NULL,
    NULL,
    'card/faces/SD01/SD01-019-C.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD01-019-C');

COMMIT;
