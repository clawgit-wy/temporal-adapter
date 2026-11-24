package com.yutong.modules.vehicle.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "YtsafetyQueryPermItemVO对象", description = "信息查询权限项VO")
public class YtsafetyQueryPermItemVO {

    @ApiModelProperty(value = "权限类别：person_info-人员信息查询, vehicle_info-车辆信息查询, person_pass-人员通行记录, vehicle_pass-车辆通行记录, building_access-楼宇门禁通行记录")
    private String permCategory;

    @ApiModelProperty(value = "权限类型 0-无权限 1-全部可查 2-自定义")
    private Integer permType;

    @ApiModelProperty(value = "可查询类型（自定义时存，逗号分隔，0:正式员工 1:外协人员 2:访客 3:物流司机 4:员工车辆 5:物流车辆 6:领导车辆 7:公务车辆）")
    private List<String> queryableTypes;

    @ApiModelProperty(value = "查询时间范围（天），仅通行记录类权限使用")
    private Integer queryDays;
}
