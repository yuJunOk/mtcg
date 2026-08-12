package com.aris.mtcg.engine.effect;

/**
 * 效果文本解析器（预留）。
 *
 * <p>将卡牌 effect_text 原文解析为结构化 {@link Effect}。当前迭代不实现具体解析逻辑。
 *
 * @author pengYuJun
 */
public interface EffectParser {

    /**
     * 解析效果文本。
     *
     * @param effectText 效果原文（可能含「可以」「如此做后」等关键字）
     * @return 解析后的 Effect；不支持时返回仅含原文的 Effect
     */
    Effect parse(String effectText);

    /** 是否支持解析该文本（按标记色/关键字判断）。 */
    boolean supports(String effectText);
}
