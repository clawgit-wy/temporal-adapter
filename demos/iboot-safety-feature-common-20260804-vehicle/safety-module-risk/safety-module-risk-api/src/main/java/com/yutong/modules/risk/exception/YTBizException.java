package com.yutong.modules.risk.exception;

import com.yutong.modules.risk.constants.YTResponseEnum;

/**
 * create gwcuiyx 23-07-06.
 */
public class YTBizException extends RuntimeException {
    private String code ;
    private String msg ;

    public YTBizException(YTResponseEnum responseEnum) {
        this.code = responseEnum.getCode() ;
        this.msg = responseEnum.getName() ;
    }

    public YTBizException(String code, String msg) {
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
