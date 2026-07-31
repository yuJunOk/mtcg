package com.aris.mtcg.service;

import com.aris.mtcg.domain.vo.HealthVO;

/**
 * 健康检查服务
 *
 * @author pengYuJun
 */
public interface HealthService {

    /**
     * 获取健康信息
     *
     * @return 健康展示对象
     */
    HealthVO getHealthInfo();
}
