package com.jiang.mall.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

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
@TableName("tb_users")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String username;

    private String password;

    private String email;

    private String phone;

    private String firstName;

    private String lastName;

	private Date birthDate;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    private Boolean isActive;

    private Integer roleId;

    @Override
    public String toString() {
        return getClass().getSimpleName() +
			    " [" +
			    "Hash = " + hashCode() +
			    ", id=" + id +
			    ", username=" + username +
			    ", email=" + email +
			    ", phone=" + phone +
			    ", firstName=" + firstName +
			    ", lastName=" + lastName +
			    ", birthDate=" + birthDate +
			    ", createdAt=" + createdAt +
			    ", updatedAt=" + updatedAt +
			    ", isActive=" + isActive +
			    ", roleId=" + roleId +
			    "]";
    }

}
