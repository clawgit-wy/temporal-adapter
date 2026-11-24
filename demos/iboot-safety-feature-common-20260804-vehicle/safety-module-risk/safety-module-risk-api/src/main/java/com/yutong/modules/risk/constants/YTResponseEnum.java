package com.yutong.modules.risk.constants;

/**
 * create gwcuiyx 23-07-06.
 */
public enum YTResponseEnum {
    SUCCESS("0", "操作成功"),
    FAIL("1", "操作失败，请稍后重试"),
    RELOGIN("1000", "重新登录") ,
    LOGIN_PARAM_ERROR("1001", "手机号或密码有误"),
    PARAM_ERROR("2000", "请求参数有误") ,
    NOTICE_TEMPLATE_NOT_EXIT("3001", "消息模板不存在！") ,
    File_NOT_EXIT("3006", "文件不存在！"),
    File_FORMART_ERROR("3007", "文件格式错误！"),
    EXCEPTION("9999", "系统异常，请稍后重试"),
    DB_DATA_NOT_EXIT("3001", "操作失败,数据不存在"),
    TOCKENFAIL("401", "token loss");

    private String code ;
    private String name ;

    YTResponseEnum(String code, String name) {
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
