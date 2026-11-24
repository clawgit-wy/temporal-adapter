package com.yutong.safety.core.db.mapper.build;

import com.yutong.safety.core.db.entity.build.SafetyBuildIssuedLogDO;
import com.yutong.safety.core.dto.build.SafetyBuildIssuedLogDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SafetyBuildIssuedLogDOBizMapper extends SafetyBuildIssuedLogDOMapper{
    List<SafetyBuildIssuedLogDTO> selectPageList(SafetyBuildIssuedLogDTO param);
    long selectListCount(SafetyBuildIssuedLogDTO param);

    void batchInsert(@Param("list") List<SafetyBuildIssuedLogDO> list);
    void batchUpdate(@Param("list") List<Integer> successList,@Param("user")String user);
    void batchUpdateGuard(@Param("list") List<String> list,@Param("user")String user);

    List<SafetyBuildIssuedLogDO> getExceptData();
}
