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
import com.jiang.mall.domain.config.User;
import com.jiang.mall.domain.entity.Config;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;



/**
 * 公共控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@Controller
@RequestMapping("/common")
public class CommonController {

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
    @GetMapping("/getSalt")
    @ResponseBody
    public ResponseResult<Object> getSalt(HttpSession session) {
        return ResponseResult.okResult(User.AES_SALT,"获取随机盐值");
    }

    @GetMapping("/getFooter")
    @ResponseBody
    public ResponseResult<Object> getFooter(HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        map.put("phone", Config.phone);
        map.put("email", Config.email);
        return ResponseResult.okResult(map);
    }

}