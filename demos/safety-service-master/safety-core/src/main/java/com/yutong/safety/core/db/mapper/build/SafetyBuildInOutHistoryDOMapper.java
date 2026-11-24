package com.yutong.safety.core.db.mapper.build;

import com.yutong.safety.core.db.entity.build.SafetyBuildInOutHistoryDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildInOutHistoryDOExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SafetyBuildInOutHistoryDOMapper {
    long countByExample(SafetyBuildInOutHistoryDOExample example);

    int deleteByExample(SafetyBuildInOutHistoryDOExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(SafetyBuildInOutHistoryDO record);

    int insertSelective(SafetyBuildInOutHistoryDO record);

    List<SafetyBuildInOutHistoryDO> selectByExample(SafetyBuildInOutHistoryDOExample example);

    SafetyBuildInOutHistoryDO selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") SafetyBuildInOutHistoryDO record, @Param("example") SafetyBuildInOutHistoryDOExample example);

    int updateByExample(@Param("record") SafetyBuildInOutHistoryDO record, @Param("example") SafetyBuildInOutHistoryDOExample example);

    int updateByPrimaryKeySelective(SafetyBuildInOutHistoryDO record);

    int updateByPrimaryKey(SafetyBuildInOutHistoryDO record);
}