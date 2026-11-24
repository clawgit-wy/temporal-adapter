package com.yutong.safety.core.db.entity.enter;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SafetyEnterIssuanceDO implements Serializable {

    private Integer id;
    //备案类型
    private String fdEnterTypeCode;
    //姓名
    private String fdName;
    //身份证号
    private String fdCardNo;
    //证件照URL
    private String fdImg;
    //备案人联系电话
    private String fdPerPhone;
    //是否删除 0-否 1-是
    private Integer isDelete;
    //创建人
    private String createBy;
    //创建时间
    private Date createTime;
    //更新人
    private String updateBy;
    //更新时间
    private Date updateTime;
    //准入厂区编号
    private String enterFactoryCode;
    //准入厂区
    private String enterFactory;
    //下发时间
    private Date issuanceTime;
    //下发结构
    private String issuanceResult;
    //准入时间-开始
    private String enterStartTime;
    //准入时间-结束
    private String enterEndTime;
    //备案ID 物流车safety_enter_filing.id
    private String fdFilingId;
}
