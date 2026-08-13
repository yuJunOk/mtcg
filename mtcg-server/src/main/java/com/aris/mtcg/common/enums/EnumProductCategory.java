package com.aris.mtcg.common.enums;

import lombok.Getter;

/**
 * 产品分类枚举（对标官网：基础卡组 / 补充包 / 其他）
 *
 * @author pengYuJun
 */
@Getter
public enum EnumProductCategory {
    STARTER("STARTER", "基础卡组"),
    BOOSTER("BOOSTER", "补充包"),
    OTHER("OTHER", "其他");

    private final String code;

    private final String desc;

    EnumProductCategory(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static EnumProductCategory of(String code) {
        if (code == null) {
            return null;
        }
        for (EnumProductCategory c : values()) {
            if (c.code.equals(code)) {
                return c;
            }
        }
        return null;
    }

    /** 由产品编号推断分类：SD→基础卡组，BP→补充包，其余→其他。 用于存量数据回填或创建时未传分类的默认值。 */
    public static EnumProductCategory inferFromProductCode(String productCode) {
        if (productCode == null) {
            return OTHER;
        }
        String code = productCode.trim().toUpperCase();
        if (code.startsWith("SD")) {
            return STARTER;
        }
        if (code.startsWith("BP")) {
            return BOOSTER;
        }
        return OTHER;
    }
}
