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

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 购物车实体类，对应数据库表 tb_carts
 *
 * @author jiang
 * @email  jiangrongjun2004@163.com
 * @link <a href="https://github.com/ab1213home/mall">https://github.com/ab1213home/mall</a>
 * @apiNote 购物车实体类
 * @version 1.0
 * @since 2024年9月8日
 */
@Data
@TableName("tb_carts")
public class Cart implements Serializable {

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
     * 商品数量
     */
    private Long num;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 版本号，用于redis缓存同步锁
     */
    private Long version;


    public Cart() {
    }

	/**
     * 购物车对象的字符串表示形式
     */
    @Override
    public String toString() {
        return "Cart{" +
            "id = " + id +
            ", prodId = " + prodId +
            ", num = " + num +
            ", userId = " + userId +
            ", version = " + version +
        "}";
    }
}
