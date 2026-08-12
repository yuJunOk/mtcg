package com.aris.mtcg.engine.keyword;

import com.aris.mtcg.engine.action.ActionSupport;
import com.aris.mtcg.engine.effect.EffectContext;
import com.aris.mtcg.engine.enums.Zone;
import com.aris.mtcg.engine.model.CardInstance;
import com.aris.mtcg.engine.model.PlayerState;

/**
 * 唯一（规则 305.6）：场上常驻；我方场上不能有其他同名卡；效果不能失去。
 *
 * @author pengYuJun
 */
public class UniqueKeywordHandler implements KeywordHandler {

    @Override
    public Keyword getKeyword() {
        return Keyword.UNIQUE;
    }

    @Override
    public Zone getEffectiveZone() {
        // 场上统称：用 BASE 代表 isOnField（实际 canApply 用 isOnField）
        return Zone.BASE;
    }

    @Override
    public boolean canApply(EffectContext ctx) {
        CardInstance source = ctx.getSource();
        return source != null
                && source.getCurrentZone() != null
                && source.getCurrentZone().isOnField()
                && source.hasKeyword(Keyword.UNIQUE);
    }

    @Override
    public void apply(EffectContext ctx) {
        // 301.33 例外：唯一效果不可失去
        ctx.getSource().setEffectUnlosable(true);
    }

    /**
     * 进场前校验：我方场上是否已存在同名卡（305.6 / 201.7 同名即同卡）。
     *
     * @param controller 控制者
     * @param cardName 待进场卡名
     * @return true 表示会违反唯一约束
     */
    public static boolean wouldViolateUnique(PlayerState controller, String cardName) {
        return ActionSupport.hasSameNameOnField(controller, cardName);
    }
}
