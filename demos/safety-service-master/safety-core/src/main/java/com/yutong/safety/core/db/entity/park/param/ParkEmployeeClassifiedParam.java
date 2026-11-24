package com.yutong.safety.core.db.entity.park.param;

import com.yutong.safety.common.param.web.WebPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 无涉密业务关联员工 检索实体类
 */
@Data
public class ParkEmployeeClassifiedParam extends WebPageParam {
    @ApiModelProperty("员工号")
    private String empNo;

    @ApiModelProperty("员工姓名")
    private String empName;

    @ApiModelProperty("人员类型 ZS:正式员工,LD:绿都员工,WX:外协员工")
    private String userType;

    @ApiModelProperty("车牌号")
    private String carNo;

    @ApiModelProperty("组织")
    private String orgNamePath;

    @ApiModelProperty("授权厂区")
    private String factoryName;

    @ApiModelProperty("厂区未进出天数-起始")
    private String dayBegin;

    @ApiModelProperty("厂区未进出天数-截止")
    private String dayEnd;

    @ApiModelProperty("是否白名单")
    private String whetherWhite;

    @ApiModelProperty("白名单添加角色")
    private String whiteRole;

    @ApiModelProperty("有效期结束时间-开始时间")
    private String beginDate;

    @ApiModelProperty("有效期结束时间-结束时间")
    private String endDate;


    @ApiModelProperty("过滤部门")
    private String filterDutyDept;

    @ApiModelProperty("当前登录人员工号")
    private String createEmployeeNo;

    @ApiModelProperty("分页标记-后台使用")
    private Boolean pageFlag;
}
