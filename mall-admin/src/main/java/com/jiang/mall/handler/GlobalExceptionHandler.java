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

package com.jiang.mall.handler;

import com.alibaba.fastjson2.JSON;
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.service.II18nService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private II18nService i18nService;

    @Autowired
    public void setI18nService(II18nService i18nService) {
        this.i18nService = i18nService;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(@NotNull IllegalArgumentException ex) {
        // 记录异常信息
	    logger.error("无效请求: {}", ex.getMessage());
        return new ResponseEntity<>("无效请求", HttpStatus.BAD_REQUEST);
    }

    // 处理500错误
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e, HttpServletRequest request) {
        //TODO:错误推送到kafka
//        if (UserAgentUtils.isCurl(request)) {
            // 返回JSON错误实体
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).body(JSON.toJSONString(ResponseResult.failResult(i18nService.getMessage("server.error"))));
//        } else {
//            // 返回HTML错误页面
//            return ResponseEntity.status(500).body("forward:/err/500.html");
//        }
    }

//    // 捕获 PathVariable 参数类型不匹配或格式错误的异常，并返回错误信息
//    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ResponseBody
//    public Map<String, Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
//        return createErrorResponse(HttpStatus.BAD_REQUEST.value(), "请求参数有误: " + ex.getMessage());
//    }
//
//    // 捕获其他未处理的异常，并返回错误信息
//    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    @ResponseBody
//    public Map<String, Object> handleUncaughtException(Exception ex) {
//        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "系统内部错误: " + ex.getMessage());
//    }
//
//    // 创建包含错误码和错误消息的 Map 对象
//    private Map<String, Object> createErrorResponse(int code, String message) {
//        Map<String, Object> errorResponse = new HashMap<>();
//        errorResponse.put("code", code);
//        errorResponse.put("message", message);
//        return errorResponse;
//    }


}
