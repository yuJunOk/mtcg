package com.aris.mtcg.engine.action.handler;

import com.aris.mtcg.engine.action.ActionRequest;
import com.aris.mtcg.engine.action.ActionResult;
import com.aris.mtcg.engine.action.ActionSupport;
import com.aris.mtcg.engine.action.ActionType;
import com.aris.mtcg.engine.action.ActionTypeHandler;
import com.aris.mtcg.engine.action.EngineException;
import com.aris.mtcg.engine.combat.CombatContext;
import com.aris.mtcg.engine.combat.CombatRow;
import com.aris.mtcg.engine.combat.CombatStep;
import com.aris.mtcg.engine.combat.DistanceCalculator;
import com.aris.mtcg.engine.enums.PhaseType;
import com.aris.mtcg.engine.enums.Zone;
import com.aris.mtcg.engine.model.ActionLog;
import com.aris.mtcg.engine.model.CardInstance;
import com.aris.mtcg.engine.model.GameState;
import com.aris.mtcg.engine.model.PlayerState;

/**
 * 攻击序列处理器（303.2.a.4.3）。
 *
 * <p>声明攻击并锁定目标 → 进入应对步骤；判定由双方连续 PASS 后触发 {@link com.aris.mtcg.engine.combat.BattleResolver}。 Q&A
 * Q9：应对后目标失效回 TARGET 重选，不可取消攻击。
 *
 * @author pengYuJun
 */
public class AttackSequenceHandler implements ActionTypeHandler {

    @Override
    public ActionType supportedType() {
        return ActionType.ATTACK;
    }

    @Override
    public void validate(GameState state, ActionRequest request) {
        ActionSupport.assertActivePlayer(state, request);
        ActionSupport.assertPhase(state, PhaseType.COMBAT);

        CombatContext ctx = requireContext(state);
        if (ctx.getStep() != CombatStep.ADJUST && ctx.getStep() != CombatStep.TARGET) {
            throw new EngineException("当前不能声明或重选攻击目标", "303.2.a.4.3");
        }

        PlayerState ap = state.getActivePlayer();
        CardInstance attacker = resolveAttacker(state, request);
        if (attacker == null) {
            throw new EngineException("场上不存在攻击者", "303.2.a.4.3");
        }
        if (!attacker.getCurrentZone().isCombatZone()) {
            throw new EngineException("攻击者须在战区", "303.2.a.4.3");
        }
        assertAttackerInCurrentRow(ctx, attacker);

        // 303.2.a.4.5：R=0 不能攻击
        if (attacker.getCurrentRange() <= 0) {
            throw new EngineException("攻击者 R=0，不能攻击", "303.2.a.4.5");
        }
        if (attacker.isFaceDown()) {
            throw new EngineException("盖卡不能攻击", "303.2.a.4.5");
        }

        // 重选目标不重复扣次数；新声明检查攻击次数
        boolean retarget = ctx.getStep() == CombatStep.TARGET && ctx.getAttacker() == attacker;
        if (!retarget) {
            int attackLimit = ActionSupport.hasComboKeyword(attacker) ? 2 : 1;
            if (attacker.getAttackUsed() >= attackLimit) {
                throw new EngineException("该角色本回合攻击次数已用尽", "303.2.a.4.4");
            }
        }

        Zone targetZone = request.getTargetZone();
        if (targetZone == null || !targetZone.isCombatZone()) {
            throw new EngineException("须指定敌方战区目标", "303.2.a.4.3.1");
        }

        if (request.getTargetCardCode() == null) {
            assertVulnerable(state.getInactivePlayer(), targetZone, attacker);
        } else {
            CardInstance target =
                    ActionSupport.findOnField(
                            state.getInactivePlayer(), request.getTargetCardCode());
            if (target == null) {
                throw new EngineException("场上不存在攻击目标", "303.2.a.4.3.1");
            }
            if (target.getCurrentZone() != targetZone) {
                throw new EngineException("目标卡与目标区域不一致", "303.2.a.4.3.1");
            }
            if (!DistanceCalculator.canReach(attacker, targetZone)) {
                int dist = DistanceCalculator.distance(attacker.getCurrentZone(), targetZone);
                throw new EngineException(
                        "距离 " + dist + " 超过攻击者 R=" + attacker.getCurrentRange(), "303.2.a.4.3.1.1");
            }
        }
    }

