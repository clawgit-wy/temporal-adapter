package com.yutong.safety.core.db.entity.enter;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 车辆出入场记录表-出入口同步
 */
@Data
public class SafetyCrkCarOutHistoryDO implements Serializable {

    private Integer id;

    private Integer crkId;

    private String tokenNo;

    private String tcmName;

    private String staffNo;

    private String staffName;

    private String inTime;

    private String inLaneName;

    private String inOperatorName;

    private String outTime;

    private String outLaneName;

    private String outOperatorName;

    private String stayLasts;

    private String inPicture;

    private String outPicture;

    private Date createTime;

    private Date updateTime;

    private String remark;

    private static final long serialVersionUID = 1L;

}