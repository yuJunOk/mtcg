package com.aris.mtcg.engine.keyword;

import com.aris.mtcg.engine.effect.EffectContext;
import com.aris.mtcg.engine.enums.Zone;

/**
 * 关键词能力处理器接口。
 *
 * <p>每个关键词一个实现；由 {@link KeywordHandlerRegistry} 注册并按 Keyword 查找。
 *
 * @author pengYuJun
 */
public interface KeywordHandler {

    /** 所处理的关键词。 */
    Keyword getKeyword();

    /** 生效区域（手牌/战区代表位/场上）。 */
    Zone getEffectiveZone();

    /** 是否可应用/可启动（条件校验）。 */
    boolean canApply(EffectContext ctx);

    /** 应用关键词效果（修改状态或挂接战斗流程）。 */
    void apply(EffectContext ctx);
}
