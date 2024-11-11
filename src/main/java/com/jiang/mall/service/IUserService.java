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

    User login(String username, String password);

    Boolean modifyPassword(Integer userId, String oldPassword, String newPassword);

    User getUserInfo(Long userId);

    Boolean modifyUserInfo(User newUser);

    Boolean lockUser(Long userId);

    Boolean queryByUserName(String userName);

    Boolean queryByEmail(String email);

    Long registerStep(User user);

    List<UserVo> getUserList(Integer pageNum, Integer pageSize, Integer userId);

    Boolean updateUser(User user);

	Boolean unlockUser(Long userId);

    ResponseResult hasPermission(Long oldUserId, HttpSession session);

    ResponseResult checkAdminUser(HttpSession session);

    ResponseResult checkUserLogin(HttpSession session);

    Long getUserNum();

	User getUserByUserNameOrEmail(String username);

    Boolean modifyPassword(Long userId, String newPassword);
}
