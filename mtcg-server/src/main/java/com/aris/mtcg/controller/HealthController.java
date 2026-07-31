package com.aris.mtcg.controller;

import com.aris.mtcg.common.result.Result;
import com.aris.mtcg.domain.vo.HealthVO;
import com.aris.mtcg.service.HealthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查接口
 *
 * @author pengYuJun
 */
@Tag(name = "系统健康")
@RestController
@RequestMapping("/health")
public class HealthController {

    @Resource
    private HealthService healthService;

    /**
     * 探活接口
     *
     * @return 健康状态
     */
    @Operation(summary = "健康检查")
    @GetMapping
    public Result<HealthVO> health() {
        return Result.success(healthService.getHealthInfo());
    }
}
