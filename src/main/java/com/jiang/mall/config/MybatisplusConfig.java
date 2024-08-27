package com.jiang.mall.config;

 import com.baomidou.mybatisplus.annotation.DbType;
 import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
 import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
 import org.springframework.context.annotation.Bean;
 import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisplusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
//        创建拦截器对象：函数创建一个MybatisPlusInterceptor对象，用于拦截Mybatis-Plus的执行过程。
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
//        添加分页拦截器：向拦截器中添加一个PaginationInnerInterceptor，用于自动分页功能，参数DbType.MYSQL指明数据库类型为MySQL。
        return mybatisPlusInterceptor;
//        返回拦截器
    }
}
