package com.yutong.safety.common.param.web;

import lombok.Data;

import java.util.List;

/**
 * @author gwcuiyx
 * @date 2024-4-3 13:53
 */
@Data
public class EmployeeMessageUpdateParam {
    /**
     * 员工号
     */
    private String empNo;
    /**
     * 姓名
     */
    private String empName;
    /**
     * 手机号
     */
    private String phoneNo;
    /**
     *  备注
     */
    private String remark;
    /**
     * 人员类型
     */
    private String userType;
    /**
     * 类别
     */
    private String positionType;
    /**
     * 批量修改备注内容
     */
    private String content;
    /**
     * 选中的数据
     */
    private String ids;

    private List<String> empNos;
    /**
     * 员工状态 离职(DISABLED)/在职(NORMAL)
     */
    private String status;
}
