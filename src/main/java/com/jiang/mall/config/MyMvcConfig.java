package com.jiang.mall.config;

import com.jiang.mall.intercepter.AdminLoginInterceptor;
import com.jiang.mall.intercepter.LoginIntercepter;
import com.jiang.mall.intercepter.RegistrationIntercepter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static com.jiang.mall.controller.CommonController.FILE_UPLOAD_PATH;

@Configuration
public class MyMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AdminLoginInterceptor adminLoginInterceptor;
    @Autowired
    private RegistrationIntercepter registrationIntercepter;
    /**
     * 重写addInterceptors方法，用于添加拦截器
     *
     * @param registry InterceptorRegistry对象，用于注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加登录拦截器，拦截所有以"/admin/"开头的请求
        registry.addInterceptor(new LoginIntercepter())
                //.addPathPatterns("/admin/**")
                .addPathPatterns("**/admin/**");
                //.addPathPatterns("/user/**");
        registry.addInterceptor(adminLoginInterceptor)
                .addPathPatterns("/admin/**");
//        registry.addInterceptor(registrationIntercepter)
//                .addPathPatterns("/user/register_step2.html");
    }

    /**
     * 重写addResourceHandlers方法，用于处理静态资源访问
     * 该方法主要用于配置静态资源（如图片、CSS、JavaScript文件等）的访问路径
     *
     * @param registry 资源处理器注册对象，用于注册静态资源路径
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将文件上传路径映射到/upload/**，使上传的文件可以通过该路径进行访问
        registry.addResourceHandler("/upload/**").addResourceLocations("file:" + FILE_UPLOAD_PATH);
    }

}
