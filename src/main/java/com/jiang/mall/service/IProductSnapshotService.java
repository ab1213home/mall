package com.jiang.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiang.mall.domain.entity.ProductSnapshot;
import com.jiang.mall.domain.vo.ProductSnapshotVo;

public interface IProductSnapshotService extends IService<ProductSnapshot> {
	ProductSnapshotVo getSnapshotInfo(Long id, Long userId);
}
