package com.aris.mtcg.engine.effect;

import com.aris.mtcg.engine.model.PlayerState;

/**
 * 待结算的触发型效果实例（规则 304.1）。
 *
 * @author pengYuJun
 */
public class TriggeredEffect {

    private final Effect effect;
    private final EffectContext context;
    private final PlayerState controller;

    /** 结算前校验：源头未离场、未失效等 */
    private boolean valid = true;

    public TriggeredEffect(Effect effect, EffectContext context, PlayerState controller) {
        this.effect = effect;
        this.context = context;
        this.controller = controller;
    }

    public Effect getEffect() {
        return effect;
    }

    public EffectContext getContext() {
        return context;
    }

    public PlayerState getController() {
        return controller;
    }

    public boolean isStillValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
