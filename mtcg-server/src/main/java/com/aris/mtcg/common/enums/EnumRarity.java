package com.aris.mtcg.common.enums;

import lombok.Getter;

/**
 * 卡牌稀有度枚举
 *
 * @see <a href="https://mp.weixin.qq.com/s/qWZcy5F4BK7_5rGf2KmWWQ">超英击战综合规则书 v1.01 - 201.14</a>
 * @author pengYuJun
 */
@Getter
public enum EnumRarity {
    R("R", "稀有"),
    SR("SR", "超稀有"),
    GR("GR", "极稀有"),
    UR("UR", "终极稀有"),
    MR("MR", "一级异画"),
    SEC("SEC", "二级异画"),
    HR("HR", "英雄异画"),
    LR("LR", "传奇异画"),
    PR("PR", "推广稀有"),
    ER("ER", "赛事稀有"),
    TR("TR", "宝藏稀有"),
    C("C", "普通");

    private final String code;

    private final String desc;

    EnumRarity(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static EnumRarity of(String code) {
        if (code == null) {
            return null;
        }
        for (EnumRarity r : values()) {
            if (r.code.equals(code)) {
                return r;
            }
        }
        return null;
    }
}
