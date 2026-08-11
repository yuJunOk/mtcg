package com.aris.mtcg.engine.action;

import com.aris.mtcg.engine.enums.Zone;

/**
 * 战斗调整位置互换对（303.2.a.4.2.1）。
 *
 * @author pengYuJun
 */
public class AdjustPair {

    private Zone from;
    private Zone to;

    public AdjustPair() {}

    public AdjustPair(Zone from, Zone to) {
        this.from = from;
        this.to = to;
    }

    public Zone getFrom() {
        return from;
    }

    public void setFrom(Zone from) {
        this.from = from;
    }

    public Zone getTo() {
        return to;
    }

    public void setTo(Zone to) {
        this.to = to;
    }
}
