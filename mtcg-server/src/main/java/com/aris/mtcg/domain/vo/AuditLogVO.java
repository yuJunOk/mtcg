package com.aris.mtcg.domain.vo;

import com.aris.mtcg.domain.entity.AuditLogDO;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 审计日志展示对象
 *
 * @author pengYuJun
 */
@Data
public class AuditLogVO {

    private Long id;

    private String actorUsercode;

    private String action;

    private String resourceType;

    private String resourceId;

    private String detail;

    private LocalDateTime createTime;

    /** DO 转 VO */
    public static AuditLogVO fromDO(AuditLogDO entity) {
        if (entity == null) {
            return null;
        }
        AuditLogVO vo = new AuditLogVO();
        vo.setId(entity.getId());
        vo.setActorUsercode(entity.getActorUsercode());
        vo.setAction(entity.getAction());
        vo.setResourceType(entity.getResourceType());
        vo.setResourceId(entity.getResourceId());
        vo.setDetail(entity.getDetail());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }
}
