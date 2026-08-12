package com.aris.mtcg.domain.vo;

import lombok.Data;

/**
 * 个人胜败统计（FR5.4）
 *
 * @author pengYuJun
 */
@Data
public class GameStatsVO {

    /** 已结束对局总数 */
    private Integer totalGames;

    private Integer wins;

    private Integer losses;

    private Integer draws;

    /** 胜率 = wins / totalGames（无对局时为 0） */
    private Double winRate;
}
