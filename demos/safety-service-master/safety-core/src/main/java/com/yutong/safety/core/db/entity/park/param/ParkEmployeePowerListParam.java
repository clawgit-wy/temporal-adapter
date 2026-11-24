package com.yutong.safety.core.db.entity.park.param;

import com.yutong.safety.common.param.web.WebPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ParkEmployeePowerListParam extends WebPageParam {
    @ApiModelProperty("员工号")
    private String empNo;

    @ApiModelProperty("员工姓名")
    private String empName;

    @ApiModelProperty("用户类型")
    private String userType;

    @ApiModelProperty("车牌号")
    private String carNo;

    @ApiModelProperty("授权厂区")
    private String factoryCode;

    @ApiModelProperty("授权门禁")
    private String buildCode;

    @ApiModelProperty("锁定车辆")
    private String lockCar;

    @ApiModelProperty("分页标记-后台使用")
    private Boolean pageFlag;

    @ApiModelProperty("下发标记-同步车辆权限使用")
    private String downMark;

    @ApiModelProperty("员工状态 在职/离职")
    private String empStatus;

    private String deleteFlag;

    @ApiModelProperty("员工号拼接串")
    private List<String> empNoList;

    @ApiModelProperty("排除厂区集合")
    private List<String> notFactoryIdList;

    @ApiModelProperty("厂区集合")
    private List<String> factoryIdList;

    @ApiModelProperty("组织编码-（单选-当前组织的Code）")
    private String deptCode;

    private List<String> deptCodeList;

    @ApiModelProperty("后端参数-责任部门过滤")
    private String filterDutyDept;

    /**
     * fixedEmpNo 固定的员工号
     */
    private String fixedEmpNo;
}
