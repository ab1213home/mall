package com.jiang.mall.handler;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(@NotNull IllegalArgumentException ex) {
        // 记录异常信息
	    logger.error("无效请求: {}", ex.getMessage());
        return new ResponseEntity<>("无效请求", HttpStatus.BAD_REQUEST);
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
