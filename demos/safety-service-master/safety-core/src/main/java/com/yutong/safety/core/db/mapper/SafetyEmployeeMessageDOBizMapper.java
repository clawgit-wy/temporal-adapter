package com.yutong.safety.core.db.mapper;


import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SafetyEmployeeMessageDOBizMapper extends SafetyEmployeeMessageDOMapper {

    /**
     * 批量添加
     * @param blackList
     */
    public void batchInsert(@Param("blackList") List<SafetyEmployeeMessageDO> blackList);

    /**
     * 批量更新
     * @param blackList
     */
    public void batchUpdate(@Param("blackList") List<SafetyEmployeeMessageDO> blackList);

    /**
     * 批量添加/更新
     * @param employeeList 入参
     */
    public void batchUpdateNew(@Param("employeeList") List<SafetyEmployeeMessageDO> employeeList);

    SafetyEmployeeMessageDO selectByEmpNo(@Param("empNo") String empNo);

    int modifyFaceIssueFlag(@Param("list") List<String> empNoList);

    int updateEmpPathByOrg(@Param("orgCodeList")List<String> orgCodeList);
}