package com.jiang.mall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.dao.CategorySnapshotMapper;
import com.jiang.mall.domain.entity.CategorySnapshot;
import com.jiang.mall.service.ICategorySnapshotServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategorySnapshotServerImpl extends ServiceImpl<CategorySnapshotMapper, CategorySnapshot> implements ICategorySnapshotServer {

	private CategorySnapshotMapper categorySnapshotMapper;

	@Autowired
	public void setCategorySnapshotMapper(CategorySnapshotMapper categorySnapshotMapper) {
		this.categorySnapshotMapper = categorySnapshotMapper;
	}
}
