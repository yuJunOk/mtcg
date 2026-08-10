package com.aris.mtcg.controller.user;

import com.aris.mtcg.common.constant.SecurityConstant;
import com.aris.mtcg.common.result.Result;
import com.aris.mtcg.domain.dto.ChangePasswordDTO;
import com.aris.mtcg.domain.dto.UserUpdateDTO;
import com.aris.mtcg.domain.vo.UserVO;
import com.aris.mtcg.service.UserService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户个人资料接口
 *
 * @author pengYuJun
 */
@RestController
@RequestMapping("/users")
public class UserController {

    @Resource private UserService userService;

    /** 获取当前用户信息 */
    @GetMapping("/me")
    public Result<UserVO> me(@RequestAttribute(SecurityConstant.ATTR_USER_ID) Long userId) {
        return Result.success(userService.getCurrentUserInfo(userId));
    }

    /** 更新当前用户资料 */
    @PostMapping("/me")
    public Result<Void> updateMe(
            @RequestAttribute(SecurityConstant.ATTR_USER_ID) Long userId,
            @Valid @RequestBody UserUpdateDTO dto) {
        userService.updateCurrentUser(userId, dto);
        return Result.success();
    }

    /** 修改当前用户密码 */
    @PostMapping("/me/password")
    public Result<Void> changePassword(
            @RequestAttribute(SecurityConstant.ATTR_USER_ID) Long userId,
            @Valid @RequestBody ChangePasswordDTO dto) {
        userService.changePassword(userId, dto);
        return Result.success();
    }
}
