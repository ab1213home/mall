package com.jiang.mall.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 轮播图实体类，对应数据库表 tb_banners
 *
 * @author jiang
 * @email  jiangrongjun2004@163.com
 * @link <a href="https://gitee.com/jiangrongjun/mall">https://gitee.com/jiangrongjun/mall</a>
 * @apiNote 轮播图实体类
 * @version 1.0
 * @since 2024年9月8日
 */
@Data
@TableName("tb_banners")
public class Banner implements Serializable {

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
     * 轮播图图片地址
     */
    private String img;

    /**
     * 跳转URL
     */
    private String url;

    /**
     * 描述信息
     */
    private String description;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private Integer creator;

    /**
     * 更新人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Integer updater;

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

    public Banner(String img, String url, String description) {
        this.img = img;
        this.url = url;
        this.description = description;
    }

    public Banner(Integer id, String img, String url, String description) {
        this.id = id;
        this.img = img;
        this.url = url;
        this.description = description;
    }

    public Banner() {
    }

    /**
     * 轮播图对象的字符串表示形式
     */
    @Override
    public String toString() {
        return "Banner{" +
            "id = " + id +
            ", img = " + img +
            ", url = " + url +
            ", description = " + description +
            ", creator = " + creator +
            ", createdAt = " + createdAt +
            ", updatedAt = " + updatedAt +
            ", isDel = " + isDel +
        "}";
    }
}
