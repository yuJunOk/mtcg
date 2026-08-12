package com.aris.mtcg.manager;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.aris.mtcg.engine.model.ActionLog;
import com.aris.mtcg.engine.model.GameState;
import java.util.List;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * GameState 序列化器。
 *
 * <p>负责 GameState ↔ JSON 互转，支撑 turn_snapshot 持久化与崩溃恢复。 使用 fastjson2；枚举按 name/toString 序列化；通过
 * FieldBased 处理不可变 final 字段。
 *
 * <p>快照序列化开启 ReferenceDetection，保证 activePlayer / firstPlayer / CombatContext 卡牌引用的对象同一性。
 *
 * @author pengYuJun
 */
@Component
public class GameStateSerializer {

    private static final JSONWriter.Feature[] SNAPSHOT_WRITE_FEATURES = {
        JSONWriter.Feature.WriteClassName,
        JSONWriter.Feature.FieldBased,
        JSONWriter.Feature.WriteEnumsUsingName,
        JSONWriter.Feature.ReferenceDetection
    };

    private static final JSONReader.Feature[] SNAPSHOT_READ_FEATURES = {
        JSONReader.Feature.FieldBased, JSONReader.Feature.SupportAutoType
    };

    private static final JSONWriter.Feature[] ACTION_LOG_WRITE_FEATURES = {
        JSONWriter.Feature.WriteEnumsUsingName, JSONWriter.Feature.FieldBased
    };

    private static final JSONReader.Feature[] ACTION_LOG_READ_FEATURES = {
        JSONReader.Feature.FieldBased, JSONReader.Feature.SupportAutoType
    };

    /**
     * 序列化 GameState 为 turn_snapshot JSON。
     *
     * <p>包裹 { snapshotActionSeq, snapshotTurn, gameState } 结构，记录快照水位。
     */
    public String serializeSnapshot(GameState state, long snapshotActionSeq) {
        SnapshotWrapper wrapper = new SnapshotWrapper();
        wrapper.setSnapshotActionSeq(snapshotActionSeq);
        wrapper.setSnapshotTurn(state.getTurnCount());
        wrapper.setGameState(state);
        return JSON.toJSONString(wrapper, SNAPSHOT_WRITE_FEATURES);
    }

    /** 反序列化 turn_snapshot JSON 为快照包装器。 */
    public SnapshotWrapper deserializeSnapshot(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        return JSON.parseObject(json, SnapshotWrapper.class, SNAPSHOT_READ_FEATURES);
    }

    /** 序列化操作流水列表为 action_log JSON 数组字符串。 */
    public String serializeActionLog(List<ActionLog> logs) {
        if (logs == null || logs.isEmpty()) {
            return "[]";
        }
        return JSON.toJSONString(logs, ACTION_LOG_WRITE_FEATURES);
    }

    /** 反序列化 action_log JSON 数组字符串为操作流水列表。 */
    public List<ActionLog> deserializeActionLog(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        List<ActionLog> logs = JSON.parseArray(json, ActionLog.class, ACTION_LOG_READ_FEATURES);
        return logs != null ? logs : List.of();
    }

    /** 快照包装器：记录水位 + 完整状态 */
    @Data
    public static class SnapshotWrapper {

        /** 快照已应用的最后操作序号 */
        private long snapshotActionSeq;

        /** 快照对应回合计数 */
        private int snapshotTurn;

        /** 完整对局状态 */
        private GameState gameState;
    }
}
