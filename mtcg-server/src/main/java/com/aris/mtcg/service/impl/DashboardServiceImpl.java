package com.aris.mtcg.service.impl;

import com.aris.mtcg.dao.CardMapper;
import com.aris.mtcg.dao.ProductMapper;
import com.aris.mtcg.dao.UserMapper;
import com.aris.mtcg.domain.vo.DashboardStatsVO;
import com.aris.mtcg.service.DashboardService;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 仪表盘服务实现
 *
 * @author pengYuJun
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private CardMapper cardMapper;

    @Resource
    private ProductMapper productMapper;

    @Override
    public DashboardStatsVO getStats() {
        DashboardStatsVO vo = new DashboardStatsVO();
        vo.setUserCount(userMapper.selectCountByQuery(QueryWrapper.create()));
        vo.setCardCount(cardMapper.selectCountByQuery(QueryWrapper.create()));
        vo.setProductCount(productMapper.selectCountByQuery(QueryWrapper.create()));
        vo.setTodayBattleCount(0L);
        return vo;
    }
}
