package com.jiang.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiang.mall.domain.entity.VerificationCode;

public interface IVerificationCodeService extends IService<VerificationCode> {
	boolean inspectByEmail(String email);

	boolean queryByEmail(String email);

	boolean checkingByUserId(Long id);

	VerificationCode queryCodeByEmail(String email);

	boolean useCode(Long userId, VerificationCode verificationCode);
}
