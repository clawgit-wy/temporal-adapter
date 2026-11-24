package com.yutong.modules.vehicle.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@TableName("ytsafety_query_perm_info")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "YtsafetyQueryPermInfo", description = "信息查询权限表")
public class YtsafetyQueryPermInfo {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "角色编码")
    private String roleCode;

    @ApiModelProperty(value = "角色名称")
    private String roleName;

    @ApiModelProperty(value = "权限类别：person_info-人员信息查询, vehicle_info-车辆信息查询, person_pass-人员通行记录, vehicle_pass-车辆通行记录, building_access-楼宇门禁通行记录")
    private String permCategory;

    @ApiModelProperty(value = "权限类型 0-无权限 1-全部可查 2-自定义")
    private Integer permType;

    @ApiModelProperty(value = "可查询类型（自定义时存，逗号分隔，0:正式员工 1:外协人员 2:访客 3:物流司机 4:员工车辆 5:物流车辆 6:领导车辆 7:公务车辆）")
    private String queryableTypes;

    @ApiModelProperty(value = "查询时间范围（天），仅通行记录类权限使用")
    private Integer queryDays;

    @ApiModelProperty(value = "创建人姓名")
    private String createName;

    @ApiModelProperty(value = "创建人工号")
    private String createUser;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "修改人姓名")
    private String updateName;

    @ApiModelProperty(value = "修改人工号")
    private String updateUser;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;


    @ApiModelProperty(value = "删除标记")
    @TableLogic(value = "0", delval = "1")
    private Integer delFlag;

    @ApiModelProperty(value = "修改版本")
    private Integer version;
}
