package com.yutong.safety.core.db.entity.bpm.operation;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * BPM-人员信息VO
 * @author gwxiexj
 */
@Data
public class BpmUserVO implements Serializable {

    @ApiModelProperty("登录名")
    private String loginName;
    @ApiModelProperty("姓名")
    private String name;
    @ApiModelProperty("唯一ID")
    private String id;
}
