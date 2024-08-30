package com.jiang.mall.config;

 import com.baomidou.mybatisplus.annotation.DbType;
 import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
 import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
 import org.springframework.context.annotation.Bean;
 import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig {

    /**
     * 配置MybatisPlus拦截器
     * 该方法用于创建并返回一个MybatisPlusInterceptor对象，用于增强MybatisPlus的功能
     * 主要通过添加PaginationInnerInterceptor实现分页功能
     *
     * @return MybatisPlusInterceptor 返回配置好的MybatisPlus拦截器对象
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        // 创建MybatisPlus拦截器实例
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        // 添加分页拦截器到MybatisPlus拦截器中，指定数据库类型为MySQL
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        // 返回配置好的MybatisPlus拦截器对象
        return mybatisPlusInterceptor;
    }
}
