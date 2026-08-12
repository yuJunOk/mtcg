package com.aris.mtcg.engine;

import com.aris.mtcg.engine.action.ActionDispatcher;
import com.aris.mtcg.engine.action.ActionRequest;
import com.aris.mtcg.engine.action.ActionResult;
import com.aris.mtcg.engine.action.ActionType;
import com.aris.mtcg.engine.action.handler.ActivateEffectHandler;
import com.aris.mtcg.engine.action.handler.AttachHandler;
import com.aris.mtcg.engine.action.handler.AttackSequenceHandler;
import com.aris.mtcg.engine.action.handler.BaseDeployHandler;
import com.aris.mtcg.engine.action.handler.CombatAdjustHandler;
import com.aris.mtcg.engine.action.handler.CombatBaseMoveHandler;
import com.aris.mtcg.engine.action.handler.CombatResponseHandler;
import com.aris.mtcg.engine.action.handler.DetachHandler;
import com.aris.mtcg.engine.action.handler.EndPhaseHandler;
import com.aris.mtcg.engine.action.handler.FlipFaceUpHandler;
import com.aris.mtcg.engine.action.handler.InterceptHandler;
import com.aris.mtcg.engine.action.handler.MulliganHandler;
import com.aris.mtcg.engine.action.handler.ResponseSummonHandler;
import com.aris.mtcg.engine.action.handler.SetFaceDownHandler;
import com.aris.mtcg.engine.action.handler.SummonHandler;
import com.aris.mtcg.engine.action.handler.SurrenderHandler;
import com.aris.mtcg.engine.combat.WinChecker;
import com.aris.mtcg.engine.effect.EffectResolver;
import com.aris.mtcg.engine.effect.NoOpEffectParser;
import com.aris.mtcg.engine.effect.PowerModifierStack;
import com.aris.mtcg.engine.enums.GameStatus;
import com.aris.mtcg.engine.enums.PhaseType;
import com.aris.mtcg.engine.keyword.KeywordHandlerRegistry;
import com.aris.mtcg.engine.model.GameState;
import com.aris.mtcg.engine.phase.ActionPhaseHandler;
import com.aris.mtcg.engine.phase.CombatHandler;
import com.aris.mtcg.engine.phase.DrawHandler;
import com.aris.mtcg.engine.phase.PhaseHandler;
import com.aris.mtcg.engine.phase.ResponseHandler;
import com.aris.mtcg.engine.phase.TurnEndHandler;
import com.aris.mtcg.engine.phase.TurnStartHandler;
import java.util.EnumMap;
import java.util.Map;
import lombok.Getter;

/**
 * 对战引擎核心。
 *
 * <p>纯 POJO，不依赖 Spring。通过 new 创建，管理 GameState、阶段流转与操作分发。
 *
 * <p>阶段流转不可回退（303.2.a），按 TURN_START → DRAW → ACTION → COMBAT → RESPONSE → TURN_END 顺序执行。
 *
 * @author pengYuJun
 */
public class GameEngine {

    @Getter private final GameState state;
    @Getter private final ActionDispatcher actionDispatcher;
    @Getter private final EffectResolver effectResolver;
    @Getter private final KeywordHandlerRegistry keywordHandlerRegistry;

    private final Map<PhaseType, PhaseHandler> handlers = new EnumMap<>(PhaseType.class);
    private final GameInitializer gameInitializer;

    public GameEngine(GameState state) {
        this(state, new GameInitializer());
    }

    /** 可注入 GameInitializer（便于测试固定随机种子的调度）。 */
    public GameEngine(GameState state, GameInitializer gameInitializer) {
        this.state = state;
        this.gameInitializer = gameInitializer;
        this.actionDispatcher = new ActionDispatcher();
        this.effectResolver = new EffectResolver(new PowerModifierStack(), new NoOpEffectParser());
        this.keywordHandlerRegistry = new KeywordHandlerRegistry();
        registerPhaseHandlers();
        registerActionHandlers();
    }

