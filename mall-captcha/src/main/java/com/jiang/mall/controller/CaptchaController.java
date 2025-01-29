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

import com.jiang.mall.service.ICaptchaService;
import com.wf.captcha.SpecCaptcha;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping("/common")
public class CaptchaController {

    private static final Logger logger = LoggerFactory.getLogger(CaptchaController.class);

    @Qualifier("CaptchaRedis")
    private ICaptchaService captchaService;

    @Autowired
    public void setCaptchaService(ICaptchaService captchaService) {
        this.captchaService = captchaService;
    }

	/**
     * 生成验证码并作为响应返回
     * 该方法通过HttpServletRequest和HttpServletResponse对象进行操作，生成并返回一个验证码图像
     * 验证码文本被存储在用户会话中，以便后续验证使用
     *
     * @param request 用于获取请求信息，以便设置session属性
     * @param response 用于设置响应头，内容类型，并输出验证码图像
     * @throws IOException 如果在写入响应体过程中发生I/O错误
     */
    @RequestMapping("/captcha")
    public void generateCaptcha(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
        // 禁止缓存响应数据，确保不同浏览器或缓存服务器下验证码图像不会被缓存
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        // 设置响应内容类型为PNG图像，告知浏览器将接收的数据显示为图像
        response.setContentType("image/png");
        // 创建一个自定义的验证码对象
        SpecCaptcha captcha = captchaService.generateCaptcha(request.getSession().getId());
        // 将验证码图像输出到HTTP响应中，实现浏览器展示验证码图像
        try (OutputStream out = response.getOutputStream()) {
        captcha.out(out);
        } catch (IOException e) {
            // 处理异常情况
            logger.error("Failed to generate captcha image", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to generate captcha image");
        }
    }
}
