package com.yutong.modules.user.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.yutong.modules.user.entity.SafetySysPerm;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @Description: 1
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
public interface SafetySysPermMapper extends BaseMapper<SafetySysPerm> {

    @Delete("delete from safety_sys_role_perm where perm_id = #{permId}")
    public void deleteSysRolePerm(@Param("permId") String permId);

    @Update("update ytsafety_sys_user_data_rule set del_flag = '1', update_time = now(),update_user=#{userName} where perm_id = #{permId}" )
    public void deleteUserDataRule(@Param("permId") String permId,@Param("userName") String userName);

    List<SafetySysPerm> getSafetySysPermList(@Param(Constants.WRAPPER) LambdaQueryWrapper<SafetySysPerm> queryWrapper);

    @Select("select * from safety_sys_perm where delete_falg='0' and cate=1 and is_show=1 order by sort asc")
    public List<SafetySysPerm> querySysPermMenu();
}
