package com.aris.mtcg.common.result;

import lombok.Getter;

/**
 * 统一错误码枚举
 * <p>
 * 约定：0 表示成功，4xx 表示客户端问题，5xx 表示服务端问题，
 * 业务错误码建议从 1000 起按领域段划分。
 *
 * @author pengYuJun
 */
@Getter
public enum ErrorCode {

    /**
     * 操作成功
     */
    SUCCESS(0, "操作成功"),

    /**
     * 请求参数错误
     */
    PARAMS_ERROR(400, "请求参数错误"),

    /**
     * 未登录
     */
    UNAUTHORIZED(401, "未登录"),

    /**
     * 无操作权限
     */
    FORBIDDEN(403, "无操作权限"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 系统内部错误
     */
    SYSTEM_ERROR(500, "系统内部错误"),

    /**
     * 规则引擎执行失败（预留）
     */
    RULE_ENGINE_ERROR(1001, "规则引擎执行失败"),

    /**
     * 非法游戏操作（预留）
     */
    ILLEGAL_GAME_ACTION(1002, "非法游戏操作");

    private final int code;

    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
