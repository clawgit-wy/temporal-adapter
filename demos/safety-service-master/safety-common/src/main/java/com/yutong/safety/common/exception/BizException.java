package com.yutong.safety.common.exception;

import com.yutong.safety.common.constants.ResponseEnum;

/**
 * create gwcuiyx 23-07-06.
 */
public class BizException extends RuntimeException {
    private String code ;
    private String msg ;

    public BizException(ResponseEnum responseEnum) {
        this.code = responseEnum.getCode() ;
        this.msg = responseEnum.getName() ;
    }

    public BizException(String code, String msg) {
        this.code = code ;
        this.msg = msg ;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
