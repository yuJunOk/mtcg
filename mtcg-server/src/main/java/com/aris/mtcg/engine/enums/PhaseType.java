package com.aris.mtcg.engine.enums;

/**
 * 回合阶段枚举（303.2.a）。
 *
 * <p>6 阶段严格顺序执行，不可回退。
 *
 * @author pengYuJun
 */
public enum PhaseType {

    /** 回合开始（303.2.a.1） */
    TURN_START("回合开始"),
    /** 抽卡阶段（303.2.a.2） */
    DRAW("抽卡阶段"),
    /** 行动阶段（303.2.a.3） */
    ACTION("行动阶段"),
    /** 战斗阶段（303.2.a.4） */
    COMBAT("战斗阶段"),
    /** 应对阶段（303.2.a.5） */
    RESPONSE("应对阶段"),
    /** 回合结束（303.2.a.6） */
    TURN_END("回合结束");

    private final String label;

    PhaseType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /**
     * 获取下一个阶段（不可回退，303.2.a）。
     *
     * <p>TURN_END 的下一个是 TURN_START（下一回合）。
     */
    public PhaseType next() {
        PhaseType[] values = values();
        int nextIndex = (this.ordinal() + 1) % values.length;
        return values[nextIndex];
    }
}
