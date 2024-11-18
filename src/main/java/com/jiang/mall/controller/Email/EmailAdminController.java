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

package com.jiang.mall.controller.Email;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.config.Email;
import com.jiang.mall.domain.vo.EmailSettingVo;
import com.jiang.mall.service.IUserService;
import com.jiang.mall.service.IVerificationCodeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 邮箱验证码控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月20日
 */
@RestController
@RequestMapping("/email")
public class EmailAdminController {

    private IVerificationCodeService verificationCodeService;

    /**
     * 注入CodeService
     *
     * @param verificationCodeService 验证码服务实例
     */
    @Autowired
    public void setVerificationCodeService(IVerificationCodeService verificationCodeService) {
        this.verificationCodeService = verificationCodeService;
    }

    private IUserService userService;

    /**
     * 注入UserService
     *
     * @param userService UserService
     */
    @Autowired
    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/getSetting")
    public ResponseResult<Object> getSetting(HttpSession session) {
        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult<Object> result = userService.checkAdminUser(session);
        if (!result.isSuccess()) {
            return result;
        }
        EmailSettingVo emailSettingVo = new EmailSettingVo();
        emailSettingVo.setAllowSendEmail(Email.AllowSendEmail);
        emailSettingVo.setHost(Email.HOST);
        emailSettingVo.setPort(Email.PORT);
        emailSettingVo.setUsername(Email.USERNAME);
        emailSettingVo.setSender_end(Email.SENDER_END);
        emailSettingVo.setNickname(Email.NICKNAME);
        emailSettingVo.setPassword("******");
        emailSettingVo.setExpiration_time(Email.expiration_time);
        emailSettingVo.setMax_request_num(Email.max_request_num);
        emailSettingVo.setMin_request_num(Email.min_request_num);
        emailSettingVo.setMax_fail_rate(Email.max_fail_rate);
        return ResponseResult.okResult(emailSettingVo);
    }

    @PostMapping("/setSetting")
    public ResponseResult<Object> setSetting(@RequestBody EmailSettingVo emailSettingVo,
                                             HttpSession session) {
        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult<Object> result = userService.checkAdminUser(session);
        // 如果用户未登录，则直接返回
        if (!result.isSuccess()) {
            return result;
        }
        // 更新配置文件
        Email.HOST = emailSettingVo.getHost();
        Email.PORT = emailSettingVo.getPort();
        Email.USERNAME = emailSettingVo.getUsername();
        Email.SENDER_END = emailSettingVo.getSender_end();
        Email.NICKNAME = emailSettingVo.getNickname();
        Email.PASSWORD = emailSettingVo.getPassword();
        Email.expiration_time = emailSettingVo.getExpiration_time();
        Email.max_request_num = emailSettingVo.getMax_request_num();
        Email.min_request_num = emailSettingVo.getMin_request_num();
        Email.max_fail_rate = emailSettingVo.getMax_fail_rate();
        Email.saveProperties();
        return ResponseResult.okResult();
    }
}
//  "data": {
//    "host": "smtp.126.com",
//    "port": "465",
//    "username": "jiangrongjun2004@126.com",
//    "sender_end": "mall.com",
//    "nickname": "jiangrongjun",
//    "password": "******",
//    "expiration_time": 15,
//    "max_request_num": 10,
//    "min_request_num": 9,
//    "max_fail_rate": 0.4,
//    "allowSendEmail": true
//  },