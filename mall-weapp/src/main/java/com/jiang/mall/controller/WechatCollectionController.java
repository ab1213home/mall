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

import com.jiang.mall.annotation.Wechat;
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.enums.PermissionType;
import com.jiang.mall.domain.vo.CollectionVo;
import com.jiang.mall.service.IWechatService;
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
@RequestMapping("/wechat/collection")
public class WechatCollectionController {

	private IWechatService wechatService;

	@Autowired
	public void setWechatService(IWechatService wechatService){
		this.wechatService = wechatService;
	}

	@PostMapping("/add")
	@Wechat(PermissionType.USER)
	public ResponseResult<Object> insertCollection(@RequestParam("prodId") Long productId,
	                                               @RequestHeader("Token")String token) {
        // 校验商品ID是否为空
        if (productId == null|| productId <= 0) {
            return ResponseResult.failResult("非法请求");
        }
		if (!StringUtils.hasText(productId.toString())){
			return ResponseResult.failResult("商品ID为空");
		}
        Boolean res = wechatService.insertCollection(productId, token);
        if (res == null) {
			return ResponseResult.notFoundResourceResult("该商品已经被收藏");
        }else if (res) {
            return ResponseResult.okResult("添加成功");
        }else {
            return ResponseResult.serverErrorResult("添加失败");
        }
	}

	@GetMapping("/delete")
	@Wechat(PermissionType.USER)
	public ResponseResult<Object> deleteCollection(@RequestParam("prodId") Long productId,
                                                    @RequestHeader("Token")String token) {
		if (productId == null|| productId <= 0) {
            return ResponseResult.failResult("非法请求");
        }
		if (!StringUtils.hasText(productId.toString())){
			return ResponseResult.failResult("商品ID为空");
		}
		Boolean res = wechatService.deleteCollection(productId, token);
		if (res == null) {
			return ResponseResult.notFoundResourceResult("该收藏不存在");
		}else if (res) {
			return ResponseResult.okResult("删除成功");
		} else {
			return ResponseResult.serverErrorResult("删除失败");
		}
	}

	@GetMapping("/getList")
	@Wechat(PermissionType.USER)
	public ResponseResult<Object> getCollectionList(@RequestParam(defaultValue = "1") Integer pageNum,
                                            @RequestParam(defaultValue = "10") Integer pageSize,
                                            @RequestHeader("Token")String token){
		List<CollectionVo> collections = wechatService.getCollectionList(pageNum, pageSize, token);
		if (collections.isEmpty()) {
			return ResponseResult.notFoundResourceResult("没有收藏记录");
		}
		return ResponseResult.okResult(collections);
	}

	@GetMapping("/getNum")
	@Wechat(PermissionType.USER)
	public ResponseResult<Object> getCollectionNum(@RequestHeader("Token")String token) {
		return ResponseResult.okResult(wechatService.getCollectionNum(token));
	}

	@GetMapping("/isCollected")
	@Wechat(PermissionType.USER)
	public ResponseResult<Object> isCollected(@RequestParam("prodId") Long productId,
	                                  @RequestHeader("Token")String token) {
		if (productId == null|| productId <= 0) {
            return ResponseResult.failResult("非法请求");
        }
		if (!StringUtils.hasText(productId.toString())){
			return ResponseResult.failResult("商品ID为空");
		}
		return ResponseResult.okResult(wechatService.isCollect(productId, token));
	}
}
