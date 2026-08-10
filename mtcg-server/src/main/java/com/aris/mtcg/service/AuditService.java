package com.aris.mtcg.service;

/**
 * 审计日志服务
 *
 * @author pengYuJun
 */
public interface AuditService {

    /**
     * 记录审计日志（操作者从当前请求上下文读取）
     *
     * @param action 动作
     * @param resourceType 资源类型
     * @param resourceId 资源 ID
     * @param detail 详情
     */
    void record(String action, String resourceType, String resourceId, String detail);
}
