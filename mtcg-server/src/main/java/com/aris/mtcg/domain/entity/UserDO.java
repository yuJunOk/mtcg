package com.aris.mtcg.domain.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 用户数据对象，与 mtcg_user 表一一对应
 *
 * @author pengYuJun
 */
@Data
@Table("mtcg_user")
public class UserDO {

    /** 主键 */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 玩家编号（唯一，登录凭证，不可改） */
    private String usercode;

    /** 密码哈希（BCrypt，长度 60） */
    private String passwordHash;

    /** 用户名（展示名） */
    private String username;

    /** 头像路径 */
    private String avatar;

    /** 角色，见 {@link com.aris.mtcg.common.enums.EnumUserRole} */
    private String role;

    /** 状态，见 {@link com.aris.mtcg.common.enums.EnumUserStatus} */
    private String status;

    /** 创建时间（插入时由数据库 NOW() 自动填充） */
    @Column(onInsertValue = "NOW()")
    private LocalDateTime createTime;

    /** 更新时间（插入/更新时由数据库 NOW() 自动填充） */
    @Column(onInsertValue = "NOW()", onUpdateValue = "NOW()")
    private LocalDateTime updateTime;
}
