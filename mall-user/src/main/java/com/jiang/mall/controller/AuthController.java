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

import cn.hutool.core.lang.UUID;
import com.jiang.mall.annotation.Permission;
import com.jiang.mall.config.GeneralConfig;
import com.jiang.mall.config.UserConfig;
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.enums.OAuthProvider;
import com.jiang.mall.domain.enums.OAuthResult;
import com.jiang.mall.domain.enums.PermissionType;
import com.jiang.mall.service.II18nService;
import com.jiang.mall.service.IOAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/user/oauth2")
public class AuthController {

	private IOAuthService oAuthService;

	@Autowired
	public void setOAuthService(IOAuthService oAuthService) {
		this.oAuthService = oAuthService;
	}

	private GeneralConfig generalConfig;

	@Autowired
	public void setGeneralConfig(GeneralConfig generalConfig) {
		this.generalConfig = generalConfig;
	}

	private UserConfig userConfig;

	@Autowired
	public void setUserConfig(UserConfig userConfig) {
		this.userConfig = userConfig;
	}

	private II18nService i18nService;

	@Autowired
	public void setI18nService(II18nService i18nService) {
		this.i18nService = i18nService;
	}

	@GetMapping("/getList")
	@Permission(PermissionType.NONE)
	public ResponseResult<Object> getList(){
		return ResponseResult.okResult();
	}

	@GetMapping("/login/{type}")
	@Permission(PermissionType.NONE)
	public void AuthLogin(HttpServletResponse response, @PathVariable("type") String type) throws IOException {
        String url = oAuthService.getAuthUrl(type);
        response.sendRedirect(url);
    }

	// 处理回调获取code
    @GetMapping("/callback/gitee/code")
    @Permission(PermissionType.NONE)
    public void callback(@RequestParam String code, HttpServletRequest request, HttpServletResponse response) throws IOException {
		String token = UUID.fastUUID().toString();
		OAuthResult flag = oAuthService.callback(code,token,request.getSession().getId(),OAuthProvider.GITEE);
		if (flag==OAuthResult.ERROR) {
			//重定向到登录界面
			response.setHeader("Location", request.getContextPath() + "/user/login.html");
			response.setContentType("text/html; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
			String messageParam = URLEncoder.encode("Gitee账号信息获取失败", StandardCharsets.UTF_8);
	        // 将编码后的重定向URL和提示信息拼接，执行重定向
	        response.sendRedirect(request.getContextPath() + "/user/login.html"+ "?message=" + messageParam);
		} else if (flag==OAuthResult.UNBOUND){
			response.setContentType("text/html; charset=UTF-8");
		}else if (flag==OAuthResult.SECOND_VERIFY) {
			response.setContentType("text/html; charset=UTF-8");
//			return ResponseResult.okResult("false","需要二次验证");
		} else if (flag==OAuthResult.SUCCESS){
			response.setContentType("text/html; charset=UTF-8");
//			return ResponseResult.okResult(token,i18nService.getMessage("user.login.success"));
		}
    }
}
