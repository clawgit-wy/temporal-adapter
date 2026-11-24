package com.yutong.modules.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yutong.modules.user.entity.YtsafetyResponsibleProcess;
import com.yutong.modules.user.model.SysDictSelect;
import com.yutong.modules.user.vo.SafetyEmployeeMessageVO;
import com.yutong.modules.user.vo.YtsafetyResponsibleChangeVO;
import com.yutong.modules.user.vo.YtsafetyResponsibleProcessVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Description: 安全责任人流程信息表
 * @Author: lvss
 * @Date:   2026-06-22
 * @Version: V1.0
 */
public interface YtsafetyResponsibleProcessMapper extends BaseMapper<YtsafetyResponsibleProcess> {


    @Select("select * from ytsafety_responsible_process where id=#{id} and del_flag=0 ")
    public YtsafetyResponsibleProcessVO queryById(@Param("id") Long id);

    public List<YtsafetyResponsibleChangeVO> queryChangePerson(@Param("userInfo") String userInfo);

    public List<SafetyEmployeeMessageVO> querySuperApproves(@Param("empLogins") List<String> empLogins);

    @Select("select id,role_code code,role_name value from safety_sys_role where role_type in (0, 1) and id in " +
            "(select distinct role_id from safety_sys_user_role where del_flag=0 and user_id=#{userId})")
    public List<SysDictSelect> queryRoles(@Param("userId") Long userId);

    @Select("select depart_id id,depart_code code,depart_name value from ytsafety_sys_user_depart where user_id=#{userId}")
    public List<SysDictSelect> queryDeparts(@Param("userId") Long userId);

    @Select("select factory_id id,factory_code code,factory_name value from ytsafety_sys_user_factory where user_id=#{userId}")
    public List<SysDictSelect> queryFactorys(@Param("userId") Long userId);

    /**
     * 获取上级审核人员
     * @param roleCode
     * @param orgCode
     * @return
     */
    public List<YtsafetyResponsibleChangeVO> querySuperApprove(@Param("roleCode") String roleCode,@Param("orgCode") String orgCode);
}
