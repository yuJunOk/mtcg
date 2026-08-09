package com.aris.mtcg.domain.dto;

import lombok.Data;

/**
 * 用户分页查询入参
 *
 * @author pengYuJun
 */
@Data
public class UserQueryDTO {

    /**
     * 用户名（模糊匹配）
     */
    private String username;

    /**
     * 角色
     */
    private String role;

    /**
     * 状态
     */
    private String status;

    /**
     * 当前页码，默认 1
     */
    private Integer page = 1;

    /**
     * 每页条数，默认 20
     */
    private Integer size = 20;
}
