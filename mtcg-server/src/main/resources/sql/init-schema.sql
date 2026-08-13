-- MTCG 初始化脚本
-- 超英击战 / Hero Rush TCG
-- PostgreSQL 语法
-- 脚本幂等：可重复执行（IF NOT EXISTS / WHERE NOT EXISTS）

-- =====================================================
-- 用户表
-- 注意：user 是 PostgreSQL 保留字，表名须加双引号
-- =====================================================
CREATE TABLE IF NOT EXISTS "mtcg_user" (
    id              BIGSERIAL       PRIMARY KEY,
    usercode        VARCHAR(32)     NOT NULL,
    password_hash   VARCHAR(72)     NOT NULL,
    username        VARCHAR(64),
    avatar          VARCHAR(256),
    role            VARCHAR(16)     NOT NULL DEFAULT 'PLAYER',
    status          VARCHAR(16)     NOT NULL DEFAULT 'ACTIVE',
    create_time     TIMESTAMP       NOT NULL DEFAULT NOW(),
    update_time     TIMESTAMP       NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_user_usercode UNIQUE (usercode),
    CONSTRAINT ck_user_role CHECK (role IN ('PLAYER', 'CARD_ADMIN', 'SYS_ADMIN', 'AI')),
    CONSTRAINT ck_user_status CHECK (status IN ('ACTIVE', 'DISABLED'))
);

CREATE INDEX IF NOT EXISTS idx_user_role ON "mtcg_user" (role);

-- =====================================================
-- 卡牌表（特征 traits：中文标签斜杠分隔，枚举见 EnumTrait）
-- =====================================================
CREATE TABLE IF NOT EXISTS mtcg_card (
    id              BIGSERIAL       PRIMARY KEY,
    card_code       VARCHAR(32)     NOT NULL UNIQUE,
    product_code    VARCHAR(16),
    card_name       VARCHAR(128)    NOT NULL,
    card_type       VARCHAR(16)     NOT NULL,
    level           SMALLINT,
    color           VARCHAR(16),
    environment     VARCHAR(16),
    traits          VARCHAR(256),
    attack_range    SMALLINT,
    power           SMALLINT,
    rarity          VARCHAR(16)     NOT NULL,
    effect_text     TEXT,
    effect_json     TEXT,
    image_path      VARCHAR(256),
    create_time     TIMESTAMP       NOT NULL DEFAULT NOW(),
    update_time     TIMESTAMP       NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_card_type CHECK (card_type IN ('CHARACTER', 'RUSH_POINT')),
    CONSTRAINT ck_card_rarity CHECK (rarity IN (
        'C', 'R', 'SR', 'GR', 'UR', 'MR', 'SEC', 'HR', 'LR', 'PR', 'ER', 'TR'
    ))
);

CREATE INDEX IF NOT EXISTS idx_card_type    ON mtcg_card (card_type);
CREATE INDEX IF NOT EXISTS idx_card_product ON mtcg_card (product_code);

-- 兼容已有库：补 traits 列
ALTER TABLE mtcg_card ADD COLUMN IF NOT EXISTS traits VARCHAR(256);

-- =====================================================
-- 产品表
-- =====================================================
CREATE TABLE IF NOT EXISTS mtcg_product (
    id              BIGSERIAL       PRIMARY KEY,
    product_code    VARCHAR(16)     NOT NULL UNIQUE,
    product_name    VARCHAR(128)    NOT NULL,
    release_date    DATE,
    description     TEXT,
    category        VARCHAR(16)     NOT NULL DEFAULT 'OTHER',
    image_path      VARCHAR(256),
    create_time     TIMESTAMP       NOT NULL DEFAULT NOW(),
    update_time     TIMESTAMP       NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_product_category CHECK (category IN ('STARTER', 'BOOSTER', 'OTHER'))
);

ALTER TABLE mtcg_product ADD COLUMN IF NOT EXISTS image_path VARCHAR(256);

