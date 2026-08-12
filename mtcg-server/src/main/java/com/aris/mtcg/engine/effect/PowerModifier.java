package com.aris.mtcg.engine.effect;

import com.aris.mtcg.engine.model.CardInstance;

/**
 * 属性修改器（规则 301.40–301.41）。
 *
 * <p>作用于 Lv / R / 战力；记录来源、类别、类型、值、持续时间。
 *
 * @author pengYuJun
 */
public class PowerModifier {

    /** 修改器类别：来自触发/启动效果，还是常驻效果（决定 301.41 叠加规则）。 */
    public enum Category {
        TRIGGER_ACTIVATED,
        CONTINUOUS
    }

    /** 修改器类型：变更（设绝对值/替换）还是增减（±）。 */
    public enum Type {
        CHANGE,
        INCREMENT
    }

    /** 作用属性。 */
    public enum Attribute {
        POWER,
        LEVEL,
        ATTACK_RANGE
    }

    /** 持续时间。 */
    public enum Duration {
        /** 仅本回合（回合结束终止，303.2.a.6） */
        THIS_TURN,
        /** 来源在场期间（常驻型/离场即终） */
        WHILE_SOURCE_ON_FIELD,
        /** 永久 */
        PERMANENT
    }

    private final CardInstance source;
    private final CardInstance target;
    private final Category category;
    private final Type type;
    private final Attribute attribute;
    private final int value;
    private final Duration duration;

    public PowerModifier(
            CardInstance source,
            CardInstance target,
            Category category,
            Type type,
            Attribute attribute,
            int value,
            Duration duration) {
        this.source = source;
        this.target = target;
        this.category = category;
        this.type = type;
        this.attribute = attribute;
        this.value = value;
        this.duration = duration;
    }

    public CardInstance getSource() {
        return source;
    }

    public CardInstance getTarget() {
        return target;
    }

    public Category getCategory() {
        return category;
    }

    public Type getType() {
        return type;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public int getValue() {
        return value;
    }

    public Duration getDuration() {
        return duration;
    }
}
