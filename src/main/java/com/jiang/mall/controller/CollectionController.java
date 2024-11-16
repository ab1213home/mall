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

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.Collection;
import com.jiang.mall.domain.vo.CollectionVo;
import com.jiang.mall.service.ICollectionService;
import com.jiang.mall.service.IUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;


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

	/**
	 * 注入收藏服务
	 *
	 * @param collectionService 收藏服务
	 */
	@Autowired
	public void setCollectionService(ICollectionService collectionService) {
		this.collectionService = collectionService;
	}

	private IUserService userService;

	/**
	 * 注入用户服务
	 *
	 * @param userService 用户服务
	 */
	@Autowired
	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	@PostMapping("/add")
	public ResponseResult<Object> insertCollection(@RequestParam("productId") Long productId,
                                           HttpSession session) {
		// 检查会话中是否设置表示用户已登录的标志
        ResponseResult<Object> result = userService.checkUserLogin(session);
        if (!result.isSuccess()) {
            // 如果未登录，则直接返回
            return result;
        }
        Long userId = (Long) result.getData();

        // 校验商品ID是否为空
        if (productId == null|| productId <= 0) {
            return ResponseResult.failResult("非法请求");
        }
		if (!StringUtils.hasText(productId.toString())){
			return ResponseResult.failResult("商品ID为空");
		}
        // 调用购物车服务添加商品
        if (collectionService.addCollection(productId, userId)){
            return ResponseResult.okResult("添加成功");
        }else {
            return ResponseResult.serverErrorResult("添加失败");
        }
	}

	@GetMapping("/delete")
	public ResponseResult<Object> deleteCollection(@RequestParam("productId") Long productId,
                                           HttpSession session) {
		// 检查会话中是否设置表示用户已登录的标志
        ResponseResult<Object> result = userService.checkUserLogin(session);
        if (!result.isSuccess()) {
            // 如果未登录，则直接返回
            return result;
        }
		if (productId == null|| productId <= 0) {
            return ResponseResult.failResult("非法请求");
        }
		if (!StringUtils.hasText(productId.toString())){
			return ResponseResult.failResult("商品ID为空");
		}
        Long userId = (Long) result.getData();
		Collection collection =collectionService.queryByProductIdByUserId(productId, userId);
		if (collection == null) {
			return ResponseResult.notFoundResourceResult("该收藏不存在");
		}
		if (collectionService.deleteCollection(productId, userId)) {
			return ResponseResult.okResult("删除成功");
		} else {
			return ResponseResult.serverErrorResult("删除失败");
		}
	}

	@GetMapping("/deleteById")
	public ResponseResult<Object> deleteByIdCollection(@RequestParam("id") Integer id,
                                           HttpSession session) {
		if (id == null|| id <= 0) {
            return ResponseResult.failResult("非法请求");
        }
		if (!StringUtils.hasText(id.toString())){
			return ResponseResult.failResult("收藏ID为空");
		}
		Collection collection =collectionService.getById(id);
        if (collection == null) {
			return ResponseResult.notFoundResourceResult("该收藏不存在");
		}
		// 检查会话中是否设置表示用户已登录的标志
		ResponseResult<Object> result = userService.checkUserLogin(session);
        if (!result.isSuccess()) {
            // 如果未登录，则直接返回
            return result;
        }
		Long userId = (Long) result.getData();
		if (!Objects.equals(collection.getUserId(), userId)){
			return ResponseResult.failResult("您没有权限删除该收藏");
		}
		if (collectionService.deleteById(collection.getId())) {
			return ResponseResult.okResult("删除成功");
		} else {
			return ResponseResult.serverErrorResult("删除失败");
		}
	}

	@GetMapping("/getList")
	public ResponseResult<Object> getCollectionList(@RequestParam(defaultValue = "1") Integer pageNum,
                                            @RequestParam(defaultValue = "10") Integer pageSize,
                                            HttpSession session){
		// 检查会话中是否设置表示用户已登录的标志
        ResponseResult<Object> result = userService.checkUserLogin(session);
        if (!result.isSuccess()) {
            // 如果未登录，则直接返回
            return result;
        }
        Long userId = (Long) result.getData();
		List<CollectionVo> collections =collectionService.getCollectionList(pageNum, pageSize,userId);
		if (collections.isEmpty()) {
			return ResponseResult.okResult(collections,"没有收藏记录");
		}
		return ResponseResult.okResult(collections);
	}

	@GetMapping("/getNum")
	public ResponseResult<Object> getCollectionNum(HttpSession session) {
		// 检查会话中是否设置表示用户已登录的标志
        ResponseResult<Object> result = userService.checkUserLogin(session);
        if (!result.isSuccess()) {
            // 如果未登录，则直接返回
            return result;
        }
        Long userId = (Long) result.getData();
		return ResponseResult.okResult(collectionService.getCollectionNum(userId));
	}

	@GetMapping("/isCollected")
	public ResponseResult<Object> isCollected(@RequestParam("productId") Long productId,
	                                  HttpSession session) {
		// 检查会话中是否设置表示用户已登录的标志
        ResponseResult<Object> result = userService.checkUserLogin(session);
        if (!result.isSuccess()) {
            // 如果未登录，则直接返回
            return result;
        }
		if (productId == null|| productId <= 0) {
            return ResponseResult.failResult("非法请求");
        }
		if (!StringUtils.hasText(productId.toString())){
			return ResponseResult.failResult("商品ID为空");
		}
        Long userId = (Long) result.getData();
		return ResponseResult.okResult(collectionService.isCollect(productId, userId));
	}
}
