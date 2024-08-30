package com.jiang.mall.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 实体类
 * </p>
 *作者： 蒋神 HJL
 * @since 2024-08-05
 */
@Getter
@Setter
@Data
@TableName("banner")
public class Banner implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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
     * 是否删除
     */
    @TableLogic
    private Boolean isDel;

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
