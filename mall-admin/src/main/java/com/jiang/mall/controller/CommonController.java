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

import com.jiang.mall.config.GeneralConfig;
import com.jiang.mall.domain.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 公共控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@RestController
@RequestMapping("/common")
public class CommonController {

    @GetMapping("/getFooter")
    public ResponseResult<Object> getFooter() {
        Map<String, Object> map = new HashMap<>();
        map.put("phone", GeneralConfig.getPhone());
        map.put("email", GeneralConfig.getEmail());
        return ResponseResult.okResult(map);
    }

}