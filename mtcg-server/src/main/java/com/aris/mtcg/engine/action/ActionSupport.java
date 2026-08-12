package com.aris.mtcg.engine.action;

import com.aris.mtcg.engine.enums.PhaseType;
import com.aris.mtcg.engine.enums.Zone;
import com.aris.mtcg.engine.keyword.Keyword;
import com.aris.mtcg.engine.model.CardInstance;
import com.aris.mtcg.engine.model.FieldZone;
import com.aris.mtcg.engine.model.GameState;
import com.aris.mtcg.engine.model.PlayerState;
import com.aris.mtcg.engine.rule.RuleConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 行动 Handler 公共工具（校验、找卡、场上放置等）。
 *
 * <p>Handler 无状态，工具方法均为静态。
 *
 * @author pengYuJun
 */
public final class ActionSupport {

    /** 引擎内角色卡类型字面量（不依赖 common 包）。 */
    public static final String CARD_TYPE_CHARACTER = "CHARACTER";

    private ActionSupport() {}

    /** 仅当前回合玩家可操作。 */
    public static void assertActivePlayer(GameState state, ActionRequest request) {
        if (request.getPlayerId() == null
                || !request.getPlayerId().equals(state.getActivePlayer().getPlayerId())) {
            throw new EngineException("仅当前回合玩家可执行此操作", "303.2.a.3");
        }
    }

    /** 断言当前阶段。 */
    public static void assertPhase(GameState state, PhaseType expected) {
        if (state.getCurrentPhase() != expected) {
            throw new EngineException(
                    "当前阶段不允许此操作，期望 " + expected + "，实际 " + state.getCurrentPhase(), "303.2.a");
        }
    }

    /** 是否为角色卡。 */
    public static boolean isCharacter(CardInstance card) {
        return card != null && CARD_TYPE_CHARACTER.equals(card.getSnapshot().getCardType());
    }

    /** 卡牌等级（冲击卡等无等级视为 0）。 */
    public static int getLevel(CardInstance card) {
        Integer level = card.getSnapshot().getLevel();
        return level != null ? level : 0;
    }

    /**
     * 手牌中查找卡牌：优先匹配 instanceId，其次匹配 cardCode。
     *
     * @return 首个匹配，未找到返回 null
     */
    public static CardInstance findInHand(PlayerState player, String ref) {
        if (ref == null) {
            return null;
        }
        for (CardInstance c : player.getHand()) {
            if (matches(c, ref)) {
                return c;
            }
        }
        return null;
    }

    /** 从手牌移除并返回（按 instanceId / cardCode）。 */
    public static CardInstance removeFromHand(PlayerState player, String ref) {
        CardInstance card = findInHand(player, ref);
        if (card != null) {
            player.getHand().remove(card);
        }
        return card;
    }

    /** 场上（战区+基地）查找卡牌。 */
    public static CardInstance findOnField(PlayerState player, String ref) {
        if (ref == null) {
            return null;
        }
        for (CardInstance c : listFieldCards(player)) {
            if (matches(c, ref)) {
                return c;
            }
        }
        return null;
    }

    /** 列出场上全部非空卡牌（战区+基地）。 */
    public static List<CardInstance> listFieldCards(PlayerState player) {
        List<CardInstance> list = new ArrayList<>();
        FieldZone field = player.getField();
        if (field.getVanguard() != null) {
            list.add(field.getVanguard());
        }
        for (CardInstance c : field.getFlank()) {
            if (c != null) {
                list.add(c);
            }
        }
        if (field.getRearguard() != null) {
            list.add(field.getRearguard());
        }
        for (CardInstance c : field.getBase()) {
            if (c != null) {
                list.add(c);
            }
        }
        return list;
    }

    /** 从场上移除卡牌（按实例引用清空槽位）。 */
    public static void removeFromField(PlayerState player, CardInstance card) {
        FieldZone field = player.getField();
        if (field.getVanguard() == card) {
            field.setVanguard(null);
            return;
        }
        CardInstance[] flank = field.getFlank();
        for (int i = 0; i < flank.length; i++) {
            if (flank[i] == card) {
                flank[i] = null;
                return;
            }
        }
        if (field.getRearguard() == card) {
            field.setRearguard(null);
            return;
        }
        CardInstance[] base = field.getBase();
        for (int i = 0; i < base.length; i++) {
            if (base[i] == card) {
                base[i] = null;
                return;
            }
        }
    }

