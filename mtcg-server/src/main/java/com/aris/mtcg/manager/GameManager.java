package com.aris.mtcg.manager;

import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

/**
 * 对局缓存管理器。
 *
 * <p>内存中维护进行中的对局上下文（GameEngine + 元数据 + 操作序号 + 锁）。 进行中对局常驻内存，操作直接读写内存；结束对局从缓存移除，状态已落库。
 *
 * <p>崩溃恢复：缓存未命中但 DB 中 status=IN_PROGRESS 时，由 GameService 触发恢复重建 GameContext。
 *
 * @author pengYuJun
 */
@Component
public class GameManager {

    /** gameId → GameContext，对局隔离 */
    private final ConcurrentHashMap<String, GameContext> games = new ConcurrentHashMap<>();

    /** 缓存对局上下文 */
    public void put(String gameId, GameContext context) {
        games.put(gameId, context);
    }

    /** 获取对局上下文，不存在返回 null */
    public GameContext get(String gameId) {
        return games.get(gameId);
    }

    /** 是否在缓存中 */
    public boolean contains(String gameId) {
        return games.containsKey(gameId);
    }

    /** 移除对局上下文（对局结束时调用） */
    public GameContext remove(String gameId) {
        return games.remove(gameId);
    }

    /** 当前缓存对局数（监控指标，NFR10） */
    public int activeCount() {
        return games.size();
    }
}
