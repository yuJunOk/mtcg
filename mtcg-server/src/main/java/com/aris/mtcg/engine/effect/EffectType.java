package com.aris.mtcg.engine.effect;

/**
 * 效果类型（规则 201.10.d–f）。
 *
 * <pre>
 * 201.10.d 触发型：满足条件自动触发
 * 201.10.e 常驻型：在对应区域持续生效
 * 201.10.f 启动型：行动阶段可启动；含「应对·启动」变体
 * </pre>
 *
 * @author pengYuJun
 */
public enum EffectType {
    /** 触发型（201.10.d） */
    TRIGGER,
    /** 常驻型（201.10.e） */
    CONTINUOUS,
    /** 启动型（201.10.f） */
    ACTIVATED,
    /** 应对·启动（201.10.f 变体） */
    RESPONSE_ACTIVATED
}
