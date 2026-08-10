package com.aris.mtcg.service.impl;

import com.aris.mtcg.common.constant.UserConstant;
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
import com.aris.mtcg.manager.RateLimitManager;
import com.aris.mtcg.manager.TokenBlacklistManager;
import com.aris.mtcg.service.AuditService;
import com.aris.mtcg.service.UserService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户服务实现
 *
 * <p>密码使用 BCrypt 加密；登录失败统一返回"玩家编号或密码错误"以防止用户名枚举。
 *
 * @author pengYuJun
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource private UserMapper userMapper;

    @Resource private JwtManager jwtManager;

    @Resource private TokenBlacklistManager tokenBlacklistManager;

    @Resource private BCryptPasswordEncoder passwordEncoder;

    @Resource private AuditService auditService;

    @Resource private RateLimitManager rateLimitManager;

    private static final int LOGIN_FAIL_LIMIT = 5;
    private static final long LOGIN_FAIL_WINDOW_MS = 60_000L;

    /** 用户注册（usercode 由系统自动生成） */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long register(UserRegisterDTO dto) {
        UserDO user = new UserDO();
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setUsername(dto.getUsername());
        user.setRole(EnumUserRole.PLAYER.getCode());
        user.setStatus(EnumUserStatus.ACTIVE.getCode());
        userMapper.insert(user);
        user.setUsercode(String.valueOf(UserConstant.USERCODE_BASE + user.getId()));
        userMapper.update(user);
        return user.getId();
    }

    @Override
    public LoginVO login(UserLoginDTO dto) {
        String failKey = "login-fail:" + dto.getUsercode();
        UserDO user =
                userMapper.selectOneByQuery(
                        QueryWrapper.create().eq("usercode", dto.getUsercode(), v -> true));
        if (user == null) {
            recordLoginFailure(failKey);
            throw new BusinessException(ErrorCode.PASSWORD_INCORRECT);
        }
        if (!EnumUserStatus.ACTIVE.getCode().equals(user.getStatus())) {
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            recordLoginFailure(failKey);
            throw new BusinessException(ErrorCode.PASSWORD_INCORRECT);
        }
        return buildLoginVO(user);
    }

    /** 累计鉴权失败；超限则抛出限流异常 */
    private void recordLoginFailure(String failKey) {
        if (!rateLimitManager.tryAcquire(failKey, LOGIN_FAIL_LIMIT, LOGIN_FAIL_WINDOW_MS)) {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUESTS, "登录失败次数过多，请稍后再试");
        }
    }

    @Override
    public LoginVO refresh(String refreshToken) {
        Claims claims = jwtManager.parse(refreshToken);
        if (!JwtManager.TOKEN_TYPE_REFRESH.equals(claims.get("tokenType", String.class))) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "无效的刷新令牌");
        }
        String jti = claims.getId();
        if (tokenBlacklistManager.isBlacklisted(jti)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "刷新令牌已失效");
        }
        Long userId = Long.parseLong(claims.getSubject());
        UserDO user = loadOrThrow(userId);
        if (!EnumUserStatus.ACTIVE.getCode().equals(user.getStatus())) {
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }
        // 轮换：拉黑旧 refresh
        Date expiration = claims.getExpiration();
        if (expiration != null) {
            tokenBlacklistManager.blacklist(jti, expiration.getTime());
        }
        return buildLoginVO(user);
    }

    @Override
    public void logout(String accessToken) {
        Claims claims = jwtManager.parse(accessToken);
        String jti = claims.getId();
        Date expiration = claims.getExpiration();
        long expireAt = expiration != null ? expiration.getTime() : System.currentTimeMillis();
        tokenBlacklistManager.blacklist(jti, expireAt);
    }

    @Override
    public UserVO getCurrentUserInfo(Long userId) {
        return UserVO.fromDO(loadOrThrow(userId));
    }

    @Override
    public void updateCurrentUser(Long userId, UserUpdateDTO dto) {
        loadOrThrow(userId);
        UserDO update = new UserDO();
        update.setId(userId);
        if (dto.getUsername() != null) {
            update.setUsername(dto.getUsername());
        }
        if (dto.getAvatar() != null) {
            update.setAvatar(dto.getAvatar());
        }
        userMapper.update(update);
    }

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

    @Override
    public PageVO<UserVO> listUsers(UserQueryDTO query) {
        QueryWrapper qw =
                QueryWrapper.create()
                        .like("usercode", query.getUsercode(), StringUtils::isNotBlank)
                        .like("username", query.getUsername(), StringUtils::isNotBlank)
                        .eq("role", query.getRole(), StringUtils::isNotBlank)
                        .eq("status", query.getStatus(), StringUtils::isNotBlank);
        int pageNum =
                (query.getPageNum() == null || query.getPageNum() < 1) ? 1 : query.getPageNum();
        int pageSize =
                (query.getPageSize() == null || query.getPageSize() < 1) ? 10 : query.getPageSize();
        Page<UserDO> page = userMapper.paginate(Page.of(pageNum, pageSize), qw);
        List<UserVO> records =
                page.getRecords().stream().map(UserVO::fromDO).collect(Collectors.toList());
        return new PageVO<>(records, page.getTotalRow());
    }

    @Override
    public UserVO getUserById(Long id) {
        return UserVO.fromDO(loadOrThrow(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long adminCreateUser(AdminUserCreateDTO dto) {
        if (EnumUserRole.of(dto.getRole()) == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "非法角色");
        }
        UserDO user = new UserDO();
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setUsername(dto.getUsername());
        user.setRole(dto.getRole());
        user.setStatus(EnumUserStatus.ACTIVE.getCode());
        userMapper.insert(user);
        user.setUsercode(String.valueOf(UserConstant.USERCODE_BASE + user.getId()));
        userMapper.update(user);
        auditService.record(
                "CREATE", "USER", String.valueOf(user.getId()), "创建用户 " + user.getUsercode());
        return user.getId();
    }

    @Override
    public void adminUpdateUser(Long id, AdminUserUpdateDTO dto, Long currentUserId) {
        UserDO user = loadOrThrow(id);
        if (EnumUserRole.SYS_ADMIN.getCode().equals(user.getRole())
                && dto.getRole() != null
                && !EnumUserRole.SYS_ADMIN.getCode().equals(dto.getRole())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "禁止修改系统管理员的角色");
        }
        UserDO update = new UserDO();
        update.setId(id);
        if (dto.getUsername() != null) {
            update.setUsername(dto.getUsername());
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
        auditService.record("UPDATE", "USER", String.valueOf(id), "更新用户");
    }

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
        auditService.record("STATUS", "USER", String.valueOf(id), "状态变更为 " + status);
    }

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
        auditService.record("DELETE", "USER", String.valueOf(id), "删除用户 " + user.getUsercode());
    }

    @Override
    public void adminResetPassword(Long id, AdminResetPasswordDTO dto, Long currentUserId) {
        UserDO user = loadOrThrow(id);
        if (EnumUserRole.SYS_ADMIN.getCode().equals(user.getRole())) {
            throw new BusinessException(ErrorCode.CANNOT_RESET_SYSADMIN);
        }
        if (id.equals(currentUserId)) {
            throw new BusinessException(ErrorCode.CANNOT_MODIFY_SELF);
        }
        UserDO update = new UserDO();
        update.setId(id);
        update.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        userMapper.update(update);
        auditService.record("UPDATE", "USER", String.valueOf(id), "重置密码");
    }

    @Override
    public UserDO verifyAndLoad(String token) {
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

    private LoginVO buildLoginVO(UserDO user) {
        String accessToken =
                jwtManager.generateAccessToken(user.getId(), user.getUsercode(), user.getRole());
        String refreshToken =
                jwtManager.generateRefreshToken(user.getId(), user.getUsercode(), user.getRole());
        LoginVO vo = new LoginVO();
        vo.setToken(accessToken);
        vo.setRefreshToken(refreshToken);
        vo.setUser(UserVO.fromDO(user));
        return vo;
    }

    private UserDO loadOrThrow(Long id) {
        UserDO user = userMapper.selectOneById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return user;
    }
}
