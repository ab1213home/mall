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
import com.jiang.mall.domain.entity.User;
import com.jiang.mall.domain.entity.VerificationCode;
import com.jiang.mall.domain.vo.UserVo;
import jakarta.servlet.http.HttpSession;

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

    Boolean login(String username, String password, String clientIp, String fingerprint,String sessionId);

    Boolean modifyPassword(Long userId, String oldPassword, String newPassword, String sessionId, String clientIp, String fingerprint);

    User getUserInfo(Long userId);

    Boolean modifyUserInfo(User newUser);

    Boolean lockUser(Long userId);

    Boolean queryByUserName(String userName);

    Boolean queryByEmail(String email);

    Long register(User user, VerificationCode verificationCode, String sessionId, String clientIp, String fingerprint);

    List<UserVo> getUserList(Integer pageNum, Integer pageSize, Long userId);

    Boolean updateUser(User user);

	Boolean unlockUser(Long userId);

    ResponseResult<Object> hasPermission(Long oldUserId, HttpSession session);

    ResponseResult<Object> checkAdminUser(String sessionId);

    ResponseResult<Object> checkUserLogin(String sessionId);

    Long getUserNum();

	User getUserByUserNameOrEmail(String username);

    Boolean modifyPassword(Long userId, String newPassword, VerificationCode verificationCode, String clientIp, String fingerprint);

    Boolean logout(String sessionId);

    Boolean validatePassword(Long userId, String password);

    Boolean modifyEmail(Long userId, String email, VerificationCode verificationCode, String sessionId, String clientIp, String fingerprint);
}
