package com.aris.mtcg.engine.action.handler;

import com.aris.mtcg.engine.action.ActionRequest;
import com.aris.mtcg.engine.action.ActionResult;
import com.aris.mtcg.engine.action.ActionSupport;
import com.aris.mtcg.engine.action.ActionType;
import com.aris.mtcg.engine.action.ActionTypeHandler;
import com.aris.mtcg.engine.action.EngineException;
import com.aris.mtcg.engine.combat.BattleResolver;
import com.aris.mtcg.engine.combat.CombatContext;
import com.aris.mtcg.engine.combat.CombatStep;
import com.aris.mtcg.engine.combat.DistanceCalculator;
import com.aris.mtcg.engine.enums.PhaseType;
import com.aris.mtcg.engine.enums.Zone;
import com.aris.mtcg.engine.model.ActionLog;
import com.aris.mtcg.engine.model.CardInstance;
import com.aris.mtcg.engine.model.GameState;

/**
 * 战斗应对：不行动（PASS）（303.2.a.4.3.2.4）。
 *
 * <p>双方连续不行动 → 进入判定并结算。应对号召 / 拦截见独立 Handler。
 *
 * @author pengYuJun
 */
public class CombatResponseHandler implements ActionTypeHandler {

    @Override
    public ActionType supportedType() {
        return ActionType.PASS;
    }

    @Override
    public void validate(GameState state, ActionRequest request) {
        ActionSupport.assertPhase(state, PhaseType.COMBAT);
        CombatContext ctx = requireResponseContext(state);
        if (request.getPlayerId() == null) {
            throw new EngineException("须指定操作玩家", "303.2.a.4.3.2");
        }
        ActionSupport.requirePlayer(state, request.getPlayerId());
    }

    @Override
    public ActionResult execute(GameState state, ActionRequest request) {
        CombatContext ctx = requireResponseContext(state);
        ctx.recordPass(request.getPlayerId());

        state.logAction(
                new ActionLog(
                        state.getTurnCount(),
                        state.getCurrentPhase(),
                        request.getPlayerId(),
                        ActionType.PASS.name(),
                        "不行动"));

        if (BattleResolver.isAttackerInvalid(ctx.getAttacker())) {
            ctx.endCombat();
            return ActionResult.ok("攻击者失效，战斗结束");
        }

        if (ctx.isBothPassedConsecutively()) {
            return resolveBattle(state, ctx);
        }
        return ActionResult.ok();
    }

    /** 双方连续 PASS 后结算判定。 */
    static ActionResult resolveBattle(GameState state, CombatContext ctx) {
        ctx.setStep(CombatStep.RESOLUTION);
        CardInstance attacker = ctx.getAttacker();
        BattleResolver.resolve(state, attacker, ctx.getTarget(), ctx.getTargetZone());

        if (attacker != null) {
            attacker.setAttackUsed(attacker.getAttackUsed() + 1);
        }

        // 清空锁定，回到目标步骤等待同行下一击或 END_PHASE / 推进行
        ctx.setAttacker(null);
        ctx.setTarget(null);
        ctx.setTargetZone(null);
        ctx.resetResponseFlags();
        ctx.setStep(CombatStep.TARGET);

        if (BattleResolver.isAttackerInvalid(attacker)) {
            // 攻击者已撤退等，不强制结束整行；由玩家继续或结束阶段
        }

        ActionResult result = ActionResult.ok("判定结算完成");
        if (state.getWinnerId() != null) {
            result.setGameEnded(true);
            result.setWinnerId(state.getWinnerId());
        }
        return result;
    }

    /**
     * Q&A Q9：应对后检查攻击目标是否仍合法；不合法则回 TARGET 重选（不可取消攻击）。
     *
     * @return true 表示已回退到目标步骤
     */
    static boolean retreatToTargetIfInvalid(GameState state, CombatContext ctx) {
        if (BattleResolver.isAttackerInvalid(ctx.getAttacker())) {
            ctx.endCombat();
            return false;
        }
        if (!isTargetStillValid(state, ctx)) {
            // 保留攻击者，清除目标，回 TARGET
            ctx.setTarget(null);
            ctx.setTargetZone(null);
            ctx.resetResponseFlags();
            ctx.setStep(CombatStep.TARGET);
            return true;
        }
        return false;
    }

    static boolean isTargetStillValid(GameState state, CombatContext ctx) {
        CardInstance attacker = ctx.getAttacker();
        Zone targetZone = ctx.getTargetZone();
        if (attacker == null || targetZone == null) {
            return false;
        }
        if (!DistanceCalculator.canReach(attacker, targetZone)) {
            return false;
        }
        CardInstance target = ctx.getTarget();
        if (target == null) {
            // 破绽：仍须空置
            return ActionSupport.getAt(state.getInactivePlayer().getField(), targetZone, 0) == null;
        }
        // 角色：仍须在场且位于目标区
        CardInstance onField =
                ActionSupport.findOnField(state.getInactivePlayer(), target.getInstanceId());
        return onField == target && target.getCurrentZone() == targetZone;
    }

    static CombatContext requireResponseContext(GameState state) {
        CombatContext ctx = state.getCombatContext();
        if (ctx == null) {
            throw new EngineException("战斗上下文未初始化", "303.2.a.4");
        }
        if (ctx.getStep() != CombatStep.COMBAT_RESPONSE) {
            throw new EngineException("当前不在战斗应对步骤", "303.2.a.4.3.2");
        }
        return ctx;
    }
}
