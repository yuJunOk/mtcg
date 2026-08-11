package com.aris.mtcg.engine.action;

import com.aris.mtcg.engine.model.GameState;
import java.util.EnumMap;
import java.util.Map;

/**
 * 操作分发器：注册 Handler 并按 ActionType 路由。
 *
 * <p>由 GameEngine 在构造时装配，运行时调用 {@link #dispatch}。
 *
 * @author pengYuJun
 */
public class ActionDispatcher {

    private final Map<ActionType, ActionTypeHandler> handlers = new EnumMap<>(ActionType.class);

    /** 注册操作处理器（同类型后注册覆盖先注册）。 */
    public void register(ActionTypeHandler handler) {
        handlers.put(handler.supportedType(), handler);
    }

    /**
     * 按 ActionType 路由：先 validate 再 execute。
     *
     * @throws EngineException 未注册的操作类型，或 validate 失败
     */
    public ActionResult dispatch(GameState state, ActionRequest request) {
        ActionTypeHandler handler = handlers.get(request.getType());
        if (handler == null) {
            throw new EngineException("未注册的操作类型: " + request.getType());
        }
        handler.validate(state, request);
        return handler.execute(state, request);
    }

    /** 查询已注册的 Handler（供 AI LegalActionProvider 等复用 validate）。 */
    public ActionTypeHandler handlerOf(ActionType type) {
        return handlers.get(type);
    }
}
