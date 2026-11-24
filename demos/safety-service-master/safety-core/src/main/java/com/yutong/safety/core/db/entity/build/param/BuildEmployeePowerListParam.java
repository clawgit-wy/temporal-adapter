package com.yutong.safety.core.db.entity.build.param;

import com.yutong.safety.common.param.web.WebPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class BuildEmployeePowerListParam extends WebPageParam {

    private String creator;

    private String anFactoryName;

    private String anBuildName;

    private String anDeviceName;

    private String deptName;

    private String dutyDept;

    private String dutyDeptCode;

    private String dutyPerson;

    private String isTop;

    private String powerTimeBegin;

    private String powerTimeEnd;

    @ApiModelProperty("过滤截止日期-始")
    private String powerEndTimeBegin;
    @ApiModelProperty("过滤截止日期-终")
    private String powerEndTimeEnd;

    private List<String> deviceCodeList;

    @ApiModelProperty("过滤部门")
    private String filterDutyDept;

    @ApiModelProperty("当前登录人员工号")
    private String createEmployeeNo;

    @ApiModelProperty("员工号")
    private String employeeCode;

    private List<String> empCodeList;

    private List<String> notIdList;

    private List<String> idList;

    @ApiModelProperty("分页标记-不需前端传值")
    private boolean pageFlag;

    @ApiModelProperty("过滤失效标记 0-不过滤 1-过滤")
    private Integer filterFlag;

    private String processId;
    private String deleteFlag;
    private List<String> employeeCodeList;
    private List<String> factoryIdList;
    private List<String> deptCodeList;

    @ApiModelProperty("组织编码")
    private String deptCode;

    @ApiModelProperty("设备类型 1-新开普 2-富士")
    private String deviceType;
    private String updateTimeEnd;

    private String roleWorkNo;
}
