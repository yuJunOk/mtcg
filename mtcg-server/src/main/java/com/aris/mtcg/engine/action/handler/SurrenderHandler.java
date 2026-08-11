package com.aris.mtcg.engine.action.handler;

import com.aris.mtcg.engine.action.ActionRequest;
import com.aris.mtcg.engine.action.ActionResult;
import com.aris.mtcg.engine.action.ActionType;
import com.aris.mtcg.engine.action.ActionTypeHandler;
import com.aris.mtcg.engine.action.EngineException;
import com.aris.mtcg.engine.enums.GameStatus;
import com.aris.mtcg.engine.model.ActionLog;
import com.aris.mtcg.engine.model.GameState;

/**
 * 认输处理器（FR4.4）。
 *
 * <p>发起认输的玩家判负，对手获胜，对局立即结束。
 *
 * @author pengYuJun
 */
public class SurrenderHandler implements ActionTypeHandler {

    @Override
    public ActionType supportedType() {
        return ActionType.SURRENDER;
    }

    @Override
    public void validate(GameState state, ActionRequest request) {
        if (state.getStatus() != GameStatus.IN_PROGRESS) {
            throw new EngineException("对局未进行中", "FR4.4");
        }
        if (request.getPlayerId() == null) {
            throw new EngineException("认输须指定玩家", "FR4.4");
        }
        String pid = request.getPlayerId();
        boolean isParticipant =
                pid.equals(state.getActivePlayer().getPlayerId())
                        || pid.equals(state.getInactivePlayer().getPlayerId());
        if (!isParticipant) {
            throw new EngineException("非对局玩家不能认输", "FR4.4");
        }
    }

    @Override
    public ActionResult execute(GameState state, ActionRequest request) {
        String winnerId =
                state.getActivePlayer().getPlayerId().equals(request.getPlayerId())
                        ? state.getInactivePlayer().getPlayerId()
                        : state.getActivePlayer().getPlayerId();

        state.setStatus(GameStatus.FINISHED);
        state.setWinnerId(winnerId);
        state.setCombatContext(null);

        state.logAction(
                new ActionLog(
                        state.getTurnCount(),
                        state.getCurrentPhase(),
                        request.getPlayerId(),
                        ActionType.SURRENDER.name(),
                        "认输，胜者 " + winnerId));

        ActionResult result = ActionResult.ok();
        result.setGameEnded(true);
        result.setWinnerId(winnerId);
        return result;
    }
}
