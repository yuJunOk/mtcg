package com.aris.mtcg.engine.combat;

import com.aris.mtcg.engine.action.ActionRequest;
import com.aris.mtcg.engine.action.ActionSupport;
import com.aris.mtcg.engine.enums.Zone;
import com.aris.mtcg.engine.keyword.AssaultKeywordHandler;
import com.aris.mtcg.engine.keyword.Keyword;
import com.aris.mtcg.engine.model.CardInstance;
import com.aris.mtcg.engine.model.GameState;
import com.aris.mtcg.engine.model.PlayerState;

/**
 * 战斗判定结算（303.2.a.4.3.3）。
 *
 * <p>角色 vs 角色：战力比较；角色 vs 破绽：冲击卡组顶进时间线；结算后调用 {@link WinChecker}。
 *
 * @author pengYuJun
 */
public final class BattleResolver {

    private BattleResolver() {}

    /**
     * 结算单次战斗判定。
     *
     * @param state 全局状态
     * @param attacker 攻击者
     * @param target 目标角色（破绽攻击时为 null）
     * @param targetZone 目标区域
     */
    public static void resolve(
            GameState state, CardInstance attacker, CardInstance target, Zone targetZone) {
        PlayerState defender = state.getInactivePlayer();
        PlayerState attackerPlayer = state.getActivePlayer();

        if (target == null) {
            // 角色 vs 破绽（303.2.a.4.3.3.2）
            resolveVsVulnerable(attackerPlayer);
        } else {
            // 角色 vs 角色（303.2.a.4.3.3.1）
            resolveVsCharacter(attacker, target, attackerPlayer, defender);
        }

        WinChecker.check(state);
    }

    /** 兼容设计稿签名：从 ActionRequest 取目标。 */
    public static void resolve(GameState state, CardInstance attacker, ActionRequest request) {
        CardInstance target = null;
        if (request.getTargetCardCode() != null) {
            target =
                    ActionSupport.findOnField(
                            state.getInactivePlayer(), request.getTargetCardCode());
        }
        resolve(state, attacker, target, request.getTargetZone());
    }

    /** 角色 vs 角色：战力大者胜，败者撤退；相等相杀。 */
    private static void resolveVsCharacter(
            CardInstance attacker,
            CardInstance target,
            PlayerState attackerPlayer,
            PlayerState defender) {
        int ap = attacker.getCurrentPower();
        int tp = target.getCurrentPower();
        if (ap > tp) {
            ActionSupport.retreatCard(defender, target);
            // 强袭：战胜时视为成功攻击破绽（305.4）
            if (attacker.hasKeyword(Keyword.ASSAULT) || ActionSupport.hasAssaultKeyword(attacker)) {
                AssaultKeywordHandler.applySuccessfulBreak(attackerPlayer);
            }
        } else if (ap < tp) {
            ActionSupport.retreatCard(attackerPlayer, attacker);
        } else {
            ActionSupport.retreatCard(defender, target);
            ActionSupport.retreatCard(attackerPlayer, attacker);
        }
    }

    /** 角色 vs 破绽：冲击卡组顶 1 张 → 时间线。 */
    private static void resolveVsVulnerable(PlayerState attackerPlayer) {
        drawRushToTimeline(attackerPlayer);
    }

    /** 冲击卡组顶 → 时间线（列表头部视为顶，与设计稿一致）。 */
    public static void drawRushToTimeline(PlayerState player) {
        if (player.getRushDeck().isEmpty()) {
            return;
        }
        CardInstance rush = player.getRushDeck().remove(0);
        rush.setCurrentZone(Zone.TIMELINE);
        player.getTimeline().add(rush);
    }

    /** 中断条件（303.2.a.4.3.1.4–1.5 / 4.3.2.6–2.7）。 */
    public static boolean isAttackerInvalid(CardInstance attacker) {
        if (attacker == null) {
            return true;
        }
        if (attacker.getCurrentZone() == null || !attacker.getCurrentZone().isCombatZone()) {
            return true; // 离场或不在战区
        }
        if (attacker.getCurrentRange() <= 0) {
            return true;
        }
        return attacker.isFaceDown();
    }
}
