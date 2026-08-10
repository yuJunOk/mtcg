package com.aris.mtcg.service;

import com.aris.mtcg.domain.vo.AuditLogVO;
import com.aris.mtcg.domain.vo.DashboardStatsVO;
import java.util.List;

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

    /**
     * 查询最近审计活动
     *
     * @param limit 条数上限
     * @return 审计日志列表
     */
    List<AuditLogVO> listRecentActivities(int limit);
}
