package com.baijiacms.qsp.common;

import java.io.Serializable;

/**
 * @author：ChenXingYu
 * @date 2023/8/4 15:49
 */
public class ResponseResult<T>  implements Serializable {
    private int code;

    private String msg;
    public static final int STATE_ERROR = 500;
    public static final int STATE_OK = 200;
    public static final String MESSAGE_SUCCESS = "成功";

    /**
     * 结果集
     */
    private T result;


    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    public static  <T> ResponseResult<T> createErrorResult(String message) {
        ResponseResult<T> result = new ResponseResult<T>();
        result.setMsg(message);
        result.setCode(STATE_ERROR);
        return result;
    }
    public static <T> ResponseResult<T> createSuccessResult(T data, String message) {
        ResponseResult<T> result = new ResponseResult<T>();
        result.setResult(data);
        result.setMsg(message);
        result.setCode(STATE_OK);
        return result;
    }

    /**
     * 成功结构体
     * @param data
     * @param <T>
     * @return
     */
    public static <T> ResponseResult<T> createSuccessResult(T data) {
        return createSuccessResult( data, MESSAGE_SUCCESS);
    }
}
