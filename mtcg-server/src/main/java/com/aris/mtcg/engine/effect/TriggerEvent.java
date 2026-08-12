package com.aris.mtcg.engine.effect;

import com.aris.mtcg.engine.model.CardInstance;
import com.aris.mtcg.engine.model.PlayerState;

/**
 * 触发事件（规则 304.1）。
 *
 * <p>同一动作同时触发的多个效果视为同时（304.1 末条）。
 *
 * @author pengYuJun
 */
public class TriggerEvent {

    private final TriggerType type;

    /** 事件发生时点的控制者（双方同时→回合玩家先） */
    private final PlayerState controller;

    /** 事件源头卡（如号召进场的那张卡） */
    private final CardInstance source;

    /** 事件附加数据（如攻击距离、战斗结果） */
    private final Object payload;

    public TriggerEvent(TriggerType type, PlayerState controller, CardInstance source) {
        this(type, controller, source, null);
    }

    public TriggerEvent(
            TriggerType type, PlayerState controller, CardInstance source, Object payload) {
        this.type = type;
        this.controller = controller;
        this.source = source;
        this.payload = payload;
    }

    public TriggerType getType() {
        return type;
    }

    public PlayerState getController() {
        return controller;
    }

    public CardInstance getSource() {
        return source;
    }

    public Object getPayload() {
        return payload;
    }
}
