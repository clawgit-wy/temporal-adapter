package com.yutong.modules.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description: EHR员工信息列表查询参数
 * @Author: jeecg-boot
 * @Date:   2026-07-06
 * @Version: V1.0
 */
@Data
@ApiModel(value="员工信息列表查询参数", description="EHR员工信息列表查询参数")
public class EmployeeMessageListParam {

    @ApiModelProperty(value = "页码")
    private Integer pageNo = 1;

    @ApiModelProperty(value = "每页条数")
    private Integer pageSize = 15;

    @ApiModelProperty(value = "员工号（模糊匹配）")
    private String empNo;

    @ApiModelProperty(value = "员工号（精确匹配）")
    private String fullEmpNo;

    @ApiModelProperty(value = "姓名")
    private String empName;

    @ApiModelProperty(value = "手机号")
    private String phoneNo;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "人员类型 ZS:正式员工,LD:绿都员工,WX:外协员工")
    private String userType;

    @ApiModelProperty(value = "类别")
    private String positionType;

    @ApiModelProperty(value = "岗位名称")
    private String positionName;

    @ApiModelProperty(value = "组织编码")
    private String orgCode;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "创建日期-起")
    private String createBegin;

    @ApiModelProperty(value = "创建日期-止")
    private String createEnd;

    @ApiModelProperty(value = "组织路径包含关键字")
    private String orgPatchName;

    @ApiModelProperty(value = "组织路径不包含关键字")
    private String noOrgPatchName;

    @ApiModelProperty(value = "组织路径为空标识")
    private String nullOrgPathName;

    @ApiModelProperty(value = "人脸下发标记 0:未下发 1:已下发")
    private Integer faceIssueFlag;

    @ApiModelProperty(value = "卡下发标记 0:未下发 1:已下发")
    private Integer cardIssueFlag;

    @ApiModelProperty(value = "排除的人员类型")
    private String noUserType;

    @ApiModelProperty(value = "员工号列表")
    private List<String> empNoList;

    @ApiModelProperty(value = "员工号+姓名联合搜索")
    private String empNoAndName;

    @ApiModelProperty(value = "员工号排除前缀")
    private String noEmpNoStart;

    @ApiModelProperty(value = "组织路径编码")
    private String orgPathCode;

    @ApiModelProperty(value = "岗位名称列表")
    private List<String> positionNames;

    @ApiModelProperty(value = "职类（逗号分隔）")
    private String empTypes;

    @ApiModelProperty(value = "职种（逗号分隔）")
    private String jobTypeNames;
}
