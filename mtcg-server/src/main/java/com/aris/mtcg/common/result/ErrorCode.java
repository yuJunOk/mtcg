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

    // === 用户/认证领域（1001-1099）===

    /**
     * 用户名已存在
     */
    USERNAME_DUPLICATE(1001, "用户名已存在"),

    /**
     * 用户名或密码错误
     */
    PASSWORD_INCORRECT(1002, "用户名或密码错误"),

    /**
     * 账号已被禁用
     */
    USER_DISABLED(1003, "账号已被禁用"),

    /**
     * 用户不存在
     */
    USER_NOT_FOUND(1004, "用户不存在"),

    /**
     * 原密码错误
     */
    OLD_PASSWORD_INCORRECT(1005, "原密码错误"),

    /**
     * 不能禁用/删除自己的账号
     */
    CANNOT_MODIFY_SELF(1006, "不能禁用/删除自己的账号"),

    /**
     * 禁止删除系统管理员账号
     */
    CANNOT_DELETE_SYSADMIN(1007, "禁止删除系统管理员账号"),

    /**
     * 禁止禁用系统管理员账号
     */
    CANNOT_DISABLE_SYSADMIN(1008, "禁止禁用系统管理员账号"),

    // === 规则引擎领域（2001-2999，预留）===

    /**
     * 规则引擎执行失败（预留）
     */
    RULE_ENGINE_ERROR(2001, "规则引擎执行失败"),

    /**
     * 非法游戏操作（预留）
     */
    ILLEGAL_GAME_ACTION(2002, "非法游戏操作"),

    // === 卡牌/产品领域（3001-3099）===

    /**
     * 卡牌不存在
     */
    CARD_NOT_FOUND(3001, "卡牌不存在"),

    /**
     * 卡牌编号已存在
     */
    CARD_CODE_DUPLICATE(3002, "卡牌编号已存在"),

    /**
     * 产品不存在
     */
    PRODUCT_NOT_FOUND(3003, "产品不存在"),

    /**
     * 产品编号已存在
     */
    PRODUCT_CODE_DUPLICATE(3004, "产品编号已存在");

    private final int code;

    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
