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

import com.jiang.mall.annotation.Permission;
import com.jiang.mall.config.EmailConfig;
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.enums.PermissionType;
import com.jiang.mall.domain.vo.EmailSettingVo;
import com.jiang.mall.domain.vo.VerificationCodeVo;
import com.jiang.mall.service.IVerificationCodeService;
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
@RequestMapping("/email/admin")
public class EmailAdminController {

    private IVerificationCodeService verificationCodeService;

    @Autowired
    public void setVerificationCodeService(IVerificationCodeService verificationCodeService) {
        this.verificationCodeService = verificationCodeService;
    }

    private EmailConfig emailConfig;

	@Autowired
	public void setEmailConfig(EmailConfig emailConfig) {
		this.emailConfig = emailConfig;
	}

    /**
     * 获取邮箱设置信息
     * 此方法首先检查用户是否已登录，然后创建并填充EmailSettingVo对象，用于展示邮箱设置信息
     * 为了安全起见，密码字段被设置为"******"
     * @return 包含邮箱设置信息的响应结果
     */
    @GetMapping("/getSetting")
    @Permission(type = PermissionType.SYSTEM, value = "system:email")
    public ResponseResult<Object> getSetting() {
        return ResponseResult.okResult(emailConfig.getSetting());
    }

    /**
     * 设置邮箱配置信息
     * 此方法首先检查用户是否已登录，然后根据EmailSettingVo对象中的信息更新邮箱配置，并保存这些配置
     * @param emailSettingVo 包含新的邮箱设置信息的对象
     * @return 表示设置操作结果的响应结果
     */
    @PostMapping("/setSetting")
    @Permission(type = PermissionType.SYSTEM, value = "system:email")
    public ResponseResult<Object> setSetting(@RequestBody EmailSettingVo emailSettingVo) {
        // 更新配置文件
        emailConfig.updateSetting(emailSettingVo);
        return ResponseResult.okResult();
    }

    /**
     * 获取验证码列表
     * 此方法首先检查用户是否已登录，然后根据指定的页码和页面大小获取验证码列表
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @return 包含验证码列表的响应结果
     */
    @GetMapping("/getList")
    @Permission(type = PermissionType.SYSTEM, value = "email:list")
    public ResponseResult<Object> getList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                          @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        List<VerificationCodeVo> list = verificationCodeService.getList(pageNum, pageSize);
        return ResponseResult.okResult(list);
    }

    /**
     * 获取验证码数量
     * 此方法首先检查用户是否已登录，然后获取当前验证码的数量
     * @return 包含验证码数量的响应结果
     */
    @GetMapping("/getNum")
    @Permission(type = PermissionType.SYSTEM, value = "email:list")
    public ResponseResult<Object> getNum() {
        return ResponseResult.okResult(verificationCodeService.getVerificationCodeNum());
    }
}
