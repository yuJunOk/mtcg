package com.aris.mtcg.common.enums;

import lombok.Getter;

/**
 * 常见卡牌特征目录（规则 201.9）。
 *
 * <p>{@code mtcg_card.traits} 存中文印刷标签（斜杠分隔）。本枚举供筛选与效果匹配引用，不强制校验录入；程序侧用枚举常量（如 {@code
 * EnumTrait.HUMAN}），展示/匹配用 {@link #desc}。
 *
 * @author pengYuJun
 */
@Getter
public enum EnumTrait {
    HUMAN("人类"),
    AVENGERS("复仇者联盟"),
    MACHINE("机械"),
    ASGARD("阿斯加德"),
    WAKANDA("瓦坎达"),
    FANTASTIC_FOUR("神奇四侠"),
    MUTANT("变种人"),
    DEFENDERS("捍卫者联盟"),
    GUARDIANS("银河护卫队"),
    TIME_CRIMINAL("时间犯"),
    SHIELD("神盾局"),
    HYDRA("九头蛇"),
    XANDAR("赞恩拉"),
    ATLANTIS("亚特兰蒂斯"),
    KAMAR_TAJ("卡玛泰姬"),
    ARENA("斗界");

    /** 中文印刷标签（与存库一致） */
    private final String desc;

    EnumTrait(String desc) {
        this.desc = desc;
    }

    /** 按中文印刷标签解析；未知返回 null */
    public static EnumTrait ofDesc(String desc) {
        if (desc == null) {
            return null;
        }
        String trimmed = desc.trim();
        for (EnumTrait t : values()) {
            if (t.desc.equals(trimmed)) {
                return t;
            }
        }
        return null;
    }
}