    @Override
    public ActionResult execute(GameState state, ActionRequest request) {
        CombatContext ctx = requireContext(state);
        CardInstance attacker = resolveAttacker(state, request);

        CardInstance target = null;
        if (request.getTargetCardCode() != null) {
            target =
                    ActionSupport.findOnField(
                            state.getInactivePlayer(), request.getTargetCardCode());
        }

        // 跳过调整步骤（直接攻击）
        if (ctx.getStep() == CombatStep.ADJUST) {
            ctx.setStep(CombatStep.TARGET);
        }

        // 锁定 → 进入应对（303.2.a.4.3.2）
        ctx.lockAttack(attacker, target, request.getTargetZone());

        state.logAction(
                new ActionLog(
                        state.getTurnCount(),
                        state.getCurrentPhase(),
                        request.getPlayerId(),
                        ActionType.ATTACK.name(),
                        "攻击声明 "
                                + attacker.getSnapshot().getCardCode()
                                + " → "
                                + request.getTargetZone()));
        return ActionResult.ok();
    }

    /** 推进到下一攻击行；全部结束后进入 RESPONSE（303.2.a.4.8–4.10）。 */
    public void advanceToNextRow(GameState state) {
        CombatContext ctx = state.getCombatContext();
        if (ctx == null) {
            return;
        }
        ctx.advanceRow();
        if (ctx.getRow() == CombatRow.FINISHED) {
            state.setCombatContext(null);
            state.setCurrentPhase(PhaseType.RESPONSE);
        }
    }

    private static CardInstance resolveAttacker(GameState state, ActionRequest request) {
        CombatContext ctx = state.getCombatContext();
        // Q&A Q9 重选：不可取消攻击，强制沿用已锁定攻击者
        if (ctx != null && ctx.getStep() == CombatStep.TARGET && ctx.getAttacker() != null) {
            return ctx.getAttacker();
        }
        return ActionSupport.findOnField(state.getActivePlayer(), request.getCardCode());
    }

    private static void assertAttackerInCurrentRow(CombatContext ctx, CardInstance attacker) {
        Zone zone = attacker.getCurrentZone();
        boolean ok =
                switch (ctx.getRow()) {
                    case VANGUARD -> zone == Zone.VANGUARD;
                    case FLANK -> zone == Zone.FLANK_LEFT || zone == Zone.FLANK_RIGHT;
                    case REARGUARD -> zone == Zone.REARGUARD;
                    case FINISHED -> false;
                };
        if (!ok) {
            throw new EngineException("攻击者与当前攻击行不符: " + ctx.getRow(), "303.2.a.4.8");
        }
    }

    /** 破绽：目标战区须空置，且距离可达（空袭关键词迭代六）。 */
    private static void assertVulnerable(
            PlayerState defender, Zone targetZone, CardInstance attacker) {
        CardInstance occupied = ActionSupport.getAt(defender.getField(), targetZone, 0);
        if (occupied != null) {
            throw new EngineException("目标区域非破绽（有角色）", "303.2.a.4.3.1");
        }
        if (!DistanceCalculator.canReach(attacker, targetZone)) {
            int dist = DistanceCalculator.distance(attacker.getCurrentZone(), targetZone);
            throw new EngineException(
                    "距离 " + dist + " 超过攻击者 R=" + attacker.getCurrentRange(), "303.2.a.4.3.1.1");
        }
    }

    private static CombatContext requireContext(GameState state) {
        CombatContext ctx = state.getCombatContext();
        if (ctx == null) {
            throw new EngineException("战斗上下文未初始化", "303.2.a.4");
        }
        return ctx;
    }
}
