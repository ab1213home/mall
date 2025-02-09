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

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.TypeReference;
import com.jiang.mall.config.GeneralConfig;
import com.jiang.mall.domain.ResponseResult;
import jakarta.servlet.http.HttpSession;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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

    @GetMapping("/getGit")
    public ResponseResult<Object> getGit() {
        Map<String,String> map;
	    try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("git.json")) {
            // 检查资源是否存在
            if (inputStream == null) {
                throw new IllegalStateException("文件未找到: git.json");
            }
            // 读取输入流为字符串
            String jsonContent = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            // 使用 Fastjson2 解析为 Map<String, String>
            map= JSON.parseObject(jsonContent, new TypeReference<>() {});
        } catch (JSONException | IOException e) {
            return ResponseResult.failResult();
        }
        return ResponseResult.okResult(map);
    }

}