package com.yutong.safety.core.db.entity.entry.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 外来人员入厂次数VO
 */
@Data
public class EntryFactoryRecordVO implements Serializable {

    @ApiModelProperty("对接人员工号")
    private String buttJointCode;
    @ApiModelProperty("身份证号")
    private String cardNo;
    @ApiModelProperty("备案类型编码")
    private String recordTypeCode;
    @ApiModelProperty("备案次数")
    private Integer recordNum;
}
