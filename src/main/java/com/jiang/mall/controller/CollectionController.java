package com.jiang.mall.controller;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.Collection;
import com.jiang.mall.domain.vo.CollectionVo;
import com.jiang.mall.service.ICollectionService;
import com.jiang.mall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static com.jiang.mall.util.CheckUser.checkUserLogin;

/**
 * 收藏控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月20日
 */
@Controller
@RequestMapping("/collection")
public class CollectionController {

	@Autowired
	private ICollectionService collectionService;


	@PostMapping("/add")
	public ResponseResult insertCollection(@RequestParam("productId") Integer productId,
                                           HttpSession session) {
		// 检查会话中是否设置表示用户已登录的标志
        ResponseResult result = checkUserLogin(session);
        if (!result.isSuccess()) {
            // 如果未登录，则直接返回
            return result;
        }
        Integer userId = (Integer) result.getData();

        // 校验商品ID是否为空
        if (productId == null) {
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
	public ResponseResult deleteCollection(@RequestParam("id") Integer id,
                                           HttpSession session) {
		// 检查会话中是否设置表示用户已登录的标志
        ResponseResult result = checkUserLogin(session);
        if (!result.isSuccess()) {
            // 如果未登录，则直接返回
            return result;
        }
        Integer userId = (Integer) result.getData();
		Collection collection =collectionService.queryByIdByUserId(id, userId);
		if (collection == null) {
			return ResponseResult.notFoundResourceResult("该收藏不存在");
		}
		if (collectionService.deleteCollection(id)) {
			return ResponseResult.okResult("删除成功");
		} else {
			return ResponseResult.serverErrorResult("删除失败");
		}
	}

	@GetMapping("/getList")
	public ResponseResult getCollectionList(@RequestParam(defaultValue = "1") Integer pageNum,
                                            @RequestParam(defaultValue = "10") Integer pageSize,
                                            HttpSession session){
		// 检查会话中是否设置表示用户已登录的标志
        ResponseResult result = checkUserLogin(session);
        if (!result.isSuccess()) {
            // 如果未登录，则直接返回
            return result;
        }
        Integer userId = (Integer) result.getData();
		List<CollectionVo> collections =collectionService.getCollectionList(pageNum, pageSize,userId);
		return ResponseResult.okResult(collections);
	}

	@GetMapping("/getNum")
	public ResponseResult getCollectionNum(HttpSession session) {
		// 检查会话中是否设置表示用户已登录的标志
        ResponseResult result = checkUserLogin(session);
        if (!result.isSuccess()) {
            // 如果未登录，则直接返回
            return result;
        }
        Integer userId = (Integer) result.getData();
		Integer num = collectionService.getCollectionNum(userId);
		return ResponseResult.okResult(num);
	}
}
