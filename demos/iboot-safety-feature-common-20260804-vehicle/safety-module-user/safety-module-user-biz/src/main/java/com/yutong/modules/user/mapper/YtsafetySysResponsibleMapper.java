package com.yutong.modules.user.mapper;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.user.dto.YtsafetySysResponsibleQueryDTO;
import com.yutong.modules.user.entity.*;
import com.yutong.modules.user.model.SysDictSelect;
import com.yutong.modules.user.vo.SafetyEmployeeMessageVO;
import com.yutong.modules.user.vo.SafetySysUserVO;
import com.yutong.modules.user.vo.YtsafetySysResponsibleEditVO;
import com.yutong.modules.user.vo.YtsafetySysResponsibleVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.jeecg.common.aspect.annotation.DataPermission;
import org.jeecg.common.aspect.annotation.PermissionData;
import org.yaml.snakeyaml.scanner.Constant;

/**
 * @Description: 安全责任信息表
 * @Author: jeecg-boot
 * @Date:   2026-05-27
 * @Version: V1.0
 */
public interface YtsafetySysResponsibleMapper extends BaseMapper<YtsafetySysResponsible> {

    /**
     * 分页查询
     *
     * @param page
     * @param sysResponsibleQueryDTO
     * @return
     */
    @DataPermission(permCode="systemd:secResponsible")
    public IPage<YtsafetySysResponsibleVO> queryPage(Page<YtsafetySysResponsibleVO> page, @Param("dto") YtsafetySysResponsibleQueryDTO sysResponsibleQueryDTO);

    /**
     * 列表查询
     *
     * @param sysResponsibleQueryDTO
     * @return
     */
    @DataPermission(permCode="systemd:secResponsible")
    public List<YtsafetySysResponsibleVO> queryExportList(@Param("dto") YtsafetySysResponsibleQueryDTO sysResponsibleQueryDTO);
    /**
     * 用EIP账号查询用户信息
     * @param empNo
     * @return
     */
    @Select("select * from safety_sys_user where delete_falg=0 and emp_no=#{empNo}")
    public SafetySysUser querySysUser(@Param("empNo") String empNo);

    /**
     * 查询系统用户角色
     * @param userId
     * @param roleType
     * @return
     */
    @Select("select GROUP_CONCAT(DISTINCT role_id SEPARATOR ',') from safety_sys_user_role where del_flag=0 and user_id=#{userId} and role_type=#{roleType}")
    public String queryUserSystemRole(@Param("userId") Long userId, @Param("roleType") Integer roleType);

    /**
     * 依据类型删除用户角色
     * @param wrapper
     */
    @Delete("delete from safety_sys_user_role ${ew.customSqlSegment}")
    public void deleteRoleByUserId(@Param(Constants.WRAPPER) Wrapper<SafetySysUserRole> wrapper);


    /**
     * 组织机构查询所属厂区
     * @param orgCode
     * @return
     */
    @Select("select factory_id id,factory_code code,factory_name value from ytsafety_sys_org_factory where del_flag=0 and org_code=#{orgCode} limit 1")
    public SysDictSelect queryBelongFactory(@Param("orgCode") String orgCode);


    /**
     * 获取EIP员工信息
     * @param empNo
     * @return
     */
    @Select("select * from safety_employee_message where emp_no=#{empNo} and emp_login is not null limit 1")
    public SafetyEmployeeMessage queryEmployeeMessageByEip(@Param("empNo") String empNo);

    /**
     * 查询用户工厂关系
     * @param wrapper
     * @param userId
     * @return
     */
    @Select("select #{userId} user_id,id factory_id,field_code factory_code,field_name factory_name,now() create_time  from safety_field_manage ${ew.customSqlSegment} ")
    public List<YtsafetySysUserFactory> queryUserFactory(@Param(Constants.WRAPPER) Wrapper<SafetyFieldManage> wrapper,@Param("userId") Long userId);

    /**
     * 物理删除用户工厂信息
     * @param userId
     */
    @Delete("delete from ytsafety_sys_user_factory where user_id=#{userId} ")
    public void deleteUserFactory(@Param("userId") Long userId);

    /**
     * 查询用户部门关系
     * @param wrapper
     * @param userId
     * @return
     */
    @Select("select #{userId} user_id,id depart_id,code depart_code,name depart_name,now() create_time from safety_group_info ${ew.customSqlSegment} ")
    public List<YtsafetySysUserDepart> queryUserDepart(@Param(Constants.WRAPPER) Wrapper<SafetyGroupInfo> wrapper, @Param("userId") Long userId);

    /**
     * 物理删除用户部门关系
     * @param userId
     */
    @Delete("delete from ytsafety_sys_user_depart where user_id=#{userId} ")
    public void deleteUserDepart(@Param("userId") Long userId);

    /**
     * 查询用户角色表
     * @param wrapper
     * @param userId
     * @return
     */
    @Select("select  #{userId} user_id,id role_id,role_type,role_code, #{status} status,0 del_flag,now() create_time from safety_sys_role ${ew.customSqlSegment}")
    public List<SafetySysUserRole> queryUserRole(@Param(Constants.WRAPPER) Wrapper<SafetySysRole> wrapper, @Param("userId") Long userId,@Param("status") Integer status);


    /**
     * 查询详情
     * @param id
     * @return
     */
    public YtsafetySysResponsibleEditVO querySysResponsibleEditVO(@Param("id") Long id);

    /**
     * 查询用户详情
     * @param empNo
     * @return
     */
    public YtsafetySysResponsibleEditVO querySysUserByEmpNo(@Param("empNo") String empNo);

    /**
     * 查询用户详情
     * @param empNo
     * @return
     */
    public YtsafetySysResponsibleEditVO queryResponsibleByEmpNo(@Param("empNo") String empNo);

    /**
     * 通过EIP账号查询用户详情
     * @param empNos
     * @return
     */
    public List<YtsafetySysResponsibleEditVO> querySysUserByUserNo(@Param("empNos") List<String> empNos);

    /**
     * 判定安全责任人是否存在
     * @param userId
     * @return
     */
    @Select("select count(1) from ytsafety_sys_responsible where del_flag=0 and user_id=#{userId}")
    public boolean checkSysResponsibleExists(@Param("userId") Long userId);

    /**
     * 通过empNo查询用户授权厂区ID列表（需满足安全责任人+指定角色）
     * @param empNo
     * @param roleCode
     * @return
     */
    @Select("SELECT DISTINCT suf.factory_id FROM ytsafety_sys_responsible sr " +
            "INNER JOIN safety_sys_user su ON sr.user_id = su.id " +
            "INNER JOIN ytsafety_sys_user_factory suf ON sr.user_id = suf.user_id " +
            "INNER JOIN safety_sys_user_role sur ON su.id = sur.user_id " +
            "AND sur.del_flag = 0 AND sur.role_type IN (0, 1) AND sur.role_code = #{roleCode} " +
            "WHERE sr.del_flag = 0 AND su.delete_falg = 0 AND sr.emp_no = #{empNo}")
    List<Long> getAuthorizedFactoryIds(@Param("empNo") String empNo, @Param("roleCode") String roleCode);

    @Update("update safety_sys_user_role set status=#{status} ${ew.customSqlSegment}")
    public void updateRolesStatus(@Param(Constants.WRAPPER) Wrapper<SafetySysUserRole> wrapper,@Param("status") Integer status);

    @Update("update ytsafety_sys_responsible set status=#{status} ${ew.customSqlSegment}")
    public void updateResponsibleStatus(@Param(Constants.WRAPPER) Wrapper<YtsafetySysResponsible> wrapper,@Param("status") Integer status);
}

