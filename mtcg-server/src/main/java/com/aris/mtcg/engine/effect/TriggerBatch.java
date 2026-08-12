package com.aris.mtcg.engine.effect;

import com.aris.mtcg.engine.model.PlayerState;
import java.util.ArrayList;
import java.util.List;

/**
 * 同一事件同时触发的效果批次（规则 304.1）。
 *
 * <p>同一规则动作/效果同时触发多个 → 视为同时（304.1 末条）。
 *
 * @author pengYuJun
 */
public class TriggerBatch {

    private final List<TriggeredEffect> effects = new ArrayList<>();

    public void add(TriggeredEffect te) {
        effects.add(te);
    }

    /** 按 304.1 排序：双方同时→回合玩家先；同一玩家多个→自选顺序（本迭代按入队顺序，D6-2）。 */
    public void orderByTurnPlayerFirst(PlayerState turnPlayer) {
        effects.sort(
                (a, b) -> {
                    boolean aTurn = a.getController() == turnPlayer;
                    boolean bTurn = b.getController() == turnPlayer;
                    return Boolean.compare(bTurn, aTurn);
                });
    }

    public List<TriggeredEffect> getEffects() {
        return effects;
    }

    public boolean isEmpty() {
        return effects.isEmpty();
    }
}
