package com.jiang.mall.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiang.mall.domain.entity.Collection;
import org.apache.ibatis.annotations.Mapper;

/**
 * Collection的映射接口，继承自BaseMapper<Collection>
 * 本接口用于定义与数据库中tb_collections表进行交互的方法，专门用于处理Collection实体的CRUD操作
 *
 * @author jiang
 * @email  jiangrongjun2004@163.com
 * @link <a href="https://gitee.com/jiangrongjun/mall">https://gitee.com/jiangrongjun/mall</a>
 * @apiNote Collection的映射接口
 * @version 1.0
 * @since 2024年9月8日
 */
@Mapper
public interface CollectionMapper extends BaseMapper<Collection> {
}
