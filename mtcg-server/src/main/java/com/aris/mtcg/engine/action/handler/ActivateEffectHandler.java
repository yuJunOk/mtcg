package com.aris.mtcg.engine.action.handler;

import com.aris.mtcg.engine.action.ActionRequest;
import com.aris.mtcg.engine.action.ActionResult;
import com.aris.mtcg.engine.action.ActionSupport;
import com.aris.mtcg.engine.action.ActionType;
import com.aris.mtcg.engine.action.ActionTypeHandler;
import com.aris.mtcg.engine.action.EngineException;
import com.aris.mtcg.engine.enums.PhaseType;
import com.aris.mtcg.engine.model.ActionLog;
import com.aris.mtcg.engine.model.CardInstance;
import com.aris.mtcg.engine.model.GameState;

/**
 * 启动效果处理器骨架（303.2.a.3.1.4 / 304.2）。
 *
 * <p>本迭代仅校验阶段与目标，记录流水；效果结算由迭代六 {@code EffectResolver} 填实。
 *
 * @author pengYuJun
 */
public class ActivateEffectHandler implements ActionTypeHandler {

    @Override
    public ActionType supportedType() {
        return ActionType.ACTIVATE_EFFECT;
    }

    @Override
    public void validate(GameState state, ActionRequest request) {
        ActionSupport.assertActivePlayer(state, request);
        ActionSupport.assertPhase(state, PhaseType.ACTION);

        CardInstance card =
                ActionSupport.findOnField(state.getActivePlayer(), request.getCardCode());
        if (card == null) {
            // 结附卡也可能启动【应对·启动】等，迭代六扩展；本迭代仅场上主卡
            throw new EngineException("场上不存在该角色", "303.2.a.3.1.4");
        }
        if (card.isFaceDown()) {
            throw new EngineException("盖卡不能启动效果", "303.2.a.3.1.4");
        }
        // 304.2：具体效果合法性交由迭代六 EffectResolver；此处不引入效果引擎
    }

    @Override
    public ActionResult execute(GameState state, ActionRequest request) {
        // 迭代六：交由 EffectResolver 结算
        state.logAction(
                new ActionLog(
                        state.getTurnCount(),
                        state.getCurrentPhase(),
                        request.getPlayerId(),
                        ActionType.ACTIVATE_EFFECT.name(),
                        "启动效果（骨架）" + request.getCardCode()));
        return ActionResult.ok("启动效果骨架：效果系统将在迭代六实现");
    }
}
