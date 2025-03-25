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

package com.jiang.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.cache.UserCache;
import com.jiang.mall.domain.entity.User;
import com.jiang.mall.domain.entity.VerificationCode;
import com.jiang.mall.domain.vo.UserAdminVo;
import com.jiang.mall.domain.vo.UserVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jiang
 * @since 2024年9月11日
 */
public interface IUserService extends IService<User> {

    Boolean login(String username, String password, String token, String clientIp, String fingerprint,String sessionId);

    Boolean modifyPassword(String oldPassword, String newPassword, String sessionId, String clientIp, String fingerprint);

    Boolean queryByUserName(String userName);

    Boolean queryByEmail(String email);

    List<UserAdminVo> getUserList(Integer pageNum, Integer pageSize);

    ResponseResult<Object> checkUserLogin(String sessionId);

    UserCache getUserFromRedis(String sessionId);

    void setUserToRedis(UserCache user);

    Long getUserNum();

	User getUserByUserNameOrEmail(String username);

    Boolean logout(String sessionId);

    Boolean validatePassword(Long userId, String password);

    Boolean modifyEmail(VerificationCode verificationCode, String sessionId, String clientIp, String fingerprint);

    Long register(VerificationCode verificationCode, String sessionId, String clientIp, String fingerprint);

    Boolean register(User user, String sessionId);

    Boolean forgot(VerificationCode verificationCode, String password, String clientIp, String fingerprint);

    Boolean lock(String sessionId, String clientIp, String fingerprint);

    Boolean lock(Long userId,String sessionId, String clientIp, String fingerprint);

    Boolean unlock(Long userId, String sessionId, String clientIp, String fingerprint);

    Boolean modifyInfo(User user, String sessionId);

    UserVo getUserById(Long userId);

    boolean getTotpStatus(String sessionId);

    String enableTotp(String sessionId);

    boolean disableTotp(String sessionId);

    boolean enableTotp(String sessionId, int code);
}
