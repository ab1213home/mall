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

package com.jiang.mall.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 商品实体类，对应数据库表 tb_products
 *
 * @author jiang
 * @email  jiangrongjun2004@163.com
 * @link <a href="https://gitee.com/jiangrongjun/mall">https://gitee.com/jiangrongjun/mall</a>
 * @apiNote 商品实体类
 * @version 1.0
 * @since 2024年9月8日
 */
@Data
@TableName("tb_products")
public class Product implements Serializable {

    /**
     * 序列化版本UID
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID，自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商品编码，不可重复
     */
    private String code;

    /**
     * 商品标题
     */
    private String title;

    /**
     * 商品分类ID
     */
    private Long categoryId;

    /**
     * 商品图片
     */
    private String img;

    /**
     * 商品价格
     */
    private Double price;

    /**
     * 商品库存
     */
    private Integer stocks;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private Long creator;

    /**
     * 更新人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updater;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updatedAt;

    /**
     * 是否删除，默认为 false
     */
    @TableLogic
    private Boolean isDel;

    /**
     * 构造函数：创建一个产品对象
     *
     * @param code 产品代码，唯一标识产品
     * @param title 产品标题，用于显示产品名称
     * @param categoryId 类别ID，整数类型，用于归类产品
     * @param img 图片路径，字符串类型，用于显示产品图片
     * @param price 价格，双精度浮点型，表示产品的售价
     * @param stocks 库存数量，整数类型，表示当前可售产品数量
     * @param description 产品描述，字符串类型，提供产品的详细信息
     */
    public Product(String code, String title, Long categoryId, String img, Double price, Integer stocks, String description) {
        this.code = code;
        this.title = title;
        this.categoryId = categoryId;
        this.img = img;
        this.price = price;
        this.stocks = stocks;
        this.description = description;
    }

    /**
     * 商品类的构造方法，用于初始化商品对象的属性
     *
     * @param id 商品的唯一标识符
     * @param code 商品的代码
     * @param title 商品的标题
     * @param categoryId 商品所属的类别标识
     * @param img 商品的图片路径
     * @param price 商品的价格
     * @param stocks 商品的库存量
     * @param description 商品的描述信息
     */
    public Product(Long id, String code, String title, Long categoryId, String img, Double price, Integer stocks, String description) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.categoryId = categoryId;
        this.img = img;
        this.price = price;
        this.stocks = stocks;
        this.description = description;
    }

    public Product() {
    }

    /**
     * 商品对象的字符串表示形式
     */
    @Override
    public String toString() {
        return "Product{" +
            "id = " + id +
            ", code = " + code +
            ", title = " + title +
            ", categoryId = " + categoryId +
            ", img = " + img +
            ", price = " + price +
            ", stocks = " + stocks +
            ", description = " + description +
            ", creator = " + creator +
            ", createdAt = " + createdAt +
            ", updatedAt = " + updatedAt +
            ", isDel = " + isDel +
        "}";
    }
}

