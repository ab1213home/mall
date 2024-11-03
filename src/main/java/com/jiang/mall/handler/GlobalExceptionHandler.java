package com.jiang.mall.handler;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(@NotNull IllegalArgumentException ex) {
        // 记录异常信息
        System.err.println("无效请求: " + ex.getMessage());
        return new ResponseEntity<>("无效请求", HttpStatus.BAD_REQUEST);
    }
}
