-- MTCG 初始化脚本
-- 超英击战 / Hero Rush TCG
-- PostgreSQL 语法
-- 仅在数据库为空时执行一次（mode: embedded）

-- =====================================================
-- 用户表（本迭代核心）
-- 注意：user 是 PostgreSQL 保留字，建表与查询均须加双引号
-- =====================================================
CREATE TABLE IF NOT EXISTS "mtcg_user" (
    id              BIGSERIAL       PRIMARY KEY,
    username        VARCHAR(32)     NOT NULL,
    password_hash   VARCHAR(72)     NOT NULL,
    nickname        VARCHAR(64),
    avatar          VARCHAR(256),
    role            VARCHAR(16)     NOT NULL DEFAULT 'PLAYER',
    status          VARCHAR(16)     NOT NULL DEFAULT 'ACTIVE',
    create_time     TIMESTAMP       NOT NULL DEFAULT NOW(),
    update_time     TIMESTAMP       NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_user_username UNIQUE (username),
    CONSTRAINT ck_user_role   CHECK (role   IN ('PLAYER','CARD_ADMIN','SYS_ADMIN','AI')),
    CONSTRAINT ck_user_status CHECK (status IN ('ACTIVE','DISABLED'))
);

CREATE INDEX IF NOT EXISTS idx_user_role ON "mtcg_user" (role);

-- =====================================================
-- 卡牌表（迭代二使用，本迭代先建空表）
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
    CONSTRAINT ck_color     CHECK (color IS NULL OR color IN ('RED','YELLOW','BLUE','GREEN','ORANGE','PURPLE')),
    CONSTRAINT ck_rarity    CHECK (rarity IN ('C','R','SR','GR','UR','MR','SEC','HR','LR','PR','ER','TR')),
    CONSTRAINT ck_level     CHECK (level IS NULL OR (level >= 1 AND level <= 6))
);

CREATE INDEX IF NOT EXISTS idx_card_type    ON mtcg_card (card_type);
CREATE INDEX IF NOT EXISTS idx_card_product ON mtcg_card (product_code);

-- =====================================================
-- 产品表（迭代二使用，本迭代先建空表）
-- 卡包/商品系列目录，如 BP01 复仇者联盟补充包、SD01 蜘蛛侠预组牌
-- =====================================================
CREATE TABLE IF NOT EXISTS mtcg_product (
    id              BIGSERIAL       PRIMARY KEY,
    product_code    VARCHAR(16)     NOT NULL UNIQUE,
    product_name    VARCHAR(128)    NOT NULL,
    release_date    DATE,
    description     TEXT,
    create_time     TIMESTAMP       NOT NULL DEFAULT NOW(),
    update_time     TIMESTAMP       NOT NULL DEFAULT NOW()
);

-- =====================================================
-- 自动更新 update_time 触发器
-- =====================================================
DO $$
BEGIN
    CREATE OR REPLACE FUNCTION update_update_time()
    RETURNS TRIGGER AS $$
    BEGIN
        NEW.update_time = NOW();
        RETURN NEW;
    END;
    $$ LANGUAGE plpgsql;
END $$;

DO $$
BEGIN
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
END $$;
