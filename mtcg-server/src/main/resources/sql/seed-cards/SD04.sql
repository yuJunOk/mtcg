-- 卡牌/产品种子：SD04 时间
-- product_code = SD04，共 21 张卡
-- 幂等：按 product_code / card_code 去重插入

BEGIN;

-- ========== 产品 ==========
INSERT INTO mtcg_product (product_code, product_name, release_date, description, category)
SELECT 'SD04', '时间', '2026-06-17', NULL, 'STARTER'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_product WHERE product_code = 'SD04');

-- ========== 卡牌 ==========
INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD04-001-MR',
    'SD04',
    '「恶作剧之神」洛基',
    'CHARACTER',
    6,
    'GREEN',
    'S1',
    '阿斯加德',
    1,
    6000,
    'MR',
    '应对·起动【手牌】：把手牌的此卡结附于场上1张特征含有【人类】的角色。 常驻【场上】：此卡结附的角色的R变更为0，并且该角色获得战力+1000。',
    NULL,
    'card/faces/SD04/SD04-001-MR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD04-001-MR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD04-001-SEC',
    'SD04',
    '「恶作剧之神」洛基',
    'CHARACTER',
    6,
    'GREEN',
    'S1',
    '阿斯加德',
    1,
    6000,
    'SEC',
    '应对·起动【手牌】：把手牌的此卡结附于场上1张特征含有【人类】的角色。 常驻【场上】：此卡结附的角色的R变更为0，并且该角色获得战力+1000。',
    NULL,
    'card/faces/SD04/SD04-001-SEC.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD04-001-SEC');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD04-001-UR',
    'SD04',
    '「恶作剧之神」洛基',
    'CHARACTER',
    6,
    'GREEN',
    'S1',
    '阿斯加德',
    1,
    6000,
    'UR',
    '应对·起动【手牌】：把手牌的此卡结附于场上1张特征含有【人类】的角色。 常驻【场上】：此卡结附的角色的R变更为0，并且该角色获得战力+1000。',
    NULL,
    'card/faces/SD04/SD04-001-UR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD04-001-UR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD04-002-GR',
    'SD04',
    '「集束箭」鹰眼',
    'CHARACTER',
    6,
    'GREEN',
    'S1',
    '人类/复仇者联盟',
    3,
    500,
    'GR',
    '触发【场上】：此卡放置进场时，舍弃我方卡组顶2张卡。如此做后，敌方战区1张角色本回合获得战力-X000，X为因此效果舍弃的卡牌Lv合计数。',
    NULL,
    'card/faces/SD04/SD04-002-GR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD04-002-GR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD04-003-GR',
    'SD04',
    '「量子回溯」蚁人',
    'CHARACTER',
    3,
    'GREEN',
    'S1',
    '人类/复仇者联盟',
    0,
    5500,
    'GR',
    '触发【撤退】：场上的此卡放置进撤退区时，裁剪撤退区的此卡。如此做后，把我方战区1张Lv最高的角色和敌方战区1张Lv最低的角色移回手牌。',
    NULL,
    'card/faces/SD04/SD04-003-GR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD04-003-GR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD04-004-SR',
    'SD04',
    '「天降正义」新美国队长',
    'CHARACTER',
    3,
    'GREEN',
    'S1',
    '人类/复仇者联盟',
    1,
    3500,
    'SR',
    '拦截（应对·起动【战区/回合1次】：若此卡可以作为敌方攻击中的角色的攻击目标，则把该角色的攻击目标变更为此卡。）',
    NULL,
    'card/faces/SD04/SD04-004-SR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD04-004-SR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD04-005-SR',
    'SD04',
    '「量子传态」蚁人',
    'CHARACTER',
    3,
    'GREEN',
    'S1',
    '人类/复仇者联盟',
    2,
    2000,
    'SR',
    '触发【撤退】：场上的此卡放置进撤退区时，裁剪撤退区的此卡。如此做后，我方场上1张Lv3或以下的角色本回合获得能力【空袭】（常驻【战区】：即使敌方战区存在角色，此卡也可以把该角色所处战区作为破绽进行攻击）。',
    NULL,
    'card/faces/SD04/SD04-005-SR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD04-005-SR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD04-006-SR',
    'SD04',
    '「暗局运作」尼克·弗瑞',
    'CHARACTER',
    1,
    'GREEN',
    'S1',
    '人类/神盾局',
    2,
    1500,
    'SR',
    '触发【场上/回合1次】：此卡战基移动时，把我方场上1张特征含有【复仇者联盟】的角色移回手牌。如此做后，我方抽1张卡。',
    NULL,
    'card/faces/SD04/SD04-006-SR.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD04-006-SR');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD04-007-R',
    'SD04',
    '「蛇首督军」交叉骨',
    'CHARACTER',
    2,
    'GREEN',
    'S1',
    '人类/九头蛇',
    1,
    3000,
    'R',
    '起动【场上/回合1次】：我方场上1张角色进行1次战基移动。',
    NULL,
    'card/faces/SD04/SD04-007-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD04-007-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD04-008-R',
    'SD04',
    '「以退为进」泽莫男爵',
    'CHARACTER',
    5,
    'GREEN',
    'S1',
    '人类/九头蛇',
    0,
    6000,
    'R',
    '触发【撤退】：此卡因效果舍弃放置进撤退区时，把我方卡组顶2张卡盖放进我方基地。',
    NULL,
    'card/faces/SD04/SD04-008-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD04-008-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD04-009-R',
    'SD04',
    '「以退为进」美国队长',
    'CHARACTER',
    3,
    'GREEN',
    'S1',
    '人类/复仇者联盟',
    1,
    3500,
    'R',
    '触发【撤退】：此卡因效果舍弃放置进撤退区时，可以把撤退区的此卡放置进我方场上。如此做后，敌方把其卡组顶1张卡盖放进敌方基地。',
    NULL,
    'card/faces/SD04/SD04-009-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD04-009-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD04-010-R',
    'SD04',
    '「伏击蛇牙」海德拉',
    'CHARACTER',
    3,
    'GREEN',
    'S1',
    '人类/九头蛇',
    3,
    1000,
    'R',
    '应对（常驻【手牌】：此卡可以应对号召。）',
    NULL,
    'card/faces/SD04/SD04-010-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD04-010-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD04-011-R',
    'SD04',
    '「地下之声」鼹鼠人',
    'CHARACTER',
    4,
    'GREEN',
    'S1',
    '人类',
    1,
    3500,
    'R',
    '起动【战区/回合1次】：我方战区1张角色的R本回合变更为2。',
    NULL,
    'card/faces/SD04/SD04-011-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD04-011-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD04-012-R',
    'SD04',
    '「英雄档案」蚁人',
    'CHARACTER',
    3,
    'GREEN',
    'S1',
    '人类/复仇者联盟',
    0,
    5500,
    'R',
    '无效果',
    NULL,
    'card/faces/SD04/SD04-012-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD04-012-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD04-013-R',
    'SD04',
    '「英雄档案」鹰眼',
    'CHARACTER',
    5,
    'GREEN',
    'S1',
    '人类/复仇者联盟',
    5,
    3500,
    'R',
    '无效果',
    NULL,
    'card/faces/SD04/SD04-013-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD04-013-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD04-014-R',
    'SD04',
    '「公平正义」美国队长',
    'CHARACTER',
    6,
    'GREEN',
    'S1',
    '人类/复仇者联盟',
    1,
    6000,
    'R',
    '触发【撤退】：此卡因效果舍弃放置进撤退区时，可以把撤退区的此卡和我方场上1张Lv6的角色互相替换。',
    NULL,
    'card/faces/SD04/SD04-014-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD04-014-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD04-015-R',
    'SD04',
    '「量子门」蚁人',
    'CHARACTER',
    3,
    'GREEN',
    'S1',
    '人类/复仇者联盟',
    2,
    2500,
    'R',
    '触发【撤退】：场上的此卡放置进撤退区时，裁剪撤退区的此卡。如此做后，我方场上1张角色进行1次战基移动。',
    NULL,
    'card/faces/SD04/SD04-015-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD04-015-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD04-016-R',
    'SD04',
    '「诡计之神」洛基',
    'CHARACTER',
    6,
    'GREEN',
    'S1',
    '阿斯加德',
    1,
    6000,
    'R',
    '常驻【场上】：敌方后卫区角色获得Lv-2。',
    NULL,
    'card/faces/SD04/SD04-016-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD04-016-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD04-017-R',
    'SD04',
    '「打带跑」冬兵',
    'CHARACTER',
    3,
    'GREEN',
    'S1',
    '人类/复仇者联盟',
    2,
    2000,
    'R',
    '触发【场上】：此卡战基移动时，敌方战区1张角色本回合获得战力-1000。',
    NULL,
    'card/faces/SD04/SD04-017-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD04-017-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD04-018-R',
    'SD04',
    '「跑带搜」冬兵',
    'CHARACTER',
    3,
    'GREEN',
    'S1',
    '人类/复仇者联盟',
    1,
    2500,
    'R',
    '触发【场上】：此卡战基移动时，我方抽1张卡。如此做后，舍弃我方1张手牌。',
    NULL,
    'card/faces/SD04/SD04-018-R.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD04-018-R');

INSERT INTO mtcg_card (
    card_code, product_code, card_name, card_type, level, color,
    environment, traits, attack_range, power, rarity,
    effect_text, effect_json, image_path
)
SELECT
    'SD04-019-C',
    'SD04',
    '冲击卡',
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
    'card/faces/SD04/SD04-019-C.png'
WHERE NOT EXISTS (SELECT 1 FROM mtcg_card WHERE card_code = 'SD04-019-C');

COMMIT;
