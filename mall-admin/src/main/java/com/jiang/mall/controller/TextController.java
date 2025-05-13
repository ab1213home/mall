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
import com.jiang.mall.domain.enums.UserStatus;
import com.jiang.mall.service.IUserLogService;
import com.jiang.mall.util.NetworkUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/text")
public class TextController {

    private IUserLogService userLogService;

    @Autowired
    public void setLoginRecordService(IUserLogService userLogService) {
        this.userLogService = userLogService;
    }

    @GetMapping("/log")
    public ResponseResult<Object> getLog(@RequestParam(defaultValue = "100") Integer number) {
        //随机生成参数
        String username = "text";
        for (int i = 0; i < number; i++) {
            userLogService.defaultLog(username, "192.168.1.1", "text_"+i,  UserStatus.SUCCESS_LOGIN,new HashMap<>());
        }
        return ResponseResult.okResult();
    }

	@GetMapping("/time")
	public ResponseResult<Object> time(@RequestParam(defaultValue = "1") Double time) {
		try {
            // 延迟指定的时间（以毫秒为单位）
            Thread.sleep((long) (time * 1000));
        } catch (InterruptedException e) {
            // 处理中断异常
            Thread.currentThread().interrupt();
            return ResponseResult.serverErrorResult("请求被中断");
        }
		return ResponseResult.okResult();
	}

	@GetMapping("/get-user-info")
    @ResponseBody
    public ResponseEntity<Object> getUserInfo(@RequestHeader(value = "X-Forwarded-For", required = false) String xForwardedFor,
	                                          @RequestHeader(value = "X-Real-IP",required = false) String xRealIp) {
        String userIp = determineUserIp(xForwardedFor, xRealIp);
        return getGeoInfo(userIp);
    }

    private @NotNull String determineUserIp(String xForwardedFor, String xRealIp) {
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // 如果是多级反向代理，则取第一个IP地址
            String[] addresses = xForwardedFor.split(",");
            for (String address : addresses) {
                if (!address.isEmpty()) {
                    return address.trim();
                }
            }
        } else if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        } else {
            return "127.0.0.1"; // 默认本地IP
        }
        return "127.0.0.1";
    }

    private @NotNull ResponseEntity<Object> getGeoInfo(String userIp) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://ip-api.com/json/" +
                userIp;
        return restTemplate.getForEntity(url, Object.class);
    }

    @GetMapping("/getIp")
//    @ResponseBody
    public ResponseResult<Object> getIp(HttpServletRequest request) {
        Map<Object, Object> map = new HashMap<>();
        String ipAddress = NetworkUtils.getIpAddr(request);
        int serverPort = request.getServerPort();
        map.put("serverPort", serverPort);
        map.put("serverName", request.getServerName());
        String portSuffix = (serverPort == 80 || serverPort == 443) ? "" : ":" + serverPort;
        String url = request.getScheme() + "://" + request.getServerName() + portSuffix;
        map.put("ipAddress", ipAddress);
        map.put("url", url);
        map.put("RequestURI", request.getRequestURI());
        //输出请求头
        Enumeration<String> headerNames = request.getHeaderNames();
        Collections.list(headerNames).forEach(headerName -> {
            String headerValue = request.getHeader(headerName);
            map.put(headerName, headerValue);
        });
        return ResponseResult.okResult(map);
    }

// {
//  "code": 200,
//  "message": "默认成功消息提示",
//  "data": {
//    "sec-fetch-mode": "navigate",
//    "sec-fetch-site": "none",
//    "accept-language": "zh-CN,zh;q=0.9",
//    "ipAddress": "0:0:0:0:0:0:0:1",
//    "sec-fetch-user": "?1",
//    "url": "http://localhost:8080",
//    "accept": "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
//    "sec-ch-ua": "\"Chromium\";v=\"136\", \"Microsoft Edge\";v=\"136\", \"Not.A/Brand\";v=\"99\"",
//    "sec-ch-ua-mobile": "?0",
//    "sec-ch-ua-platform": "\"Windows\"",
//    "host": "localhost:8080",
//    "upgrade-insecure-requests": "1",
//    "RequestURI": "/text/getIp",
//    "connection": "keep-alive",
//    "cache-control": "max-age=0",
//    "accept-encoding": "gzip, deflate, br, zstd",
//    "user-agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36 Edg/136.0.0.0",
//    "sec-fetch-dest": "document"
//  },
//  "timestamp": 1746453909169,
//  "success": true
//}
}
