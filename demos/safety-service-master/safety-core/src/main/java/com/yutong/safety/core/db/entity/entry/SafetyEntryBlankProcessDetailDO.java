package com.yutong.safety.core.db.entity.entry;

import com.yutong.safety.common.vo.SafetyWebBaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SafetyEntryBlankProcessDetailDO extends SafetyWebBaseEntity implements Serializable {
    @ApiModelProperty("数据ID：修改时必填")
    private Integer id;
    @ApiModelProperty("主表数据行ID")
    private Integer processId;
    @ApiModelProperty("对接人员工号")
    private String buttJointCode;
    @ApiModelProperty("对接人名称")
    private String buttJointName;
    @ApiModelProperty("对接人手机号")
    private String buttJointPhone;
    @ApiModelProperty("备案类型")
    private String recordType;
    @ApiModelProperty("备案类型编码")
    private String recordTypeCode;
    @ApiModelProperty("所属公司")
    private String companyName;
    @ApiModelProperty("所属公司编码")
    private String companyCode;
    @ApiModelProperty("姓名")
    private String name;
    @ApiModelProperty("身份证号")
    private String cardNo;
    @ApiModelProperty("手机号")
    private String phone;
    @ApiModelProperty("准入厂区")
    private String enterFactory;
    @ApiModelProperty("准入厂区编码")
    private String enterFactoryCode;
    @ApiModelProperty("准入时间-开始")
    private String enterStartTime;
    @ApiModelProperty("准入时间-截止")
    private String enterEndTime;
    @ApiModelProperty("人脸照片")
    private String photoUrl;
    @ApiModelProperty("提交人手机号")
    private String submitPhone;
    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty("是否夜间工作 0-否 1-是")
    private Integer nightFlag;
    @ApiModelProperty("状态 0-待审批 1-审批通过  2-审批驳回")
    private String applyStatus;
    @ApiModelProperty("审批时间")
    private Date applyTime;
    @ApiModelProperty("审批意见")
    private String applyAudit;
    @ApiModelProperty("组织路径编码")
    private String orgCodePath;
    @ApiModelProperty("组织路径名称")
    private String orgNamePath;
    @ApiModelProperty("员工类型")
    private String positionType;
    /**
     * 黑名单类型 0: 安保黑名单， 1.友商黑名单
     */
    @ApiModelProperty("黑名单类型 0: 安保黑名单， 1.友商黑名单")
    private String blankType;
    /**
     * 非友商负责人
     */
    @ApiModelProperty("非友商负责人")
    private String competitorDirector;


    // 主表部分字段
    @ApiModelProperty("流程状态：00-废弃； 10-驳回； 20-待审核； 30-审核通过； 40：审核不通过")
    private String status;
    @ApiModelProperty("审批人工号")
    private String applyEmpNo;
    @ApiModelProperty("审批人名称")
    private String applyEmpName;
    @ApiModelProperty("审批人岗位")
    private String applyEmpPosition;
    @ApiModelProperty("流程ID")
    private String bpmFlowId;



    private static final long serialVersionUID = 1L;

    @Override
    public boolean saveOrUpdate() {
        return true;
    }
}