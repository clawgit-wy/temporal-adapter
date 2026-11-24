package com.yutong.hksync.common;


/**
 * 返回对象工具类
 */
public class VoUtil {
    public static VO success() {
        return new VO(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getName());
    }

    public static VO success(String msg) {
        return new VO(ResponseEnum.SUCCESS.getCode(), msg);
    }

    public static <T> VO successData(T data) {
        return new VO<T>(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getName(), data);
    }

    public static <T> VO successData(String code, T data) {
        return new VO<>(code, ResponseEnum.SUCCESS.getName(), data);
    }

    public static <T> VO successMsg(String msg, T data) {
        return new VO<>(ResponseEnum.SUCCESS.getCode(), msg, data);
    }

    public static VO fail() {
        return new VO(ResponseEnum.FAIL.getCode(), ResponseEnum.FAIL.getName());
    }

    public static VO fail(String code, String msg) {
        return new VO(code, msg);
    }

    public static VO fail(String msg) {
        return new VO(ResponseEnum.FAIL.getCode(), msg);
    }

    public static <T> VO fail(String msg, T data) {
        return new VO<>(ResponseEnum.FAIL.getCode(), msg, data);
    }

    public static VO error() {
        return new VO(ResponseEnum.PARAM_ERROR.getCode(), ResponseEnum.PARAM_ERROR.getName());
    }

    public static VO exception() {
        return new VO(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getName());
    }

    public static VO exception(String msg) {
        return new VO(ResponseEnum.EXCEPTION.getCode(), msg);
    }

    public static VO response(ResponseEnum res) {
        return new VO(res.getCode(), res.getName());
    }
}
