package com.aris.mtcg.service.impl;

import com.aris.mtcg.common.constant.SecurityConstant;
import com.aris.mtcg.dao.AuditLogMapper;
import com.aris.mtcg.domain.entity.AuditLogDO;
import com.aris.mtcg.service.AuditService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 审计日志服务实现
 *
 * @author pengYuJun
 */
@Slf4j
@Service
public class AuditServiceImpl implements AuditService {

    @Resource private AuditLogMapper auditLogMapper;

    @Override
    public void record(String action, String resourceType, String resourceId, String detail) {
        AuditLogDO entity = new AuditLogDO();
        entity.setAction(action);
        entity.setResourceType(resourceType);
        entity.setResourceId(resourceId);
        entity.setDetail(detail);

        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            Object userId = request.getAttribute(SecurityConstant.ATTR_USER_ID);
            Object usercode = request.getAttribute(SecurityConstant.ATTR_USERCODE);
            if (userId instanceof Long id) {
                entity.setActorId(id);
            }
            if (usercode instanceof String code) {
                entity.setActorUsercode(code);
            }
        }

        try {
            auditLogMapper.insert(entity);
        } catch (Exception e) {
            log.warn(
                    "写入审计日志失败: action={}, resourceType={}, resourceId={}, err={}",
                    action,
                    resourceType,
                    resourceId,
                    e.getMessage());
        }
    }
}
