package com.yutong.modules.risk.constants;

/**
 * @author gwcuiyx
 * @date 2024-1-19 10:00
 */
public class YTConstants {
    /**
     * 文件导出大小限制
     */
    public static final int FILE_SIZE = 5000;

    /**
     * 中台数据在职员工标识
     */
    public static final String NORMAL = "NORMAL";

    /**
     * 中台数据离职员工标识
     */
    public static final String DISABLED = "DISABLED";

    /**
     * 是否当被访人是
     */
    public static final Integer STATE_TRUE = 0;

    /**
     * 是否当被访人否
     */
    public static final Integer STATE_FALSE = 1;

    public static final String MP_TOKEN_KEY = "MP-X-Access-Token";

    public static final String EIP_TOKEN_KEY = "LRToken";

    public static final String REDIS_TOKEN_PRE = "safety:token:";
    public static final String REDIS_ENTRY_PHONE_PRE = "safety:entry:phone:";
    public static final String REDIS_ENTRY_TOKEN_PRE = "safety:entry:token:";
    public static final String REDIS_BLACK_YXT_MSG_PRE = "safety:blankYxt:msg:";
    public static final String REDIS_AUTO_DEPT_YXT_MSG_PRE = "safety:autoDeptYxt:msg:";
    /**
     * 过期时间
     */
    public static final long REDIS_ENTRY_TOKEN_EXPIR_TIME = 3000;

    public static final String USER_TYPE_LD = "LD"; //员工类型-LD
    public static final String USER_TYPE_WX = "WX"; //员工类型-WX
    public static final String ORG_NAME_KEYS = "绿都";
    //默认服务截止日期
    public static final String DEFAULT_SERVER_END = "2099-12-31";
    /**
     * 成功
     */
    public static final String Y = "Y";
    /**
     * 失败
     */
    public static final String N = "N";

    public static final String FLOW_STATUS_SUCCESS="30";
    
    public static final String BLANK_FIRST_SOURCE="EHR导入";
    public static final String FILLING_USER_TYPE_CODE = "enter_per_type";

    /**
     * BPM 流程状态
     */
    public static final String BPM_HANDLER_SUBMIT = "drafter_submit";
    public static final String BPM_HANDLER_PASS = "handler_pass";
    public static final String BPM_HANDLER_REFUSE = "handler_refuse";
    public static final String BPM_HANDLER_ABANDON = "handler_abandon";
}
