package com.jiang.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.dao.LoginRecordMapper;
import com.jiang.mall.domain.entity.LoginRecord;
import com.jiang.mall.service.ILoginRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoginRecordServerImpl extends ServiceImpl<LoginRecordMapper, LoginRecord> implements ILoginRecordService {

	private LoginRecordMapper loginRecordMapper;

	@Autowired
	public void setLoginRecordMapper(LoginRecordMapper loginRecordMapper) {
		this.loginRecordMapper = loginRecordMapper;
	}

	@Override
	public int countByUsername(String username) {
		QueryWrapper<LoginRecord> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("username",username);
		List<LoginRecord> list = loginRecordMapper.selectList(queryWrapper);
		return list.size();
	}
}
