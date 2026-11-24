package com.yutong.safety.core.db.mapper.build;

import com.yutong.safety.core.db.entity.build.SafetyBuildInfoRecordDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildInfoRecordDOExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SafetyBuildInfoRecordDTOMapper extends SafetyBuildInfoRecordDOMapper {

    void batchSave(@Param("list") List<SafetyBuildInfoRecordDO> list);

}