package com.aris.mtcg.engine.keyword;

import com.aris.mtcg.engine.combat.BattleResolver;
import com.aris.mtcg.engine.effect.EffectContext;
import com.aris.mtcg.engine.enums.Zone;
import com.aris.mtcg.engine.model.CardInstance;
import com.aris.mtcg.engine.model.GameState;
import com.aris.mtcg.engine.model.PlayerState;

/**
 * 强袭（规则 305.4）：战区常驻；因攻击战胜对方角色时，额外视为成功攻击破绽。
 *
 * @author pengYuJun
 */
public class AssaultKeywordHandler implements KeywordHandler {

    @Override
    public Keyword getKeyword() {
        return Keyword.ASSAULT;
    }

    @Override
    public Zone getEffectiveZone() {
        return Zone.VANGUARD;
    }

    @Override
    public boolean canApply(EffectContext ctx) {
        CardInstance source = ctx.getSource();
        return source != null
                && source.getCurrentZone() != null
                && source.getCurrentZone().isCombatZone()
                && source.hasKeyword(Keyword.ASSAULT);
    }

    @Override
    public void apply(EffectContext ctx) {
        GameState state = ctx.getGameState();
        if (state == null) {
            return;
        }
        applySuccessfulBreak(state.getActivePlayer());
    }

    /**
     * 战胜后额外冲击进时间线（305.4）。
     *
     * <p>供 {@link BattleResolver} 在战力判定后直接调用。
     */
    public static void applySuccessfulBreak(PlayerState attackerPlayer) {
        BattleResolver.drawRushToTimeline(attackerPlayer);
    }
}
