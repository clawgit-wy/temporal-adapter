package com.yutong.safety.common.param.web.spot;

import com.yutong.safety.common.param.Check;
import lombok.Data;


/**
 * @author gwcuiyx
 * @date 2024-4-28 17:01
 */
@Data
public class SpotCheckDeviceAddParam implements Check {
    private Integer id;
    /**
     * 厂区id
     */
    private Integer factoryId;
    /**
     * 设备类型
     */
    private String deviceType;
    /**
     * 项目名称
     */
    private String teamName;
    /**
     * 设备名称
     */
    private String deviceName;
    /**
     * 位置
     */
    private String location;
    /**
     * 设备编号
     */
    private String deviceCode;
    /**
     * 责任部门
     */
    private String dutyName;
    /**
     * 点检人
     */
    private String checkEmp;

    /**
     * 字段管理表--项目id
     */
    private Integer teamId;
    /**
     * 点检人员工号
     */
    private String checkEmpNo;

    /**
     * 临时点检人姓名
     */
    private String proTemCheckEmp;
    /**
     * 临时点检时间
     */
    private String proTemBeginDate;
    /**
     * 临时点检结束时间
     */
    private String proTemEndDate;
    /**
     * 备注
     */
    private String remark;

    /**
     * 设备巡检标准ids
     */
    private String ruleIds;

    /**
     * 设备类型编号
     */
    private String deviceFieldCode;

    @Override
    public void check() {
//        PreconditonsEx.checkArgument(Objects.nonNull(companyName), ResponseEnum.PARAM_ERROR.getCode(), "公司companyName不能为空！");
//        PreconditonsEx.checkArgument(Objects.nonNull(empName), ResponseEnum.PARAM_ERROR.getCode(), "姓名empName不能为空！");
//        PreconditonsEx.checkArgument(Objects.nonNull(phoneNo), ResponseEnum.PARAM_ERROR.getCode(), "手机号phoneNo不能为空！");
//        PreconditonsEx.checkArgument(Objects.nonNull(idCardNo), ResponseEnum.PARAM_ERROR.getCode(), "身份证号idCardNo不能为空！");
//        PreconditonsEx.checkArgument(Objects.nonNull(empType), ResponseEnum.PARAM_ERROR.getCode(), "人员类型empType不能为空！");
    }
}
