package com.yutong.safety.core.db.mapper.build;

import java.util.List;

import com.yutong.safety.core.db.entity.build.SafetyBuildAutoDeptDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildAutoDeptDOExample;
import com.yutong.safety.core.db.entity.build.param.BuildAutoDeptListParam;
import com.yutong.safety.core.db.entity.build.vo.BuildAutoDeptVO;
import org.apache.ibatis.annotations.Param;

public interface SafetyBuildAutoDeptDOMapper {

    int batchInsert(List<SafetyBuildAutoDeptDO> list);

    long countByExample(BuildAutoDeptListParam example);

    int deleteByPrimaryKey(String id);

    int insert(SafetyBuildAutoDeptDO record);

    int insertSelective(SafetyBuildAutoDeptDO record);

    List<BuildAutoDeptVO> selectByExample(BuildAutoDeptListParam example);

    SafetyBuildAutoDeptDO selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") SafetyBuildAutoDeptDO record, @Param("example") SafetyBuildAutoDeptDOExample example);

    int updateByPrimaryKeySelective(SafetyBuildAutoDeptDO record);

}