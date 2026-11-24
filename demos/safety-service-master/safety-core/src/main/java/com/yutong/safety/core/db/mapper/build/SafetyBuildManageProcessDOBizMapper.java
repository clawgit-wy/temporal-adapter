package com.yutong.safety.core.db.mapper.build;

import com.yutong.safety.core.db.entity.build.SafetyBuildManageProcessDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildManageProcessDOExample;
import com.yutong.safety.core.dto.build.SafetyBuildManageProcessDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SafetyBuildManageProcessDOBizMapper {
    List<SafetyBuildManageProcessDTO> selectList(SafetyBuildManageProcessDTO param);
    Integer selectListCount(SafetyBuildManageProcessDTO param);
}