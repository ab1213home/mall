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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
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

    @GetMapping("/system-info")
    public ResponseResult<Object> getSystemInfo() {
        Map<String, Object> info = new HashMap<>();

        // Java Info
        Map<String, String> javaInfo = new HashMap<>();
        javaInfo.put("version", System.getProperty("java.version"));
        javaInfo.put("vendor", System.getProperty("java.vendor"));
        javaInfo.put("runtime.name", System.getProperty("java.runtime.name"));
        javaInfo.put("runtime.version", System.getProperty("java.runtime.version"));
        javaInfo.put("jvm.name", System.getProperty("java.vm.name"));
        javaInfo.put("jvm.vendor", System.getProperty("java.vm.vendor"));
        javaInfo.put("jvm.version", System.getProperty("java.vm.version"));
        info.put("java", javaInfo);

        // OS Info
        Map<String, String> osInfo = new HashMap<>();
        osInfo.put("name", System.getProperty("os.name"));
        osInfo.put("version", System.getProperty("os.version"));
        osInfo.put("arch", System.getProperty("os.arch"));
        info.put("os", osInfo);

        return ResponseResult.okResult(info);
    }

    @Autowired
    private DataSource dataSource;

    @GetMapping("/data-info")
    public ResponseResult<Object> getDatabaseInfo() {
        Map<String, Object> info = new HashMap<>();
        try (Connection connection = DataSourceUtils.getConnection(dataSource)) {
	        DatabaseMetaData metaData = connection.getMetaData();
	        String dbProductName = metaData.getDatabaseProductName(); // 数据库名称
	        String dbProductVersion = metaData.getDatabaseProductVersion(); // 数据库版本
	        info.put("database", dbProductName);
	        info.put("version", dbProductVersion);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ResponseResult.okResult(info);
    }

}