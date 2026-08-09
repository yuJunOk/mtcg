package com.aris.mtcg.service;

import com.aris.mtcg.domain.vo.DashboardStatsVO;

/**
 * 仪表盘服务
 *
 * @author pengYuJun
 */
public interface DashboardService {

    /**
     * 获取仪表盘统计数据
     *
     * @return 统计数据 VO
     */
    DashboardStatsVO getStats();
}
