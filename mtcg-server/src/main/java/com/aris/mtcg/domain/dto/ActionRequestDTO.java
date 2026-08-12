package com.aris.mtcg.domain.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import lombok.Data;

/**
 * 执行对局操作入参
 *
 * <p>{@code playerId} 不在 DTO 中，由 Service 根据当前登录用户与对局归属确定。 字段与引擎 {@code ActionRequest} 一一对应（除 gameId
 * / playerId）。
 *
 * @author pengYuJun
 */
@Data
public class ActionRequestDTO {

    /** 操作类型（ActionType 枚举名），如 SUMMON / ATTACK */
    @NotBlank(message = "操作类型不能为空")
    private String actionType;

    /** 主体卡编号，可空 */
    private String cardCode;

    /** 源区域枚举名，可空 */
    private String sourceZone;

    /** 源区域下标（侧翼/基地多格），可空 */
    private Integer sourceIndex;

    /** 目标区域枚举名，可空 */
    private String targetZone;

    /** 目标区域下标，可空 */
    private Integer targetIndex;

    /** 目标卡编号（结附父卡、攻击目标），可空 */
    private String targetCardCode;

    /** 扩展参数（如调整位置互换对、Lv4+ 撤退清单） */
    private Map<String, Object> extras;
}
