package com.aris.mtcg.manager;

import com.aris.mtcg.domain.entity.GameDO;
import com.aris.mtcg.engine.GameEngine;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 对局上下文（非 Spring 管理，由 GameService 创建后放入 {@link GameManager}）。
 *
 * <p>持有引擎实例与对局元数据，操作序号单调递增，对局级锁串行化同对局操作。
 *
 * @author pengYuJun
 */
public class GameContext {

    /** 对局 ID（字符串形式，与 GameState.gameId 一致） */
    private final String gameId;

    /** 引擎实例（含 GameState，操作直接读写） */
    private final GameEngine engine;

    /** DB 元数据（含 player1Id/player2Id/status 等，落库时更新） */
    private GameDO record;

    /** 操作序号，单调递增，用于 action_log 排序与快照水位标记 */
    private long actionSeq;

    /** 对局级锁，串行化同对局操作 */
    private final ReentrantLock lock = new ReentrantLock();

    public GameContext(String gameId, GameEngine engine, GameDO record) {
        this.gameId = gameId;
        this.engine = engine;
        this.record = record;
        this.actionSeq = 0L;
    }

    /** 加锁 */
    public void lock() {
        lock.lock();
    }

    /** 解锁 */
    public void unlock() {
        lock.unlock();
    }

    /** 递增并返回下一个操作序号 */
    public long nextSeq() {
        return ++actionSeq;
    }

    public String getGameId() {
        return gameId;
    }

    public GameEngine getEngine() {
        return engine;
    }

    public GameDO getRecord() {
        return record;
    }

    public void setRecord(GameDO record) {
        this.record = record;
    }

    public long getActionSeq() {
        return actionSeq;
    }

    public void setActionSeq(long actionSeq) {
        this.actionSeq = actionSeq;
    }
}
