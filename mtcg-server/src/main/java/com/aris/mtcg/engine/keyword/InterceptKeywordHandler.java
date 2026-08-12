package com.aris.mtcg.engine.keyword;

import com.aris.mtcg.engine.combat.CombatContext;
import com.aris.mtcg.engine.effect.EffectContext;
import com.aris.mtcg.engine.enums.Zone;
import com.aris.mtcg.engine.model.CardInstance;
import com.aris.mtcg.engine.model.GameState;

/**
 * 拦截（规则 305.2）：应对·启动；战区生效；将攻击目标改为此卡；每回合 1 次。
 *
 * @author pengYuJun
 */
public class InterceptKeywordHandler implements KeywordHandler {

    @Override
    public Keyword getKeyword() {
        return Keyword.INTERCEPT;
    }

    @Override
    public Zone getEffectiveZone() {
        // 战区统称：用 VANGUARD 代表（实际 canApply 用 isCombatZone）
        return Zone.VANGUARD;
    }

    @Override
    public boolean canApply(EffectContext ctx) {
        CardInstance source = ctx.getSource();
        if (source == null || !source.hasKeyword(Keyword.INTERCEPT)) {
            return false;
        }
        if (source.getCurrentZone() == null || !source.getCurrentZone().isCombatZone()) {
            return false;
        }
        if (source.isInterceptUsed()) {
            return false;
        }
        return hasPendingAttack(ctx);
    }

    @Override
    public void apply(EffectContext ctx) {
        GameState state = ctx.getGameState();
        CardInstance source = ctx.getSource();
        if (state == null || source == null || state.getCombatContext() == null) {
            return;
        }
        CombatContext combat = state.getCombatContext();
        combat.setTarget(source);
        combat.setTargetZone(source.getCurrentZone());
        source.setInterceptUsed(true);
    }

    private static boolean hasPendingAttack(EffectContext ctx) {
        GameState state = ctx.getGameState();
        return state != null
                && state.getCombatContext() != null
                && state.getCombatContext().getAttacker() != null;
    }
}
