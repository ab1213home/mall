package com.jiang.mall.config;

import com.jiang.mall.intercepter.*;
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
    private RegistrationInterceptor registrationInterceptor;
    @Autowired
    private UserLoginInterceptor userLoginIntercepter;
    @Autowired
    private CheckoutInterceptor checkoutInterceptor;
    /**
     * 重写addInterceptors方法，用于添加拦截器
     *
     * @param registry InterceptorRegistry对象，用于注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminLoginInterceptor)
                .addPathPatterns("/admin.html")
                .addPathPatterns("/admin/**");
        registry.addInterceptor(registrationInterceptor)
                .addPathPatterns("/user/register_step1")
                .addPathPatterns("/user/register_step1.html")
                .addPathPatterns("/user/register_step2")
                .addPathPatterns("/user/register_step2.html")
                .addPathPatterns("/user/register");
        registry.addInterceptor(userLoginIntercepter)
                .addPathPatterns("/user/index")
                .addPathPatterns("/user/index.html")
                .addPathPatterns("/user/")
                .addPathPatterns("/cart")
                .addPathPatterns("/cart.html")
                .addPathPatterns("/orders")
                .addPathPatterns("/orders.html")
                .addPathPatterns("/checkout.html")
                .addPathPatterns("/checkout")
                .addPathPatterns("/checkout.html")
                .addPathPatterns("/user/modify/info")
                .addPathPatterns("/user/modify/info.html")
                .addPathPatterns("/user/modify/password")
                .addPathPatterns("/user/modify/password.html")
                .addPathPatterns("/user/modify/address")
                .addPathPatterns("/user/modify/address.html")
                .addPathPatterns("/collections")
                .addPathPatterns("/collections.html")
                .addPathPatterns("/user/index.html");
//        registry.addInterceptor(checkoutInterceptor)
//                .addPathPatterns("/checkout.html");
    }
}
