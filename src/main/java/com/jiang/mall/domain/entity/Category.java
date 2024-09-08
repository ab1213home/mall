package com.jiang.mall.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商品分类实体类，对应数据库表 tb_category
 *
 * @author jiang
 * @email  jiangrongjun2004@163.com
 * @link <a href="https://gitee.com/jiangrongjun/mall">https://gitee.com/jiangrongjun/mall</a>
 * @apiNote 商品分类实体类
 * @version 1.0
 * @since 2024年9月8日
 */
@Data
@TableName("tb_category")
public class Category implements Serializable {

    /**
     * 序列化版本UID
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID，自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 编码，需唯一
     */
    private String code;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private String creator;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 是否删除，默认为 false
     */
    @TableLogic
    private Boolean isDel;

    /**
     * 商品分类对象的字符串表示形式
     */
    @Override
    public String toString() {
        return "Category{" +
            "id = " + id +
            ", code = " + code +
            ", name = " + name +
            ", creator = " + creator +
            ", createdAt = " + createdAt +
            ", updatedAt = " + updatedAt +
            ", isDel = " + isDel +
        "}";
    }
}
