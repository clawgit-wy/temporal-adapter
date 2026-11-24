package com.yutong.safety.core.dto;

import com.yutong.safety.common.param.web.ProcessParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class SafetyOutsiderPromiseChangeDTO{

    @ApiModelProperty("流程表主键ID")
    private String id;
    @ApiModelProperty("流程ID")
    private String flowId;
    @ApiModelProperty("流程主题")
    private String title;
    @ApiModelProperty("流程创建人员工号")
    private String creator;
    @ApiModelProperty("流程创建时间")
    private Date createTime;
    @ApiModelProperty("流程发布时间")
    private Date publishTime;
    @ApiModelProperty("流程状态")
    private String status;
    @ApiModelProperty("流程编号")
    private String flowNo;
    @ApiModelProperty("创建人姓名")
    private String creatorName;
    @ApiModelProperty("创建人部门")
    private String creatorDept;
    @ApiModelProperty("联系方式")
    private String creatorPhone;
    @ApiModelProperty("业务说明")
    private String serviceDesc;
    @ApiModelProperty("任职部门科长")
    private String sectionChiefNo;
    @ApiModelProperty("任职部门科长姓名")
    private String sectionChiefName;
    @ApiModelProperty("任职部门科长EIP")
    private String sectionChiefUsername;
    @ApiModelProperty("任职部门负责人")
    private String deptHeadNo;
    @ApiModelProperty("任职部门负责人姓名")
    private String deptHeadName;
    @ApiModelProperty("任职部门负责人EIP")
    private String deptHeadUsername;
    @ApiModelProperty("是否涉密")
    private String secret;
    @ApiModelProperty("涉密区负责人EIP")
    private String secretAuditor;

    @ApiModelProperty("流程参数")
    private ProcessParam processParam;
    @ApiModelProperty("明细表数据")
    private String detailDTOs;
    @ApiModelProperty("现有权限数据")
    private String oldDetailDTOs;

    @ApiModelProperty("当前登录人")
    private String loginName;
    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty("授权工厂-ID拼接串")
    private String factory;
    @ApiModelProperty("服务截止日期")
    private String serviceEndDate;
}
