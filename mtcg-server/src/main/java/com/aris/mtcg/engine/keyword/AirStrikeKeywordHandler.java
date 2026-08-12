package com.aris.mtcg.engine.keyword;

import com.aris.mtcg.engine.effect.EffectContext;
import com.aris.mtcg.engine.enums.Zone;
import com.aris.mtcg.engine.model.CardInstance;

/**
 * 空袭（规则 305.5）：战区常驻；即便敌方战区有角色，也可把该战区作为破绽攻击。
 *
 * @author pengYuJun
 */
public class AirStrikeKeywordHandler implements KeywordHandler {

    @Override
    public Keyword getKeyword() {
        return Keyword.AIR_STRIKE;
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
                && source.hasKeyword(Keyword.AIR_STRIKE);
    }

    @Override
    public void apply(EffectContext ctx) {
        // 实际放宽由 AttackSequenceHandler.assertVulnerable 检查 hasKeyword(AIR_STRIKE)
    }

    /** 是否允许将有角色的敌方战区作为破绽（305.5）。 */
    public static boolean allowsOccupiedAsVulnerable(CardInstance attacker) {
        return attacker != null && attacker.hasKeyword(Keyword.AIR_STRIKE);
    }
}
