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
-- 卡牌特征表
-- =====================================================
CREATE TABLE IF NOT EXISTS mtcg_card_feature (
    id              BIGSERIAL       PRIMARY KEY,
    code            VARCHAR(32)      NOT NULL UNIQUE,
    name            VARCHAR(64)      NOT NULL,
    bg_color        VARCHAR(16),
    create_time     TIMESTAMP        NOT NULL DEFAULT NOW(),
    update_time     TIMESTAMP        NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_card_feature_code ON mtcg_card_feature (code);

-- =====================================================
-- 卡牌表
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
    create_time     TIMESTAMP       NOT NULL DEFAULT NOW(),
    update_time     TIMESTAMP       NOT NULL DEFAULT NOW()
);

-- =====================================================
-- 卡牌-特征关联表
-- =====================================================
CREATE TABLE IF NOT EXISTS mtcg_card_feature_rel (
    id              BIGSERIAL       PRIMARY KEY,
    card_id         BIGINT          NOT NULL,
    feature_id      BIGINT          NOT NULL,
    create_time     TIMESTAMP       NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_card_feature_rel UNIQUE (card_id, feature_id)
);

CREATE INDEX IF NOT EXISTS idx_card_feature_rel_card ON mtcg_card_feature_rel (card_id);
CREATE INDEX IF NOT EXISTS idx_card_feature_rel_feature ON mtcg_card_feature_rel (feature_id);

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

    IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trigger_mtcg_card_feature_update_time') THEN
        CREATE TRIGGER trigger_mtcg_card_feature_update_time
            BEFORE UPDATE ON mtcg_card_feature
            FOR EACH ROW EXECUTE FUNCTION update_update_time();
    END IF;
END $$;
