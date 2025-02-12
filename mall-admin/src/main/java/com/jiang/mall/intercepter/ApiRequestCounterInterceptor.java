package com.jiang.mall.intercepter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class ApiRequestCounterInterceptor implements HandlerInterceptor {

    private static final ConcurrentHashMap<String, Integer> requestCounts = new ConcurrentHashMap<>();

    @Getter
    private static Long count = 0L;

	@Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        // 获取原始请求URI并移除查询参数部分
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (!"".equals(contextPath)) {
            requestURI = requestURI.replaceFirst(contextPath, "");
        }

        // 如果有查询字符串，则去掉它
        int queryIndex = requestURI.indexOf('?');
        if (queryIndex != -1) {
            requestURI = requestURI.substring(0, queryIndex);
        }

        // 更新计数
        requestCounts.put(requestURI, requestCounts.getOrDefault(requestURI, 0) + 1);
        count++;
        return true;
    }

    public static int getRequestCount(String uri) {
        return requestCounts.getOrDefault(uri, 0);
    }

}