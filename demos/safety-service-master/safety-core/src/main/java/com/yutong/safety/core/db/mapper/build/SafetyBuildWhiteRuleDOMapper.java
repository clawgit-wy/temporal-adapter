package com.yutong.safety.core.db.mapper.build;

import com.yutong.safety.core.db.entity.build.SafetyBuildWhiteRuleDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildWhiteRuleDOExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SafetyBuildWhiteRuleDOMapper {

    long countByExample(SafetyBuildWhiteRuleDOExample example);

    int deleteByExample(SafetyBuildWhiteRuleDOExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(SafetyBuildWhiteRuleDO record);

    int insertSelective(SafetyBuildWhiteRuleDO record);

    List<SafetyBuildWhiteRuleDO> selectByExample(SafetyBuildWhiteRuleDOExample example);

    SafetyBuildWhiteRuleDO selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") SafetyBuildWhiteRuleDO record, @Param("example") SafetyBuildWhiteRuleDOExample example);

    int updateByExample(@Param("record") SafetyBuildWhiteRuleDO record, @Param("example") SafetyBuildWhiteRuleDOExample example);

    int updateByPrimaryKeySelective(SafetyBuildWhiteRuleDO record);

    int updateByPrimaryKey(SafetyBuildWhiteRuleDO record);
}