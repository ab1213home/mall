package com.jiang.mall.domain;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * 响应结果类
 * 封装了操作结果，包括状态码、消息和数据等。
 * @author jiang
 * @param <T> 响应结果的数据类型，可以是任何类型
 *             默认为null
 */
@Getter
@Setter
public class ResponseResult<T> implements Serializable {

    /**
     * 状态码，用于标识操作的结果
     */
    private int code;

    /**
     * 消息，用于描述操作的结果
     */
    private String message;

    /**
     * 数据，用于返回操作的结果数据
     */
    private T data;

    /**
     * 是否成功，true表示成功，false表示失败
     */
    private boolean success;

    /**
     * 默认成功消息
     */
    private static final String DEFAULT_SUCCESS_MESSAGE = "默认成功消息提示";
    /**
     * 默认失败消息
     */
    private static final String DEFAULT_FAIL_MESSAGE = "fail";
    /**
     * 默认成功状态码
     */
    private static final int DEFAULT_SUCCESS_CODE = 200;
    /**
     * 默认失败状态码
     */
    private static final int DEFAULT_FAIL_CODE = 500;
    /**
     * 默认错误状态码
     */
    private static final int DEFAULT_ERROR_CODE = 400;
    /**
     * 默认未授权状态码
     */
    private static final int DEFAULT_UNAUTHORIZED_CODE = 401;
    /**
     * 默认禁止状态码
     */
    private static final int DEFAULT_FORBIDDEN_CODE = 403;
    /**
     * 默认未找到资源状态码
     */
    private static final int DEFAULT_NOT_FOUND_CODE = 404;
    /**
     * 默认服务器错误状态码
     */
    private static final int DEFAULT_SERVER_ERROR_CODE = 500;
    /**
     * 默认错误状态码
     */
    private static final int DEFAULT_BAD_REQUEST_CODE = 400;
    /**
     * 默认不可接受状态码
     */
    private static final int DEFAULT_NOT_ACCEPTABLE_CODE = 406;
    /**
     * 默认不可处理实体状态码
     */
    private static final int DEFAULT_UNPROCESSABLE_ENTITY_CODE = 422;
    /**
     * 默认服务器错误状态码
     */
    private static final int DEFAULT_INTERNAL_SERVER_ERROR_CODE = 500;
    /**
     * 默认未实现状态码
     */
    private static final int DEFAULT_NOT_IMPLEMENTED_CODE = 501;
    /**
     * 默认未找到资源状态码
     */
    private static final int DEFAULT_NOT_FOUND_RESOURCE_CODE = 404;
    /**
     * 创建一个表示未授权资源的响应结果对象
     */
    private static final int DEFAULT_UNAUTHORIZED_RESOURCE_CODE = 401;
    /**
     * 创建一个表示未找到资源的响应结果对象
     */
    private static final int DEFAULT_NOT_FOUND_RESOURCE_MESSAGE = 404;
    /**
     * 创建一个表示未授权资源的响应结果对象
     */
    private static final int DEFAULT_UNAUTHORIZED_RESOURCE_MESSAGE = 401;

    /**
     * 创建一个表示操作成功的响应结果，使用默认的成功消息
     *
     * @return 返回包含默认成功消息的响应结果对象
     */
    public static @NotNull ResponseResult okResult() {
        // 创建一个新的响应结果对象
        ResponseResult responseResult = new ResponseResult();
        // 设置响应结果的状态码为默认的成功码
        responseResult.setCode(DEFAULT_SUCCESS_CODE);
        // 设置响应结果的消息为传入的默认消息
        responseResult.setMessage(DEFAULT_SUCCESS_MESSAGE);
        // 返回构建好的响应结果对象
        return responseResult;
    }

    /**
     * 创建一个表示成功的响应结果对象
     * 此方法用于生成一个包含自定义消息的响应结果，用于告知调用者操作成功
     *
     * @param message 自定义的消息字符串，用于更详细的描述成功情况
     * @return ResponseResult 返回一个初始化为默认成功状态码和自定义消息的响应结果对象
     */
    public static @NotNull ResponseResult okResult(String message) {
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
    public static @NotNull ResponseResult okResult(Object data) {
        ResponseResult responseResult = new ResponseResult();
        responseResult.setCode(DEFAULT_SUCCESS_CODE);
        responseResult.setMessage("默认成功消息提示");
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
    public static @NotNull ResponseResult failResult(){
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
    public static @NotNull ResponseResult failResult(String message) {
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
    public static @NotNull ResponseResult failResult(int code, String message) {
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
        this.success=true;
    }

    /**
     * 创建一个表示未授权的响应结果
     * 此方法用于生成一个特殊的响应结果，指示用户未授权访问资源这是因为用户未登录或其他授权失败的情况
     *
     * @param message 错误消息，用于详细说明未授权的原因
     * @return 返回一个 ResponseResult 对象，其中包含了默认的未授权状态码和给定的错误消息
     */
    public static @NotNull ResponseResult notLoggedResult(String message) {
        ResponseResult responseResult = new ResponseResult();
        responseResult.setCode(DEFAULT_UNAUTHORIZED_CODE);
        responseResult.setMessage(message);
        return responseResult;
    }

    /**
     * 创建一个表示未找到资源的响应结果
     * 此方法用于生成一个特殊的响应结果，指示请求的资源不存在于服务器上，通常用于处理无效的请求路径等错误情况
     *
     * @param message 错误消息，用于详细说明未找到资源的原因
     * @return 返回一个 ResponseResult 对象，其中包含了默认的未找到资源状态码和给定的错误消息
     */
    public static @NotNull ResponseResult notFoundResourceResult(String message) {
        ResponseResult responseResult = new ResponseResult();
        responseResult.setCode(DEFAULT_NOT_FOUND_RESOURCE_CODE);
        responseResult.setMessage(message);
        return responseResult;
    }

    /**
     * 创建一个表示服务器错误的响应结果
     * 此方法用于生成一个特殊的响应结果，指示服务器内部发生错误，通常用于处理程序逻辑错误等错误情况
     *
     * @param message 错误消息，用于详细说明服务器错误的原因
     * @return 返回一个 ResponseResult 对象，其中包含了默认的服务器错误状态码和给定的错误消息
     */
    public static @NotNull ResponseResult serverErrorResult(String message) {
        ResponseResult responseResult = new ResponseResult();
        responseResult.setCode(DEFAULT_NOT_IMPLEMENTED_CODE);
        responseResult.setMessage(message);
        return responseResult;
    }

    /**
     * 创建一个表示操作成功的响应结果，同时携带数据对象
     * 此方法用于封装一个成功的响应结果，同时携带数据对象，用于返回API调用的结果
     * 它会创建一个新的ResponseResult实例，设置默认的成功状态码和成功消息，并将给定的数据对象封装进去
     *
     * @param data 成功响应中要封装的数据对象，可以是任何类型的对象
     * @param message 响应结果的消息，用于提供额外的信息
     * @return 返回一个填充了默认成功状态、数据对象和消息的ResponseResult对象
     */
    public static @NotNull ResponseResult okResult(Object data, String message) {
        ResponseResult responseResult = new ResponseResult();
        responseResult.setCode(DEFAULT_SUCCESS_CODE);
        responseResult.setData(data);
        responseResult.setMessage(message);
        return responseResult;
    }

    /**
     * 判断当前响应结果是否表示成功状态
     *
     * @return 如果响应结果的状态码等于默认的成功状态码，则返回true；否则返回false
     */
    public boolean isSuccess() {
	    return code == DEFAULT_SUCCESS_CODE;
    }
}