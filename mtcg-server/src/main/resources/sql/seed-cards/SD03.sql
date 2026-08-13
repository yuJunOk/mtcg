-- 卡牌/产品种子：SD03 空间
-- product_code = SD03，共 21 张卡
-- 幂等：按 product_code / card_code 去重插入

BEGIN;

-- ========== 产品 ==========
INSERT INTO mtcg_product (product_code, product_name, release_date, description, category)
SELECT 'SD03', '空间', '2026-06-17', NULL, 'STARTER'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_product WHERE product_code = 'SD03');

-- ========== 卡牌 ==========
INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD03-001-MR',
    'SD03',
    '「至高守望」美国队长',
    'CHARACTER',
    6,
    'BLUE',
    NULL,
    '人类/复仇者联盟',
    1,
    500,
    'MR',
    '应对（常驻【手牌】：此卡可以应对号召。） 触发【场上】：此卡放置进场时，此卡本回合获得战力+X，X为敌方场上1张角色的战力。',
    NULL,
    'card/faces/SD03/SD03-001-MR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD03-001-MR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD03-001-SEC',
    'SD03',
    '「至高守望」美国队长',
    'CHARACTER',
    6,
    'BLUE',
    NULL,
    '人类/复仇者联盟',
    1,
    500,
    'SEC',
    '应对（常驻【手牌】：此卡可以应对号召。） 触发【场上】：此卡放置进场时，此卡本回合获得战力+X，X为敌方场上1张角色的战力。',
    NULL,
    'card/faces/SD03/SD03-001-SEC.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD03-001-SEC');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD03-001-UR',
    'SD03',
    '「至高守望」美国队长',
    'CHARACTER',
    6,
    'BLUE',
    NULL,
    '人类/复仇者联盟',
    1,
    500,
    'UR',
    '应对（常驻【手牌】：此卡可以应对号召。） 触发【场上】：此卡放置进场时，此卡本回合获得战力+X，X为敌方场上1张角色的战力。',
    NULL,
    'card/faces/SD03/SD03-001-UR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD03-001-UR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD03-002-GR',
    'SD03',
    '「折翼风压」猎鹰',
    'CHARACTER',
    3,
    'BLUE',
    NULL,
    '人类/复仇者联盟',
    1,
    3500,
    'GR',
    '触发【场上】：此卡放置进场时，把敌方侧翼区1张Lv4或以下的角色移动至敌方基地。如此做后，若敌方基地存在4张或以上的卡牌，则盖伏该角色。',
    NULL,
    'card/faces/SD03/SD03-002-GR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD03-002-GR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD03-003-GR',
    'SD03',
    '「紧急号召」尼克·弗瑞',
    'CHARACTER',
    4,
    'BLUE',
    NULL,
    '人类/神盾局',
    2,
    4000,
    'GR',
    '触发【场上】：此卡战基移动时，可以把我方手牌1张特征含有【复仇者联盟】的角色放置进我方战区。如此做后，撤退此卡。',
    NULL,
    'card/faces/SD03/SD03-003-GR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD03-003-GR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD03-004-SR',
    'SD03',
    '「壁影寒光」新黑豹',
    'CHARACTER',
    3,
    'BLUE',
    NULL,
    '人类/瓦坎达',
    1,
    3500,
    'SR',
    '常驻【后卫】：此卡获得R+2。 常驻【基地】：此卡获得Lv+1。',
    NULL,
    'card/faces/SD03/SD03-004-SR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD03-004-SR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD03-005-SR',
    'SD03',
    '「战地回收」美国队长',
    'CHARACTER',
    2,
    'BLUE',
    NULL,
    '人类/复仇者联盟',
    1,
    2500,
    'SR',
    '触发【场上/回合1次】：此卡战基移动时，可以把我方基地1张盖卡移回手牌。',
    NULL,
    'card/faces/SD03/SD03-005-SR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD03-005-SR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD03-006-SR',
    'SD03',
    '「牵引箭」鹰眼',
    'CHARACTER',
    4,
    'BLUE',
    NULL,
    '人类/复仇者联盟',
    3,
    2500,
    'SR',
    '唯一（常驻【场上】：我方场上不能存在其他和此卡名称相同的卡牌，此效果不能失去。） 起动【战区/回合1次】：舍弃我方卡组顶2张卡。如此做后，我方抽1张卡。',
    NULL,
    'card/faces/SD03/SD03-006-SR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD03-006-SR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD03-007-R',
    'SD03',
    '「量子虹吸」蚁人',
    'CHARACTER',
    1,
    'BLUE',
    NULL,
    '人类/复仇者联盟',
    1,
    1000,
    'R',
    '触发【场上】：此卡战基移动时，可以裁剪场上的此卡。如此做后，把我方卡组顶3张卡盖放进我方基地。',
    NULL,
    'card/faces/SD03/SD03-007-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD03-007-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD03-008-R',
    'SD03',
    '「自由鼓舞」新美国队长',
    'CHARACTER',
    6,
    'BLUE',
    NULL,
    '人类/复仇者联盟',
    1,
    6000,
    'R',
    '起动【战区/回合2次】：撤退我方基地1张盖卡。如此做后，我方战区1张角色本回合获得战力+500。',
    NULL,
    'card/faces/SD03/SD03-008-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD03-008-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD03-009-R',
    'SD03',
    '「火力掩护」菲尔·科尔森',
    'CHARACTER',
    3,
    'BLUE',
    NULL,
    '人类/神盾局',
    2,
    2500,
    'R',
    '触发【场上/回合1次】：我方角色战胜时，可以把场上的此卡和该角色互相替换。',
    NULL,
    'card/faces/SD03/SD03-009-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD03-009-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD03-010-R',
    'SD03',
    '「天降正义」美国队长',
    'CHARACTER',
    6,
    'BLUE',
    NULL,
    '人类/复仇者联盟',
    0,
    5000,
    'R',
    '触发【手牌】：敌方Lv6的角色放置进场时，可以把手牌的此卡放置进我方战区。',
    NULL,
    'card/faces/SD03/SD03-010-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD03-010-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD03-011-R',
    'SD03',
    '「先祖领域」新黑豹',
    'CHARACTER',
    3,
    'BLUE',
    NULL,
    '人类/瓦坎达',
    2,
    2500,
    'R',
    '常驻【先锋】：敌方侧翼区所有角色均获得战力-1000。 常驻【侧翼】：敌方先锋区角色获得战力-500。',
    NULL,
    'card/faces/SD03/SD03-011-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD03-011-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD03-012-R',
    'SD03',
    '「英雄档案」美国队长',
    'CHARACTER',
    4,
    'BLUE',
    NULL,
    '人类/复仇者联盟',
    1,
    5500,
    'R',
    '无效果',
    NULL,
    'card/faces/SD03/SD03-012-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD03-012-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD03-013-R',
    'SD03',
    '「英雄档案」猎鹰',
    'CHARACTER',
    3,
    'BLUE',
    NULL,
    '人类/复仇者联盟',
    2,
    3500,
    'R',
    '无效果',
    NULL,
    'card/faces/SD03/SD03-013-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD03-013-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD03-014-R',
    'SD03',
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
    'card/faces/SD03/SD03-014-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD03-014-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD03-015-R',
    'SD03',
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
    'card/faces/SD03/SD03-015-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD03-015-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD03-016-R',
    'SD03',
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
    'card/faces/SD03/SD03-016-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD03-016-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD03-017-R',
    'SD03',
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
    'card/faces/SD03/SD03-017-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD03-017-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD03-018-R',
    'SD03',
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
    'card/faces/SD03/SD03-018-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD03-018-R');

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
