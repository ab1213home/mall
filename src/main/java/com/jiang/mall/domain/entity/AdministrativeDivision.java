package com.jiang.mall.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 行政区划实体类，对应数据库表 tb_china_administrative_divisions
 *
 * @author jiang
 * @email  jiangrongjun2004@163.com
 * @link <a href="https://gitee.com/jiangrongjun/mall">https://gitee.com/jiangrongjun/mall</a>
 * @apiNote 行政区划实体类，只读
 * @version 1.0
 * @since 2024年10月13日
 */
@Data
@TableName("tb_china_administrative_divisions")
public class AdministrativeDivision {

	/**
     * 地区编码,主键
     */
	@TableId(value = "area_code", type = IdType.AUTO)
    private Long areaCode;

	/**
     * 层级
     */
    private Byte level;

	/**
     * 父级编码
     */
    private Long parentCode;

	/**
     * 邮编
     */
    private Integer zipCode;

	/**
     * 电话区号
     */
    private String cityCode;

	/**
     * 名称
     */
    private String name;

	/**
     * 简称
     */
    private String shortName;

	/**
     * 拼音
     */
    private String pinyin;

	/**
     * 经度
     */
    private BigDecimal lng;

	/**
     * 纬度
     */
    private BigDecimal lat;
}
