package com.aris.mtcg.service.impl;

import com.aris.mtcg.dao.AuditLogMapper;
import com.aris.mtcg.dao.CardMapper;
import com.aris.mtcg.dao.ProductMapper;
import com.aris.mtcg.dao.UserMapper;
import com.aris.mtcg.domain.entity.AuditLogDO;
import com.aris.mtcg.domain.vo.AuditLogVO;
import com.aris.mtcg.domain.vo.DashboardStatsVO;
import com.aris.mtcg.service.DashboardService;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * 仪表盘服务实现
 *
 * @author pengYuJun
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    @Resource private UserMapper userMapper;

    @Resource private CardMapper cardMapper;

    @Resource private ProductMapper productMapper;

    @Resource private AuditLogMapper auditLogMapper;

    @Override
    public DashboardStatsVO getStats() {
        DashboardStatsVO vo = new DashboardStatsVO();
        vo.setUserCount(userMapper.selectCountByQuery(QueryWrapper.create()));
        vo.setCardCount(cardMapper.selectCountByQuery(QueryWrapper.create()));
        vo.setProductCount(productMapper.selectCountByQuery(QueryWrapper.create()));
        vo.setTodayBattleCount(0L);
        return vo;
    }

    @Override
    public List<AuditLogVO> listRecentActivities(int limit) {
        int safeLimit = limit < 1 ? 20 : Math.min(limit, 100);
        List<AuditLogDO> logs =
                auditLogMapper.selectListByQuery(
                        QueryWrapper.create().orderBy("create_time", false).limit(safeLimit));
        return logs.stream().map(AuditLogVO::fromDO).collect(Collectors.toList());
    }
}
