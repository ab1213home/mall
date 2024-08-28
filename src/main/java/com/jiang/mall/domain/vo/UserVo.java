package com.jiang.mall.domain.vo;

import lombok.Data;

@Data
public class UserVo {
    private Integer id;
    private String username;

    public UserVo(Integer userId, String username) {
        this.id = userId;
        this.username = username;
    }
    public UserVo() {
    }
}
