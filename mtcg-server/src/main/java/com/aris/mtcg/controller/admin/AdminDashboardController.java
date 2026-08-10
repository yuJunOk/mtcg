package com.aris.mtcg.controller.admin;

import com.aris.mtcg.common.annotation.RequireRole;
import com.aris.mtcg.common.enums.EnumUserRole;
import com.aris.mtcg.common.result.Result;
import com.aris.mtcg.domain.vo.AuditLogVO;
import com.aris.mtcg.domain.vo.DashboardStatsVO;
import com.aris.mtcg.service.DashboardService;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员仪表盘接口
 *
 * @author pengYuJun
 */
@RestController
@RequestMapping("/admin/dashboard")
@RequireRole({EnumUserRole.CARD_ADMIN, EnumUserRole.SYS_ADMIN})
public class AdminDashboardController {

    @Resource private DashboardService dashboardService;

    /** 获取仪表盘统计 */
    @GetMapping("/stats")
    public Result<DashboardStatsVO> stats() {
        return Result.success(dashboardService.getStats());
    }

    /** 最近操作活动 */
    @GetMapping("/activities")
    public Result<List<AuditLogVO>> activities(
            @RequestParam(required = false, defaultValue = "20") Integer limit) {
        return Result.success(dashboardService.listRecentActivities(limit));
    }
}
