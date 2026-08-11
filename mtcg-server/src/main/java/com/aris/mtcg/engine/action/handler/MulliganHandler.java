package com.aris.mtcg.engine.action.handler;

import com.aris.mtcg.engine.GameInitializer;
import com.aris.mtcg.engine.action.ActionRequest;
import com.aris.mtcg.engine.action.ActionResult;
import com.aris.mtcg.engine.action.ActionType;
import com.aris.mtcg.engine.action.ActionTypeHandler;
import com.aris.mtcg.engine.action.EngineException;
import com.aris.mtcg.engine.enums.GameStatus;
import com.aris.mtcg.engine.model.ActionLog;
import com.aris.mtcg.engine.model.GameState;
import java.util.Collections;
import java.util.List;

/**
 * 调度处理器（303.1）。
 *
 * <p>委托 {@link GameInitializer#mulligan}；extras 键 {@code cardIndices} 为手牌下标列表。
 *
 * @author pengYuJun
 */
public class MulliganHandler implements ActionTypeHandler {

    public static final String EXTRA_CARD_INDICES = "cardIndices";

    private final GameInitializer initializer;

    public MulliganHandler(GameInitializer initializer) {
        this.initializer = initializer;
    }

    @Override
    public ActionType supportedType() {
        return ActionType.MULLIGAN;
    }

    @Override
    public void validate(GameState state, ActionRequest request) {
        // 调度在对局开始前（WAITING）或按规则时机；本迭代允许 WAITING / IN_PROGRESS
        if (state.getStatus() != GameStatus.WAITING
                && state.getStatus() != GameStatus.IN_PROGRESS) {
            throw new EngineException("当前对局状态不能调度", "303.1");
        }
        if (request.getPlayerId() == null) {
            throw new EngineException("须指定调度玩家", "303.1");
        }
        boolean isParticipant =
                request.getPlayerId().equals(state.getActivePlayer().getPlayerId())
                        || request.getPlayerId().equals(state.getInactivePlayer().getPlayerId());
        if (!isParticipant) {
            throw new EngineException("非对局玩家不能调度", "303.1");
        }
    }

    @Override
    public ActionResult execute(GameState state, ActionRequest request) {
        List<Integer> indices = extractIndices(request);
        initializer.mulligan(state, request.getPlayerId(), indices);

        state.logAction(
                new ActionLog(
                        state.getTurnCount(),
                        state.getCurrentPhase(),
                        request.getPlayerId(),
                        ActionType.MULLIGAN.name(),
                        "调度 " + indices.size() + " 张"));
        return ActionResult.ok();
    }

    private static List<Integer> extractIndices(ActionRequest request) {
        Object raw = request.getExtras().get(EXTRA_CARD_INDICES);
        if (!(raw instanceof List<?> list) || list.isEmpty()) {
            return Collections.emptyList();
        }
        return list.stream()
                .filter(Number.class::isInstance)
                .map(Number.class::cast)
                .map(Number::intValue)
                .toList();
    }
}
