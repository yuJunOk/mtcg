package com.aris.mtcg.engine.model;

import java.util.List;

/**
 * 卡牌快照（不可变）。
 *
 * <p>对局初始化时从 DB 加载，对局中只读，不修改。 引擎通过快照自包含运行，不访问数据库（概要设计 §6.1）。
 *
 * @author pengYuJun
 */
public final class CardSnapshot {

    /** 卡牌编号，如 BP01-020 */
    private final String cardCode;

    /** 卡牌名称（含称号） */
    private final String name;

    /** 等级 Lv（1-6），冲击卡为 null（201.6） */
    private final Integer level;

    /** 颜色：RED/YELLOW/BLUE/GREEN/ORANGE/PURPLE，冲击卡为 null（201.5） */
    private final String color;

    /** 攻击距离 R，冲击卡为 null（201.11） */
    private final Integer attackRange;

    /** 战力（印刷值），冲击卡为 null（201.12） */
    private final Integer power;

    /** 特征列表，如 ["人类","复仇者联盟"]（201.9） */
    private final List<String> traits;

    /** 效果描述原文（当前阶段不解析，迭代六处理）（201.10） */
    private final String effectText;

    /** 卡牌类型：CHARACTER / RUSH_POINT */
    private final String cardType;

    public CardSnapshot(
            String cardCode,
            String name,
            Integer level,
            String color,
            Integer attackRange,
            Integer power,
            List<String> traits,
            String effectText,
            String cardType) {
        this.cardCode = cardCode;
        this.name = name;
        this.level = level;
        this.color = color;
        this.attackRange = attackRange;
        this.power = power;
        this.traits = traits == null ? List.of() : List.copyOf(traits);
        this.effectText = effectText;
        this.cardType = cardType;
    }

    public String getCardCode() {
        return cardCode;
    }

    public String getName() {
        return name;
    }

    public Integer getLevel() {
        return level;
    }

    public String getColor() {
        return color;
    }

    public Integer getAttackRange() {
        return attackRange;
    }

    public Integer getPower() {
        return power;
    }

    public List<String> getTraits() {
        return traits;
    }

    public String getEffectText() {
        return effectText;
    }

    public String getCardType() {
        return cardType;
    }
}
