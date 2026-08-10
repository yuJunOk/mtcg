package com.aris.mtcg.manager;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

/**
 * JWT 令牌黑名单（基于 jti）
 *
 * <p>进程内 ConcurrentHashMap 实现，登出或刷新轮换时写入。
 *
 * @author pengYuJun
 */
@Component
public class TokenBlacklistManager {

    /** jti → 过期时间戳（毫秒） */
    private final ConcurrentHashMap<String, Long> blacklist = new ConcurrentHashMap<>();

    /**
     * 将令牌加入黑名单
     *
     * @param jti 令牌 ID
     * @param expireAtMs 令牌原过期时间（毫秒时间戳）
     */
    public void blacklist(String jti, long expireAtMs) {
        if (jti == null || jti.isBlank()) {
            return;
        }
        blacklist.put(jti, expireAtMs);
        cleanup();
    }

    /**
     * 判断令牌是否在黑名单中
     *
     * @param jti 令牌 ID
     * @return true 表示已拉黑
     */
    public boolean isBlacklisted(String jti) {
        if (jti == null || jti.isBlank()) {
            return false;
        }
        Long expireAt = blacklist.get(jti);
        if (expireAt == null) {
            return false;
        }
        if (expireAt < System.currentTimeMillis()) {
            blacklist.remove(jti, expireAt);
            return false;
        }
        return true;
    }

    /** 清理已过期的黑名单条目 */
    public void cleanup() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<String, Long>> it = blacklist.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Long> entry = it.next();
            if (entry.getValue() < now) {
                it.remove();
            }
        }
    }
}
