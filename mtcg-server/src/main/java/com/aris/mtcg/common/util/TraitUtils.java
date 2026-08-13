package com.aris.mtcg.common.util;

import com.aris.mtcg.common.enums.EnumTrait;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

/**
 * 卡牌特征（traits）拆分 / 拼接 / 匹配工具。
 *
 * <p>存储规范：中文标签、斜杠分隔、无首尾空格，如 {@code 人类/复仇者联盟}；兼容历史逗号分隔。
 *
 * @author pengYuJun
 */
public final class TraitUtils {

    private TraitUtils() {}

    /** 拆分为中文标签列表（空串 → 空列表） */
    public static List<String> split(String traits) {
        if (StringUtils.isBlank(traits)) {
            return Collections.emptyList();
        }
        String normalized = traits.replace('，', ',').replace(',', '/');
        return Arrays.stream(normalized.split("/"))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
    }

    /** 拼接为存储格式（斜杠、无空格）；空列表返回空串便于更新清空 */
    public static String join(List<String> labels) {
        if (labels == null || labels.isEmpty()) {
            return "";
        }
        return labels.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining("/"));
    }

    /** 规范化录入：拆分后去空再拼接 */
    public static String normalize(String traits) {
        return join(split(traits));
    }

    /** 是否含有指定印刷标签（精确成员匹配，规则 301.36） */
    public static boolean hasTrait(List<String> traitLabels, String label) {
        if (traitLabels == null || traitLabels.isEmpty() || StringUtils.isBlank(label)) {
            return false;
        }
        String target = label.trim();
        for (String t : traitLabels) {
            if (target.equals(t)) {
                return true;
            }
        }
        return false;
    }

    /** 是否含有枚举特征 */
    public static boolean hasTrait(List<String> traitLabels, EnumTrait trait) {
        return trait != null && hasTrait(traitLabels, trait.getDesc());
    }

    /** 从存储串解析已知枚举（未知标签跳过） */
    public static List<EnumTrait> parseKnown(String traits) {
        List<EnumTrait> result = new ArrayList<>();
        for (String label : split(traits)) {
            EnumTrait t = EnumTrait.ofDesc(label);
            if (t != null) {
                result.add(t);
            }
        }
        return result;
    }
}
