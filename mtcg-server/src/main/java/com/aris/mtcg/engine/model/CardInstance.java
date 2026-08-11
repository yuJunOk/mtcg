package com.aris.mtcg.engine.model;

import com.aris.mtcg.engine.enums.Zone;
import java.util.ArrayList;
import java.util.List;

/**
 * 卡牌实例：对局中所有卡牌的统一表示。
 *
 * <p>= 不可变快照（CardSnapshot）+ 运行时状态。 场外区域的卡牌运行时状态字段保持默认值；场上卡牌才使用这些标记。
 *
 * @author pengYuJun
 */
public class CardInstance {

    /** 实例唯一 ID（对局内唯一，用于区分同名卡） */
    private final String instanceId;

    /** 卡牌快照（不可变） */
    private final CardSnapshot snapshot;

    // === 运行时状态（仅场上卡牌使用） ===

    /** 当前所在区域（302） */
    private Zone currentZone;

    /** 当前战力（受效果影响，301.41） */
    private int currentPower;

    /** 当前攻击距离 R（受效果影响，301.41） */
    private int currentRange;

    /** 本回合进场（影响战基移动限制，303.2.a.3.1.3） */
    private boolean enteredThisTurn;

    /** 本回合已战基移动（每角色每回合 1 次，303.2.a.3.1.3） */
    private boolean movedThisTurn;

    /** 本回合已攻击次数（连击=2，305.3） */
    private int attackUsed;

    /** 本回合已使用拦截（每回合 1 次，305.2） */
    private boolean interceptUsed;

    /** 是否盖卡（背面朝上，301.21） */
    private boolean faceDown;

    /** 结附卡列表（按结附顺序，不可改变，301.25.m） */
    private final List<CardInstance> attachedCards = new ArrayList<>();

    public CardInstance(String instanceId, CardSnapshot snapshot) {
        this.instanceId = instanceId;
        this.snapshot = snapshot;
        this.currentPower = snapshot.getPower() != null ? snapshot.getPower() : 0;
        this.currentRange = snapshot.getAttackRange() != null ? snapshot.getAttackRange() : 0;
    }

    /**
     * 回合结束时重置本回合标记（303.2.a.6）。
     *
     * <p>由 TurnEndHandler 调用。
     */
    public void resetTurnFlags() {
        this.enteredThisTurn = false;
        this.movedThisTurn = false;
        this.attackUsed = 0;
        this.interceptUsed = false;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public CardSnapshot getSnapshot() {
        return snapshot;
    }

    public Zone getCurrentZone() {
        return currentZone;
    }

    public void setCurrentZone(Zone currentZone) {
        this.currentZone = currentZone;
    }

    public int getCurrentPower() {
        return currentPower;
    }

    public void setCurrentPower(int currentPower) {
        this.currentPower = currentPower;
    }

    public int getCurrentRange() {
        return currentRange;
    }

    public void setCurrentRange(int currentRange) {
        this.currentRange = currentRange;
    }

    public boolean isEnteredThisTurn() {
        return enteredThisTurn;
    }

    public void setEnteredThisTurn(boolean enteredThisTurn) {
        this.enteredThisTurn = enteredThisTurn;
    }

    public boolean isMovedThisTurn() {
        return movedThisTurn;
    }

    public void setMovedThisTurn(boolean movedThisTurn) {
        this.movedThisTurn = movedThisTurn;
    }

    public int getAttackUsed() {
        return attackUsed;
    }

    public void setAttackUsed(int attackUsed) {
        this.attackUsed = attackUsed;
    }

    public boolean isInterceptUsed() {
        return interceptUsed;
    }

    public void setInterceptUsed(boolean interceptUsed) {
        this.interceptUsed = interceptUsed;
    }

    public boolean isFaceDown() {
        return faceDown;
    }

    public void setFaceDown(boolean faceDown) {
        this.faceDown = faceDown;
    }

    public List<CardInstance> getAttachedCards() {
        return attachedCards;
    }
}
