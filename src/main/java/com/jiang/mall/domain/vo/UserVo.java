package com.jiang.mall.domain.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class UserVo {
    private Integer id;

    private String username;

    private String password;

    private String email;

    private String phone;

    private String firstName;

    private String lastName;

	private Date birthDate;

	private Integer defaultAddressId;

    private Integer roleId;

    public UserVo(Integer userId, String username) {
        this.id = userId;
        this.username = username;
    }
    public UserVo() {
    }
}
