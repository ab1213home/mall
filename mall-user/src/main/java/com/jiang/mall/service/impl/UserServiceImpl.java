/*
 * Copyright (c) 2024 Jiang RongJun
 * Jiang Mall is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan
 * PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY
 * KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.jiang.mall.service.impl;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.config.UserConfig;
import com.jiang.mall.dao.GroupMapper;
import com.jiang.mall.dao.UserGroupRelationMapper;
import com.jiang.mall.dao.UserMapper;
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.User;
import com.jiang.mall.domain.entity.UserGroupRelation;
import com.jiang.mall.domain.entity.VerificationCode;
import com.jiang.mall.domain.vo.UserVo;
import com.jiang.mall.service.*;
import com.jiang.mall.util.BeanCopyUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.jiang.mall.util.TimeUtils.getDaysUntilNextBirthday;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jiang
 * @since 2024年9月11日
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	private UserMapper userMapper;

	@Autowired
	public void setUserMapper(UserMapper userMapper) {
	    this.userMapper = userMapper;
	}

	private IUserLogService userRecordService;

    @Autowired
    public void setLoginRecordService(IUserLogService userRecordService) {
        this.userRecordService = userRecordService;
    }

	private II18nService i18nService;

	@Autowired
	public void setI18nService(II18nService i18nService) {
		this.i18nService = i18nService;
	}

	private IUserRedisService  redisService;

    @Autowired
    public void setRedisService(IUserRedisService redisService) {
        this.redisService = redisService;
    }

	private IVerificationCodeService verificationCodeService;

    @Autowired
    public void setVerificationCodeService(IVerificationCodeService verificationCodeService) {
        this.verificationCodeService = verificationCodeService;
    }

	private ITemporaryRedisService temporaryRedisService;

	@Autowired
	public void setTemporaryRedisService(ITemporaryRedisService temporaryRedisService) {
		this.temporaryRedisService = temporaryRedisService;
	}

	private GroupMapper groupMapper;

	@Autowired
	public void setGroupMapper(GroupMapper groupMapper) {
		this.groupMapper = groupMapper;
	}


	private UserGroupRelationMapper userGroupRelationMapper;

	@Autowired
	public void setUserGroupRelationMapper(UserGroupRelationMapper userGroupRelationMapper) {
		this.userGroupRelationMapper = userGroupRelationMapper;
	}

	private UserConfig userConfig;

	@Autowired
	public void setUserConfig(UserConfig userConfig) {
		this.userConfig = userConfig;
	}


	/**
	 * 检查用户是否已登录
	 * <p>
	 * 此方法检查会话中的 "User" 属性以确认用户是否已经登录。
	 * 如果用户未登录，则返回失败的结果；如果已登录，则返回用户对象。
	 *
	 * @param sessionId 当前用户的会话Id
	 * @return 如果用户已登录，返回用户ID；否则返回失败结果
	 */
	@Override
	public ResponseResult<Object> checkUserLogin(String sessionId) {
		UserVo user = redisService.getUserBySessionId(sessionId);
		if (user == null){
			return ResponseResult.notLoggedResult(i18nService.getMessage("user.checkUser.noLogin"));
		}else{
			if (user.getId()==null){
				return ResponseResult.failResult(i18nService.getMessage("user.checkUser.error"));
			}else{
				return ResponseResult.okResult(user);
			}
		}
	}

	@Override
	public UserVo getUserFromRedis(String sessionId) {
		return redisService.getUserBySessionId(sessionId);
	}

	@Override
	public void setUserToRedis(UserVo user) {
		redisService.updateUser(user);
	}

	/**
	 * 用于获取用户数量
	 *
	 * @return 用户数量
	 */
	@Override
	public Long getUserNum() {
	    // 通过userMapper查询所有用户，null参数表示不使用任何条件
	    return userMapper.selectCount(null);
	}

	/**
	 * 检查当前用户是否为管理员
	 * 此方法首先调用checkUserLogin方法验证用户是否已登录
	 * 如果用户未登录，则返回相应的未登录结果
	 * 如果用户已登录但不是管理员，则返回无权限访问的结果
	 * 如果用户已登录且是管理员，则返回成功的验证结果
	 *
	 * @param sessionId 当前用户的会话Id
	 * @return ResponseResult 包含验证结果的对象，包括用户是否已登录和是否有管理员权限
	 */
	@Override
	public ResponseResult<Object> checkAdminUser(String sessionId) {
	    // 检查用户是否已登录
	    ResponseResult<Object> result = checkUserLogin(sessionId);
	    if (!result.isSuccess()) {
	        // 如果未登录，则直接返回
	        return result;
	    }
		UserVo user = (UserVo) result.getData();
		if (user.isAdmin()){
			return ResponseResult.okResult(user);
		}else{
			return ResponseResult.failResult(i18nService.getMessage("user.checkAdmin.noAdmin"));
		}
	}

	/**
	 * 用户登录方法
	 * 通过用户名(邮箱)和密码尝试登录系统。用户密码是经过MD5加密的，以提高安全性。
	 *
	 * @param username    用户名或者邮箱，用于登录验证
	 * @param password    密文密码，用于登录验证
	 * @param clientIp    客户端IP地址
	 * @param fingerprint 浏览器指纹，用于登录验证
	 * @return 如果验证成功，返回对应的ture对象；如果验证失败或用户不存在，返回false
	 */
	@Override
	public Boolean login(String username, String password, String clientIp, String fingerprint,String sessionId) {
		User user = getUserByUserNameOrEmail(username, password);
		if (user == null) {
			// 登录失败，记录登录记录
			userRecordService.failedLoginLog(username, clientIp, fingerprint);
			logger.debug("用户名或密码错误");
			return false;
		} else {
			UserVo userVo = BeanCopyUtils.copyBean(user, UserVo.class);
	        assert userVo != null;
			Set<Long> groupIds = userGroupRelationMapper.selectGroupIdByUserId(user.getId());
			userVo.setGroups(groupIds);
			StringBuilder permissions_str = new StringBuilder();
			for (Long groupId : groupIds) {
				permissions_str.append(groupMapper.selectPermissionByGroupId(groupId)).append(",");
			}
			permissions_str.append(user.getPermission());

			Set<String> permissions = new HashSet<>();
			for (String permission : permissions_str.toString().split(",")) {
				permissions.add(permission);
			}
			userVo.setPermissions(permissions);
	        userVo.setAdmin(!permissions.isEmpty());
			// 设置用户的出生日期，并计算下个生日的天数
            if (user.getBirthDate()!=null){
                userVo.setNextBirthday(getDaysUntilNextBirthday(user.getBirthDate()));
            }
			String token = UUID.randomUUID().toString();
			// 将用户信息存储到Redis中，并设置过期时间
			redisService.setUser(sessionId, token, userVo);
			// 登录成功，记录登录记录
			userRecordService.successLoginLog(user, clientIp, fingerprint);
			logger.debug("用户{}登录成功", user.getUsername());
			//TODO:返回token
			return true;
		}
	}

	public User getUserByUserNameOrEmail(String username, String password) {
	    // 根据查询条件尝试获取用户信息
	    User user_username = userMapper.selectByUsernameAndIsActive(username);

	    // 根据邮箱格式匹配用户
	    if (i18nService.isValidEmail(username)) {
	        // 创建基于邮箱的查询条件
	        User user_email = userMapper.selectByEmailAndIsActive(username);
			//encryptToSHA256(password,AES_SALT)
	        // 判断邮箱是否对应用户
	        if (user_email == null) {
	            // 如果邮箱未注册，检查用户名是否注册
	            if (user_username == null) {
					// 用户名和邮箱均未注册
	                return null;
	            } else if (user_username.getPassword().equals(password)) {
					// 用户名注册且密码匹配
	                return user_username;
	            } else {
					// 用户名注册但密码不匹配
	                return null;
	            }
	        } else {
	            // 邮箱已注册，检查是否与用户名对应同一用户
		        if (user_username==null){
					if (user_email.getPassword().equals(password)){
						return user_email;
					}else {
						return null;
					}
		        }else if (Objects.equals(user_email.getId(), user_username.getId())) {
	                // 同一用户，检查密码
	                if (user_username.getPassword().equals(password)) {
						// 密码匹配
	                    return user_username;
	                } else {
						// 密码不匹配
	                    return null;
	                }
	            } else {
	                // 不是同一用户，分别检查密码
	                if (user_email.getPassword().equals(password)) {
						// 邮箱用户密码匹配
	                    return user_email;
	                } else if (user_username.getPassword().equals(password)) {
						// 用户名用户密码匹配
	                    return user_username;
	                } else {
						// 密码均不匹配
	                    return null;
	                }
	            }
	        }
	    } else {
	        // 如果不是邮箱格式，直接检查用户名是否注册
	        if (user_username == null) {
				// 用户名未注册
	            return null;
	        } else if (user_username.getPassword().equals(password)) {
				// 用户名注册且密码匹配
	            return user_username;
	        } else {
				// 用户名注册但密码不匹配
	            return null;
	        }
	    }
	}

	/**
	 * 根据用户名或电子邮件地址获取用户信息
	 *
	 * @param username 用户名或电子邮件地址
	 * @return 如果找到对应的用户信息，则返回User对象；否则返回null
	 */
	@Override
	public User getUserByUserNameOrEmail(String username) {
	    // 创建查询条件，指定用户名
	    User user_username = userMapper.selectByUsername(username);

	    // 根据邮箱格式匹配用户
	    if (i18nService.isValidEmail(username)) {
	        // 创建基于邮箱的查询条件
	        User user_email = userMapper.selectByEmail(username);

	        // 判断邮箱是否对应用户
	        if (user_email == null) {
	            // 如果邮箱未注册，检查用户名是否注册
	            return user_username;
	        } else {
	            // 邮箱已注册，检查是否与用户名对应同一用户
		        if (user_username==null){
					return user_email;
		        }else if (Objects.equals(user_email.getId(), user_username.getId())) {
	                return user_username;
	            } else {
	                return null;
	            }
	        }
	    } else {
	        // 如果不是邮箱格式，直接检查用户名是否注册
	        return user_username;
	    }
	}

	@Override
	public Boolean logout(String sessionId) {
		return redisService.deleteUserBySessionId(sessionId);
	}

	@Override
	public Boolean validatePassword(Long userId, String password) {
		return userMapper.validatePassword(userId,password)>0;
	}

	@Override
	public Boolean modifyEmail(@NotNull VerificationCode verificationCode, String sessionId, String clientIp, String fingerprint) {
		UserVo userVo = getUserFromRedis(sessionId);
		User user = userMapper.selectUserByIdAndActive(userVo.getId());
		if (userMapper.updateEmail(user.getId(),verificationCode.getEmail())>0){
			// 验证码使用标记
			verificationCodeService.useCode(getUserFromRedis(sessionId).getId(), verificationCode);
			// 记录邮箱修改成功日志
			userRecordService.successModifyEmailLog(user,verificationCode.getEmail(),clientIp,fingerprint);
			userVo.setEmail(verificationCode.getEmail());
			setUserToRedis(userVo);
			return true;
		}else {
			logger.error("修改{}用户邮箱失败", getUserFromRedis(sessionId).getId());
			return false;
		}
	}

	@Override
	public Long register(@NotNull VerificationCode verificationCode, String sessionId, String clientIp, String fingerprint) {
		User user = new User();
		user.setUsername(verificationCode.getUsername());
		user.setPassword(verificationCode.getPassword());
		user.setEmail(verificationCode.getEmail());
		user.setIsActive(true);
		user.setTotpEnabled(false);
		if (userMapper.insert(user) > 0){
			temporaryRedisService.setKey(sessionId, String.valueOf(user.getId()),30, TimeUnit.MINUTES);
			verificationCodeService.useCode(user.getId(), verificationCode);
			userRecordService.successRegisterLog(user, clientIp, fingerprint);
			if (userConfig.getDefaultGroup()!=-1){
				UserGroupRelation userGroupRelation = new UserGroupRelation();
				userGroupRelation.setUserId(user.getId());
				userGroupRelation.setGroupId(userConfig.getDefaultGroup());
				userGroupRelationMapper.insert(userGroupRelation);
			}
			return user.getId();
		}else {
			logger.error("注册{}用户失败", user);
			return null;
		}
	}

	@Override
	public Boolean register(User user, String sessionId) {
		if (userMapper.updateById(user)>0){
			temporaryRedisService.deleteKey(sessionId);
			return true;
		}else {
			logger.error("{}用户信息补充失败", user);
			return false;
		}
	}

	/**
	 * 处理用户忘记密码的情况
	 *
	 * @param verificationCode 验证码对象，用于验证用户身份
	 * @param password 新密码，用户希望设置的新密码
	 * @param clientIp 客户端IP地址，用于记录用户活动
	 * @param fingerprint 用户设备指纹，用于增强安全性
	 * @return 返回一个布尔值，表示密码重置是否成功
	 */
	@Override
	public Boolean forgot(@NotNull VerificationCode verificationCode, String password, String clientIp, String fingerprint) {
	    // 根据查询条件尝试获取用户信息。
	    User user = userMapper.selectById(verificationCode.getUserId());

	    // 验证用户是否存在
	    if (user != null) {
	        // 如果验证成功，更新用户密码为新密码。
	        user.setPassword(password);
	        // 激活用户账户
	        user.setIsActive(true);
	        // 通过ID更新用户信息。
	        if (userMapper.updateById(user) > 0){
	            // 更新验证码对象的密码信息
	            verificationCode.setPassword(password);
	            // 使用验证码，并记录使用信息
	            verificationCodeService.useCode(user.getId(), verificationCode);
	            // 记录用户成功找回密码的日志
	            userRecordService.successForgotLog(user.getId(),clientIp,fingerprint);
	            // 更新成功，返回true
	            return true;
	        }else {
	            // 更新失败，返回null
	            return null;
	        }
	    }else {
	        // 如果用户不存在，返回false。
	        return false;
	    }
	}

	@Override
	public Boolean lock(String sessionId, String clientIp, String fingerprint) {
		UserVo user = getUserFromRedis(sessionId);

		if(userMapper.lockById(user.getId(),user.getId())>0) {
			userRecordService.successLockLog(user.getId(),clientIp,fingerprint);
			logout(sessionId);
			return true;
		}else {
			logger.error("锁定{}用户失败", user.getId());
			return false;
		}
	}

	@Override
	public Boolean lock(Long userId, String sessionId, String clientIp, String fingerprint) {
		UserVo user = getUserFromRedis(sessionId);
		if (userMapper.selectById(userId)==null){
			logger.info("尝试锁定不存在的{}用户", userId);
			return null;
		}
		if(userMapper.lockById(userId,user.getId())>0) {
			userRecordService.successLockAdminLog(userId,clientIp,fingerprint);
//			if (redisService.hasUser(String.valueOf(userId))){
//				String userKey = redisService.getUserKey(String.valueOf(user.getId()));
//				logger.debug("管理员锁定{}用户在一个地方登录，自动注销用户登录状态", user.getUsername());
//				redisService.deleteUser(userKey);
//				redisService.deleteUser(String.valueOf(userId));
//			}
			redisService.deleteUser(userId);
			return true;
		}else {
			logger.error("管理员锁定{}用户失败", userId);
			return false;
		}
	}

	@Override
	public Boolean unlock(Long userId, String sessionId, String clientIp, String fingerprint) {
		UserVo user = getUserFromRedis(sessionId);
		if (userMapper.selectById(userId)==null){
			logger.info("尝试解锁不存在的{}用户", userId);
			return null;
		}
		if(userMapper.unlockById(userId,user.getId())>0) {
			userRecordService.successUnlockAdminLog(userId, clientIp, fingerprint);
			logger.debug("管理员解锁{}用户成功", userId);
			return true;
		}else {
			logger.error("管理员解锁{}用户失败", userId);
			return false;
		}
	}

	/**
	 * 修改用户信息
	 *
	 * @param user 用户对象，包含要修改的用户信息，不能为空
	 * @param sessionId 用户会话ID，用于从Redis中获取当前用户信息
	 * @return 如果用户信息修改成功，则返回true；否则返回false
	 */
	@Override
	public Boolean modifyInfo(@NotNull User user, String sessionId) {
	    // 从Redis中获取当前用户信息，并设置其ID到用户对象中
	    user.setId(getUserFromRedis(sessionId).getId());

	    // 尝试更新用户信息
	    if (userMapper.updateById(user)>0){
	        // 如果更新成功，从Redis中获取当前用户信息
	        UserVo userVo = getUserFromRedis(sessionId);

	        // 更新用户信息
	        userVo.setFirstName(user.getFirstName());
	        userVo.setLastName(user.getLastName());
	        userVo.setPhone(user.getPhone());
	        userVo.setAvatar(user.getAvatar());
	        userVo.setBirthDate(user.getBirthDate());

	        // 如果用户生日不为空，则计算并设置距离下一次生日的天数
	        if (user.getBirthDate()!=null){
	            userVo.setNextBirthday(getDaysUntilNextBirthday(user.getBirthDate()));
	        }

	        // 更新Redis中的用户信息
	        setUserToRedis(userVo);
	        return true;
	    }else {
	        // 如果更新失败，记录错误日志
	        logger.error("修改{}用户信息失败", user);
	        return false;
	    }
	}

	@Override
	public UserVo getUserById(Long userId) {
		if (userId==-1){
			UserVo userVo = new UserVo();
			userVo.setId(-1L);
			userVo.setUsername("系统默认");
			return userVo;
		}
		User user = userMapper.selectById(userId);
		if (user==null){
			UserVo userVo = new UserVo();
			userVo.setId(0L);
			userVo.setUsername("未知用户");
			return userVo;
		}else {
			return BeanCopyUtils.copyBean(user, UserVo.class);
		}
	}


	/**
	 * 修改用户密码
	 *
	 * @param oldPassword 旧密码，用于验证当前用户的密码是否正确
	 * @param newPassword 新密码，用于替换旧密码
	 * @param sessionId 用户会话ID，用于识别用户会话
	 * @param clientIp 客户端IP地址，用于记录操作日志
	 * @param fingerprint 用户设备指纹，用于增强日志的唯一性
	 * @return 返回一个布尔值表示密码修改的结果如果返回null，则表示修改密码失败
	 */
	@Override
	public Boolean modifyPassword(@NotNull String oldPassword, String newPassword, String sessionId, String clientIp, String fingerprint) {
	    // 从Redis中获取当前用户信息
	    UserVo user = getUserFromRedis(sessionId);

	    // 验证旧密码是否正确
	    if (!oldPassword.equals(userMapper.selectById(user.getId()).getPassword())){
	        // 记录失败的修改密码日志
	        userRecordService.failedModifyPasswordLog(user.getId(),clientIp,fingerprint);
	        return false;
	    }

	    // 尝试修改密码
	    if (userMapper.modifyPasswordById(user.getId(), newPassword) > 0){
	        // 记录成功的修改密码日志
	        userRecordService.successModifyPasswordLog(user.getId(),clientIp,fingerprint);
	        // 清除会话中的用户信息，因为密码已修改
	        logout(sessionId);
	        return true;
	    }else {
	        // 记录修改密码失败的日志
	        logger.error("{}用户修改密码失败", user.getId());
	        return null;
	    }
	}

	/**
	 * 根据用户名查询用户是否存在
	 * 如果找到匹配的用户，则返回true；否则返回false
	 *
	 * @param userName 待查询的用户名
	 * @return 如果用户存在则返回true，否则返回false
	 */
	@Override
	public Boolean queryByUserName(String userName) {
	    // 根据查询条件尝试获取用户信息
	    return userMapper.selectCountByUsername(userName)>0;
	}

	/**
	 * 根据邮箱查询用户是否存在
	 * 如果找到匹配的用户，则返回true；否则返回false
	 *
	 * @param email 待查询的邮箱
	 * @return 如果用户存在则返回true，否则返回false
	 */
	@Override
	public Boolean queryByEmail(String email) {
		// 根据查询条件尝试获取用户信息
		return userMapper.selectCountByEmail(email) > 0;
	}

    /**
     * 获取用户列表
     * <p>
     * 此方法根据用户的ID查询数据库中的用户列表，并为每个用户计算下一次生日和是否为管理员状态
     * 它首先通过用户ID获取用户信息，然后根据此用户的角色ID从数据库中查询相应的用户列表
     * 最后，它会为每个用户计算下一次生日的天数，并设置是否为管理员的状态
     *
     * @param pageNum 当前页码
     * @param pageSize 页面大小
     * @param userId 用户ID
     * @return 用户列表的Vo对象
     */
	@Override
    public List<UserVo> getUserList(Integer pageNum, Integer pageSize, Long userId) {
        // 通过用户ID获取用户信息
//        User user = userMapper.selectById(userId);
        // 创建分页对象
        Page<User> userPage = new Page<>(pageNum, pageSize);
        // 创建查询条件对象，并限制角色ID
//        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>().le(User::getRoleId,user.getRoleId()+0.1);
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 根据分页和查询条件获取用户列表
        List<User> users = userMapper.selectPage(userPage,queryWrapper).getRecords();
        // 将用户列表转换为Vo对象列表
        List<UserVo> userVos = BeanCopyUtils.copyBeanList(users, UserVo.class);
        // 为每个Vo对象计算下一次生日和设置是否为管理员状态
        for (UserVo userVo : userVos) {
			userVo.setActive(userMapper.selectById(userVo.getId()).getIsActive());
			if (userVo.getBirthDate() != null) {
				userVo.setNextBirthday(getDaysUntilNextBirthday(userVo.getBirthDate()));
			}
            userVo.setAdmin(!userVo.getPermissions().isEmpty());
        }
        // 返回处理后的用户列表Vo对象
        return userVos;
    }
}
