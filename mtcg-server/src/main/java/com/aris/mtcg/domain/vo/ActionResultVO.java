package com.aris.mtcg.domain.vo;

import lombok.Data;

/**
 * 执行操作结果视图
 *
 * @author pengYuJun
 */
@Data
public class ActionResultVO {

    private Boolean success;

    /** 失败原因或附加说明 */
    private String message;

    /** 是否触发阶段推进 */
    private Boolean phaseAdvanced;

    /** 是否触发对局结束 */
    private Boolean gameEnded;

    /** 对局结束时胜方 */
    private String winner;

    /** 操作后的最新局面（便于前端刷新） */
    private GameStateVO gameState;
}
