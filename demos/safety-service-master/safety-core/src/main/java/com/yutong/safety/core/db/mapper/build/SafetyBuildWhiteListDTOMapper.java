package com.yutong.safety.core.db.mapper.build;

import com.yutong.safety.core.db.entity.build.SafetyBuildWhiteListDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SafetyBuildWhiteListDTOMapper extends SafetyBuildWhiteListDOMapper {
    void batchSaveOrUpdate(@Param("list")List<SafetyBuildWhiteListDO> list);

    void updateByEmpNo(@Param("empNo")String empNo,@Param("list")List<String> dutyDeptCodeList);

    void batchUpdate(@Param("list")List<SafetyBuildWhiteListDO> list);

    void updateByConditions(@Param("dutyDeptCode")String dutyDeptCode,@Param("list")List<String> oldEmpCodeSet,@Param("newEmpCode")String newEmpCode,@Param("newEmpName")String newEmpName);
}