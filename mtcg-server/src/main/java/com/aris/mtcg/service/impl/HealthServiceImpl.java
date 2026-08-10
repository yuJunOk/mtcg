package com.aris.mtcg.service.impl;

import com.aris.mtcg.domain.vo.HealthVO;
import com.aris.mtcg.service.HealthService;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 健康检查服务实现
 *
 * @author pengYuJun
 */
@Service
public class HealthServiceImpl implements HealthService {

    @Value("${spring.application.name}")
    private String applicationName;

    @Override
    public HealthVO getHealthInfo() {
        HealthVO healthVO = new HealthVO();
        healthVO.setApplication(applicationName);
        healthVO.setStatus("UP");
        healthVO.setServerTime(LocalDateTime.now());
        return healthVO;
    }
}
