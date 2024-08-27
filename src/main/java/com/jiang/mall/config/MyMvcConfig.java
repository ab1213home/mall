package com.jiang.mall.config;

import com.jiang.mall.intercepter.LoginIntercepter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static com.jiang.mall.controller.CommonController.FILE_UPLOAD_PATH;

@Configuration
public class MyMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginIntercepter())
//        调用InterceptorRegistry的addInterceptor方法，创建一个新的LoginIntercepter实例，并将其添加到拦截器注册表中。
                .addPathPatterns("/*/admin/**");
//        调用addPathPatterns方法，为拦截器指定为所有以/admin开头的URL路径
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**").addResourceLocations("file:" + FILE_UPLOAD_PATH);
//        将URL路径"/upload/**"映射到服务器文件系统中的FILE_UPLOAD_PATH路径下，使得该目录下的文件可通过URL访问。
    }
}
