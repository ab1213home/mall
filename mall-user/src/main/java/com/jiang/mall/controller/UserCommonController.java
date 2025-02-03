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

package com.jiang.mall.controller;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.vo.UserVo;
import com.jiang.mall.service.IUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.jiang.mall.config.UserConfig.AES_SALT;

/**
 * 用户控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@RestController
@RequestMapping
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
    @GetMapping("/user/getDays")
    public ResponseResult<Object> getDaysNextBirthday(HttpSession session){
        UserVo user = (UserVo) userService.checkUserLogin(session.getId()).getData();
        // 检查session中是否设置了用户生日
        if (user.getBirthDate() == null){
            return ResponseResult.failResult("未设置生日！");
        }
        // 计算并返回距离下一次生日的天数
        return ResponseResult.okResult(user.getNextBirthday());
    }

    /**
     * 获取随机盐值
     * <p>
     * 本方法主要用于向客户端返回一个用于加密的随机盐值（AES_SALT），
     * 盐值在加密过程中与密码结合使用，增加加密的安全性。
     * 方法通过HttpSession参数接收会话信息，但实际上并未使用该参数，
     * 因为盐值是固定配置好的，并不需要会话状态来决定。
     *
     * @param session HttpSession对象，用于管理用户会话，本方法中未使用该参数
     * @return 返回一个ResponseResult对象，其中包含状态码和盐值，
     *         状态码表示请求处理的结果，盐值为配置好的固定值。
     */
    @GetMapping("/common/getSalt")
    public ResponseResult<Object> getSalt(HttpSession session) {
        return ResponseResult.okResult(AES_SALT,"获取随机盐值");
    }

}
