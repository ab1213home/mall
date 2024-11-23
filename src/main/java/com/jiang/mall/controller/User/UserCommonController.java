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

package com.jiang.mall.controller.User;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.vo.UserVo;
import com.jiang.mall.service.IUserService;
import com.jiang.mall.service.Redis.IUserRedisService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@RestController
@RequestMapping("/user")
public class UserCommonController {

    private IUserService userService;

    /**
     * 设置用户服务实例
     *
     * @param userService 用户服务实例
     */
    @Autowired
    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

    private IUserRedisService userRedisService;

    @Autowired
    public void setUserRedisService(IUserRedisService userRedisService) {
        this.userRedisService = userRedisService;
    }

    /**
     * 获取距离下一次生日的天数
     *
     * @param session HttpSession对象，用于获取用户 session 信息
     * @return ResponseResult包含距离下一次生日的天数或错误信息
     *
     * 本方法首先检查用户是否设置了生日，
     * 如果没有设置或者设置的值是"null"字符串，则返回失败结果并提示用户未设置生日。
     * 如果生日已设置，则计算距离下一次生日的天数，并返回成功结果。
     */
    @GetMapping("/getDays")
    public ResponseResult<Object> getDaysNextBirthday(HttpSession session){
        ResponseResult<Object> result = userService.checkUserLogin(session);
		if (!result.isSuccess()) {
		    // 如果未登录，则直接返回
		    return result;
		}

        UserVo user = (UserVo) session.getAttribute("User");

        // 检查session中是否设置了用户生日
        if (user.getBirthDate() == null){
            return ResponseResult.failResult("未设置生日！");
        }
        // 计算并返回距离下一次生日的天数
        return ResponseResult.okResult(user.getNextBirthday());
    }

}
