package com.aris.mtcg.domain.vo;

import lombok.Data;

/**
 * 复盘流水单条（FR4.5）
 *
 * @author pengYuJun
 */
@Data
public class ActionReplayEntryVO {

    /** 操作序号 */
    private Long seq;

    private Integer turnCount;

    private String phase;

    private String playerId;

    private String actionType;

    /** 操作详情 JSON 字符串（前端按 actionType 解析） */
    private String actionDetail;

    private Long timestamp;
}
