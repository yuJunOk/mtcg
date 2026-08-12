package com.aris.mtcg.engine.effect;

import com.aris.mtcg.engine.model.CardInstance;
import com.aris.mtcg.engine.model.GameState;

/**
 * 效果结算上下文。
 *
 * <p>每次触发或启动时构造；贯穿条件校验 → 动作结算 → 战力变更全流程。
 *
 * @author pengYuJun
 */
public class EffectContext {

    private GameState gameState;

    /** 效果源头（【此卡】，规则 301.7） */
    private CardInstance source;

    /** 目标卡（无目标则为 null） */
    private CardInstance target;

    /** 触发事件（启动型为 null） */
    private TriggerEvent triggerEvent;

    public EffectContext(GameState gameState, CardInstance source) {
        this.gameState = gameState;
        this.source = source;
    }

    public EffectContext(
            GameState gameState,
            CardInstance source,
            CardInstance target,
            TriggerEvent triggerEvent) {
        this.gameState = gameState;
        this.source = source;
        this.target = target;
        this.triggerEvent = triggerEvent;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public CardInstance getSource() {
        return source;
    }

    public void setSource(CardInstance source) {
        this.source = source;
    }

    public CardInstance getTarget() {
        return target;
    }

    public void setTarget(CardInstance target) {
        this.target = target;
    }

    public TriggerEvent getTriggerEvent() {
        return triggerEvent;
    }

    public void setTriggerEvent(TriggerEvent triggerEvent) {
        this.triggerEvent = triggerEvent;
    }
}
