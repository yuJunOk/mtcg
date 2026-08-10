package com.aris.mtcg.domain.vo;

import com.aris.mtcg.domain.entity.UserDO;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 用户展示对象
 *
 * <p>不暴露密码哈希等敏感字段。
 *
 * @author pengYuJun
 */
@Data
public class UserVO {

    private Long id;

    private String usercode;

    private String username;

    private String avatar;

    private String role;

    private String status;

    private LocalDateTime createTime;

    /** 从 DO 转换 */
    public static UserVO fromDO(UserDO user) {
        if (user == null) {
            return null;
        }
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsercode(user.getUsercode());
        vo.setUsername(user.getUsername());
        vo.setAvatar(user.getAvatar());
        vo.setRole(user.getRole());
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }
}
