package com.jiang.mall.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiang.mall.domain.entity.Product;
import org.apache.ibatis.annotations.Mapper;

/**
 * Product的映射接口，继承自BaseMapper<Product>
 * 本接口用于定义与数据库中tb_products表进行交互的方法，专门用于处理Product实体的CRUD操作
 *
 * @author jiang
 * @email  jiangrongjun2004@163.com
 * @link <a href="https://gitee.com/jiangrongjun/mall">https://gitee.com/jiangrongjun/mall</a>
 * @apiNote Product的映射接口
 * @version 1.0
 * @since 2024年9月8日
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {

}
