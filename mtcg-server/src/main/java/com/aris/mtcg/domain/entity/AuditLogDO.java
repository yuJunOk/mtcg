package com.aris.mtcg.domain.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 审计日志数据对象，与 mtcg_audit_log 表一一对应
 *
 * @author pengYuJun
 */
@Data
@Table("mtcg_audit_log")
public class AuditLogDO {

    /** 主键 */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 操作者用户 ID */
    private Long actorId;

    /** 操作者玩家编号 */
    private String actorUsercode;

    /** 动作（CREATE / UPDATE / DELETE / STATUS 等） */
    private String action;

    /** 资源类型（USER / CARD / PRODUCT / CARD_FEATURE 等） */
    private String resourceType;

    /** 资源 ID */
    private String resourceId;

    /** 详情说明 */
    private String detail;

    /** 创建时间 */
    @Column(onInsertValue = "NOW()")
    private LocalDateTime createTime;
}
