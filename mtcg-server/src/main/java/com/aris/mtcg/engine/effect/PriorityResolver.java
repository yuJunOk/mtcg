package com.aris.mtcg.engine.effect;

import java.util.ArrayList;
import java.util.List;

/**
 * 优先级裁定（规则 301.1）。
 *
 * <p>收集所有相关许可（来自规则与效果），取最高层级决定最终能否执行。
 *
 * @author pengYuJun
 */
public class PriorityResolver {

    /** 单条许可：某来源对某动作的裁定。 */
    public static final class Permission {
        public final PriorityLevel level;
        public final String source;

        public Permission(PriorityLevel level, String source) {
            this.level = level;
            this.source = source;
        }
    }

    private final List<Permission> permissions = new ArrayList<>();

    public void add(Permission p) {
        permissions.add(p);
    }

    public void clear() {
        permissions.clear();
    }

    /**
     * 最终裁定：取最高层级，若为 *CANNOT 系列 → false。
     *
     * <p>无许可时默认规则【能】。
     */
    public boolean canDo() {
        if (permissions.isEmpty()) {
            return true;
        }
        Permission top =
                permissions.stream()
                        .max((x, y) -> Integer.compare(x.level.getRank(), y.level.getRank()))
                        .orElseThrow();
        return top.level != PriorityLevel.EFFECT_CANNOT && top.level != PriorityLevel.RULE_CANNOT;
    }
}
