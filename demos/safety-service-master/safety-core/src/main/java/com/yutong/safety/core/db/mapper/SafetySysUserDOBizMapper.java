package com.yutong.safety.core.db.mapper;


import com.yutong.safety.common.param.web.UserMessageListParam;
import com.yutong.safety.common.vo.sys.SysUserVO;
import com.yutong.safety.core.db.entity.SafetySysUserDO;
import com.yutong.safety.core.db.entity.SafetySysUserRoleDO;
import com.yutong.safety.core.dto.SafetyUserDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SafetySysUserDOBizMapper extends SafetySysUserDOMapper {

    /**
     * 分页列表
     * @param map
     * @return
     */
    public List<SysUserVO> pageList(Map map);

    /**
     * 分页列表总数据
     * @param map
     * @return
     */
    Long pageListCount(Map map);

    /**
     * 数据添加
     * @param map
     * @return
     */
    public Integer setUserRoles(Map map);

    /**
     * 数据删除操作
     * @param userId
     * @return
     */
    public Integer deleteUserRolesByUserId(Integer userId);

    /**
     * 获取数据
     * @param id
     * @return
     */
    public List<Integer> getUserHasRoles(Integer id);

    /**
     * 批量添加
     * @param blackList
     */
    public void batchInsert(@Param("blackList") List<SafetySysUserDO> blackList);

    /**
     * excel导出操作
     * @param map
     * @return
     */
    public List<SysUserVO> exportList(Map map);

    /**
     * 获取用户列表
     * @param param 入参
     * @return
     */
    List<SafetySysUserDO> getUserList(UserMessageListParam param);

    List<SysUserVO> getAllUserRole();

    List<SafetyUserDTO> getAllUserInfo();

    List<Integer> getUserIdByCodeList(@Param("list") List<String> userCodeList);
    List<Integer> getUserRoleList(@Param("roleId") Integer roleId);
    void batchInserUserRole(@Param("paramList")List<SafetySysUserRoleDO> paramList);
    List<SafetySysUserDO> getUserListByRoleId(@Param("roleId")Integer roleId);

    List<SafetySysUserDO> getUserListByParam(Map<String,Object> paramMap);

    int batchInsertUserForEmpnos(@Param("empNos") List<String> empNos);
}