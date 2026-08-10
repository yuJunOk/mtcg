package com.aris.mtcg.manager;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

/**
 * 滑动窗口限流管理器
 *
 * <p>按 key（通常为 ip+path 或 user+path）统计时间窗内请求次数。
 *
 * @author pengYuJun
 */
@Component
public class RateLimitManager {

    /** key → 请求时间戳队列（毫秒） */
    private final ConcurrentHashMap<String, Deque<Long>> windows = new ConcurrentHashMap<>();

    /**
     * 尝试通过限流检查
     *
     * @param key 限流键
     * @param limit 窗口内最大请求数
     * @param windowMs 窗口时长（毫秒）
     * @return true 表示允许；false 表示超限
     */
    public boolean tryAcquire(String key, int limit, long windowMs) {
        long now = System.currentTimeMillis();
        Deque<Long> deque = windows.computeIfAbsent(key, k -> new ArrayDeque<>());
        synchronized (deque) {
            while (!deque.isEmpty() && now - deque.peekFirst() >= windowMs) {
                deque.pollFirst();
            }
            if (deque.size() >= limit) {
                return false;
            }
            deque.addLast(now);
            return true;
        }
    }
}
