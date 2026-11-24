package com.yutong.safety.core.db.entity.park;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 园区权限-人车权限默认授权规则DO
 * @since 2025-04-12 13:50:00
 * @author gwxiexj
 */
@Data
public class SafetyParkPowerRuleDO implements Serializable {

    private Integer id;

    private String postName;

    private String orgCode;

    private String remark;

    private Integer priority;

    private String factoryPower;

    private String factoryNamePower;

    private Date createTime;

    private Date updateTime;

    private Integer isDelete;

    private static final long serialVersionUID = 1L;

}