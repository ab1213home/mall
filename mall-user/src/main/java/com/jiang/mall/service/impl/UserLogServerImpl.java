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
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.config.UserConfig;
import com.jiang.mall.dao.UserLogMapper;
import com.jiang.mall.domain.entity.User;
import com.jiang.mall.domain.entity.UserLog;
import com.jiang.mall.domain.enums.LogStatus;
import com.jiang.mall.service.IUserLogService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class UserLogServerImpl extends ServiceImpl<UserLogMapper, UserLog> implements IUserLogService {

	private UserLogMapper loginRecordMapper;

	@Autowired
	public void setLoginRecordMapper(UserLogMapper loginRecordMapper) {
		this.loginRecordMapper = loginRecordMapper;
	}

	private UserConfig userConfig;

	@Autowired
	public void setUserConfig(UserConfig userConfig) {
		this.userConfig = userConfig;
	}

	/**
	 * 计算用户尝试登录的次数
	 * 该方法用于计算给定用户在过去24小时内失败的登录尝试次数，以确定用户是否被锁定
	 *
	 * @param username    用户名，用于识别用户
	 * @param clientIp    客户端IP地址，用于识别登录尝试的来源
	 * @param fingerprint 设备指纹，用于进一步验证登录尝试的唯一性
	 * @return 返回用户的登录尝试次数如果超过最大尝试次数，返回最大尝试次数+1
	 */
	@Override
	public Integer countTryNumber(String username, String clientIp, String fingerprint) {
	    // 当前时间
	    Date now = new Date();
	    // 一天前的时间
	    Date yesterday = new Date(now.getTime() - 24 * 60 * 60 * 1000);

	    // 查询过去24小时内，用户名匹配且登录失败的记录
	    QueryWrapper<UserLog> queryWrapper_username = new QueryWrapper<>();
	    queryWrapper_username.eq("username",username);
	    queryWrapper_username.between("trigger_time", yesterday, now);
	    queryWrapper_username.eq("state", LogStatus.FAIL_LOGIN.getValue());
	    List<UserLog> list_username = loginRecordMapper.selectList(queryWrapper_username);

	    // 查询过去24小时内，IP地址匹配且登录失败的记录数量
	    QueryWrapper<UserLog> queryWrapper_ip = new QueryWrapper<>();
	    queryWrapper_ip.eq("ip",clientIp);
	    queryWrapper_ip.between("trigger_time", yesterday, now);
	    queryWrapper_ip.eq("state", LogStatus.FAIL_LOGIN.getValue());
	    Long list_ip = loginRecordMapper.selectCount(queryWrapper_ip);

	    // 查询过去24小时内，设备指纹匹配且登录失败的记录数量
	    QueryWrapper<UserLog> queryWrapper_fingerprint = new QueryWrapper<>();
	    queryWrapper_fingerprint.eq("fingerprint",fingerprint);
	    queryWrapper_fingerprint.between("trigger_time", yesterday, now);
	    queryWrapper_fingerprint.eq("state", LogStatus.FAIL_LOGIN.getValue());
	    Long list_fingerprint = loginRecordMapper.selectCount(queryWrapper_fingerprint);

	    // 如果设备指纹匹配的失败登录次数超过最大尝试次数的平方，返回最大尝试次数+1
	    if (list_fingerprint> (long) userConfig.getUserMaxTry() * userConfig.getUserMaxTry()){
	        return userConfig.getUserMaxTry()+1;
	    }
	    // 如果IP地址匹配的失败登录次数超过最大尝试次数的平方，返回最大尝试次数+1
	    if (list_ip> (long) userConfig.getUserMaxTry() * userConfig.getUserMaxTry()){
	        return userConfig.getUserMaxTry()+1;
	    }
	    // 如果用户名匹配的失败登录次数超过最大尝试次数的两倍，返回最大尝试次数+1
	    if (list_username.size()> userConfig.getUserMaxTry() * 2){
	        return userConfig.getUserMaxTry()+1;
	    }

	    // 计算加权失败次数
	    double count = 0;
	    for (UserLog userLog : list_username) {
	        // 如果设备指纹和IP地址都匹配，失败次数加1
	        if (Objects.equals(userLog.getFingerprint(), fingerprint) && Objects.equals(userLog.getIp(), clientIp)){
	            count=count+1;
	        }
	        // 如果仅IP地址匹配，失败次数加0.5
	        else if (Objects.equals(userLog.getIp(), clientIp)){
	            count=count+0.5;
	        }
	        // 如果仅设备指纹匹配，失败次数加0.3
	        else if (Objects.equals(userLog.getFingerprint(), fingerprint)){
	            count=count+0.3;
	        }
	    }
	    // 返回计算出的加权失败次数
	    return (int) count;
	}

	/**
	 * 创建成功的登录记录
	 *
	 * @param user        用户对象，包含用户的具体信息，此处标记为非空，确保用户信息已提供
	 * @param clientIp    客户端IP地址，记录登录者的IP地址
	 * @param fingerprint 用户指纹，唯一标识用户的设备信息
	 * @return 返回一个布尔值，表示登录记录是否成功插入到数据库中
	 */
	@Override
	public Boolean successLoginLog(@NotNull User user, String clientIp, String fingerprint) {
	    // 创建一个登录记录对象，包含用户ID、用户名、客户端IP、登录状态为成功和用户指纹
	    UserLog userLog = new UserLog(user.getId(), user.getUsername(), clientIp, LogStatus.SUCCESS_LOGIN.getValue(),fingerprint);
	    // 插入登录记录到数据库，如果插入成功返回true，否则返回false
	    return loginRecordMapper.insert(userLog)>0;
	}

	/**
	 * 记录失败的登录尝试
	 * <p>
	 * 此方法用于在用户登录失败时调用，以记录登录尝试的相关信息
	 * 它创建一个表示登录失败记录的UserRecord对象，并将其插入到数据库中
	 *
	 * @param username 用户名，用于标识尝试登录的用户
	 * @param clientIp 客户端IP地址，记录尝试登录的设备来源
	 * @param fingerprint 用户设备的唯一标识符，用于增强登录记录的准确性
	 * @return 返回一个布尔值，表示登录失败记录是否成功插入到数据库中
	 *         如果返回true，则表示记录成功；如果返回false，则表示记录失败
	 */
	@Override
	public Boolean failedLoginLog(String username, String clientIp, String fingerprint) {
	    // 创建一个UserRecord对象，表示登录失败的用户记录
	    UserLog userLog = new UserLog(username,clientIp,fingerprint, LogStatus.FAIL_LOGIN.getValue());
	    // 将登录失败记录插入数据库，并判断插入操作是否成功
	    return loginRecordMapper.insert(userLog)>0;
	}

	/**
	 * 成功注册用户记录
	 * <p>
	 * 此方法用于在用户成功注册后记录相关信息它创建一个用户记录对象，
	 * 包含用户ID、用户名、客户端IP、成功注册的状态值和用户指纹，
	 * 然后将此记录插入数据库中
	 *
	 * @param user 注册成功的用户对象，不能为空
	 * @param clientIp 客户端IP地址，用于记录用户注册的来源
	 * @param fingerprint 用户设备的唯一标识符，用于增强安全性
	 * @return 返回一个布尔值，表示记录是否成功插入数据库true表示成功，false表示失败
	 */
	@Override
	public Boolean successRegisterLog(@NotNull User user, String clientIp, String fingerprint) {
	    // 创建用户记录对象，包含用户ID、用户名、客户端IP、成功注册的状态值和用户指纹
	    UserLog userLog = new UserLog(user.getId(), user.getUsername(), clientIp, LogStatus.SUCCESS_REGISTER.getValue(),fingerprint);
	    // 将用户记录插入数据库，如果插入成功返回true，否则返回false
	    return loginRecordMapper.insert(userLog)>0;
	}

	@Override
	public Boolean successModifyEmailLog(@NotNull User user, String email, String clientIp, String fingerprint) {
		// 创建用户记录对象，包含用户ID、用户名、客户端IP、成功注册的状态值和用户指纹
	    UserLog userLog = new UserLog(user.getId(), user.getUsername(), clientIp, LogStatus.SUCCESS_MODIFY_EMAIL.getValue(),fingerprint);
	    // 将用户记录插入数据库，如果插入成功返回true，否则返回false
	    return loginRecordMapper.insert(userLog)>0;
	}

	@Override
	public Boolean failedModifyPasswordLog(Long user, String clientIp, String fingerprint) {
	    UserLog userLog = new UserLog(user, clientIp, LogStatus.FAIL_MODIFY_PASSWORD.getValue(),fingerprint);
	    // 将用户记录插入数据库，如果插入成功返回true，否则返回false
	    return loginRecordMapper.insert(userLog)>0;
	}

	@Override
	public Boolean successForgotLog(Long user, String clientIp, String fingerprint) {
		UserLog userLog = new UserLog(user, clientIp, LogStatus.FORGET_PASSWORD.getValue(),fingerprint);
	    // 将用户记录插入数据库，如果插入成功返回true，否则返回false
	    return loginRecordMapper.insert(userLog)>0;
	}

	@Override
	public Boolean successModifyPasswordLog(Long user, String clientIp, String fingerprint) {
		UserLog userLog = new UserLog(user, clientIp, LogStatus.SUCCESS_MODIFY_PASSWORD.getValue(),fingerprint);
	    // 将用户记录插入数据库，如果插入成功返回true，否则返回false
	    return loginRecordMapper.insert(userLog)>0;
	}

	@Override
	public Boolean successLockLog(Long user, String clientIp, String fingerprint) {
		UserLog userLog = new UserLog(user, clientIp, LogStatus.SUCCESS_LOCK.getValue(),fingerprint);
	    // 将用户记录插入数据库，如果插入成功返回true，否则返回false
	    return loginRecordMapper.insert(userLog)>0;
	}

	@Override
	public Boolean successLockAdminLog(Long user, String clientIp, String fingerprint) {
		UserLog userLog = new UserLog(user, clientIp, LogStatus.SUCCESS_ADMIN_LOCK.getValue(),fingerprint);
	    // 将用户记录插入数据库，如果插入成功返回true，否则返回false
	    return loginRecordMapper.insert(userLog)>0;
	}

	@Override
	public Boolean successUnlockAdminLog(Long user, String clientIp, String fingerprint) {
		UserLog userLog = new UserLog(user, clientIp, LogStatus.SUCCESS_UNLOCK.getValue(),fingerprint);
	    // 将用户记录插入数据库，如果插入成功返回true，否则返回false
	    return loginRecordMapper.insert(userLog)>0;
	}

}
