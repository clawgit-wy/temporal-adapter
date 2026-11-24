package com.yutong.safety.core.db.entity.entry.common;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import java.util.Map;

/**
 * 入场备案 公共参数说明
 */
public interface SafetyEntryConstants {

    /**
     * 外部供应商注册信息通过或驳回的短信提醒
     */
    String COMPANY_REGISTER_SMS_OK_CONTENT = "【宇通客车】您备案入厂平台注册审核通过，可登录使用。如有疑问可与对接人联系。";

    /**
     * 外部供应商注册信息通过或驳回的短信提醒
     */
    String COMPANY_REGISTER_SMS_FAIL_CONTENT = "【宇通客车】您备案入厂平台注册审核不通过，可与对接人沟通后重新申请。";

    /**
     * 外部供应商备案信息表状态-通过
     */
    String COMPANY_REGISTER_STATUS_PASS = "30";

    /**
     * 外部供应商备案信息表状态-驳回
     */
    String COMPANY_REGISTER_STATUS_REJECT = "10";

    /**
     * 外部供应商备案信息表状态-审批中
     */
    String COMPANY_REGISTER_STATUS_WAIT = "20";

    /**
     * 外部供应商备案信息表状态集
     */
    Map<String, String> COMPANY_REGISTER_STATUS = ImmutableMap.of("10", "驳回", "20", "待审批", "30", "通过");

    /**
     * 是否夜间作业的结果
     */
    List<String> NIGHT_OR_DAYS = Lists.newArrayList("是", "否");

    /**
     * 入场备案信息表状态-驳回
     */
    String ENTRY_CONFIG_STATUS_REJECT = "10";

    /**
     * 入场备案信息表状态-审批中
     */
    String ENTRY_CONFIG_STATUS_WAIT = "20";

    /**
     * 入场备案信息表状态-超时未审批
     */
    String ENTRY_CONFIG_STATUS_TIMEOUT = "25";

    /**
     * 入场备案信息表状态-通过
     */
    String ENTRY_CONFIG_STATUS_PASS = "30";

    /**
     * 入场备案信息表状态-注销
     */
    String ENTRY_CONFIG_STATUS_LOGOUT = "40";

    /**
     * 备案系统的备案类型在数据字典的类型值
     */
    String ENTRY_DICT_SUPPER_CODE_TYPE = "enter_per_type";
    /**
     * 备案系统的备案类型在数据字典的展示标记
     */
    String ENTRY_DICT_SHOW_FLAG_VALUE = "1";

    /**
     * 黑名单入场备案记录的状态
     */
    Map<String, String> ENTRY_BLACK_STATUS = ImmutableMap.of("10", "驳回", "20", "待审核", "30", "审核通过", "00", "废弃", "40", "审核不通过");

    /**
     * 黑名单入场备案记录状态-待审核
     */
    String ENTRY_BLACK_PROCESS_STATUS_WAIT = "20";

    /**
     * 黑名单入场备案记录状态-通过
     */
    String ENTRY_BLACK_PROCESS_STATUS_PASS = "30";
    /**
     * 黑名单入场备案记录状态-驳回
     */
    String ENTRY_BLACK_PROCESS_STATUS_REJECT = "10";

    /**
     * 黑名单入厂申请明细状态-待审批
     */
    String ENTRY_BLACK_PROCESS_DETAIL_STATUS_WAIT = "0";

    /**
     * 黑名单入厂申请明细状态-审批通过
     */
    String ENTRY_BLACK_PROCESS_DETAIL_STATUS_PASS = "1";

    Map<String, String> ENTRY_BLACK_PROCESS_DETAIL_STATUS = ImmutableMap.of("0", "待审批", "1", "审批通过", "2", "审批驳回");

    String SMS_CODE_TYPE_LOGIN = "LOGIN";

    String SMS_CODE_TYPE_REGION = "REGION";

    /**
     * 流程的基础数据
     */
    String ENTRY_PROCESS_MODEL_ID = "entry_blank_process";
    String ENTRY_PROCESS_FORM_ID = "safety_entry_blank_process";

    /**
     * 黑名单入场备案-状态
     */
    int ENTRY_BPM_TYPE_CREAT = 1;
    int ENTRY_BPM_TYPE_REVIEW = 2;

    String CONFIG_FAILE_BLANK1 = "禁止入厂-一级黑名单";
    String CONFIG_FAILE_BLANK2 = "禁止入厂-二级黑名单";

    String ENTRY_ROLE_ADMIN = "超级管理员";

    //备案入厂EIP代办唯一标识
    String EIPTODO_CONFIG_MODELID = "ENTRY_CONFIG";
    //备案入厂EIP代办跳转页
    String EIPTODO_CONFIG_PAGE="entry_factory_config_todo";
    String EIPTODO_CONFIG_MODEL="entry_factory_config";

    /**
     * 受限人员申请数据权限排除人员
     */
    List<String> DATA_PERMISSION_EMP = Arrays.asList("90019864", "90100755");
}
