package com.jiang.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiang.mall.domain.entity.UserCode;

public interface IUserCodeService extends IService<UserCode> {
	boolean inspectByEmail(String email);

	boolean queryByEmail(String email);
}
