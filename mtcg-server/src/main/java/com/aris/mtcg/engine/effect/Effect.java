package com.aris.mtcg.engine.effect;

import com.aris.mtcg.engine.enums.Zone;
import com.aris.mtcg.engine.keyword.Keyword;
import java.util.Collections;
import java.util.List;

/**
 * 卡牌效果定义（规则 201.10）。
 *
 * <p>不可变：从卡牌快照加载后不再修改；运行时变更记录在 {@link PowerModifier}。
 *
 * @author pengYuJun
 */
public final class Effect {

    private final EffectType type;
    private final Zone effectiveZone;
    private final String requirement;
    private final String text;
    private final String condition;
    private final String action;
    private final List<Keyword> keywords;

    /** 含「可以」则为可选（304.1.a） */
    private final boolean optional;

    public Effect(
            EffectType type,
            Zone effectiveZone,
            String requirement,
            String text,
            String condition,
            String action,
            List<Keyword> keywords) {
        this.type = type;
        this.effectiveZone = effectiveZone;
        this.requirement = requirement;
        this.text = text;
        this.condition = condition;
        this.action = action;
        this.keywords = keywords == null ? Collections.emptyList() : List.copyOf(keywords);
        this.optional = text != null && text.contains("可以");
    }

    /** 仅含原文的便捷构造（解析器空壳用）。 */
    public static Effect ofText(String text, List<Keyword> keywords) {
        return new Effect(null, null, null, text, null, null, keywords);
    }

    public EffectType getType() {
        return type;
    }

    public Zone getEffectiveZone() {
        return effectiveZone;
    }

    public String getRequirement() {
        return requirement;
    }

    public String getText() {
        return text;
    }

    public String getCondition() {
        return condition;
    }

    public String getAction() {
        return action;
    }

    public List<Keyword> getKeywords() {
        return keywords;
    }

    /** 304.1.a：含「可以」= 可选。 */
    public boolean isOptional() {
        return optional;
    }
}
