package com.yutong.safety.core.db.mapper.build;

import com.yutong.safety.core.db.entity.build.SafetyBuildAreaDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildAreaDOExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SafetyBuildAreaDOBizMapper {

    int batchUpdate(@Param(value="list") List<SafetyBuildAreaDO> buildAreaDOS);

    List<SafetyBuildAreaDO> selectFactoryList();

    long countByExample(SafetyBuildAreaDOExample example);

    int deleteByExample(SafetyBuildAreaDOExample example);

    int deleteByPrimaryKey(Integer areaId);

    int insert(SafetyBuildAreaDO record);

    int insertSelective(SafetyBuildAreaDO record);

    List<SafetyBuildAreaDO> selectByExample(SafetyBuildAreaDOExample example);

    SafetyBuildAreaDO selectByPrimaryKey(Integer areaId);

    int updateByExampleSelective(@Param("record") SafetyBuildAreaDO record, @Param("example") SafetyBuildAreaDOExample example);

    int updateByExample(@Param("record") SafetyBuildAreaDO record, @Param("example") SafetyBuildAreaDOExample example);

    int updateByPrimaryKeySelective(SafetyBuildAreaDO record);

    int updateByPrimaryKey(SafetyBuildAreaDO record);
}