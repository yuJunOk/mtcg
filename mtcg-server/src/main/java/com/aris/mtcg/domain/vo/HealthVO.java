package com.aris.mtcg.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 健康检查展示对象
 *
 * @author pengYuJun
 */
@Data
public class HealthVO {

    /**
     * 应用名称
     */
    private String application;

    /**
     * 状态
     */
    private String status;

    /**
     * 服务端时间
     */
    private LocalDateTime serverTime;
}
