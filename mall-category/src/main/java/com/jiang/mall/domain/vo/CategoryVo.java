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

import lombok.Data;

/**
 * 商品分类视图对象
 *
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@Data
public class CategoryVo {

    /**
     * 分类ID
     */
    private Long id;

    /**
     * 父级分类名称
     */
    private String parent;

    /**
     * 父类别ID，用于建立类别之间的层级关系
     * 如果该类别是顶级类别，则此字段为null或特定值（如0）
     */
    private Long parentId;

    /**
     * 分类层级（1-一级分类 2-二级分类）
     */
    private Integer level;

    /**
     * 分类排序
     */
    private Integer sort;

    /**
     * 分类名称
     */
    private String name;
}
