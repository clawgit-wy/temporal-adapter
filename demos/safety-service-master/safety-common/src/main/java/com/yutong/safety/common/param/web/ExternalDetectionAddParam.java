package com.yutong.safety.common.param.web;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;


/**
 * @author gwcuiyx
 * @date 2024-2-28 15:40
 */
@Data
public class ExternalDetectionAddParam implements Check {
    /**
     * 数据id
     */
    private Integer id;
    /**
     * 员工号
     */
    private String empNo;
    /**
     * 员工姓名
     */
    private String empName;

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 备注
     */
    private String remark;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(StringUtils.isNotEmpty(empNo), ResponseEnum.PARAM_ERROR.getCode(), "员工号不能为空！");
        PreconditonsEx.checkArgument(StringUtils.isNotEmpty(empName), ResponseEnum.PARAM_ERROR.getCode(), "员工姓名不能为空！");
    }
}
