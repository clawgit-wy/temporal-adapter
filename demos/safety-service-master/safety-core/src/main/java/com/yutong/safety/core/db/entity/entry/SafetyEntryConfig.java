package com.yutong.safety.core.db.entity.entry;

import com.yutong.safety.common.vo.SafetyWebBaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SafetyEntryConfig extends SafetyWebBaseEntity implements Serializable {
    @ApiModelProperty("数据ID：修改时必填")
    private Integer id;
    @ApiModelProperty("对接人员工号")
    private String buttJointCode;
    @ApiModelProperty("对接人员名称")
    private String buttJointName;
    @ApiModelProperty("对接人员电话")
    private String buttJointPhone;
    @ApiModelProperty("备案类型")
    private String recordType;
    @ApiModelProperty("备案类型编码")
    private String recordTypeCode;
    @ApiModelProperty("所属公司")
    private String companyName;
    @ApiModelProperty("公司代码")
    private String companyCode;
    @ApiModelProperty("姓名")
    private String name;
    @ApiModelProperty("身份证号")
    private String cardNo;
    @ApiModelProperty("联系电话")
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
    @ApiModelProperty("提交人手机号-外部供应商提交时必填信息")
    private String submitPhone;
    @ApiModelProperty("二维码")
    private String qrCode;
    @ApiModelProperty("是否夜间工作 0-否 1-是")
    private Integer nightFlag;
    @ApiModelProperty("状态 10-驳回 20-审批中 30-审批通过")
    private String status;
    @ApiModelProperty("审批时间")
    private Date applyTime;
    @ApiModelProperty("审批意见")
    private String applyAudit;
    @ApiModelProperty("未进厂天数")
    private Integer unEnterNum;
    @ApiModelProperty("是否黑名单 0-否 1-是")
    private Integer blankFlag;
    @ApiModelProperty("对应历史信息的ID-用于更新状态")
    private Integer historyId;
    @ApiModelProperty("入厂方式 0：刷脸+二维码，1：刷脸，2：二维码")
    private Integer entryMethod;

    private String num;
    private String nightOrDay;
    private String targetId;
    /**
     * 黑名单类型 0: 安保黑名单， 1.友商黑名单
     */
    private String blankType;
    /**
     * 非友商负责人
     */
    private String competitorDirector;

    private static final long serialVersionUID = 1L;

    @Override
    public boolean saveOrUpdate() {
        return this.id == null;
    }
}