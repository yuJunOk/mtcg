package com.aris.mtcg.engine.action;

import com.aris.mtcg.engine.model.GameState;

/**
 * 操作处理器统一接口。
 *
 * <p>设计要点：
 *
 * <ol>
 *   <li>每个 Handler 只处理一种 ActionType；
 *   <li>校验与执行分离——validate 抛 EngineException，execute 返回 ActionResult；
 *   <li>Handler 无状态，可单例复用，所有状态读写均通过 GameState。
 * </ol>
 *
 * @author pengYuJun
 */
public interface ActionTypeHandler {

    /** 该 Handler 处理的操作类型。 */
    ActionType supportedType();

    /** 校验操作合法性，不通过抛 EngineException。 */
    void validate(GameState state, ActionRequest request);

    /** 执行操作，返回结果。调用前必须先通过 validate。 */
    ActionResult execute(GameState state, ActionRequest request);
}
