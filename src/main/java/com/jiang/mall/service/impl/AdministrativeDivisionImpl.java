package com.jiang.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.dao.AdministrativeDivisionMapper;
import com.jiang.mall.domain.entity.AdministrativeDivision;
import com.jiang.mall.service.IAdministrativeDivisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdministrativeDivisionImpl extends ServiceImpl<AdministrativeDivisionMapper, AdministrativeDivision> implements IAdministrativeDivisionService {

	private AdministrativeDivisionMapper administrativeDivisionMapper;

	@Autowired
	public void setAdministrativeDivisionMapper(AdministrativeDivisionMapper administrativeDivisionMapper) {
		this.administrativeDivisionMapper = administrativeDivisionMapper;
	}


	@Override
	public List<AdministrativeDivision> getList(Integer level, Long parentCode) {
		QueryWrapper<AdministrativeDivision> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("level", level);
		queryWrapper.eq("parent_code", parentCode);
		return administrativeDivisionMapper.selectList(queryWrapper);
	}
}
