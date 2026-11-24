package com.yutong.safety.core.db.mapper;


import com.yutong.safety.common.param.web.SafetyStaffEvectionParam;
import com.yutong.safety.core.db.entity.SafetyStaffEvectionDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SafetyStaffEvectionDOBizMapper {

    /**
     * 批量添加
     * @param staffList
     */
    public int batchInsert(@Param("staffList") List<SafetyStaffEvectionDO> staffList);

    /**
     * 批量更新
     * @param staffList
     */
    public int batchUpdate(@Param("staffList") List<SafetyStaffEvectionDO> staffList);

    List<String> findAll();

    List<SafetyStaffEvectionDO> queryList(@Param("param")SafetyStaffEvectionParam param, @Param("start") int start, @Param("end") int end);

    long pageListCount(@Param("param")SafetyStaffEvectionParam param);

    int add(@Param("param") SafetyStaffEvectionParam param);

    int update(@Param("param") SafetyStaffEvectionParam param);

    List<SafetyStaffEvectionDO> queryListByEmpNos(@Param("users")List<String> users);

    List<SafetyStaffEvectionDO> getGcList();
}