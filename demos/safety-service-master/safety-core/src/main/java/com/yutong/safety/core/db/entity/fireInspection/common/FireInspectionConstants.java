package com.yutong.safety.core.db.entity.fireInspection.common;

/**
 * 消防巡检公共的变量
 */
public interface FireInspectionConstants {

    /**
     * 任务详情明细-检查结果
     */
    String FIRE_NORMAL = "正常";
    String FIRE_ABNORMAL = "异常";

    /**
     * 任务详情-任务状态
     * 任务详情整改结果整改完时，也用已完成
     */
    String FIRE_TASK_COMPLETED = "已完成";
    String FIRE_TASK_INCOMPLETE = "未完成";

    /**
     * 任务详情，检查结果为异常时 检查结果的后缀和整改结果的后缀
     */
    String INSPECTION_RESULT_SUFFIX = "项异常";
    String RECTIFICATION_RESULT_SUFFIX = "项未整改";

    /**
     * 任务详情明细的整改结果，0：整改中； 1：整改完成
     */
    long RECTIFICATION_N = 0L;

    /**
     * 任务设置列表-任务对象类型 0：默认值； 1：自定义
     */
    Long TASK_OBJECT_TYPE_DEFAULT = 0L;
    Long TASK_OBJECT_TYPE_CUSTOMIZE = 1L;

    /**
     * 消息开关 0:关；  1:开
     */
    String SEND_MESSAGE_OFF = "XFXJ_SEND_MSG_TODO";

    /**
     * EIP 流程状态 00 废弃， 30 发布
     */
    String FQ = "00";
    String FB = "30";

    /**
     * 管理员角色
     */
    String FIRE_INSPECTION_ROLE = "消防管理员,超级管理员";
}
