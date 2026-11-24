package com.yutong.safety.core.db.mapper.login;


import com.yutong.safety.common.vo.login.UserRoleMenuVO;
import com.yutong.safety.core.db.entity.SafetySysRoleDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


@Mapper
public interface LoginMapper {

    /**
     * 查询用户拥有的权限资源
     * @param map
     * @return
     */
    public List<UserRoleMenuVO> selectUserRoleMenus(Map map);

    public Integer getRoleId(@Param("empNo")String empNo);

    public List<Integer> getRoleIdList(@Param("empNo")String empNo);

    public List<UserRoleMenuVO> selectUserRolesMenus(Map map);

    List<SafetySysRoleDO> getRoleList(@Param("empNo")String empNo);

    List<SafetySysRoleDO> getRoleNoDelList(@Param("empNo")String empNo);
}
