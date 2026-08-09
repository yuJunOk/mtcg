package com.aris.mtcg.service.impl;

import com.aris.mtcg.common.enums.EnumUserRole;
import com.aris.mtcg.common.enums.EnumUserStatus;
import com.aris.mtcg.common.exception.BusinessException;
import com.aris.mtcg.common.result.ErrorCode;
import com.aris.mtcg.dao.UserMapper;
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
import com.aris.mtcg.manager.JwtManager;
import com.aris.mtcg.service.UserService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现
 * <p>
 * 密码使用 BCrypt 加密；登录失败统一返回"用户名或密码错误"以防止用户名枚举。
 *
 * @author pengYuJun
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private JwtManager jwtManager;

    @Resource
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * 用户注册
     */
    @Override
    public Long register(UserRegisterDTO dto) {
        if (existsByUsername(dto.getUsername())) {
            throw new BusinessException(ErrorCode.USERNAME_DUPLICATE);
        }
        UserDO user = new UserDO();
        user.setUsername(dto.getUsername());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(dto.getNickname());
        user.setRole(EnumUserRole.PLAYER.getCode());
        user.setStatus(EnumUserStatus.ACTIVE.getCode());
        userMapper.insert(user);
        return user.getId();
    }

    /**
     * 用户登录
     * <p>
     * 防枚举：用户不存在与密码错误统一返回"用户名或密码错误"。
     */
    @Override
    public LoginVO login(UserLoginDTO dto) {
        UserDO user = userMapper.selectOneByQuery(
                QueryWrapper.create().eq("username", dto.getUsername(), v -> true));
        if (user == null) {
            throw new BusinessException(ErrorCode.PASSWORD_INCORRECT);
        }
        if (!EnumUserStatus.ACTIVE.getCode().equals(user.getStatus())) {
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.PASSWORD_INCORRECT);
        }
        String token = jwtManager.generateToken(user.getId(), user.getUsername(), user.getRole());
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUser(toVO(user));
        return vo;
    }

    /**
     * 获取当前用户信息
     */
    @Override
    public UserVO getCurrentUserInfo(Long userId) {
        return toVO(loadOrThrow(userId));
    }

    /**
     * 更新当前用户资料
     */
    @Override
    public void updateCurrentUser(Long userId, UserUpdateDTO dto) {
        // 确保用户存在
        loadOrThrow(userId);
        UserDO update = new UserDO();
        update.setId(userId);
        if (dto.getNickname() != null) {
            update.setNickname(dto.getNickname());
        }
        if (dto.getAvatar() != null) {
            update.setAvatar(dto.getAvatar());
        }
        userMapper.update(update);
    }

    /**
     * 修改当前用户密码
     */
    @Override
    public void changePassword(Long userId, ChangePasswordDTO dto) {
        UserDO user = loadOrThrow(userId);
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.OLD_PASSWORD_INCORRECT);
        }
        UserDO update = new UserDO();
        update.setId(userId);
        update.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        userMapper.update(update);
    }

    /**
     * 分页查询用户列表
     */
    @Override
    public PageVO<UserVO> listUsers(UserQueryDTO query) {
        QueryWrapper qw = QueryWrapper.create()
                .like("username", query.getUsername(), StringUtils::isNotBlank)
                .eq("role", query.getRole(), StringUtils::isNotBlank)
                .eq("status", query.getStatus(), StringUtils::isNotBlank);
        int pageNum = (query.getPage() == null || query.getPage() < 1) ? 1 : query.getPage();
        int pageSize = (query.getSize() == null || query.getSize() < 1) ? 20 : query.getSize();
        Page<UserDO> page = userMapper.paginate(Page.of(pageNum, pageSize), qw);
        List<UserVO> records = page.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        return new PageVO<>(records, page.getTotalRow());
    }

    /**
     * 根据 ID 查询用户
     */
    @Override
    public UserVO getUserById(Long id) {
        return toVO(loadOrThrow(id));
    }

    /**
     * 管理员创建用户
     */
    @Override
    public Long adminCreateUser(AdminUserCreateDTO dto) {
        if (existsByUsername(dto.getUsername())) {
            throw new BusinessException(ErrorCode.USERNAME_DUPLICATE);
        }
        if (EnumUserRole.of(dto.getRole()) == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "非法角色");
        }
        UserDO user = new UserDO();
        user.setUsername(dto.getUsername());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(dto.getNickname());
        user.setRole(dto.getRole());
        user.setStatus(EnumUserStatus.ACTIVE.getCode());
        userMapper.insert(user);
        return user.getId();
    }

    /**
     * 管理员更新用户
     * <p>
     * 禁止修改系统管理员的角色。
     */
    @Override
    public void adminUpdateUser(Long id, AdminUserUpdateDTO dto, Long currentUserId) {
        UserDO user = loadOrThrow(id);
        // 禁止修改 SYS_ADMIN 的 role
        if (EnumUserRole.SYS_ADMIN.getCode().equals(user.getRole())
                && dto.getRole() != null
                && !EnumUserRole.SYS_ADMIN.getCode().equals(dto.getRole())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "禁止修改系统管理员的角色");
        }
        UserDO update = new UserDO();
        update.setId(id);
        if (dto.getNickname() != null) {
            update.setNickname(dto.getNickname());
        }
        if (dto.getRole() != null) {
            if (EnumUserRole.of(dto.getRole()) == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "非法角色");
            }
            update.setRole(dto.getRole());
        }
        if (dto.getStatus() != null) {
            if (EnumUserStatus.of(dto.getStatus()) == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "非法状态");
            }
            update.setStatus(dto.getStatus());
        }
        userMapper.update(update);
    }

    /**
     * 管理员更新用户状态
     * <p>
     * 禁止禁用系统管理员账号；禁止禁用自己的账号。
     */
    @Override
    public void adminUpdateStatus(Long id, String status, Long currentUserId) {
        UserDO user = loadOrThrow(id);
        if (EnumUserRole.SYS_ADMIN.getCode().equals(user.getRole())) {
            throw new BusinessException(ErrorCode.CANNOT_DISABLE_SYSADMIN);
        }
        if (id.equals(currentUserId)) {
            throw new BusinessException(ErrorCode.CANNOT_MODIFY_SELF);
        }
        if (EnumUserStatus.of(status) == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "非法状态");
        }
        UserDO update = new UserDO();
        update.setId(id);
        update.setStatus(status);
        userMapper.update(update);
    }

    /**
     * 管理员删除用户
     * <p>
     * 禁止删除系统管理员账号；禁止删除自己的账号。
     */
    @Override
    public void adminDeleteUser(Long id, Long currentUserId) {
        UserDO user = loadOrThrow(id);
        if (EnumUserRole.SYS_ADMIN.getCode().equals(user.getRole())) {
            throw new BusinessException(ErrorCode.CANNOT_DELETE_SYSADMIN);
        }
        if (id.equals(currentUserId)) {
            throw new BusinessException(ErrorCode.CANNOT_MODIFY_SELF);
        }
        userMapper.deleteById(id);
    }

    /**
     * 管理员重置用户密码
     * <p>
     * 禁止重置系统管理员密码；禁止重置自己的密码（应走修改密码流程）。
     */
    @Override
    public void adminResetPassword(Long id, AdminResetPasswordDTO dto, Long currentUserId) {
        UserDO user = loadOrThrow(id);
        if (EnumUserRole.SYS_ADMIN.getCode().equals(user.getRole())) {
            throw new BusinessException(ErrorCode.CANNOT_DISABLE_SYSADMIN);
        }
        if (id.equals(currentUserId)) {
            throw new BusinessException(ErrorCode.CANNOT_MODIFY_SELF);
        }
        UserDO update = new UserDO();
        update.setId(id);
        update.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        userMapper.update(update);
    }

    /**
     * 校验令牌并加载用户
     * <p>
     * 解析失败、用户不存在或被禁用均抛出异常。
     */
    @Override
    public UserDO verifyAndLoad(String token) {
        // 解析令牌（失败时由 JwtManager 抛出 UNAUTHORIZED）
        Long userId = jwtManager.getUserId(token);
        UserDO user = userMapper.selectOneById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Token 无效或已过期");
        }
        if (!EnumUserStatus.ACTIVE.getCode().equals(user.getStatus())) {
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }
        return user;
    }

    // ==================== 私有方法 ====================

    /**
     * DO 转 VO，不暴露密码哈希
     *
     * @param user 用户实体
     * @return 用户展示对象
     */
    private UserVO toVO(UserDO user) {
        if (user == null) {
            return null;
        }
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setRole(user.getRole());
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }

    /**
     * 判断用户名是否已存在
     *
     * @param username 用户名
     * @return true 表示已存在
     */
    private boolean existsByUsername(String username) {
        long count = userMapper.selectCountByQuery(
                QueryWrapper.create().eq("username", username, v -> true));
        return count > 0;
    }

    /**
     * 根据 ID 加载用户，不存在则抛异常
     *
     * @param id 用户 ID
     * @return 用户实体
     */
    private UserDO loadOrThrow(Long id) {
        UserDO user = userMapper.selectOneById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return user;
    }
}
