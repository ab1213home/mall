package com.jiang.mall.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiang.mall.domain.entity.AdministrativeDivision;
import org.apache.ibatis.annotations.Mapper;

/**
 * AdministrativeDivision的映射接口，继承自BaseMapper<AdministrativeDivision>
 * 本接口用于定义与数据库中tb_china_administrative_divisions表进行交互的方法，专门用于处理AdministrativeDivision实体的CRUD操作
 *
 * @author jiang
 * @email  jiangrongjun2004@163.com
 * @link <a href="https://gitee.com/jiangrongjun/mall">https://gitee.com/jiangrongjun/mall</a>
 * @apiNote AdministrativeDivision的映射接口
 * @version 1.0
 * @since 2024年10月13日
 */
@Mapper
public interface AdministrativeDivisionMapper extends BaseMapper<AdministrativeDivision> {
}
