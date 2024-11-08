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
 * 商品分类实体类，对应数据库表 tb_category_snapshots
 *
 * @author jiang
 * @email  jiangrongjun2004@163.com
 * @link <a href="https://gitee.com/jiangrongjun/mall">https://gitee.com/jiangrongjun/mall</a>
 * @apiNote 商品分类实体快照类
 * @version 1.0
 * @since 2024年10月23日
 */
@Data
@TableName("tb_category_snapshots")
public class CategorySnapshot implements Serializable {

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
     * 分类ID
     */
    private Long categoryId;

    /**
     * 编码，需唯一
     */
    private String code;

    /**
     * 父级分类ID，默认为 0
     */
    private Long parentId;

    /**
     * 分类级别，默认为 1
     */
    private Byte level;

    /**
     * 分类名称
     */
    private String name;

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

    public CategorySnapshot() {
    }

    /**
     * 构造函数，用于将 Category 对象转换为 CategorySnapshot 对象
     *
     * @param category Category 对象
     */
    public CategorySnapshot(@NotNull Category category) {
        this.parentId = category.getParentId();
        this.level = category.getLevel();
        this.categoryId = category.getId();
        this.code = category.getCode();
        this.name = category.getName();
    }

    @Override
    public String toString() {
        return "CategorySnapshot{" +
                "id=" + id +
                ", categoryId=" + categoryId +
                ", code='" + code + '\'' +
                ", parentId=" + parentId +
                ", level=" + level +
                ", name='" + name + '\'' +
                ", creator=" + creator +
                ", createdAt=" + createdAt +
                ", isDel=" + isDel +
                '}';
    }
}
