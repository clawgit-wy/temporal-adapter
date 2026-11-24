package com.yutong.safety.core.db.entity.visitor.common;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

public interface SafetyVisitorConstants {

    Integer STATUS_NORMAL = 1;

    Map<Integer, String> STATUS_INFO = ImmutableMap.of(0, "审批中", 1, "正常", 2, "注销");

    /**
     * 授权人员类型
     */
    List<String> EHR_INTERVIEWEE_NAME = Lists.newArrayList("室级管理", "科级管理", "部级管理", "处级管理", "模块级管理", "院级管理");

    /**
     * 授权人科长的职种信息
     */
    List<String> EHR_APPLY_EMP_JOB_NAME = Lists.newArrayList("室级管理", "科级管理", "部级管理", "处级管理", "院级管理");
    /**
     * 模块级管理
     */
    String MODEL_MANAGER = "模块级管理";

    /**
     * 流程的基础数据
     */
    String VISITOR_MODEL_ID = "visitor_authorization_approval";
    String VISITOR_PROCESS_FORM_ID = "safety_visitor_authorization_approval";

    /**
     * 照片默认值
     */
    String ID_CARD_PHOTO_DEFAULT_VALUE = "T";
}
