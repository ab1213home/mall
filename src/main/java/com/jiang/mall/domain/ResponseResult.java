package com.jiang.mall.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
/**
 * 响应结果类
 * 封装了操作结果，包括状态码、消息和数据等。
 * @author jiang
 * @param <T> 响应结果的数据类型，可以是任何类型
 *             默认为null
 */
public class ResponseResult<T> implements Serializable {

    private int code;
    private String message;
    private T data;

    private static final String DEFAULT_SUCCESS_MESSAGE = "success";
    private static final String DEFAULT_FAIL_MESSAGE = "fail";
    private static final int DEFAULT_SUCCESS_CODE = 200;
    private static final int DEFAULT_FAIL_CODE = 500;

    /**
     * 创建一个表示操作成功的响应结果，使用默认的成功消息
     *
     * @return 返回包含默认成功消息的响应结果对象
     */
    public static ResponseResult okResult() {
        return okResult(DEFAULT_SUCCESS_MESSAGE);
    }

    /**
     * 创建一个表示成功的响应结果对象
     * 此方法用于生成一个包含自定义消息的响应结果，用于告知调用者操作成功
     *
     * @param message 自定义的消息字符串，用于更详细的描述成功情况
     * @return ResponseResult 返回一个初始化为默认成功状态码和自定义消息的响应结果对象
     */
    public static ResponseResult okResult(String message) {
        // 创建一个新的响应结果对象
        ResponseResult responseResult = new ResponseResult();
        // 设置响应结果的状态码为默认的成功码
        responseResult.setCode(DEFAULT_SUCCESS_CODE);
        // 设置响应结果的消息为传入的自定义消息
        responseResult.setMessage(message);
        // 返回构建好的响应结果对象
        return responseResult;
    }

    /**
     * 创建一个表示成功的响应结果对象
     * 此方法用于封装一个成功的响应结果，广泛用于返回API调用的结果
     * 它会创建一个新的ResponseResult实例，设置默认的成功状态码和成功消息，并将给定的数据对象封装进去
     *
     * @param data 成功响应中要封装的数据对象，可以是任何类型的对象
     * @return 返回一个填充了默认成功状态和给定数据的ResponseResult对象
     */
    public static ResponseResult okResult(Object data) {
        ResponseResult responseResult = new ResponseResult();
        responseResult.setCode(DEFAULT_SUCCESS_CODE);
        responseResult.setMessage(DEFAULT_SUCCESS_MESSAGE);
        responseResult.setData(data);
        return responseResult;
    }

    /**
     * 创建一个失败结果的响应对象
     * 将该对象的状态码设置为预定义的失败状态码。
     * 将该对象的消息设置为预定义的失败消息
     *
     * @return 返回一个表示失败结果的ResponseResult对象
     */
    public static ResponseResult failResult(){
        ResponseResult responseResult = new ResponseResult();
        responseResult.setCode(DEFAULT_FAIL_CODE);
        responseResult.setMessage(DEFAULT_FAIL_MESSAGE);
        return responseResult;
    }

    /**
     * 创建一个表示操作失败的响应结果
     *
     * @param message 失败的详细信息信息
     * @return 包含失败信息的响应对象
     */
    public static ResponseResult failResult(String message) {
        // 创建一个新的响应对象
        ResponseResult responseResult = new ResponseResult();
        // 设置响应代码为默认的失败代码
        responseResult.setCode(DEFAULT_FAIL_CODE);
        // 设置失败的详细信息信息
        responseResult.setMessage(message);
        // 返回包含失败信息的响应对象
        return responseResult;
    }

    /**
     * 创建一个表示操作失败的响应结果
     *
     * @param code 错误码，用于标识错误的类型
     * @param message 错误消息，用于描述错误的详细信息
     * @return 返回包含指定错误码和错误消息的响应结果对象
     */
    public static ResponseResult failResult(int code, String message) {
        // 创建一个新的响应结果对象
        ResponseResult responseResult = new ResponseResult();
        // 设置响应结果的错误码
        responseResult.setCode(code);
        // 设置响应结果的错误消息
        responseResult.setMessage(message);
        // 返回响应结果对象
        return responseResult;
    }

    public ResponseResult() {
    }

}