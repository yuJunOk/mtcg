package com.aris.mtcg.service;

import com.aris.mtcg.domain.dto.AdminResetPasswordDTO;
import com.aris.mtcg.domain.dto.AdminUserCreateDTO;
import com.aris.mtcg.domain.dto.AdminUserUpdateDTO;
import com.aris.mtcg.domain.dto.ChangePasswordDTO;
import com.aris.mtcg.domain.dto.UserLoginDTO;
import com.aris.mtcg.domain.dto.UserQueryDTO;
import com.aris.mtcg.domain.dto.UserRegisterDTO;
import com.aris.mtcg.domain.dto.UserUpdateDTO;
import com.aris.mtcg.domain.entity.UserDO;
import com.aris.mtcg.domain.vo.LoginVO;
import com.aris.mtcg.domain.vo.PageVO;
import com.aris.mtcg.domain.vo.UserVO;

/**
 * 用户服务
 *
 * <p>涵盖注册、登录、个人资料维护以及管理员用户管理能力。
 *
 * @author pengYuJun
 */
public interface UserService {

    /**
     * 用户注册
     *
     * @param dto 注册入参
     * @return 新用户 ID
     */
    Long register(UserRegisterDTO dto);

    /**
     * 用户登录
     *
     * @param dto 登录入参
     * @return 登录响应（含访问令牌、刷新令牌与用户信息）
     */
    LoginVO login(UserLoginDTO dto);

    /**
     * 刷新访问令牌
     *
     * @param refreshToken 刷新令牌
     * @return 新的登录响应
     */
    LoginVO refresh(String refreshToken);

    /**
     * 登出（将当前访问令牌加入黑名单）
     *
     * @param accessToken 访问令牌
     */
    void logout(String accessToken);

    /**
     * 获取当前用户信息
     *
     * @param userId 当前用户 ID
     * @return 用户展示对象
     */
    UserVO getCurrentUserInfo(Long userId);

    /**
     * 更新当前用户资料
     *
     * @param userId 当前用户 ID
     * @param dto 资料更新入参
     */
    void updateCurrentUser(Long userId, UserUpdateDTO dto);

    /**
     * 修改当前用户密码
     *
     * @param userId 当前用户 ID
     * @param dto 修改密码入参
     */
    void changePassword(Long userId, ChangePasswordDTO dto);

    /**
     * 分页查询用户列表
     *
     * @param query 查询条件
     * @return 分页结果
     */
    PageVO<UserVO> listUsers(UserQueryDTO query);

    /**
     * 根据 ID 查询用户
     *
     * @param id 用户 ID
     * @return 用户展示对象
     */
    UserVO getUserById(Long id);

    /**
     * 管理员创建用户
     *
     * @param dto 创建入参
     * @return 新用户 ID
     */
    Long adminCreateUser(AdminUserCreateDTO dto);

    /**
     * 管理员更新用户
     *
     * @param id 目标用户 ID
     * @param dto 更新入参
     * @param currentUserId 当前操作者 ID
     */
    void adminUpdateUser(Long id, AdminUserUpdateDTO dto, Long currentUserId);

    /**
     * 管理员更新用户状态
     *
     * @param id 目标用户 ID
     * @param status 新状态
     * @param currentUserId 当前操作者 ID
     */
    void adminUpdateStatus(Long id, String status, Long currentUserId);

    /**
     * 管理员删除用户
     *
     * @param id 目标用户 ID
     * @param currentUserId 当前操作者 ID
     */
    void adminDeleteUser(Long id, Long currentUserId);

    /**
     * 管理员重置用户密码
     *
     * @param id 目标用户 ID
     * @param dto 重置密码入参
     * @param currentUserId 当前操作者 ID
     */
    void adminResetPassword(Long id, AdminResetPasswordDTO dto, Long currentUserId);

    /**
     * 校验令牌并加载用户
     *
     * @param token JWT 令牌
     * @return 用户实体
     */
    UserDO verifyAndLoad(String token);
}