-- 商品多图：JSON 数组文本，如 ["product/BP01/a.png","product/BP01/b.png"]
ALTER TABLE mtcg_product ADD COLUMN IF NOT EXISTS image_paths TEXT;

-- 产品分类（存量库补列；SD→基础卡组，BP→补充包）
ALTER TABLE mtcg_product ADD COLUMN IF NOT EXISTS category VARCHAR(16);
UPDATE mtcg_product SET category = 'OTHER' WHERE category IS NULL;
UPDATE mtcg_product SET category = 'STARTER'
WHERE category = 'OTHER' AND upper(product_code) LIKE 'SD%';
UPDATE mtcg_product SET category = 'BOOSTER'
WHERE category = 'OTHER' AND upper(product_code) LIKE 'BP%';
ALTER TABLE mtcg_product ALTER COLUMN category SET DEFAULT 'OTHER';
ALTER TABLE mtcg_product ALTER COLUMN category SET NOT NULL;

-- 将旧单图迁入数组（仅当 image_paths 为空且 image_path 有值）
UPDATE mtcg_product
SET image_paths = '["' || replace(image_path, '"', '\"') || '"]'
WHERE (image_paths IS NULL OR btrim(image_paths) = '' OR btrim(image_paths) = '[]')
  AND image_path IS NOT NULL
  AND btrim(image_path) <> '';

-- 废弃特征字典表（已有库执行一次即可；新建库无此表则忽略）
DROP TRIGGER IF EXISTS trigger_mtcg_card_feature_update_time ON mtcg_card_feature;
DROP TABLE IF EXISTS mtcg_card_feature_rel;
DROP TABLE IF EXISTS mtcg_card_feature;

