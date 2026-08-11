package com.aris.mtcg.engine.action;

/**
 * 操作结果。
 *
 * @author pengYuJun
 */
public class ActionResult {

    /** 是否成功 */
    private boolean success;

    /** 失败原因或附加说明 */
    private String message;

    /** 是否触发了阶段推进 */
    private boolean phaseAdvanced;

    /** 是否触发游戏结束 */
    private boolean gameEnded;

    /** 游戏结束时胜者 playerId */
    private String winnerId;

    public ActionResult() {}

    public ActionResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    /** 构造成功结果。 */
    public static ActionResult ok() {
        return new ActionResult(true, null);
    }

    /** 构造成功结果（带说明）。 */
    public static ActionResult ok(String message) {
        return new ActionResult(true, message);
    }

    /** 构造失败结果。 */
    public static ActionResult fail(String message) {
        return new ActionResult(false, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isPhaseAdvanced() {
        return phaseAdvanced;
    }

    public void setPhaseAdvanced(boolean phaseAdvanced) {
        this.phaseAdvanced = phaseAdvanced;
    }

    public boolean isGameEnded() {
        return gameEnded;
    }

    public void setGameEnded(boolean gameEnded) {
        this.gameEnded = gameEnded;
    }

    public String getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(String winnerId) {
        this.winnerId = winnerId;
    }
}
