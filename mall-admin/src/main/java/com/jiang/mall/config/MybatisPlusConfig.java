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

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig {
//自动分页: PaginationInnerInterceptor
//多租户: TenantLineInnerInterceptor
//动态表名: DynamicTableNameInnerInterceptor
//乐观锁: OptimisticLockerInnerInterceptor
//SQL 性能规范: IllegalSQLInnerInterceptor
//防止全表更新与删除: BlockAttackInnerInterceptor
//@Service
//@DS("slave")
//public class UserServiceImpl implements UserService {
//
//  @Autowired
//  private JdbcTemplate jdbcTemplate;
//
//  @Override
//  @DS("slave_1")
//  public List selectByCondition() {
//    return jdbcTemplate.queryForList("select * from user where age >10");
//  }
//}
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
//        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor())乐观锁;
        return mybatisPlusInterceptor;
    }

    /**
     * 创建并配置一个SqlSessionTemplate实例
     * 该方法通过Spring框架的@Bean注解标识，确保在需要时能够产生一个SqlSessionTemplate实例
     * 主要用于执行数据库操作，通过SqlSessionFactory和ExecutorType来配置该实例
     *
     * @param sqlSessionFactory 一个SqlSessionFactory实例，负责生产SqlSession
     *                          通过@Qualifier注解指定使用名称为"sqlSessionFactory"的Bean
     * @return 返回一个配置了BATCH执行器类型的SqlSessionTemplate实例
     *         BATCH执行器类型意味着SqlSession将缓存SQL语句，直到手动提交或flush，
     *         这样可以减少数据库操作次数，提高性能
     */
    @Bean
    public SqlSessionTemplate sqlSessionTemplate(
            @Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory, ExecutorType.SIMPLE);
    }


}
