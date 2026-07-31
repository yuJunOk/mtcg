-- MTCG 初始化脚本（骨架）
-- 超英击战 / Hero Rush TCG，后续按正式卡表与规则字段迭代
-- PostgreSQL 语法

CREATE TABLE IF NOT EXISTS mtcg_card (
    id           BIGSERIAL PRIMARY KEY,
    card_code    VARCHAR(64)  NOT NULL UNIQUE,
    card_name    VARCHAR(128) NOT NULL,
    card_type    VARCHAR(32)  NOT NULL,
    cost         INTEGER,
    attack       INTEGER,
    health       INTEGER,
    effect_text  TEXT,
    effect_json  TEXT,
    create_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_card_type ON mtcg_card (card_type);
CREATE INDEX IF NOT EXISTS idx_card_name ON mtcg_card (card_name);
