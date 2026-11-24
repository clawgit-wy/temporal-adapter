package com.yutong.modules.user.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="安全责任人变更流程详情映射对象", description="安全责任人变更流程明细表")
public class YtsafetyResponsibleProcessDetailsVO {
    /**主键*/
    @ApiModelProperty(value = "主键")
    private Long id;
    /**流程主键*/
    @ApiModelProperty(value = "流程主键")
    private Long processId;
    /**变更人员主键*/
    @ApiModelProperty(value = "变更人员主键")
    private Integer changeUserId;
    /**员工号*/
    @ApiModelProperty(value = "员工号")
    private String changeEmpNo;
    /**用户名*/
    @ApiModelProperty(value = "用户名")
    private String changeEmpName;
    /**用户主键*/
    @ApiModelProperty(value = "用户主键")
    private Integer userId;
    /**用户名*/
    @ApiModelProperty(value = "用户名")
    private String empName;
    /**员工号*/
    @ApiModelProperty(value = "员工号")
    private String empNo;
    /**EIP用户名*/
    @ApiModelProperty(value = "EIP用户名")
    private String userName;
    /**手机号*/
    @ApiModelProperty(value = "手机号")
    private String phone;
    /**组织架构*/
    @ApiModelProperty(value = "组织架构")
    private String orgName;
    /**岗位*/
    @ApiModelProperty(value = "岗位")
    private String positionName;
    /**邮件*/
    @ApiModelProperty(value = "邮件")
    private String email;
    /**人员类型 0 非保卫 1 保卫人员*/
    @ApiModelProperty(value = "人员类型 0 非保卫 1 保卫人员")
    private Integer userType;
    /**有效期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "有效期")
    private Date validPeriod;
    /**状态:0 正常 1 禁用*/
    @ApiModelProperty(value = "状态:0 正常 1 禁用")
    private Integer status;
    /**变更类型 0 新增 1 变更*/
    @ApiModelProperty(value = "变更类型 0 新增 1 变更")
    private Integer changeType;
    /**授权厂区*/
    @ApiModelProperty(value = "授权厂区")
    private String factoryId;
    /**授权厂区*/
    @ApiModelProperty(value = "授权厂区名称")
    private String factoryNames;
    /**责任部门*/
    @ApiModelProperty(value = "责任部门")
    private String departId;
    /**责任部门*/
    @ApiModelProperty(value = "责任部门名称")
    private String departNames;
    /**授权角色*/
    @ApiModelProperty(value = "授权角色")
    private String roleId;
    /**授权角色*/
    @ApiModelProperty(value = "授权角色名称")
    private String roleNames;
    /**备注*/
    @ApiModelProperty(value = "备注")
    private String remark;
}
