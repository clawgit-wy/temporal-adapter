package com.yutong.safety.core.db.entity.build;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 楼宇门禁区域-从楼宇定时同步
 * @author gwxiexj
 */
@Data
public class SafetyBuildAreaDO implements Serializable {
    /**
     * 区域ID
     */
    private Integer areaId;

    /**
     * 区域全路径ID拼接
     */
    private String fullAreaId;

    /**
     * 区域名称
     */
    private String areaName;

    /**
     * 区域层级 1园区、2楼栋、3单元、4楼层、5房间
     */
    private Integer levf;

    /**
     * 楼层
     */
    private Integer floor;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}