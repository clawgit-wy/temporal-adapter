package com.yutong.safety.core.db.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SafetyEmployeeMessageDO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    @ApiModelProperty("员工号-唯一")
    private String empNo;

    @ApiModelProperty("姓名")
    private String empName;

    @ApiModelProperty("手机号")
    private String phoneNo;

    @ApiModelProperty("人员类型ZS:正式员工,LD:绿都员工,WX:外协员工")
    private String userType;

    @ApiModelProperty("岗位编码")
    private String positionCode;

    @ApiModelProperty("岗位名称")
    private String positionName;

    @ApiModelProperty("类别")
    private String positionType;

    @ApiModelProperty("部门路径")
    private String orgNamePath;

    @ApiModelProperty("部门code路径")
    private String orgCodePath;

    @ApiModelProperty("上级组织code")
    private String parentCode;

    @ApiModelProperty("组织代码")
    private String orgCode;

    @ApiModelProperty("组织名称")
    private String orgName;
    @ApiModelProperty("电子邮箱")
    private String email;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("状态(正常:NORMAL,离职:DISABLED)")
    private String status;

    @ApiModelProperty("员工登录名")
    private String empLogin;

    @ApiModelProperty("人脸下发标记 0-待下发 1-已下发")
    private Short faceIssueFlag;

    @ApiModelProperty("卡下发标记 0-待下发 1-已下发")
    private Short cardIssueFlag;

    @ApiModelProperty("员工卡ID")
    private String employeeCardId;

    @ApiModelProperty("性别")
    private String sex;

    @ApiModelProperty("员工类型")
    private String empType;

    @ApiModelProperty("员工类型编码")
    private String empTypeCode;

    @ApiModelProperty("职种")
    private String jobTypeName;

    @ApiModelProperty("照片")
    private String photo;

    private Date photoUpdateTime;
}