-- =====================================================
-- 审计日志表
-- =====================================================
CREATE TABLE IF NOT EXISTS mtcg_audit_log (
  id BIGSERIAL PRIMARY KEY,
  actor_id BIGINT,
  actor_usercode VARCHAR(32),
  action VARCHAR(32) NOT NULL,
  resource_type VARCHAR(32) NOT NULL,
  resource_id VARCHAR(64),
  detail TEXT,
  create_time TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_audit_log_create_time ON mtcg_audit_log (create_time DESC);

-- =====================================================
-- 迭代三：卡组表
-- main/rush 存有序条目 JSON：[{"cardCode":"...","quantity":n},...]；数组序=卡组内排序
-- =====================================================
CREATE TABLE IF NOT EXISTS mtcg_deck (
    id                  BIGSERIAL       PRIMARY KEY,
    user_id             BIGINT          NOT NULL,
    deck_code           VARCHAR(16)     NOT NULL,
    deck_name           VARCHAR(64)     NOT NULL,
    main_deck_codes     TEXT            NOT NULL DEFAULT '[]',
    rush_deck_codes     TEXT            NOT NULL DEFAULT '[]',
    is_valid            BOOLEAN         NOT NULL DEFAULT FALSE,
    status              VARCHAR(16)     NOT NULL DEFAULT 'DRAFT',
    is_public           BOOLEAN         NOT NULL DEFAULT FALSE,
    is_copyable         BOOLEAN         NOT NULL DEFAULT FALSE,
    sort_order          INTEGER         NOT NULL DEFAULT 0,
    tags                VARCHAR(256),
    cover_card_code     VARCHAR(32),
    create_time         TIMESTAMP       NOT NULL DEFAULT NOW(),
    update_time         TIMESTAMP       NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_deck_code UNIQUE (deck_code)
);

ALTER TABLE mtcg_deck ADD COLUMN IF NOT EXISTS cover_card_code VARCHAR(32);

-- 对外卡组编码 + 公开/可复制开关（存量回填）
ALTER TABLE mtcg_deck ADD COLUMN IF NOT EXISTS deck_code VARCHAR(16);
ALTER TABLE mtcg_deck ADD COLUMN IF NOT EXISTS is_public BOOLEAN;
ALTER TABLE mtcg_deck ADD COLUMN IF NOT EXISTS is_copyable BOOLEAN;
UPDATE mtcg_deck
SET deck_code = 'D-' || upper(substr(md5(random()::text || id::text), 1, 8))
WHERE deck_code IS NULL OR btrim(deck_code) = '';
UPDATE mtcg_deck SET is_public = FALSE WHERE is_public IS NULL;
UPDATE mtcg_deck SET is_copyable = FALSE WHERE is_copyable IS NULL;
ALTER TABLE mtcg_deck ALTER COLUMN deck_code SET NOT NULL;
ALTER TABLE mtcg_deck ALTER COLUMN is_public SET DEFAULT FALSE;
ALTER TABLE mtcg_deck ALTER COLUMN is_public SET NOT NULL;
ALTER TABLE mtcg_deck ALTER COLUMN is_copyable SET DEFAULT FALSE;
ALTER TABLE mtcg_deck ALTER COLUMN is_copyable SET NOT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS uk_deck_code ON mtcg_deck (deck_code);

CREATE INDEX IF NOT EXISTS idx_deck_user_id ON mtcg_deck (user_id);
CREATE INDEX IF NOT EXISTS idx_deck_user_sort ON mtcg_deck (user_id, sort_order);
CREATE INDEX IF NOT EXISTS idx_deck_public ON mtcg_deck (is_public) WHERE is_public = TRUE;

-- 卡组状态枚举（READY=可用 / DRAFT=草稿），与 is_valid 同步
ALTER TABLE mtcg_deck ADD COLUMN IF NOT EXISTS status VARCHAR(16);
UPDATE mtcg_deck
SET status = CASE WHEN is_valid THEN 'READY' ELSE 'DRAFT' END
WHERE status IS NULL OR btrim(status) = '';
ALTER TABLE mtcg_deck ALTER COLUMN status SET DEFAULT 'DRAFT';
ALTER TABLE mtcg_deck ALTER COLUMN status SET NOT NULL;

-- =====================================================
-- 迭代三：卡牌收藏表
-- =====================================================
CREATE TABLE IF NOT EXISTS mtcg_card_collection (
    id                  BIGSERIAL       PRIMARY KEY,
    user_id             BIGINT          NOT NULL,
    card_code           VARCHAR(32)     NOT NULL,
    quantity            INTEGER         NOT NULL DEFAULT 1,
    tags                VARCHAR(256),
    note                VARCHAR(256),
    create_time         TIMESTAMP       NOT NULL DEFAULT NOW(),
    update_time         TIMESTAMP       NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_collection_user_code UNIQUE (user_id, card_code),
    CONSTRAINT ck_collection_qty CHECK (quantity >= 0)
);

CREATE INDEX IF NOT EXISTS idx_collection_user_id ON mtcg_card_collection (user_id);

-- =====================================================
-- 迭代七：对局记录表（操作流水 + 回合快照混合持久化，D2）
-- player1 = 发起方；先攻由应用层/引擎决定，不强制等于 player1
-- =====================================================
CREATE TABLE IF NOT EXISTS mtcg_game_record (
    id                  BIGSERIAL       PRIMARY KEY,
    game_code           VARCHAR(16)     NOT NULL,
    player1_id          BIGINT          NOT NULL,
    player2_id          BIGINT,
    deck1_id            BIGINT          NOT NULL,
    deck2_id            BIGINT,
    winner              VARCHAR(16),
    game_mode           VARCHAR(16)     NOT NULL,
    status              VARCHAR(16)     NOT NULL,
    turn_snapshot       TEXT,
    action_log          TEXT            NOT NULL DEFAULT '[]',
    create_time         TIMESTAMP       NOT NULL DEFAULT NOW(),
    update_time         TIMESTAMP       NOT NULL DEFAULT NOW(),
    end_time            TIMESTAMP,
    CONSTRAINT uk_game_code UNIQUE (game_code),
    CONSTRAINT ck_game_winner CHECK (winner IS NULL OR winner IN ('PLAYER1', 'PLAYER2', 'DRAW')),
    CONSTRAINT ck_game_mode   CHECK (game_mode IN ('CASUAL', 'RANKED', 'AI')),
    CONSTRAINT ck_game_status CHECK (status IN ('WAITING', 'IN_PROGRESS', 'FINISHED'))
);

CREATE INDEX IF NOT EXISTS idx_game_player1     ON mtcg_game_record (player1_id);
CREATE INDEX IF NOT EXISTS idx_game_player2     ON mtcg_game_record (player2_id);
CREATE INDEX IF NOT EXISTS idx_game_create_time ON mtcg_game_record (create_time DESC);

-- 对外对局编码（存量回填）
ALTER TABLE mtcg_game_record ADD COLUMN IF NOT EXISTS game_code VARCHAR(16);
UPDATE mtcg_game_record
SET game_code = 'G-' || upper(substr(md5(random()::text || id::text), 1, 8))
WHERE game_code IS NULL OR btrim(game_code) = '';
ALTER TABLE mtcg_game_record ALTER COLUMN game_code SET NOT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS uk_game_code ON mtcg_game_record (game_code);

-- 已有库：等待房间需要 player2/deck2 可空，status 含 WAITING
ALTER TABLE mtcg_game_record ALTER COLUMN player2_id DROP NOT NULL;
ALTER TABLE mtcg_game_record ALTER COLUMN deck2_id DROP NOT NULL;
ALTER TABLE mtcg_game_record DROP CONSTRAINT IF EXISTS ck_game_status;
ALTER TABLE mtcg_game_record ADD CONSTRAINT ck_game_status
    CHECK (status IN ('WAITING', 'IN_PROGRESS', 'FINISHED'));

-- 已有库：turn_snapshot 从 JSONB 改为 TEXT，避免 JDBC 写入 varchar→jsonb 类型错误
ALTER TABLE mtcg_game_record
    ALTER COLUMN turn_snapshot TYPE TEXT USING turn_snapshot::text;

-- =====================================================
-- 自动更新 update_time 触发器
-- =====================================================
DO $$
BEGIN
    CREATE OR REPLACE FUNCTION update_update_time()
    RETURNS TRIGGER AS $func$
    BEGIN
        NEW.update_time = NOW();
        RETURN NEW;
    END;
    $func$ LANGUAGE plpgsql;

    IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trigger_mtcg_user_update_time') THEN
        CREATE TRIGGER trigger_mtcg_user_update_time
            BEFORE UPDATE ON "mtcg_user"
            FOR EACH ROW EXECUTE FUNCTION update_update_time();
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trigger_mtcg_card_update_time') THEN
        CREATE TRIGGER trigger_mtcg_card_update_time
            BEFORE UPDATE ON mtcg_card
            FOR EACH ROW EXECUTE FUNCTION update_update_time();
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trigger_mtcg_product_update_time') THEN
        CREATE TRIGGER trigger_mtcg_product_update_time
            BEFORE UPDATE ON mtcg_product
            FOR EACH ROW EXECUTE FUNCTION update_update_time();
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trigger_deck_update_time') THEN
        CREATE TRIGGER trigger_deck_update_time
            BEFORE UPDATE ON mtcg_deck
            FOR EACH ROW EXECUTE FUNCTION update_update_time();
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trigger_collection_update_time') THEN
        CREATE TRIGGER trigger_collection_update_time
            BEFORE UPDATE ON mtcg_card_collection
            FOR EACH ROW EXECUTE FUNCTION update_update_time();
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trigger_game_record_update_time') THEN
        CREATE TRIGGER trigger_game_record_update_time
            BEFORE UPDATE ON mtcg_game_record
            FOR EACH ROW EXECUTE FUNCTION update_update_time();
    END IF;
END $$;
