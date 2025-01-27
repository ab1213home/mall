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
import com.jiang.mall.service.IRedisService;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RestController
@RequestMapping("/text")
public class TextController {

	@Autowired
	private IRedisService redisService;

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

	@GetMapping("/session")
	public ResponseResult<Object> error(HttpSession session) {
		redisService.setString(session.getId(), "Hello, Redis!");
		String greeting = redisService.getString(session.getId());
		System.out.println(greeting);
		return ResponseResult.okResult("text");
	}

	@GetMapping("/session-id")
    public ResponseResult<Object> getSessionId(HttpSession session) {
        // 获取当前请求的会话对象
        return ResponseResult.okResult(session.getId());
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
    @ResponseBody
    public String getIp(HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        return "Client IP address: " + ipAddress;
    }

    @GetMapping("/getIp2")
    @ResponseBody
    public String getIp2(ServletRequest request) {
        String ipAddress = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getRemoteAddr();
        return "Client IP address: " + ipAddress;
    }


    @GetMapping("/getIp3")
    @ResponseBody
    public String getIp3(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }

        // 如果 X-Forwarded-For 包含多个 IP 地址，取第一个非未知的 IP 地址
        if (ipAddress != null && ipAddress.contains(",")) {
            String[] ipAddresses = ipAddress.split(",");
            for (String ip : ipAddresses) {
                if (!"unknown".equalsIgnoreCase(ip.trim())) {
                    ipAddress = ip.trim();
                    break;
                }
            }
        }

        return "Client IP address: " + ipAddress;
    }
}
