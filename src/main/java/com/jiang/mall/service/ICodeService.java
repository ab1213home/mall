package com.jiang.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiang.mall.domain.entity.Code;

public interface ICodeService extends IService<Code> {
	boolean inspectByEmail(String email);

	boolean queryByEmail(String email);

	boolean checkingByUserId(Integer id);

	Code queryCodeByEmail(String email);

	boolean useCode(int userId, Code code);
}
