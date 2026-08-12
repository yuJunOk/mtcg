package com.aris.mtcg.engine.keyword;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * 关键词能力（规则 305）。
 *
 * @author pengYuJun
 */
public enum Keyword {
    /** 应对（305.1） */
    RESPONSE,
    /** 拦截（305.2） */
    INTERCEPT,
    /** 连击（305.3） */
    COMBO,
    /** 强袭（305.4） */
    ASSAULT,
    /** 空袭（305.5） */
    AIR_STRIKE,
    /** 唯一（305.6） */
    UNIQUE;

    /**
     * 从效果原文提取关键词标记（黑底白字，201.10.g）。
     *
     * <p>本迭代仅识别固定标记；完整解析留给 EffectParser。
     */
    public static Set<Keyword> fromEffectText(String effectText) {
        if (effectText == null || effectText.isBlank()) {
            return Collections.emptySet();
        }
        EnumSet<Keyword> set = EnumSet.noneOf(Keyword.class);
        if (effectText.contains("【应对】")) {
            set.add(RESPONSE);
        }
        if (effectText.contains("【拦截】")) {
            set.add(INTERCEPT);
        }
        if (effectText.contains("【连击】")) {
            set.add(COMBO);
        }
        if (effectText.contains("【强袭】")) {
            set.add(ASSAULT);
        }
        if (effectText.contains("【空袭】")) {
            set.add(AIR_STRIKE);
        }
        if (effectText.contains("【唯一】")) {
            set.add(UNIQUE);
        }
        return Collections.unmodifiableSet(set);
    }
}
