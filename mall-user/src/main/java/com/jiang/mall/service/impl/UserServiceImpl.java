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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.dao.GroupMapper;
import com.jiang.mall.dao.UserGroupRelationMapper;
import com.jiang.mall.dao.UserMapper;
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.User;
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
		UserVo user = redisService.getUser(sessionId);
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
		if (redisService.hasUser(sessionId)){
			return redisService.getUser(sessionId);
		}else{
			return null;
		}
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
			// 确保单例登录，同一用户在同一时间只能在一个地方登录。如果用户在另一个地方尝试登录，系统会自动将之前的登录状态注销
			if (redisService.hasUser(String.valueOf(user.getId()))){
				String userKey = redisService.getUserKey(String.valueOf(user.getId()));
				logger.debug("用户{}在另一个地方登录，自动注销之前的登录状态", user.getUsername());
				redisService.deleteUser(userKey);
			}
			// 将用户信息存储到Redis中，并设置过期时间
			redisService.setUser(sessionId, userVo,4, TimeUnit.HOURS);
			// 登录成功，记录登录记录
			userRecordService.successLoginLog(user, clientIp, fingerprint);
			logger.debug("用户{}登录成功", user.getUsername());
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
	public Boolean modifyPassword(Long userId, String newPassword, VerificationCode verificationCode, String clientIp, String fingerprint) {
		// 创建查询条件，指定用户ID和账号激活状态。
	    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
	    queryWrapper.eq("id",userId);

	    // 根据查询条件尝试获取用户信息。
	    User user = userMapper.selectOne(queryWrapper);

	    // 验证用户是否存在且旧密码是否正确。
	    if (user != null) {
	        // 如果验证成功，更新用户密码为新密码。
	        user.setPassword(newPassword);
			user.setIsActive(true);
			// 通过ID更新用户信息。
		    if (userMapper.updateById(user) > 0){
				verificationCode.setPassword(newPassword);
				verificationCodeService.useCode(userId, verificationCode);
				userRecordService.successForgotLog(userId,clientIp,fingerprint);
				return true;
		    }else {
				return null;
		    }
	    }else {
			// 如果用户不存在或旧密码验证失败，返回false。
			return false;
	    }
	}

	@Override
	public Boolean logout(String sessionId) {
		if (redisService.hasUser(sessionId)){
			return redisService.deleteUser(sessionId);
		}
		return true;
	}

	@Override
	public Boolean validatePassword(Long userId, String password) {
		// 创建查询条件，指定用户ID和账号激活状态。
	    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
	    queryWrapper.eq("id",userId);
		queryWrapper.eq("password",password);

	    // 根据查询条件尝试获取用户信息。
	    User user = userMapper.selectOne(queryWrapper);

		return user != null;
	}

	@Override
	public Boolean modifyEmail(Long userId, String email, VerificationCode verificationCode, String sessionId, String clientIp, String fingerprint) {
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("id",userId);
		queryWrapper.eq("is_active", true);
		User user = userMapper.selectOne(queryWrapper);
		if (user != null){
			user.setEmail(email);
			if (userMapper.updateById(user)>0){
				// 验证码使用标记
				verificationCodeService.useCode(userId, verificationCode);
				// 记录邮箱修改成功日志
				userRecordService.successModifyEmailLog(user,email,clientIp,fingerprint);
				return true;
			}else {
				logger.error("修改用户邮箱失败{}", userId);
				return null;
			}
		}else {
			return false;
		}
	}

	@Override
	public Boolean lockUserByAdmin(Long userId, String clientIp, String fingerprint) {
		// 创建查询条件，指定用户ID和当前为激活状态
	    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
	    queryWrapper.eq("id", userId);
	    queryWrapper.eq("is_active", true);

	    // 根据查询条件尝试获取用户信息
	    User user = userMapper.selectOne(queryWrapper);

	    // 如果用户存在
	    if (user != null) {
	        // 将用户活跃状态设置为false，即锁定用户
	        user.setIsActive(false);
	        // 更新数据库中的用户信息
	        if(userMapper.updateById(user)>0) {
				userRecordService.successLockAdminLog(userId,clientIp,fingerprint);
				return true;
	        }else {
				logger.error("管理员锁定用户失败{}", userId);
				return null;
	        }
	    } else {
	        // 记录日志，提示尝试锁定不存在的用
	        logger.info("管理员尝试锁定不存在的用户，ID: {}", userId);
	        return false;
	    }
	}

	@Override
	public User getUserById(Long userId) {
		return userMapper.selectById(userId);
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
			return user.getId();
		}else {
			logger.error("注册用户失败{}", user);
			return null;
		}
	}

	/**
	 * 修改用户密码的方法。用户名密码是经过MD5加密的，以提高安全性。
	 *
	 * @param userId      用户ID，用于查询用户信息。
	 * @param oldPassword 用户当前密码，用于验证身份。
	 * @param newPassword 用户新密码，待验证通过后设置。
	 * @param sessionId   用户会话ID，用于记录操作日志。
	 * @param clientIp    ip
	 * @param fingerprint  指纹
	 * @return 如果密码修改成功，返回true；否则返回false。
	 */
    @Override
    public Boolean modifyPassword(Long userId, String oldPassword, String newPassword, String sessionId, String clientIp, String fingerprint) {
		// 创建查询条件，指定用户ID和账号激活状态。
	    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
	    queryWrapper.eq("id",userId);
		queryWrapper.eq("password",oldPassword);
	    queryWrapper.eq("is_active", true);

	    // 根据查询条件尝试获取用户信息。
	    User user = userMapper.selectOne(queryWrapper);

	    // 验证用户是否存在且旧密码是否正确。
	    if (user != null) {
	        // 如果验证成功，更新用户密码为新密码。
	        user.setPassword(newPassword);
			// 通过ID更新用户信息。
		    if (userMapper.updateById(user) > 0){
				// 记录邮箱修改成功日志
				userRecordService.successModifyPasswordLog(userId,clientIp,fingerprint);
				// 清除会话中的用户信息，因为密码已修改
                logout(sessionId);
				return true;
		    }else {
				logger.error("修改用户密码失败{}", userId);
				return null;
		    }
	    }else {
			// 如果用户不存在或旧密码验证失败，返回false
		    userRecordService.failedModifyPasswordLog(userId,clientIp,fingerprint);
			return false;
	    }
	}

	/**
	 * 修改用户信息。
	 * 此方法用于更新用户的信息。
	 * 它首先检查用户是否存在于数据库中且当前状态为非激活状态。
	 * 如果用户存在且满足条件，则更新用户的密码和激活状态。
	 * 如果用户不存在，则记录日志并返回false。
	 *
	 * @param newUser 包含新用户信息的对象，其中ID用于查找用户，密码和激活状态用于更新用户信息。
	 * @return 如果用户信息成功更新，则返回true；否则返回false。
	 */
    @Override
    public Boolean modifyUserInfo(@NotNull User newUser) {
	    // 创建查询条件，特定于用户的ID和非激活状态。
	    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
	    queryWrapper.eq("id", newUser.getId());
	    queryWrapper.eq("is_active", true);

	    // 根据查询条件尝试获取用户信息。
	    User user = userMapper.selectOne(queryWrapper);

	    // 如果用户存在且当前是非激活状态，则进行更新。
	    if (user != null) {
	        // 维持原密码不变，确保用户不会因为信息修改而失去访问权限。
	        newUser.setPassword(user.getPassword());
			newUser.setUsername(user.getUsername());
	        // 将用户状态设置为激活，确保用户不会因为信息修改而失去访问权限。
	        newUser.setIsActive(user.getIsActive());
//			newUser.setRoleId(user.getRoleId());
	        // 更新数据库中的用户信息。
	        int result = userMapper.updateById(newUser);
	        // 检查更新是否成功，并返回结果。
	        return result > 0;
	    } else {
	        // 如果用户不存在，则记录日志并返回false。
	        logger.info("尝试更新(modify)不存在的用户信息，ID: {}", newUser.getId());
	        return false;
	    }
	}

	/**
	 * 锁定用户方法。
	 * 通过设置用户的活跃状态为false来锁定用户账号。
	 *
	 * @param userId      用户ID，用于查询和锁定特定用户。
	 * @param sessionId   会话ID，用于记录操作日志。
	 * @param clientIp    ip
	 * @param fingerprint  指纹
	 * @return 如果用户成功被锁定，返回true；如果用户不存在或锁定失败，返回false。
	 */
    @Override
    public Boolean lockUser(Long userId, String sessionId, String clientIp, String fingerprint) {
	    // 创建查询条件，指定用户ID和当前为激活状态
	    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
	    queryWrapper.eq("id", userId);
	    queryWrapper.eq("is_active", true);

	    // 根据查询条件尝试获取用户信息
	    User user = userMapper.selectOne(queryWrapper);

	    // 如果用户存在
	    if (user != null) {
	        // 将用户活跃状态设置为false，即锁定用户
	        user.setIsActive(false);
	        // 更新数据库中的用户信息
	        if(userMapper.updateById(user)>0) {
				userRecordService.successLockLog(userId,clientIp,fingerprint);
				logout(sessionId);
				return true;
	        }else {
				logger.error("锁定用户失败{}", userId);
				return null;
	        }
	    } else {
	        // 记录日志，提示尝试锁定不存在的用
	        logger.info("尝试锁定不存在的用户，ID: {}", userId);
	        return false;
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
	 * 用户注册步骤
	 *
	 * @param user             待注册/更新的用户信息
	 * @param verificationCode 验证码对象，用于验证用户输入的验证码
	 * @param sessionId        用户会话ID
	 * @param clientIp         ip
	 * @param fingerprint       指纹
	 * @return 注册/更新成功返回用户ID，否则返回0
	 */
	@Override
	public Long register(@NotNull User user, VerificationCode verificationCode, String sessionId, String clientIp, String fingerprint) {
	    // 检查用户信息是否完整
	    if(user.getUsername()!=null && user.getPassword()!=null && user.getEmail()!=null){
	        // 设置用户账户为激活状态
	        user.setIsActive(true);
	        // 设置用户角色为普通用户
//	        user.setRoleId(1);
	        // 插入用户信息，若成功则返回用户ID，否则返回0
		    if (userMapper.insert(user) > 0){
				temporaryRedisService.setKey(sessionId, String.valueOf(user.getId()),30, TimeUnit.MINUTES);
                verificationCodeService.useCode(user.getId(), verificationCode);
                userRecordService.successRegisterLog(user, clientIp, fingerprint);
		    }
	        return userMapper.insert(user)>0?user.getId():0;
	    }else{
	        // 对于信息不完整的用户，清除其账户信息
	        user.setUsername(null);
	        user.setPassword(null);
	        user.setEmail(null);
			user.setIsActive(true);
//			user.setRoleId(1);
	        // 更新用户信息，若成功则返回用户ID，否则返回0
		    redisService.deleteUser(sessionId);
	        return userMapper.updateById(user)>0?user.getId():0;
	    }
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


    @Override
    public Boolean updateUser(@NotNull User newUser) {

	    User user = userMapper.selectById(newUser.getId());

	    // 如果用户存在且当前是非激活状态，则进行更新。
	    if (user != null) {
	        // 维持原密码不变，确保用户不会因为信息修改而失去访问权限。
	        newUser.setPassword(user.getPassword());
			newUser.setUsername(user.getUsername());
	        newUser.setIsActive(user.getIsActive());
	        // 更新数据库中的用户信息。
	        int result = userMapper.updateById(newUser);
	        // 检查更新是否成功，并返回结果。
	        return result > 0;
	    } else {
	        // 如果用户不存在，则记录日志并返回false。
	        logger.info("尝试更新不存在的用户信息，ID: {}", newUser.getId());
	        return false;
	    }
    }

	/**
	 * 解锁用户方法。
	 * 通过设置用户的活跃状态为true来解锁用户账号。
	 *
	 * @param userId      用户ID，用于查询和锁定特定用户。
	 * @param clientIp      ip
	 * @param fingerprint   指纹
	 * @return 如果用户成功被解锁，返回true；如果用户不存在或解锁失败，返回false。
	 */
	@Override
	public Boolean unlockUser(Long userId, String clientIp, String fingerprint) {
	    // 创建查询条件对象
	    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
	    // 设置查询条件：用户ID等于userId且用户当前是锁定状态（is_active为false）
	    queryWrapper.eq("id", userId);
	    queryWrapper.eq("is_active", false);

	    // 根据查询条件尝试获取用户信息
	    User user = userMapper.selectOne(queryWrapper);

	    // 如果用户存在
	    if (user != null) {
	        // 将用户状态设置为解锁
	        user.setIsActive(true);
	        // 更新数据库中的用户信息
	        if (userMapper.updateById(user)>0){
				userRecordService.successUnlockAdminLog(userId, clientIp, fingerprint);
				return true;
	        }else{
				return null;
			}
	    } else {
	        // 记录日志，提示尝试解锁不存在的用户
	        logger.info("尝试解锁不存在的用户，ID: {}", userId);
	        return false;
	    }
	}
}
