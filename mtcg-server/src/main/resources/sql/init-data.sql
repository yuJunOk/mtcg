-- MTCG 初始化数据
-- 仅在数据库为空时执行一次（mode: embedded）

-- 管理员账号（密码: admin123，BCrypt 加密）
INSERT INTO "mtcg_user" (username, password_hash, nickname, role, status)
SELECT 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMye.Q4E4bN9qo8uLOickgx2ZMRZoMye', '系统管理员', 'SYS_ADMIN', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM "mtcg_user" WHERE username = 'admin');
