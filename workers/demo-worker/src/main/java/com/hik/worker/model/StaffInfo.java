package com.hik.worker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * 人员信息模型（对应海康接口 Staff 对象）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffInfo implements Serializable {
    /** 人员编号 */
    private String staffNo;
    /** 人员姓名 */
    private String staffName;
    /** 性别 (0=男, 1=女) */
    private int sex;
    /** 组织ID */
    private String organizationId;
    /** 角色ID */
    private String rid;
    /** 身份证号 */
    private String idNo;
    /** 手机号 */
    private String mobileNo;
    /** 邮箱 */
    private String email;
}
