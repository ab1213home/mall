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

package com.jiang.mall.intercepter;

import com.jiang.mall.domain.cache.UserCache;
import com.jiang.mall.service.II18nService;
import com.jiang.mall.service.IUserRedisService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Deprecated
public class RepeatLoginInterceptor implements HandlerInterceptor {

    private IUserRedisService redisService;

    @Autowired
    public void setRedisService(IUserRedisService redisService) {
        this.redisService = redisService;
    }

    private II18nService i18nService;

    @Autowired
    public void setI18nService(II18nService i18nService) {
        this.i18nService = i18nService;
    }

    private GeneralInterceptor generalInterceptor;

    @Autowired
    public void setGeneralInterceptor(GeneralInterceptor generalInterceptor) {
        this.generalInterceptor = generalInterceptor;
    }

    /**
     * 在请求处理之前进行预处理
     *
     * @param request  HTTP请求对象，用于获取请求信息
     * @param response HTTP响应对象，用于发送响应信息
     * @param o        处理请求的处理器，通常是一个控制器方法
     * @return boolean 返回值决定是否继续执行其他拦截器和当前请求的处理器方法
     *                 如果返回true，表示继续执行；如果返回false，表示中断执行
     * <p>
     * 此方法主要用于检查用户是否已经登录如果用户已经登录，将直接重定向到用户首页，
     * 以避免未授权的访问此拦截器对所有请求生效，但只对未登录的用户进行重定向操作
     */
    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object o) throws Exception {
        //获取token
        String token = request.getHeader("Authorization");
        if (i18nService.checkString(token)){
            UserCache user = redisService.getUserByToken(token);
            if (!checkLogin(user)){
                return true;
            }else {
                generalInterceptor.redirectToUserIndexBecauseRepeated(request, response);
                return false;
            }
        } else {
            UserCache user = redisService.getUserBySessionId(request.getSession().getId());
            if (!checkLogin(user)){
                return true;
            }else {
                generalInterceptor.redirectToUserIndexBecauseRepeated(request, response);
                return false;
            }
        }
    }

    /**
     * 检查用户登录状态
     *
     * @param user 用户缓存对象，用于检查用户是否已登录
     * @return 如果用户存在且用户ID不为空，则返回true，表示用户已登录；否则返回false
     */
    private boolean checkLogin(UserCache user){
        // 检查传入的用户对象是否为空
        if (user == null){
            // 如果用户对象为空，则返回false，表示未登录
            return false;
        }else{
            // 如果用户对象不为空，进一步检查用户ID是否为空
            return user.getId() != null;
        }
    }
}