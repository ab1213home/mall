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

package com.jiang.mall.domain.vo;

import com.alibaba.fastjson2.JSON;
import com.jiang.mall.util.SecureUtil;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 商品视图对象
 *
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@Data
public class ProductVo {

    /**
     * 商品ID
     */
    private Long id;

    /**
     * 商品编码
     */
    private String code;

    /**
     * 商品标题
     */
    private String title;

    /**
     * 商品分类
     */
    private CategoryVo category;

    /**
     * 商品图片地址
     */
    private String img;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 商品属性
     */
	private String properties;

    /**
     * 库存数量
     */
    private Integer stocks;

    /**
     * 商品描述
     */
    private String description;

    public ProductVo(){

    }

    public @NotNull String getHash() {
		Map<String, Object> map = new HashMap<>();
		map.put("id", this.id);
		map.put("code", this.code);
		map.put("title", this.title);
		map.put("price", this.price);
		map.put("img", this.img);
		map.put("description", this.description);
		map.put("category", JSON.toJSONString(this.category));
		map.put("properties", this.properties);
		return SecureUtil.sha256Hex(JSON.toJSONString(map));
	}
}

