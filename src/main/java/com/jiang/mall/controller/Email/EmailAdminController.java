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
import com.jiang.mall.domain.vo.VerificationCodeVo;
import com.jiang.mall.service.IUserService;
import com.jiang.mall.service.IVerificationCodeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 邮箱管理员控制器
 * 负责处理与邮箱设置相关的管理员操作，如获取和设置邮箱配置，以及验证码管理
 * @author jiang
 * @version 1.0
 * @since 2024年9月20日
 */
@RestController
@RequestMapping("/email")
public class EmailAdminController {

    // 验证码服务接口，用于验证码的管理
    private IVerificationCodeService verificationCodeService;

    /**
     * 注入CodeService
     * 用于处理验证码相关操作
     * @param verificationCodeService 验证码服务实例
     */
    @Autowired
    public void setVerificationCodeService(IVerificationCodeService verificationCodeService) {
        this.verificationCodeService = verificationCodeService;
    }

    // 用户服务接口，用于用户信息的管理
    private IUserService userService;

    /**
     * 注入UserService
     * 用于处理用户信息相关操作
     * @param userService UserService实例
     */
    @Autowired
    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

    /**
     * 获取邮箱设置信息
     * 此方法首先检查用户是否已登录，然后创建并填充EmailSettingVo对象，用于展示邮箱设置信息
     * 为了安全起见，密码字段被设置为"******"
     * @param session 当前会话
     * @return 包含邮箱设置信息的响应结果
     */
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

    /**
     * 设置邮箱配置信息
     * 此方法首先检查用户是否已登录，然后根据EmailSettingVo对象中的信息更新邮箱配置，并保存这些配置
     * @param emailSettingVo 包含新的邮箱设置信息的对象
     * @param session 当前会话
     * @return 表示设置操作结果的响应结果
     */
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

    /**
     * 获取验证码列表
     * 此方法首先检查用户是否已登录，然后根据指定的页码和页面大小获取验证码列表
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @param session 当前会话
     * @return 包含验证码列表的响应结果
     */
    @GetMapping("/getList")
    public ResponseResult<Object> getList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                          @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
                                          HttpSession session) {
        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult<Object> result = userService.checkAdminUser(session);
		if (!result.isSuccess()) {
			// 如果未登录，则直接返回
		    return result;
		}
        List<VerificationCodeVo> list = verificationCodeService.getList(pageNum, pageSize);
        return ResponseResult.okResult(list);
    }

    /**
     * 获取验证码数量
     * 此方法首先检查用户是否已登录，然后获取当前验证码的数量
     * @param session 当前会话
     * @return 包含验证码数量的响应结果
     */
    @GetMapping("/getNum")
    public ResponseResult<Object> getNum(HttpSession session) {
        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult<Object> result = userService.checkAdminUser(session);
		if (!result.isSuccess()) {
			// 如果未登录，则直接返回
		    return result;
		}
        return ResponseResult.okResult(verificationCodeService.getVerificationCodeNum());
    }
}
