package com.yutong.safety.core.db.mapper.build;

import com.yutong.safety.core.db.entity.build.SafetyBuildProcessDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildProcessDOExample;
import com.yutong.safety.core.db.entity.build.SafetyBuildProcessDOWithBLOBs;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SafetyBuildProcessDOMapper {
    long countByExample(SafetyBuildProcessDOExample example);

    int deleteByExample(SafetyBuildProcessDOExample example);

    int deleteByPrimaryKey(String id);

    int insert(SafetyBuildProcessDOWithBLOBs record);

    int insertSelective(SafetyBuildProcessDOWithBLOBs record);

    List<SafetyBuildProcessDOWithBLOBs> selectByExampleWithBLOBs(SafetyBuildProcessDOExample example);

    List<SafetyBuildProcessDO> selectByExample(SafetyBuildProcessDOExample example);

    SafetyBuildProcessDOWithBLOBs selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") SafetyBuildProcessDOWithBLOBs record, @Param("example") SafetyBuildProcessDOExample example);

    int updateByExampleWithBLOBs(@Param("record") SafetyBuildProcessDOWithBLOBs record, @Param("example") SafetyBuildProcessDOExample example);

    int updateByExample(@Param("record") SafetyBuildProcessDO record, @Param("example") SafetyBuildProcessDOExample example);

    int updateByPrimaryKeySelective(SafetyBuildProcessDOWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(SafetyBuildProcessDOWithBLOBs record);

    int updateByPrimaryKey(SafetyBuildProcessDO record);
}