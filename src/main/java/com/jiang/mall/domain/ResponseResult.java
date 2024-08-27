package com.jiang.mall.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ResponseResult<T> implements Serializable {

    private int code;
    private String message;
    private T data;

    private static final String DEFAULT_SUCCESS_MESSAGE = "success";
    private static final String DEFAULT_FAIL_MESSAGE = "fail";
    private static final int DEFAULT_SUCCESS_CODE = 200;
    private static final int DEFAULT_FAIL_CODE = 500;


//    函数创建并返回一个ResponseResult对象，该对象表示默认的成功响应，其中包含默认的成功代码和消息。
    public static ResponseResult okResult() {
        ResponseResult responseResult = new ResponseResult();
        responseResult.setCode(DEFAULT_SUCCESS_CODE);
        responseResult.setMessage(DEFAULT_SUCCESS_MESSAGE);
        return responseResult;
    }


//    创建一个成功响应的结果对象，并设置自定义的消息。
//    函数内部会新建一个ResponseResult对象，设置其默认的成功代码和传入的消息，然后返回这个对象。
    public static ResponseResult okResult(String message) {
        ResponseResult responseResult = new ResponseResult();
        responseResult.setCode(DEFAULT_SUCCESS_CODE);
        responseResult.setMessage(message);
        return responseResult;
    }


//    成功的响应结果的对象
    public static ResponseResult okResult(Object data) {
        ResponseResult responseResult = new ResponseResult();
        responseResult.setCode(DEFAULT_SUCCESS_CODE);
        responseResult.setMessage(DEFAULT_SUCCESS_MESSAGE);
        responseResult.setData(data);
        return responseResult;
    }


//    创建一个失败结果的响应对象,将该对象的状态码设置为预定义的失败状态码。将该对象的消息设置为预定义的失败消息
    public static ResponseResult failResult(){
        ResponseResult responseResult = new ResponseResult();
        responseResult.setCode(DEFAULT_FAIL_CODE);
        responseResult.setMessage(DEFAULT_FAIL_MESSAGE);
        return responseResult;
    }



//    创建并返回一个ResponseResult对象实例，表示操作失败的结果。
    public static ResponseResult failResult(String message) {
        ResponseResult responseResult = new ResponseResult();
        responseResult.setCode(DEFAULT_FAIL_CODE);
        responseResult.setMessage(message);
        return responseResult;
    }



//    创建并返回一个失败的响应结果对象。
    public static ResponseResult failResult(int code, String message) {
        ResponseResult responseResult = new ResponseResult();
        responseResult.setCode(code);
        responseResult.setMessage(message);
        return responseResult;
    }

    public ResponseResult() {
    }

}