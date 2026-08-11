package com.aris.mtcg.engine.action;

/**
 * 引擎内部统一异常，承载规则条款编号便于追溯。
 *
 * @author pengYuJun
 */
public class EngineException extends RuntimeException {

    /** 规则条款编号，如 "303.2.a.3.1.1"；无对应条款时可为空 */
    private final String ruleRef;

    public EngineException(String message) {
        super(message);
        this.ruleRef = null;
    }

    public EngineException(String message, String ruleRef) {
        super(ruleRef != null ? message + " [规则: " + ruleRef + "]" : message);
        this.ruleRef = ruleRef;
    }

    public String getRuleRef() {
        return ruleRef;
    }
}