    /**
     * 将卡牌放入场上指定区域。
     *
     * <p>基地：优先使用 index 空槽，否则找第一个空槽。
     */
    public static void placeOnField(PlayerState player, CardInstance card, Zone zone, int index) {
        FieldZone field = player.getField();
        switch (zone) {
            case VANGUARD -> field.setVanguard(card);
            case FLANK_LEFT -> field.getFlank()[0] = card;
            case FLANK_RIGHT -> field.getFlank()[1] = card;
            case REARGUARD -> field.setRearguard(card);
            case BASE -> {
                int slot = resolveBaseSlot(field, index);
                if (slot < 0) {
                    throw new EngineException("基地区已满（上限 " + RuleConstants.MAX_BASE + "）", "302.6");
                }
                field.getBase()[slot] = card;
            }
            default -> throw new EngineException("非法场上目标区域: " + zone, "302");
        }
        card.setCurrentZone(zone);
    }

    /** 目标槽位是否空闲（可放置）。 */
    public static void assertTargetFree(PlayerState player, Zone zone, int index) {
        if (zone == null || !zone.isOnField()) {
            throw new EngineException("目标须为场上区域", "302");
        }
        FieldZone field = player.getField();
        CardInstance occupied =
                switch (zone) {
                    case VANGUARD -> field.getVanguard();
                    case FLANK_LEFT -> field.getFlank()[0];
                    case FLANK_RIGHT -> field.getFlank()[1];
                    case REARGUARD -> field.getRearguard();
                    case BASE -> {
                        if (field.isBaseFull()) {
                            throw new EngineException(
                                    "基地区已满（上限 " + RuleConstants.MAX_BASE + "）", "302.6");
                        }
                        // 指定了槽位则该槽须空；未指定则只要有空槽即可
                        if (index >= 0 && index < RuleConstants.MAX_BASE) {
                            yield field.getBase()[index];
                        }
                        yield null;
                    }
                    default -> throw new EngineException("目标须为场上区域", "302");
                };
        if (occupied != null) {
            throw new EngineException("目标位置已被占用: " + zone, "302");
        }
    }

    /** 从卡组顶抽 n 张到手牌（卡组顶 = 列表末尾）。 */
    public static void drawFromDeck(PlayerState player, int n) {
        List<CardInstance> deck = player.getDeck();
        List<CardInstance> hand = player.getHand();
        int drawCount = Math.min(n, deck.size());
        for (int i = 0; i < drawCount; i++) {
            CardInstance card = deck.remove(deck.size() - 1);
            card.setCurrentZone(Zone.HAND);
            hand.add(card);
        }
    }

    /** 计算可撤退等级合计（Q&A Q7）：战区+基地；盖卡每张计 Lv1。 */
    public static int computeRetrievableLevel(PlayerState player) {
        int total = 0;
        for (CardInstance c : listFieldCards(player)) {
            total += retreatLevelOf(c);
        }
        return total;
    }

    /** 单卡撤退时计值：盖卡=1，否则实际 Lv。 */
    public static int retreatLevelOf(CardInstance card) {
        if (card.isFaceDown()) {
            return 1; // 301.21
        }
        return getLevel(card);
    }

    /** 指定撤退清单合计 Lv。 */
    public static int sumRetreatLevel(PlayerState player, List<String> retreatRefs) {
        int total = 0;
        List<CardInstance> used = new ArrayList<>();
        for (String ref : retreatRefs) {
            CardInstance card = findOnField(player, ref);
            if (card == null || used.contains(card)) {
                continue;
            }
            used.add(card);
            total += retreatLevelOf(card);
        }
        return total;
    }

