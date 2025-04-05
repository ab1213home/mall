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

import cn.hutool.core.codec.Base32;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.otp.TOTP;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.config.GeneralConfig;
import com.jiang.mall.config.UserConfig;
import com.jiang.mall.dao.*;
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.cache.UserCache;
import com.jiang.mall.domain.dto.GiteeUserDto;
import com.jiang.mall.domain.dto.ShopPermissionDto;
import com.jiang.mall.domain.entity.User;
import com.jiang.mall.domain.entity.UserGroupRelation;
import com.jiang.mall.domain.entity.UserOauth;
import com.jiang.mall.domain.entity.VerificationCode;
import com.jiang.mall.domain.enums.OAuthProvider;
import com.jiang.mall.domain.enums.OAuthResult;
import com.jiang.mall.domain.enums.UserStatus;
import com.jiang.mall.domain.vo.UserAdminVo;
import com.jiang.mall.domain.vo.UserVo;
import com.jiang.mall.service.*;
import com.jiang.mall.util.BeanCopyUtil;
import com.jiang.mall.util.SecureUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

import static cn.hutool.crypto.digest.otp.HOTP.generateSecretKey;

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

	private IUserLogService userLogService;

    @Autowired
    public void setLoginRecordService(IUserLogService userLogService) {
        this.userLogService = userLogService;
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

	private IEmailService emailService;

	@Autowired
	public void setEmailService(IEmailService emailService) {
		this.emailService = emailService;
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

	private UserOauth2Mapper userOauth2Mapper;

	@Autowired
	public void setUserOauth2Mapper(UserOauth2Mapper userOauth2Mapper) {
		this.userOauth2Mapper = userOauth2Mapper;
	}

	private ShopStaffMapper shopStaffMapper;

	@Autowired
	public void setShopStaffMapper(ShopStaffMapper shopStaffMapper) {
		this.shopStaffMapper = shopStaffMapper;
	}

	private UserConfig userConfig;

	@Autowired
	public void setUserConfig(UserConfig userConfig) {
		this.userConfig = userConfig;
	}

	private GeneralConfig generalConfig;

	@Autowired
	public void setGeneralConfig(GeneralConfig generalConfig) {
		this.generalConfig = generalConfig;
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
		UserCache user = getUserFromRedis(sessionId);
		if (user == null){
			return ResponseResult.notLoggedResult(i18nService.getMessage("user.checkUser.noLogin"));
		}else{
			if (user.getId()==null){
				return ResponseResult.failResult(i18nService.getMessage("user.checkUser.error"));
			}else{
				return ResponseResult.okResult(BeanCopyUtil.copyBean(user, UserVo.class));
			}
		}
	}

	@Override
	public UserCache getUserFromRedis(String sessionId) {
		return redisService.getUserBySessionId(sessionId);
	}

	@Override
	public void setUserToRedis(UserCache user) {
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
	 * 用户登录方法
	 * 通过用户名(邮箱)和密码尝试登录系统。用户密码是经过MD5加密的，以提高安全性。
	 *
	 * @param username    用户名或者邮箱，用于登录验证
	 * @param password    密文密码，用于登录验证
	 * @param token       token，用于登录状态
	 * @param clientIp    客户端IP地址
	 * @param fingerprint 浏览器指纹，用于登录验证
	 * @return 如果验证成功，返回对应的ture对象；如果验证失败或用户不存在，返回false
	 */
	@Override
	public Boolean login(String username, String password, String token, String clientIp, String fingerprint, String sessionId) {
		User user = getUserByUserNameOrEmail(username, password);
		//flag==null账号密码错误，flag==false账号密码正确，但是需要二次登录，flag==true账号密码正确且无需二次登录，即登录成功
		if (user == null) {
			// 登录失败，记录登录记录
			Map<String, Object> map = new HashMap<>();
			map.put("username", username);
			map.put("password", password);
			userLogService.defaultLog(username, clientIp, fingerprint, UserStatus.FAIL_LOGIN , map);
			logger.debug("用户名或密码错误");
			return null;
		} else if (user.isTotpEnabled()) {
			//TODO:需要完善逻辑
			redisService.setTwoLogin(user.getId(), sessionId);
			return false;
		} else {
			// 登录成功，记录登录记录
			userLogService.defaultLog(username, clientIp, fingerprint, UserStatus.SUCCESS_LOGIN , null);
			login(user, token, sessionId);
			return true;
		}
	}

	private void login(@NotNull User user, String token, String sessionId) {
		UserCache userCache = BeanCopyUtil.copyBean(user, UserCache.class);
		assert userCache != null;
		Set<Long> groupIds = userGroupRelationMapper.selectGroupIdByUserId(user.getId());
		userCache.setGroups(groupIds);
		Set<String> deniedPermissions = new HashSet<>();
		if (user.getDeniedPermission() != null && !user.getDeniedPermission().isEmpty()) {
			deniedPermissions = new HashSet<>(Arrays.asList(user.getDeniedPermission().split(",")));
		}
		Set<String> permissions = new HashSet<>();
		if (!groupIds.isEmpty()){
			for (Long groupId : groupIds) {
				String groupPermission = groupMapper.selectPermissionByGroupId(groupId);
				permissions.addAll(Arrays.asList(groupPermission.split(",")));
			}
		}
		permissions.addAll(Arrays.asList(user.getPermission().split(",")));
		// 去除权限user.getDeniedPermission()
		permissions.removeAll(deniedPermissions);
		// 获取店铺权限与id
		List<ShopPermissionDto> shopPermissions = shopStaffMapper.selectShopPermissionByUserId(user.getId());
		if (!shopPermissions.isEmpty()){
			for (ShopPermissionDto entry : shopPermissions){
				String[] shopPermissionList = entry.getPermission().split(",");
				for (String permission : shopPermissionList) {
					permissions.add("shop_"+entry.getShopId()+":"+permission);
				}
			}
		}

		userCache.setPermissions(permissions);
		// 将用户信息存储到Redis中，并设置过期时间
		redisService.setUser(sessionId, token, userCache);
		logger.debug("用户{}登录成功", user.getUsername());
	}

	public User getUserByUserNameOrEmail(String username, String password) {
	    // 根据查询条件尝试获取用户信息
	    User user_username = userMapper.selectByUsernameAndIsActive(username,true );

	    // 根据邮箱格式匹配用户
	    if (i18nService.isValidEmail(username)) {
	        // 创建基于邮箱的查询条件
	        User user_email = userMapper.selectByEmailAndIsActive(username, true);
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
		return userMapper.validatePassword(userId,password, true )>0;
	}

	@Override
	public Boolean modifyEmail(@NotNull VerificationCode verificationCode, String sessionId, String clientIp, String fingerprint) {
		UserCache userVo = getUserFromRedis(sessionId);
		User user = userMapper.selectUserByIdAndActive(userVo.getId(),true);
		Map<String,Object> map = new HashMap<>();
		map.put("new_email",verificationCode.getEmail());
		map.put("old_email",user.getEmail());
		if (userMapper.updateEmail(user.getId(),verificationCode.getEmail())>0){
			// 验证码使用标记
			emailService.useCode(userVo.getId(), verificationCode);
			// 记录邮箱修改成功日志
			userLogService.defaultLog(user.getUsername(),clientIp,fingerprint, UserStatus.SUCCESS_MODIFY_EMAIL,map);
			userVo.setEmail(verificationCode.getEmail());
			setUserToRedis(userVo);
			return true;
		}else {
			logger.error("修改{}用户邮箱失败", getUserFromRedis(sessionId).getId());
			userLogService.defaultLog(user.getUsername(),clientIp,fingerprint, UserStatus.FAIL_MODIFY_EMAIL,map);
			return false;
		}
	}

	@Override
	public Long register(@NotNull VerificationCode verificationCode, String sessionId, String clientIp, String fingerprint) {
		User user = new User();
		user.setUsername(verificationCode.getUsername());
		user.setPassword(verificationCode.getPassword());
		user.setEmail(verificationCode.getEmail());
		user.setActive(true);
		user.setTotpEnabled(false);
		Map<String,Object> map = new HashMap<>();
		map.put("email",user.getEmail());
		map.put("password",user.getPassword());
		if (userMapper.insert(user) > 0){
			redisService.setTwoRegister(user.getId(),sessionId);
			emailService.useCode(user.getId(), verificationCode);
			userLogService.defaultLog(user.getUsername(),clientIp,fingerprint, UserStatus.SUCCESS_REGISTER,map);
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
	public Boolean register(@NotNull User user, String sessionId) {
		Long userId = redisService.getTwoRegister(sessionId);
		user.setId(userId);
		if (userMapper.updateById(user)>0){
			redisService.deleteTwoRegister(sessionId);
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
		Map<String,Object> map = new HashMap<>();
		map.put("new_password",password);

	    // 验证用户是否存在
	    if (user != null) {
	        // 如果验证成功，更新用户密码为新密码。
		    map.put("old_password",user.getPassword());
	        user.setPassword(password);
	        // 激活用户账户
	        user.setActive(true);
	        // 通过ID更新用户信息。
	        if (userMapper.updateById(user) > 0){
	            // 更新验证码对象的密码信息
	            verificationCode.setPassword(password);
	            // 使用验证码，并记录使用信息
	            emailService.useCode(user.getId(), verificationCode);
	            // 记录用户成功找回密码的日志
		        userLogService.defaultLog(user.getUsername(),clientIp,fingerprint, UserStatus.SUCCESS_FORGET_PASSWORD,map);
	            // 更新成功，返回true
	            return true;
	        }else {
	            // 更新失败，返回null
	            return null;
	        }
	    }else {
	        // 如果用户不存在，返回false。
		    userLogService.defaultLog(verificationCode.getUsername(),clientIp,fingerprint, UserStatus.FAIL_FORGET_PASSWORD,map);
	        return false;
	    }
	}

	@Override
	public Boolean lock(String sessionId, String clientIp, String fingerprint) {
		UserCache user = getUserFromRedis(sessionId);
		if(userMapper.lockById(user.getId(),user.getId())>0) {
			userLogService.defaultLog(user.getUsername(),clientIp,fingerprint, UserStatus.SUCCESS_LOCK,null);
			logout(sessionId);
			return true;
		}else {
			logger.error("锁定{}用户失败", user.getId());
			return false;
		}
	}

	@Override
	public Boolean lock(Long userId, String sessionId, String clientIp, String fingerprint) {
		UserCache user = getUserFromRedis(sessionId);
		if (userMapper.selectById(userId)==null){
			logger.info("尝试锁定不存在的{}用户", userId);
			return null;
		}
		if(userMapper.lockById(userId,user.getId())>0) {
			userLogService.defaultLog(userMapper.selectById(userId).getUsername(), clientIp, fingerprint, UserStatus.SUCCESS_ADMIN_LOCK, null);
			redisService.deleteUser(userId);
			return true;
		}else {
			logger.error("管理员锁定{}用户失败", userId);
			return false;
		}
	}

	@Override
	public Boolean unlock(Long userId, String sessionId, String clientIp, String fingerprint) {
		UserCache user = getUserFromRedis(sessionId);
		if (userMapper.selectById(userId)==null){
			logger.info("尝试解锁不存在的{}用户", userId);
			return null;
		}
		if(userMapper.unlockById(userId,user.getId())>0) {
			userLogService.defaultLog(userMapper.selectById(userId).getUsername(), clientIp, fingerprint, UserStatus.SUCCESS_UNLOCK, null);
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
	        UserCache userCache = getUserFromRedis(sessionId);

	        // 更新用户信息
	        userCache.setFirstName(user.getFirstName());
	        userCache.setLastName(user.getLastName());
	        userCache.setPhone(user.getPhone());
	        userCache.setAvatar(user.getAvatar());
	        userCache.setBirthDate(user.getBirthDate());

	        // 更新Redis中的用户信息
	        setUserToRedis(userCache);
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
		}else if (userId==0){
			UserVo userVo = new UserVo();
			userVo.setId(0L);
			userVo.setUsername("未知用户");
			return userVo;
		}
		User user;
		if (redisService.hasUser(userId)){
			UserCache userCache = redisService.getUser(userId);
			user = BeanCopyUtil.copyBean(userCache, User.class);
		}else {
			user = userMapper.selectById(userId);
		}
		if (user==null){
			UserVo userVo = new UserVo();
			userVo.setId(0L);
			userVo.setUsername("未知用户");
			return userVo;
		}else {
			return BeanCopyUtil.copyBean(user, UserVo.class);
		}
	}

	@Override
	public boolean getTotpStatus(String sessionId) {
		UserCache user = getUserFromRedis(sessionId);
		return userMapper.selectTotpStatusById(user.getId());
	}

	@Override
	public String enableTotp(String sessionId) {
		UserCache user = getUserFromRedis(sessionId);
		if (userMapper.selectTotpStatusById(user.getId())){
			return null;
		}else {
			Map<String, String> map = generateSecretKeyAndQRCodeUrl(user.getUsername());
			logger.debug("用户{}生成TOTP密钥和二维码URL", user.getUsername());
			if (userMapper.updateTotpSecretById(user.getId(),map.get("secretKey"))>0){
				return map.get("qrCodeUrl");
			}else {
				return null;
			}
		}
	}

	@Override
	public boolean disableTotp(String sessionId) {
		UserCache user = getUserFromRedis(sessionId);
		return userMapper.updateTotpStatusById(user.getId(), false) > 0;
	}

	@Override
	public boolean enableTotp(String sessionId, int code) {
		UserCache user = getUserFromRedis(sessionId);
		String secretKey = userMapper.selectTotpSecretById(user.getId());
		if (verifyTOTP(secretKey, code)){
			if (userMapper.updateTotpStatusById(user.getId(), true) > 0){
				//TODO: 修改成功后记录日志,用户行为日志计划重构
//				userRecordService.successModifyTotpLog(user.getId(),clientIp,fingerprint);
				return true;
			}else {
//				userRecordService.failedModifyTotpLog(user.getId(),clientIp,fingerprint);
				return false;
			}
		}else {
			return false;
		}
	}

	@Override
	public List<Long> getOnlineUser() {
		return redisService.getOnlineUser();
	}

	@Override
	public boolean login(String sessionId, int code, String token, String clientIp, String fingerprint) {
		if (redisService.validateTwoLogin(sessionId)){
			Long userId = redisService.getTwoLogin(sessionId);
			String secretKey = userMapper.selectTotpSecretById(userId);
			if (verifyTOTP(secretKey, code)){
				User user = userMapper.selectById(userId);
				// 登录成功，记录登录记录
				userLogService.defaultLog(user.getUsername(), clientIp, fingerprint, UserStatus.SUCCESS_LOGIN, null);
				login(user, token, sessionId);
				return true;
			}else {
				Map<String,Object> map = new HashMap<>();
				map.put("code",code);
				userLogService.defaultLog(userMapper.selectById(userId).getUsername(), clientIp, fingerprint, UserStatus.FAIL_LOGIN, map);
				return false;
			}
		}else {
			logger.error("登录失败，sessionId不存在");
			return false;
		}

	}

	@Override
	public int countTryNumber(String username, String clientIp, String fingerprint) {
		return userLogService.countTryNumber(username, clientIp, fingerprint);
	}

	@Override
	public Boolean login(String password, String token, String clientIp, String fingerprint, String sessionId) {
		if (!redisService.validateRememberMe(token)){
			return null;
		}
		Long userId = redisService.getRememberMe(token);
		User user = userMapper.selectById(userId);
		Map<String, Object> map = new HashMap<>();
		map.put("username", user.getUsername());
		map.put("password", password);
		map.put("token", token);
		if (!user.isActive()||!validatePassword(userId, password)){
			userLogService.defaultLog(user.getUsername(), clientIp, fingerprint, UserStatus.FAIL_LOGIN , map);
			logger.debug("token或密码错误");
			return null;
		}
		//flag==null账号密码错误，flag==false账号密码正确，但是需要二次登录，flag==true账号密码正确且无需二次登录，即登录成功
		if (user.isTotpEnabled()) {
			//TODO:需要完善逻辑
			redisService.setTwoLogin(user.getId(), sessionId);
			return false;
		} else {
			// 登录成功，记录登录记录
			userLogService.defaultLog(user.getUsername(), clientIp, fingerprint, UserStatus.SUCCESS_LOGIN , map);
			login(user, token, sessionId);
			return true;
		}
	}

	@Override
	public OAuthResult oauthLogin(@NotNull GiteeUserDto user, String token, String sessionId) {
		QueryWrapper<UserOauth> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("provider_type", OAuthProvider.GITEE.getKey());
		queryWrapper.eq("provider_user_id", user.getId());
		UserOauth userOauth = userOauth2Mapper.selectOne(queryWrapper);
		if (userOauth==null){
			return OAuthResult.UNBOUND;
		}
		String hash = SecureUtil.sha256Hex(JSON.toJSONString(user));
		if (!userOauth.getHash().equals(hash)){
			userOauth2Mapper.updateAnnotations(userOauth.getId(), hash ,JSON.toJSONString(user));
		}
		User user_db = userMapper.selectById(userOauth.getUserId());
		if (!user_db.isActive()){
			return OAuthResult.UNBOUND;
		}
		if (user_db.isTotpEnabled()){
			redisService.setTwoLogin(user_db.getId(), sessionId);
			return OAuthResult.SECOND_VERIFY;
		}else {
			// 登录成功，记录登录记录
			userLogService.oauthLoginLog(user_db.getUsername(), UserStatus.SUCCESS_LOGIN);
			login(user_db, token, sessionId);
			return OAuthResult.SUCCESS;
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
	    UserCache user = getUserFromRedis(sessionId);
		Map<String,Object> map = new HashMap<>();
		map.put("old_password",oldPassword);
		map.put("new_password",newPassword);
	    // 验证旧密码是否正确
	    if (!oldPassword.equals(userMapper.selectById(user.getId()).getPassword())){
	        // 记录失败的修改密码日志
	        userLogService.defaultLog(user.getUsername(), clientIp, fingerprint, UserStatus.FAIL_MODIFY_PASSWORD, map);
	        return false;
	    }

	    // 尝试修改密码
	    if (userMapper.modifyPasswordById(user.getId(), newPassword) > 0){
	        // 记录成功的修改密码日志
	        userLogService.defaultLog(user.getUsername(), clientIp, fingerprint, UserStatus.SUCCESS_MODIFY_PASSWORD, map);
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
     * @param pageNum  当前页码
     * @param pageSize 页面大小
     * @return 用户列表的Vo对象
     */
	@Override
    public List<UserAdminVo> getUserList(Integer pageNum, Integer pageSize) {
        // 创建分页对象
        Page<User> userPage = new Page<>(pageNum, pageSize);
        // 创建查询条件对象
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 根据分页和查询条件获取用户列表
        List<User> users = userMapper.selectPage(userPage,queryWrapper).getRecords();
        // 将用户列表转换为Vo对象列表
        List<UserAdminVo> userVos = new ArrayList<>();
        // 为每个Vo对象计算下一次生日和设置是否为管理员状态
        for (User user : users) {
			UserAdminVo userVo = BeanCopyUtil.copyBean(user, UserAdminVo.class);
	        assert userVo != null;
	        userVo.setUpdater(getUserById(user.getUpdater()));
	        userVos.add(userVo);
        }
        // 返回处理后的用户列表Vo对象
        return userVos;
    }

	//生成密钥和返回二维码URL
	private @NotNull Map<String, String> generateSecretKeyAndQRCodeUrl(String username) {
	    String secretKey = generateSecretKey(15);
	    String qrCodeUrl = StrUtil.format("otpauth://totp/{}?secret={}&issuer={}", username, secretKey, generalConfig.getName());
	    Map<String, String> result = new HashMap<>();
	    result.put("secretKey", secretKey);
	    result.put("qrCodeUrl", qrCodeUrl);
	    return result;
	}

	//通过一次性密码和密钥验证是否适配
	private boolean verifyTOTP(String secretKey, int oneTime) {
	    byte[] keyBytes = Base32.decode(secretKey);
		TOTP totp = new TOTP(keyBytes);
		return totp.validate(Instant.now(), 1, oneTime);
	}
}
