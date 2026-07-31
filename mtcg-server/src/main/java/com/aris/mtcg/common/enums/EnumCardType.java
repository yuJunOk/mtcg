package com.aris.mtcg.common.enums;

import lombok.Getter;

/**
 * 卡牌类型枚举（骨架，后续按正式规则扩充）
 *
 * @author pengYuJun
 */
@Getter
public enum EnumCardType {

    /**
     * 英雄
     */
    HERO("HERO", "英雄"),

    /**
     * 随从 / 单位
     */
    UNIT("UNIT", "单位"),

    /**
     * 事件 / 法术
     */
    EVENT("EVENT", "事件"),

    /**
     * 装备 / 道具
     */
    ITEM("ITEM", "道具");

    private final String code;

    private final String desc;

    EnumCardType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
