package com.yutong.safety.core.dto.build;

import com.yutong.safety.core.db.entity.build.SafetyBuildDeviceDO;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class SafetyBuildDeviceDTO implements Serializable {
    private String id;

    /**
     * 门编码-分控器ID
     */
    private String deviceCode;

    /**
     * 门名称-分控器
     */
    private String deviceName;

    /**
     * 厂区名
     */
    private String factoryName;

    /**
     * 厂区编码
     */
    private String factoryCode;

    /**
     * 楼栋
     */
    private String building;

    /**
     * 保密等级
     */
    private String securityLevel;

    /**
     * 状态 在线 离线
     */
    private String deviceStatus;

    /**
     * 是否拆除 0-否 1-是
     */
    private String removeFlag;

    /**
     * 门禁名称-安保平台定义
     */
    private String anDeviceName;

    /**
     * 厂区名-安保平台定义
     */
    private String anFactoryName;

    /**
     * 厂区编码-安保平台定义
     */
    private String anFactoryCode;

    /**
     * 楼栋-安保平台定义
     */
    private String anBuildingCode;

    /**
     * 楼栋-安保平台定义
     */
    private String anBuilding;

    /**
     * 序号--安保平台定义
     */
    private Integer anShort;

    /**
     * 备注--安保平台定义
     */
    private String anRemark;

    /**
     * 是否删除 0-否 1-是
     */
    private String isDelete;

    /**
     * 离线时间
     */
    private Date officeTime;

    /**
     * 变更时间
     */
    private Date updateTime;

    /**
     * 是否公告区域 0-否 1-是
     */
    private String isPublic;

    /**
     * 门禁-区域ID
     */
    private Integer areaId;

    /**
     * 门禁-区域名称
     */
    private String areaName;

    /**
     * 门禁-设备ID
     */
    private Integer termId;

    /**
     * 终端IP地址
     */
    private String termAddr;

    /**
     * 门禁-门编码 分控器编码，一个设备下可能有多个分控器，以ID来区分
     */
    private String extendCode;

    /**
     * 设备名称
     */
    private String termName;

    /**
     * 设备类型名称
     */
    private String termType;

    /**
     * 区域层级 1园区、2楼栋、3单元、4楼层、5房间
     */
    private Integer levf;

    /**
     * 绝密负责人员工号
     */
    private String topSecretEmpCode;

    /**
     * 绝密负责人登录名
     */
    private String topSecretEmpLogin;

    /**
     * 绝密负责人姓名
     */
    private String topSecretEmpName;

    private static final long serialVersionUID = 1L;

    private Date createTime;

    private List<SafetyBuildPowerInfoDTO> powerList;
    private List<SafetyBuildDeviceDO> deviceDOList;
}