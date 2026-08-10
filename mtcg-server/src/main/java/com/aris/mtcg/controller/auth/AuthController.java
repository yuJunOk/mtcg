package com.aris.mtcg.controller.auth;

import com.aris.mtcg.common.constant.SecurityConstant;
import com.aris.mtcg.common.result.Result;
import com.aris.mtcg.domain.dto.RefreshTokenDTO;
import com.aris.mtcg.domain.dto.UserLoginDTO;
import com.aris.mtcg.domain.dto.UserRegisterDTO;
import com.aris.mtcg.domain.vo.LoginVO;
import com.aris.mtcg.service.UserService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证接口
 *
 * @author pengYuJun
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Resource private UserService userService;

    /** 用户注册 */
    @PostMapping("/register")
    public Result<Long> register(@Valid @RequestBody UserRegisterDTO dto) {
        return Result.success(userService.register(dto));
    }

    /** 用户登录 */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody UserLoginDTO dto) {
        return Result.success(userService.login(dto));
    }

    /** 刷新访问令牌 */
    @PostMapping("/refresh")
    public Result<LoginVO> refresh(@Valid @RequestBody RefreshTokenDTO dto) {
        return Result.success(userService.refresh(dto.getRefreshToken()));
    }

    /** 登出（需登录） */
    @PostMapping("/logout")
    public Result<Void> logout(
            @RequestAttribute(SecurityConstant.ATTR_ACCESS_TOKEN) String accessToken) {
        userService.logout(accessToken);
        return Result.success();
    }
}
