package com.yutong.hksync.common;

/**
 * Created by gwcuiyx on 23-07-06.
 */
public class VO<T> {
    private String code;
    private String msg;
    private T data;

    public VO(String code, String msg) {
        this(code, msg, null);
    }

    public VO(String code, String msg, T data) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
