package com.jiang.mall.config;

import com.jiang.mall.intercepter.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyMvcConfig implements WebMvcConfigurer {

    private AdminLoginInterceptor adminLoginInterceptor;

    @Autowired
    public void setAdminLoginInterceptor(AdminLoginInterceptor adminLoginInterceptor) {
        this.adminLoginInterceptor = adminLoginInterceptor;
    }

    private AboutInterceptor aboutInterceptor;

    @Autowired
    public void setAboutInterceptor(AboutInterceptor aboutInterceptor) {
        this.aboutInterceptor = aboutInterceptor;
    }

    private UserLoginInterceptor userLoginIntercepter;
    @Autowired
    public void setUserLoginIntercepter(UserLoginInterceptor userLoginIntercepter) {
        this.userLoginIntercepter = userLoginIntercepter;
    }

    private CheckoutInterceptor checkoutInterceptor;

    @Autowired
    public void setCheckoutInterceptor(CheckoutInterceptor checkoutInterceptor) {
        this.checkoutInterceptor = checkoutInterceptor;
    }

    private ApiLoginInterceptor apiLoginInterceptor;

    @Autowired
    public void setApiLoginInterceptor(ApiLoginInterceptor apiLoginInterceptor) {
        this.apiLoginInterceptor = apiLoginInterceptor;
    }

    /**
     * 重写addInterceptors方法，用于添加拦截器
     *
     * @param registry InterceptorRegistry对象，用于注册拦截器
     */
    @Override
    public void addInterceptors(@NotNull InterceptorRegistry registry) {
        registry.addInterceptor(adminLoginInterceptor)
                .addPathPatterns("/admin.html")
                .addPathPatterns("/admin/**");
        registry.addInterceptor(aboutInterceptor)
                .addPathPatterns("/about.html")
                .addPathPatterns("/about");
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
                .addPathPatterns("/about")
                .addPathPatterns("/about.html")
                .addPathPatterns("/user/index.html");
        registry.addInterceptor(checkoutInterceptor)
                .addPathPatterns("/checkout.html");
        registry.addInterceptor(apiLoginInterceptor)
                .addPathPatterns("/api/**");
    }
}
