package com.yutong.modules.common.constant;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class RoleCodeConstant {

    /** 安全员 */
    @Value("${ytsafety.custom.constant.safetyOfficer}")
    private String safetyOfficer;

    /** 安全主管 */
    @Value("${ytsafety.custom.constant.safetySupervisor}")
    private String safetySupervisor;

    /** 消防员 */
    @Value("${ytsafety.custom.constant.firefighter}")
    private String firefighter;

    /** 消防组长 */
    @Value("${ytsafety.custom.constant.fireTeamLeader}")
    private String fireTeamLeader;

    /** 保卫巡逻组长 */
    @Value("${ytsafety.custom.constant.securityPatrolLeader}")
    private String securityPatrolLeader;

    /** 治安管理员 */
    @Value("${ytsafety.custom.constant.securityAdministrator}")
    private String securityAdministrator;

    /** 业务系统负责人 */
    @Value("${ytsafety.custom.constant.businessSystemLead}")
    private String businessSystemLead;

    /** 巡逻人员 */
    @Value("${ytsafety.custom.constant.patrolPersonnel}")
    private String patrolPersonnel;

    /** 系统管理员 */
    @Value("${ytsafety.custom.constant.sysAdminRoleCode}")
    private String sysAdminRoleCode;

    /** 保卫科长 */
    @Value("${ytsafety.custom.constant.securityChief}")
    private String securityChief;

}
