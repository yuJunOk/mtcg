package com.aris.mtcg.controller.admin;

import com.aris.mtcg.common.annotation.RequireRole;
import com.aris.mtcg.common.constant.SecurityConstant;
import com.aris.mtcg.common.enums.EnumUserRole;
import com.aris.mtcg.common.result.Result;
import com.aris.mtcg.domain.dto.AdminResetPasswordDTO;
import com.aris.mtcg.domain.dto.AdminUserCreateDTO;
import com.aris.mtcg.domain.dto.AdminUserUpdateDTO;
import com.aris.mtcg.domain.dto.UserQueryDTO;
import com.aris.mtcg.domain.vo.PageVO;
import com.aris.mtcg.domain.vo.UserVO;
import com.aris.mtcg.service.UserService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员用户管理接口
 *
 * @author pengYuJun
 */
@RestController
@RequestMapping("/admin/users")
@RequireRole(EnumUserRole.SYS_ADMIN)
public class AdminUserController {

    @Resource private UserService userService;

    /** 分页查询用户列表 */
    @GetMapping
    public Result<PageVO<UserVO>> list(UserQueryDTO query) {
        return Result.success(userService.listUsers(query));
    }

    /** 根据ID查询用户 */
    @GetMapping("/{id}")
    public Result<UserVO> get(@PathVariable Long id) {
        return Result.success(userService.getUserById(id));
    }

    /** 创建用户 */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody AdminUserCreateDTO dto) {
        return Result.success(userService.adminCreateUser(dto));
    }

    /** 更新用户 */
    @PostMapping("/{id}")
    public Result<Void> update(
            @PathVariable Long id,
            @Valid @RequestBody AdminUserUpdateDTO dto,
            @RequestAttribute(SecurityConstant.ATTR_USER_ID) Long currentUserId) {
        userService.adminUpdateUser(id, dto, currentUserId);
        return Result.success();
    }

    /** 更新用户状态 */
    @PostMapping("/{id}/status")
    public Result<Void> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestAttribute(SecurityConstant.ATTR_USER_ID) Long currentUserId) {
        userService.adminUpdateStatus(id, status, currentUserId);
        return Result.success();
    }

    /** 删除用户 */
    @PostMapping("/{id}/delete")
    public Result<Void> delete(
            @PathVariable Long id,
            @RequestAttribute(SecurityConstant.ATTR_USER_ID) Long currentUserId) {
        userService.adminDeleteUser(id, currentUserId);
        return Result.success();
    }

    /** 重置用户密码 */
    @PostMapping("/{id}/password")
    public Result<Void> resetPassword(
            @PathVariable Long id,
            @Valid @RequestBody AdminResetPasswordDTO dto,
            @RequestAttribute(SecurityConstant.ATTR_USER_ID) Long currentUserId) {
        userService.adminResetPassword(id, dto, currentUserId);
        return Result.success();
    }
}
