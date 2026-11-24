package com.yutong.modules.user.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

@Data
public class SafetyEmployeeMessageVO {

    /**员工号*/
    @Excel(name = "员工号", width = 15)
    @ApiModelProperty(value = "员工号")
    private String empNo;
    /**姓名*/
    @Excel(name = "员工名", width = 15)
    @ApiModelProperty(value = "员工名")
    private String empName;
    @Excel(name = "eip用户名", width = 15)
    @ApiModelProperty(value = "eip用户名")
    private String userName;
    /**手机号*/
    @Excel(name = "手机号", width = 15)
    @ApiModelProperty(value = "手机号")
    private String phoneNo;
    /**电子邮箱*/
    @Excel(name = "电子邮箱", width = 15)
    @ApiModelProperty(value = "电子邮箱")
    private String email;
    /**组织名称*/
    @Excel(name = "组织名称", width = 15)
    @ApiModelProperty(value = "组织名称")
    private String orgName;
    /**组织名称*/
    @Excel(name = "组织名称id", width = 15)
    @ApiModelProperty(value = "组织名称id")
    private String orgCode;

    @ApiModelProperty(value = "所属工厂名称")
    private String factoryName;

    @ApiModelProperty(value = "岗位")
    private String positionName;

    @ApiModelProperty(value = "所属工厂主键")
    private Long factoryId;

    @ApiModelProperty(value = "用户类型 0 非安保人员 1 安保人员")
    private String userType;

    @ApiModelProperty(value = "用户主键")
    private Long userId;

}
