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

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CategoryAdminVo{

    /**
     * 主键ID，自增
     */
    private Long id;
    /**
     * 父级分类ID，默认为 0
     */
    private Long parentId;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类层级（1-一级分类 2-二级分类）
     */
    private Integer level;

    /**
     * 分类排序
     */
    private Integer sort;

    /**
     * 创建人
     */
    private Long creator;

    /**
     * 更新人
     */
    private Long updater;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
//    @JSONField(format = "yyyy-MM-dd HH:mm:ss", locale = "zh")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
//    @JSONField(format = "yyyy-MM-dd HH:mm:ss", locale = "zh")
    private LocalDateTime updatedAt;

    public CategoryAdminVo() {
    }

//    @TableField(exist = false)
//    private List<Category> children = new ArrayList<>();

    /**
     * 商品分类对象的字符串表示形式
     */
    @Override
    public String toString() {
        return "Category{" +
            "id = " + id +
            ", name = " + name +
            ", parentId = " + parentId +
            ", level = " + level +
            ", sort = " + sort +
            ", creator = " + creator +
            ", createdAt = " + createdAt +
            ", updatedAt = " + updatedAt +
        "}";
    }
}
