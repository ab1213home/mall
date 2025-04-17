/*
 * Copyright (c) 2024 Jiang RongJun
 * Jiang Mall is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan
 * PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY
 * KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.jiang.mall.controller;

import com.jiang.mall.annotation.Permission;
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.Collection;
import com.jiang.mall.domain.enums.PermissionType;
import com.jiang.mall.domain.vo.CollectionVo;
import com.jiang.mall.service.ICollectionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 收藏控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月20日
 */
@RestController
@RequestMapping("/collection")
public class CollectionController {

	private ICollectionService collectionService;

	@Autowired
	public void setCollectionService(ICollectionService collectionService) {
		this.collectionService = collectionService;
	}

	@PostMapping("/add")
	@Permission(PermissionType.USER)
	public ResponseResult<Object> insertCollection(@RequestParam("prodId") Long productId,
	                                               HttpSession session) {
        // 校验商品ID是否为空
        if (productId == null|| productId <= 0) {
            return ResponseResult.failResult("非法请求");
        }
		if (!StringUtils.hasText(productId.toString())){
			return ResponseResult.failResult("商品ID为空");
		}
        // 调用购物车服务添加商品
        if (collectionService.insertCollection(productId, session.getId())){
            return ResponseResult.okResult("添加成功");
        }else {
            return ResponseResult.serverErrorResult("添加失败");
        }
	}

	@GetMapping("/delete")
	@Permission(PermissionType.USER)
	public ResponseResult<Object> deleteCollection(@RequestParam("prodId") Long productId,
                                                    HttpSession session) {
		if (productId == null|| productId <= 0) {
            return ResponseResult.failResult("非法请求");
        }
		if (!StringUtils.hasText(productId.toString())){
			return ResponseResult.failResult("商品ID为空");
		}
		Collection collection =collectionService.queryByProductIdByUserId(productId, session.getId());
		if (collection == null) {
			return ResponseResult.notFoundResourceResult("该收藏不存在");
		}
		if (collectionService.deleteCollection(productId, session.getId())) {
			return ResponseResult.okResult("删除成功");
		} else {
			return ResponseResult.serverErrorResult("删除失败");
		}
	}

	@GetMapping("/getList")
	@Permission(PermissionType.USER)
	public ResponseResult<Object> getCollectionList(@RequestParam(defaultValue = "1") Integer pageNum,
                                            @RequestParam(defaultValue = "10") Integer pageSize,
                                            HttpSession session){
		List<CollectionVo> collections =collectionService.getCollectionList(pageNum, pageSize, session.getId());
		if (collections.isEmpty()) {
			return ResponseResult.okResult(collections,"没有收藏记录");
		}
		return ResponseResult.okResult(collections);
	}

	@GetMapping("/getNum")
	@Permission(PermissionType.USER)
	public ResponseResult<Object> getCollectionNum(HttpSession session) {
		return ResponseResult.okResult(collectionService.getCollectionNum(session.getId()));
	}

	@GetMapping("/isCollected")
	@Permission(PermissionType.USER)
	public ResponseResult<Object> isCollected(@RequestParam("prodId") Long productId,
	                                  HttpSession session) {
		if (productId == null|| productId <= 0) {
            return ResponseResult.failResult("非法请求");
        }
		if (!StringUtils.hasText(productId.toString())){
			return ResponseResult.failResult("商品ID为空");
		}
		return ResponseResult.okResult(collectionService.isCollect(productId, session.getId()));
	}
}
