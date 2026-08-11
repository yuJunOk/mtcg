package com.aris.mtcg.engine.model;

import com.aris.mtcg.engine.enums.PlayerSide;
import com.aris.mtcg.engine.rule.RuleConstants;
import java.util.ArrayList;
import java.util.List;

/**
 * 玩家状态。
 *
 * <p>包含该玩家的所有区域（场外 + 场上）及行动阶段计数器。
 *
 * <p>约定：主卡组顶 = {@code deck} 列表末尾；卡组底 = 列表头部。
 *
 * @author pengYuJun
 */
public class PlayerState {

    /** 玩家 ID */
    private final String playerId;

    /** 先后攻：FIRST / SECOND */
    private PlayerSide side;

    // === 场外区域 ===

    /** 主卡组（50 张，101.1.b）；顶=末尾，底=头部 */
    private final List<CardInstance> deck = new ArrayList<>();

    /** 冲击卡组（9 张，101.2.b） */
    private final List<CardInstance> rushDeck = new ArrayList<>();

    /** 手牌（无上限，302.13） */
    private final List<CardInstance> hand = new ArrayList<>();

    /** 时间线（冲击卡，达 9 张获胜，103.1.a） */
    private final List<CardInstance> timeline = new ArrayList<>();

    /** 撤退区（无上限，302.8） */
    private final List<CardInstance> retreat = new ArrayList<>();

    /** 虚空区（无上限，302.9） */
    private final List<CardInstance> voidZone = new ArrayList<>();

    // === 场上区域 ===

    /** 场上（战区+基地） */
    private final FieldZone field = new FieldZone();

    // === 行动阶段计数器（303.2.a.3） ===

    /** 基地部署次数（每阶段上限 1，303.2.a.3.1.1） */
    private int baseDeployCount;

    /** 行动号召次数（每阶段上限 3，先攻首回合 1，303.2.a.3.1.2） */
    private int summonCount;

    /** 本回合是否已使用调整位置（303.2.a.4.2.1） */
    private boolean adjustUsed;

    public PlayerState(String playerId) {
        this.playerId = playerId;
    }

    /**
     * 回合结束时重置行动计数器（303.2.a.6）。
     *
     * <p>由 TurnEndHandler 调用。
     */
    public void resetActionCounters() {
        this.baseDeployCount = 0;
        this.summonCount = 0;
        this.adjustUsed = false;
    }

    /**
     * 本回合行动号召上限（303.2.a.3.1.2）。
     *
     * <p>先攻首回合 1 次，其余 3 次。
     *
     * @param isFirstTurnOfGame 是否为先攻玩家首回合
     */
    public int getSummonLimit(boolean isFirstTurnOfGame) {
        if (side == PlayerSide.FIRST && isFirstTurnOfGame) {
            return RuleConstants.MAX_SUMMON_FIRST; // 303.2.a.3.1.2
        }
        return RuleConstants.MAX_SUMMON; // 303.2.a.3.1.2
    }

    public String getPlayerId() {
        return playerId;
    }

    public PlayerSide getSide() {
        return side;
    }

    public void setSide(PlayerSide side) {
        this.side = side;
    }

    public List<CardInstance> getDeck() {
        return deck;
    }

    public List<CardInstance> getRushDeck() {
        return rushDeck;
    }

    public List<CardInstance> getHand() {
        return hand;
    }

    public List<CardInstance> getTimeline() {
        return timeline;
    }

    public List<CardInstance> getRetreat() {
        return retreat;
    }

    public List<CardInstance> getVoidZone() {
        return voidZone;
    }

    public FieldZone getField() {
        return field;
    }

    public int getBaseDeployCount() {
        return baseDeployCount;
    }

    public void setBaseDeployCount(int baseDeployCount) {
        this.baseDeployCount = baseDeployCount;
    }

    public int getSummonCount() {
        return summonCount;
    }

    public void setSummonCount(int summonCount) {
        this.summonCount = summonCount;
    }

    public boolean isAdjustUsed() {
        return adjustUsed;
    }

    public void setAdjustUsed(boolean adjustUsed) {
        this.adjustUsed = adjustUsed;
    }
}
