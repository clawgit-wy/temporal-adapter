package com.yutong.modules.user.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.user.query.SafetySysUserQuery;
import com.yutong.modules.user.entity.SafetySysUser;
import com.yutong.modules.user.vo.MenuTree;
import com.yutong.modules.user.vo.SafetySysUserVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.common.aspect.annotation.DataPermission;
import org.jeecg.common.system.vo.LoginUser;


import java.util.List;

/**
 * @Description: 系统用户表
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
public interface SafetySysUserMapper extends BaseMapper<SafetySysUser> {
    @DataPermission(permCode="systemd:users")
    IPage<SafetySysUserVO> queryPageList(Page<SafetySysUserVO> page, @Param("dto") SafetySysUserQuery safetySysUserQueryDto);

    SafetySysUserVO queryById(Long id);

    @DataPermission(permCode="systemd:users")
    List<SafetySysUserVO> queryList(@Param("dto") SafetySysUserQuery safetySysUserQueryDto);

    List<MenuTree> getTree();

    Long selectIdByEmpNo(@Param("empNo") String empNo);

    /**
     * 获取登录信息
     * @param empNo
     * @return
     */
    @Select("select su.id user_id,em.emp_no,em.emp_name real_name,em.emp_login eip_name,em.email,em.org_code,em.org_name,em.position_code,em.position_name,em.phone_no phone \n" +
            "from safety_employee_message em left join safety_sys_user su on em.emp_no=su.emp_no where su.delete_falg='0' and em.emp_no=#{empNo}")
    LoginUser queryLoginUser(@Param("empNo") String empNo);

    List<LoginUser> queryUserListByEmpNos(@Param("empNos") List<String> empNos);
}
