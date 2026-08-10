package com.aris.mtcg.common.constant;

/**
 * 用户领域常量
 *
 * @author pengYuJun
 */
public final class UserConstant {

    private UserConstant() {}

    /** 玩家编号生成基数：usercode = USERCODE_BASE + 自增 ID */
    public static final long USERCODE_BASE = 100_000L;
}
