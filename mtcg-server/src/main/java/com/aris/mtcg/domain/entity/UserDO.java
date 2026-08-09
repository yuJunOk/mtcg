package com.aris.mtcg.domain.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户数据对象，与 mtcg_user 表一一对应
 * <p>
 * 注意：user 是 PostgreSQL 保留字，表名须加双引号。
 *
 * @author pengYuJun
 */
@Data
@Table("mtcg_user")
public class UserDO {

    /**
     * 主键
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 用户名（唯一）
     */
    private String username;

    /**
     * 密码哈希（BCrypt，长度 60）
     */
    private String passwordHash;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像路径
     */
    private String avatar;

    /**
     * 角色，见 {@link com.aris.mtcg.common.enums.EnumUserRole}
     */
    private String role;

    /**
     * 状态，见 {@link com.aris.mtcg.common.enums.EnumUserStatus}
     */
    private String status;

    /**
     * 创建时间（插入时由数据库 NOW() 自动填充）
     */
    @Column(onInsertValue = "NOW()")
    private LocalDateTime createTime;

    /**
     * 更新时间（插入/更新时由数据库 NOW() 自动填充）
     */
    @Column(onInsertValue = "NOW()", onUpdateValue = "NOW()")
    private LocalDateTime updateTime;
}
