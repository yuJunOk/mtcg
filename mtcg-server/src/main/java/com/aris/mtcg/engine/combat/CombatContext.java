package com.aris.mtcg.engine.combat;

import com.aris.mtcg.engine.enums.Zone;
import com.aris.mtcg.engine.model.CardInstance;
import java.util.HashSet;
import java.util.Set;

/**
 * 战斗阶段运行时上下文（303.2.a.4）。
 *
 * <p>非战斗阶段为 null（挂在 {@code GameState.combatContext}）；进入 COMBAT 时新建。
 * 维护当前攻击行、单次战斗步骤、锁定的攻击者/目标，以及应对步骤计数。
 *
 * @author pengYuJun
 */
public class CombatContext {

    /** 当前单次战斗步骤 */
    private CombatStep step = CombatStep.ADJUST;

    /** 当前攻击行（先锋→侧翼→后卫） */
    private CombatRow row = CombatRow.VANGUARD;

    /** 当前锁定的攻击者 */
    private CardInstance attacker;

    /** 攻击目标实例（角色 vs 角色；破绽攻击时为 null） */
    private CardInstance target;

    /** 攻击目标区域（破绽或角色所在区） */
    private Zone targetZone;

    /** 本步骤已使用应对号召的玩家 ID（303.2.a.4.3.2.3.1：双方各 1 次） */
    private final Set<String> responseSummonUsed = new HashSet<>();

    /** 连续不行动（PASS）次数；双方连续不行动 → 应对步骤结束 */
    private int consecutivePassCount;

    public CombatContext() {}

    /**
     * 推进到下一攻击行（303.2.a.4.8–4.10）。
     *
     * <p>先锋 → 侧翼 → 后卫 → FINISHED；并清空当前攻击锁定。
     */
    public void advanceRow() {
        clearCurrentAttack();
        switch (row) {
            case VANGUARD -> row = CombatRow.FLANK;
            case FLANK -> row = CombatRow.REARGUARD;
            case REARGUARD, FINISHED -> row = CombatRow.FINISHED;
        }
        if (row != CombatRow.FINISHED) {
            step = CombatStep.TARGET;
        }
    }

    /**
     * 结束当前战斗（攻击者失效等中断，303.2.a.4.3.2.6–2.7）。
     *
     * <p>将行置为 FINISHED 并清空攻击锁定。
     */
    public void endCombat() {
        clearCurrentAttack();
        row = CombatRow.FINISHED;
    }

    /** 记录一次不行动（PASS）。 */
    public void recordPass(String playerId) {
        consecutivePassCount++;
    }

    /** 双方是否已连续不行动（连续 2 次 PASS）。 */
    public boolean isBothPassedConsecutively() {
        return consecutivePassCount >= 2;
    }

    /** 该玩家本步骤是否已用过应对号召。 */
    public boolean isResponseSummonUsed(String playerId) {
        return responseSummonUsed.contains(playerId);
    }

    /** 标记该玩家本步骤已使用应对号召，并打断连续 PASS。 */
    public void markResponseSummonUsed(String playerId) {
        responseSummonUsed.add(playerId);
        resetPassStreak();
    }

    /** 打断连续 PASS（拦截、号召等有实际行动时调用）。 */
    public void resetPassStreak() {
        consecutivePassCount = 0;
    }

    /** 清空本步骤应对号召与 PASS 计数（进入新一轮应对时）。 */
    public void resetResponseFlags() {
        responseSummonUsed.clear();
        consecutivePassCount = 0;
    }

    /** 锁定攻击声明：攻击者 + 目标卡（可空）+ 目标区域。 */
    public void lockAttack(CardInstance attacker, CardInstance target, Zone targetZone) {
        this.attacker = attacker;
        this.target = target;
        this.targetZone = targetZone;
        this.step = CombatStep.COMBAT_RESPONSE;
        resetResponseFlags();
    }

    private void clearCurrentAttack() {
        attacker = null;
        target = null;
        targetZone = null;
        resetResponseFlags();
    }

    public CombatStep getStep() {
        return step;
    }

    public void setStep(CombatStep step) {
        this.step = step;
    }

    public CombatRow getRow() {
        return row;
    }

    public void setRow(CombatRow row) {
        this.row = row;
    }

    public CardInstance getAttacker() {
        return attacker;
    }

    public void setAttacker(CardInstance attacker) {
        this.attacker = attacker;
    }

    public CardInstance getTarget() {
        return target;
    }

    public void setTarget(CardInstance target) {
        this.target = target;
    }

    public Zone getTargetZone() {
        return targetZone;
    }

    public void setTargetZone(Zone targetZone) {
        this.targetZone = targetZone;
    }
}
