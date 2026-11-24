package com.yutong.safety.core.dto.wechat;

/**
 * Created by jackliu on 16-12-1.
 */
public enum ResponseStatusEnum {
    SUCCESS("0", "操作成功"),
    FAIL("1", "操作失败"),
    RELOGIN("1000", "重新登录") ,
    LOGIN_PARAM_ERROR("1001", "手机号或密码有误"),
    PARAM_ERROR("2000", "请求参数有误"),
    NOT_OPENID("3001","手机号没有对应openId"),
    NOT_WX_TOKEN("3002","获取微信token失败"),
    NOT_ACCOUNT("3003","没有该账号"),
    ERROR_SIGN("3004","签名错误"),
    TOKEN_EXPIRE("3004","token失效"),
    TOKEN_ERROR("3005","token有误"),
    RATING_LIMIT("3006","请求次数超过上限"),
    EXCEPTION("9999", "系统异常，请稍后重试") ;

    private String code ;
    private String name ;

    ResponseStatusEnum(String code, String name) {
        this.code = code ;
        this.name = name ;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}