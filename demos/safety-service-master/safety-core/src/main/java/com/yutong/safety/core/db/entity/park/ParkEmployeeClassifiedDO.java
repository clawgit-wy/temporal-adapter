package com.yutong.safety.core.db.entity.park;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 无涉密业务关联员工 实体类
 */
@Data
public class ParkEmployeeClassifiedDO implements Serializable {

    private int id;

    /**
     * 员工号(员工台账)
     */
    private String empNo;

    /**
     * 员工姓名(员工台账)
     */
    private String empName;
    /**
     * 部门路径编码(员工台账)
     */
    private String orgCodePath;
    /**
     * 部门路径(员工台账)
     */
    private String orgNamePath;
    /**
     * 组织编码(员工台账)
     */
    private String orgCode;
    /**
     * 组织名称(员工台账)
     */
    private String orgName;
    /**
     * 人员类型(员工台账)
     */
    private String userType;
    /**
     * 员工车辆 （safety_employee_private_car 员工入厂四家车信息表）
     */
    private String carNo;
    /**
     * 锁定车辆 （safety_employee_private_car 员工入厂四家车信息表 锁定的数据）
     */
    private String lockCarNo;
    /**
     * 授权厂区 （safety_park_employee_power_info 员工及车辆厂区权限明细）
     */
    private String factoryName;

    /**
     * 授权厂区编码 （safety_park_employee_power_info 员工及车辆厂区权限明细）
     */
    private String factoryCode;
    /**
     * 开始时间 （safety_park_employee_power_info 表的开始时间）
     */
    private String beginDate;
    /**
     * 结束时间 （safety_park_employee_power_info 表的结束时间）
     */
    private String endDate;
    /**
     * 授权厂区来源 （流程表）
     */
    private String targetId;
    /**
     * 厂区未进出天数
     */
    private int notInOutDay;
    /**
     * 是否白名单 （safety_build_white_list 楼宇门禁白名单）
     */
    private String whetherWhite;
    /**
     * 白名单添加角色 （safety_build_white_list 楼宇门禁白名单 添加人角色）
     */
    private String whiteRole;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改时间
     */
    private Date updateTime;
}
