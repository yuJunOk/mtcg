package com.aris.mtcg.engine.keyword;

import com.aris.mtcg.engine.effect.EffectContext;
import com.aris.mtcg.engine.enums.PhaseType;
import com.aris.mtcg.engine.enums.Zone;
import com.aris.mtcg.engine.model.CardInstance;
import com.aris.mtcg.engine.model.GameState;

/**
 * 应对（规则 305.1）：手牌常驻；可在应对阶段/战斗应对步骤执行应对号召。
 *
 * @author pengYuJun
 */
public class ResponseKeywordHandler implements KeywordHandler {

    @Override
    public Keyword getKeyword() {
        return Keyword.RESPONSE;
    }

    @Override
    public Zone getEffectiveZone() {
        return Zone.HAND;
    }

    @Override
    public boolean canApply(EffectContext ctx) {
        CardInstance source = ctx.getSource();
        return source != null
                && source.getCurrentZone() == Zone.HAND
                && source.hasKeyword(Keyword.RESPONSE)
                && isResponseWindow(ctx);
    }

    @Override
    public void apply(EffectContext ctx) {
        // 实际号召由 ResponseSummonHandler 执行；此处仅作能力校验钩子
    }

    /** 303.2.a.4.3.2 / 303.2.a.5：应对窗口。 */
    private static boolean isResponseWindow(EffectContext ctx) {
        GameState state = ctx.getGameState();
        if (state == null) {
            return false;
        }
        PhaseType phase = state.getCurrentPhase();
        if (phase == PhaseType.RESPONSE) {
            return true;
        }
        // 战斗应对步骤
        return phase == PhaseType.COMBAT
                && state.getCombatContext() != null
                && state.getCombatContext().getStep() != null;
    }
}
