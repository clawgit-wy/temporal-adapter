package com.yutong.safety.core.db.mapper.build;

import com.yutong.safety.core.db.entity.build.SafetyBuildDepartConfirmDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildDepartConfirmHistoryDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildDepartConfirmHistoryDOExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SafetyBuildDepartConfirmHistoryDOMapper {

    long countByExample(SafetyBuildDepartConfirmHistoryDOExample example);

    int deleteByExample(SafetyBuildDepartConfirmHistoryDOExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(SafetyBuildDepartConfirmHistoryDO record);

    int insertSelective(SafetyBuildDepartConfirmHistoryDO record);

    List<SafetyBuildDepartConfirmHistoryDO> selectByExample(SafetyBuildDepartConfirmHistoryDOExample example);

    SafetyBuildDepartConfirmHistoryDO selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") SafetyBuildDepartConfirmHistoryDO record, @Param("example") SafetyBuildDepartConfirmHistoryDOExample example);

    int updateByExample(@Param("record") SafetyBuildDepartConfirmHistoryDO record, @Param("example") SafetyBuildDepartConfirmHistoryDOExample example);

    int updateByPrimaryKeySelective(SafetyBuildDepartConfirmHistoryDO record);

    int updateByPrimaryKey(SafetyBuildDepartConfirmHistoryDO record);

    int batchInsert(@Param("list") List<SafetyBuildDepartConfirmDO> list);
}