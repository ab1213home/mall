package com.jiang.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiang.mall.domain.entity.LoginRecord;

public interface ILoginRecordService extends IService<LoginRecord> {
	int countByUsername(String username);
}
