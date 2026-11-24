package com.yutong.safety.common.utils;


import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;

/**
 * create gwcuiyx 2023.07.10
 */
public class PreconditonsEx {
    public static void checkArgument(boolean expression, ResponseEnum responseEnum) {
        if(!expression) {
            throw new BizException(responseEnum) ;
        }
    }

    public static void checkArgument(boolean expression, String code, String msg) {
        if(!expression) {
            throw new BizException(code, msg) ;
        }
    }

    public static void checkArgumentNull(boolean expression, String code, String msg) {
        if(expression) {
            throw new BizException(code, msg) ;
        }
    }
}
