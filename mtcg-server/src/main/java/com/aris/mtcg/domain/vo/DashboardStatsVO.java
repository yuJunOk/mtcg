package com.aris.mtcg.domain.vo;

import lombok.Data;

/**
 * 仪表盘统计信息
 *
 * @author pengYuJun
 */
@Data
public class DashboardStatsVO {

    /** 卡牌总数 */
    private long cardCount;

    /** 产品总数 */
    private long productCount;

    /** 用户总数 */
    private long userCount;

    /** 今日对局数（迭代五才有对战表，暂返回 0） */
    private long todayBattleCount;
}
