package com.aris.mtcg.engine.effect;

import com.aris.mtcg.engine.action.ActionSupport;
import com.aris.mtcg.engine.model.CardInstance;
import com.aris.mtcg.engine.model.GameState;
import com.aris.mtcg.engine.model.PlayerState;
import java.util.ArrayList;
import java.util.List;

/**
 * 战力修改器栈（规则 301.41）。
 *
 * <p>所有修改器按目标卡分组存储；重算时按 301.41.a–e 叠加/覆盖。
 *
 * @author pengYuJun
 */
public class PowerModifierStack {

    private final List<PowerModifier> modifiers = new ArrayList<>();

    /** 新增修改器；若触发 301.41 覆盖规则，移除被结束的旧修改器。 */
    public void add(PowerModifier m) {
        applyOverrideRules(m);
        modifiers.add(m);
    }

    /** 来源离场 / 失去效果时移除。 */
    public void removeBySource(CardInstance source) {
        modifiers.removeIf(m -> m.getSource() == source);
    }

    /** 回合结束清除仅本回合修改器（303.2.a.6.1.2 / Q&A Q2）。 */
    public void removeExpiredThisTurn() {
        modifiers.removeIf(m -> m.getDuration() == PowerModifier.Duration.THIS_TURN);
    }

    /** 重算指定卡的指定属性当前值（301.8 下限 0）。 */
    public int compute(CardInstance card, PowerModifier.Attribute attr) {
        int original = readOriginal(card, attr);
        List<PowerModifier> relevant = filterByTarget(card, attr);

        PowerModifier contChange = latestContinuousChange(relevant);
        boolean hasTaIncrement =
                relevant.stream()
                        .anyMatch(
                                m ->
                                        m.getCategory() == PowerModifier.Category.TRIGGER_ACTIVATED
                                                && m.getType() == PowerModifier.Type.INCREMENT);

        int base;
        if (contChange != null) {
            // 常驻变更为最终（301.41.c）
            base = contChange.getValue();
        } else if (!hasTaIncrement) {
            PowerModifier taChange = latestTriggerActivatedChange(relevant);
            base = taChange != null ? taChange.getValue() : original;
        } else {
            // 301.41.a：触发/启动变更 vs 触发/启动增减 → 变更结束，回退印刷值
            base = original;
        }

        int delta =
                relevant.stream()
                        .filter(m -> m.getType() == PowerModifier.Type.INCREMENT)
                        .mapToInt(PowerModifier::getValue)
                        .sum();
        return Math.max(0, base + delta);
    }

    /**
     * 全局重算（常驻效果重算入口，201.10.e）。
     *
     * <p>写回 currentPower / currentRange；POWER==0 的撤退由调用方 {@link
     * EffectResolver#reevaluateContinuous} 统一处理。
     */
    public void recomputeAll(GameState state) {
        for (PlayerState player : List.of(state.getActivePlayer(), state.getInactivePlayer())) {
            for (CardInstance card : ActionSupport.listFieldCards(player)) {
                card.setCurrentPower(compute(card, PowerModifier.Attribute.POWER));
                card.setCurrentRange(compute(card, PowerModifier.Attribute.ATTACK_RANGE));
            }
        }
    }

    public List<PowerModifier> getModifiers() {
        return modifiers;
    }

    private void applyOverrideRules(PowerModifier incoming) {
        if (incoming.getCategory() == PowerModifier.Category.CONTINUOUS
                && incoming.getType() == PowerModifier.Type.CHANGE) {
            // 301.41.b：新常驻变更结束旧常驻变更
            modifiers.removeIf(
                    m ->
                            m.getTarget() == incoming.getTarget()
                                    && m.getAttribute() == incoming.getAttribute()
                                    && m.getCategory() == PowerModifier.Category.CONTINUOUS
                                    && m.getType() == PowerModifier.Type.CHANGE);
            // 301.41.c：常驻变更结束触发/启动变更
            modifiers.removeIf(
                    m ->
                            m.getTarget() == incoming.getTarget()
                                    && m.getAttribute() == incoming.getAttribute()
                                    && m.getCategory() == PowerModifier.Category.TRIGGER_ACTIVATED
                                    && m.getType() == PowerModifier.Type.CHANGE);
        }
    }

    private List<PowerModifier> filterByTarget(CardInstance card, PowerModifier.Attribute attr) {
        List<PowerModifier> list = new ArrayList<>();
        for (PowerModifier m : modifiers) {
            if (m.getTarget() == card && m.getAttribute() == attr) {
                list.add(m);
            }
        }
        return list;
    }

    private PowerModifier latestContinuousChange(List<PowerModifier> relevant) {
        PowerModifier latest = null;
        for (PowerModifier m : relevant) {
            if (m.getCategory() == PowerModifier.Category.CONTINUOUS
                    && m.getType() == PowerModifier.Type.CHANGE) {
                latest = m;
            }
        }
        return latest;
    }

    private PowerModifier latestTriggerActivatedChange(List<PowerModifier> relevant) {
        PowerModifier latest = null;
        for (PowerModifier m : relevant) {
            if (m.getCategory() == PowerModifier.Category.TRIGGER_ACTIVATED
                    && m.getType() == PowerModifier.Type.CHANGE) {
                latest = m;
            }
        }
        return latest;
    }

    private static int readOriginal(CardInstance card, PowerModifier.Attribute attr) {
        return switch (attr) {
            case POWER -> {
                Integer p = card.getSnapshot().getPower();
                yield p != null ? p : 0;
            }
            case ATTACK_RANGE -> {
                Integer r = card.getSnapshot().getAttackRange();
                yield r != null ? r : 0;
            }
            case LEVEL -> {
                Integer lv = card.getSnapshot().getLevel();
                yield lv != null ? lv : 0;
            }
        };
    }
}
