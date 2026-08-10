package com.aris.mtcg.controller.system;

import com.aris.mtcg.common.result.Result;
import com.aris.mtcg.domain.vo.HealthVO;
import com.aris.mtcg.service.HealthService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查接口
 *
 * @author pengYuJun
 */
@RestController
@RequestMapping("/health")
public class HealthController {

    @Resource private HealthService healthService;

    /** 健康检查 */
    @GetMapping
    public Result<HealthVO> health() {
        return Result.success(healthService.getHealthInfo());
    }
}
