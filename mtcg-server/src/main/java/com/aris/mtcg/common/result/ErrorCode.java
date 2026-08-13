package com.aris.mtcg.common.result;

import lombok.Getter;

/**
 * 统一错误码枚举
 *
 * <p>约定：0 表示成功，4xx 表示客户端问题，5xx 表示服务端问题， 业务错误码建议从 1000 起按领域段划分。
 *
 * @author pengYuJun
 */
@Getter
public enum ErrorCode {

    /** 操作成功 */
    SUCCESS(0, "操作成功"),

    /** 请求参数错误 */
    PARAMS_ERROR(400, "请求参数错误"),

    /** 未登录 */
    UNAUTHORIZED(401, "未登录"),

    /** 无操作权限 */
    FORBIDDEN(403, "无操作权限"),

    /** 资源不存在 */
    NOT_FOUND(404, "资源不存在"),

    /** 请求过于频繁 */
    TOO_MANY_REQUESTS(429, "请求过于频繁"),

    /** 系统内部错误 */
    SYSTEM_ERROR(500, "系统内部错误"),

    /** 数据库操作错误 */
    DB_ERROR(5001, "数据库操作失败"),

    // === 用户/认证领域（1001-1099）===

    /** 玩家编号已存在（仅作数据库唯一约束兜底，新用户 usercode 由系统自动生成不会触发） */
    USERCODE_DUPLICATE(1001, "玩家编号已存在"),

    /** 玩家编号或密码错误 */
    PASSWORD_INCORRECT(1002, "玩家编号或密码错误"),

    /** 账号已被禁用 */
    USER_DISABLED(1003, "账号已被禁用"),

    /** 用户不存在 */
    USER_NOT_FOUND(1004, "用户不存在"),

    /** 原密码错误 */
    OLD_PASSWORD_INCORRECT(1005, "原密码错误"),

    /** 不能禁用/删除自己的账号 */
    CANNOT_MODIFY_SELF(1006, "不能禁用/删除自己的账号"),

    /** 禁止删除系统管理员账号 */
    CANNOT_DELETE_SYSADMIN(1007, "禁止删除系统管理员账号"),

    /** 禁止禁用系统管理员账号 */
    CANNOT_DISABLE_SYSADMIN(1008, "禁止禁用系统管理员账号"),

    /** 禁止重置系统管理员密码 */
    CANNOT_RESET_SYSADMIN(1009, "禁止重置系统管理员密码"),

    // === 规则引擎领域（2001-2999，预留）===

    /** 规则引擎执行失败（预留） */
    RULE_ENGINE_ERROR(2001, "规则引擎执行失败"),

    /** 非法游戏操作（预留） */
    ILLEGAL_GAME_ACTION(2002, "非法游戏操作"),

    // === 卡牌/产品领域（3001-3099）===

    /** 卡牌不存在 */
    CARD_NOT_FOUND(3001, "卡牌不存在"),

    /** 卡牌编号已存在 */
    CARD_CODE_DUPLICATE(3002, "卡牌编号已存在"),

    /** 卡牌特征不存在 */
    CARD_FEATURE_NOT_FOUND(3005, "卡牌特征不存在"),

    /** 卡牌特征编码已存在 */
    CARD_FEATURE_CODE_DUPLICATE(3006, "卡牌特征编码已存在"),

    /** 产品不存在 */
    PRODUCT_NOT_FOUND(3003, "产品不存在"),

    /** 产品编号已存在 */
    PRODUCT_CODE_DUPLICATE(3004, "产品编号已存在"),

    // === 卡组/收藏领域（4001-4099）===

    /** 卡组不存在 */
    DECK_NOT_FOUND(4001, "卡组不存在"),

    /** 无权操作该卡组 */
    DECK_FORBIDDEN(4002, "无权操作该卡组"),

    /** 收藏记录不存在 */
    COLLECTION_NOT_FOUND(4003, "收藏记录不存在"),

    /** 卡牌编号不存在 */
    CARD_CODE_NOT_EXIST(4004, "卡牌编号不存在"),

    // === 对局领域（5101-5199）===

    /** 对局不存在 */
    GAME_NOT_FOUND(5101, "对局不存在"),

    /** 非对局参与方 */
    NOT_GAME_PARTICIPANT(5102, "非对局参与方"),

    /** 对局已结束 */
    GAME_ALREADY_FINISHED(5103, "对局已结束"),

    /** 当前不是你的操作回合 */
    NOT_YOUR_TURN(5104, "当前不是你的操作回合"),

    /** 卡组未通过合法性校验 */
    DECK_INVALID(5105, "卡组未通过合法性校验"),

    /** AI 对战尚未交付 */
    AI_NOT_AVAILABLE(5106, "AI 对战尚未开放"),

    /** 房间无法加入（已开局 / 已取消 / 自己的房间） */
    GAME_NOT_JOINABLE(5107, "对局无法加入");

    private final int code;

    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
