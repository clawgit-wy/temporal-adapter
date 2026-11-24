package com.yutong.safety.core.db.entity.build.param;

import com.yutong.safety.common.param.web.WebPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class BuildAutoDeptListParam  extends WebPageParam {

    @ApiModelProperty("分页标记-不需前端传值")
    private boolean pageFlag;
    @ApiModelProperty("工厂编码-AN")
    private String anFactoryCode;
    private String anFactoryName;
    @ApiModelProperty("楼栋编码-AN")
    private String anBuildCode;
    private String anBuildName;
    @ApiModelProperty("门禁设备名称-AN")
    private String anDeviceName;
    @ApiModelProperty("授权部门")
    private String powerDeptCode;
    private String powerDeptName;
    @ApiModelProperty("对接部门")
    private String dockDeptCode;
    private String dockDeptName;
    @ApiModelProperty("创建日期-开始")
    private String createTimeBegin;
    @ApiModelProperty("创建日期-结束")
    private String createTimeEnd;
    @ApiModelProperty("过滤责任部门路径")
    private String filterDutyDept;
    @ApiModelProperty("创建人员工号")
    private String createEmployeeLogin;
    @ApiModelProperty("结束日期")
    private String endTimeStr;
    @ApiModelProperty("是否终止 0-否 1-是")
    private String autoFlag;
    @ApiModelProperty("申请人")
    private String create;

    /**
     * 楼栋
     */
    private String anBuilding;

    /**
     * 授权部门
     */
    private String deptName;

    /**
     * 对接部门
     */
    private String dockingDeptName;

    /**
     * 授权期限-开始时间
     */
    private String beginTime;
    /**
     * 授权期限-截止时间
     */
    private String endTime;

    @Override
    public void check() {
        if (StringUtils.isBlank(this.anBuildName) && StringUtils.isNotBlank(this.anBuilding)) {
            this.anBuildName = this.anBuilding;
        }
        if (StringUtils.isBlank(powerDeptName) && StringUtils.isNotBlank(this.deptName)) {
            this.powerDeptName = this.deptName;
        }
        if (StringUtils.isBlank(this.dockDeptName) && StringUtils.isNotBlank(this.dockingDeptName)) {
            this.dockDeptName = this.dockingDeptName;
        }
    }
}