    /** 将场上角色移入撤退区（结附卡跟随，301.25）。 */
    public static void retreatFromField(PlayerState player, String ref) {
        CardInstance card = findOnField(player, ref);
        if (card == null) {
            throw new EngineException("撤退目标不在场上: " + ref, "301.19");
        }
        removeFromField(player, card);
        card.setCurrentZone(Zone.RETREAT);
        card.setFaceDown(false);
        for (CardInstance att : card.getAttachedCards()) {
            att.setCurrentZone(Zone.RETREAT);
            att.setFaceDown(false);
        }
        player.getRetreat().add(card);
    }

    /** 场上是否已有同名卡（305.6）。 */
    public static boolean hasSameNameOnField(PlayerState player, String name) {
        if (name == null) {
            return false;
        }
        for (CardInstance c : listFieldCards(player)) {
            if (!c.isFaceDown() && name.equals(c.getSnapshot().getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否具备「唯一」关键词（305.6）。
     *
     * <p>优先查 {@link CardInstance#getKeywords()}；效果文本含「【唯一】」作兼容兜底。
     */
    public static boolean hasUniqueKeyword(CardInstance card) {
        if (card.hasKeyword(Keyword.UNIQUE)) {
            return true;
        }
        String text = card.getSnapshot().getEffectText();
        return text != null && text.contains("【唯一】");
    }

    /** 战基移动：一为战区、一为基地（301.24）。 */
    public static boolean isOneCombatOneBase(Zone a, Zone b) {
        if (a == null || b == null) {
            return false;
        }
        return (a.isCombatZone() && b == Zone.BASE) || (a == Zone.BASE && b.isCombatZone());
    }

    /** 按 playerId 查找对局中的玩家。 */
    public static PlayerState requirePlayer(GameState state, String playerId) {
        if (playerId != null && playerId.equals(state.getActivePlayer().getPlayerId())) {
            return state.getActivePlayer();
        }
        if (playerId != null && playerId.equals(state.getInactivePlayer().getPlayerId())) {
            return state.getInactivePlayer();
        }
        throw new EngineException("玩家不在本对局中: " + playerId, "303.2.a");
    }

    /** 读取场上指定战区/基地槽位的卡（基地需 index）。 */
    public static CardInstance getAt(FieldZone field, Zone zone, int index) {
        return switch (zone) {
            case VANGUARD -> field.getVanguard();
            case FLANK_LEFT -> field.getFlank()[0];
            case FLANK_RIGHT -> field.getFlank()[1];
            case REARGUARD -> field.getRearguard();
            case BASE -> {
                if (index < 0 || index >= field.getBase().length) {
                    yield null;
                }
                yield field.getBase()[index];
            }
            default -> null;
        };
    }

    /** 互换两个战区位置（不计为移动，303.2.a.4.2.1）。 */
    public static void swapCombatPositions(FieldZone field, Zone from, Zone to) {
        if (!from.isCombatZone() || !to.isCombatZone()) {
            throw new EngineException("调整位置仅限战区", "303.2.a.4.2.1");
        }
        CardInstance a = getAt(field, from, 0);
        CardInstance b = getAt(field, to, 0);
        placeAt(field, from, b);
        placeAt(field, to, a);
        if (a != null) {
            a.setCurrentZone(to);
        }
        if (b != null) {
            b.setCurrentZone(from);
        }
    }

    private static void placeAt(FieldZone field, Zone zone, CardInstance card) {
        switch (zone) {
            case VANGUARD -> field.setVanguard(card);
            case FLANK_LEFT -> field.getFlank()[0] = card;
            case FLANK_RIGHT -> field.getFlank()[1] = card;
            case REARGUARD -> field.setRearguard(card);
            default -> throw new EngineException("非法战区: " + zone, "302");
        }
    }

    /** 将指定实例移入撤退区（若仍在场上则先移除）。 */
    public static void retreatCard(PlayerState player, CardInstance card) {
        if (card == null) {
            return;
        }
        removeFromField(player, card);
        card.setCurrentZone(Zone.RETREAT);
        card.setFaceDown(false);
        for (CardInstance att : card.getAttachedCards()) {
            att.setCurrentZone(Zone.RETREAT);
            att.setFaceDown(false);
        }
        if (!player.getRetreat().contains(card)) {
            player.getRetreat().add(card);
        }
    }

    /**
     * 是否具备「连击」关键词（305.3）。
     *
     * <p>优先查 {@link CardInstance#getKeywords()}；效果文本含「【连击】」作兼容兜底。
     */
    public static boolean hasComboKeyword(CardInstance card) {
        if (card.hasKeyword(Keyword.COMBO)) {
            return true;
        }
        String text = card.getSnapshot().getEffectText();
        return text != null && text.contains("【连击】");
    }

    /**
     * 是否具备「强袭」关键词（305.4）。
     *
     * <p>优先查 {@link CardInstance#getKeywords()}；效果文本含「【强袭】」作兼容兜底。
     */
    public static boolean hasAssaultKeyword(CardInstance card) {
        if (card.hasKeyword(Keyword.ASSAULT)) {
            return true;
        }
        String text = card.getSnapshot().getEffectText();
        return text != null && text.contains("【强袭】");
    }

    /**
     * 是否具备「拦截」关键词（305.2）。
     *
     * <p>优先查 keywords；效果文本含「【拦截】」作兼容兜底。
     */
    public static boolean hasInterceptKeyword(CardInstance card) {
        if (card.hasKeyword(Keyword.INTERCEPT)) {
            return true;
        }
        String text = card.getSnapshot().getEffectText();
        return text != null && text.contains("【拦截】");
    }

    /**
     * 是否具备「应对」关键词（305.1）。
     *
     * <p>优先查 keywords；效果文本含「【应对】」作兼容兜底。
     */
    public static boolean hasResponseKeyword(CardInstance card) {
        if (card.hasKeyword(Keyword.RESPONSE)) {
            return true;
        }
        String text = card.getSnapshot().getEffectText();
        return text != null && text.contains("【应对】");
    }

    /**
     * 是否具备「空袭」关键词（305.5）。
     *
     * <p>优先查 keywords；效果文本含「【空袭】」作兼容兜底。
     */
    public static boolean hasAirStrikeKeyword(CardInstance card) {
        if (card.hasKeyword(Keyword.AIR_STRIKE)) {
            return true;
        }
        String text = card.getSnapshot().getEffectText();
        return text != null && text.contains("【空袭】");
    }

    /**
     * 在场上父卡的结附列表中查找结附卡。
     *
     * @return 结附卡实例，未找到返回 null
     */
    public static CardInstance findAttached(PlayerState player, String ref) {
        if (ref == null) {
            return null;
        }
        for (CardInstance parent : listFieldCards(player)) {
            for (CardInstance att : parent.getAttachedCards()) {
                if (matches(att, ref)) {
                    return att;
                }
            }
        }
        return null;
    }

    /** 查找结附卡的父卡。 */
    public static CardInstance findAttachParent(PlayerState player, CardInstance child) {
        for (CardInstance parent : listFieldCards(player)) {
            if (parent.getAttachedCards().contains(child)) {
                return parent;
            }
        }
        return null;
    }

    /** 从父卡结附列表中移除结附卡（301.26）。 */
    public static void removeFromParent(PlayerState player, CardInstance child) {
        CardInstance parent = findAttachParent(player, child);
        if (parent == null) {
            throw new EngineException("目标不是结附卡", "301.26");
        }
        parent.getAttachedCards().remove(child);
    }

    /** 重置卡牌运行时战力/距离为快照印刷值（盖伏刷新，301.13）。 */
    public static void resetCombatStats(CardInstance card) {
        Integer power = card.getSnapshot().getPower();
        Integer range = card.getSnapshot().getAttackRange();
        card.setCurrentPower(power != null ? power : 0);
        card.setCurrentRange(range != null ? range : 0);
    }

    private static int resolveBaseSlot(FieldZone field, int index) {
        CardInstance[] base = field.getBase();
        if (index >= 0 && index < base.length && base[index] == null) {
            return index;
        }
        for (int i = 0; i < base.length; i++) {
            if (base[i] == null) {
                return i;
            }
        }
        return -1;
    }

    private static boolean matches(CardInstance card, String ref) {
        return Objects.equals(card.getInstanceId(), ref)
                || Objects.equals(card.getSnapshot().getCardCode(), ref);
    }
}
