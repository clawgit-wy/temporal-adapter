package com.yutong.modules.user.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.yutong.modules.user.entity.SafetyEmployeeMessage;
import com.yutong.modules.user.entity.SafetySysRole;
import com.yutong.modules.user.entity.SafetySysUser;
import com.yutong.modules.user.vo.*;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
/**
 * 公共映射类
 */
public interface SafetyCommonMapper {

    public List<SafetyEmployeeMessageVO> queryEmployFuzzySearch(@Param("userInfo") String userInfo);

    public List<SafetyEmployeeMessageVO> queryEmployByOrgCode(@Param("orgCode") String orgCode);

    @Select("select id, field_code,field_name from safety_field_manage where field_type = 'factory' and delete_falg = 0")
    public List<SafetyFieldManageVO> queryFactoryDrop();

    @Select("select id, field_code,field_name from safety_field_manage where field_type = 'factory' and remark is null and delete_falg = 0")
    public List<SafetyFieldManageVO> queryFactoryFilterDrop();


    @Select("select id, field_code,field_name from safety_field_manage where field_type = 'region' and delete_falg = 0")
    public List<SafetyFieldManageVO> queryRegionDrop();

    public List<SafetyFieldManageVO> queryRiskRegion(@Param("parentIds") List<String> parentIds);

    /**
     * 获取所有风险区域
     * @return
     */
    public List<SafetyFieldManageVO> queryAllRiskRegion();

    @Select("select id,role_name,role_code from safety_sys_role ${ew.customSqlSegment} ")
    public List<SafetySysRoleOptionListVO> optionlist(@Param(Constants.WRAPPER) Wrapper<SafetySysRole> wrapper);

    @Select("select * from safety_group_info where status='Y' and org_code_path like CONCAT(#{parentCode},'%')  ")
    public List<SafetyGroupInfoTreeVO> queryGroupInfoChild(@Param("parentCode") String parentCode);

    @Select("select * from safety_group_info where status='Y' and parent_code=#{parentCode} and code!=#{parentCode}")
    public List<SafetyGroupInfoTreeVO> queryGroupInfoRoot(@Param("parentCode") String parentCode);

    @Select("SELECT t1.position_code as jobCode,t1.position_name as jobName FROM safety_employee_message t1 INNER JOIN (SELECT MIN(id) AS id FROM safety_employee_message WHERE position_code IS NOT NULL AND position_code != '' AND position_name IS NOT NULL AND position_name != '' GROUP BY position_code ) t2 ON t1.id = t2.id ORDER BY t1.position_code;")
    List<JobOptionListVO> jobOptionList();

    @Select("SELECT count(jr.id) FROM ytsafety_sys_job_role_mapping jr LEFT JOIN safety_employee_message em on em.position_code = jr.job_code where jr.del_flag = 0 and em.emp_no = #{empNo};")
    Long checkDefend(@Param("empNo") String empNo);

    List<YtsafetySysResponsibleVO> queryResponsibleByOrgCode(@Param("departCode") String departCode, @Param("roleCodeList") List<String> roleCodeList);

    List<YtsafetySysResponsibleVO> queryResponsibleByFactory(@Param("factoryId") Long factoryId, @Param("roles") List<String> roles);

    List<YtsafetySysResponsibleVO> queryResponsibleByFactoryAndDepart(@Param("departCode") String departCode,@Param("factoryId") Long factoryId, @Param("roles") List<String> roles);

    @Select("SELECT DISTINCT suf.factory_id FROM ytsafety_sys_responsible sr " +
            "INNER JOIN safety_sys_user su ON sr.user_id = su.id " +
            "INNER JOIN ytsafety_sys_user_factory suf ON sr.user_id = suf.user_id " +
            "WHERE sr.del_flag = 0 and sr.status = 0 AND su.delete_falg = 0 AND sr.emp_no = #{empNo}")
    List<Long> getAuthorizedFactoryIds(@Param("empNo") String empNo);

    Boolean checkIsResponsible(String empNo, List<String> roleCodeList);

    List<SafetyOfficerVO> queryGroupInfoSafetyOfficer(@Param("code") String code, @Param("safetyOfficer")  String safetyOfficer);

    SafetySysUser getIsSuperAdmin(@Param("empNo") String empNo, @Param("roleList") List<String> roleList);

    SafetySysUser isSecurityPersonnel(@Param("empNo") String empNo);

    SafetySysUser isSafetyOfficer(@Param("userId") String userId , @Param("safetyOfficer") String safetyOfficer);

    List<SafetySysUser> getAproveUser(@Param("roleCode")String roleCode, @Param("code") String code);

    List<SafetySysUser> selectFactoryRole(@Param("factoryCode") String factoryCode,@Param("roles") List<String> roles);

    public List<SafetyLocationInfoVO> queryLocationInfo(@Param("factoryId") String factoryId,
                                                        @Param("areaId") String areaId,
                                                        @Param("orgId") String orgId,
                                                        @Param("orgCode") String orgCode);

    @Select("SELECT * FROM safety_employee_message em " +
            "WHERE em.status = 'NORMAL' AND em.emp_login = #{empLogin}")
    SafetyEmployeeMessage querySafetyEmployeeMessageByEmpLogin(@Param("empLogin")String empLogin);
}
