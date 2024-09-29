package com.jiang.mall.intercepter;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.vo.UserVo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class AdminLoginInterceptor implements HandlerInterceptor {

	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        String requestURI = request.getRequestURI();
        if (request.getSession().getAttribute("User")!=null){
            UserVo user = (UserVo) request.getSession().getAttribute("User");
            if (user.getId()==null){
                redirectToLogin(request, response, requestURI);
                return false;
            }
            if (user.isAdmin()){
                return true;
            }else {
                response.sendRedirect(request.getContextPath() + "/user/index.html");
                return false;
            }
        }else {
            redirectToLogin(request, response, requestURI);
            return false;
        }
	}

    /**
     * 重定向用户到登录页面
     * 当检测到尝试访问的请求需要登录权限时，会调用此方法将用户重定向到登录页面，并携带当前尝试访问的URL和提示信息
     *
     * @param request  HTTP请求对象，用于获取上下文路径
     * @param response HTTP响应对象，用于重定向用户到登录页面
     * @param requestURI 当前请求的URI，尝试访问但需要登录权限的资源地址
     * @throws IOException 重定向过程中可能抛出的IO异常
     */
    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response, String requestURI) throws IOException {
        // 构造登录页面的URL，使用上下文路径确保正确获取登录页面的位置
        String loginUrl = request.getContextPath() + "/user/login.html";
        // 编码请求的URI，以确保URL中的特殊字符能够正确传递
        String urlParam = URLEncoder.encode(requestURI, StandardCharsets.UTF_8);
        // 编码提示信息，以确保非ASCII字符能正确传递
        String messageParam = URLEncoder.encode("非法访问，请登录！", StandardCharsets.UTF_8);

        // 设置响应的内容类型和字符编码，确保浏览器正确解析重定向的URL
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        // 执行重定向，将用户引导至登录页面，并传递目标URL和提示信息作为参数
        response.sendRedirect(loginUrl + "?url=" + urlParam + "&message=" + messageParam);
    }
}
