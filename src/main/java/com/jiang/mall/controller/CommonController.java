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
import com.jiang.mall.service.IRedisService;
import com.jiang.mall.service.IUserService;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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

    private IRedisService redisService;

    @Autowired
    public void setRedisService(IRedisService redisService) {
        this.redisService = redisService;
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
    public void generateCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 禁止缓存响应数据，确保不同浏览器或缓存服务器下验证码图像不会被缓存
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        // 设置响应内容类型为PNG图像，告知浏览器将接收的数据显示为图像
        response.setContentType("image/png");
        // 创建一个自定义的验证码对象，参数分别为宽度、高度和字符数
        SpecCaptcha captcha = new SpecCaptcha(160, 40, 4);
        // 设置验证码字符类型为纯数字，增加用户辨识的易用性
        captcha.setCharType(Captcha.TYPE_ONLY_NUMBER);
        // 以下代码行被注释掉，因此没有设置自定义字体
        // captcha.setFont(Captcha.FONT_8, 40);
        // 将生成的验证码文本存储在session中，以便后续表单提交时验证
        redisService.setString(request.getSession().getId() , captcha.text().toLowerCase());
        redisService.expire(request.getSession().getId(), 60*5);
//        request.getSession().setAttribute("captcha", captcha.text().toLowerCase());
        // 将验证码图像输出到HTTP响应中，实现浏览器展示验证码图像
        captcha.out(response.getOutputStream());
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