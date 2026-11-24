package com.yutong.safety.core.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author caomg
 * @date 2024-9-12 10:46
 * @description ParamDTO
 */
@Data
public class ParamDTO extends SafetySendEhrDTO implements Serializable {
    private List<Integer> idList;
    private String eip;

    //下发权限需要参数
    private String empNo;
    private String empName;
    private String factoryId;
    private String factoryName;
    private String powerEndTime;

    private String powerBeginTime;
    private String carNo;
    private String phoneNo;
    //yyyy-MM-dd
    private String beginDate;
    //yyyy-MM-dd
    private String endDate;
    /**
     * 门禁编码（员工所拥有的门禁）
     */
    private String deviceCode;
    /**
     * 门禁名称（员工所拥有的门禁）
     */
    private String deviceName;

    /**
     * 0或1 0：下发， 1：注销
     */
    private String type;

    /**
     * 人员类型 WX、ZS
     */
    private String userType;

    /**
     * 组织ID 接口使用
     */
    private String orgId;
    /**
     * rid 接口使用
     */
    private String curId;

    /**
     * 组织名称(日志使用)
     */
    private String orgNamePath;
    /**
     * 组织编码(日志使用)
     */
    private String orgCodePath;

    /**
     * 操作人 （日志使用）
     */
    private String createBy;

    /**
     * 流程ID （日志使用）
     */
    private String bpmFlowId;

    private String photoUrl;
}
