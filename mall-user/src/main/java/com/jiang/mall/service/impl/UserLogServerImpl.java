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

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.config.UserConfig;
import com.jiang.mall.dao.UserLogMapper;
import com.jiang.mall.domain.entity.UserLog;
import com.jiang.mall.domain.enums.UserStatus;
import com.jiang.mall.mq.UserLogProducer;
import com.jiang.mall.service.IUserLogService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class UserLogServerImpl extends ServiceImpl<UserLogMapper, UserLog> implements IUserLogService {

	private static final Logger logger = LoggerFactory.getLogger(UserLogServerImpl.class);

	private UserLogMapper userLogMapper;

	@Autowired
	public void setUserLogMapper(UserLogMapper userLogMapper) {
		this.userLogMapper = userLogMapper;
	}

	private UserConfig userConfig;

	@Autowired
	public void setUserConfig(UserConfig userConfig) {
		this.userConfig = userConfig;
	}

	private UserLogProducer producer;

	@Autowired
	public void setProducer(UserLogProducer producer) {
		this.producer = producer;
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
	    queryWrapper_username.eq("state", UserStatus.FAIL_LOGIN.getValue());
	    List<UserLog> list_username = userLogMapper.selectList(queryWrapper_username);

	    // 查询过去24小时内，IP地址匹配且登录失败的记录数量
	    QueryWrapper<UserLog> queryWrapper_ip = new QueryWrapper<>();
	    queryWrapper_ip.eq("ip",clientIp);
	    queryWrapper_ip.between("trigger_time", yesterday, now);
	    queryWrapper_ip.eq("state", UserStatus.FAIL_LOGIN.getValue());
	    Long list_ip = userLogMapper.selectCount(queryWrapper_ip);

	    // 查询过去24小时内，设备指纹匹配且登录失败的记录数量
	    QueryWrapper<UserLog> queryWrapper_fingerprint = new QueryWrapper<>();
	    queryWrapper_fingerprint.eq("fingerprint",fingerprint);
	    queryWrapper_fingerprint.between("trigger_time", yesterday, now);
	    queryWrapper_fingerprint.eq("state", UserStatus.FAIL_LOGIN.getValue());
	    Long list_fingerprint = userLogMapper.selectCount(queryWrapper_fingerprint);

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
	 * 记录用户日志的默认实现方法
	 * <p>
	 * 该方法用于记录用户的操作日志，包括用户名、客户端IP、指纹信息、日志状态以及附加属性
	 * 它通过创建一个UserLog对象，填充这些信息，并将其插入到数据库中来实现日志记录的功能
	 *
	 * @param username 用户名，标识操作的用户
	 * @param clientIp 客户端IP地址，记录操作的来源
	 * @param fingerprint 指纹信息，用于唯一标识用户的设备或会话
	 * @param status 日志状态，表示日志的类型或结果，不能为空
	 * @param properties 附加属性，以键值对形式记录额外的信息，可能为null
	 * @return 返回日志记录是否成功，如果插入数据库成功则返回true，否则返回false
	 */
	@Override
	public boolean defaultLog(String username, String clientIp, String fingerprint, @NotNull UserStatus status, Map<String, Object> properties) {
	    // 创建UserLog对象以记录用户操作日志
	    UserLog userLog = new UserLog();
	    // 设置日志中的用户名
	    userLog.setUsername(username);
	    // 设置日志中的客户端IP地址
	    userLog.setIp(clientIp);
	    // 设置日志中的指纹信息
	    userLog.setFingerprint(fingerprint);
	    // 设置日志的状态，使用LogStatus的getValue方法获取状态值
	    userLog.setState(status.getValue());
	    // 如果附加属性不为空，则将其转换为JSON字符串并设置到日志中
	    if (properties!=null){
	        userLog.setProperties(JSON.toJSONString(properties));
	    }
	    // 将日志对象插入数据库，如果插入成功则返回true，否则返回false
	    return userLogMapper.insert(userLog) > 0;
	}

	@Override
	public void defaultLogToMq(String username, String clientIp, String fingerprint, @NotNull UserStatus status, Map<String, Object> properties) {
		// 创建UserLog对象以记录用户操作日志
	    UserLog userLog = new UserLog();
	    // 设置日志中的用户名
	    userLog.setUsername(username);
	    // 设置日志中的客户端IP地址
	    userLog.setIp(clientIp);
	    // 设置日志中的指纹信息
	    userLog.setFingerprint(fingerprint);
	    // 设置日志的状态，使用LogStatus的getValue方法获取状态值
	    userLog.setState(status.getValue());
	    // 如果附加属性不为空，则将其转换为JSON字符串并设置到日志中
	    if (properties!=null){
	        userLog.setProperties(JSON.toJSONString(properties));
	    }
		producer.sendUserLog(userLog);
	}

	@Override
	public void oauthLoginLog(String username, @NotNull UserStatus status) {
		UserLog userLog = new UserLog();
		// 设置日志中的用户名
	    userLog.setUsername(username);
	    // 设置日志中的客户端IP地址
	    userLog.setIp("127.0.0.1");
	    // 设置日志中的指纹信息
	    userLog.setFingerprint("fingerprint");
	    // 设置日志的状态，使用LogStatus的getValue方法获取状态值
	    userLog.setState(status.getValue());
	    // 将日志对象插入数据库，如果插入成功则返回true，否则返回false
	    userLogMapper.insert(userLog);
	}

}
