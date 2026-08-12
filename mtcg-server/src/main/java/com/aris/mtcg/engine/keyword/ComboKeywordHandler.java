package com.aris.mtcg.engine.keyword;

import com.aris.mtcg.engine.effect.EffectContext;
import com.aris.mtcg.engine.enums.Zone;
import com.aris.mtcg.engine.model.CardInstance;

/**
 * 连击（规则 305.3）：战区常驻；获得第 2 次攻击机会。
 *
 * @author pengYuJun
 */
public class ComboKeywordHandler implements KeywordHandler {

    /** 连击角色每回合最大攻击次数。 */
    public static final int MAX_ATTACKS_WITH_COMBO = 2;

    @Override
    public Keyword getKeyword() {
        return Keyword.COMBO;
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
                && source.hasKeyword(Keyword.COMBO);
    }

    @Override
    public void apply(EffectContext ctx) {
        // 305.3：本回合最大攻击次数设为 2
        ctx.getSource().setMaxAttacksThisTurn(MAX_ATTACKS_WITH_COMBO);
    }

    /** 查询某卡本回合攻击上限（供 AttackSequenceHandler 使用）。 */
    public static int maxAttacks(CardInstance card) {
        if (card != null && card.hasKeyword(Keyword.COMBO)) {
            return MAX_ATTACKS_WITH_COMBO;
        }
        return 1;
    }
}
