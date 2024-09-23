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
        // 添加登录拦截器，拦截所有以"/admin/"开头的请求
        registry.addInterceptor(new LoginInterceptor())
                //.addPathPatterns("/admin/**")
                .addPathPatterns("**/admin/**");
                //.addPathPatterns("/user/**");
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

    /**
     * 重写addResourceHandlers方法，用于处理静态资源访问
     * 该方法主要用于配置静态资源（如图片、CSS、JavaScript文件等）的访问路径
     *
     * @param registry 资源处理器注册对象，用于注册静态资源路径
     */
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        // 将文件上传路径映射到/upload/**，使上传的文件可以通过该路径进行访问
//        registry.addResourceHandler("/upload/**").addResourceLocations("file:" + FILE_UPLOAD_PATH);
//    }

}
