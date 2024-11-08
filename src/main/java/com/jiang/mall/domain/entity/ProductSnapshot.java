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
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 商品快照实体类，对应数据库表 tb_product_snapshots
 *
 * @author jiang
 * @email  jiangrongjun2004@163.com
 * @link <a href="https://gitee.com/jiangrongjun/mall">https://gitee.com/jiangrongjun/mall</a>
 * @apiNote 商品快照实体类
 * @version 1.0
 * @since 2024年10月23日
 */
@Data
@TableName("tb_product_snapshots")
public class ProductSnapshot implements Serializable {

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
     * 商品ID
     */
    private Long prodId;

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
     * 商品描述
     */
    private String description;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private Long creator;


    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;

    /**
     * 是否删除，默认为 false
     */
    @TableLogic
    private Boolean isDel;


    public ProductSnapshot() {
    }

    /**
     * 构造函数，用于创建商品快照对象
     *
     * @param product 商品对象
     */
    public ProductSnapshot(@NotNull Product product) {
        this.prodId=product.getId();
        this.code=product.getCode();
        this.title=product.getTitle();
        this.img=product.getImg();
        this.price=product.getPrice();
        this.description=product.getDescription();
    }

    @Override
    public String toString() {
        return "ProductSnapshot{" +
                "id=" + id +
                ", prodId=" + prodId +
                ", code='" + code + '\'' +
                ", title='" + title + '\'' +
                ", categoryId=" + categoryId +
                ", img='" + img + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", creator=" + creator +
                ", createdAt="+ createdAt +
                ", isDel=" + isDel +
                '}';
    }
}