    /** 注册 6 个阶段处理器。 */
    private void registerPhaseHandlers() {
        handlers.put(PhaseType.TURN_START, new TurnStartHandler());
        handlers.put(PhaseType.DRAW, new DrawHandler());
        handlers.put(PhaseType.ACTION, new ActionPhaseHandler());
        handlers.put(PhaseType.COMBAT, new CombatHandler());
        handlers.put(PhaseType.RESPONSE, new ResponseHandler());
        handlers.put(PhaseType.TURN_END, new TurnEndHandler());
    }

    /** 注册全部操作处理器。 */
    private void registerActionHandlers() {
        actionDispatcher.register(new MulliganHandler(gameInitializer));
        actionDispatcher.register(new BaseDeployHandler());
        actionDispatcher.register(new SummonHandler(effectResolver));
        actionDispatcher.register(new CombatBaseMoveHandler());
        actionDispatcher.register(new ActivateEffectHandler(effectResolver));
        actionDispatcher.register(new SetFaceDownHandler());
        actionDispatcher.register(new FlipFaceUpHandler());
        actionDispatcher.register(new AttachHandler());
        actionDispatcher.register(new DetachHandler());
        actionDispatcher.register(new EndPhaseHandler());
        actionDispatcher.register(new SurrenderHandler());
        actionDispatcher.register(new CombatAdjustHandler());
        actionDispatcher.register(new AttackSequenceHandler());
        actionDispatcher.register(new CombatResponseHandler());
        actionDispatcher.register(new ResponseSummonHandler(effectResolver));
        actionDispatcher.register(new InterceptHandler());
    }

    /** 开始对局：设置状态为进行中，进入第一回合的回合开始阶段（303.2.a.1）。 */
    public void startGame() {
        state.setStatus(GameStatus.IN_PROGRESS);
        state.setTurnCount(1);
        enterPhase(PhaseType.TURN_START);
    }

    /** 进入指定阶段并执行该阶段的进入逻辑。 */
    public void enterPhase(PhaseType phase) {
        state.setCurrentPhase(phase);
        PhaseHandler handler = handlers.get(phase);
        handler.onEnter(state, this);
    }

    /**
     * 推进到下一阶段（不可回退，303.2.a）。
     *
     * <p>由当前阶段的 handler 在完成本阶段逻辑后调用。
     */
    public void advancePhase() {
        PhaseType next = state.getCurrentPhase().next();
        enterPhase(next);
    }

    /**
     * 分发玩家操作：先 validate 再 execute；END_PHASE 成功后 {@link #advancePhase()}； 成功后调用 {@link
     * WinChecker#check}。
     */
    public ActionResult dispatch(ActionRequest request) {
        if (request.getGameId() == null) {
            request.setGameId(state.getGameId());
        }
        ActionResult result = actionDispatcher.dispatch(state, request);

        // END_PHASE：由引擎推进阶段，触发 onEnter（CombatHandler / ResponseHandler 等）
        if (result.isSuccess()
                && result.isPhaseAdvanced()
                && request.getType() == ActionType.END_PHASE) {
            advancePhase();
        }

        if (!result.isGameEnded() && state.getStatus() == GameStatus.IN_PROGRESS) {
            String winnerId = WinChecker.check(state);
            if (winnerId != null) {
                result.setGameEnded(true);
                result.setWinnerId(winnerId);
            }
        }
        return result;
    }

    /**
     * 结束行动阶段（303.2.a.3）。
     *
     * <p>薄封装：构造 END_PHASE 请求走 {@link #dispatch}。
     */
    public void endActionPhase() {
        if (state.getCurrentPhase() != PhaseType.ACTION) {
            throw new IllegalStateException("当前不在行动阶段: " + state.getCurrentPhase());
        }
        ActionRequest request = new ActionRequest();
        request.setPlayerId(state.getActivePlayer().getPlayerId());
        request.setType(ActionType.END_PHASE);
        dispatch(request);
    }

    /**
     * 结束对局（胜负判定或认输）。
     *
     * @param winnerId 胜利者玩家 ID
     */
    public void endGame(String winnerId) {
        state.setStatus(GameStatus.FINISHED);
        state.setWinnerId(winnerId);
    }
}
