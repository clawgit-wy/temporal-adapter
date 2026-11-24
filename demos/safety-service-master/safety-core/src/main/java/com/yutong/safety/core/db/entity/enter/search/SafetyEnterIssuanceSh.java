package com.yutong.safety.core.db.entity.enter.search;

import com.yutong.safety.common.param.Page;
import lombok.Data;

import java.io.Serializable;

@Data
public class SafetyEnterIssuanceSh extends Page implements Serializable {

    private Integer id;
    //姓名
    private String fdName;
    //身份证号
    private String fdCardNo;
    //证件照URL
    private String fdImg;
    //是否删除 0-否 1-是
    private Integer isDelete;
    //创建人
    private String createBy;
    //创建时间
    private String createTime;
    //更新人
    private String updateBy;
    //更新时间
    private String updateTime;
    //准入厂区编号
    private String enterFactoryCode;
    //下发时间
    private String issuanceTime;
    //下发结构
    private String issuanceResult;
    //准入时间-开始
    private String enterStartTime;
    //准入时间-结束
    private String enterEndTime;
    //备案类型
    private String fdEnterTypeCode;

    private String downStartTime;
    private String downEndTime;

}
