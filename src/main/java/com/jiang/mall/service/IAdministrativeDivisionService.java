package com.jiang.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiang.mall.domain.entity.AdministrativeDivision;

import java.util.List;

public interface IAdministrativeDivisionService extends IService<AdministrativeDivision> {
	List<AdministrativeDivision> getList(Integer level, Long parentCode);
}
