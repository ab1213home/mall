package com.jiang.mall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.dao.AdministrativeDivisionMapper;
import com.jiang.mall.domain.entity.AdministrativeDivision;
import com.jiang.mall.service.IAdministrativeDivisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdministrativeDivisionImpl extends ServiceImpl<AdministrativeDivisionMapper, AdministrativeDivision> implements IAdministrativeDivisionService {

	private AdministrativeDivisionMapper administrativeDivisionMapper;

	@Autowired
	public void setAdministrativeDivisionMapper(AdministrativeDivisionMapper administrativeDivisionMapper) {
		this.administrativeDivisionMapper = administrativeDivisionMapper;
	}


}
