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

package com.jiang.mall.config;

import com.jiang.mall.intercepter.UploadAllowedInterceptor;
import com.jiang.mall.intercepter.UserInterceptor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FileMvcConfig implements WebMvcConfigurer {

    private UploadAllowedInterceptor uploadAllowedInterceptor;

    @Autowired
    public void setUploadAllowedInterceptor(UploadAllowedInterceptor uploadAllowedInterceptor) {
        this.uploadAllowedInterceptor = uploadAllowedInterceptor;
    }

    private UserInterceptor userInterceptor;

    @Autowired
    public void setUserLoginInterceptor(UserInterceptor userInterceptor) {
        this.userInterceptor = userInterceptor;
    }


    /**
     * 重写addInterceptors方法，用于添加拦截器
     *
     * @param registry InterceptorRegistry对象，用于注册拦截器
     */
    @Override
    public void addInterceptors(@NotNull InterceptorRegistry registry) {
        registry.addInterceptor(uploadAllowedInterceptor)
                .addPathPatterns("/common/uploadFile?*")
                .addPathPatterns("/common/uploadFile")
                .addPathPatterns("/common/uploadFaces?*")
                .addPathPatterns("/common/uploadFaces");
        registry.addInterceptor(userInterceptor)
                .addPathPatterns("/common/uploadFile?*")
                .addPathPatterns("/common/uploadFile")
                .addPathPatterns("/common/uploadFaces?*")
                .addPathPatterns("/common/uploadFaces");
    }
}
